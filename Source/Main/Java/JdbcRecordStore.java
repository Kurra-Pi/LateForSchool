import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


final class JdbcRecordStore implements RecordStore {
    private static final String DB_URL = setting("ORACLE_DB_URL", "jdbc:oracle:thin:@//localhost:1521/XE");
    private static final String DB_USER = setting("ORACLE_DB_USER", "SYSTEM");
    private static final String DB_PASSWORD = setting("ORACLE_DB_PASSWORD", "PARRYDOTTY21");
    private static final String TABLE_NAME = "SCHOOL_PLAYERS";
    private static final int QUERY_TIMEOUT_SECONDS = 10;

    @FunctionalInterface
    interface ConnectionFactory {
        Connection open() throws SQLException;
    }

    private final ConnectionFactory connections;
    private volatile boolean schemaReady;

    JdbcRecordStore() {
        this(() -> {
            DriverManager.setLoginTimeout(5);
            return DriverManager.getConnection(DB_URL.strip(), DB_USER.strip(), DB_PASSWORD);
        });
    }

    JdbcRecordStore(ConnectionFactory connections) {
        this.connections = Objects.requireNonNull(connections);
    }

    static boolean isConfigured() {
        return !DB_USER.equals("YOUR_ORACLE_USERNAME")
                && !DB_PASSWORD.equals("YOUR_ORACLE_PASSWORD");
    }

    private static String setting(String environmentName, String fallback) {
        String value = System.getenv(environmentName);
        return value == null || value.isBlank() ? fallback : value;
    }

    private synchronized void ensureSchema(Connection connection) throws SQLException {
        if (schemaReady) return;
        String sql = "CREATE TABLE " + TABLE_NAME + " ("
                + "PLAYER_KEY VARCHAR2(240 CHAR) PRIMARY KEY, "
                + "DISPLAY_NAME VARCHAR2(60 CHAR) NOT NULL, "
                + "BEST_SCORE NUMBER(10), BEST_TIME_MS NUMBER(15), "
                + "TOTAL_CANDIES NUMBER(10) DEFAULT 0 NOT NULL, "
                + "CREATED_AT TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL, "
                + "UPDATED_AT TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL, "
                + "CONSTRAINT CK_SCHOOL_PLAYERS_RUN CHECK ((BEST_SCORE IS NULL AND BEST_TIME_MS IS NULL) "
                + "OR (BEST_SCORE IS NOT NULL AND BEST_TIME_MS IS NOT NULL "
                + "AND BEST_SCORE >= 0 AND BEST_TIME_MS >= 0)), "
                + "CONSTRAINT CK_SCHOOL_PLAYERS_CANDY CHECK (TOTAL_CANDIES >= 0))";
        try (Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(QUERY_TIMEOUT_SECONDS);
            statement.executeUpdate(sql);
        } catch (SQLException exception) {
            if (exception.getErrorCode() != 955) throw exception;
        }
        schemaReady = true;
    }

    @Override
    public PlayerRecord createOrLoad(String name) throws SQLException {
        String normalized = PlayerNames.normalize(name);
        try (Connection connection = connections.open()) {
            ensureSchema(connection);
            connection.setAutoCommit(false);
            try {
                PlayerRecord record = lockOrCreate(connection, normalized);
                connection.commit();
                return record;
            } catch (SQLException | RuntimeException exception) {
                rollback(connection, exception);
                throw exception;
            }
        }
    }

    @Override
    public PlayerRecord saveBest(String name, int score, long elapsedMillis, int candies)
            throws SQLException {
        String normalized = PlayerNames.normalize(name);
        PlayerRecord.validateRun(score, elapsedMillis, candies);
        try (Connection connection = connections.open()) {
            ensureSchema(connection);
            connection.setAutoCommit(false);
            try {
                PlayerRecord previous = lockOrCreate(connection, normalized);
                PlayerRecord result = previous;
                if (PlayerRecord.isBetter(score, elapsedMillis, previous)) {
                    String sql = "UPDATE " + TABLE_NAME + " SET BEST_SCORE = ?, BEST_TIME_MS = ?, "
                            + "TOTAL_CANDIES = ?, UPDATED_AT = SYSTIMESTAMP WHERE PLAYER_KEY = ?";
                    try (PreparedStatement statement = prepare(connection, sql)) {
                        statement.setInt(1, score);
                        statement.setLong(2, elapsedMillis);
                        statement.setInt(3, candies);
                        statement.setString(4, PlayerNames.key(normalized));
                        if (statement.executeUpdate() != 1) {
                            throw new SQLException("The player profile changed; please try saving again.");
                        }
                    }
                    result = new PlayerRecord(previous.playerName(), score, elapsedMillis, candies);
                }
                connection.commit();
                return result;
            } catch (SQLException | RuntimeException exception) {
                rollback(connection, exception);
                throw exception;
            }
        }
    }

