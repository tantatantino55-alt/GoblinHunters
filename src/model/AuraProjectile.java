package model;

import utils.Config;
import utils.Direction;

public class AuraProjectile extends Projectile {

    public AuraProjectile(double startX, double startY, Direction dir) {
        // Velocità alta (es. 4x), NON è nemico (false)
        super(startX, startY, dir, 4.0, false);
    }

    @Override
    protected void handleCollision(double nextX, double nextY) {
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