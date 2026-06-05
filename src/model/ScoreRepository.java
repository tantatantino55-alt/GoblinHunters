package model;

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

    private static final Path FILE = Paths.get("scores.txt");

    private static final String[] SEED = {
        "SHADOW:8500:0",
        "EMBER:7200:1",
        "BLAZE:5800:2",
        "STORM:4100:3",
        "GOBLIN:2300:4"
    };

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

    // --- I/O privato ---

    private void ensureFile() {
        if (Files.notExists(FILE) || isFileEmpty()) {
            writeAll(parseSeed());
        }
    }

    private boolean isFileEmpty() {
        try { return Files.size(FILE) == 0; } catch (IOException e) { return true; }
    }

    private List<ScoreRecord> parseSeed() {
        List<ScoreRecord> list = new ArrayList<>();
        for (String s : SEED) list.add(parseLine(s));
        return list;
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
            System.err.println("ScoreRepository: errore scrittura — " + e.getMessage());
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
        if (name == null || name.isBlank()) return "ANONIMO";
        String s = name.replaceAll(":", "").trim();
        return s.substring(0, Math.min(12, s.length())).toUpperCase();
    }
}
