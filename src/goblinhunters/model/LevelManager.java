package goblinhunters.model;

import goblinhunters.utils.Config;

import java.util.List;

/** Manages level progression: zones, exit gate, portal, boss timer, and map transitions. */
class LevelManager {

    private final Model model;

    // progression
    private int currentZone = 0;
    private int difficultyCycle = 1;
    private String currentTheme = "VILLAGE";

    // transition flags
    private boolean exitGateActive = false;
    private boolean levelCompletedFlag = false;
    private boolean isTransitioning = false;
    private long lastExitGateSpawnTime = 0;

    // portal
    private int portalRow = -1;
    private int portalCol = -1;
    private boolean portalRevealed = false;
    // exit gate is always placed at (0,6)
    private final int exitGateRow = 0;
    private final int exitGateCol = 6;

    // boss timer
    private int bossPreparationTimer;
    private boolean isPreparationPhase = false;
    private static final int PREP_TIME_SECONDS = 20;

    // goblin portal on boss map (fixed position from Config)
    private boolean bossPortalActive = false;
    LevelManager(Model model) {
        this.model = model;
    }

    // ==========================================================
    // getters
    // ==========================================================

    int getCurrentZone()             { return currentZone; }
    int getDifficultyCycle()         { return difficultyCycle; }
    String getCurrentTheme()         { return currentTheme; }
    boolean isExitGateActive()       { return exitGateActive; }
    boolean isLevelCompletedFlag()   { return levelCompletedFlag; }
    boolean isTransitioning()        { return isTransitioning; }
    void setTransitioning(boolean t) { isTransitioning = t; }
    int getPortalRow()               { return portalRow; }
    int getPortalCol()               { return portalCol; }
    boolean isPortalRevealed()       { return portalRevealed; }
    int getExitGateRow()             { return exitGateRow; }
    int getExitGateCol()             { return exitGateCol; }
    long getExitGateActivationTime() { return lastExitGateSpawnTime; }
    boolean isPreparationPhase()     { return isPreparationPhase; }

    // boss portal
    boolean isBossPortalActive()          { return bossPortalActive; }
    int getBossPortalRow()                { return Config.BOSS_PORTAL_ROW; }
    int getBossPortalCol()                { return Config.BOSS_PORTAL_COL; }
    // ==========================================================
    // level configuration (count, cap, distribution)
    // ==========================================================

    /**
     * Number of enemies to spawn at the start of the level.
     * Zone 0: cycle 1=4, cycle 2=5, cycle 3+=6
     * Zone 1: cycle 1=5, cycle 2+=6
     * Zone 2: 0 (boss spawns after the preparation phase)
     */
    int getInitialEnemyCount() {
        int cycle = Math.min(difficultyCycle, 3); // cap at 3
        return switch (currentZone) {
            case 0 -> switch (cycle) {
                case 1  -> 4;
                case 2  -> 5;
                default -> 6;
            };
            case 1 -> switch (cycle) {
                case 1  -> 5;
                default -> 6;
            };
            default -> 0; // zone 2 (boss)
        };
    }

    /**
     * Maximum number of enemies the portal can maintain on the map.
     * Same value as getInitialEnemyCount (portal fills up to this cap).
     */
    int getPortalMaxEnemies() {
        return getInitialEnemyCount();
    }

    /**
     * Creates an enemy according to the current zone's distribution.
     * Zone 0: 75% Common, 25% Chasing
     * Zone 1: 40% Common, 30% Chasing, 30% Shooter
     *
     * @param roll random value 0-99
     */
    Enemy createEnemyForZone(int roll, double x, double y) {
        return switch (currentZone) {
            case 0 -> {
                // 75% Common (0-74), 25% Chasing (75-99)
                if (roll < 75) yield new CommonGoblin(x, y);
                else           yield new ChasingGoblin(x, y);
            }
            case 1 -> {
                // 40% Common (0-39), 30% Chasing (40-69), 30% Shooter (70-99)
                if (roll < 40)      yield new CommonGoblin(x, y);
                else if (roll < 70) yield new ChasingGoblin(x, y);
                else                yield new ShooterGoblin(x, y);
            }
            default -> new CommonGoblin(x, y); // zone 2 fallback (boss is spawned separately)
        };
    }

