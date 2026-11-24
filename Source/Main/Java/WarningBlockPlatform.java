import java.awt.Color;
import java.awt.image.BufferedImage;

final class WarningBlockPlatform extends PngPlatform {
    static final int DISPLAY_WIDTH = 36;
    static final int DISPLAY_HEIGHT = 80;

    WarningBlockPlatform(int centerX, int bottomY, BufferedImage image) {
        super(centerX, bottomY, image, "WARNING", new Color(233, 165, 43), DISPLAY_WIDTH, DISPLAY_HEIGHT);
    }
}
