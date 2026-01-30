package model;

import utils.Config;

public class Model implements IModel{  //---------------------------------------------------------------
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
    public Player getPlayer(){
        return this.player;
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


    // verifica con il central point
    /*
    public boolean isWalkable (int nextX, int nextY) {
        // 1. Converte coordinate pixel assolute (incluse offset) in coordinate di griglia logica
        // Usiamo il centro del Player per una collisione più precisa (AABB)
        int gridCol = (nextX - Config.GRID_OFFSET_X + Config.TILE_SIZE / 2) / Config.TILE_SIZE;
        int gridRow = (nextY - Config.GRID_OFFSET_Y + Config.TILE_SIZE / 2) / Config.TILE_SIZE;



        // Controlli sui limiti di griglia
        if (gridCol < 0 || gridCol >= Config.GRID_WIDTH  || gridRow < 0 || gridRow >= Config.GRID_HEIGHT) {
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

    */
    private int checkBounds(int next, int min, int max) {
        return Math.max(min, Math.min(max, next));
    }



    // Verifica la collisione AABB (Bounding Box) con il grid

    public boolean isWalkable (int nextX, int nextY) {

        // 1. Definisce l'area in pixel che il Player occuperà nella prossima posizione

        // Angolo Superiore Sinistro (in indice di griglia)
        int startCol = (nextX - Config.GRID_OFFSET_X) / Config.TILE_SIZE;
        int startRow = (nextY - Config.GRID_OFFSET_Y) / Config.TILE_SIZE;

        // Angolo Inferiore Destro (in indice di griglia)
        // Usiamo TILE_SIZE - 1 per assicurarci di controllare l'ultimo pixel del Player
        int endCol = (nextX - Config.GRID_OFFSET_X + Config.TILE_SIZE - 1) / Config.TILE_SIZE;
        int endRow = (nextY - Config.GRID_OFFSET_Y + Config.TILE_SIZE - 1) / Config.TILE_SIZE;

        // 2. Controllo Sicurezza Limiti Array
        if (startCol < 0 || endCol >= Config.GRID_WIDTH || startRow < 0 || endRow >= Config.GRID_HEIGHT) {
            return false;
        }

        // 3. Iterazione sulla Bounding Box del Player
        // Controlla ogni cella (max 4) occupata dalla prossima posizione del Player.
        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {

                // Re-check di sicurezza (anche se la check iniziale dovrebbe bastare)
                if (c < 0 || c >= Config.GRID_WIDTH || r < 0 || r >= Config.GRID_HEIGHT) {
                    continue;
                }

                int targetCellType = gameAreaArray[r][c];

                // Se la cella è un blocco indistruttibile (1) o distruttibile (2)
                if (targetCellType == Config.CELL_INDESTRUCTIBLE_BLOCK || targetCellType == Config.CELL_DESTRUCTIBLE_BLOCK) {
                    return false; // Collisione trovata!
                }
            }
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

        int minX = Config.MIN_X;
        int maxX = Config.MAX_X;
        int minY = Config.MIN_Y;
        int maxY = Config.MAX_Y;

        if (deltaX == 0 && deltaY == 0) {
            return; // Nessun movimento richiesto
        }

        // --- Tentativo di movimento sull'asse X ---
        int nextX = currentX + deltaX;
        if (isWalkable(nextX, currentY)) {
            nextX = checkBounds(nextX, minX, maxX);
            player.setXCoordinate(nextX);
        } else {
            // Collisione X: ferma il movimento su X
            player.setDelta(0, deltaY);
        }

        // tentativo di movimento sull'asse Y
        // Ricontrolla con la X che potrebbe essere stata aggiornata (nextX) o bloccata (currentX)
        int nextY = currentY + deltaY;
        if (isWalkable(player.getXCoordinate(), nextY)) {
            nextY = checkBounds(nextY, minY, maxY);

            player.setYCoordinate(nextY);
        } else {
            // Collisione Y: ferma il movimento su Y
            player.setDelta(player.getDeltaX(), 0);
        }
    }

    @Override
    public void PlaceBomb() {

    }

    @Override
    public void setPlayerDelta(int dx, int dy) {
        // Il Model conosce e gestisce i suoi oggetti interni (Player)
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


    public static IModel getInstance() {
        if (instance == null)
            instance = new Model();
        return instance;
    }

}
