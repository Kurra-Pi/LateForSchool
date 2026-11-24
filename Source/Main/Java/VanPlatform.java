import java.awt.Color;
import java.awt.image.BufferedImage;

final class VanPlatform extends PngPlatform {
    static final int DISPLAY_WIDTH = 260;
    static final int DISPLAY_HEIGHT = 142;

    VanPlatform(int centerX, int bottomY, BufferedImage image) {
        super(centerX, bottomY, image, "VAN", new Color(71, 126, 163), DISPLAY_WIDTH, DISPLAY_HEIGHT);
    }
}
