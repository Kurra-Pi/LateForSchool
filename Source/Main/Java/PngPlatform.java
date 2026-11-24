import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

abstract class PngPlatform extends PlatformObject {
    private final SpriteMask mask;
    private final Rectangle spriteBounds;
    private final List<Rectangle> collisionBounds;
    private final String label;
    private final Color fallback;

    PngPlatform(int centerX, int bottomY, BufferedImage image, String label,
            Color fallback, int displayWidth, int displayHeight) {
        super(0, 0, 1, 1);
        this.label = label;
        this.fallback = fallback;
        mask = new SpriteMask(image, displayWidth, displayHeight);
        spriteBounds = new Rectangle(centerX - mask.width / 2,
                bottomY - mask.height, mask.width, mask.height);
        collisionBounds = mask.worldPieces(spriteBounds.x, spriteBounds.y);
        bounds.setBounds(spriteBounds);
    }

    Rectangle getSpriteBounds() {
        return new Rectangle(spriteBounds);
    }

    @Override
    List<Rectangle> getCollisionBounds() {
        return List.copyOf(collisionBounds);
    }

    @Override
    void draw(Graphics2D graphics) {
        if (mask.image != null) {
            graphics.drawImage(mask.image, spriteBounds.x, spriteBounds.y, null);
        } else {
            ImageUtils.drawPngOrPlaceholder(graphics, null,
                    spriteBounds.x, spriteBounds.y,
                    spriteBounds.width, spriteBounds.height, label, fallback);
        }
    }
}
