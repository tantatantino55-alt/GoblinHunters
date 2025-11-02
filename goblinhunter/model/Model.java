package goblinhunter.model;

import goblinhunter.utils.Config;

public class Model implements IModel{
    //---------------------------------------------------------------
    // STATIC CONSTANTS
    //---------------------------------------------------------------
    public static final int DEFAULT_NUM_ROWS  = 11;
    public static final int DEFAULT_NUM_COLUMNS = 13;



    //---------------------------------------------------------------
    // STATIC FIELDS
    //---------------------------------------------------------------
    private static Model instance = null;
    //---------------------------------------------------------------
    // INSTANCE ATTRIBUTES
    //---------------------------------------------------------------
    private final int[][] gameAreaArray;
    private Player player;

    //TEST MAP
    private static final int[][] TEST_MAP = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,2,0,2,0,0,0,2,0,2,0,1},
            {1,2,1,0,1,0,1,0,1,0,1,2,1},
            {1,0,0,0,2,0,0,0,2,0,0,0,1},
            {1,2,1,2,1,2,1,2,1,2,1,2,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,2,1,2,1,2,1,2,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,2,1,2,1,2,1,2,1,2,1,2,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1}
    };


    private Model() {
        this.gameAreaArray = new int[Config.GRID_HEIGHT][Config.GRID_WIDTH];
        int startX = Config.GRID_OFFSET_X + 1 * Config.TILE_SIZE;

        this.player = new Player(startX, Config.y);

    }

    private void loadMap(int[][] map){
        for (int i = 0; i < Config.GRID_HEIGHT; i++)
           for (int j = 0; j < Config.GRID_WIDTH; j++)
               this.gameAreaArray[i][j] = map[i][j];
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
    public int xCoordinatePlayer() {
        return this.player.getXCoordinate();
    }
    public int yCoordinatePlayer() {
        return this.player.getYCoordinate();

    }

    @Override
    public void MoveUp() {
        this.player.updateYcoordinate(-1);

    }

    @Override
    public void MoveDown() {
        this.player.updateYcoordinate(1);
    }

    @Override
    public void MoveLeft() {
        this.player.updateXcoordinate(-1);
    }

    @Override
    public void MoveRight() {
        this.player.updateXcoordinate(+1);

    }

    public boolean isMovable (int x, int y) {
        int nextX = this.xCoordinatePlayer() + x;
        int nextY = this.yCoordinatePlayer() + y;

        //verifico bordo
        if (nextY < 0 || nextY >= Model.getInstance().getNumRows() ||
                nextX < 0 || nextX >= Model.getInstance().getNumColumns())
            return false;
        //verifico cella
        // if()
        //   return false;
        //else
         return true;
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
