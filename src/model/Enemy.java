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
// In src/model/Enemy.java - Sostituisci moveInDirection
    // Sostituisci questo metodo in Enemy.java
    protected void moveInDirection() {
        IModel model = Model.getInstance();
        double nextX = x;
        double nextY = y;

        // 1. Calcola la posizione teorica successiva
        switch (currentDirection) {
            case UP ->    nextY -= speed;
            case DOWN ->  nextY += speed;
            case LEFT ->  nextX -= speed;
            case RIGHT -> nextX += speed;
        }

        // 2. CONTROLLO COLLISIONE REALE
        // Usiamo una hitbox leggermente ridotta (0.8) per non incastrarsi negli spigoli visivi
        if (model.isWalkable(nextX, nextY) && !model.isAreaOccupiedByOtherEnemy(nextX, nextY, this)) {
            this.x = nextX;
            this.y = nextY;

            // 3. LANE CENTERING (solo dopo essersi mossi, per mantenere i binari)
            applyLaneCentering();
        } else {
            // Se non può camminare, forza il cambio direzione immediato
            changeDirection();
        }
    }

    private void applyLaneCentering() {
        // Se mi muovo in verticale, correggo dolcemente la X verso il centro del tile
        if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
            double idealX = Math.round(x);
            if (Math.abs(x - idealX) < 0.2) this.x = idealX;
        }
        // Se mi muovo in orizzontale, correggo la Y
        else {
            double idealY = Math.round(y);
            if (Math.abs(y - idealY) < 0.2) this.y = idealY;
        }
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