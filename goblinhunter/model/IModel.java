package goblinhunter.model;

public interface IModel {

    int getNumColumns();

    int getNumRows();

    Player getPlayer();

    public int XCoordinatePlayer();

    public int yCoordinatePlayer();
    public void MoveUp();

    public void MoveDown();

    public void MoveLeft();

    public void MoveRight();


    public void PlaceBomb();
}
