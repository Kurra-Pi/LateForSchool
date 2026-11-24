import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

final class RooftopSprite {
    private final BufferedImage image;
    private final Rectangle drawingBounds;
    private final String label;
    private final Color fallback;

    RooftopSprite(BufferedImage source, Rectangle drawingBounds, String label, Color fallback) {
        this.drawingBounds = new Rectangle(drawingBounds);
        this.label = label;
        this.fallback = fallback;
        if (source == null) {
            image = null;
        } else {
            image = new BufferedImage(drawingBounds.width, drawingBounds.height,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            graphics.drawImage(source, 0, 0, image.getWidth(), image.getHeight(), null);
            graphics.dispose();
        }
    }

    Rectangle getDrawingBounds() {
        return new Rectangle(drawingBounds);
    }

    Rectangle roofCollision(int thickness) {
        if (image != null) {
            int minimumWidth = Math.max(1, (int) Math.ceil(image.getWidth() * 0.6));
            for (int y = 0; y < image.getHeight(); y++) {
                int start = -1;
                for (int x = 0; x <= image.getWidth(); x++) {
                    boolean solid = x < image.getWidth()
                            && (image.getRGB(x, y) >>> 24) >= SpriteMask.ALPHA_THRESHOLD;
                    if (solid && start < 0) {
                        start = x;
                    } else if (!solid && start >= 0) {
                        if (x - start >= minimumWidth) {
                            return new Rectangle(drawingBounds.x + start,
                                    drawingBounds.y + y, x - start, thickness);
                        }
                        start = -1;
                    }
                }
            }
        }
        return new Rectangle(drawingBounds.x, drawingBounds.y, drawingBounds.width, thickness);
    }

    void draw(Graphics2D graphics) {
        if (image != null) {
            graphics.drawImage(image, drawingBounds.x, drawingBounds.y, null);
        } else {
            ImageUtils.drawPngOrPlaceholder(graphics, null,
                    drawingBounds.x, drawingBounds.y, drawingBounds.width,
                    drawingBounds.height, label, fallback);
        }
    }
}
