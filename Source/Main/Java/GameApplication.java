import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


final class GameApplication extends JPanel {
    private static final String TITLE = "title";
    private static final String NAME = "name";
    private static final String GAME = "game";
    private static final String RESULTS = "results";
    private static final String RECORDS = "records";

    private final GameAssets assets;
    private final RecordStore store;
    private final CardLayout cards = new CardLayout();
    private final ExecutorService worker = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "player-records-database");
        thread.setDaemon(false);
        return thread;
    });
    private final TitlePanel titlePanel;
    private final NameEntryPanel namePanel;
    private final ResultsPanel resultsPanel;
    private final RecordsPanel recordsPanel;

    private GamePanel gamePanel;
    private String activeName;
    private PlayerRecord profile;
    private RunResult lastRun;
    private String recordsReturn = TITLE;
    private boolean saved;
    private boolean busy;
    private boolean closed;

    GameApplication() {
        this(new GameAssets(), JdbcRecordStore.isConfigured()
                ? new JdbcRecordStore() : new MemoryRecordStore());
    }

    GameApplication(GameAssets assets, RecordStore store) {
        this.assets = assets;
        this.store = store;
        setPreferredSize(new Dimension(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT));
        setLayout(cards);
        titlePanel = new TitlePanel(assets, store.description(),
                () -> showScreen(NAME), () -> showRecords(TITLE));
        namePanel = new NameEntryPanel(assets, this::enterPlayer, this::showTitle);
        resultsPanel = new ResultsPanel(assets, this::startGame, this::saveResult,
                () -> showRecords(RESULTS), this::confirmTitle);
        recordsPanel = new RecordsPanel(assets, this::deleteOwnData,
                () -> showRecords(recordsReturn), this::leaveRecords);
        add(titlePanel, TITLE);
        add(namePanel, NAME);
        add(resultsPanel, RESULTS);
        add(recordsPanel, RECORDS);
        showTitle();
    }

    private void showScreen(String name) {
        cards.show(this, name);
        if (NAME.equals(name)) {
            SwingUtilities.invokeLater(namePanel::focusName);
        }
    }

    private void showTitle() {
        removeGame();
        activeName = null;
        profile = null;
        lastRun = null;
        saved = false;
        showScreen(TITLE);
    }

    private void confirmTitle() {
        if (lastRun != null && lastRun.completed() && !saved
                && PlayerRecord.isBetter(lastRun.score(), lastRun.elapsedMillis(), profile)
                && JOptionPane.showConfirmDialog(this,
                        "Return to the title screen without saving this result?", "Unsaved result",
                        JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        showTitle();
    }

    private void enterPlayer(String rawName) {
        if (busy) return;
        final String name;
        try {
            name = PlayerNames.normalize(rawName);
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(),
                    "Player name", JOptionPane.WARNING_MESSAGE);
            namePanel.focusName();
            return;
        }
        database(() -> store.createOrLoad(name), loaded -> {
            profile = loaded;
            activeName = loaded.playerName();
            startGame();
        });
    }

    private void startGame() {
        if (busy || activeName == null) return;
        removeGame();
        lastRun = null;
        saved = false;
        gamePanel = new GamePanel(assets, activeName, profile, this::finishGame);
        add(gamePanel, GAME);
        showScreen(GAME);
        revalidate();
        SwingUtilities.invokeLater(gamePanel::requestFocusInWindow);
    }

    private void finishGame(RunResult result) {
        lastRun = result;
        saved = false;
        refreshResults(null);
        showScreen(RESULTS);
    }

    private void refreshResults(String message) {
        if (lastRun != null) {
            resultsPanel.showResult(lastRun, profile, saved, message);
        }
    }

    private void saveResult() {
        if (busy || saved || lastRun == null || !lastRun.completed()) return;
        RunResult result = lastRun;
        database(() -> store.saveBest(activeName, result.score(), result.elapsedMillis(), result.candies()),
                updated -> {
                    profile = updated;
                    saved = true;
                    boolean thisResult = updated.hasRecord() && updated.bestScore() == result.score()
                            && updated.bestTimeMillis() == result.elapsedMillis();
                    refreshResults(thisResult ? "Your record has been saved."
                            : "An equal or better record is already saved.");
                });
    }

    private void showRecords(String returnScreen) {
        if (busy) return;
        recordsReturn = returnScreen;
        database(store::list, records -> {
            if (activeName != null) {
                profile = records.stream()
                        .filter(record -> PlayerNames.key(activeName).equals(PlayerNames.key(record.playerName())))
                        .findFirst().orElse(null);
                if (profile == null) saved = false;
            }
            recordsPanel.showRecords(records, activeName, store.description());
            showScreen(RECORDS);
        });
    }

    private void leaveRecords() {
        refreshResults(null);
        showScreen(recordsReturn);
    }

    private void deleteOwnData() {
        if (busy || activeName == null) return;
        if (JOptionPane.showConfirmDialog(this,
                "Delete all saved data for " + activeName + "?", "Delete my data",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
            return;
        }
        deleteActiveProfile();
    }

    // Called only after the active player confirms deletion in the UI.
    void deleteActiveProfile() {
        if (busy || activeName == null) return;
        String name = activeName;
        database(() -> store.delete(name), deleted -> {
            profile = null;
            saved = false;
            recordsPanel.removeActiveEntry();
            refreshResults("Your saved data was deleted. Saving a completed result creates it again.");
            showRecords(recordsReturn);
        });
    }

    private <T> void database(Callable<T> operation, Consumer<T> success) {
        if (busy || closed) return;
        setBusy(true);
        worker.submit(() -> {
            try {
                T value = operation.call();
                SwingUtilities.invokeLater(() -> {
                    if (closed) return;
                    setBusy(false);
                    success.accept(value);
                });
            } catch (Exception exception) {
                System.err.println("Player records: " + exception.getMessage());
                SwingUtilities.invokeLater(() -> {
                    if (closed) return;
                    setBusy(false);
                    JOptionPane.showMessageDialog(this,
                            "Could not access player records.\n" + exception.getMessage(),
                            "Player records", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }
    

    private void setBusy(boolean value) {
        busy = value;
        titlePanel.setBusy(value);
        namePanel.setBusy(value);
        resultsPanel.setBusy(value);
        recordsPanel.setBusy(value);
    }

    private void removeGame() {
        if (gamePanel != null) {
            gamePanel.stop();
            remove(gamePanel);
            gamePanel = null;
        }
    }

    void close() {
        closed = true;
        removeGame();
        worker.shutdown();
    }
}