    private PlayerRecord lockOrCreate(Connection connection, String name) throws SQLException {
        String key = PlayerNames.key(name);
        for (int attempt = 0; attempt < 3; attempt++) {
            String sql = "SELECT DISPLAY_NAME, BEST_SCORE, BEST_TIME_MS, TOTAL_CANDIES FROM "
                    + TABLE_NAME + " WHERE PLAYER_KEY = ? FOR UPDATE";
            try (PreparedStatement statement = prepare(connection, sql)) {
                statement.setString(1, key);
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) return readRecord(result);
                }
            }
            try (PreparedStatement statement = prepare(connection, "INSERT INTO " + TABLE_NAME
                    + " (PLAYER_KEY, DISPLAY_NAME, TOTAL_CANDIES) VALUES (?, ?, 0)")) {
                statement.setString(1, key);
                statement.setString(2, name);
                statement.executeUpdate();
                return new PlayerRecord(name, null, null, 0);
            } catch (SQLException exception) {
                // A simultaneous session may have just created this same normalized name.
                // Oracle rolls back only this failed statement, so re-select and lock the row.
                if (exception.getErrorCode() != 1) throw exception;
            }
        }
        throw new SQLException("The player profile is busy; please try again.");
    }

    @Override
    public List<PlayerRecord> list() throws SQLException {
        try (Connection connection = connections.open()) {
            ensureSchema(connection);
            String sql = "SELECT DISPLAY_NAME, BEST_SCORE, BEST_TIME_MS, TOTAL_CANDIES FROM "
                    + TABLE_NAME + " ORDER BY BEST_SCORE DESC NULLS LAST, BEST_TIME_MS ASC NULLS LAST, PLAYER_KEY";
            List<PlayerRecord> records = new ArrayList<>();
            try (PreparedStatement statement = prepare(connection, sql);
                 ResultSet result = statement.executeQuery()) {
                while (result.next()) records.add(readRecord(result));
            }
            return List.copyOf(records);
        }
    }

    @Override
    public boolean delete(String name) throws SQLException {
        String key = PlayerNames.key(name);
        try (Connection connection = connections.open()) {
            ensureSchema(connection);
            connection.setAutoCommit(false);
            try (PreparedStatement statement = prepare(connection,
                    "DELETE FROM " + TABLE_NAME + " WHERE PLAYER_KEY = ?")) {
                statement.setString(1, key);
                boolean deleted = statement.executeUpdate() > 0;
                connection.commit();
                return deleted;
            } catch (SQLException | RuntimeException exception) {
                rollback(connection, exception);
                throw exception;
            }
        }
    }

    private static PreparedStatement prepare(Connection connection, String sql) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setQueryTimeout(QUERY_TIMEOUT_SECONDS);
        return statement;
    }

    private static PlayerRecord readRecord(ResultSet result) throws SQLException {
        String name = result.getString("DISPLAY_NAME");
        int score = result.getInt("BEST_SCORE");
        Integer bestScore = result.wasNull() ? null : score;
        long time = result.getLong("BEST_TIME_MS");
        Long bestTime = result.wasNull() ? null : time;
        return new PlayerRecord(name, bestScore, bestTime, result.getInt("TOTAL_CANDIES"));
    }

    private static void rollback(Connection connection, Exception original) {
        try {
            connection.rollback();
        } catch (SQLException rollbackFailure) {
            original.addSuppressed(rollbackFailure);
        }
    }

    @Override
    public String description() {
        return "Oracle database records";
    }
}
