package model;

import utils.Config;

import java.util.List;

/**
 * Gestisce tutte le verifiche di collisione:
 * walkability della mappa, hitbox delle entità, bombe.
 */
class CollisionManager {

    CollisionManager(Model model) {
        // Il model è il mediatore, ma CollisionManager riceve i dati come parametri
        // per mantenere la dipendenza esplicita e testabile.
        // Il costruttore accetta Model per coerenza col pattern Facade.
    }

    // ==========================================================
    // WALKABILITY
    // ==========================================================

    boolean isWalkable(double nextX, double nextY, int[][] map, List<Bomb> activeBombs, Player player, List<Enemy> enemies) {
        double hbW = Config.ENTITY_LOGICAL_HITBOX_WIDTH;
        double hbH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;

        double left   = nextX + (1.0 - hbW) / 2.0;
        double right  = left + hbW - 0.01;
        double yOffset = 0.4;
        double bottom = nextY + 1.0 - yOffset;
        double top    = bottom - hbH;

        int startCol = (int) Math.floor(left);
        int endCol   = (int) Math.floor(right);
        int startRow = (int) Math.floor(top);
        int endRow   = (int) Math.floor(bottom);

        if (startCol < 0 || endCol >= Config.GRID_WIDTH || startRow < 0 || endRow >= Config.GRID_HEIGHT)
            return false;

        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {
                int cell = map[r][c];
                if (cell == Config.CELL_INDESTRUCTIBLE_BLOCK ||
                    cell == Config.CELL_DESTRUCTIBLE_BLOCK   ||
                    cell == Config.CELL_ORNAMENT             ||
                    cell == Config.CELL_SKELETON_START) {
                    return false;
                }
                Bomb bomb = getBombAt(r, c, activeBombs);
                if (bomb != null && !isPlayerCurrentlyInside(r, c, player) && !isAnyEnemyCurrentlyInside(r, c, enemies)) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean isCellBlocked(int r, int c, int[][] map, List<Bomb> activeBombs, Player player) {
        if (r < 0 || r >= Config.GRID_HEIGHT || c < 0 || c >= Config.GRID_WIDTH) return true;

        int cell = map[r][c];
        if (cell == Config.CELL_INDESTRUCTIBLE_BLOCK ||
            cell == Config.CELL_DESTRUCTIBLE_BLOCK   ||
            cell == Config.CELL_ORNAMENT             ||
            cell == Config.CELL_SKELETON_START) {
            return true;
        }

        Bomb bomb = getBombAt(r, c, activeBombs);
        return bomb != null && !isPlayerCurrentlyInside(r, c, player);
    }

    // ==========================================================
    // OCCUPAZIONE AREA
    // ==========================================================

    boolean isOccupiedByEnemies(double nextX, double nextY, List<Enemy> enemies) {
        double margin = 0.15;
        double pHW = Config.ENTITY_LOGICAL_HITBOX_WIDTH;
        double pHH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;

        for (Enemy e : enemies) {
            double eX = e.getX();
            double eY = e.getY();

            boolean overlapX = (nextX + margin) < (eX + Config.GOBLIN_HITBOX_WIDTH - margin) &&
                               (nextX + pHW - margin) > (eX + margin);
            boolean overlapY = (nextY + margin) < (eY + Config.GOBLIN_HITBOX_HEIGHT - margin) &&
                               (nextY + pHH - margin) > (eY + margin);

            if (overlapX && overlapY) return true;
        }
        return false;
    }

    boolean isAreaOccupiedByOtherEnemy(double nextX, double nextY, Enemy self, List<Enemy> enemies) {
        double size = 0.55;

        for (Enemy other : enemies) {
            if (other == self) continue;

            double nextDistX = Math.abs(nextX - other.getX());
            double nextDistY = Math.abs(nextY - other.getY());

            if (nextDistX < size && nextDistY < size) {
                double currentDistX = Math.abs(self.getX() - other.getX());
                double currentDistY = Math.abs(self.getY() - other.getY());

                if (nextDistX < currentDistX || nextDistY < currentDistY) {
                    return true;
                }
            }
        }
        return false;
    }

    // ==========================================================
    // HELPER
    // ==========================================================

    private boolean isPlayerCurrentlyInside(int r, int c, Player player) {
        double pX  = player.getXCoordinate();
        double pY  = player.getYCoordinate();
        double hbW = Config.ENTITY_LOGICAL_HITBOX_WIDTH;
        double hbH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;

        double pLeft   = pX + (1.0 - hbW) / 2.0;
        double pRight  = pLeft + hbW;
        double pBottom = pY + 1.0 - 0.4;
        double pTop    = pBottom - hbH;

        return pRight > c && pLeft < (c + 1.0) && pBottom > r && pTop < (r + 1.0);
    }

    private boolean isAnyEnemyCurrentlyInside(int r, int c, List<Enemy> enemies) {
        double hbW = Config.ENTITY_LOGICAL_HITBOX_WIDTH;
        double hbH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;

        for (Enemy e : enemies) {
            double eLeft   = e.getX() + (1.0 - hbW) / 2.0;
            double eRight  = eLeft + hbW;
            double eBottom = e.getY() + 1.0 - 0.4;
            double eTop    = eBottom - hbH;

            if (eRight > c && eLeft < (c + 1.0) && eBottom > r && eTop < (r + 1.0)) return true;
        }
        return false;
    }

    Bomb getBombAt(int r, int c, List<Bomb> activeBombs) {
        for (Bomb b : activeBombs) {
            if (b.getRow() == r && b.getCol() == c) return b;
        }
        return null;
    }
}
