package goblinhunter.controller;

public class Main {

    public static void main(String[] args) {
      ControllerForView.getInstance().openGameGUI();
      ControllerForModel.getInstance().startGameLoop();
    }

}// end class