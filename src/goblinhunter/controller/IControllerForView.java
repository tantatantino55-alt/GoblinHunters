package goblinhunter.controller;

import goblinhunter.utils.*;

import java.util.List;
import java.util.function.BiConsumer;

public interface IControllerForView {
    void openGameGUI();

    GameState getGameState();
    void resetGame();

    void menuHandleClick(int characterIndex);
    void menuConfirmSelection();
    double getXCoordinatePlayer();
    double getYCoordinatePlayer();
    void requestRepaint();
    void placeBomb();
    void setPlayerMovement(double dx, double dy);
    double getDeltaX();
    double getDeltaY();
    int[][] getGameAreaArray();

    int getEnemyCount();
    double getEnemyX(int index);
    double getEnemyY(int index);
    Direction getEnemyDirection(int index);
    EnemyType getEnemyType(int index);
    PlayerState getPlayerState();
    long getPlayerStateStartTime();
    boolean isPlayerInvincible();
    int getPlayerLives();
    int getElapsedTimeInSeconds();
    int getScore();
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

    void playerShoot();
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

    int getCollectibleCount();
    double getCollectibleX(int index);
    double getCollectibleY(int index);
    ItemType getCollectibleType(int index);
    boolean isGateActive();
    long getGateActivationTime();
    int getExitGateCol();
    int getExitGateRow();

    String getEnemyState(int index);
    boolean isEnemyInvincible(int index);
    long getEnemyStateStartTime(int index);

    int getCrackCount();
    int getCrackRow(int index);
    int getCrackCol(int index);

    int getBossHP();
    int getBossMaxHP();

    boolean isBossPortalActive();
    int getBossPortalRow();
    int getBossPortalCol();
    boolean isStaffUsable();

    void   menuAppendNameChar(char c);
    void   menuDeleteNameChar();
    void   menuSetTypingName(boolean v);
    String getMenuPlayerName();
    int    getMenuSelectedIndex();
    boolean isMenuTypingName();

    List<ScoreEntry> getTopScores();

    boolean isPaused();
    void setPaused(boolean paused);

    PauseController getPauseController();

    void setKeyBindingApplier(BiConsumer<Integer, String> applier);
    void applyKeyBinding(int actionIndex, String newKeyName);
}
