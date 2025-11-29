package goblinhunter.controller;

import goblinhunter.utils.ResourceManager;

public class Main {

    public static void main(String[] args) {
      ControllerForView.getInstance().openGameGUI();
      ControllerForModel.getInstance().startGameLoop();
      ResourceManager.printDebugInfo();
    }

}// end class