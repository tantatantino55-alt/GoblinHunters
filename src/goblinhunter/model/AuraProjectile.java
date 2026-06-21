package goblinhunter.model;

import goblinhunter.utils.Config;
import goblinhunter.utils.Direction;

public class AuraProjectile extends Projectile {

    private final double startX;
    private final double startY;
    private final double maxRange;

    public AuraProjectile(double startX, double startY, Direction dir) {
        super(startX, startY, dir, Config.AURA_PROJECTILE_SPEED, false);
        this.startX   = startX;
        this.startY   = startY;
        this.maxRange = Config.AURA_PROJECTILE_MAX_RANGE;
    }

    @Override
    protected void handleCollision(double nextX, double nextY) {
        if (nextX < 0 || nextX >= Config.GRID_WIDTH || nextY < 0 || nextY >= Config.GRID_HEIGHT) {
            this.active = false;
            return;
        }

        double distanceTraveled = Math.abs(nextX - startX) + Math.abs(nextY - startY);
        if (distanceTraveled >= maxRange) {
            this.active = false;
            return;
        }

        int col = (int) (nextX + 0.5);
        int row = (int) (nextY + 0.5);

        // secondary bounds check on array indices after rounding
        if (col < 0 || col >= Config.GRID_WIDTH || row < 0 || row >= Config.GRID_HEIGHT) {
            this.active = false;
            return;
        }

        int cellType = Model.getInstance().getGameAreaArray()[row][col];

        if (cellType == Config.CELL_INDESTRUCTIBLE_BLOCK) {
            this.active = false;
            return;
        }

        if (cellType == Config.CELL_DESTRUCTIBLE_BLOCK) {
            Model.getInstance().destroyBlock(row, col);
            this.active = false;
            return;
        }

        this.x = nextX;
        this.y = nextY;
    }
}
