package goblinhunter.model;

import goblinhunter.utils.Config;
import goblinhunter.utils.Direction;
import goblinhunter.utils.ItemType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/** Manages procedural map generation, the game grid, crate loot, and block destruction. */
class MapManager {

    private final java.util.Map<String, ItemType> hiddenLoot = new java.util.HashMap<>();
    private final Random randomGenerator = new Random();
    private final int[][] gameAreaArray;

    // boss floor cracks: overlay only — never modifies gameAreaArray
    private final List<FloorCrack> activeCracks = new ArrayList<>();
    private final List<CrackWave>  activeWaves  = new ArrayList<>();

    MapManager() {
        this.gameAreaArray = new int[Config.GRID_HEIGHT][Config.GRID_WIDTH];
    }

    int[][] getGameAreaArray() { return gameAreaArray; }

    int[][] generateProceduralMap(int currentZone, LevelManager levelManager) {
        int[][] nextMap = new int[Config.GRID_HEIGHT][Config.GRID_WIDTH];
        List<int[]> emptyCells    = new ArrayList<>();
        List<int[]> cratePositions = new ArrayList<>();

        hiddenLoot.clear();

        int buildingID = Config.CELL_ORNAMENT;

        if (currentZone < 2) {
            generateStandardMap(nextMap, emptyCells, cratePositions, buildingID);
        } else {
            generateBossArena(nextMap, emptyCells, buildingID);
        }

        int numCrates = 35;
        java.util.Collections.shuffle(emptyCells, randomGenerator);
        for (int i = 0; i < numCrates && i < emptyCells.size(); i++) {
            int[] pos = emptyCells.get(i);
            nextMap[pos[0]][pos[1]] = Config.CELL_DESTRUCTIBLE_BLOCK;
            cratePositions.add(pos);
        }

        if (currentZone < 2 && !cratePositions.isEmpty()) {
            java.util.Collections.shuffle(cratePositions, randomGenerator);
            int[] portalPos = cratePositions.get(0);
            levelManager.setPortal(portalPos[0], portalPos[1]);
        }

        java.util.Collections.shuffle(cratePositions, randomGenerator);
        if (!cratePositions.isEmpty()) {
            for (int i = 0; i < 12 && i < cratePositions.size(); i++) {
                int[] cc = cratePositions.get(i);
                hiddenLoot.put(cc[0] + "," + cc[1], ItemType.AMMO_BOMB);
            }
            for (int i = 15; i < 20 && i < cratePositions.size(); i++) {
                int[] cc = cratePositions.get(i);
                hiddenLoot.put(cc[0] + "," + cc[1], ItemType.AMMO_AURA);
            }
        }

        return nextMap;
    }

