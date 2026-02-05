package controller;

import utils.Direction;
import utils.EnemyType;

public interface IControllerForView {
    public void openGameGUI();
    public void closeGameGUI();
    public int getNumColumns();
    public int getNumRows();
    public double XCoordinatePlayer();
    public double yCoordinatePlayer();
    public void requestRepaint();
    public void PlaceBomb();
    public void setPlayerMovement(double dx, double dy); // Cambiato in double
    public double getDeltaX();
    public double getDeltaY();
    public int[][] getGameAreaArray();
    public String getPlayerAction();
    public int getCurrentPlayerFrameIndex();
    public int[][] getActiveBombsData();
    int getEnemyCount();
    double getEnemyX(int index);
    double getEnemyY(int index);
    Direction getEnemyDirection(int index);
    EnemyType getEnemyType(int index);

}