package model;

import utils.Direction;
import utils.EnemyType;
import utils.PlayerState;

import java.util.List;

public interface IModel {
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
}