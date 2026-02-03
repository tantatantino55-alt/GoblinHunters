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

        drawTileGrid(g2d);

        drawPlayer(g2d);
    }

    private void drawTileGrid(Graphics2D g2d) {
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
        String action = ControllerForView.getInstance().getPlayerAction();
        int frameIdx = ControllerForView.getInstance().getCurrentPlayerFrameIndex();

        int playerX = ControllerForView.getInstance().XCoordinatePlayer();
        int playerY = ControllerForView.getInstance().yCoordinatePlayer();

        BufferedImage playerSprite = SpriteManager.getInstance().getFrame(action, frameIdx);

        if (playerSprite != null) {
            int anchorX = playerX + (Config.TILE_SIZE / 2);
            int anchorY = playerY + Config.TILE_SIZE;

            int pivotX = playerSprite.getWidth() / 2;


            int pivotY = Config.PLAYER_PIVOT_Y;

            int drawX = anchorX - pivotX;
            int drawY = anchorY - pivotY;


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
