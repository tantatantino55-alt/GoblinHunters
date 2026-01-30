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


    //versione nuova:
    public boolean isWalkable(int nextX, int nextY) {
        // 1. DEFINIZIONE AREA DI COLLISIONE (PIEDI)
        // Calcoliamo i margini della hitbox relativi alla posizione logica 64x64
        // nextX e nextY rappresentano l'angolo Top-Left della tile logica del Player.

        // Centriamo la hitbox orizzontalmente
        int hitboxLeft = nextX + (Config.TILE_SIZE - Config.PLAYER_HITBOX_WIDTH) / 2;
        int hitboxRight = hitboxLeft + Config.PLAYER_HITBOX_WIDTH - 1;

        // Posizioniamo la hitbox alla base della tile (dove ci sono i piedi)
        int hitboxTop = nextY + (Config.TILE_SIZE - Config.PLAYER_HITBOX_HEIGHT);
        int hitboxBottom = nextY + Config.TILE_SIZE - 1;

        // 2. TRADUZIONE IN COORDINATE DI GRIGLIA
        // Troviamo quali celle della matrice sono coperte dalla hitbox dei piedi
        int startCol = (hitboxLeft - Config.GRID_OFFSET_X) / Config.TILE_SIZE;
        int endCol = (hitboxRight - Config.GRID_OFFSET_X) / Config.TILE_SIZE;
        int startRow = (hitboxTop - Config.GRID_OFFSET_Y) / Config.TILE_SIZE;
        int endRow = (hitboxBottom - Config.GRID_OFFSET_Y) / Config.TILE_SIZE;

        // 3. CONTROLLO LIMITI MAPPA
        if (startCol < 0 || endCol >= Config.GRID_WIDTH || startRow < 0 || endRow >= Config.GRID_HEIGHT) {
            return false;
        }

        // 4. VERIFICA COLLISIONI
        // Controlliamo solo le celle (solitamente 1 o 2) che i piedi stanno toccando
        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {
                int cellType = gameAreaArray[r][c];

                // Se la cella è un muro o un blocco indistruttibile
                if (cellType == Config.CELL_INDESTRUCTIBLE_BLOCK || cellType == Config.CELL_DESTRUCTIBLE_BLOCK) {
                    return false; // Collisione rilevata
                }
            }
        }

        return true; // Spazio libero per i piedi
    }





    /**
     * Aggiorna la posizione e lo stato dell'animazione del Player.
     * Gestisce le collisioni, i confini della mappa e il cambio dei frame.
     */
    @Override
    public void updatePlayerMovement() {
        int currentX = player.getXCoordinate();
        int currentY = player.getYCoordinate();
        int deltaX = player.getDeltaX();
        int deltaY = player.getDeltaY();

        // 1. GESTIONE DELL'AZIONE (ANIMAZIONE)
        // Determiniamo l'animazione corretta in base alla direzione del movimento
        if (deltaX > 0) {
            // Camminata a Destra (Frame count: 12 come da setupResources)
            player.setAction("PLAYER_RIGHT_RUNNING", 12);
        } else if (deltaX < 0) {
            // Camminata a Sinistra (Frame count: 12)
            player.setAction("PLAYER_LEFT_RUNNING", 12);
        } else if (deltaY > 0) {
            // Camminata in Avanti (Frame count: 12)
            player.setAction("PLAYER_FRONT_RUNNING", 12);
        } else if (deltaY < 0) {
            // Camminata all'Indietro (Frame count: 12)
            player.setAction("PLAYER_BACK_RUNNING", 12);
        } else {
            // LOGICA IDLE: Se i delta sono 0, il player è fermo.
            // Recuperiamo l'ultima azione per capire in che direzione deve guardare da fermo.
            String lastAction = player.getCurrentAction();

            if (lastAction.contains("RIGHT")) {
                player.setAction("PLAYER_RIGHT_IDLE", 16); // Frame count: 16
            } else if (lastAction.contains("LEFT")) {
                player.setAction("PLAYER_LEFT_IDLE", 16);
            } else if (lastAction.contains("BACK")) {
                player.setAction("PLAYER_BACK_IDLE", 16);
            } else {
                // Default: guarda in avanti
                player.setAction("PLAYER_FRONT_IDLE", 16);
            }
        }

        // 2. AVANZAMENTO DEL TEMPO DELL'ANIMAZIONE
        // Chiamiamo updateAnimation() ad ogni tick per far scorrere i frame
        player.updateAnimation();

        // 3. LOGICA DI MOVIMENTO FISICO (Se c'è movimento)
        if (deltaX == 0 && deltaY == 0) {
            return; // Salta il calcolo delle collisioni se non c'è spostamento
        }

        // --- Tentativo di movimento sull'asse X ---
        int nextX = currentX + deltaX;
        if (isWalkable(nextX, currentY)) {
            nextX = checkBounds(nextX, Config.MIN_X, Config.MAX_X);
            player.setXCoordinate(nextX);
        } else {
            player.setDelta(0, deltaY); // Collisione X: blocca asse X
        }

        // --- Tentativo di movimento sull'asse Y ---
        int nextY = currentY + deltaY;
        if (isWalkable(player.getXCoordinate(), nextY)) {
            nextY = checkBounds(nextY, Config.MIN_Y, Config.MAX_Y);
            player.setYCoordinate(nextY);
        } else {
            player.setDelta(player.getDeltaX(), 0); // Collisione Y: blocca asse Y
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
