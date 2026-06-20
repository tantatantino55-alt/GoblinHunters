package goblinhunters.model;

import goblinhunters.utils.Config;
import goblinhunters.utils.EnemyType;

import java.util.ArrayList;
import java.util.List;

class BombManager {

    private final Model model;
    private final CollisionManager collisionManager;
    private final ScoreManager scoreManager;

    BombManager(Model model, CollisionManager collisionManager, ScoreManager scoreManager) {
        this.model = model;
        this.collisionManager = collisionManager;
        this.scoreManager = scoreManager;
    }

    void updateBombs() {
        List<Bomb> activeBombs = model.getActiveBombs();
        List<Bomb> exploded = new ArrayList<>();
        for (Bomb b : activeBombs) {
            b.updateDetonationTimer();
            if (b.isExploded()) { handleExplosion(b); exploded.add(b); }
        }
        activeBombs.removeAll(exploded);
    }

    // Decrements fire duration and checks explosion damage; removes expired tiles.
    void tickFire() {
        List<int[]> activeFire = model.getActiveFire();
        for (int[] f : activeFire) {
            f[3]--;
            if (f[3] > 0) checkExplosionDamage(f[0], f[1]);
        }
        activeFire.removeIf(f -> f[3] <= 0);
    }

    private void handleExplosion(Bomb b) {
        List<int[]> activeFire = model.getActiveFire();
        List<Bomb> activeBombs = model.getActiveBombs();
        int r = b.getRow(), c = b.getCol(), rad = b.getRadius();
        activeFire.add(new int[]{r, c, 0, Config.FIRE_DURATION_TICKS});
        checkExplosionDamage(r, c);

        Bomb chain = collisionManager.getBombAt(r, c, activeBombs);
        if (chain != null && !chain.isExploded()) chain.detonate();

        expandFire(r, c, -1,  0, rad, 4, 8);
        expandFire(r, c,  1,  0, rad, 5, 1);
        expandFire(r, c,  0, -1, rad, 2, 6);
        expandFire(r, c,  0,  1, rad, 3, 7);
    }

    private void expandFire(int sr, int sc, int dr, int dc, int rad, int midType, int tipType) {
        int[][] map = model.getMapManager().getGameAreaArray();
        List<int[]> activeFire = model.getActiveFire();
        List<Bomb> activeBombs = model.getActiveBombs();

        for (int i = 1; i <= rad; i++) {
            int cr = sr + dr * i, cc = sc + dc * i;
            if (cr < 0 || cr >= Config.GRID_HEIGHT || cc < 0 || cc >= Config.GRID_WIDTH) break;

            int cell = map[cr][cc];
            if (cell == Config.CELL_INDESTRUCTIBLE_BLOCK || cell == Config.CELL_ORNAMENT) break;
            if (cell == Config.CELL_DESTRUCTIBLE_BLOCK) { model.destroyBlock(cr, cc); break; }

            Bomb chainBomb = collisionManager.getBombAt(cr, cc, activeBombs);
            if (chainBomb != null && !chainBomb.isExploded()) {
                chainBomb.detonate();
                break; // fire hits this bomb and chain-triggers it; does not continue past it
            }

            boolean tip = (i == rad);
            activeFire.add(new int[]{cr, cc, tip ? tipType : midType, Config.FIRE_DURATION_TICKS});
            checkExplosionDamage(cr, cc);
        }
    }

    private void checkExplosionDamage(int row, int col) {
        double expL = col, expR = col + 1.0, expT = row, expB = row + 1.0;

        List<Enemy> enemies = model.getEnemies();
        LevelManager levelManager = model.getLevelManager();
        List<Collectible> activeItems = model.getActiveItemsList();
        Player player = model.getPlayer();

        List<Enemy> killed = new ArrayList<>();
        for (Enemy e : enemies) {
            if (e.isDead()) continue;

            double eW = Config.ENTITY_LOGICAL_HITBOX_WIDTH, eH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;
            double eL = e.getX() + (1.0 - eW) / 2.0, eR = eL + eW;
            double eB = e.getY() + 1.0 - 0.4,         eT = eB - eH;
            double m = 0.1;

            if ((eL + m) < expR && (eR - m) > expL && (eT + m) < expB && (eB - m) > expT) {
                boolean fatal = e.takeDamage(1);
                if (fatal) {
                    scoreManager.handleEnemyDeath(e, levelManager.getCurrentZone(), activeItems);
                    if (e.getType() != EnemyType.BOSS) killed.add(e);
                }
            }
        }
        enemies.removeAll(killed);

        if (!player.isInvincible()) {
            double pX = player.getXCoordinate(), pY = player.getYCoordinate();
            double pW = Config.ENTITY_LOGICAL_HITBOX_WIDTH, pH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;
            double pL = pX + (1.0 - pW) / 2.0, pR = pL + pW;
            double pB = pY + 1.0 - 0.4,         pT = pB - pH;
            double m = 0.1;
            if ((pL + m) < expR && (pR - m) > expL && (pT + m) < expB && (pB - m) > expT) {
                model.handlePlayerHit();
            }
        }
    }
}
