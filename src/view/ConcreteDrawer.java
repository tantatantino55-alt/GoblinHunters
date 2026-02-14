package view;

import controller.ControllerForView;
import utils.Config;
import utils.PlayerState;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

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

        // 1. Sfondo Base (Nero di sicurezza per tutto lo schermo)
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getDrawingWidth(), getDrawingHeight());

        // 2. Disegna la Mappa (Pavimento + Muri)
        drawMap(g2d);
        drawDestructions(g2d);
        drawFire(g2d);

        // 3. Disegna le Entità
        drawBombs(g2d);
        drawEnemies(g2d);
        drawProjectiles(g2d);
        drawPlayer(g2d);

        // 4. Debug Griglia (Disegna linee sopra tutto per controllo)
        drawDebugGrid(g2d);

        // 5. Disegna l'HUD (Senza ridisegnare il rettangolo nero!)
        drawHUD(g2d);
    }

    private void drawHUD(Graphics2D g2d) {
        // 1. Recupero Dati
        int lives = ControllerForView.getInstance().getPlayerLives();
        int totalSeconds = ControllerForView.getInstance().getElapsedTimeInSeconds();

        // Calcolo minuti e secondi formattati (es. 01:05)
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);

        // 2. Impostazione Stile Testo
        g2d.setColor(Color.WHITE);
        // Usa un font base in grassetto, grandezza 20
        g2d.setFont(new Font("Arial", Font.BOLD, 20));

        // 3. Disegno a Schermo (A sinistra, nella zona di offset)
        int startX = 20; // 20 pixel dal bordo sinistro della finestra

        g2d.drawString("VITE: " + lives, startX, 100);
        g2d.drawString("TEMPO: " + timeString, startX, 150);
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
        // 1. RECUPERO STATO INVINCIBILITÀ
        boolean isInvincible = ControllerForView.getInstance().isPlayerInvincible();

        // 2. LOGICA LAMPEGGIO (Flickering)
        if (isInvincible) {
            // Alterna visibilità ogni Config.FLICKER_DELAY_MS millisecondi
            if ((System.currentTimeMillis() / Config.FLICKER_DELAY_MS) % 2 == 0) {
                return; // Salta il disegno per questo frame (effetto "invisibile")
            }
        }

        // A. RECUPERO DATI LOGICI
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
            int screenX = (int) (logX * Config.TILE_SIZE) + Config.GRID_OFFSET_X;
            int screenY = (int) (logY * Config.TILE_SIZE) + Config.GRID_OFFSET_Y;

            // Centramento orizzontale e allineamento piedi in basso
            int drawX = screenX + (Config.TILE_SIZE - Config.ENTITY_FRAME_SIZE) / 2;
            int drawY = screenY + (Config.TILE_SIZE - Config.ENTITY_FRAME_SIZE);

            g2d.drawImage(sprite, drawX, drawY, Config.ENTITY_FRAME_SIZE, Config.ENTITY_FRAME_SIZE, null);
        }
    }
    // In src/view/ConcreteDrawer.java


    private void drawEnemies(Graphics2D g2d) {
        int count = ControllerForView.getInstance().getEnemyCount();

        for (int i = 0; i < count; i++) {
            // 1. Recupero dati logici dal Controller
            double x = ControllerForView.getInstance().getEnemyX(i);
            double y = ControllerForView.getInstance().getEnemyY(i);
            utils.Direction dir = ControllerForView.getInstance().getEnemyDirection(i);
            utils.EnemyType type = ControllerForView.getInstance().getEnemyType(i);

            // 2. Definizione del prefisso per lo SpriteManager
            String prefix = switch (type) {
                case COMMON -> "COMMON";
                case HUNTER -> "HUNTER";
                case SHOOTER -> "SHOOTER";
                default -> "COMMON";
            };

            // 3. Gestione degli stati e delle animazioni
            String state = "RUN";
            int frames = Config.GOBLIN_RUN_FRAMES;

            // Se lo Shooter sta mirando (telegraph != null), usiamo l'animazione di attacco
            if (type == utils.EnemyType.SHOOTER && ControllerForView.getInstance().getEnemyTelegraph(i) != null) {
                state = "ATTACK";
                frames = Config.SHOOTER_ATTACK_FRAMES;
            }

            // Calcolo del frame corrente (velocità 80ms)
            int currentFrame = (int) (System.currentTimeMillis() / 80) % frames;
            String spriteKey = prefix + "_" + state + "_" + dir.name();

            // 4. Recupero dello sprite caricato
            BufferedImage sprite = SpriteManager.getInstance().getSprite(spriteKey, currentFrame);

            if (sprite != null) {
                // 5. CALCOLO COORDINATE SCHERMO (Stessa logica del Player)
                // Convertiamo la posizione logica in pixel aggiungendo l'offset della griglia
                int screenX = (int) (x * Config.TILE_SIZE) + Config.GRID_OFFSET_X;
                int screenY = (int) (y * Config.TILE_SIZE) + Config.GRID_OFFSET_Y;

                // 6. CENTRAMENTO E ALLINEAMENTO (Sprite 128x128 su Tile 64x64)
                // Centriamo orizzontalmente rispetto alla tile
                int drawX = screenX + (Config.TILE_SIZE - 128) / 2;

                // Allineiamo i piedi alla base della tile (screenY + 64 - 128)
                int drawY = screenY + (Config.TILE_SIZE - 128);

                // Disegno finale dello sprite
                g2d.drawImage(sprite, drawX, drawY, 128, 128, null);
            }
        }
    }


