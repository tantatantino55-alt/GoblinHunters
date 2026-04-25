package model;

import utils.Config;

import java.util.List;

/**
 * Gestisce la progressione dei livelli: zone, gate di uscita, portale,
 * timer boss e transizioni tra mappe.
 */
class LevelManager {

    private final Model model;

    // Progressione
    private int currentZone = 0;
    private int difficultyCycle = 1;
    private String currentTheme = "VILLAGE";

    // Flag transizione
    private boolean exitGateActive = false;
    private boolean levelCompletedFlag = false;
    private boolean isTransitioning = false;
    private long lastExitGateSpawnTime = 0;

    // Portale
    private int portalRow = -1;
    private int portalCol = -1;
    private boolean portalRevealed = false;
    private long lastPortalRevealTime = 0;

    // Exit gate fisso in (0,6)
    private final int exitGateRow = 0;
    private final int exitGateCol = 6;

    // Timer boss
    private int bossPreparationTimer;
    private boolean isPreparationPhase = false;
    private static final int PREP_TIME_SECONDS = 3;

    // Portale goblin nella mappa Boss (posizione fissa da Config)
    private boolean bossPortalActive = false;
    private long bossPortalActivationTime = 0;

    LevelManager(Model model) {
        this.model = model;
    }

    // ==========================================================
    // GETTERS
    // ==========================================================

    int getCurrentZone()         { return currentZone; }
    int getDifficultyCycle()     { return difficultyCycle; }
    String getCurrentTheme()     { return currentTheme; }
    boolean isExitGateActive()   { return exitGateActive; }
    boolean isLevelCompletedFlag(){ return levelCompletedFlag; }
    boolean isTransitioning()    { return isTransitioning; }
    void setTransitioning(boolean t) { isTransitioning = t; }
    int getPortalRow()           { return portalRow; }
    int getPortalCol()           { return portalCol; }
    boolean isPortalRevealed()   { return portalRevealed; }
    long getPortalRevealTime()   { return lastPortalRevealTime; }
    int getExitGateRow()         { return exitGateRow; }
    int getExitGateCol()         { return exitGateCol; }
    long getExitGateActivationTime() { return lastExitGateSpawnTime; }
    boolean isPreparationPhase() { return isPreparationPhase; }
    int getBossPreparationTimer(){ return bossPreparationTimer; }

    // --- PORTALE BOSS ---
    boolean isBossPortalActive()         { return bossPortalActive; }
    int getBossPortalRow()               { return Config.BOSS_PORTAL_ROW; }
    int getBossPortalCol()               { return Config.BOSS_PORTAL_COL; }
    long getBossPortalActivationTime()   { return bossPortalActivationTime; }

    // ==========================================================
    // CONFIGURAZIONE LIVELLO (count, cap, distribuzione)
    // ==========================================================

