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
        return Model.getInstance().xCoordinatePlayer();
    }



    public int yCoordinatePlayer() {
        return Model.getInstance().yCoordinatePlayer();
    }

    /*
    public void setPlayerMovement(int dx, int dy){
        Model.getInstance().getPlayer().setDelta(dx, dy);
    }
     */

    @Override
    public void setPlayerMovement(int dx, int dy) {
        // CORREZIONE: Chiama il Model tramite l'interfaccia/metodo che protegge l'incapsulamento del Player.
        Model.getInstance().setPlayerDelta(dx, dy);
    }

    @Override
    public int getDeltaX() {
        return Model.getInstance().getPlayerDeltaX();
    }

    @Override
    public int getDeltaY() {
         return Model.getInstance().getPlayerDeltaaY();
    }

    @Override
    public int[][] getGameAreaArray() {
        return Model.getInstance().getGameAreaArray();
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
