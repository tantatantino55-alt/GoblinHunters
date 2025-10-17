package goblinhunter.model;

import goblinhunter.utils.Config;

public class Model implements IModel{
    //---------------------------------------------------------------
    // STATIC CONSTANTS
    //---------------------------------------------------------------
    public static final int DEFAULT_NUM_ROWS  = 13;
    public static final int DEFAULT_NUM_COLUMNS = 11;



    //---------------------------------------------------------------
    // STATIC FIELDS
    //---------------------------------------------------------------
    private static Model instance = null;
    //---------------------------------------------------------------
    // INSTANCE ATTRIBUTES
    //---------------------------------------------------------------
    private final int[][] gameAreaArray;
    private Player player;
    private Model() {
        this.gameAreaArray = new int[Config.GRID_HEIGHT][Config.GRID_WIDTH];
        this.player = new Player(Config.x, Config.y);

    }



    public int getNumRows(){
        return this.gameAreaArray.length;
    }

    public int getNumColumns() {
        return this.gameAreaArray[0].length;
    }
    public Player getPlayer(){
        return this.player;
    }
    public int XCoordinatePlayer() {
        return this.player.getXCoordinate();
    }
    public int yCoordinatePlayer() {
        return this.player.getYCoordinate();
    }
    public int setXCoordinatePlayer(){
        //this.player.setXCoordinate(this.getXCoorPlayer())
        return 0;
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


    public static IModel getInstance() {
        if (instance == null)
            instance = new Model();
        return instance;
    }

}
