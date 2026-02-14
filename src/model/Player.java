package model;

import utils.Config;
import utils.Direction;
import utils.PlayerState;

public class Player extends Entity {
    private double xCoordinate;
    private double yCoordinate;
    private double deltaX = 0;
    private double deltaY = 0;
    private double speed;

    private PlayerState currentState;
    private long stateStartTime;

    // --- NUOVI ATTRIBUTI PER GESTIRE IL MOVIMENTO ---
    private Direction currentDirection = Direction.DOWN; // Default
    private boolean isMoving = false;

    // Attributi gameplay
    private int lives;
    private long invincibilityEndTime = 0;
    private int maxBombs = Config.INITIAL_MAX_BOMBS;
    private int bombRadius = Config.DEFAULT_BOMB_RADIUS;

    public Player(double startX, double startY) {
        this.xCoordinate = startX;
        this.yCoordinate = startY;
        this.currentState = PlayerState.IDLE_FRONT;
        this.lives = Config.INITIAL_LIVES;
        this.speed = Config.ENTITY_LOGICAL_SPEED;
    }

    // --- METODI AGGIUNTI PER IL LANE CENTERING ---

    public void setDirection(Direction newDirection) {
        this.currentDirection = newDirection;
        updateState(); // Aggiorna lo sprite subito
    }

    public Direction getDirection() {
        return currentDirection;
    }

    public void setMoving(boolean moving) {
        this.isMoving = moving;
        updateState(); // Aggiorna lo sprite se ci fermiamo o partiamo
    }

    // Questo metodo calcola quale PlayerState usare in base a Direzione + Movimento
    private void updateState() {
        if (isMoving) {
            switch (currentDirection) {
                case UP:    setState(PlayerState.RUN_BACK); break;
                case DOWN:  setState(PlayerState.RUN_FRONT); break;
                case LEFT:  setState(PlayerState.RUN_LEFT); break;
                case RIGHT: setState(PlayerState.RUN_RIGHT); break;
            }
        } else {
            switch (currentDirection) {
                case UP:    setState(PlayerState.IDLE_BACK); break;
                case DOWN:  setState(PlayerState.IDLE_FRONT); break;
                case LEFT:  setState(PlayerState.IDLE_LEFT); break;
                case RIGHT: setState(PlayerState.IDLE_RIGHT); break;
            }
        }
    }

    // --- FINE METODI AGGIUNTI ---

    public void setState(PlayerState newState) {
        // Resetta il timer dell'animazione solo se lo stato cambia davvero
        if (this.currentState != newState) {
            this.currentState = newState;
            this.stateStartTime = System.currentTimeMillis();
        }
    }

    public PlayerState getState() { return currentState; }
    public long getStateStartTime() { return stateStartTime; }

    // ... (Il resto dei getter/setter per coordinate, vite, bombe rimane uguale) ...
    public boolean isInvincible() { return System.currentTimeMillis() < invincibilityEndTime; }
    public void takeDamage() {
        if (!isInvincible() && lives > 0) {
            lives--;
            invincibilityEndTime = System.currentTimeMillis() + Config.INVINCIBILITY_DURATION_MS;
            System.out.println("Player colpito! Vite rimaste: " + lives);
        }
    }
    public int getLives() { return lives; }
    public double getXCoordinate() { return xCoordinate; }
    public double getYCoordinate() { return yCoordinate; }
    public void setXCoordinate(double x) { this.xCoordinate = x; }
    public void setYCoordinate(double y) { this.yCoordinate = y; }
    public void setDelta(double dx, double dy) { this.deltaX = dx; this.deltaY = dy; }
    public double getDeltaX() { return deltaX; }
    public double getDeltaY() { return deltaY; }
    public int getMaxBombs() { return maxBombs; }
    public void setMaxBombs(int maxBombs) { this.maxBombs = maxBombs; }
    public int getBombRadius() { return bombRadius; }
    public void setBombRadius(int radius) { this.bombRadius = radius; }
    public double getSpeed() { return speed; }
}