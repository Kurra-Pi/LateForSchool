import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

final class Candy {
    final int x;
    final int y;
    boolean collected;

    Candy(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Rectangle getBounds() {
        return new Rectangle(x, y, 22, 20);
    }

    void draw(Graphics2D graphics) {
        int x = this.x;
        int y = this.y;
        graphics.setColor(new Color(233, 77, 105));
        graphics.fillPolygon(new int[]{x + 4, x, x + 4},
                new int[]{y + 5, y + 10, y + 16}, 3);
        graphics.fillPolygon(new int[]{x + 18, x + 22, x + 18},
                new int[]{y + 5, y + 10, y + 16}, 3);
        graphics.setColor(new Color(255, 207, 74));
        graphics.fillRoundRect(x + 3, y + 4, 17, 14, 8, 8);
        graphics.setColor(new Color(250, 238, 185));
        graphics.fillOval(x + 7, y + 7, 5, 5);
    }
}
