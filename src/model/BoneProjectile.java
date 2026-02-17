package model;

import utils.Direction;

public class BoneProjectile extends Projectile {

    // Salviamo le coordinate iniziali per calcolare la distanza percorsa
    private final double startX;
    private final double startY;

    // Imposta qui la gittata massima in celle logiche (es. 5.0)
    private final double maxRange = 5.0;

    public BoneProjectile(double startX, double startY, Direction dir) {
        // Velocità media (es. 3x), è un proiettile nemico (true)
        super(startX, startY, dir, 3.0, true);
        this.startX = startX;
        this.startY = startY;
    }

    @Override
    protected void handleCollision(double nextX, double nextY) {
        // Calcola quante celle logiche ha attraversato
        double distanceTraveled = Math.abs(nextX - startX) + Math.abs(nextY - startY);

        // Se ha superato la gittata massima o c'è un ostacolo, il proiettile muore
        if (distanceTraveled >= maxRange || !Model.getInstance().isWalkable(nextX, nextY)) {
            this.active = false;
        } else {
            // Se è libero e nel raggio, avanza
            this.x = nextX;
            this.y = nextY;
        }
    }
}

/**
 *------VERSIONE CON GITTATA INFINITA-------
package model;

import utils.Direction;

public class BoneProjectile extends Projectile {

    public BoneProjectile(double startX, double startY, Direction dir) {
        // Velocità media (es. 3x), è un proiettile nemico (true)
        super(startX, startY, dir, 2.0, true);
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
*/