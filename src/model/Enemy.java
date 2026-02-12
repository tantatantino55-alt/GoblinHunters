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

        // 1. Calcolo futuro basato sulla velocità (Configurata nel Model)
        switch (currentDirection) {
            case UP:    nextY -= speed; break;
            case DOWN:  nextY += speed; break;
            case LEFT:  nextX -= speed; break;
            case RIGHT: nextX += speed; break;
        }

        IModel model = Model.getInstance();

        // 2. CONTROLLO COLLISIONI (Muri e Mappa)
        // Usiamo un piccolo offset (margine) per non farli camminare "dentro" i muri
        double margin = 0.2;
        if (!model.isWalkable(nextX + margin, nextY + margin) ||
                !model.isWalkable(nextX + 1 - margin, nextY + 1 - margin)) {
            changeDirection(); // Se sbatte, cambia strada
            return;
        }

        // 3. COLLISIONE CON ALTRI NEMICI
        // Questo evita che si ammassino tutti in un unico punto rosa/sdoppiato
        if (model.isAreaOccupiedByOtherEnemy(nextX, nextY, this)) {
            // Se la strada è bloccata da un compagno, rallenta o cambia direzione
            return;
        }

        // 4. AGGIORNAMENTO POSIZIONE
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