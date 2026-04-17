package model;

import utils.Config;
import utils.Direction;
import utils.ItemType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Gestisce la generazione procedurale della mappa, la griglia di gioco,
 * il loot nascosto nelle casse e la distruzione dei blocchi.
 */
class MapManager {

    private final java.util.Map<String, ItemType> hiddenLoot = new java.util.HashMap<>();
    private final Random randomGenerator = new Random();
    private final int[][] gameAreaArray;

    // Crepe del Boss: overlay temporaneo (NON modifica gameAreaArray)
    private final List<FloorCrack> activeCracks = new ArrayList<>();

    private final List<CrackWave> activeWaves = new ArrayList<>();

    MapManager() {
        this.gameAreaArray = new int[Config.GRID_HEIGHT][Config.GRID_WIDTH];
    }

    int[][] getGameAreaArray() {
        return gameAreaArray;
    }

    java.util.Map<String, ItemType> getHiddenLoot() {
        return hiddenLoot;
    }

    // ==========================================================
    // GENERAZIONE MAPPA PROCEDURALE
    // ==========================================================

    int[][] generateProceduralMap(int currentZone, LevelManager levelManager) {
        int[][] nextMap = new int[Config.GRID_HEIGHT][Config.GRID_WIDTH];
        List<int[]> emptyCells    = new ArrayList<>();
        List<int[]> cratePositions = new ArrayList<>();

        hiddenLoot.clear();

        int buildingID = (currentZone == 2) ? Config.CELL_SKELETON_START : Config.CELL_ORNAMENT;

        if (currentZone < 2) {
            generateStandardMap(nextMap, emptyCells, cratePositions, buildingID);
        } else {
            generateBossArena(nextMap, emptyCells, buildingID);
        }

        // --- PORTAL RANDOM ---
        int portalRow, portalCol;
        if (!emptyCells.isEmpty()) {
            int[] portalPos = emptyCells.remove(0);
            portalRow = portalPos[0];
            portalCol = portalPos[1];
        } else {
            portalRow = 1;
            portalCol = 1;
        }
        levelManager.setPortal(portalRow, portalCol);

        // --- CASSE CASUALI ---
        int numCrates = 35;
        java.util.Collections.shuffle(emptyCells, randomGenerator);
        for (int i = 0; i < numCrates && i < emptyCells.size(); i++) {
            int[] pos = emptyCells.get(i);
            nextMap[pos[0]][pos[1]] = Config.CELL_DESTRUCTIBLE_BLOCK;
            cratePositions.add(pos);
        }

        // --- LOOT NELLE CASSE ---
        java.util.Collections.shuffle(cratePositions, randomGenerator);
        if (!cratePositions.isEmpty()) {
            for (int i = 0; i < 10 && i < cratePositions.size(); i++) {
                int[] cc = cratePositions.get(i);
                hiddenLoot.put(cc[0] + "," + cc[1], ItemType.AMMO_BOMB);
            }
            for (int i = 10; i < 20 && i < cratePositions.size(); i++) {
                int[] cc = cratePositions.get(i);
                hiddenLoot.put(cc[0] + "," + cc[1], ItemType.AMMO_AURA);
            }
        }

        return nextMap;
    }

    private void generateStandardMap(int[][] map, List<int[]> empty, List<int[]> crates, int buildingID) {
        for (int r = 0; r < Config.GRID_HEIGHT; r++) {
            for (int c = 0; c < Config.GRID_WIDTH; c++) {
                boolean isSafeZone     = (r == 0 && c == 0) || (r == 0 && c == 1) || (r == 1 && c == 0);
                boolean isBunkerWall   = (r == 0 && c == 2) || (r == 2 && c == 0);
                boolean isLeftBuilding  = (r == 0) && (c == 3 || c == 4);
                boolean isRightBuilding = (r == 0) && (c == 8 || c == 9);

                if (isLeftBuilding || isRightBuilding) {
                    map[r][c] = (c == 3 || c == 8) ? buildingID : Config.CELL_INDESTRUCTIBLE_BLOCK;
                } else if (isSafeZone) {
                    map[r][c] = Config.CELL_EMPTY;
                } else if (isBunkerWall) {
                    map[r][c] = Config.CELL_DESTRUCTIBLE_BLOCK;
                    crates.add(new int[]{r, c});
                } else if (r == 1) {
                    if (c == 1 || c == 6 || c == 11) {
                        map[r][c] = Config.CELL_INDESTRUCTIBLE_BLOCK;
                    } else {
                        map[r][c] = Config.CELL_EMPTY;
                        empty.add(new int[]{r, c});
                    }
                } else if (r % 2 != 0 && c % 2 != 0) {
                    map[r][c] = Config.CELL_INDESTRUCTIBLE_BLOCK;
                } else {
                    map[r][c] = Config.CELL_EMPTY;
                    if (!(r == 0 && c == 6)) {
                        empty.add(new int[]{r, c});
                    }
                }
            }
        }
    }

