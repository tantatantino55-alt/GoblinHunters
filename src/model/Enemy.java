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

        // SNAP-TO-GRID: Allinea al centro della cella sull'asse opposto al movimento
        // Questo impedisce di sbattere sugli spigoli
        if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
            nextX = Math.round(x);
        } else {
            nextY = Math.round(y);
        }

        switch (currentDirection) {
            case UP:    nextY -= speed; break;
            case DOWN:  nextY += speed; break;
            case LEFT:  nextX -= speed; break;
            case RIGHT: nextX += speed; break;
        }

        IModel model = Model.getInstance();

        // 1. Controllo Muri (usa isWalkable a griglia)
        if (!model.isWalkable(nextX, nextY)) {
            // Se sbatte:
            if (this.type == EnemyType.COMMON) {
                changeDirection(); // I comuni cambiano a caso
            }
            // Gli inseguitori si fermano (l'IA deciderà al prossimo frame)
            return;
        }

        // 2. Controllo Altri Nemici (Evita sovrapposizioni)
        if (model.isAreaOccupiedByOtherEnemy(nextX, nextY, this)) {
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