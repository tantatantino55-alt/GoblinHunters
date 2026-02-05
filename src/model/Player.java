package model;

import utils.Config;
import utils.PlayerState;

public class Player extends Entity {
    private double xCoordinate;
    private double yCoordinate;
    private double deltaX = 0;
    private double deltaY = 0;

    // STATO LOGICO (Sostituisce le stringhe e i numeri di frame)
    private PlayerState currentState;
    private long stateStartTime;

    private int maxBombs = Config.INITIAL_MAX_BOMBS;
    private int bombRadius = Config.DEFAULT_BOMB_RADIUS;

    public Player(double startX, double startY) {
        this.xCoordinate = startX;
        this.yCoordinate = startY;

        this.currentState = PlayerState.IDLE_FRONT;
    }
    // --- GESTIONE STATO (Sostituisce setAction) ---

    public void setState(PlayerState newState) {
        this.currentState = newState;
    }

    public PlayerState getState() {
        return currentState;
    }
    // NUOVO GETTER: Serve alla View per calcolare i frame
    public long getStateStartTime() {
        return stateStartTime;
    }

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
}