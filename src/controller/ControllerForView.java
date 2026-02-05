package controller;

import model.Model;
import utils.Direction;
import utils.EnemyType;
import utils.PlayerState;
import view.View;

public class ControllerForView implements IControllerForView {

    private static ControllerForView instance = null;

    private ControllerForView() {}

    @Override
    public void openGameGUI() { View.getInstance().openGameGUI(); }

    @Override
    public void closeGameGUI() { View.getInstance().closeGameGUI(); }

    @Override
    public int getNumColumns() { return Model.getInstance().getNumColumns(); }

    @Override
    public int getNumRows() { return Model.getInstance().getNumRows(); }

    @Override
    public double getXCoordinatePlayer() { return Model.getInstance().xCoordinatePlayer(); }

    @Override
    public double getYCoordinatePlayer() { return Model.getInstance().yCoordinatePlayer(); }

    @Override
    public double getDeltaX() { return Model.getInstance().getPlayerDeltaX(); }

    @Override
    public double getDeltaY() { return Model.getInstance().getPlayerDeltaY(); }

    @Override
    public void setPlayerMovement(double dx, double dy) { Model.getInstance().setPlayerDelta(dx, dy); }

    @Override
    public int[][] getGameAreaArray() { return Model.getInstance().getGameAreaArray(); }

    @Override
    public void PlaceBomb() { Model.getInstance().PlaceBomb(); }

    @Override
    public void requestRepaint() { View.getInstance().requestRepaint(); }





    @Override
    public int[][] getActiveBombsData() {
        return Model.getInstance().getActiveBombsData();
    }
    @Override
    public int getEnemyCount() {
        return Model.getInstance().getEnemyCount();
    }

    @Override
    public double getEnemyX(int index) {
        return Model.getInstance().getEnemyX(index);
    }

    @Override
    public double getEnemyY(int index) {
        return Model.getInstance().getEnemyY(index);
    }

    @Override
    public Direction getEnemyDirection(int index) {
        return Model.getInstance().getEnemyDirection(index);
    }

    @Override
    public EnemyType getEnemyType(int index) {
        return Model.getInstance().getEnemyType(index);
    }

    @Override
    public PlayerState getPlayerState() {
        return Model.getInstance().getPlayerState();
    }
    public long getPlayerStateStartTime() {
        return Model.getInstance().getPlayerStateStartTime();
    }

    public static IControllerForView getInstance() {
        if (instance == null) instance = new ControllerForView();
        return instance;
    }
}