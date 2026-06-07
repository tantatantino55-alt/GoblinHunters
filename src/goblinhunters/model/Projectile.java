package goblinhunters.model;

import goblinhunters.utils.Config;
import goblinhunters.utils.Direction;

public abstract class Projectile extends Entity {

    protected Direction direction;
    protected boolean active;
    protected boolean isEnemyProjectile;
    protected long creationTime;

    public Projectile(double startX, double startY, Direction dir, double speedMult, boolean isEnemy) {
        super(startX, startY);
        this.direction = dir;
        this.speed = Config.ENTITY_LOGICAL_SPEED * speedMult;
        this.active = true;
        this.isEnemyProjectile = isEnemy;
    }

    public void update() {
        if (!active) return;

        double nextX = x;
        double nextY = y;

        switch (direction) {
            case UP    -> nextY -= speed;
            case DOWN  -> nextY += speed;
            case LEFT  -> nextX -= speed;
            case RIGHT -> nextX += speed;
        }

        handleCollision(nextX, nextY);
    }

    protected abstract void handleCollision(double nextX, double nextY);

    public boolean isActive()                 { return active; }
    public void setActive(boolean active)     { this.active = active; }
    public boolean isEnemyProjectile()        { return isEnemyProjectile; }
    public Direction getDirection()           { return direction; }
}
