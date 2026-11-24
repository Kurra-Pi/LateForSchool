import java.awt.Color;
import java.awt.image.BufferedImage;

final class TablePlatform extends PngPlatform {
    static final int DISPLAY_WIDTH = 149;
    static final int DISPLAY_HEIGHT = 49;
    
    TablePlatform(int centerX, int bottomY, BufferedImage image) {
        super(centerX, bottomY, image, "TABLE", new Color(153, 102, 52), DISPLAY_WIDTH, DISPLAY_HEIGHT);
    }

    @Override
    int getDrawLayer() {
        return 2;
    }
}
