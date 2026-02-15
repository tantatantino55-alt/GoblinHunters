package model;

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
// In Enemy.java

// In src/model/Enemy.java

    protected void moveInDirection() {
        Model model = (Model) Model.getInstance();

        // EFFETTO BINARIO: Forza la posizione sulla corsia centrale
        if (currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT) {
            this.y = Math.round(this.y); // Aggancio magnetico alla riga
        } else {
            this.x = Math.round(this.x); // Aggancio magnetico alla colonna
        }

        double nextX = x;
        double nextY = y;

        switch (currentDirection) {
            case UP ->    nextY -= speed;
            case DOWN ->  nextY += speed;
            case LEFT ->  nextX -= speed;
            case RIGHT -> nextX += speed;
        }

        // Usiamo il nuovo metodo con la cessione del passo
        if (model.isWalkableForGoblin(nextX, nextY, this)) {
            this.x = nextX;
            this.y = nextY;
        } else {
            // Se deve cedere il passo o c'è un muro, si ferma e resetta la posizione
            this.x = Math.round(this.x);
            this.y = Math.round(this.y);
            handleWallCollision();
        }
    }

    // Modifica handleWallCollision per essere meno "isterico"
    protected void handleWallCollision() {
        // Quando sbatte, forza la posizione al centro della cella attuale
        this.x = Math.round(this.x);
        this.y = Math.round(this.y);
        changeDirection();
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