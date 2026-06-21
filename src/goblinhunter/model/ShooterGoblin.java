package goblinhunter.model;

import goblinhunter.utils.Config;
import goblinhunter.utils.Direction;
import goblinhunter.utils.EnemyType;

public class ShooterGoblin extends ChasingGoblin {

    private int ammo;
    private int reloadTimer;
    private int telegraphTimer;
    private int attackAnimTimer;
    // telegraphDirection is inherited from Enemy

    private enum State { RELOADING, PATROL_OR_CHASE, TELEGRAPHING, ATTACKING }
    private State state;

    // projectile spawn offset in logical units (1.0 = one cell)
    private final double OFFSET_X_RIGHT =  0.6;
    private final double OFFSET_X_LEFT  = -0.6;
    private final double OFFSET_Y_DOWN  =  0.6;
    private final double OFFSET_Y_UP    = -0.6;

    private final java.util.List<Projectile> activeProjectiles;

    public ShooterGoblin(double startX, double startY) {
        super(startX, startY, Config.GOBLIN_COMMON_SPEED, EnemyType.SHOOTER);
        this.ammo = Config.SHOOTER_MAX_AMMO;
        this.state = State.PATROL_OR_CHASE;
        this.telegraphDirection = null;
        this.reloadTimer = 0;
        this.activeProjectiles = new java.util.ArrayList<>();
    }

    @Override
    public void updateBehavior() {
        double px = Model.getInstance().xCoordinatePlayer();
        double py = Model.getInstance().yCoordinatePlayer();

        switch (state) {
            case RELOADING      -> handleReloadState();
            case TELEGRAPHING   -> handleTelegraphState();
            case ATTACKING      -> handleAttackingState();
            case PATROL_OR_CHASE -> handleNormalState(px, py);
        }
    }

    private void handleNormalState(double px, double py) {
        if (ammo > 0 && hasLineOfSight(px, py)) {
            startTelegraphing(px, py);
        } else {
            this.speed = Config.GOBLIN_COMMON_SPEED;
            super.updateBehavior();
        }
    }

    private void startTelegraphing(double px, double py) {
        state = State.TELEGRAPHING;
        telegraphTimer = Config.SHOOTER_TELEGRAPH_TIME;

        double dx = px - this.x;
        double dy = py - this.y;

        if (Math.abs(dx) > Math.abs(dy))
            telegraphDirection = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
        else
            telegraphDirection = (dy > 0) ? Direction.DOWN : Direction.UP;

        this.currentDirection = telegraphDirection;
        this.speed = 0.0;

        // snap to cell centre before shooting — prevents clipping against wall corners on resume
        this.x = Math.round(this.x);
        this.y = Math.round(this.y);
    }

    private void handleTelegraphState() {
        telegraphTimer--;
        if (telegraphTimer <= 0) {
            startAttacking();
        }
    }

    private void startAttacking() {
        state = State.ATTACKING;
        attackAnimTimer = 10;
    }

    private void handleAttackingState() {
        attackAnimTimer--;
        if (attackAnimTimer <= 0) {
            shoot();

            if (ammo > 0) {
                state = State.PATROL_OR_CHASE;
            } else {
                state = State.RELOADING;
                reloadTimer = Config.SHOOTER_RELOAD_TIME;
            }
        }
    }

    private void shoot() {
        ammo--;

        double projX = this.x;
        double projY = this.y;

        switch (telegraphDirection) {
            case RIGHT -> projX += OFFSET_X_RIGHT;
            case LEFT  -> projX += OFFSET_X_LEFT;
            case DOWN  -> projY += OFFSET_Y_DOWN;
            case UP    -> projY += OFFSET_Y_UP;
        }

        Projectile p = new BoneProjectile(projX, projY, telegraphDirection);
        ((Model) Model.getInstance()).addProjectile(p);
        activeProjectiles.add(p);

        telegraphDirection = null;
    }

    private void handleReloadState() {
        activeProjectiles.removeIf(p -> !p.isActive());

        // freeze while a bone is still in flight — gives the player time to dodge
        if (!activeProjectiles.isEmpty()) {
            this.speed = 0.0;
            return;
        }

        reloadTimer--;
        this.speed = Config.GOBLIN_COMMON_SPEED;
        super.updateBehavior();

        if (reloadTimer <= 0) {
            ammo = Config.SHOOTER_MAX_AMMO;
            state = State.PATROL_OR_CHASE;
        }
    }

    private boolean hasLineOfSight(double px, double py) {
        int cx = (int) (this.x + 0.5);
        int cy = (int) (this.y + 0.5);
        int tx = (int) (px + 0.5);
        int ty = (int) (py + 0.5);

        if (cx != tx && cy != ty) return false;
        return checkPathClear(cx, cy, tx, ty);
    }

    private boolean checkPathClear(int x1, int y1, int x2, int y2) {
        int[][] map = Model.getInstance().getGameAreaArray();
        if (x1 == x2) {
            for (int r = Math.min(y1, y2); r <= Math.max(y1, y2); r++) {
                if (map[r][x1] != Config.CELL_EMPTY) return false;
            }
        } else {
            for (int c = Math.min(x1, x2); c <= Math.max(x1, x2); c++) {
                if (map[y1][c] != Config.CELL_EMPTY) return false;
            }
        }
        return true;
    }

    /**
     * State → animation mapping consumed by the View:
     *   TELEGRAPHING  → "IDLE"   (aiming pause)
     *   ATTACKING     → "ATTACK" (throw animation)
     *   RELOADING + projectile in flight → "IDLE" (watching the bone)
     *   otherwise     → "RUN"
     */
    @Override
    public String getEnemyState() {
        return switch (state) {
            case TELEGRAPHING -> "IDLE";
            case ATTACKING    -> "ATTACK";
            case RELOADING    -> activeProjectiles.isEmpty() ? "RUN" : "IDLE";
            default           -> "RUN";
        };
    }
}