    private void generateBossArena(int[][] map, List<int[]> empty, int buildingID) {
        // Svuota
        for (int r = 0; r < Config.GRID_HEIGHT; r++)
            for (int c = 0; c < Config.GRID_WIDTH; c++)
                map[r][c] = Config.CELL_EMPTY;

        // Edifici decorativi
        for (int r = 0; r < Config.GRID_HEIGHT; r++) {
            for (int c = 0; c < Config.GRID_WIDTH; c++) {
                boolean isLeft  = (r == 0) && (c == 3 || c == 4);
                boolean isRight = (r == 0) && (c == 8 || c == 9);
                if (isLeft || isRight) {
                    map[r][c] = (c == 3 || c == 8) ? buildingID : Config.CELL_INDESTRUCTIBLE_BLOCK;
                }
            }
        }

        // Blocchi dell'arena
        int[][] arenaBlocks = {
            {1,2},{1,4},{1,6},{1,8},{1,10},
            {3,2},{5,2},{7,2},
            {3,10},{5,10},{7,10},
            {9,2},{9,4},{9,6},{9,8},{9,10}
        };
        for (int[] b : arenaBlocks) {
            map[b[0]][b[1]] = Config.CELL_INDESTRUCTIBLE_BLOCK;
        }

        // Celle libere per casse
        for (int r = 0; r < Config.GRID_HEIGHT; r++) {
            for (int c = 0; c < Config.GRID_WIDTH; c++) {
                boolean isSafe = (r == 0 && c == 0) || (r == 0 && c == 1) || (r == 1 && c == 0);
                if (map[r][c] == Config.CELL_EMPTY && !isSafe && !(r == 0 && c == 6)) {
                    empty.add(new int[]{r, c});
                }
            }
        }
    }

    // ==========================================================
    // DISTRUZIONE BLOCCHI
    // ==========================================================

    /**
     * Distrugge un blocco distruttibile alla posizione (row, col).
     * Aggiunge l'effetto di distruzione e il drop dell'oggetto nascosto se presente.
     */
    void destroyBlock(int row, int col, int currentZone, List<Collectible> activeItems,
                      List<BlockDestruction> effects, LevelManager levelManager, ScoreManager scoreManager) {
        if (gameAreaArray[row][col] != Config.CELL_DESTRUCTIBLE_BLOCK) return;

        gameAreaArray[row][col] = Config.CELL_EMPTY;
        scoreManager.addScore(Config.SCORE_CRATE, false, currentZone);

        // Drop loot nascosto
        String key = row + "," + col;
        if (hiddenLoot.containsKey(key)) {
            ItemType dropped = hiddenLoot.remove(key);
            activeItems.add(new Collectible(col, row, dropped));
            System.out.println("Cassa droppa: " + dropped.name());
        }

        // Portale
        levelManager.onBlockDestroyed(row, col);

        effects.add(new BlockDestruction(row, col));
        System.out.println("Cassa distrutta in [" + row + ", " + col + "]");
    }

    /** Distrugge tutte le casse presenti sulla mappa (evento Boss). */
    void destroyAllCrates(List<Collectible> activeItems,
                          List<BlockDestruction> effects) {
        for (int r = 0; r < Config.GRID_HEIGHT; r++) {
            for (int c = 0; c < Config.GRID_WIDTH; c++) {
                if (gameAreaArray[r][c] == Config.CELL_DESTRUCTIBLE_BLOCK) {
                    gameAreaArray[r][c] = Config.CELL_EMPTY;
                    effects.add(new BlockDestruction(r, c));
                }
            }
        }
        activeItems.clear();
        hiddenLoot.clear();
    }

    /** Copia una mappa sorgente nell'array interno. */
    void applyMap(int[][] source) {
        for (int r = 0; r < Config.GRID_HEIGHT; r++) {
            System.arraycopy(source[r], 0, gameAreaArray[r], 0, Config.GRID_WIDTH);
        }
    }

    // ==========================================================
    // LOGICA ONDA GRADUALE (STEP-BY-STEP)
    // ==========================================================

    void spawnCrackWave(int originRow, int originCol, Direction dir, int[][] map) {
        // Invece di creare le crepe subito, lanciamo un'onda che si evolverà nel tempo
        activeWaves.add(new CrackWave(originRow, originCol, dir));
    }

    void updateCracks() {
        // 1. Aggiorna le crepe esistenti (decremento timer)
        Iterator<FloorCrack> it = activeCracks.iterator();
        while (it.hasNext()) {
            if (it.next().tick()) it.remove();
        }

        // 2. Aggiorna le onde in movimento (propagazione)
        Iterator<CrackWave> waveIt = activeWaves.iterator();
        while (waveIt.hasNext()) {
            if (waveIt.next().update(gameAreaArray)) {
                waveIt.remove(); // L'onda ha finito la sua corsa
            }
        }
    }

