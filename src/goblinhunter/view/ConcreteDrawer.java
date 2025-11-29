package goblinhunter.view;

import goblinhunter.controller.ControllerForView;
import goblinhunter.utils.Config;
import goblinhunter.utils.TileManager;

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

        // 1. Disegna griglia di tiles
        drawTileGrid(g2d);

        // 2. Disegna player
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
        int playerX = ControllerForView.getInstance().XCoordinatePlayer();
        int playerY = ControllerForView.getInstance().yCoordinatePlayer();

        g2d.setColor(Color.BLUE);
        g2d.fillRect(playerX, playerY, Config.TILE_SIZE, Config.TILE_SIZE);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(playerX, playerY, Config.TILE_SIZE - 1, Config.TILE_SIZE - 1);
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

    /*public int getDrawingWidth() {
        return ((int)Math.round(rect.getX() + rect.getWidth() + 0.5));
    }

    @Override
    public int getDrawingHeight() {
        return ((int)Math.round(rect.getY() + rect.getHeight() + 0.5));
    }
    /*
    public int getDrawingWidth() {

    }
    public int getDrawingHeight(){

    }

    public void updateDrawing() {

    }*/