import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;


class MenuBackground extends JPanel {
    private final SceneRenderer renderer;

    MenuBackground(GameAssets assets) {
        renderer = new SceneRenderer(assets);
        setPreferredSize(new Dimension(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT));
        setLayout(new GridBagLayout());
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        renderer.drawMenuBackground(g);
        g.setColor(new Color(8, 24, 35, 115));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.dispose();
    }

    static JPanel card(int padding) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(22, 44, 57));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(86, 128, 139), 2),
                BorderFactory.createEmptyBorder(padding, padding, padding, padding)));
        return panel;
    }

    static JLabel text(String value, int size, boolean bold) {
        JLabel label = new JLabel(value);
        label.putClientProperty("html.disable", Boolean.TRUE);
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(CENTER_ALIGNMENT);
        return label;
    }

    static JButton button(String value, Runnable action) {
        JButton button = new JButton(value);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(new Color(246, 194, 82));
        button.setForeground(new Color(28, 40, 49));
        button.setFocusPainted(false);
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(270, 42));
        button.setPreferredSize(new Dimension(270, 42));
        button.addActionListener(event -> action.run());
        return button;
    }
}
