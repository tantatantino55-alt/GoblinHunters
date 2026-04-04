package model;

import utils.Config;

import java.util.List;
import java.util.Random;

/**
 * Gestisce lo spawn dei nemici: spawn iniziale, spawn dal portale,
 * validazione del punto di spawn e la logica periodica di mantenimento.
 */
class SpawnManager {

    private final Random randomGenerator = new Random();
    private long lastPortalSpawnTime = 0;

    SpawnManager(Model model) {
        // Il costruttore accetta Model per coerenza col pattern Facade.
    }

    // ==========================================================
    // SPAWN PRINCIPALE
    // ==========================================================

    void spawnEnemy(List<Enemy> enemies, Player player, int[][] map) {
        int attempts = 0;
        while (attempts < 100) {
            int c = randomGenerator.nextInt(Config.GRID_WIDTH);
            int r = randomGenerator.nextInt(Config.GRID_HEIGHT);

            if (isValidSpawnPoint(c, r, player, enemies, map)) {
                int typeIndex = randomGenerator.nextInt(3);
                Enemy newEnemy = createEnemy(typeIndex, c, r);
                enemies.add(newEnemy);
                System.out.println("Model: Generato " + newEnemy.getType() + " in (" + c + ", " + r + ")");
                return;
            }
            attempts++;
        }
    }

    void spawnEnemyAtPortal(List<Enemy> enemies, int portalCol, int portalRow) {
        int typeIndex = randomGenerator.nextInt(3);
        Enemy newEnemy = createEnemy(typeIndex, portalCol, portalRow);
        enemies.add(newEnemy);
        System.out.println("Allarme! Il Portale ha sputato fuori un " + newEnemy.getType() + "!");
    }

    // ==========================================================
    // GESTIONE PERIODICA
    // ==========================================================

    void manageSpawning(List<Enemy> enemies, int portalCol, int portalRow, boolean portalRevealed) {
        if (enemies.isEmpty()) return;

        if (portalRevealed && enemies.size() < 6) {
            long now = System.currentTimeMillis();
            if (now - lastPortalSpawnTime > 10_000) {
                spawnEnemyAtPortal(enemies, portalCol, portalRow);
                lastPortalSpawnTime = now;
            }
        }
    }

    // ==========================================================
    // HELPER
    // ==========================================================

    private boolean isValidSpawnPoint(int col, int row, Player player, List<Enemy> enemies, int[][] map) {
        if (col < 1 || col >= Config.GRID_WIDTH - 1 || row < 1 || row >= Config.GRID_HEIGHT - 1) return false;
        if (map[row][col] != Config.CELL_EMPTY) return false;

        double dist = Math.sqrt(Math.pow(col - player.getXCoordinate(), 2) + Math.pow(row - player.getYCoordinate(), 2));
        if (dist < Config.MIN_SPAWN_DISTANCE) return false;

        for (Enemy e : enemies) {
            if (Math.abs(e.getX() - col) < 0.8 && Math.abs(e.getY() - row) < 0.8) return false;
        }
        return true;
    }

    private Enemy createEnemy(int typeIndex, double x, double y) {
        return switch (typeIndex) {
            case 0  -> new ChasingGoblin(x, y);
            case 1  -> new ShooterGoblin(x, y);
            default -> new CommonGoblin(x, y);
        };
    }

    /** Resetta il timer del portale (chiamato quando il portale viene scoperto). */
    void resetPortalTimer() {
        lastPortalSpawnTime = System.currentTimeMillis();
    }
}
