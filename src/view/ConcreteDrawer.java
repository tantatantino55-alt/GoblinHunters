package view;

import controller.ControllerForView;
import utils.Config;
import utils.SpriteManager;
import utils.TileManager;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ConcreteDrawer extends AbstractDrawer {

    private final TileManager tileManager;

    public ConcreteDrawer() {
        this.tileManager = TileManager.getInstance();
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // 1. Disegna la mappa (Sempre sotto il player)
        drawTileGrid(g2d);

        // 2. Disegna il Player animato
        drawPlayer(g2d);
    }

    private void drawTileGrid(Graphics2D g2d) {
        // ... (Codice invariato, va bene come lo hai scritto) ...
        int numRows = ControllerForView.getInstance().getNumRows();
        int numCols = ControllerForView.getInstance().getNumColumns();
        int[][] gameAreaArray = ControllerForView.getInstance().getGameAreaArray();

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int cellType = gameAreaArray[row][col];
                int tileX = Config.GRID_OFFSET_X + col * Config.TILE_SIZE;
                int tileY = Config.GRID_OFFSET_Y + row * Config.TILE_SIZE;
                BufferedImage tileImage = tileManager.getTileImage(cellType);
                if (tileImage != null) {
                    g2d.drawImage(tileImage, tileX, tileY, Config.TILE_SIZE, Config.TILE_SIZE, null);
                }
            }
        }
    }
    private void drawPlayer(Graphics2D g2d) {
        // 1. Recupero dei dati dal Controller
        String action = ControllerForView.getInstance().getPlayerAction();
        int frameIdx = ControllerForView.getInstance().getCurrentPlayerFrameIndex();

        // Assumiamo che playerX e playerY siano il TOP-LEFT della hitbox logica (64x64)
        int playerX = ControllerForView.getInstance().XCoordinatePlayer();
        int playerY = ControllerForView.getInstance().yCoordinatePlayer();

        BufferedImage playerSprite = SpriteManager.getInstance().getFrame(action, frameIdx); //

        if (playerSprite != null) {
            // --- STEP 1: TROVARE L'ANCORA LOGICA (Sulla Mappa) ---
            // Visto che partiamo dal Top-Left, l'ancora è il centro della base della hitbox.
            int anchorX = playerX + (Config.TILE_SIZE / 2); // Punto centrale X (es. +32)
            int anchorY = playerY + Config.TILE_SIZE;       // Punto alla base Y (es. +64)

            // --- STEP 2: DEFINIRE IL PIVOT DELLO SPRITE (Sull'Immagine) ---
            // Definiamo dove sono i piedi nell'immagine 128x128
            int pivotX = playerSprite.getWidth() / 2;      // Centro orizzontale (64)

            // Usiamo un moltiplicatore (0.92-0.95) per trovare la base dei piedi
            // Questo evita che il mago sembri "volare" se c'è spazio vuoto nello sheet
            int pivotY = Config.PLAYER_PIVOT_Y; //utilizzando anche editor grafico

            // --- STEP 3: CALCOLO COORDINATE DI DISEGNO ---
            // Sottraiamo il pivot del corpo dall'ancora logica del mondo
            int drawX = anchorX - pivotX;
            int drawY = anchorY - pivotY;

            // Disegno finale
            g2d.drawImage(playerSprite, drawX, drawY, null);
        } else {
            // Fallback: Rettangolo blu
            g2d.setColor(Color.BLUE);
            g2d.fillRect(playerX, playerY, Config.TILE_SIZE, Config.TILE_SIZE);
        }
    }



    @Override
    public int getDrawingWidth() {
        return Config.GRID_OFFSET_X + Config.GAME_PANEL_WIDTH;
    }

    @Override
    public int getDrawingHeight() {
        return Config.GRID_OFFSET_Y + Config.GAME_PANEL_HEIGHT;
    }
}
