package model;

import utils.Config;

public class Model implements IModel{


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
    private static final int[][] testMap = {
            {0,1,0,0,0,0,0,0,0,0,0,0,0},
            {0,1,0,0,0,0,0,2,0,0,0,0,0},
            {0,1,1,1,1,0,0,0,0,0,0,0,0},
            {0,0,0,0,1,0,0,0,0,0,0,0,0},
            {1,1,1,0,1,0,0,0,0,0,2,0,0},
            {0,0,2,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,2,0,0,0,0,0,0,0},
            {0,0,2,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,2,0,0,0},
            {0,0,0,0,0,2,2,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0}
    };


    private Model() {
        this.gameAreaArray = new int[Config.GRID_HEIGHT][Config.GRID_WIDTH];
        int startX = Config.GRID_OFFSET_X + 0 * Config.TILE_SIZE;
        int startY = Config.GRID_OFFSET_Y + 0 * Config.TILE_SIZE;
        this.loadGameArea(testMap);
        this.player = new Player(startX, startY);

    }

    private void loadGameArea(int[][] map){
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
    public int xCoordinatePlayer() {
        return this.player.getXCoordinate();
    }
    public int yCoordinatePlayer() {
        return this.player.getYCoordinate();
    }
    public int[][] getGameAreaArray() {
        return this.testMap;
    }



    private int checkBounds(int next, int min, int max) {
        return Math.max(min, Math.min(max, next));
    }

    public boolean isWalkable(int nextX, int nextY) {
        int hitboxLeft = nextX + (Config.TILE_SIZE - Config.PLAYER_HITBOX_WIDTH) / 2;
        int hitboxRight = hitboxLeft + Config.PLAYER_HITBOX_WIDTH - 1;


        int hitboxTop = nextY + (Config.TILE_SIZE - Config.PLAYER_HITBOX_HEIGHT);
        int hitboxBottom = nextY + Config.TILE_SIZE - 1;


        int startCol = (hitboxLeft - Config.GRID_OFFSET_X) / Config.TILE_SIZE;
        int endCol = (hitboxRight - Config.GRID_OFFSET_X) / Config.TILE_SIZE;
        int startRow = (hitboxTop - Config.GRID_OFFSET_Y) / Config.TILE_SIZE;
        int endRow = (hitboxBottom - Config.GRID_OFFSET_Y) / Config.TILE_SIZE;

        if (startCol < 0 || endCol >= Config.GRID_WIDTH || startRow < 0 || endRow >= Config.GRID_HEIGHT) {
            return false;
        }


        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {
                int cellType = gameAreaArray[r][c];

                if (cellType == Config.CELL_INDESTRUCTIBLE_BLOCK || cellType == Config.CELL_DESTRUCTIBLE_BLOCK) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void updatePlayerMovement() {
        int currentX = player.getXCoordinate();
        int currentY = player.getYCoordinate();
        int deltaX = player.getDeltaX();
        int deltaY = player.getDeltaY();

        if (deltaX > 0) {
            player.setAction("PLAYER_RIGHT_RUNNING", 12);
        } else if (deltaX < 0) {
            player.setAction("PLAYER_LEFT_RUNNING", 12);
        } else if (deltaY > 0) {
            player.setAction("PLAYER_FRONT_RUNNING", 12);
        } else if (deltaY < 0) {
            player.setAction("PLAYER_BACK_RUNNING", 12);
        } else {
            String lastAction = player.getCurrentAction();

            if (lastAction.contains("RIGHT")) {
                player.setAction("PLAYER_RIGHT_IDLE", 16); // Frame count: 16
            } else if (lastAction.contains("LEFT")) {
                player.setAction("PLAYER_LEFT_IDLE", 16);
            } else if (lastAction.contains("BACK")) {
                player.setAction("PLAYER_BACK_IDLE", 16);
            } else {
                player.setAction("PLAYER_FRONT_IDLE", 16);
            }
        }

        player.updateAnimation();

        if (deltaX == 0 && deltaY == 0) {
            return;
        }

        int nextX = currentX + deltaX;
        if (isWalkable(nextX, currentY)) {
            nextX = checkBounds(nextX, Config.MIN_X, Config.MAX_X);
            player.setXCoordinate(nextX);
        } else {
            player.setDelta(0, deltaY);
        }

        int nextY = currentY + deltaY;
        if (isWalkable(player.getXCoordinate(), nextY)) {
            nextY = checkBounds(nextY, Config.MIN_Y, Config.MAX_Y);
            player.setYCoordinate(nextY);
        } else {
            player.setDelta(player.getDeltaX(), 0);
        }
    }


    @Override
    public void PlaceBomb() {

    }

    @Override
    public void setPlayerDelta(int dx, int dy) {
        this.player.setDelta(dx, dy);
    }

    @Override
    public int getPlayerDeltaX() {
        return this.player.getDeltaX();
    }

    @Override
    public int getPlayerDeltaY() {
        return this.player.getDeltaY();
    }

    @Override
    public String getPlayerAction() {
        return this.player.getCurrentAction();
    }

    @Override
    public int getPlayerFrameIndex() {
        return this.player.getFrameIndex();
    }


    public static IModel getInstance() {
        if (instance == null)
            instance = new Model();
        return instance;
    }
}