    /** Classe interna per gestire la propagazione di un singolo attacco. */
    private class CrackWave {
        private final int[][] lanePositions; // [3 corsie][riga, colonna]
        private final boolean[] laneStopped; // corsia bloccata da muro
        private final int fdr, fdc;          // direzione di avanzamento
        private int propagationTimer = 0;
        private static final int PROPAGATION_DELAY = 5; // Velocità: 1 cella ogni 5 tick

        CrackWave(int r, int c, Direction d) {
            int[][] offsets = getLateralOffsets(d);
            this.lanePositions = new int[3][2];
            this.laneStopped = new boolean[3];
            for (int i = 0; i < 3; i++) {
                lanePositions[i][0] = r + offsets[i][0];
                lanePositions[i][1] = c + offsets[i][1];
            }
            int[] front = getFrontDelta(d);
            this.fdr = front[0];
            this.fdc = front[1];
        }

        /** Ritorna true se l'onda deve essere rimossa. */
        boolean update(int[][] map) {
            propagationTimer--;
            if (propagationTimer <= 0) {
                propagationTimer = PROPAGATION_DELAY;
                boolean anyLaneAdvanced = false;

                for (int i = 0; i < 3; i++) {
                    if (laneStopped[i]) continue;

                    int r = lanePositions[i][0];
                    int c = lanePositions[i][1];

                    // Controllo confini
                    if (r < 0 || r >= Config.GRID_HEIGHT || c < 0 || c >= Config.GRID_WIDTH) {
                        laneStopped[i] = true;
                        continue;
                    }

                    // Controllo ostacoli indistruttibili
                    int cell = map[r][c];
                    if (cell == Config.CELL_INDESTRUCTIBLE_BLOCK || cell == Config.CELL_SKELETON_START) {
                        laneStopped[i] = true;
                        continue;
                    }

                    // Piazza la crepa o resetta il timer se esiste già (Overwrite Task 3)
                    FloorCrack existing = getCrackAt(r, c);
                    if (existing != null) {
                        existing.resetTicks(FloorCrack.CRACK_DURATION_TICKS);
                    } else {
                        activeCracks.add(new FloorCrack(r, c));
                    }

                    // Avanza la corsia per il prossimo step
                    lanePositions[i][0] += fdr;
                    lanePositions[i][1] += fdc;
                    anyLaneAdvanced = true;
                }
                return !anyLaneAdvanced; // Se nessuna corsia può più avanzare, l'onda è finita
            }
            return false;
        }
    }

    // ==========================================================
    // CREPE DEL BOSS (FLOOR CRACK WAVE)
    // ==========================================================


    /** Controlla se una cella ha gia' una crepa attiva (booleano). */
    boolean hasCrackAt(int row, int col) {
        for (FloorCrack c : activeCracks) {
            if (c.row == row && c.col == col) return true;
        }
        return false;
    }

    /**
     * Ritorna la FloorCrack esistente nella cella indicata, oppure null
     * se non c'e' nessuna crepa in quella posizione.
     * Usato da spawnCrackWave per resettare il timer invece di duplicare.
     */
    FloorCrack getCrackAt(int row, int col) {
        for (FloorCrack c : activeCracks) {
            if (c.row == row && c.col == col) return c;
        }
        return null;
    }

    /** Rimuove tutte le crepe e le onde (es. al cambio livello o riposo Boss). */
    void clearCracks() {
        activeCracks.clear();
        activeWaves.clear();
    }

    // Getters per l'esposizione alla View tramite Model (pattern uguale ad activeFire)
    List<FloorCrack> getActiveCracks()   { return activeCracks; }
    int getCrackCount()                   { return activeCracks.size(); }
    int getCrackRow(int i)                { return activeCracks.get(i).row; }
    int getCrackCol(int i)                { return activeCracks.get(i).col; }

    // ==========================================================
    // HELPER GEOMETRICI
    // ==========================================================

    /**
     * Ritorna i 3 offset laterali (dx, dy) rispetto all'origine, in funzione
     * della direzione di attacco:
     * - [0]: la corsia centrale (offset 0 rispetto alla direzione laterale)
     * - [1]: la corsia sinistra (−1 sull'asse perpendicolare)
     * - [2]: la corsia destra  (+1 sull'asse perpendicolare)
     */
    private int[][] getLateralOffsets(Direction dir) {
        return switch (dir) {
            // Il Boss guarda UP/DOWN → si muove lungo le RIGHE → offset laterali sono nelle COLONNE
            case UP, DOWN -> new int[][]{ {0, 0}, {0, -1}, {0, 1} };
            // Il Boss guarda LEFT/RIGHT → si muove lungo le COLONNE → offset laterali sono nelle RIGHE
            case LEFT, RIGHT -> new int[][]{ {0, 0}, {-1, 0}, {1, 0} };
        };
    }

    /** Ritorna il delta (dr, dc) della direzione frontale di propagazione. */
    private int[] getFrontDelta(Direction dir) {
        return switch (dir) {
            case UP    -> new int[]{-1,  0};
            case DOWN  -> new int[]{ 1,  0};
            case LEFT  -> new int[]{ 0, -1};
            case RIGHT -> new int[]{ 0,  1};
        };
    }
}
