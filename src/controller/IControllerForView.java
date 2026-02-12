package controller;

import utils.Direction;
import utils.EnemyType;
import utils.PlayerState;

import java.util.List;

public interface IControllerForView {
    public void openGameGUI();
    public void closeGameGUI();
    public int getNumColumns();
    public int getNumRows();
    public double getXCoordinatePlayer();
    public double getYCoordinatePlayer();
    public void requestRepaint();
    public void placeBomb();
    public void setPlayerMovement(double dx, double dy); // Cambiato in double
    public double getDeltaX();
    public double getDeltaY();
    public int[][] getGameAreaArray();
    public int[][] getActiveBombsData();
    int getEnemyCount();
    double getEnemyX(int index);
    double getEnemyY(int index);
    Direction getEnemyDirection(int index);
    EnemyType getEnemyType(int index);
    PlayerState getPlayerState();

    long getPlayerStateStartTime();

    Direction getEnemyTelegraph(int index);

    List<double[]> getProjectilesData();
    List<int[]> getDestructionsData();

    List<int[]> getFireData();

    public boolean isPlayerInvincible();
}