package model;

import utils.Config;
import utils.Direction;
import utils.PlayerState;

public class Player extends Entity {
    // x, y, speed sono ora ereditati da Entity

    private double deltaX = 0;
    private double deltaY = 0;

    private PlayerState currentState;
    private long stateStartTime;

    // --- ATTRIBUTI PER IL MOVIMENTO ---
    private Direction currentDirection = Direction.DOWN;
    private boolean isMoving = false;

    // Attributi gameplay
    private int lives;
    private long invincibilityEndTime = 0;
    private int maxBombs = Config.INITIAL_MAX_BOMBS;
    private int bombRadius = Config.DEFAULT_BOMB_RADIUS;

    // --- ATTRIBUTI PER L'ATTACCO (Aura) ---
    private boolean isCasting = false;
    private int castTimer = 0;
    private long lastCastTime = 0;
    private final long CAST_COOLDOWN_MS = 800;

    // --- RISORSE E POWER-UP ---
    private int bombAmmo = 5;
    private int auraAmmo = 10;
    private boolean hasShield = false;
    private boolean hasMaxRadius = false;
    private boolean hasMaxSpeed = false;

    // Traccia se ha preso danni nel livello corrente per il Perfect Bonus
    private boolean perfectLevel = true;

    public Player(double startX, double startY) {
        super(startX, startY); // delega x, y a Entity
        this.currentState = PlayerState.IDLE_FRONT;
        this.lives = Config.INITIAL_LIVES;
        this.speed = Config.ENTITY_LOGICAL_SPEED;
    }

    // --- METODI DIREZIONE ---

    public void setDirection(Direction newDirection) {
        this.currentDirection = newDirection;
    }

    public Direction getDirection() {
        return currentDirection;
    }

    public void setMoving(boolean moving) {
        this.isMoving = moving;
        updateState();
    }

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

    // --- STATO ---

    public void setState(PlayerState newState) {
        if (this.currentState != newState) {
            this.currentState = newState;
            this.stateStartTime = System.currentTimeMillis();
        }
    }

    public PlayerState getState() { return currentState; }
    public long getStateStartTime() { return stateStartTime; }

    // --- DANNO / VITA ---

    public boolean isInvincible() { return System.currentTimeMillis() < invincibilityEndTime; }

    public boolean takeDamage() {
        if (isInvincible() || lives <= 0) return false;

        this.perfectLevel = false;

        if (hasShield) {
            hasShield = false;
            invincibilityEndTime = System.currentTimeMillis() + 2000;
            System.out.println("Scudo distrutto! Nessuna vita persa.");
            return false;
        }

        lives--;
        invincibilityEndTime = System.currentTimeMillis() + 2000;
        setState(PlayerState.HURT_FRONT);
        System.out.println("Player colpito! Vite rimaste: " + lives);
        return true;
    }

    // --- COORDINATE (delegano a x/y da Entity, mantengono i nomi originali) ---

    public int getLives() { return lives; }
    public double getXCoordinate() { return x; }
    public double getYCoordinate() { return y; }
    public void setXCoordinate(double newX) { this.x = newX; }
    public void setYCoordinate(double newY) { this.y = newY; }
    public void setDelta(double dx, double dy) { this.deltaX = dx; this.deltaY = dy; }
    public double getDeltaX() { return deltaX; }
    public double getDeltaY() { return deltaY; }
    public int getMaxBombs() { return maxBombs; }
    public void setMaxBombs(int maxBombs) { this.maxBombs = maxBombs; }
    public int getBombRadius() { return bombRadius; }
    public void setBombRadius(int radius) { this.bombRadius = radius; }
    public double getSpeed() { return speed; }

    // --- CASTING (Aura) ---

    public boolean canCast() {
        return System.currentTimeMillis() - lastCastTime > CAST_COOLDOWN_MS;
    }

    public void startCast() {
        this.isCasting = true;
        this.lastCastTime = System.currentTimeMillis();
        this.castTimer = 10;
        this.isMoving = false;
    }

    public boolean isCasting() { return isCasting; }

    public void decrementCastTimer() {
        if (castTimer > 0) castTimer--;
    }

    public int getCastTimer() { return castTimer; }

    public void finishCast() { this.isCasting = false; }

    // --- RISORSE ---

    public int getBombAmmo() { return bombAmmo; }
    public void addBombAmmo(int amount) { this.bombAmmo += amount; }

    public int getAuraAmmo() { return auraAmmo; }
    public void addAuraAmmo(int amount) { this.auraAmmo += amount; }

    // --- POWER-UP ---

    public boolean hasShield() { return hasShield; }
    public void setShield(boolean shield) { this.hasShield = shield; }

    public boolean hasMaxRadius() { return hasMaxRadius; }
    public void setMaxRadius(boolean max) {
        this.hasMaxRadius = max;
        this.bombRadius = max ? Config.DEFAULT_BOMB_RADIUS + 1 : Config.DEFAULT_BOMB_RADIUS;
    }

    public boolean isPerfectLevel() { return perfectLevel; }
    public void resetPerfectLevel() { this.perfectLevel = true; }

    public boolean hasMaxSpeed() { return hasMaxSpeed; }
    public void setMaxSpeed(boolean max) {
        this.hasMaxSpeed = max;
        this.speed = max ? Config.ENTITY_LOGICAL_SPEED * 1.2 : Config.ENTITY_LOGICAL_SPEED;
    }

    public void resetPowerUps() {
        this.hasShield = false;
        this.hasMaxRadius = false;
        this.hasMaxSpeed = false;
        this.bombRadius = Config.DEFAULT_BOMB_RADIUS;
        this.speed = Config.ENTITY_LOGICAL_SPEED;
        System.out.println("Power-up resettati per il nuovo livello.");
    }
}