package goblinhunter.controller;

import goblinhunter.model.Model;
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








    //---------------------------------------------------------------
    // STATIC METHODS
    //---------------------------------------------------------------
    public static IControllerForView getInstance() {
        if (instance == null)
            instance = new ControllerForView();
        return instance;
    }

}