    private void generateStandardMap(int[][] map, List<int[]> empty, List<int[]> crates, int buildingID) {
        for (int r = 0; r < Config.GRID_HEIGHT; r++) {
            for (int c = 0; c < Config.GRID_WIDTH; c++) {
                boolean isSafeZone      = (r == 0 && c == 0) || (r == 0 && c == 1) || (r == 1 && c == 0);
                boolean isBunkerWall    = (r == 0 && c == 2) || (r == 2 && c == 0);
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
        for (int r = 0; r < Config.GRID_HEIGHT; r++)
            for (int c = 0; c < Config.GRID_WIDTH; c++)
                map[r][c] = Config.CELL_EMPTY;

        for (int r = 0; r < Config.GRID_HEIGHT; r++) {
            for (int c = 0; c < Config.GRID_WIDTH; c++) {
                boolean isLeft  = (r == 0) && (c == 3 || c == 4);
                boolean isRight = (r == 0) && (c == 8 || c == 9);
                if (isLeft || isRight) {
                    map[r][c] = (c == 3 || c == 8) ? buildingID : Config.CELL_INDESTRUCTIBLE_BLOCK;
                }
            }
        }

        int[][] arenaBlocks = {
            {1,2},{1,4},{1,6},{1,8},{1,10},
            {3,2},{5,2},{7,2},
            {3,10},{5,10},{7,10},
            {9,2},{9,4},{9,6},{9,8},{9,10}
        };
        for (int[] b : arenaBlocks) {
            map[b[0]][b[1]] = Config.CELL_INDESTRUCTIBLE_BLOCK;
        }

        for (int r = 0; r < Config.GRID_HEIGHT; r++) {
            for (int c = 0; c < Config.GRID_WIDTH; c++) {
                boolean isSafe = (r == 0 && c == 0) || (r == 0 && c == 1) || (r == 1 && c == 0);
                if (map[r][c] == Config.CELL_EMPTY && !isSafe && !(r == 0 && c == 6)) {
                    empty.add(new int[]{r, c});
                }
            }
        }
    }

    void destroyBlock(int row, int col, int currentZone, List<Collectible> activeItems,
                      List<BlockDestruction> effects, LevelManager levelManager, ScoreManager scoreManager) {
        if (gameAreaArray[row][col] != Config.CELL_DESTRUCTIBLE_BLOCK) return;

        gameAreaArray[row][col] = Config.CELL_EMPTY;
        scoreManager.addScore(Config.SCORE_CRATE, false, currentZone);

        String key = row + "," + col;
        if (hiddenLoot.containsKey(key)) {
            ItemType dropped = hiddenLoot.remove(key);
            activeItems.add(new Collectible(col, row, dropped));
        }

        levelManager.onBlockDestroyed(row, col);
        effects.add(new BlockDestruction(row, col));
    }

    void destroyAllCrates(List<Collectible> activeItems, List<BlockDestruction> effects) {
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

    void applyMap(int[][] source) {
        for (int r = 0; r < Config.GRID_HEIGHT; r++) {
            System.arraycopy(source[r], 0, gameAreaArray[r], 0, Config.GRID_WIDTH);
        }
    }

    void spawnCrackWave(int originRow, int originCol, Direction dir, int[][] map) {
        activeWaves.add(new CrackWave(originRow, originCol, dir));
    }

    void updateCracks() {
        Iterator<FloorCrack> it = activeCracks.iterator();
        while (it.hasNext()) {
            if (it.next().tick()) it.remove();
        }

        Iterator<CrackWave> waveIt = activeWaves.iterator();
        while (waveIt.hasNext()) {
            if (waveIt.next().update(gameAreaArray)) waveIt.remove();
        }
    }

    private class CrackWave {
        private final int[][] lanePositions; // [3 lanes][row, col]
        private final boolean[] laneStopped; // true when a lane hits a wall
        private final int fdr, fdc;          // forward advance delta
        private int propagationTimer = 0;
        private static final int PROPAGATION_DELAY = 5; // one cell every 5 ticks

        CrackWave(int r, int c, Direction d) {
            int[][] offsets = getLateralOffsets(d);
            this.lanePositions = new int[3][2];
            this.laneStopped   = new boolean[3];
            for (int i = 0; i < 3; i++) {
                lanePositions[i][0] = r + offsets[i][0];
                lanePositions[i][1] = c + offsets[i][1];
            }
            int[] front = getFrontDelta(d);
            this.fdr = front[0];
            this.fdc = front[1];
        }

        /** Returns true when all lanes are blocked and the wave should be removed. */
        boolean update(int[][] map) {
            propagationTimer--;
            if (propagationTimer <= 0) {
                propagationTimer = PROPAGATION_DELAY;
                boolean anyLaneAdvanced = false;

                for (int i = 0; i < 3; i++) {
                    if (laneStopped[i]) continue;

                    int r = lanePositions[i][0];
                    int c = lanePositions[i][1];

                    if (r < 0 || r >= Config.GRID_HEIGHT || c < 0 || c >= Config.GRID_WIDTH) {
                        laneStopped[i] = true;
                        continue;
                    }

                    int cell = map[r][c];
                    if (cell == Config.CELL_INDESTRUCTIBLE_BLOCK || cell == Config.CELL_ORNAMENT) {
                        laneStopped[i] = true;
                        continue;
                    }

                    FloorCrack existing = getCrackAt(r, c);
                    if (existing != null) {
                        existing.resetTicks(FloorCrack.CRACK_DURATION_TICKS);
                    } else {
                        activeCracks.add(new FloorCrack(r, c));
                    }

                    lanePositions[i][0] += fdr;
                    lanePositions[i][1] += fdc;
                    anyLaneAdvanced = true;
                }
                return !anyLaneAdvanced;
            }
            return false;
        }
    }

    boolean hasCrackAt(int row, int col) {
        for (FloorCrack c : activeCracks) {
            if (c.row == row && c.col == col) return true;
        }
        return false;
    }

    FloorCrack getCrackAt(int row, int col) {
        for (FloorCrack c : activeCracks) {
            if (c.row == row && c.col == col) return c;
        }
        return null;
    }

    void clearCracks() {
        activeCracks.clear();
        activeWaves.clear();
    }

    int getCrackCount()                { return activeCracks.size(); }
    int getCrackRow(int i)             { return activeCracks.get(i).row; }
    int getCrackCol(int i)             { return activeCracks.get(i).col; }

    /**
     * Returns the 3 lateral offsets for a crack wave based on attack direction.
     * Boss moves along rows (UP/DOWN) → lanes offset across columns, and vice versa.
     */
    private int[][] getLateralOffsets(Direction dir) {
        return switch (dir) {
            case UP, DOWN    -> new int[][]{ {0, 0}, {0, -1}, {0, 1} };
            case LEFT, RIGHT -> new int[][]{ {0, 0}, {-1, 0}, {1, 0} };
        };
    }

    private int[] getFrontDelta(Direction dir) {
        return switch (dir) {
            case UP    -> new int[]{-1,  0};
            case DOWN  -> new int[]{ 1,  0};
            case LEFT  -> new int[]{ 0, -1};
            case RIGHT -> new int[]{ 0,  1};
        };
    }
}
