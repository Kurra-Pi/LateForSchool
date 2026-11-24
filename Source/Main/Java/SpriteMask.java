import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class SpriteMask {
    static final int ALPHA_THRESHOLD = 32;

    final BufferedImage image;
    final int width;
    final int height;
    private final List<Rectangle> pieces = new ArrayList<>();

    SpriteMask(BufferedImage source, int maxWidth, int maxHeight) {
        if (source == null) {
            width = maxWidth;
            height = maxHeight;
            image = null;
            pieces.add(new Rectangle(0, 0, width, height));
            return;
        }

        image = ImageUtils.scaleToFit(source, maxWidth, maxHeight);
        width = image.getWidth();
        height = image.getHeight();
        buildPieces();
    }

    private void buildPieces() {
        Map<Long, Rectangle> previousRow = new HashMap<>();
        for (int y = 0; y < height; y++) {
            Map<Long, Rectangle> currentRow = new HashMap<>();
            int x = 0;
            while (x < width) {
                if (!isOpaque(x, y)) {
                    x++;
                    continue;
                }
                int start = x;
                while (x < width && isOpaque(x, y)) {
                    x++;
                }
                int runWidth = x - start;
                long key = ((long) start << 32) | runWidth;
                Rectangle piece = previousRow.get(key);
                if (piece == null) {
                    piece = new Rectangle(start, y, runWidth, 1);
                    pieces.add(piece);
                } else {
                    piece.height++;
                }
                currentRow.put(key, piece);
            }
            previousRow = currentRow;
        }
    }

    boolean isOpaque(int x, int y) {
        return image != null && x >= 0 && x < width && y >= 0 && y < height
                && (image.getRGB(x, y) >>> 24) >= ALPHA_THRESHOLD;
    }

    List<Rectangle> worldPieces(int x, int y) {
        List<Rectangle> result = new ArrayList<>();
        for (Rectangle piece : pieces) {
            result.add(new Rectangle(x + piece.x, y + piece.y,
                    piece.width, piece.height));
        }
        return result;
    }
}
