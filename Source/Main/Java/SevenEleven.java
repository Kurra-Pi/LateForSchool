import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

final class SevenEleven extends PlatformObject {
    static final int DISPLAY_WIDTH = 375;
    static final int DISPLAY_HEIGHT = 205;
    static final int ROOF_OFFSET_Y = 0;
    static final int ROOF_THICKNESS = 8;
    private final BufferedImage image;
    private final Rectangle spriteBounds;

    SevenEleven(int x, int bottomY, BufferedImage image) {
        super(x, bottomY - DISPLAY_HEIGHT + ROOF_OFFSET_Y,
                DISPLAY_WIDTH, ROOF_THICKNESS);
        this.image = image == null ? null
                : ImageUtils.scaleToFit(image, DISPLAY_WIDTH, DISPLAY_HEIGHT);
        int width = this.image == null ? DISPLAY_WIDTH : this.image.getWidth();
        int height = this.image == null ? DISPLAY_HEIGHT : this.image.getHeight();
        spriteBounds = new Rectangle(x + (DISPLAY_WIDTH - width) / 2,
                bottomY - height, width, height);
        Rectangle roof = findRoof(this.image, width);
        bounds.setBounds(spriteBounds.x + roof.x,
                spriteBounds.y + roof.y + ROOF_OFFSET_Y,
                roof.width, ROOF_THICKNESS);
    }

    private static Rectangle findRoof(BufferedImage image, int width) {
        if (image != null) {
            int minimumRun = Math.max(1, (int) Math.ceil(width * 0.6));
            for (int y = 0; y < image.getHeight(); y++) {
                int start = -1;
                for (int x = 0; x <= width; x++) {
                    boolean opaque = x < width
                            && (image.getRGB(x, y) >>> 24) >= SpriteMask.ALPHA_THRESHOLD;
                    if (opaque && start < 0) {
                        start = x;
                    } else if (!opaque && start >= 0) {
                        if (x - start >= minimumRun) {
                            return new Rectangle(start, y, x - start, 1);
                        }
                        start = -1;
                    }
                }
            }
        }
        return new Rectangle(0, 0, width, 1);
    }

    Rectangle getSpriteBounds() {
        return new Rectangle(spriteBounds);
    }

    @Override
    boolean isOneWay() {
        return true;
    }

    @Override
    int getDrawLayer() {
        return 0;
    }

    @Override
    void draw(Graphics2D g) {
        if (image != null) {
            g.drawImage(image, spriteBounds.x, spriteBounds.y, null);
        } else {
            ImageUtils.drawPngOrPlaceholder(g, null,
                    spriteBounds.x, spriteBounds.y,
                    spriteBounds.width, spriteBounds.height,
                    "7-ELEVEN PNG", new Color(218, 207, 176));
        }
    }
}
