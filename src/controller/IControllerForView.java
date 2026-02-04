package controller;

public interface IControllerForView {
    void openGameGUI();
    void closeGameGUI();
    int getNumColumns();
    int getNumRows();
    double XCoordinatePlayer();
    double yCoordinatePlayer();
    void requestRepaint();
    void PlaceBomb();
    void setPlayerMovement(double dx, double dy); // Cambiato in double
    double getDeltaX();
    double getDeltaY();
    int[][] getGameAreaArray();
    void setupResources();
    String getPlayerAction();
    int getCurrentPlayerFrameIndex();
}