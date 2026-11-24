import java.util.Locale;

final class PlayerNames {
    static final int MAX_LENGTH = 60;

    private PlayerNames() {
    }

    static String normalize(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Enter your player name.");
        }
        String normalized = name.replaceAll("[\\p{javaWhitespace}\\p{Zs}]+", " ").strip();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Enter your player name.");
        }
        if (normalized.codePointCount(0, normalized.length()) > MAX_LENGTH) {
            throw new IllegalArgumentException("Player names can contain up to 60 characters.");
        }
        if (normalized.codePoints().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Player names cannot contain control characters.");
        }
        return normalized;
    }

    static String key(String name) {
        return normalize(name).toUpperCase(Locale.ROOT);
    }
}
