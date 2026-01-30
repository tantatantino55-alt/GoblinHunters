package controller;

public interface IControllerForView {

    public void openGameGUI();

    public void closeGameGUI();

    public int getNumColumns();

    public int getNumRows();

    public int XCoordinatePlayer();

    public int yCoordinatePlayer();

    public void requestRepaint();

    public void PlaceBomb();

    public void setPlayerMovement(int dx, int dy);

    public int getDeltaX();

    public int  getDeltaY();

    public int[][] getGameAreaArray();

    public void setupResources();

    public String getPlayerAction();

    public int getCurrentPlayerFrameIndex();


}
