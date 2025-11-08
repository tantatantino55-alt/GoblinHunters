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
        int startY = Config.GRID_OFFSET_Y + 1 * Config.TILE_SIZE;
        this.player = new Player(startX, startY);

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

    /*
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
*/

    public boolean isWalkable (int nextX, int nextY) {
        // 1. Converte coordinate pixel assolute (incluse offset) in coordinate di griglia logica
        // Usiamo il centro del Player per una collisione più precisa (AABB)
        int gridCol = (nextX - Config.GRID_OFFSET_X + Config.TILE_SIZE / 2) / Config.TILE_SIZE;
        int gridRow = (nextY - Config.GRID_OFFSET_Y + Config.TILE_SIZE / 2) / Config.TILE_SIZE;


        // Controlli sui limiti di griglia
        if (gridCol < 0 || gridCol >= Config.GRID_WIDTH || gridRow < 0 || gridRow >= Config.GRID_HEIGHT) {
            return false;
        }

        // Verifica del contenuto della cella
        int targetCellType = gameAreaArray[gridRow][gridCol];

        // Se la cella è un blocco indistruttibile o distruttibile, non è camminabile.
        if (targetCellType == Config.CELL_INDESTRUCTIBLE_BLOCK || targetCellType == Config.CELL_DESTRUCTIBLE_BLOCK) {
            return false;
        }

        return true;
    }

    // Questo metodo controlla la collisione e aggiorna la posizione se possibile.
    @Override
    public void updatePlayerMovement() {
        int currentX = player.getXCoordinate();
        int currentY = player.getYCoordinate();
        int deltaX = player.getDeltaX();
        int deltaY = player.getDeltaY();

        if (deltaX == 0 && deltaY == 0) {
            return; // Nessun movimento richiesto
        }

        // --- Tentativo di movimento sull'asse X ---
        int nextX = currentX + deltaX;
        if (isWalkable(nextX, currentY)) {
            player.setXCoordinate(nextX);
        } else {
            // Collisione X: ferma il movimento su X
            player.setDelta(0, deltaY);
        }

        // tentativo di movimento sull'asse Y
        // Ricontrolla con la X che potrebbe essere stata aggiornata (nextX) o bloccata (currentX)
        int nextY = currentY + deltaY;
        if (isWalkable(player.getXCoordinate(), nextY)) {
            player.setYCoordinate(nextY);
        } else {
            // Collisione Y: ferma il movimento su Y
            player.setDelta(player.getDeltaX(), 0);
        }
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
