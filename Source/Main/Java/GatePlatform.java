import java.awt.Color;
import java.awt.image.BufferedImage;

final class GatePlatform extends PngPlatform {
    static final int DISPLAY_WIDTH = 100;
    static final int DISPLAY_HEIGHT = 200;  

    GatePlatform(int centerX, int bottomY, BufferedImage image) {
        super(centerX, bottomY, image, "GATE", new Color(123, 89, 58), DISPLAY_WIDTH, DISPLAY_HEIGHT);
    }
}
