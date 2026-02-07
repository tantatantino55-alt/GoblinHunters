package model;

import utils.Config;
import utils.Direction;

public class AuraProjectile extends Projectile {

    // Memorizziamo le coordinate di origine per calcolare la distanza percorsa
    private final double startX;
    private final double startY;

    // Raggio d'azione attuale (inizialmente 3, modificabile dai power-up)
    private double maxRange;

    public AuraProjectile(double startX, double startY, Direction dir) {
        // Velocità alta (es. 4x), NON è nemico (false)
        super(startX, startY, dir, 4.0, false);

        this.startX = startX;
        this.startY = startY;

        // Requisito: Il raggio iniziale è di 3 caselle
        this.maxRange = 3.0;
    }

    // Metodo utile per il futuro: permette di estendere il raggio (es. a 6) con un power-up
    public void setMaxRange(double newRange) {
        this.maxRange = newRange;
    }

    @Override
    protected void handleCollision(double nextX, double nextY) {
        // 0. CONTROLLO RAGGIO D'AZIONE
        // Calcoliamo la distanza percorsa dal punto di origine
        double distanceTraveled = Math.abs(nextX - startX) + Math.abs(nextY - startY);

        // Se il proiettile ha percorso una distanza pari o superiore al raggio massimo, si dissolve
        if (distanceTraveled >= maxRange) {
            this.active = false;
            return;
        }

        // 1. Controlliamo cosa c'è nella cella di destinazione
        int col = (int) (nextX + 0.5); // Centro della hitbox
        int row = (int) (nextY + 0.5);

        // Controllo limiti mappa
        if (col < 0 || col >= Config.GRID_WIDTH || row < 0 || row >= Config.GRID_HEIGHT) {
            this.active = false;
            return;
        }

        int cellType = Model.getInstance().getGameAreaArray()[row][col];

        // CASO A: Muro Indistruttibile -> Il proiettile si ferma
        if (cellType == Config.CELL_INDESTRUCTIBLE_BLOCK) {
            this.active = false;
            return;
        }

        // CASO B: Muro Distruttibile -> Il proiettile lo rompe e si ferma
        if (cellType == Config.CELL_DESTRUCTIBLE_BLOCK) {
            // Distruggi il muro (imposta a vuoto)
            Model.getInstance().getGameAreaArray()[row][col] = Config.CELL_EMPTY;
            this.active = false; // Il colpo si consuma
            return;
        }

        // CASO C: Libero -> Avanza
        this.x = nextX;
        this.y = nextY;
    }
}