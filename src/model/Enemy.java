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

    protected void moveInDirection() {
        double nextX = x;
        double nextY = y;

        // Calcolo coordinate future
        switch (currentDirection) {
            case UP:    nextY -= speed; break;
            case DOWN:  nextY += speed; break;
            case LEFT:  nextX -= speed; break;
            case RIGHT: nextX += speed; break;
        }

        IModel model = Model.getInstance();

        // 1. PRIMO CONTROLLO: MURI E BLOCCHI (Indistruttibili E Distruttibili)
        // Se isWalkable ritorna false, c'è un muro: STOP immediato.
        if (!model.isWalkable(nextX, nextY)) {
            changeDirection();
            return;
        }

        // 2. SECONDO CONTROLLO: CONFINI MAPPA
        // Evita che escano dallo schermo (area nera)
        if (!isInsideMap(nextX, nextY)) {
            changeDirection();
            return;
        }

        // 3. TERZO CONTROLLO: COLLISIONE TRA NEMICI
        // Se la cella è libera da muri ma c'è un altro goblin, STOP.
        if (model.isAreaOccupiedByOtherEnemy(nextX, nextY, this)) {
            // Opzionale: Se sbatte contro un amico, può provare a cambiare direzione subito
            changeDirection();
            return;
        }

        // SE TUTTI I CONTROLLI PASSANO: Aggiorna la posizione
        this.x = nextX;
        this.y = nextY;
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
    // Default: nessun telegraph. ShooterGoblin farà l'override.
    public Direction getTelegraphDirection() {
        return null;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public Direction getDirection() { return currentDirection; }
    public EnemyType getType() { return type; }
}