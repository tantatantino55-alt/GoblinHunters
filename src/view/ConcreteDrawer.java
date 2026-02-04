package view;

import controller.ControllerForView;
import utils.Config;
import utils.SpriteManager;
import utils.TileManager;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ConcreteDrawer extends AbstractDrawer {
    private final TileManager tileManager;

    public ConcreteDrawer() { this.tileManager = TileManager.getInstance(); }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        drawTileGrid(g2d);
        drawPlayer(g2d);
    }

    private void drawTileGrid(Graphics2D g2d) {
        int[][] gameAreaArray = ControllerForView.getInstance().getGameAreaArray();
        for (int row = 0; row < Config.GRID_HEIGHT; row++) {
            for (int col = 0; col < Config.GRID_WIDTH; col++) {
                int tileX = Config.GRID_OFFSET_X + col * Config.TILE_SIZE;
                int tileY = Config.GRID_OFFSET_Y + row * Config.TILE_SIZE;
                BufferedImage img = tileManager.getTileImage(gameAreaArray[row][col]);
                if (img != null) g2d.drawImage(img, tileX, tileY, Config.TILE_SIZE, Config.TILE_SIZE, null);
            }
        }
    }

    private void drawPlayer(Graphics2D g2d) {
        String action = ControllerForView.getInstance().getPlayerAction();
        int frameIdx = ControllerForView.getInstance().getCurrentPlayerFrameIndex();
        double logX = ControllerForView.getInstance().XCoordinatePlayer();
        double logY = ControllerForView.getInstance().yCoordinatePlayer();

        BufferedImage sprite = SpriteManager.getInstance().getFrame(action, frameIdx);
        if (sprite != null) {
            int screenX = (int) Math.round(logX * Config.TILE_SIZE) + Config.GRID_OFFSET_X;
            int screenY = (int) Math.round(logY * Config.TILE_SIZE) + Config.GRID_OFFSET_Y;

            int drawX = screenX + (Config.TILE_SIZE / 2) - (sprite.getWidth() / 2);
            int drawY = screenY + Config.TILE_SIZE - Config.PLAYER_PIVOT_Y;

            g2d.drawImage(sprite, drawX, drawY, null);
        }
    }

    @Override public int getDrawingWidth() { return Config.GRID_OFFSET_X + Config.GAME_PANEL_WIDTH; }
    @Override public int getDrawingHeight() { return Config.GRID_OFFSET_Y + Config.GAME_PANEL_HEIGHT; }
}