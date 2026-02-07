package model;

import utils.Config;
import utils.Direction;

public abstract class Projectile extends Entity {
    protected double x, y;
    protected Direction direction;
    protected double speed;
    protected boolean active; // Se false, il Model lo rimuove
    protected boolean isEnemyProjectile; // Per sapere chi colpire (Player o Nemici)

    public Projectile(double startX, double startY, Direction dir, double speedMult, boolean isEnemy) {
        this.x = startX;
        this.y = startY;
        this.direction = dir;
        this.speed = Config.ENTITY_LOGICAL_SPEED * speedMult;
        this.active = true;
        this.isEnemyProjectile = isEnemy;
    }

    // Template Method: La logica di base è uguale, ma l'impatto cambia
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

        // Delegiamo la gestione della collisione alle sottoclassi
        handleCollision(nextX, nextY);
    }

    // Metodo Astratto: Ogni proiettile decide cosa fare quando tocca qualcosa
    protected abstract void handleCollision(double nextX, double nextY);

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isEnemyProjectile() { return isEnemyProjectile; }
}