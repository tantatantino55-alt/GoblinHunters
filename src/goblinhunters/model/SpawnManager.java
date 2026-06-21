package goblinhunters.model;

import goblinhunters.utils.Config;
import goblinhunters.utils.EnemyType;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/** Manages enemy spawning: initial placement, portal spawning, and periodic replenishment. */
class SpawnManager {

    private final Random randomGenerator = new Random();
    private long lastPortalSpawnTime     = 0;
    private long lastBossPortalSpawnTime = 0;

    SpawnManager(Model model) {
        // accepts Model for consistency with the Facade pattern
    }

    // initial spawn

    void spawnEnemy(List<Enemy> enemies, Player player, int[][] map, LevelManager levelManager) {
        int attempts = 0;
        while (attempts < 100) {
            int c = randomGenerator.nextInt(Config.GRID_WIDTH);
            int r = randomGenerator.nextInt(Config.GRID_HEIGHT);

            if (isValidSpawnPoint(c, r, player, enemies, map)) {
                int roll = randomGenerator.nextInt(100);
                Enemy newEnemy = levelManager.createEnemyForZone(roll, c, r);
                enemies.add(newEnemy);
                return;
            }
            attempts++;
        }
    }

    // portal spawn (zones 0 and 1)

    void spawnEnemyAtPortal(List<Enemy> enemies, int portalCol, int portalRow, LevelManager levelManager) {
        int roll = randomGenerator.nextInt(100);
        Enemy newEnemy = levelManager.createEnemyForZone(roll, portalCol, portalRow);
        enemies.add(newEnemy);
    }

    void manageSpawning(List<Enemy> enemies, int portalCol, int portalRow,
                        boolean portalRevealed, LevelManager levelManager) {
        if (enemies.isEmpty()) return;

        long livingCount = enemies.stream().filter(e -> !e.isDead()).count();
        int maxEnemies = levelManager.getPortalMaxEnemies();

        if (portalRevealed && livingCount < maxEnemies) {
            long now = System.currentTimeMillis();
            if (now - lastPortalSpawnTime > 10_000) {
                spawnEnemyAtPortal(enemies, portalCol, portalRow, levelManager);
                lastPortalSpawnTime = now;
            }
        }
    }

    void resetPortalTimer() {
        lastPortalSpawnTime = System.currentTimeMillis();
    }

    // boss portal (zone 2)

    void manageBossSpawning(List<Enemy> enemies, boolean bossPortalActive) {
        if (!bossPortalActive) return;

        long livingGoblins = enemies.stream()
                .filter(e -> !e.isDead() && e.getType() != EnemyType.BOSS)
                .count();

        if (livingGoblins >= Config.BOSS_PORTAL_MAX_GOBLINS) return;

        long now = System.currentTimeMillis();
        if (now - lastBossPortalSpawnTime > Config.BOSS_PORTAL_SPAWN_INTERVAL_MS) {
            enemies.add(new ChasingGoblin(Config.BOSS_PORTAL_COL, Config.BOSS_PORTAL_ROW));
            lastBossPortalSpawnTime = now;
        }
    }

    /** Delays the first portal spawn by a full interval so the boss has a head start. */
    void resetBossPortalTimer() {
        lastBossPortalSpawnTime = System.currentTimeMillis();
    }

    // helpers

    private boolean isValidSpawnPoint(int col, int row, Player player, List<Enemy> enemies, int[][] map) {
        if (col < 1 || col >= Config.GRID_WIDTH - 1 || row < 1 || row >= Config.GRID_HEIGHT - 1) return false;
        if (map[row][col] != Config.CELL_EMPTY) return false;

        double dist = Math.sqrt(Math.pow(col - player.getXCoordinate(), 2) + Math.pow(row - player.getYCoordinate(), 2));
        if (dist < Config.MIN_SPAWN_DISTANCE) return false;

        for (Enemy e : enemies) {
            if (Math.abs(e.getX() - col) < 0.8 && Math.abs(e.getY() - row) < 0.8) return false;
        }

        // reject spawn points inside tiny enclosed pockets between crates
        return countConnectedCells(col, row, map) >= Config.MIN_SPAWN_OPEN_AREA;
    }

    /**
     * BFS flood-fill that counts walkable cells reachable from (startCol, startRow).
     * Stops as soon as MIN_SPAWN_OPEN_AREA cells are found to avoid scanning the whole map.
     */
    private int countConnectedCells(int startCol, int startRow, int[][] map) {
        final int maxCount = Config.MIN_SPAWN_OPEN_AREA;
        boolean[][] visited = new boolean[Config.GRID_HEIGHT][Config.GRID_WIDTH];
        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;
        int count = 0;

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        while (!queue.isEmpty() && count < maxCount) {
            int[] curr = queue.poll();
            count++;
            for (int d = 0; d < 4; d++) {
                int nr = curr[0] + dr[d];
                int nc = curr[1] + dc[d];
                if (nr >= 0 && nr < Config.GRID_HEIGHT && nc >= 0 && nc < Config.GRID_WIDTH
                        && !visited[nr][nc] && map[nr][nc] == Config.CELL_EMPTY) {
                    visited[nr][nc] = true;
                    queue.add(new int[]{nr, nc});
                }
            }
        }
        return count;
    }
}
