import java.awt.Color;
import java.awt.Graphics2D;

final class Player {
    double x;
    double y;
    double velocityY;
    boolean onGround;
    boolean facingRight = true;

    Player(double x, double y) {
        this.x = x;
        this.y = y;
    }

    void draw(Graphics2D graphics) {
        int x = (int) this.x;
        int y = (int) this.y;

        // Backpack, behind the student
        graphics.setColor(new Color(132, 73, 38));
        int bagX = this.facingRight ? x + 1 : x + 23;
        graphics.fillRoundRect(bagX, y + 24, 9, 15, 4, 4);

        // Hair and face
        graphics.setColor(new Color(35, 25, 20));
        graphics.fillRect(x + 7, y, 18, 6);
        graphics.fillRect(x + 4, y + 4, 23, 6);
        graphics.setColor(new Color(255, 211, 160));
        graphics.fillRect(x + 8, y + 7, 17, 17);
        graphics.setColor(new Color(35, 25, 20));
        graphics.fillRect(x + 5, y + 7, 4, 11);
        graphics.fillRect(x + 23, y + 7, 4, 8);

        // Eyes, neck, and white school uniform
        graphics.setColor(Color.BLACK);
        int eyeX = this.facingRight ? x + 19 : x + 11;
        graphics.fillRect(eyeX, y + 13, 3, 4);
        graphics.setColor(new Color(255, 211, 160));
        graphics.fillRect(x + 14, y + 23, 7, 4);
        graphics.setColor(new Color(240, 240, 240));
        graphics.fillRect(x + 5, y + 26, 22, 13);
        graphics.setColor(new Color(220, 220, 220));
        graphics.fillRect(x + 2, y + 27, 4, 8);
        graphics.fillRect(x + 26, y + 27, 4, 8);
        graphics.setColor(new Color(30, 90, 210));
        graphics.fillRect(x + 16, y + 26, 3, 8);

        // Pants and shoes
        graphics.setColor(new Color(20, 20, 20));
        graphics.fillRect(x + 6, y + 38, 9, 3);
        graphics.fillRect(x + 18, y + 38, 9, 3);
        graphics.setColor(new Color(35, 30, 25));
        graphics.fillRect(x + 3, y + 40, 13, 2);
        graphics.fillRect(x + 17, y + 40, 13, 2);
    }
}
