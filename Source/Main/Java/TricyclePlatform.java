import java.awt.Color;
import java.awt.image.BufferedImage;

final class TricyclePlatform extends PngPlatform {
    static final int DISPLAY_WIDTH = 150;
    static final int DISPLAY_HEIGHT = 114;

    TricyclePlatform(int centerX, int bottomY, BufferedImage image) {
        super(centerX, bottomY, image, "TRICYCLE", new Color(63, 123, 110), DISPLAY_WIDTH, DISPLAY_HEIGHT);
    }
}