    /**
     * Numero di nemici da spawnare all'inizio del livello.
     * Zona 0: ciclo 1=4, ciclo 2=5, ciclo 3+=6
     * Zona 1: ciclo 1=5, ciclo 2+=6
     * Zona 2: 0 (il boss viene generato dopo la fase di preparazione)
     */
    int getInitialEnemyCount() {
        int cycle = Math.min(difficultyCycle, 3); // cap a 3
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
            default -> 0; // zona 2 (boss)
        };
    }

    /**
     * Numero massimo di nemici che il portale puo' mantenere in mappa.
     * Stesso valore di getInitialEnemyCount (il portale riempie fino al cap).
     */
    int getPortalMaxEnemies() {
        return getInitialEnemyCount();
    }

    /**
     * Genera un nemico secondo la distribuzione della zona corrente.
     * Zona 0: 75% Common, 25% Chasing
     * Zona 1: 40% Common, 30% Chasing, 30% Shooter
     *
     * @param roll un valore casuale 0-99
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
            default -> new CommonGoblin(x, y); // fallback (non usato per zona 2)
        };
    }

    // ==========================================================
    // PORTALE
    // ==========================================================

    void setPortal(int row, int col) {
        this.portalRow = row;
        this.portalCol = col;
        this.portalRevealed = false;
    }

    /** Chiamato da MapManager quando un blocco viene distrutto. */
    void onBlockDestroyed(int row, int col) {
        if (row == portalRow && col == portalCol) {
            portalRevealed = true;
            lastPortalRevealTime = System.currentTimeMillis();
            model.getSpawnManager().resetPortalTimer();
            System.out.println("ALLARME! Portale scoperto in [" + row + ", " + col + "]!");
        }
    }

    // ==========================================================
    // EXIT GATE
    // ==========================================================

    void checkExitGateCollision(List<Enemy> enemies, Player player, int[][] map) {
        // Durante la preparazione boss non aprire il gate
        if (currentZone == 2 && isPreparationPhase) return;

        // Nella zona Boss: il portale deve essere disattivato prima che appaia l'exit gate
        // (il portale si disattiva quando il Boss e' morto E tutti i goblin sono stati eliminati)
        if (currentZone == 2 && bossPortalActive) return;

        // Conta nemici vivi
        long livingEnemies = enemies.stream().filter(e -> !e.isDead()).count();

        if (livingEnemies == 0) {
            if (!exitGateActive) {
                exitGateActive = true;
                lastExitGateSpawnTime = System.currentTimeMillis();
                map[0][6] = Model.EXIT_GATE_ID;
                if (map[0][0] == Model.EXIT_GATE_ID) map[0][0] = Config.CELL_EMPTY;
                if (map[0][7] == Model.EXIT_GATE_ID) map[0][7] = Config.CELL_EMPTY;
                System.out.println("Tutti i nemici sconfitti! L'Exit Gate e' apparso in [0, 6]!");
            }

            // Controlla se il player ha calpestato il gate
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
    // BOSS TIMER
    // ==========================================================

    /** Decrementa il timer di preparazione boss. Ritorna true se il timer è scaduto. */
    boolean tickBossPreparation() {
        if (!isPreparationPhase) return false;
        bossPreparationTimer--;

        if (bossPreparationTimer % Config.FPS == 0 && bossPreparationTimer > 0) {
            System.out.println("Boss in arrivo tra: " + (bossPreparationTimer / Config.FPS) + "s");
        }

        if (bossPreparationTimer <= 0) {
            isPreparationPhase = false;
            return true; // segnala che l'esplosione deve avvenire
        }
        return false;
    }

    // ==========================================================
    // AVANZAMENTO LIVELLO
    // ==========================================================

    void prepareNextLevel() {
        levelCompletedFlag = false;
        exitGateActive = false;
        portalRevealed = false;

        currentZone++;
        if (currentZone > 2) {
            currentZone = 0;
            difficultyCycle++;
            System.out.println("VITTORIA GLOBALE! Inizio ciclo difficoltà: " + difficultyCycle);
        } else {
            System.out.println("Avanzamento al livello: " + currentZone);
        }

        switch (currentZone) {
            case 1  -> currentTheme = "FOREST";
            case 2  -> currentTheme = "CAVE";
            default -> currentTheme = "VILLAGE";
        }

        if (currentZone == 2) {
            bossPreparationTimer = PREP_TIME_SECONDS * Config.FPS;
            isPreparationPhase = true;
            bossPortalActive = false; // il portale si attivera' a fine preparazione
            System.out.println("Fase preparazione Boss avviata! Hai " + PREP_TIME_SECONDS + " secondi!");
        } else {
            isPreparationPhase = false;
            bossPortalActive = false;
        }
    }

    // ==========================================================
    // PORTALE BOSS – Attivazione/Disattivazione
    // ==========================================================

    /** Attiva il portale goblin nella mappa boss (chiamato da triggerGlobalExplosion). */
    void activateBossPortal() {
        bossPortalActive = true;
        bossPortalActivationTime = System.currentTimeMillis();
        System.out.println("PORTALE BOSS attivato in [" + Config.BOSS_PORTAL_ROW + ", " + Config.BOSS_PORTAL_COL + "]!");
    }

    /**
     * Disattiva il portale goblin. Chiamato quando il Boss e' sconfitto
     * e non ci sono piu' goblin vivi sulla mappa.
     */
    void deactivateBossPortal() {
        if (!bossPortalActive) return;
        bossPortalActive = false;
        System.out.println("PORTALE BOSS chiuso!");
    }
}
