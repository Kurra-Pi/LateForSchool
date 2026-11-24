import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

final class RoofPlatform extends PlatformObject {
    static final int ROOF_THICKNESS = 4;
    private final RooftopSprite sprite;

    RoofPlatform(int x, int roofY, int width) {
        this(x, roofY, width, ImageUtils.loadPng("/assets/roof_platform.png"));
    }

    RoofPlatform(int x, int roofY, int width, BufferedImage image) {
        super(x, roofY, width, ROOF_THICKNESS);
        if (width <= 0 || roofY < 0 || roofY >= GameConfig.GROUND_Y) {
            throw new IllegalArgumentException("Roof platform needs a positive width and a roof above the road");
        }
        sprite = new RooftopSprite(image,
                new Rectangle(x, roofY, width, GameConfig.GROUND_Y - roofY),
                "ROOF PNG", new Color(104, 58, 42));
        bounds.setBounds(sprite.roofCollision(ROOF_THICKNESS));
    }

    Rectangle getSpriteBounds() {
        return sprite.getDrawingBounds();
    }

    @Override
    boolean isOneWay() {
        return true;
    }

    @Override
    void draw(Graphics2D graphics) {
        sprite.draw(graphics);
    }
}
