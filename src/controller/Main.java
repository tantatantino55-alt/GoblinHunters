package controller;

import utils.ResourceManager;

public class Main {

    public static void main(String[] args) {
      ControllerForView.getInstance().openGameGUI();
      ControllerForModel.getInstance().startGameLoop();
      ControllerForView.getInstance().setupResources();
    }

}// end class