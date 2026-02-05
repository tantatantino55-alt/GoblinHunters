package view;

import controller.ControllerForView;
import utils.Config;
import utils.EnemyType;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ConcreteDrawer extends AbstractDrawer {
    private final TileManager tileManager;

    public ConcreteDrawer() { this.tileManager = TileManager.getInstance(); }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        drawTileGrid(g2d);
        drawBombs(g2d);
        drawEnemies(g2d);
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

    private void drawBombs(Graphics2D g2d) {
        // Recuperiamo la matrice leggera dal Controller
        int[][] bombsData = ControllerForView.getInstance().getActiveBombsData();

        for (int[] bomb : bombsData) {
            int row = bomb[0];
            int col = bomb[1];

            // Calcolo della proiezione: trasformiamo la cella in pixel sullo schermo
            int screenX = Config.GRID_OFFSET_X + col * Config.TILE_SIZE;
            int screenY = Config.GRID_OFFSET_Y + row * Config.TILE_SIZE;

            // PLACEHOLDER: Disegniamo un cerchio nero finché non carichiamo lo sprite
            g2d.setColor(Color.BLACK);
            g2d.fillOval(screenX + 8, screenY + 8, Config.TILE_SIZE - 16, Config.TILE_SIZE - 16);
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
        private void drawEnemies(Graphics2D g2d) {
            // Chiediamo al Controller quanti nemici ci sono
            int count = ControllerForView.getInstance().getEnemyCount();

            for (int i = 0; i < count; i++) {
                // RECUPERO DATI (Zero Coupling: Solo numeri ed Enum)
                double x = ControllerForView.getInstance().getEnemyX(i);
                double y = ControllerForView.getInstance().getEnemyY(i);
                EnemyType type = ControllerForView.getInstance().getEnemyType(i);
                // Direction dir = ControllerForView.getInstance().getEnemyDirection(i);

                // CONVERSIONE LOGICA -> PIXEL
                int screenX = (int) Math.round(x * Config.TILE_SIZE) + Config.GRID_OFFSET_X;
                int screenY = (int) Math.round(y * Config.TILE_SIZE) + Config.GRID_OFFSET_Y;

                // SCELTA COLORE IN BASE AL TIPO (Placeholder finché non mettiamo le immagini)
                switch (type) {
                    case COMMON:
                        g2d.setColor(Color.GREEN); // Goblin Comune = Verde
                        break;
                    case HUNTER:
                        g2d.setColor(Color.RED);   // Goblin Cacciatore = Rosso
                        break;
                    default:
                        g2d.setColor(Color.GRAY);
                }

                // DISEGNO
                g2d.fillRect(screenX + 16, screenY + 16, 32, 32); // Rettangolo interno
                g2d.setColor(Color.BLACK);
                g2d.drawRect(screenX + 16, screenY + 16, 32, 32); // Bordo
            }
        }

    @Override public int getDrawingWidth() { return Config.GRID_OFFSET_X + Config.GAME_PANEL_WIDTH; }
    @Override public int getDrawingHeight() { return Config.GRID_OFFSET_Y + Config.GAME_PANEL_HEIGHT; }
}