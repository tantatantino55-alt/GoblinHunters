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

    // --- NUOVI ATTRIBUTI PER L'ATTACCO (Aura) ---
    private boolean isCasting = false;
    private int castTimer = 0;       // Conta i tick (frame logici) del lancio
    private long lastCastTime = 0;   // Per calcolare il Cooldown
    private final long CAST_COOLDOWN_MS = 800; // 0.8 secondi di attesa tra un colpo e l'altro

    // --- RISORSE E POWER-UP ---
    private int bombAmmo = 5;   // Iniziamo con 5 bombe per test
    private int auraAmmo = 10;  // Iniziamo con 10 magie per test
    private boolean hasShield = false;
    private boolean hasMaxRadius = false;
    private boolean hasMaxSpeed = false;

    // Traccia se ha preso danni nel livello corrente per il Perfect Bonus
    private boolean perfectLevel = true;

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
        //updateState(); // Aggiorna lo sprite subito
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
    public boolean takeDamage() {
        if (isInvincible() || lives <= 0) return false;

        // Al primo danno (scudo o vita), addio perfect level!
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

    public boolean canCast() {
        return System.currentTimeMillis() - lastCastTime > CAST_COOLDOWN_MS;
    }

    public void startCast() {
        this.isCasting = true;
        this.lastCastTime = System.currentTimeMillis();
        // A 60FPS, 15 tick sono esattamente 0.25 secondi
        // (Perfetto per mostrare 3 frame d'animazione a 80ms l'uno)
        this.castTimer = 10;
        this.isMoving = false; // Ferma visivamente le gambe
    }

    public boolean isCasting() { return isCasting; }

    public void decrementCastTimer() {
        if (castTimer > 0) castTimer--;
    }

    public int getCastTimer() { return castTimer; }

    public void finishCast() { this.isCasting = false; }

    // Metodi per le Risorse
    public int getBombAmmo() { return bombAmmo; }
    public void addBombAmmo(int amount) { this.bombAmmo += amount; }

    public int getAuraAmmo() { return auraAmmo; }
    public void addAuraAmmo(int amount) { this.auraAmmo += amount; }

    // Metodi per i Power-up
    public boolean hasShield() { return hasShield; }
    public void setShield(boolean shield) { this.hasShield = shield; }

    public boolean hasMaxRadius() { return hasMaxRadius; }
    public void setMaxRadius(boolean max) {
        this.hasMaxRadius = max;
        // Se max è true, la bomba fa un'esplosione più grande (+2 caselle)
        this.bombRadius = max ? Config.DEFAULT_BOMB_RADIUS + 1 : Config.DEFAULT_BOMB_RADIUS;
    }
    public boolean isPerfectLevel() { return perfectLevel; }
    public void resetPerfectLevel() { this.perfectLevel = true; }

    public boolean hasMaxSpeed() { return hasMaxSpeed; }
    public void setMaxSpeed(boolean max) {
        this.hasMaxSpeed = max;
        // Se max è true, aumenta la velocità del 50%
        this.speed = max ? Config.ENTITY_LOGICAL_SPEED * 1.2 : Config.ENTITY_LOGICAL_SPEED;
    }
    // In src/model/Player.java

    public void resetPowerUps() {
        // Reset degli stati logici (booleani)
        this.hasShield = false;
        this.hasMaxRadius = false;
        this.hasMaxSpeed = false;

        // Reset degli attributi fisici ai valori di default della Config
        this.bombRadius = Config.DEFAULT_BOMB_RADIUS;
        this.speed = Config.ENTITY_LOGICAL_SPEED;

        // Opzionale: puoi decidere se resettare o meno anche le munizioni
        // this.bombAmmo = 5;
        // this.auraAmmo = 10;

        System.out.println("Power-up resettati per il nuovo livello.");
    }
}