import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;

final class TitlePanel extends MenuBackground {
    private final JButton start;
    private final JButton records;

    TitlePanel(GameAssets assets, String storageMode, Runnable onStart, Runnable onRecords) {
        super(assets);
        JPanel card = card(30);
        card.add(text("LATE FOR SCHOOL", 44, true));
        card.add(Box.createVerticalStrut(10));
        card.add(text("A school-day dash through the neighborhood", 17, false));
        card.add(Box.createVerticalStrut(28));
        start = button("Start", onStart);
        records = button("Player Records", onRecords);
        card.add(start);
        card.add(Box.createVerticalStrut(12));
        card.add(records);
        card.add(Box.createVerticalStrut(24));
        card.add(text("Move: A/D or arrows    Jump: Space/W/Up", 14, false));
        add(card);
    }

    void setBusy(boolean busy) {
        start.setEnabled(!busy);
        records.setEnabled(!busy);
    }
}
