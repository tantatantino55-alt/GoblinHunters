package goblinhunters.model;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ScoreRepository {

    public static class ScoreRecord {
        public final String name;
        public final int    score;
        public final long   timestamp;

        ScoreRecord(String name, int score, long timestamp) {
            this.name      = name;
            this.score     = score;
            this.timestamp = timestamp;
        }
    }

    private static ScoreRepository instance;

    public static ScoreRepository getInstance() {
        if (instance == null) instance = new ScoreRepository();
        return instance;
    }

    private static final Path DIR  = Paths.get(System.getProperty("user.home"), "GoblinHunters");
    private static final Path FILE = DIR.resolve("scores.txt");

    private ScoreRepository() {
        ensureFile();
    }

    public void saveScore(String name, int score) {
        List<ScoreRecord> all = readAll();
        all.add(new ScoreRecord(sanitize(name), score, System.currentTimeMillis()));
        all.sort((a, b) -> Integer.compare(b.score, a.score));
        writeAll(all);
    }

    public List<ScoreRecord> getTopScores() {
        List<ScoreRecord> all = readAll();
        all.sort((a, b) -> Integer.compare(b.score, a.score));
        return all.subList(0, Math.min(5, all.size()));
    }

    public boolean isTopFive(int score) {
        List<ScoreRecord> top = getTopScores();
        if (top.size() < 5) return true;
        return score > top.get(top.size() - 1).score;
    }

    // private I/O

    private void ensureFile() {
        try {
            if (Files.notExists(DIR))  Files.createDirectories(DIR);
            if (Files.notExists(FILE)) Files.createFile(FILE);
        } catch (IOException e) {
            System.err.println("[ScoreRepository] Failed to create data file: " + e.getMessage());
        }
    }

    private List<ScoreRecord> readAll() {
        try {
            List<ScoreRecord> list = new ArrayList<>();
            for (String line : Files.readAllLines(FILE)) {
                if (!line.isBlank()) list.add(parseLine(line));
            }
            return list;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void writeAll(List<ScoreRecord> records) {
        try (BufferedWriter w = Files.newBufferedWriter(FILE)) {
            for (ScoreRecord r : records)
                w.write(r.name + ":" + r.score + ":" + r.timestamp + "\n");
        } catch (IOException e) {
            System.err.println("[ScoreRepository] Write error: " + e.getMessage());
        }
    }

    private ScoreRecord parseLine(String line) {
        String[] p = line.split(":");
        String name  = p.length > 0 ? p[0].trim() : "???";
        int    score = 0;
        long   ts    = 0;
        try { if (p.length > 1) score = Integer.parseInt(p[1].trim()); } catch (NumberFormatException ignored) {}
        try { if (p.length > 2) ts    = Long.parseLong(p[2].trim());   } catch (NumberFormatException ignored) {}
        return new ScoreRecord(name, score, ts);
    }

    private String sanitize(String name) {
        if (name == null || name.isBlank()) return "UNKNOWN";
        String s = name.replaceAll(":", "").trim();
        return s.substring(0, Math.min(12, s.length())).toUpperCase();
    }
}
