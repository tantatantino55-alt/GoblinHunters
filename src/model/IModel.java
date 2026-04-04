package model;

import utils.Direction;
import utils.EnemyType;
import utils.ItemType;
import utils.PlayerState;

public interface IModel {

    int[][] generateProceduralMap();

    public int getNumColumns();
    public int getNumRows();
    public double xCoordinatePlayer();
    public double yCoordinatePlayer();
    public void setPlayerDelta(double dx, double dy);
    public double getPlayerDeltaX();
    public double getPlayerDeltaY();
    public int[][] getGameAreaArray();
    public boolean isWalkable(double nextX, double nextY);
    public void placeBomb();
    public void updateGameLogic();

    PlayerState getPlayerState();
    long getPlayerStateStartTime();

    int getEnemyCount();
    double getEnemyX(int index);
    double getEnemyY(int index);
    Direction getEnemyDirection(int index);
    EnemyType getEnemyType(int index);
    Direction getEnemyTelegraph(int index);

    void addProjectile(Projectile projectile);
    public boolean isAreaOccupiedByOtherEnemy(double nextX, double nextY, Enemy enemy);
    public boolean isPlayerInvincible();
    public int getPlayerLives();
    public int getElapsedTimeInSeconds();
    void staffAttack();

    // --- NUOVI METODI A INDICE (Sostituiscono le allocazioni) ---
    // BOMBE
    int getBombCount();
    int getBombRow(int index);
    int getBombCol(int index);
    int getBombElapsedTime(int index);

    // PROIETTILI
    int getProjectileCount();
    double getProjectileX(int index);
    double getProjectileY(int index);
    boolean isProjectileEnemy(int index);
    int getProjectileDirection(int index);
    public void playerShoot();
    public void destroyBlock(int row, int col);

    // EFFETTI DISTRUZIONE CASSE
    int getDestructionCount();
    int getDestructionRow(int index);
    int getDestructionCol(int index);
    int getDestructionElapsedTime(int index);

    // FUOCO
    int getFireCount();
    int getFireRow(int index);
    int getFireCol(int index);
    int getFireType(int index);
    public boolean isEnemyAttacking(int index);

    public boolean isEnemyWaiting(int index);

    void resetPlayerStateAfterAction();

    // Accesso ai collectible tramite indice (nessuna esposizione del tipo concreto)
    long getCollectibleSpawnTime(int index);

    int getPlayerBombAmmo();
    int getPlayerAuraAmmo();
    boolean hasPlayerShield();
    boolean hasPlayerMaxRadius();
    boolean hasPlayerMaxSpeed();

    int getPortalRow();
    int getPortalCol();
    boolean isPortalRevealed();

    // --- GESTIONE LIVELLI E CAMBIO MAPPA (GATE) ---
    int getCurrentZone();
    int getDifficultyCycle();
    boolean isExitGateActive();
    String getCurrentTheme();

    boolean isGateActive();

    boolean isLevelCompletedFlag();

    long getGateExitActivationTime();

    void prepareNextLevel(int[][] newMap);
    boolean isTransitioning();
    void setTransitioning(boolean transitioning);
    // Collectibles (oggetti a terra) – accesso esclusivamente tramite indice
    int getCollectibleCount();
    double getCollectibleX(int index);
    double getCollectibleY(int index);
    ItemType getCollectibleType(int index);
    long getPortalRevealTime();

    long getExitGateActivationTime();
    int getExitGateRow();
    int getExitGateCol();

    int getScore();

    String getEnemyState(int index);
    boolean isEnemyInvincible(int index);
    long getEnemyStateStartTime(int index);
}