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

    public Enemy(double startX, double startY, double speed, EnemyType type) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.random = new Random();
        this.currentDirection = Direction.getRandom();
        this.type = type;
    }

    public abstract void updateBehavior();

    protected void moveInDirection() {
        double nextX = x;
        double nextY = y;

        switch (currentDirection) {
            case UP:    nextY -= speed; break;
            case DOWN:  nextY += speed; break;
            case LEFT:  nextX -= speed; break;
            case RIGHT: nextX += speed; break;
        }

        // 1. Controllo isWalkable (Muri e Confini Matrice)
        // 2. Controllo Range (Sicurezza extra per l'area nera)
        if (Model.getInstance().isWalkable(nextX, nextY) && isInsideMap(nextX, nextY)) {
            this.x = nextX;
            this.y = nextY;
        } else {
            // Se sbatte in alto (o altrove), cambia direzione
            changeDirection();
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

    public double getX() { return x; }
    public double getY() { return y; }
    public Direction getDirection() { return currentDirection; }
    public EnemyType getType() { return type; }
}