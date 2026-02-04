package controller;

import model.Model;
import utils.Config;
import utils.SpriteManager;
import view.View;

public class ControllerForView implements IControllerForView {
    private static ControllerForView instance = null;

    private ControllerForView() {}

    @Override public void openGameGUI() { View.getInstance().openGameGUI(); }
    @Override public void closeGameGUI() { View.getInstance().closeGameGUI(); }
    @Override public int getNumColumns() { return Model.getInstance().getNumColumns(); }
    @Override public int getNumRows() { return Model.getInstance().getNumRows(); }
    @Override public double XCoordinatePlayer() { return Model.getInstance().xCoordinatePlayer(); }
    @Override public double yCoordinatePlayer() { return Model.getInstance().yCoordinatePlayer(); }
    @Override public double getDeltaX() { return Model.getInstance().getPlayerDeltaX(); }
    @Override public double getDeltaY() { return Model.getInstance().getPlayerDeltaY(); }
    @Override public void setPlayerMovement(double dx, double dy) { Model.getInstance().setPlayerDelta(dx, dy); }
    @Override public int[][] getGameAreaArray() { return Model.getInstance().getGameAreaArray(); }
    @Override public void PlaceBomb() { Model.getInstance().PlaceBomb(); }
    @Override public void requestRepaint() { View.getInstance().requestRepaint(); }
    @Override public String getPlayerAction() { return Model.getInstance().getPlayerAction(); }
    @Override public int getCurrentPlayerFrameIndex() { return Model.getInstance().getPlayerFrameIndex(); }

    @Override
    public void setupResources() {
        SpriteManager sm = SpriteManager.getInstance();
        sm.loadAnimation("PLAYER_BACK_ATTACKING", Config.PLAYER1_SHEET, 0, 10, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_BACK_HURT", Config.PLAYER1_SHEET, 10, 10, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_BACK_IDLE", Config.PLAYER1_SHEET, 20, 16, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_BACK_RUNNING", Config.PLAYER1_SHEET, 36, 12, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_DYING", Config.PLAYER1_SHEET, 48, 10, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_FRONT_ATTACKING", Config.PLAYER1_SHEET, 58, 10, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_FRONT_HURT", Config.PLAYER1_SHEET, 68, 10, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_FRONT_IDLE", Config.PLAYER1_SHEET, 78, 16, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_FRONT_RUNNING", Config.PLAYER1_SHEET, 94, 12, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_LEFT_ATTACKING", Config.PLAYER1_SHEET, 106, 10, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_LEFT_HURT", Config.PLAYER1_SHEET, 116, 10, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_LEFT_IDLE", Config.PLAYER1_SHEET, 126, 16, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_LEFT_RUNNING", Config.PLAYER1_SHEET, 142, 12, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_RIGHT_ATTACKING", Config.PLAYER1_SHEET, 154, 10, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_RIGHT_HURT", Config.PLAYER1_SHEET, 164, 10, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_RIGHT_IDLE", Config.PLAYER1_SHEET, 174, 16, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_RIGHT_RUNNING", Config.PLAYER1_SHEET, 190, 12, Config.PLAYER_FRAME_SIZE);
    }

    public static IControllerForView getInstance() {
        if (instance == null) instance = new ControllerForView();
        return instance;
    }
}