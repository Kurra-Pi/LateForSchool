import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

final class RecordsPanel extends MenuBackground {
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Player", "Best score", "Completion time", "Candies in best run"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JLabel status = text("", 14, false);
    private final JButton delete;
    private final JButton refresh;
    private final JButton back;
    private String activeName;
    private boolean ownEntryExists;
    private boolean busy;

    RecordsPanel(GameAssets assets, Runnable onDelete, Runnable onRefresh, Runnable onBack) {
        super(assets);
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setBackground(new Color(22, 44, 57));
        card.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        card.setPreferredSize(new Dimension(850, 470));
        card.add(text("PLAYER RECORDS", 28, true), BorderLayout.NORTH);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setColumnHeaderView(table.getTableHeader());
        card.add(scroll, BorderLayout.CENTER);
        JPanel footer = new JPanel(new BorderLayout(8, 10));
        footer.setOpaque(false);
        footer.add(status, BorderLayout.NORTH);
        JPanel buttons = new JPanel(new GridLayout(1, 3, 8, 0));
        buttons.setOpaque(false);
        delete = button("Delete My Data", onDelete);
        refresh = button("Refresh", onRefresh);
        back = button("Back", onBack);
        buttons.add(delete);
        buttons.add(refresh);
        buttons.add(back);
        footer.add(buttons, BorderLayout.SOUTH);
        card.add(footer, BorderLayout.SOUTH);
        add(card);
    }

    void showRecords(List<PlayerRecord> records, String activeName, String storageMode) {
        this.activeName = activeName;
        ownEntryExists = false;
        model.setRowCount(0);
        int ownRow = -1;
        for (PlayerRecord record : records) {
            model.addRow(new Object[]{record.playerName(),
                record.hasRecord() ? record.bestScore() : "Not completed",
                record.hasRecord() ? GameTime.format(record.bestTimeMillis()) : "--",
                record.totalCandies()});
            if (activeName != null && PlayerNames.key(activeName).equals(PlayerNames.key(record.playerName()))) {
                ownEntryExists = true;
                ownRow = model.getRowCount() - 1;
            }
        }
        if (ownRow >= 0) {
            table.setRowSelectionInterval(ownRow, ownRow);
        }
        status.setText(activeName == null ? " | Enter your name to manage your own data."
                : "Your profile: " + activeName);
        updateButtons();
    }

    void setBusy(boolean busy) {
        this.busy = busy;
        updateButtons();
    }

    void removeActiveEntry() {
        if (activeName == null) return;
        for (int row = model.getRowCount() - 1; row >= 0; row--) {
            if (PlayerNames.key(activeName).equals(PlayerNames.key((String) model.getValueAt(row, 0)))) {
                model.removeRow(row);
            }
        }
        ownEntryExists = false;
        status.setText("Deleted your saved data: " + activeName);
        updateButtons();
    }

    private void updateButtons() {
        delete.setEnabled(!busy && activeName != null && ownEntryExists);
        refresh.setEnabled(!busy);
        back.setEnabled(!busy);
    }
}
