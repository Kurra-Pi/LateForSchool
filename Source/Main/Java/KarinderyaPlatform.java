import java.awt.Color;
import java.awt.image.BufferedImage;

final class KarinderyaPlatform extends PngPlatform {
    static final int DISPLAY_WIDTH = 222;
    static final int DISPLAY_HEIGHT = 133;

    KarinderyaPlatform(int centerX, int bottomY, BufferedImage image) {
        super(centerX, bottomY, image, "PARES", new Color(160, 91, 57), DISPLAY_WIDTH, DISPLAY_HEIGHT);
    }
}
