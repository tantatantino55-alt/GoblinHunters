package goblinhunter.controller;

import goblinhunter.model.Model;
import goblinhunter.model.Player;
import goblinhunter.view.View;

public class ControllerForView implements IControllerForView{
    //---------------------------------------------------------------
    // STATIC FIELDS
    //---------------------------------------------------------------
    private static ControllerForView instance = null;


    public void openGameGUI() {
        View.getInstance().openGameGUI();
    }


    public void closeGameGUI() {
        View.getInstance().closeGameGUI();
    }

    public int getNumColumns(){
        return Model.getInstance().getNumColumns();
    }
    public int getNumRows(){
        return Model.getInstance().getNumRows();
    }


    public Player getPlayer() {
        return Model.getInstance().getPlayer();
    }


    public int XCoordinatePlayer() {
        return Model.getInstance().XCoordinatePlayer();
    }


    public int yCoordinatePlayer() {
        return Model.getInstance().yCoordinatePlayer();
    }


    public void MoveUp() {


    }

    public void MoveDown() {

    }


    public void MoveLeft() {

    }


    public void MoveRight() {

    }


    @Override
    public void PlaceBomb() {

    }


    //  Nuovo metodo per richiedere il repaint
    @Override
    public void requestRepaint() {
        View.getInstance().requestRepaint();
    }
    //---------------------------------------------------------------
    // STATIC METHODS
    //---------------------------------------------------------------
    public static IControllerForView getInstance() {
        if (instance == null)
            instance = new ControllerForView();
        return instance;
    }

}