    // ==========================================================
    // portal
    // ==========================================================

    void setPortal(int row, int col) {
        this.portalRow = row;
        this.portalCol = col;
        this.portalRevealed = false;
    }

    /** Called by MapManager when a block is destroyed. */
    void onBlockDestroyed(int row, int col) {
        if (row == portalRow && col == portalCol) {
            portalRevealed = true;
            model.getSpawnManager().resetPortalTimer();
        }
    }

    // ==========================================================
    // exit gate
    // ==========================================================

    void checkExitGateCollision(List<Enemy> enemies, Player player, int[][] map) {
        // boss preparation phase locks the gate
        if (currentZone == 2 && isPreparationPhase) return;

        // gate is blocked until the boss portal deactivates (boss dead + all goblins cleared)
        if (currentZone == 2 && bossPortalActive) return;

        long livingEnemies = enemies.stream().filter(e -> !e.isDead()).count();

        if (livingEnemies == 0) {
            if (!exitGateActive) {
                exitGateActive = true;
                lastExitGateSpawnTime = System.currentTimeMillis();
                map[0][6] = Model.EXIT_GATE_ID;
                if (map[0][0] == Model.EXIT_GATE_ID) map[0][0] = Config.CELL_EMPTY;
                if (map[0][7] == Model.EXIT_GATE_ID) map[0][7] = Config.CELL_EMPTY;
            }

            double centerX = player.getXCoordinate() + (Config.ENTITY_LOGICAL_HITBOX_WIDTH / 2.0);
            double centerY = player.getYCoordinate() + 0.35;
            int col = (int) Math.floor(centerX);
            int row = (int) Math.floor(centerY);

            if (row >= 0 && row < Config.GRID_HEIGHT && col >= 0 && col < Config.GRID_WIDTH) {
                if (map[row][col] == Model.EXIT_GATE_ID) {
                    levelCompletedFlag = true;
                }
            }
        }
    }

    // ==========================================================
    // boss timer
    // ==========================================================

    /** Decrements the boss preparation timer. Returns true when the timer expires. */
    boolean tickBossPreparation() {
        if (!isPreparationPhase) return false;
        bossPreparationTimer--;

        if (bossPreparationTimer <= 0) {
            isPreparationPhase = false;
            return true; // signal that the global explosion should trigger
        }
        return false;
    }

    // ==========================================================
    // level advancement
    // ==========================================================

    void prepareNextLevel() {
        levelCompletedFlag = false;
        exitGateActive = false;
        portalRevealed = false;
        portalRow = -1;
        portalCol = -1;

        currentZone++;
        if (currentZone > 2) {
            currentZone = 0;
            difficultyCycle++;
        }

        switch (currentZone) {
            case 1  -> currentTheme = "FOREST";
            case 2  -> currentTheme = "CAVE";
            default -> currentTheme = "VILLAGE";
        }

        if (currentZone == 2) {
            bossPreparationTimer = PREP_TIME_SECONDS * Config.FPS;
            isPreparationPhase = true;
            bossPortalActive = false;
        } else {
            isPreparationPhase = false;
            bossPortalActive = false;
        }
    }

    // ==========================================================
    // boss portal — activation / deactivation
    // ==========================================================

    /** Activates the goblin portal on the boss map (called by triggerGlobalExplosion). */
    void activateBossPortal() {
        bossPortalActive = true;
    }

    /**
     * Deactivates the goblin portal. Called when the boss is defeated
     * and no goblins remain alive on the map.
     */
    void deactivateBossPortal() {
        if (!bossPortalActive) return;
        bossPortalActive = false;
    }
}
