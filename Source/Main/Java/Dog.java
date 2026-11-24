import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

final class Dog {
    static final int WIDTH = 38;
    static final int HEIGHT = 30;

    int x;
    final int y;
    final int leftLimit;
    final int rightLimit;
    int direction = 1;
    int stunFrames;

    Dog(int x, int y, int leftLimit, int rightLimit) {
        this.x = x;
        this.y = y;
        this.leftLimit = leftLimit;
        this.rightLimit = rightLimit;
    }

    void update() {
        if (stunFrames > 0) {
            stunFrames--;
            return;
        }

        x += direction * 2;
        if (x < leftLimit) {
            x = leftLimit;
            direction = 1;
        }
        if (x + WIDTH > rightLimit) {
            x = rightLimit - WIDTH;
            direction = -1;
        }
    }

    void stun() {
        stunFrames = 120;
    }

    boolean isStunned() {
        return stunFrames > 0;
    }

    Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    void draw(Graphics2D graphics) {
        int x = this.x;
        int y = this.y;

        if (this.isStunned()) {
            graphics.setColor(new Color(135, 82, 49));
            graphics.fillRoundRect(x + 1, y + 11, 34, 14, 12, 12);
            graphics.setColor(new Color(186, 124, 71));
            graphics.fillOval(x + 22, y + 7, 14, 13);
            graphics.setColor(new Color(78, 47, 32));
            graphics.fillOval(x + 28, y + 12, 3, 3);
            graphics.setColor(Color.WHITE);
            graphics.setFont(new Font("SansSerif", Font.BOLD, 14));
            graphics.drawString("Z", x + 9, y + 7);
            graphics.drawString("z", x + 21, y + 1);
            return;
        }

        graphics.setColor(new Color(140, 86, 51));
        graphics.fillRoundRect(x + 4, y + 8, 25, 15, 11, 11);
        graphics.fillOval(x + 20, y + 3, 15, 16);
        graphics.setColor(new Color(98, 57, 38));
        graphics.fillOval(x + 20, y + 1, 7, 10);
        graphics.fillOval(x + 30, y + 2, 7, 9);
        graphics.setColor(new Color(219, 174, 128));
        graphics.fillOval(x + 28, y + 12, 10, 7);
        graphics.setColor(Color.WHITE);
        graphics.fillOval(x + 29, y + 6, 4, 5);
        graphics.setColor(Color.BLACK);
        graphics.fillOval(x + 31, y + 8, 2, 3);
        graphics.fillOval(x + 36, y + 14, 3, 3);
        graphics.setColor(new Color(98, 57, 38));
        graphics.fillRect(x + 8, y + 20, 4, 9);
        graphics.fillRect(x + 23, y + 20, 4, 9);
        graphics.drawArc(x, y + 8, 12, 12, 40, 210);
    }
}
