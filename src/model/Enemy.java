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
// In src/model/Enemy.java - Sostituisci moveInDirection
    protected void moveInDirection() {
        double currentX = x;
        double currentY = y;
        double nextX = x;
        double nextY = y;

        // 1. CALCOLO POSIZIONE TEORICA
        switch (currentDirection) {
            case UP -> nextY -= speed;
            case DOWN -> nextY += speed;
            case LEFT -> nextX -= speed;
            case RIGHT -> nextX += speed;
        }

        // 2. CORREZIONE BINARI (Lane Centering)
        // Se mi muovo in verticale, mi allineo al centro della colonna X
        if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
            double idealX = Math.round(currentX);
            double diffX = currentX - idealX;
            if (Math.abs(diffX) < Config.MAGNET_TOLERANCE) {
                this.x = idealX; // Snap al centro
                nextX = idealX;
            }
        } else { // Se mi muovo in orizzontale, mi allineo al centro della riga Y
            double idealY = Math.round(currentY);
            double diffY = currentY - idealY;
            if (Math.abs(diffY) < Config.MAGNET_TOLERANCE) {
                this.y = idealY; // Snap al centro
                nextY = idealY;
            }
        }

        // 3. CONTROLLO COLLISIONI (Muri e Altri Nemici)
        IModel model = Model.getInstance();
        if (model.isWalkable(nextX, nextY) && !model.isAreaOccupiedByOtherEnemy(nextX, nextY, this)) {
            this.x = nextX;
            this.y = nextY;
        } else {
            handleWallCollision(); // Cambia direzione se bloccato
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