package controller;

import utils.Direction;
import utils.EnemyType;
import utils.ItemType;
import utils.PlayerState;

public interface IControllerForView {
    public void openGameGUI();
    public void closeGameGUI();
    public int getNumColumns();
    public int getNumRows();
    public double getXCoordinatePlayer();
    public double getYCoordinatePlayer();
    public void requestRepaint();
    public void placeBomb();
    public void setPlayerMovement(double dx, double dy);
    public double getDeltaX();
    public double getDeltaY();
    public int[][] getGameAreaArray();

    int getEnemyCount();
    double getEnemyX(int index);
    double getEnemyY(int index);
    Direction getEnemyDirection(int index);
    EnemyType getEnemyType(int index);
    Direction getEnemyTelegraph(int index);

    PlayerState getPlayerState();
    long getPlayerStateStartTime();
    public boolean isPlayerInvincible();
    public int getPlayerLives();
    public int getElapsedTimeInSeconds();
    public boolean isEnemyAttacking(int index);



    // --- NUOVI METODI A INDICE ---
    int getBombCount();
    int getBombRow(int index);
    int getBombCol(int index);
    int getBombElapsedTime(int index);

    int getProjectileCount();
    double getProjectileX(int index);
    double getProjectileY(int index);
    boolean isProjectileEnemy(int index);
    int getProjectileDirection(int index);

    int getDestructionCount();
    int getDestructionRow(int index);
    int getDestructionCol(int index);
    int getDestructionElapsedTime(int index);

    int getFireCount();
    int getFireRow(int index);
    int getFireCol(int index);
    int getFireType(int index);

    public boolean isEnemyWaiting(int index);

    void playerShoot();

    // In ControllerForView.java
    void resetPlayerStateAfterAction();

    void staffAttack();

    int getPlayerBombAmmo();
    int getPlayerAuraAmmo();
    boolean hasPlayerShield();
    boolean hasPlayerMaxRadius();
    boolean hasPlayerMaxSpeed();

    int getPortalRow();
    int getPortalCol();
    boolean isPortalRevealed();
    String getCurrentTheme();

    boolean isTransitioning();
    // Collectibles (oggetti a terra)
    int getCollectibleCount();
    double getCollectibleX(int index);
    double getCollectibleY(int index);
    ItemType getCollectibleType(int index);

    long getPortalRevealTime();

    boolean isGateActive();

    long getGateActivationTime();

    int getExitGateCol();

    int getExitGateRow();
}