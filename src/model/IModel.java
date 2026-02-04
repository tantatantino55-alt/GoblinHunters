package model;

public interface IModel {
    int getNumColumns();
    int getNumRows();
    double xCoordinatePlayer();
    double yCoordinatePlayer();
    void updatePlayerMovement();
    void setPlayerDelta(double dx, double dy);
    double getPlayerDeltaX();
    double getPlayerDeltaY();
    int[][] getGameAreaArray();
    String getPlayerAction();
    int getPlayerFrameIndex();
    void PlaceBomb();
}