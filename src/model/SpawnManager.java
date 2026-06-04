package model;

import utils.Config;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Gestisce lo spawn dei nemici: spawn iniziale, spawn dal portale,
 * validazione del punto di spawn e la logica periodica di mantenimento.
 */
class SpawnManager {

    private final Random randomGenerator = new Random();
    private long lastPortalSpawnTime = 0;
    private long lastBossPortalSpawnTime = 0;

    SpawnManager(Model model) {
        // Il costruttore accetta Model per coerenza col pattern Facade.
    }

    // ==========================================================
    // SPAWN PRINCIPALE (Inizio livello)
    // ==========================================================

    /**
     * Spawna un singolo nemico in una posizione casuale valida.
     * Il tipo di nemico viene scelto secondo la distribuzione della zona
     * definita in LevelManager.createEnemyForZone().
     */
    void spawnEnemy(List<Enemy> enemies, Player player, int[][] map, LevelManager levelManager) {
        int attempts = 0;
        while (attempts < 100) {
            int c = randomGenerator.nextInt(Config.GRID_WIDTH);
            int r = randomGenerator.nextInt(Config.GRID_HEIGHT);

            if (isValidSpawnPoint(c, r, player, enemies, map)) {
                int roll = randomGenerator.nextInt(100);
                Enemy newEnemy = levelManager.createEnemyForZone(roll, c, r);
                enemies.add(newEnemy);
                System.out.println("Model: Generato " + newEnemy.getType() + " in (" + c + ", " + r + ")");
                return;
            }
            attempts++;
        }
    }

    // ==========================================================
    // SPAWN DAL PORTALE (Mappe 0 e 1)
    // ==========================================================

    /**
     * Spawna un nemico alla posizione del portale.
     * Il tipo segue la distribuzione della zona corrente.
     */
    void spawnEnemyAtPortal(List<Enemy> enemies, int portalCol, int portalRow, LevelManager levelManager) {
        int roll = randomGenerator.nextInt(100);
        Enemy newEnemy = levelManager.createEnemyForZone(roll, portalCol, portalRow);
        enemies.add(newEnemy);
        System.out.println("Allarme! Il Portale ha sputato fuori un " + newEnemy.getType() + "!");
    }

    /**
     * Gestione periodica del portale classico (Mappe 0 e 1).
     * Spawna ogni 10 secondi finche' il numero di nemici vivi non raggiunge il cap
     * definito da LevelManager.getPortalMaxEnemies().
     */
    void manageSpawning(List<Enemy> enemies, int portalCol, int portalRow,
                        boolean portalRevealed, LevelManager levelManager) {
        if (enemies.isEmpty()) return;

        // Conta nemici vivi (esclude eventuali cadaveri)
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

    // ==========================================================
    // SPAWN PORTALE BOSS (MAPPA 2)
    // ==========================================================

    /**
     * Gestisce lo spawn periodico di ChasingGoblin dal portale fisso della mappa Boss.
     * Spawna solo se:
     * - Il portale boss e' attivo
     * - Il numero di goblin vivi (non-boss) non supera il cap
     * - E' trascorso l'intervallo configurato dall'ultimo spawn
     */
    void manageBossSpawning(List<Enemy> enemies, boolean bossPortalActive) {
        if (!bossPortalActive) return;

        // Conta solo i goblin vivi (escluso il Boss)
        long livingGoblins = enemies.stream()
                .filter(e -> !e.isDead() && e.getType() != utils.EnemyType.BOSS)
                .count();

        if (livingGoblins >= Config.BOSS_PORTAL_MAX_GOBLINS) return;

        long now = System.currentTimeMillis();
        if (now - lastBossPortalSpawnTime > Config.BOSS_PORTAL_SPAWN_INTERVAL_MS) {
            Enemy goblin = new ChasingGoblin(Config.BOSS_PORTAL_COL, Config.BOSS_PORTAL_ROW);
            enemies.add(goblin);
            lastBossPortalSpawnTime = now;
            System.out.println("PORTALE BOSS: Chasing Goblin spawnato in ["
                    + Config.BOSS_PORTAL_ROW + ", " + Config.BOSS_PORTAL_COL + "]!");
        }
    }

    /** Resetta il timer del portale boss (chiamato all'attivazione). */
    void resetBossPortalTimer() {
        // Il primo goblin esce dopo l'intervallo completo (10s), non subito
        lastBossPortalSpawnTime = System.currentTimeMillis();
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

        // Verifica che la cella sia in un'area abbastanza aperta (almeno MIN_OPEN_AREA celle libere collegate)
        // Evita che i goblin nascano intrappolati in piccoli sgabuzzini tra le casse
        if (countConnectedCells(col, row, map) < Config.MIN_SPAWN_OPEN_AREA) return false;

        return true;
    }

    /**
     * BFS Flood-Fill: conta quante celle calpestabili sono connesse alla cella (startCol, startRow).
     * La ricerca si ferma non appena vengono trovate almeno maxCount celle,
     * per evitare di scorrere l'intera mappa ad ogni spawn.
     *
     * @param startCol colonna di partenza
     * @param startRow riga di partenza
     * @param map      mappa corrente
     * @return numero di celle libere connesse (capped a maxCount)
     */
    private int countConnectedCells(int startCol, int startRow, int[][] map) {
        final int maxCount = Config.MIN_SPAWN_OPEN_AREA; // smette di contare appena la soglia è raggiunta
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

    /** Resetta il timer del portale classico (chiamato quando il portale viene scoperto). */
    void resetPortalTimer() {
        lastPortalSpawnTime = System.currentTimeMillis();
    }
}
