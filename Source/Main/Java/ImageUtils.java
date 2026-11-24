import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

final class ImageUtils {
    private ImageUtils() {
    }

    static BufferedImage loadPng(String resourcePath) {
        try (InputStream input = LateForSchool.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                System.err.println("Missing PNG resource: " + resourcePath);
                return null;
            }

            BufferedImage image = ImageIO.read(new MemoryCacheImageInputStream(input));
            if (image == null) {
                System.err.println("Could not read PNG resource: " + resourcePath);
                return null;
            }
            return trimTransparentBorders(image);
        } catch (IOException exception) {
            System.err.println("Could not load PNG " + resourcePath + ": "
                    + exception.getMessage());
            return null;
        }
    }

    private static BufferedImage trimTransparentBorders(BufferedImage source) {
        int minX = source.getWidth();
        int minY = source.getHeight();
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                if ((source.getRGB(x, y) >>> 24) > 10) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return source;
        }

        BufferedImage trimmed = new BufferedImage(maxX - minX + 1,
                maxY - minY + 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = trimmed.createGraphics();
        graphics.drawImage(source, 0, 0, trimmed.getWidth(), trimmed.getHeight(),
                minX, minY, maxX + 1, maxY + 1, null);
        graphics.dispose();
        return trimmed;
    }

    static BufferedImage scaleToFit(BufferedImage source, int maxWidth, int maxHeight) {
        double scale = Math.min(maxWidth / (double) source.getWidth(),
                maxHeight / (double) source.getHeight());
        int width = Math.max(1, (int) Math.round(source.getWidth() * scale));
        int height = Math.max(1, (int) Math.round(source.getHeight() * scale));
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = scaled.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        graphics.drawImage(source, 0, 0, width, height, null);
        graphics.dispose();
        return scaled;
    }

    static void drawPngOrPlaceholder(Graphics2D graphics, BufferedImage image,
            int x, int y, int width, int height, String label, Color fallbackColor) {
        if (image != null) {
            double scale = Math.min(width / (double) image.getWidth(),
                    height / (double) image.getHeight());
            int drawWidth = (int) Math.round(image.getWidth() * scale);
            int drawHeight = (int) Math.round(image.getHeight() * scale);
            int drawX = x + (width - drawWidth) / 2;
            int drawY = y + height - drawHeight;
            graphics.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
            return;
        }

        graphics.setColor(fallbackColor);
        graphics.fillRoundRect(x, y, width, height, 10, 10);
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("SansSerif", Font.BOLD, 12));
        graphics.drawString(label, x + 8, y + height / 2);
    }
}
