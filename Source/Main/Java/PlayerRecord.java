record PlayerRecord(String playerName, Integer bestScore, Long bestTimeMillis,
                    int totalCandies) {
    static final long MAX_TIME_MILLIS = 999_999_999_999_999L;

    PlayerRecord {
        playerName = PlayerNames.normalize(playerName);
        if ((bestScore == null) != (bestTimeMillis == null)) {
            throw new IllegalArgumentException("A saved run needs both a score and a time.");
        }
        if (bestScore != null && (bestScore < 0 || bestTimeMillis < 0
                || bestTimeMillis > MAX_TIME_MILLIS)) {
            throw new IllegalArgumentException("Saved score and time must be nonnegative.");
        }
        if (totalCandies < 0) {
            throw new IllegalArgumentException("Candies cannot be negative.");
        }
    }

    boolean hasRecord() {
        return bestScore != null;
    }

    static boolean isBetter(int score, long elapsedMillis, PlayerRecord previous) {
        validateRun(score, elapsedMillis, 0);
        return previous == null || !previous.hasRecord()
                || score > previous.bestScore()
                || (score == previous.bestScore() && elapsedMillis < previous.bestTimeMillis());
    }

    static void validateRun(int score, long elapsedMillis, int candies) {
        if (score < 0 || elapsedMillis < 0 || elapsedMillis > MAX_TIME_MILLIS || candies < 0) {
            throw new IllegalArgumentException("Score, completion time, and candies must be nonnegative.");
        }
    }
}
