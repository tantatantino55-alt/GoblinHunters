package model;

import utils.Config;
import utils.Direction;

public abstract class Projectile extends Entity {
    // x, y, speed sono ora ereditati da Entity

    protected Direction direction;
    protected boolean active;
    protected boolean isEnemyProjectile;
    protected long creationTime;

    public Projectile(double startX, double startY, Direction dir, double speedMult, boolean isEnemy) {
        super(startX, startY); // delega x, y a Entity
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
            case UP:    nextY -= speed; break;
            case DOWN:  nextY += speed; break;
            case LEFT:  nextX -= speed; break;
            case RIGHT: nextX += speed; break;
        }

        handleCollision(nextX, nextY);
    }

    protected abstract void handleCollision(double nextX, double nextY);

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    // getX() e getY() ereditati da Entity
    public boolean isEnemyProjectile() { return isEnemyProjectile; }
    public Direction getDirection() { return direction; }
}