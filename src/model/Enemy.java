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
    protected void moveInDirection() {
        double nextX = x;
        double nextY = y;

        switch (currentDirection) {
            case UP:    nextY -= speed; break;
            case DOWN:  nextY += speed; break;
            case LEFT:  nextX -= speed; break;
            case RIGHT: nextX += speed; break;
        }

        // 1. Usa la stessa logica di collisione del Player
        if (!Model.getInstance().isWalkable(nextX, nextY)) {
            // Solo il Goblin BASE (Common) cambia direzione a caso se sbatte
            if (this.type == EnemyType.COMMON) {
                changeDirection();
            }
            // I cacciatori (Hunter/Shooter) si fermano e lasciano che l'IA
            // scelga una nuova via nel prossimo frame
            return;
        }

        // 2. Collisione tra nemici per evitare sovrapposizioni
        if (Model.getInstance().isAreaOccupiedByOtherEnemy(nextX, nextY, this)) {
            if (this.type == EnemyType.COMMON) changeDirection();
            return;
        }

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