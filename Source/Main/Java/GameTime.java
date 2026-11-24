import java.util.Locale;

final class GameTime {
    private GameTime() {
    }

    static String format(long milliseconds) {
        long value = Math.max(0, milliseconds);
        return String.format(Locale.ROOT, "%02d:%02d.%02d",
                value / 60000, (value / 1000) % 60, (value % 1000) / 10);
    }
}
