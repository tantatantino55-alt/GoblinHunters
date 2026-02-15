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

    protected Direction telegraphDirection = null;

    public Enemy(double startX, double startY, double speed, EnemyType type) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.random = new Random();
        this.currentDirection = Direction.getRandom();
        this.type = type;
    }

    public abstract void updateBehavior();

    /**
     * Verifica se il nemico è esattamente al centro della cella.
     * Serve per decidere quando cambiare direzione.
     */
    protected boolean isAtCellCenter() {
        double diffX = Math.abs(x - Math.round(x));
        double diffY = Math.abs(y - Math.round(y));
        return diffX < 0.01 && diffY < 0.01;
    }

    /**
     * Movimento Bomberman-style:
     * 1. Allinea l'asse opposto al movimento
     * 2. Prova a muoversi nella direzione corrente
     * 3. Se bloccato, si ferma al centro (NON cambia direzione qui)
     *
     * @return
     */
    protected boolean moveInDirection() {
        // STEP 1: Allineamento magnetico all'asse opposto
        if (currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT) {
            this.y = Math.round(this.y); // Movimento orizzontale -> allinea Y
        } else {
            this.x = Math.round(this.x); // Movimento verticale -> allinea X
        }

        // STEP 2: Calcola posizione futura
        double nextX = x;
        double nextY = y;

        switch (currentDirection) {
            case UP -> nextY -= speed;
            case DOWN -> nextY += speed;
            case LEFT -> nextX -= speed;
            case RIGHT -> nextX += speed;
        }

        // STEP 3: Arrotonda per il controllo logico
        int logX = (int) Math.round(nextX);
        int logY = (int) Math.round(nextY);

        // STEP 4: Controlla se puoi andare lì
        if (Model.getInstance().isWalkableForGoblin(logX, logY, this)) {
            // Sì -> Muoviti
            this.x = nextX;
            this.y = nextY;
        } else {
            // No -> Torna al centro della cella attuale e BASTA
            // NON CAMBIARE DIREZIONE QUI! È responsabilità di updateBehavior()
            this.x = Math.round(this.x);
            this.y = Math.round(this.y);
        }
        return false;
    }

    /**
     * Cambia direzione scegliendo una nuova casualmente diversa da quella attuale.
     * Chiamato SOLO quando il nemico è al centro della cella e lo decide updateBehavior().
     */
    protected void changeDirection() {
        Direction oldDir = currentDirection;
        while (currentDirection == oldDir) {
            currentDirection = Direction.getRandom();
        }
    }

    /**
     * Prova a muoversi in una direzione specifica.
     * Usato da ChasingGoblin per il movimento intelligente.
     */
    protected boolean canMoveInDirection(Direction d) {
        double nx = x;
        double ny = y;
        double step = 1.0; // Una cella intera

        switch (d) {
            case UP -> ny -= step;
            case DOWN -> ny += step;
            case LEFT -> nx -= step;
            case RIGHT -> nx += step;
        }

        // Arrotonda per il controllo logico
        int logX = (int) Math.round(nx);
        int logY = (int) Math.round(ny);

        return Model.getInstance().isWalkableForGoblin(logX, logY, this);
    }

    // Default: nessun telegraph. ShooterGoblin fa l'override.
    public Direction getTelegraphDirection() {
        return null;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public Direction getDirection() { return currentDirection; }
    public EnemyType getType() { return type; }
}