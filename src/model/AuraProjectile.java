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
        super(startX, startY, dir, Config.AuraProjectileSpeed, false);

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
        // --- 0. CONTROLLO LIMITI ASSOLUTI (Coordinate fisiche) ---
        if (nextX < 0 || nextX >= Config.GRID_WIDTH || nextY < 0 || nextY >= Config.GRID_HEIGHT) {
            this.active = false;
            return;
        }

        // 1. CONTROLLO RAGGIO D'AZIONE
        double distanceTraveled = Math.abs(nextX - startX) + Math.abs(nextY - startY);
        if (distanceTraveled >= maxRange) {
            this.active = false;
            return;
        }

        // 2. Calcolo indici della griglia
        int col = (int) (nextX + 0.5);
        int row = (int) (nextY + 0.5);

        // --- CONTROLLO DI SICUREZZA ANTI-CRASH (Indici array) ---
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
            // Chiama l'esplosione dei frammenti!
            Model.getInstance().destroyBlock(row, col);
            this.active = false;
            return;
        }

        // CASO C: Libero -> Avanza
        this.x = nextX;
        this.y = nextY;
    }
}