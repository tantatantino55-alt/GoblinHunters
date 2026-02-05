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

    public Enemy(double startX, double startY, double speed, EnemyType type) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.random = new Random();
        this.currentDirection = Direction.getRandom(); // Direzione iniziale casuale
        this.type = type;
    }

    public abstract void updateBehavior();

    // Logica di movimento corretta: cammina dritto finché non sbatte
    protected void moveInDirection() {
        double nextX = x;
        double nextY = y;

        // Calcola la posizione successiva in base alla direzione attuale
        switch (currentDirection) {
            case UP:    nextY -= speed; break;
            case DOWN:  nextY += speed; break;
            case LEFT:  nextX -= speed; break;
            case RIGHT: nextX += speed; break;
        }

        // Verifica collisione con bordi e muri tramite il Model
        if (canMove(nextX, nextY)) {
            this.x = nextX;
            this.y = nextY;
        } else {
            // Se sbatte (canMove è false), cambia direzione
            changeDirection();
        }
    }

    private boolean canMove(double nx, double ny) {
        // Delega al Model il controllo della mappa e dei confini
        return Model.getInstance().isWalkable(nx, ny);
    }

    protected void changeDirection() {
        Direction newDir = currentDirection;
        // Cerca una nuova direzione diversa dalla precedente per evitare di bloccarsi
        while (newDir == currentDirection) {
            newDir = Direction.getRandom();
        }
        currentDirection = newDir;
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public Direction getDirection() { return currentDirection; }
    public EnemyType getType() { return type; }
}