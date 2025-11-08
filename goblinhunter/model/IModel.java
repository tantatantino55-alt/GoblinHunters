package goblinhunter.model;

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

    boolean isMovable(int x, int y);
}
