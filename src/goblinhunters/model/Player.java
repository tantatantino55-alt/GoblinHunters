package goblinhunters.model;

import goblinhunters.utils.Config;
import goblinhunters.utils.Direction;
import goblinhunters.utils.PlayerState;

public class Player extends Entity {

    private double deltaX = 0;
    private double deltaY = 0;

    private PlayerState currentState;
    private long stateStartTime;

    private Direction currentDirection = Direction.DOWN;
    private boolean isMoving = false;

    private int lives;
    private long invincibilityEndTime = 0;
    private int maxBombs = Config.INITIAL_MAX_BOMBS;
    private int bombRadius = Config.DEFAULT_BOMB_RADIUS;

    private boolean isCasting = false;
    private int castTimer = 0;
    private long lastCastTime = 0;
    private final long CAST_COOLDOWN_MS = 800;

    private static final int DEFAULT_BOMB_AMMO = 10;
    private static final int DEFAULT_AURA_AMMO = 5;
    private int bombAmmo = DEFAULT_BOMB_AMMO;
    private int auraAmmo = DEFAULT_AURA_AMMO;
    private boolean hasShield = false;
    private boolean hasMaxRadius = false;
    private boolean hasMaxSpeed = false;

    // -1 means no snapshot is active; set when the boss fight starts
    private int savedBombAmmo = -1;
    private int savedAuraAmmo = -1;

    private boolean perfectLevel = true;

    // instance methods

    public Player(double startX, double startY) {
        super(startX, startY);
        this.currentState = PlayerState.IDLE_FRONT;
        this.lives = Config.INITIAL_LIVES;
        this.speed = Config.ENTITY_LOGICAL_SPEED;
    }

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
        if (currentState == PlayerState.DYING) return;

        if (isMoving) {
            switch (currentDirection) {
                case UP    -> setState(PlayerState.RUN_BACK);
                case DOWN  -> setState(PlayerState.RUN_FRONT);
                case LEFT  -> setState(PlayerState.RUN_LEFT);
                case RIGHT -> setState(PlayerState.RUN_RIGHT);
            }
        } else {
            switch (currentDirection) {
                case UP    -> setState(PlayerState.IDLE_BACK);
                case DOWN  -> setState(PlayerState.IDLE_FRONT);
                case LEFT  -> setState(PlayerState.IDLE_LEFT);
                case RIGHT -> setState(PlayerState.IDLE_RIGHT);
            }
        }
    }

    public void setState(PlayerState newState) {
        if (this.currentState != newState) {
            this.currentState = newState;
            this.stateStartTime = System.currentTimeMillis();
        }
    }

    public PlayerState getState()       { return currentState; }
    public long getStateStartTime()     { return stateStartTime; }

    public boolean isInvincible()       { return System.currentTimeMillis() < invincibilityEndTime; }

    public boolean takeDamage() {
        if (isInvincible() || lives <= 0) return false;

        this.perfectLevel = false;

        if (hasShield) {
            hasShield = false;
            invincibilityEndTime = System.currentTimeMillis() + 2000;
            return false;
        }

        lives--;
        invincibilityEndTime = System.currentTimeMillis() + 2000;
        setState(PlayerState.HURT_FRONT);
        return true;
    }

    public int getLives() { return lives; }

    public void restoreLives() {
        this.lives = Config.INITIAL_LIVES;
    }

    public double getXCoordinate()              { return x; }
    public double getYCoordinate()              { return y; }
    public void setXCoordinate(double newX)     { this.x = newX; }
    public void setYCoordinate(double newY)     { this.y = newY; }
    public void setDelta(double dx, double dy)  { this.deltaX = dx; this.deltaY = dy; }
    public double getDeltaX()                   { return deltaX; }
    public double getDeltaY()                   { return deltaY; }
    public int getMaxBombs()                    { return maxBombs; }
    public void setMaxBombs(int maxBombs)       { this.maxBombs = maxBombs; }
    public int getBombRadius()                  { return bombRadius; }
    public void setBombRadius(int radius)       { this.bombRadius = radius; }
    public double getSpeed()                    { return speed; }

    // casting (aura)

    public boolean canCast() {
        return System.currentTimeMillis() - lastCastTime > CAST_COOLDOWN_MS;
    }

    public void startCast() {
        this.isCasting = true;
        this.lastCastTime = System.currentTimeMillis();
        this.castTimer = 10;
        this.isMoving = false;
    }

    public boolean isCasting()          { return isCasting; }

    public void decrementCastTimer() {
        if (castTimer > 0) castTimer--;
    }

    public int getCastTimer()           { return castTimer; }
    public void finishCast()            { this.isCasting = false; }

    // ammo

    public int getBombAmmo()                    { return bombAmmo; }
    public void addBombAmmo(int amount)         { this.bombAmmo += amount; }

    public int getAuraAmmo()                    { return auraAmmo; }
    public void addAuraAmmo(int amount) {
        this.auraAmmo = Math.min(this.auraAmmo + amount, Config.MAX_AURA_AMMO);
    }

    /** Saves current ammo as the boss-fight checkpoint; called once when the zone-3 crates explode. */
    public void snapshotBossFightAmmo() {
        this.savedBombAmmo = this.bombAmmo;
        this.savedAuraAmmo = this.auraAmmo;
    }

    /** Restores ammo to the boss-fight checkpoint on each respawn while the boss is alive. */
    public void restoreBossFightAmmo() {
        if (savedBombAmmo >= 0) {
            this.bombAmmo = savedBombAmmo;
            this.auraAmmo = savedAuraAmmo;
        }
    }

    public void clearBossFightSnapshot() {
        this.savedBombAmmo = -1;
        this.savedAuraAmmo = -1;
    }

    /** Falls back to safe defaults so the player cannot get permanently stuck with zero ammo. */
    public void restoreDefaultAmmo() {
        this.bombAmmo = DEFAULT_BOMB_AMMO;
        this.auraAmmo = DEFAULT_AURA_AMMO;
    }

    public boolean isOutOfAmmo() {
        return bombAmmo <= 0 && auraAmmo <= 0;
    }

    // power-ups

    public boolean hasShield()                  { return hasShield; }
    public void setShield(boolean shield)       { this.hasShield = shield; }

    public boolean hasMaxRadius()               { return hasMaxRadius; }
    public void setMaxRadius(boolean max) {
        this.hasMaxRadius = max;
        this.bombRadius = max ? Config.DEFAULT_BOMB_RADIUS + 1 : Config.DEFAULT_BOMB_RADIUS;
    }

    public boolean isPerfectLevel()             { return perfectLevel; }
    public void resetPerfectLevel()             { this.perfectLevel = true; }

    public boolean hasMaxSpeed()                { return hasMaxSpeed; }
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
    }
}
