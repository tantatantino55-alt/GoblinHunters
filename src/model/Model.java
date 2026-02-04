package model;

import utils.Config;

public class Model implements IModel {
    private static Model instance = null;
    private final int[][] gameAreaArray;
    private Player player;

    private static final int[][] testMap = {
            {0,1,0,0,0,0,0,0,0,0,0,0,0}, {0,1,0,0,0,0,0,2,0,0,0,0,0},
            {0,1,1,1,1,0,0,0,0,0,0,0,0}, {0,0,0,0,1,0,0,0,0,0,0,0,0},
            {1,1,1,0,1,0,0,0,0,0,2,0,0}, {0,0,2,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,2,0,0,0,0,0,0,0}, {0,0,2,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,2,0,0,0}, {0,0,0,0,0,2,2,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0}
    };

    private Model() {
        this.gameAreaArray = new int[Config.GRID_HEIGHT][Config.GRID_WIDTH];
        for (int i = 0; i < Config.GRID_HEIGHT; i++)
            System.arraycopy(testMap[i], 0, gameAreaArray[i], 0, Config.GRID_WIDTH);

        // Il mondo logico inizia a 0.0
        this.player = new Player(0.0, 0.0);
    }

    public boolean isWalkable(double nextX, double nextY) {
        // 1. Trasformiamo le dimensioni della Hitbox in proporzioni logiche (Unità Mondo)
        // hbW = 32/64 = 0.5 unità; hbH = 16/64 = 0.25 unità
        double hbW = (double) Config.PLAYER_HITBOX_WIDTH / Config.TILE_SIZE;
        double hbH = (double) Config.PLAYER_HITBOX_HEIGHT / Config.TILE_SIZE;

        // 2. Calcolo dei bordi della Hitbox logica
        double left = nextX + (1.0 - hbW) / 2.0;

        // Usiamo un Epsilon infinitesimale (0.0001) per la massima precisione.
        // Questo previene collisioni "fantasma" dovute a errori di calcolo dei numeri double.
        double right = left + hbW - 0.0001;

        double top = nextY + (1.0 - hbH);
        double bottom = nextY + 1.0 - 0.0001;

        // 3. Traduzione in indici di matrice (matrice discreta)
        int startCol = (int) Math.floor(left);
        int endCol = (int) Math.floor(right);
        int startRow = (int) Math.floor(top);
        int endRow = (int) Math.floor(bottom);

        // 4. Controllo bordi del mondo logico
        if (startCol < 0 || endCol >= Config.GRID_WIDTH || startRow < 0 || endRow >= Config.GRID_HEIGHT) {
            return false;
        }

        // 5. Verifica ostacoli nella matrice discreta
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
        double currentX = player.getXCoordinate();
        double currentY = player.getYCoordinate();
        double deltaX = player.getDeltaX();
        double deltaY = player.getDeltaY();

        updatePlayerAction(deltaX, deltaY);
        player.updateAnimation();

        if (deltaX == 0 && deltaY == 0) return;

        double nextX = currentX + deltaX;
        if (isWalkable(nextX, currentY)) player.setXCoordinate(nextX);

        double nextY = currentY + deltaY;
        if (isWalkable(player.getXCoordinate(), nextY)) player.setYCoordinate(nextY);
    }

    private void updatePlayerAction(double dx, double dy) {
        if (dx > 0) player.setAction("PLAYER_RIGHT_RUNNING", 12);
        else if (dx < 0) player.setAction("PLAYER_LEFT_RUNNING", 12);
        else if (dy > 0) player.setAction("PLAYER_FRONT_RUNNING", 12);
        else if (dy < 0) player.setAction("PLAYER_BACK_RUNNING", 12);
        else {
            String last = player.getCurrentAction();
            if (last.contains("RIGHT")) player.setAction("PLAYER_RIGHT_IDLE", 16);
            else if (last.contains("LEFT")) player.setAction("PLAYER_LEFT_IDLE", 16);
            else if (last.contains("BACK")) player.setAction("PLAYER_BACK_IDLE", 16);
            else player.setAction("PLAYER_FRONT_IDLE", 16);
        }
    }


    @Override
    public void setPlayerDelta(double dx, double dy) {
        // Non arrotondiamo più.
        // Il Model accetta la precisione massima del processore (64 bit).
        this.player.setDelta(dx, dy);
    }


    @Override public double xCoordinatePlayer() { return player.getXCoordinate(); }
    @Override public double yCoordinatePlayer() { return player.getYCoordinate(); }
    @Override public double getPlayerDeltaX() { return player.getDeltaX(); }
    @Override public double getPlayerDeltaY() { return player.getDeltaY(); }
    @Override public int getNumRows() { return gameAreaArray.length; }
    @Override public int getNumColumns() { return gameAreaArray[0].length; }
    @Override public int[][] getGameAreaArray() { return gameAreaArray; }
    @Override public String getPlayerAction() { return player.getCurrentAction(); }
    @Override public int getPlayerFrameIndex() { return player.getFrameIndex(); }
    @Override public void PlaceBomb() {}

    public static IModel getInstance() {
        if (instance == null) instance = new Model();
        return instance;
    }
}