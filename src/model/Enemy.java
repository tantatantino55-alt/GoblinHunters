package model;

import utils.Config;
import utils.Direction;
import utils.EnemyType;

import java.util.Random;

public abstract class Enemy extends Entity {
    protected double x;
    protected double y;
    protected double speed;
    protected Direction currentDirection;
    protected EnemyType type;
    protected Random random;

    protected boolean isChasing = false;
    protected Direction telegraphDirection = null; // Se non null, sta mirando

    public Enemy(double startX, double startY, double speed, EnemyType type) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.random = new Random();
        this.currentDirection = Direction.getRandom();
        this.type = type;
    }

    public abstract void updateBehavior();

// --- DA INSERIRE IN Enemy.java (sovrascrive il metodo precedente) ---

    // In src/model/Enemy.java
    // In src/model/Enemy.java
// src/model/Enemy.java

    protected void moveInDirection() {
        // Logica Lane Centering anche per i nemici
        double currentX = x;
        double currentY = y;

        // Variabili per la nuova posizione
        double nextX = currentX;
        double nextY = currentY;

        boolean aligned = false;

        // --- MOVIMENTO VERTICALE (UP / DOWN) ---
        if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
            double idealX = Math.round(currentX);
            double diffX = currentX - idealX;

            if (Math.abs(diffX) < Config.CENTER_TOLERANCE) {
                // Sono allineato: Snap e calcolo movimento avanti
                this.x = idealX;
                nextX = idealX; // Confermo X centrata

                if (currentDirection == Direction.UP) nextY -= speed;
                else nextY += speed;

                aligned = true;
            } else if (Math.abs(diffX) < Config.MAGNET_TOLERANCE) {
                // Non sono allineato: Correggo X invece di avanzare in Y
                double fix = Config.CORNER_CORRECTION_SPEED; // Usa velocità fissa o this.speed
                if (diffX > 0) this.x -= fix;
                else this.x += fix;

                return; // FINE TURNO: Ho usato il movimento per allinearmi
            }
        }
        // --- MOVIMENTO ORIZZONTALE (LEFT / RIGHT) ---
        else {
            double idealY = Math.round(currentY);
            double diffY = currentY - idealY;

            if (Math.abs(diffY) < Config.CENTER_TOLERANCE) {
                // Sono allineato
                this.y = idealY;
                nextY = idealY; // Confermo Y centrata

                if (currentDirection == Direction.LEFT) nextX -= speed;
                else nextX += speed;

                aligned = true;
            } else if (Math.abs(diffY) < Config.MAGNET_TOLERANCE) {
                // Correzione asse Y
                double fix = Config.CORNER_CORRECTION_SPEED;
                if (diffY > 0) this.y -= fix;
                else this.y += fix;

                return; // FINE TURNO
            }
        }

        // Se sono arrivato qui, significa che sono allineato e sto provando ad avanzare
        if (aligned) {
            IModel model = Model.getInstance();

            // 1. Controllo Muri
            if (!model.isWalkable(nextX, nextY)) {
                handleWallCollision(); // Gestito dalle sottoclassi (cambia direzione o aspetta)
                return;
            }

            // 2. Controllo altri nemici
            if (model.isAreaOccupiedByOtherEnemy(nextX, nextY, this)) {
                if (this.type == EnemyType.COMMON) changeDirection();
                return;
            }

            // Applica movimento
            this.x = nextX;
            this.y = nextY;
        }
    }
    // Controllo rigoroso per l'area nera
    private boolean isInsideMap(double nx, double ny) {
        return nx >= 0 && nx <= (Config.GRID_WIDTH - 1) &&
                ny >= 0 && ny <= (Config.GRID_HEIGHT - 1);
    }

    protected void changeDirection() {
        Direction oldDir = currentDirection;
        while (currentDirection == oldDir) {
            currentDirection = Direction.getRandom();
        }
    }
    // Metodo da sovrascrivere nelle sottoclassi
    protected void handleWallCollision() {
        changeDirection(); // Comportamento di default (casuale) per CommonGoblin
    }
    // Default: nessun telegraph. ShooterGoblin farà l'override.
    public Direction getTelegraphDirection() {
        return null;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public Direction getDirection() { return currentDirection; }
    public EnemyType getType() { return type; }
}