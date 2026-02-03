package model;

public interface IModel {

    public int getNumColumns();

    public int getNumRows();

    public int xCoordinatePlayer();

    public int yCoordinatePlayer();

    public void PlaceBomb();

    public void updatePlayerMovement();

    public void setPlayerDelta(int dx, int dy);

    public int getPlayerDeltaX();

    public int getPlayerDeltaY();

    public int[][] getGameAreaArray();

    public String getPlayerAction();

    public int getPlayerFrameIndex();
}
