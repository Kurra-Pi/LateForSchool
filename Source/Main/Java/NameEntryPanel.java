import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

final class NameEntryPanel extends MenuBackground {
    private final JTextField name = new JTextField(22);
    private final JButton play;
    private final JButton back;
    private final JLabel status = text("Use your saved name to return to your record.", 14, false);

    NameEntryPanel(GameAssets assets, Consumer<String> onPlay, Runnable onBack) {
        super(assets);
        JPanel card = card(30);
        card.add(text("What is your name?", 30, true));
        card.add(Box.createVerticalStrut(12));
        card.add(text("Your name identifies your player record.", 16, false));
        card.add(Box.createVerticalStrut(20));
        name.setFont(new Font("SansSerif", Font.PLAIN, 22));
        name.setMaximumSize(new Dimension(410, 42));
        name.setPreferredSize(new Dimension(410, 42));
        name.addActionListener(event -> onPlay.accept(name.getText()));
        card.add(name);
        card.add(Box.createVerticalStrut(16));
        card.add(status);
        card.add(Box.createVerticalStrut(20));
        play = button("Play", () -> onPlay.accept(name.getText()));
        back = button("Back", onBack);
        card.add(play);
        card.add(Box.createVerticalStrut(10));
        card.add(back);
        add(card);
    }

    void focusName() {
        name.requestFocusInWindow();
        name.selectAll();
    }

    void setBusy(boolean busy) {
        name.setEnabled(!busy);
        play.setEnabled(!busy);
        back.setEnabled(!busy);
        status.setText(busy ? "Loading your player record..."
                : "Use your saved name to return to your record.");
    }
}
