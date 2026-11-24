import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LateForSchool extends JFrame {
    private final GameApplication application;

    public LateForSchool() {
        super("Late for School: A School-Day Dash");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        application = new GameApplication();
        setContentPane(application);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                application.close();
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LateForSchool::new);
    }
}
