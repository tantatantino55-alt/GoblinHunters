package goblinhunter.controller;

import goblinhunter.model.Player;

public interface IControllerForView {

    public void openGameGUI();

    public void closeGameGUI();

    public int getNumColumns();

    public int getNumRows();

    public Player getPlayer();

    public int XCoordinatePlayer();

    public int yCoordinatePlayer();

    public void MoveUp();
    
    public void MoveDown();

    public void MoveLeft();

    public void MoveRight();
    
    public void requestRepaint();

    public void PlaceBomb();

}
