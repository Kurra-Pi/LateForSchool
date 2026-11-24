import java.awt.Color;
import java.awt.image.BufferedImage;

final class TrashPlatform extends PngPlatform {
    static final int DISPLAY_WIDTH = 100;
    static final int DISPLAY_HEIGHT = 55;

    TrashPlatform(int centerX, int bottomY, BufferedImage image) {
        super(centerX, bottomY, image, "TRASH", new Color(104, 119, 76), DISPLAY_WIDTH, DISPLAY_HEIGHT);
    }
}
