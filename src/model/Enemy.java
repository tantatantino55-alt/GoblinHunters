package model;

import utils.Direction;
import utils.EnemyType;

import java.util.Random;

public abstract class Enemy extends Entity {
    protected double x;
    protected double y;
    protected double speed;
    protected Direction currentDirection; // Usa l'Enum, non un int
    protected EnemyType type;
    protected Random random;

    // Costruttore che forza l'uso di parametri chiari
    public Enemy(double startX, double startY, double speed,EnemyType type) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.random = new Random();
        this.currentDirection = Direction.getRandom(); // Direzione iniziale type-safe
        this.type = type;
    }

    // Ogni nemico deve implementare il proprio comportamento
    public abstract void updateBehavior();

    // Logica di movimento standard (Random Walk con collisione)
    protected void moveInDirection() {
        double nextX = x;
        double nextY = y;

        // Switch sull'Enum (molto più leggibile)
        switch (currentDirection) {
            case UP:    nextY -= speed; break;
            case DOWN:  nextY += speed; break;
            case LEFT:  nextX -= speed; break;
            case RIGHT: nextX += speed; break;
        }

        // Verifica collisione usando il Model
        if (canMove(nextX, nextY)) {
            this.x = nextX;
            this.y = nextY;
        } else {
            changeDirection();
        }
    }

    // Metodo helper privato per pulizia codice
    private boolean canMove(double nx, double ny) {
        // Delega al Model la verifica fisica della mappa
        return Model.getInstance().isWalkable(nx, ny);
    }

    protected void changeDirection() {
        Direction newDir = currentDirection;
        // Cerca una nuova direzione diversa dalla precedente
        while (newDir == currentDirection) {
            newDir = Direction.getRandom();
        }
        currentDirection = newDir;
    }
    public EnemyType getType() {
        return type;
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public Direction getDirection() { return currentDirection; }
}