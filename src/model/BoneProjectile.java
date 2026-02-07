package model;

import utils.Direction;

public class BoneProjectile extends Projectile {

    public BoneProjectile(double startX, double startY, Direction dir) {
        // Velocità media (es. 3x), è un proiettile nemico (true)
        super(startX, startY, dir, 3.0, true);
    }

    @Override
    protected void handleCollision(double nextX, double nextY) {
        // Se c'è un ostacolo (muro o fuori mappa), il proiettile muore
        if (!Model.getInstance().isWalkable(nextX, nextY)) {
            this.active = false;
        } else {
            // Se è libero, avanza
            this.x = nextX;
            this.y = nextY;
        }
    }
}