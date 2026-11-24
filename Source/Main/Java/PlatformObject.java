import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

abstract class PlatformObject {
    final Rectangle bounds;

    PlatformObject(int x, int y, int width, int height) {
        bounds = new Rectangle(x, y, width, height);
    }

    abstract void draw(Graphics2D graphics);

    List<Rectangle> getCollisionBounds() {
        return List.of(bounds);
    }

    boolean isOneWay() {
        return false;
    }

    int getDrawLayer() {
        return 1;
    }
}
