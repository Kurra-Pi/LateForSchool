import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

final class ResultsPanel extends MenuBackground {
    private final JLabel heading = text("LEVEL COMPLETE", 32, true);
    private final JLabel player = text("", 19, true);
    private final JLabel score = text("", 20, false);
    private final JLabel best = text("", 15, false);
    private final JLabel status = text("", 14, false);
    private final JButton retry;
    private final JButton save;
    private final JButton records;
    private final JButton menu;
    private boolean canSave;
    private boolean busy;

    ResultsPanel(GameAssets assets, Runnable onRetry, Runnable onSave,
            Runnable onRecords, Runnable onMenu) {
        super(assets);
        JPanel card = card(24);
        card.setPreferredSize(new Dimension(730, 494));
        card.add(heading);
        card.add(Box.createVerticalStrut(8));
        card.add(player);
        card.add(Box.createVerticalStrut(12));
        card.add(score);
        card.add(Box.createVerticalStrut(9));
        card.add(best);
        card.add(Box.createVerticalStrut(10));
        card.add(status);
        card.add(Box.createVerticalStrut(18));
        retry = button("Try Again", onRetry);
        save = button("Save Record", onSave);
        records = button("My Data / Player Records", onRecords);
        menu = button("Title Screen", onMenu);
        for (JButton button : new JButton[]{retry, save, records, menu}) {
            card.add(button);
            card.add(Box.createVerticalStrut(9));
        }
        add(card);
    }

    void showResult(RunResult result, PlayerRecord previous, boolean saved, String message) {
        heading.setText(result.completed() ? "YOU MADE IT TO SCHOOL!" : "TRY AGAIN!");
        player.setText(result.playerName());
        score.setText("Score: " + result.score() + "     Time: " + GameTime.format(result.elapsedMillis()));
        best.setText(previous != null && previous.hasRecord()
                ? "Saved best: " + previous.bestScore() + " points in " + GameTime.format(previous.bestTimeMillis())
                : "No completed record saved yet.");
        canSave = result.completed() && !saved
                && PlayerRecord.isBetter(result.score(), result.elapsedMillis(), previous);
        save.setText(previous != null && previous.hasRecord() ? "Update Record" : "Save Record");
        if (message != null) {
            status.setText(message);
        } else if (!result.completed()) {
            status.setText("Complete the level to save a score and completion time.");
        } else if (!canSave) {
            status.setText(saved ? "This result has been saved." : "Beat your score, or match it in less time, to update.");
        } else {
            status.setText("Save this result, or try again for a better record.");
        }
        updateButtons();
    }

    void setBusy(boolean busy) {
        this.busy = busy;
        updateButtons();
    }

    private void updateButtons() {
        retry.setEnabled(!busy);
        save.setEnabled(!busy && canSave);
        records.setEnabled(!busy);
        menu.setEnabled(!busy);
    }
}
