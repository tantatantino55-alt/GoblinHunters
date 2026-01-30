package controller;

import model.Player;

public interface IControllerForView {

    public void openGameGUI();

    public void closeGameGUI();

    public int getNumColumns();

    public int getNumRows();

    public Player getPlayer();

    public int XCoordinatePlayer();

    public int yCoordinatePlayer();

    public void requestRepaint();

    public void PlaceBomb();

    public void setPlayerMovement(int dx, int dy);

    public int getDeltaX();

    public int  getDeltaY();

    int[][] getGameAreaArray();
}
