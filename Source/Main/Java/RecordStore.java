import java.sql.SQLException;
import java.util.List;

interface RecordStore {
    PlayerRecord createOrLoad(String name) throws SQLException;

    PlayerRecord saveBest(String name, int score, long elapsedMillis, int candies)
            throws SQLException;

    List<PlayerRecord> list() throws SQLException;

    boolean delete(String name) throws SQLException;

    String description();
}
