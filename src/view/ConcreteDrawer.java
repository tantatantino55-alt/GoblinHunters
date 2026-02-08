package view;

import controller.ControllerForView;
import utils.Config;
import utils.EnemyType;
import utils.PlayerState;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ConcreteDrawer extends AbstractDrawer {

    private final TileManager tileManager;
    private final SpriteManager spriteManager;

    public ConcreteDrawer() {
        this.tileManager = TileManager.getInstance();
        this.spriteManager = SpriteManager.getInstance();
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // 1. Sfondo Base (Nero di sicurezza)
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getDrawingWidth(), getDrawingHeight());

        // 2. Disegna la Mappa (Pavimento + Muri)
        drawMap(g2d);

        // 3. Disegna le Entità
        drawBombs(g2d);
        drawEnemies(g2d);
        drawPlayer(g2d);

        // 4. Debug Griglia (Disegna linee sopra tutto per controllo)
        drawDebugGrid(g2d);
    }

    /**
     * Disegna la mappa a strati:
     * Layer 0: Pavimento (disegnato ovunque)
     * Layer 1: Oggetti (Muri distruttibili/indistruttibili)
     */
    private void drawMap(Graphics2D g2d) {
        int[][] gameAreaArray = ControllerForView.getInstance().getGameAreaArray();

        // Ottimizzazione: prendiamo l'immagine del pavimento una volta sola
        BufferedImage floorImg = tileManager.getTileImage(Config.CELL_EMPTY);

        for (int row = 0; row < Config.GRID_HEIGHT; row++) {
            for (int col = 0; col < Config.GRID_WIDTH; col++) {

                // Calcolo posizione in pixel
                int tileX = Config.GRID_OFFSET_X + col * Config.TILE_SIZE;
                int tileY = Config.GRID_OFFSET_Y + row * Config.TILE_SIZE;

                // --- LAYER 0: PAVIMENTO ---
                // Disegniamo SEMPRE il pavimento sotto, così se distruggiamo un muro c'è la sabbia sotto
                if (floorImg != null) {
                    g2d.drawImage(floorImg, tileX, tileY, Config.TILE_SIZE, Config.TILE_SIZE, null);
                }

                // --- LAYER 1: OGGETTI ---
                int cellType = gameAreaArray[row][col];

                // Se la cella non è vuota (quindi è un muro), disegniamo l'oggetto sopra
                if (cellType != Config.CELL_EMPTY) {
                    BufferedImage wallImg = tileManager.getTileImage(cellType);
                    if (wallImg != null) {
                        g2d.drawImage(wallImg, tileX, tileY, Config.TILE_SIZE, Config.TILE_SIZE, null);
                    }
                }
            }
        }
    }

    private void drawPlayer(Graphics2D g2d) {
        // A. RECUPERO DATI
        PlayerState state = ControllerForView.getInstance().getPlayerState();
        double logX = ControllerForView.getInstance().getXCoordinatePlayer();
        double logY = ControllerForView.getInstance().getYCoordinatePlayer();
        long startTime = ControllerForView.getInstance().getPlayerStateStartTime();

        // B. CALCOLO FRAME ANIMAZIONE
        int totalFrames = getFramesForState(state);
        long timePassed = System.currentTimeMillis() - startTime;
        int currentFrame;

        if (state == PlayerState.DYING) {
            currentFrame = (int) (timePassed / Config.ANIMATION_DELAY);
            if (currentFrame >= totalFrames) currentFrame = totalFrames - 1;
        } else {
            currentFrame = (int) (timePassed / Config.ANIMATION_DELAY) % totalFrames;
        }

        // C. RECUPERO SPRITE
        BufferedImage sprite = spriteManager.getSprite(state, currentFrame);

        // D. DISEGNO A SCHERMO
        if (sprite != null) {
            // 1. Convertiamo coordinate logiche (es. 4.5) in pixel schermo
            int screenX = (int) (logX * Config.TILE_SIZE) + Config.GRID_OFFSET_X;
            int screenY = (int) (logY * Config.TILE_SIZE) + Config.GRID_OFFSET_Y;

            // 2. CENTRAMENTO SPRITE
            // Lo sprite del mago è grande (es. 128px) ma la casella è piccola (64px).
            // Centriamo orizzontalmente
            int drawX = screenX + (Config.TILE_SIZE - Config.PLAYER_FRAME_SIZE) / 2;

            // Allineiamo in basso (i piedi del mago devono toccare il fondo della casella)
            // Aggiungiamo un piccolo offset se necessario (+10 nel codice precedente, qui rimosso per pulizia ma riaggiungibile)
            int drawY = screenY + (Config.TILE_SIZE - Config.PLAYER_FRAME_SIZE);

            g2d.drawImage(sprite, drawX, drawY, Config.PLAYER_FRAME_SIZE, Config.PLAYER_FRAME_SIZE, null);

            // Debug Hitbox Player (commentare in produzione)
            // g2d.setColor(Color.RED);
            // g2d.drawRect(screenX, screenY, Config.TILE_SIZE, Config.TILE_SIZE);
        }
    }

    private void drawEnemies(Graphics2D g2d) {
        int count = ControllerForView.getInstance().getEnemyCount();

        for (int i = 0; i < count; i++) {
            double x = ControllerForView.getInstance().getEnemyX(i);
            double y = ControllerForView.getInstance().getEnemyY(i);
            EnemyType type = ControllerForView.getInstance().getEnemyType(i);

            int screenX = (int) Math.round(x * Config.TILE_SIZE) + Config.GRID_OFFSET_X;
            int screenY = (int) Math.round(y * Config.TILE_SIZE) + Config.GRID_OFFSET_Y;

            // PLACEHOLDER: Sostituiremo con SpriteManager.getEnemySprite(...) in futuro
            switch (type) {
                case COMMON: g2d.setColor(Color.GREEN); break;
                case SHOOTER: g2d.setColor(Color.ORANGE); break; // Era Hunter
                default: g2d.setColor(Color.GRAY);
            }
            g2d.fillRect(screenX + 10, screenY + 10, Config.TILE_SIZE - 20, Config.TILE_SIZE - 20);
        }
    }

    private void drawBombs(Graphics2D g2d) {
        int[][] bombsData = ControllerForView.getInstance().getActiveBombsData();

        for (int[] bomb : bombsData) {
            int row = bomb[0];
            int col = bomb[1];

            int screenX = Config.GRID_OFFSET_X + col * Config.TILE_SIZE;
            int screenY = Config.GRID_OFFSET_Y + row * Config.TILE_SIZE;

            // PLACEHOLDER BOMBA (Cerchio nero pulsante o statico)
            g2d.setColor(Color.BLACK);
            g2d.fillOval(screenX + 10, screenY + 10, Config.TILE_SIZE - 20, Config.TILE_SIZE - 20);
        }
    }

    // Metodo di debug puro: disegna solo le linee della griglia
    private void drawDebugGrid(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 40)); // Bianco trasparente

        // Linee Verticali
        for (int col = 0; col <= Config.GRID_WIDTH; col++) {
            int x = Config.GRID_OFFSET_X + (col * Config.TILE_SIZE);
            g2d.drawLine(x, Config.GRID_OFFSET_Y, x, Config.GRID_OFFSET_Y + (Config.GRID_HEIGHT * Config.TILE_SIZE));
        }

        // Linee Orizzontali
        for (int row = 0; row <= Config.GRID_HEIGHT; row++) {
            int y = Config.GRID_OFFSET_Y + (row * Config.TILE_SIZE);
            g2d.drawLine(Config.GRID_OFFSET_X, y, Config.GRID_OFFSET_X + (Config.GRID_WIDTH * Config.TILE_SIZE), y);
        }
    }

    @Override public int getDrawingWidth() { return Config.GRID_OFFSET_X + Config.GAME_PANEL_WIDTH; }
    @Override public int getDrawingHeight() { return Config.GRID_OFFSET_Y + Config.GAME_PANEL_HEIGHT; }

    private int getFramesForState(PlayerState state) {
        String s = state.name();
        if (s.contains("ATTACK") || s.contains("HURT") || s.contains("DYING")) return Config.PLAYER_ATTACK_FRAMES;
        else if (s.contains("RUN")) return Config.PLAYER_RUN_FRAMES;
        else return Config.PLAYER_IDLE_FRAMES;
    }
}