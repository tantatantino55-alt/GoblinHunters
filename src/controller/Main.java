package controller;

import view.View;

public class Main {

    public static void main(String[] args) {
        View.getInstance().setupResources();
        ControllerForView.getInstance().openGameGUI();
        ControllerForModel.getInstance().startGameLoop();
    }

}// end class