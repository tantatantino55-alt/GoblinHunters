package model;

public interface IModel {

    int getNumColumns();

    int getNumRows();

    Player getPlayer();

    public int xCoordinatePlayer();

    public int yCoordinatePlayer();
/*
    public void MoveUp();

    public void MoveDown();

    public void MoveLeft();

    public void MoveRight();
*/

    public void PlaceBomb();

    public void updatePlayerMovement();

    public void setPlayerDelta(int dx, int dy);

    public int getPlayerDeltaX();

    public int getPlayerDeltaY();

    int[][] getGameAreaArray();
}
