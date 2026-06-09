package goblinhunters.model;

import goblinhunters.utils.Direction;
import goblinhunters.utils.EnemyType;
import goblinhunters.utils.ItemType;
import goblinhunters.utils.PlayerState;

public interface IModel {

    // map
    int[][] generateProceduralMap();
    int getNumColumns();
    int getNumRows();
    int[][] getGameAreaArray();
    void destroyBlock(int row, int col);

    // player position and state
    double xCoordinatePlayer();
    double yCoordinatePlayer();
    void setPlayerDelta(double dx, double dy);
    double getPlayerDeltaX();
    double getPlayerDeltaY();
    PlayerState getPlayerState();
    long getPlayerStateStartTime();
    boolean isPlayerInvincible();
    int getPlayerLives();
    int getPlayerBombAmmo();
    int getPlayerAuraAmmo();
    boolean hasPlayerShield();
    boolean hasPlayerMaxRadius();
    boolean hasPlayerMaxSpeed();
    int getElapsedTimeInSeconds();

    // player actions
    void placeBomb();
    void playerShoot();
    void staffAttack();
    void resetPlayerStateAfterAction();

    // collision
    boolean isWalkable(double nextX, double nextY);
    boolean isAreaOccupiedByOtherEnemy(double nextX, double nextY, double selfX, double selfY);

    // enemies
    int getEnemyCount();
    double getEnemyX(int index);
    double getEnemyY(int index);
    Direction getEnemyDirection(int index);
    EnemyType getEnemyType(int index);
    Direction getEnemyTelegraph(int index);
    String getEnemyState(int index);
    boolean isEnemyInvincible(int index);
    boolean isEnemyAttacking(int index);
    boolean isEnemyWaiting(int index);
    long getEnemyStateStartTime(int index);

    // bombs
    int getBombCount();
    int getBombRow(int index);
    int getBombCol(int index);
    int getBombElapsedTime(int index);

    // projectiles
    int getProjectileCount();
    double getProjectileX(int index);
    double getProjectileY(int index);
    boolean isProjectileEnemy(int index);
    int getProjectileDirection(int index);

    // fire
    int getFireCount();
    int getFireRow(int index);
    int getFireCol(int index);
    int getFireType(int index);

    // crate destruction effects
    int getDestructionCount();
    int getDestructionRow(int index);
    int getDestructionCol(int index);
    int getDestructionElapsedTime(int index);

    // collectibles
    int getCollectibleCount();
    double getCollectibleX(int index);
    double getCollectibleY(int index);
    ItemType getCollectibleType(int index);
    long getCollectibleSpawnTime(int index);

    // portal and exit gate
    int getPortalRow();
    int getPortalCol();
    boolean isPortalRevealed();
    long getPortalRevealTime();
    int getExitGateRow();
    int getExitGateCol();
    boolean isExitGateActive();
    long getExitGateActivationTime();

    // levels and zones
    int getCurrentZone();
    int getDifficultyCycle();
    String getCurrentTheme();
    boolean isPreparationPhase();
    boolean isLevelCompletedFlag();
    boolean isTransitioning();
    void setTransitioning(boolean transitioning);
    void prepareNextLevel();

    // boss floor cracks
    int getCrackCount();
    int getCrackRow(int index);
    int getCrackCol(int index);

    // boss HUD
    int getBossHP();
    int getBossMaxHP();

    // boss portal (zone 2)
    boolean isBossPortalActive();
    int getBossPortalRow();
    int getBossPortalCol();
    long getBossPortalActivationTime();

    // score and game state
    int getScore();
    boolean isGameOverPending();
    void clearGameOverPending();

    // game loop
    void updateGameLogic();
}
