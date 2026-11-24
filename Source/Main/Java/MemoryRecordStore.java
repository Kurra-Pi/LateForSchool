import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


final class MemoryRecordStore implements RecordStore {
    private final Map<String, PlayerRecord> records = new LinkedHashMap<>();

    @Override
    public synchronized PlayerRecord createOrLoad(String name) {
        String normalized = PlayerNames.normalize(name);
        return records.computeIfAbsent(PlayerNames.key(normalized),
                ignored -> new PlayerRecord(normalized, null, null, 0));
    }

    @Override
    public synchronized PlayerRecord saveBest(String name, int score, long elapsedMillis, int candies) {
        PlayerRecord.validateRun(score, elapsedMillis, candies);
        PlayerRecord previous = createOrLoad(name);
        if (PlayerRecord.isBetter(score, elapsedMillis, previous)) {
            // This is the candy count of the best run, not an accumulating counter.
            PlayerRecord improved = new PlayerRecord(previous.playerName(), score, elapsedMillis, candies);
            records.put(PlayerNames.key(name), improved);
            return improved;
        }
        return previous;
    }

    @Override
    public synchronized List<PlayerRecord> list() {
        List<PlayerRecord> result = new ArrayList<>(records.values());
        result.sort(Comparator.comparing(PlayerRecord::bestScore,
                Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(PlayerRecord::bestTimeMillis, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(record -> PlayerNames.key(record.playerName())));
        return List.copyOf(result);
    }

    @Override
    public synchronized boolean delete(String name) {
        return records.remove(PlayerNames.key(name)) != null;
    }

    @Override
    public String description() {
        return "Session only - configure Oracle JDBC to keep records after closing";
    }
}