// In src/view/ConcreteDrawer.java

    private void drawBombs(Graphics2D g2d) {
        // [0]=Row, [1]=Col, [2]=Elapsed Time (ms)
        int[][] bombsData = ControllerForView.getInstance().getActiveBombsData();

        for (int[] bombInfo : bombsData) {
            int row = bombInfo[0];
            int col = bombInfo[1];
            int elapsedTime = bombInfo[2]; // Tempo trascorso in ms

            // --- CALCOLO FRAME LATO VIEW (Puro MVC) ---
            // 1. Dividiamo il tempo totale per la durata di un singolo frame
            // 2. Usiamo il modulo (%) per ciclare (0,1,2...7, 0,1...)
            int currentFrame = (elapsedTime / Config.BOMB_ANIM_FRAME_DURATION) % Config.BOMB_FRAMES;

            int screenX = Config.GRID_OFFSET_X + col * Config.TILE_SIZE;
            int screenY = Config.GRID_OFFSET_Y + row * Config.TILE_SIZE;

            // Chiediamo allo SpriteManager il frame calcolato
            BufferedImage sprite = SpriteManager.getInstance().getSprite("BOMB_ANIM", currentFrame);

            if (sprite != null) {
                g2d.drawImage(sprite, screenX, screenY, null);
            }
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
    private void drawDestructions(Graphics2D g2d) {
        // Supponendo che il controller restituisca una lista di int[] {row, col, elapsedTime}
        List<int[]> destructions = ControllerForView.getInstance().getDestructionsData();

        for (int[] d : destructions) {
            int row = d[0];
            int col = d[1];
            int elapsed = d[2];

            // Calcolo del frame:
            // Hai definito DESTRUCTION_FRAMES = 3 e DESTRUCTION_FRAME_DURATION = 150 in Config
            int currentFrame = elapsed / Config.DESTRUCTION_FRAME_DURATION;

            // Evita di andare fuori indice se il Model non ha ancora rimosso l'effetto
            if (currentFrame >= Config.DESTRUCTION_FRAMES) currentFrame = Config.DESTRUCTION_FRAMES - 1;

            int screenX = Config.GRID_OFFSET_X + col * Config.TILE_SIZE;
            int screenY = Config.GRID_OFFSET_Y + row * Config.TILE_SIZE;

            // Recupera lo sprite caricato con la chiave "CRATE_BREAK" nel ResourceLoader
            BufferedImage sprite = SpriteManager.getInstance().getSprite("CRATE_BREAK", currentFrame);

            if (sprite != null) {
                g2d.drawImage(sprite, screenX, screenY, Config.TILE_SIZE, Config.TILE_SIZE, null);
            }
        }
    }
    private void drawFire(Graphics2D g2d) {
        List<int[]> fireData = ControllerForView.getInstance().getFireData();

        // Se vuoi debuggare se il fuoco esiste
         if (!fireData.isEmpty()) System.out.println("Disegno " + fireData.size() + " fuochi");

        for (int[] f : fireData) {
            int r = f[0];
            int col = f[1];
            int type = f[2];
            // f[3] è la vita rimanente, ma non ci serve per l'animazione perché è statica.
            // Ci serve solo sapere che ESISTE nella lista.

            int x = Config.GRID_OFFSET_X + col * Config.TILE_SIZE;
            int y = Config.GRID_OFFSET_Y + r * Config.TILE_SIZE;

            // Chiediamo sempre il frame 0, perché ne abbiamo caricato solo 1
            BufferedImage img = SpriteManager.getInstance().getSprite("FIRE_" + type, 0);

            if (img != null) {
                g2d.drawImage(img, x, y, Config.TILE_SIZE, Config.TILE_SIZE, null);
            } else {
                // Quadrato rosso SOLO se hai sbagliato gli indici in ResourceLoader
                g2d.setColor(Color.RED);
                g2d.drawRect(x, y, Config.TILE_SIZE, Config.TILE_SIZE);
            }
        }
    }


    private void drawProjectiles(Graphics2D g2d) {
        // Recupera la lista di DTO dal Controller (Puro MVC)
        // [0]=x, [1]=y, [2]=type, [3]=dir(Ordinal)
        List<double[]> projectiles = ControllerForView.getInstance().getProjectilesData();

        for (double[] p : projectiles) {
            double x = p[0];
            double y = p[1];
            int type = (int) p[2];      // 0 = Goblin, 1 = Player
            int dirOrdinal = (int) p[3]; // Enum Direction convertito in int

            String key = null;

            // --- SELEZIONE SPRITE ---
            if (type == 0) { // NEMICO (OSSO)
                switch (dirOrdinal) {
                    case 0 -> key = "BONE_UP";    // Direction.UP
                    case 1 -> key = "BONE_DOWN";  // Direction.DOWN
                    case 2 -> key = "BONE_LEFT";  // Direction.LEFT
                    case 3 -> key = "BONE_RIGHT"; // Direction.RIGHT
                }
            } else if (type == 1) { // PLAYER (AURA)
                switch (dirOrdinal) {
                    case 0 -> key = "AURA_UP";
                    case 1 -> key = "AURA_DOWN";
                    case 2 -> key = "AURA_LEFT";
                    case 3 -> key = "AURA_RIGHT";
                }
            }

            // --- DISEGNO ---
            if (key != null) {
                BufferedImage sprite = SpriteManager.getInstance().getSprite(key, 0);

                // In drawProjectiles, modifica il calcolo della posizione:
                int screenX = (int)(x * Config.TILE_SIZE) + Config.GRID_OFFSET_X + (Config.TILE_SIZE / 4);
                int screenY = (int)(y * Config.TILE_SIZE) + Config.GRID_OFFSET_Y + (Config.TILE_SIZE / 4);

                if (sprite != null) {
                    g2d.drawImage(sprite, screenX, screenY, Config.TILE_SIZE, Config.TILE_SIZE, null);
                }
            }
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