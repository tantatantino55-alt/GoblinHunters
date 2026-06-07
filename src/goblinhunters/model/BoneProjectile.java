package goblinhunters.model;

import goblinhunters.utils.Config;
import goblinhunters.utils.Direction;

public class BoneProjectile extends Projectile {

    private final double startX;
    private final double startY;
    private final double maxRange = 5.0;

    public BoneProjectile(double startX, double startY, Direction dir) {
        super(startX, startY, dir, Config.BONE_PROJECTILE_SPEED, true);
        this.startX = startX;
        this.startY = startY;
    }

    @Override
    protected void handleCollision(double nextX, double nextY) {
        double distanceTraveled = Math.abs(nextX - startX) + Math.abs(nextY - startY);

        if (distanceTraveled >= maxRange || !Model.getInstance().isWalkable(nextX, nextY)) {
            this.active = false;
        } else {
            this.x = nextX;
            this.y = nextY;
        }
    }
}
