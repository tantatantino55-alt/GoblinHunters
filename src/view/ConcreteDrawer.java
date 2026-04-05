package view;

import controller.ControllerForView;
import utils.Config;
import utils.PlayerState;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ConcreteDrawer extends AbstractDrawer {

    private final TileManager tileManager;
    private final SpriteManager spriteManager;
    // --- VARIABILI PER IL CALCOLO FPS ---
    private long lastFpsTime = System.currentTimeMillis();
    private int frameCount = 0;
    private int currentFPS = 0;
    private float transitionAlpha = Config.MIN_ALPHA; // Partiamo da completamente trasparente
    private boolean fadingOut = true;

    public ConcreteDrawer() {
        this.tileManager = TileManager.getInstance();
        this.spriteManager = SpriteManager.getInstance();
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getDrawingWidth(), getDrawingHeight());
        drawMap(g2d);
        drawCracks(g2d); // Crepe del Boss: overlay sul pavimento
        drawPortal(g2d); // 1. Disegna l'allarme se scopri il portale spawner
        drawLevelExitGate(g2d);
        drawDestructions(g2d); // Qui vengono disegnati i blocchi che esplodono
        drawFire(g2d);
        drawBombs(g2d);
        drawCollectibles(g2d);
        drawProjectiles(g2d);
        drawEnemies(g2d);
        drawTransition(g2d);

        // --- FIX TREMOLIO: USARE IF/ELSE ---
        PlayerState state = ControllerForView.getInstance().getPlayerState();
        if (state.name().startsWith("ATTACK")) {
            drawStaffAttack(g2d); // Gestisce i 10 frame
        } else {
            drawPlayer(g2d); // Gestisce IDLE, RUN, CAST (3 frame) e INVINCIBILITÀ
        }

        drawDebugGrid(g2d);
        drawBossHUD(g2d); // Task 4: barra HP del boss (visibile solo in zona 2)
        drawHUD(g2d);
    }

    private void drawPortal(Graphics2D g2d) {
        if (ControllerForView.getInstance().isPortalRevealed()) {
            int pCol = ControllerForView.getInstance().getPortalCol();
            int pRow = ControllerForView.getInstance().getPortalRow();

            int screenX = (pCol * Config.TILE_SIZE) + Config.GRID_OFFSET_X;
            int screenY = (pRow * Config.TILE_SIZE) + Config.GRID_OFFSET_Y;

            g2d.setColor(new Color(138, 43, 226, 180)); // Viola semi-trasparente
            g2d.fillRect(screenX, screenY, Config.TILE_SIZE, Config.TILE_SIZE);

            if (System.currentTimeMillis() % 1000 < 500) {
                g2d.setColor(Color.MAGENTA);
                g2d.drawRect(screenX + 4, screenY + 4, Config.TILE_SIZE - 8, Config.TILE_SIZE - 8);
            }
        }
    }

    // In ConcreteDrawer.java

    private void drawLevelExitGate(Graphics2D g2d) {
        if (ControllerForView.getInstance().isGateActive()) {

            // Recuperiamo le coordinate corrette (ora fisse 0, 6 tramite Controller)
            int gateCol = ControllerForView.getInstance().getExitGateCol();
            int gateRow = ControllerForView.getInstance().getExitGateRow();

            int screenX = (gateCol * Config.TILE_SIZE) + Config.GRID_OFFSET_X;
            int screenY = (gateRow * Config.TILE_SIZE) + Config.GRID_OFFSET_Y;

            // Recuperiamo il tempo di attivazione dal Controller
            long startTime = ControllerForView.getInstance().getGateActivationTime();
            long elapsed = System.currentTimeMillis() - startTime;

            int totalFrames = 6;
            int currentFrame = (int) (elapsed / 150); // Velocità animazione

            // BLOCCA L'ANIMAZIONE ALL'ULTIMO FRAME
            if (currentFrame >= totalFrames) {
                currentFrame = totalFrames - 1;
            }

            BufferedImage sprite = SpriteManager.getInstance().getSprite("PORTAL_ANIM", currentFrame);

            if (sprite != null) {
                g2d.drawImage(sprite, screenX, screenY, Config.TILE_SIZE, Config.TILE_SIZE, null);
            }
        }
    }

    private void drawHUD(Graphics2D g2d) {
        // --- 1. CALCOLO FPS ---
        frameCount++;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFpsTime >= 1000) {
            currentFPS = frameCount; // Salva i frame accumulati nell'ultimo secondo
            frameCount = 0; // Azzera il contatore
            lastFpsTime = currentTime; // Resetta il timer
        }

        // --- 2. RECUPERO DATI TRAMITE CONTROLLER ---
        int lives = controller.ControllerForView.getInstance().getPlayerLives();
        int totalSeconds = controller.ControllerForView.getInstance().getElapsedTimeInSeconds();

        // Calcolo minuti e secondi formattati (es. 01:05)
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);

        int bombAmmo = controller.ControllerForView.getInstance().getPlayerBombAmmo();
        int auraAmmo = controller.ControllerForView.getInstance().getPlayerAuraAmmo();
        boolean hasShield = controller.ControllerForView.getInstance().hasPlayerShield();
        boolean hasMaxRadius = controller.ControllerForView.getInstance().hasPlayerMaxRadius();
        boolean hasMaxSpeed = controller.ControllerForView.getInstance().hasPlayerMaxSpeed();

        // --- 3. IMPOSTAZIONI DI LAYOUT (TUTTO A DESTRA) ---
        // Calcoliamo la X saltando la griglia E la cornice (che è larga 1 TILE_SIZE)
        int panelX = utils.Config.GRID_OFFSET_X
                + (utils.Config.GRID_WIDTH * utils.Config.TILE_SIZE)
                + utils.Config.TILE_SIZE // <--- Salto della cornice!
                + 30; // Margine di respiro dalla cornice al testo

        int currentY = 60; // Punto di partenza dall'alto
        int lineGap = 30; // Spazio standard tra una riga di testo e l'altra
        int sectionGap = 20; // Spazio extra per separare le diverse sezioni

        // --- 4. DISEGNO STATISTICHE BASE (Spostate a destra!) ---
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));

        g2d.drawString("FPS: " + currentFPS, panelX, currentY);
        currentY += lineGap;
        g2d.drawString("VITE: " + lives, panelX, currentY);
        currentY += lineGap;
        g2d.drawString("TEMPO: " + timeString, panelX, currentY);

        currentY += lineGap + sectionGap;

        // --- 5. DISEGNO MUNIZIONI ---
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(Color.YELLOW);
        g2d.drawString("MUNIZIONI:", panelX, currentY);
        currentY += lineGap;

        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Bombe: " + bombAmmo, panelX, currentY);
        currentY += lineGap;
        g2d.drawString("Magia: " + auraAmmo, panelX, currentY);

        currentY += lineGap + sectionGap;

        // --- 6. DISEGNO POTENZIAMENTI ---
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(Color.CYAN);
        g2d.drawString("POTENZIAMENTI:", panelX, currentY);
        currentY += lineGap;

        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Scudo: " + (hasShield ? "ATTIVO" : "NO"), panelX, currentY);
        currentY += lineGap;
        g2d.drawString("Raggio Max: " + (hasMaxRadius ? "SI" : "NO"), panelX, currentY);
        currentY += lineGap;
        g2d.drawString("Velocità Max: " + (hasMaxSpeed ? "SI" : "NO"), panelX, currentY);
    }

    /**
     * Disegna la mappa a strati:
     * Layer 0: Pavimento (disegnato ovunque)
     * Layer 1: Oggetti (Muri distruttibili/indistruttibili)
     */
    private void drawMap(Graphics2D g2d) {
        String theme = controller.ControllerForView.getInstance().getCurrentTheme();
        view.TileManager.getInstance().setCurrentTheme(theme);
        int[][] gameAreaArray = controller.ControllerForView.getInstance().getGameAreaArray();

        BufferedImage floorImg = tileManager.getTileImage(utils.Config.CELL_EMPTY);
        BufferedImage frameImg = tileManager.getTileImage(utils.Config.THEME_FRAME_INDEX);

        // --- LAYER 0: PAVIMENTO DI BASE ---
        for (int row = 0; row < utils.Config.GRID_HEIGHT; row++) {
            for (int col = 0; col < utils.Config.GRID_WIDTH; col++) {
                int tileX = utils.Config.GRID_OFFSET_X + col * utils.Config.TILE_SIZE;
                int tileY = utils.Config.GRID_OFFSET_Y + row * utils.Config.TILE_SIZE;
                if (floorImg != null) {
                    g2d.drawImage(floorImg, tileX, tileY, utils.Config.TILE_SIZE, utils.Config.TILE_SIZE, null);
                }
            }
        }

        // --- LAYER 1: CORNICE DEL TEMA ---
        // La disegniamo PRIMA degli edifici così gli edifici ci finiscono SOPRA!
        if (frameImg != null) {
            g2d.drawImage(frameImg, utils.Config.FRAME_OFFSET_X, utils.Config.FRAME_OFFSET_Y, null);
        }

        // --- LAYER 2: MURI, PILASTRI E CASSE ---
        for (int row = 0; row < utils.Config.GRID_HEIGHT; row++) {
            for (int col = 0; col < utils.Config.GRID_WIDTH; col++) {
                int cellType = gameAreaArray[row][col];
                int tileX = utils.Config.GRID_OFFSET_X + col * utils.Config.TILE_SIZE;
                int tileY = utils.Config.GRID_OFFSET_Y + row * utils.Config.TILE_SIZE;

                // Escludiamo gli edifici (che ora usano tutti l'ID 5) e i blocchi vuoti
                if (cellType != utils.Config.CELL_EMPTY && cellType != utils.Config.CELL_ORNAMENT) {

                    // NON disegniamo nulla nella cella (0,4) e (0,9) perché ci andrà sopra
                    // l'edificio
                    if (row == 0 && (col == 4 || col == 9)) {
                        continue;
                    }

                    BufferedImage wallImg = tileManager.getTileImage(cellType);
                    if (wallImg != null) {
                        g2d.drawImage(wallImg, tileX, tileY, utils.Config.TILE_SIZE, utils.Config.TILE_SIZE, null);
                    }
                }
            }
        }

        // --- LAYER 3: EDIFICI GIGANTI (Disegnati per ultimi, sopra a tutto!) ---
        for (int row = 0; row < utils.Config.GRID_HEIGHT; row++) {
            for (int col = 0; col < utils.Config.GRID_WIDTH; col++) {
                int cellType = gameAreaArray[row][col];

                // Nella mappa, tutti i grandi edifici sono segnati con il numero 5
                // (CELL_ORNAMENT)
                if (cellType == utils.Config.CELL_ORNAMENT) {
                    int tileX = utils.Config.GRID_OFFSET_X + col * utils.Config.TILE_SIZE;

                    // Spostiamo la Y in alto di 64 pixel (- utils.Config.TILE_SIZE)
                    // In questo modo la base poggia sulla Riga 0 e il tetto copre la cornice nera!
                    int tileY = utils.Config.GRID_OFFSET_Y + row * utils.Config.TILE_SIZE - utils.Config.TILE_SIZE;

                    // MAGIA: Scegliamo COSA disegnare basandoci sul tema caricato!
                    if ("CAVE".equals(theme)) {
                        // Animazione Caverna: Calcola il frame index e sommalo a CELL_SKELETON_START
                        // (5)
                        int frameIndex = (int) ((System.currentTimeMillis() / 100)
                                % utils.Config.SKELETON_FRAMES_COUNT);
                        BufferedImage skeletonFrame = tileManager
                                .getTileImage(utils.Config.CELL_SKELETON_START + frameIndex);
                        if (skeletonFrame != null) {
                            g2d.drawImage(skeletonFrame, tileX, tileY, 128, 128, null);
                        }
                    } else {
                        // Mappe 1 e 2: Disegna l'ornamento fisso (indice 5)
                        BufferedImage ornament = tileManager.getTileImage(utils.Config.CELL_ORNAMENT);
                        if (ornament != null) {
                            g2d.drawImage(ornament, tileX, tileY, 128, 128, null);
                        }
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

        // --- MODIFICA 4: FORZARE I 3 FRAME PER IL CAST ---
        // Se lo stato è un lancio magico (CAST), ignoriamo i frame totali
        // dell'intera animazione di attacco e forziamo il ciclo solo sui primi 3 frame!
        if (state.name().startsWith("CAST")) {
            totalFrames = 3;
        }
        // --------------------------------------------------

        long timePassed = System.currentTimeMillis() - startTime;
        int currentFrame;

        if (state == PlayerState.DYING) {
            currentFrame = (int) (timePassed / Config.ANIMATION_DELAY);
            if (currentFrame >= totalFrames)
                currentFrame = totalFrames - 1;
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

    /*
     * versione 1
     * private void drawEnemies(Graphics2D g2d) {
     * int count = ControllerForView.getInstance().getEnemyCount();
     * 
     * for (int i = 0; i < count; i++) {
     * // 1. Recupero dati logici dal Controller
     * double x = ControllerForView.getInstance().getEnemyX(i);
     * double y = ControllerForView.getInstance().getEnemyY(i);
     * utils.Direction dir = ControllerForView.getInstance().getEnemyDirection(i);
     * utils.EnemyType type = ControllerForView.getInstance().getEnemyType(i);
     * 
     * // 2. Definizione del prefisso per lo SpriteManager
     * String prefix = switch (type) {
     * case COMMON -> "COMMON";
     * case HUNTER -> "HUNTER";
     * case SHOOTER -> "SHOOTER";
     * default -> "COMMON";
     * };
     * 
     * // 3. Gestione degli stati e delle animazioni
     * String state = "RUN";
     * int frames = Config.GOBLIN_RUN_FRAMES;
     * 
     * // Se lo Shooter sta mirando (telegraph != null), usiamo l'animazione di
     * attacco
     * if (type == utils.EnemyType.SHOOTER &&
     * ControllerForView.getInstance().getEnemyTelegraph(i) != null) {
     * state = "ATTACK";
     * frames = Config.SHOOTER_ATTACK_FRAMES;
     * }
     * 
     * // Calcolo del frame corrente (velocità 80ms)
     * int currentFrame = (int) (System.currentTimeMillis() / 80) % frames;
     * String spriteKey = prefix + "_" + state + "_" + dir.name();
     * 
     * // 4. Recupero dello sprite caricato
     * BufferedImage sprite = SpriteManager.getInstance().getSprite(spriteKey,
     * currentFrame);
     * 
     * if (sprite != null) {
     * // 5. CALCOLO COORDINATE SCHERMO (Stessa logica del Player)
     * // Convertiamo la posizione logica in pixel aggiungendo l'offset della
     * griglia
     * int screenX = (int) (x * Config.TILE_SIZE) + Config.GRID_OFFSET_X;
     * int screenY = (int) (y * Config.TILE_SIZE) + Config.GRID_OFFSET_Y;
     * 
     * // 6. CENTRAMENTO E ALLINEAMENTO (Sprite 128x128 su Tile 64x64)
     * // Centriamo orizzontalmente rispetto alla tile
     * int drawX = screenX + (Config.TILE_SIZE - 128) / 2;
     * 
     * // Allineiamo i piedi alla base della tile (screenY + 64 - 128)
     * int drawY = screenY + (Config.TILE_SIZE - 128);
     * 
     * // Disegno finale dello sprite
     * g2d.drawImage(sprite, drawX, drawY, 128, 128, null);
     * }
     * }
     * }
     */

    private void drawEnemies(Graphics2D g2d) {
        int count = controller.ControllerForView.getInstance().getEnemyCount();

        for (int i = 0; i < count; i++) {
            double x = controller.ControllerForView.getInstance().getEnemyX(i);
            double y = controller.ControllerForView.getInstance().getEnemyY(i);
            utils.Direction dir = controller.ControllerForView.getInstance().getEnemyDirection(i);
            utils.EnemyType type = controller.ControllerForView.getInstance().getEnemyType(i);

            // --- FLICKERING (Lampeggio se in I-Frames) ---
            if (controller.ControllerForView.getInstance().isEnemyInvincible(i)) {
                if ((System.currentTimeMillis() / utils.Config.FLICKER_DELAY_MS) % 2 == 0) {
                    continue; // Salta il disegno = invisibile questo frame
                }
            }

            String prefix = switch (type) {
                case COMMON -> "COMMON";
                case HUNTER -> "HUNTER";
                case SHOOTER -> "SHOOTER";
                case BOSS -> "BOSS";
                default -> "COMMON";
            };

            // LEGGIAMO IL VERO STATO DAL MODEL!
            String state = controller.ControllerForView.getInstance().getEnemyState(i);
            int frames = utils.Config.GOBLIN_RUN_FRAMES;

            // TASK 1 + 3: Mappatura stati Boss → chiave animazione
            if (type == utils.EnemyType.BOSS) {
                switch (state) {
                    case "FURY", "EXHAUSTED" -> {
                        state = "RUN";
                        frames = utils.Config.BOSS_RUN_FRAMES;
                    }
                    case "TELEGRAPH" -> {
                        // Task 1: TELEGRAPH mostra l'animazione ATTACK (caricamento)
                        state = "ATTACK";
                        frames = utils.Config.BOSS_ATTACK_FRAMES;
                    }
                    case "IDLE_EXHAUSTED" -> {
                        // Task 3: IDLE_EXHAUSTED mostra l'animazione IDLE (riposo)
                        state = "IDLE";
                        frames = utils.Config.BOSS_IDLE_FRAMES;
                    }
                    case "DYING" -> frames = utils.Config.BOSS_DYING_FRAMES;
                    // default (RUN, IDLE, ATTACK già corretti) → nessun override
                }
            }

            // Calcolo del frame corrente
            int currentFrame = 0;
            if (state.equals("DYING")) {
                // Animazione di morte lenta e progressiva (non ciclica)
                long timePassed = System.currentTimeMillis()
                        - ControllerForView.getInstance().getEnemyStateStartTime(i);
                currentFrame = (int) (timePassed / 150); // Più lenta per godersela
                if (currentFrame >= frames) {
                    currentFrame = frames - 1; // SI FERMA SULL'ULTIMO FRAME!
                }
            } else {
                currentFrame = (int) (System.currentTimeMillis() / 80) % frames;
            }

            // Se è DYING, la sprite sheet non ha la direzione (BOSS_DYING)
            String spriteKey = prefix + "_" + state + (state.equals("DYING") ? "" : "_" + dir.name());
            java.awt.image.BufferedImage sprite = view.SpriteManager.getInstance().getSprite(spriteKey, currentFrame);

            // Fallback
            if (sprite == null && state.equals("IDLE")) {
                spriteKey = prefix + "_RUN_" + dir.name();
                sprite = view.SpriteManager.getInstance().getSprite(spriteKey, 0);
            }

            if (sprite != null) {
                int screenX = (int) (x * utils.Config.TILE_SIZE) + utils.Config.GRID_OFFSET_X;
                int screenY = (int) (y * utils.Config.TILE_SIZE) + utils.Config.GRID_OFFSET_Y;

                if (type == utils.EnemyType.BOSS) {
                    // --- CALCOLO PIVOT BOSS ---
                    int drawX = (screenX + 32) - 96;
                    int drawY = (screenY + 64) - 149;
                    g2d.drawImage(sprite, drawX, drawY, utils.Config.BOSS_FRAME_SIZE, utils.Config.BOSS_FRAME_SIZE,
                            null);
                } else {
                    int drawX = screenX + (utils.Config.TILE_SIZE - 128) / 2;
                    int drawY = screenY + (utils.Config.TILE_SIZE - 128);
                    g2d.drawImage(sprite, drawX, drawY, 128, 128, null);
                }
            }
        }
    }

    /**
     * Disegna le crepe del Boss come overlay sopra il pavimento normale.
     * Usa la tile CAVE_CRACKED_FLOOR (indice 3) con un'alfa oscillante
     * per evidenziare il pericolo senza oscurare i nemici/player disegnati dopo.
     */
    private void drawCracks(Graphics2D g2d) {
        int count = controller.ControllerForView.getInstance().getCrackCount();
        if (count == 0)
            return;

        // Recupera la tile crepata (indice Config.CELL_CRACKED_FLOOR = 3)
        BufferedImage crackTile = tileManager.getTileImage(utils.Config.CELL_CRACKED_FLOOR);

        // Alpha pulsante (tra 0.55 e 0.85) per dare un effetto "vivo" al pericolo
        float pulse = 0.55f + 0.30f * (float) Math.abs(Math.sin(System.currentTimeMillis() / 400.0));
        Composite originalComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulse));

        for (int i = 0; i < count; i++) {
            int row = controller.ControllerForView.getInstance().getCrackRow(i);
            int col = controller.ControllerForView.getInstance().getCrackCol(i);

            int screenX = utils.Config.GRID_OFFSET_X + col * utils.Config.TILE_SIZE;
            int screenY = utils.Config.GRID_OFFSET_Y + row * utils.Config.TILE_SIZE;

            if (crackTile != null) {
                g2d.drawImage(crackTile, screenX, screenY, utils.Config.TILE_SIZE, utils.Config.TILE_SIZE, null);
            } else {
                // Fallback grafico: rettangolo arancione semi-trasparente
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
                g2d.setColor(new Color(220, 80, 0));
                g2d.fillRect(screenX + 4, screenY + 4,
                        utils.Config.TILE_SIZE - 8, utils.Config.TILE_SIZE - 8);
            }
        }

        // Ripristina il composite originale per non influenzare il disegno successivo
        g2d.setComposite(originalComposite);
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

    private void drawBombs(Graphics2D g2d) {
        int count = ControllerForView.getInstance().getBombCount();

        for (int i = 0; i < count; i++) {
            int row = ControllerForView.getInstance().getBombRow(i);
            int col = ControllerForView.getInstance().getBombCol(i);
            int elapsedTime = ControllerForView.getInstance().getBombElapsedTime(i);

            int currentFrame = (elapsedTime / Config.BOMB_ANIM_FRAME_DURATION) % Config.BOMB_FRAMES;
            int screenX = Config.GRID_OFFSET_X + col * Config.TILE_SIZE;
            int screenY = Config.GRID_OFFSET_Y + row * Config.TILE_SIZE;

            BufferedImage sprite = SpriteManager.getInstance().getSprite("BOMB_ANIM", currentFrame);
            if (sprite != null) {
                g2d.drawImage(sprite, screenX, screenY, null);
            }
        }
    }

    private void drawDestructions(Graphics2D g2d) {
        int count = ControllerForView.getInstance().getDestructionCount();

        for (int i = 0; i < count; i++) {
            int row = ControllerForView.getInstance().getDestructionRow(i);
            int col = ControllerForView.getInstance().getDestructionCol(i);
            int elapsed = ControllerForView.getInstance().getDestructionElapsedTime(i);

            int currentFrame = elapsed / Config.DESTRUCTION_FRAME_DURATION;
            if (currentFrame >= Config.DESTRUCTION_FRAMES)
                currentFrame = Config.DESTRUCTION_FRAMES - 1;

            int screenX = Config.GRID_OFFSET_X + col * Config.TILE_SIZE;
            int screenY = Config.GRID_OFFSET_Y + row * Config.TILE_SIZE;

            BufferedImage sprite = SpriteManager.getInstance().getSprite("CRATE_BREAK", currentFrame);
            if (sprite != null) {
                g2d.drawImage(sprite, screenX, screenY, Config.TILE_SIZE, Config.TILE_SIZE, null);
            }
        }
    }

    private void drawFire(Graphics2D g2d) {
        int count = ControllerForView.getInstance().getFireCount();

        for (int i = 0; i < count; i++) {
            int r = ControllerForView.getInstance().getFireRow(i);
            int col = ControllerForView.getInstance().getFireCol(i);
            int type = ControllerForView.getInstance().getFireType(i);

            int x = Config.GRID_OFFSET_X + col * Config.TILE_SIZE;
            int y = Config.GRID_OFFSET_Y + r * Config.TILE_SIZE;

            BufferedImage img = SpriteManager.getInstance().getSprite("FIRE_" + type, 0);

            if (img != null) {
                g2d.drawImage(img, x, y, Config.TILE_SIZE, Config.TILE_SIZE, null);
            } else {
                g2d.setColor(Color.RED);
                g2d.drawRect(x, y, Config.TILE_SIZE, Config.TILE_SIZE);
            }
        }
    }

    private void drawProjectiles(Graphics2D g2d) {
        int count = ControllerForView.getInstance().getProjectileCount();

        for (int i = 0; i < count; i++) {
            double x = ControllerForView.getInstance().getProjectileX(i);
            double y = ControllerForView.getInstance().getProjectileY(i);
            boolean isEnemy = ControllerForView.getInstance().isProjectileEnemy(i);
            int dirOrdinal = ControllerForView.getInstance().getProjectileDirection(i);

            String key = null;

            if (isEnemy) {
                switch (dirOrdinal) {
                    case 0 -> key = "BONE_UP";
                    case 1 -> key = "BONE_DOWN";
                    case 2 -> key = "BONE_LEFT";
                    case 3 -> key = "BONE_RIGHT";
                }
            } else {
                switch (dirOrdinal) {
                    case 0 -> key = "AURA_UP";
                    case 1 -> key = "AURA_RIGHT";
                    case 2 -> key = "AURA_LEFT";
                    case 3 -> key = "AURA_DOWN";
                }
            }

            if (key != null) {
                BufferedImage sprite = SpriteManager.getInstance().getSprite(key, 0);

                if (sprite != null) {
                    // Poiché l'immagine PNG è già 64x64 con il proiettile centrato,
                    // calcoliamo solo l'angolo in alto a sinistra della cella.
                    int screenX = (int) (x * Config.TILE_SIZE) + Config.GRID_OFFSET_X;
                    int screenY = (int) (y * Config.TILE_SIZE) + Config.GRID_OFFSET_Y;

                    // Disegniamo l'immagine a grandezza naturale (64x64) senza alcun offset!
                    g2d.drawImage(sprite, screenX, screenY, Config.TILE_SIZE, Config.TILE_SIZE, null);
                }
            }
        }
    }

    private void drawStaffAttack(Graphics2D g2d) {
        PlayerState state = ControllerForView.getInstance().getPlayerState();
        double logX = ControllerForView.getInstance().getXCoordinatePlayer();
        double logY = ControllerForView.getInstance().getYCoordinatePlayer();
        long startTime = ControllerForView.getInstance().getPlayerStateStartTime();

        int totalFrames = 10;
        long timePassed = System.currentTimeMillis() - startTime;
        int currentFrame = (int) (timePassed / Config.ANIMATION_DELAY_STAFF_ATTACK);

        if (currentFrame >= totalFrames) {
            ControllerForView.getInstance().resetPlayerStateAfterAction();
            return;
        }

        BufferedImage sprite = spriteManager.getSprite(state, currentFrame);
        if (sprite != null) {
            // --- FIX COORDINATE: Usa la stessa logica di drawPlayer ---
            int screenX = (int) (logX * Config.TILE_SIZE) + Config.GRID_OFFSET_X;
            int screenY = (int) (logY * Config.TILE_SIZE) + Config.GRID_OFFSET_Y;

            int drawX = screenX + (Config.TILE_SIZE - Config.ENTITY_FRAME_SIZE) / 2;
            int drawY = screenY + (Config.TILE_SIZE - Config.ENTITY_FRAME_SIZE);

            g2d.drawImage(sprite, drawX, drawY, Config.ENTITY_FRAME_SIZE, Config.ENTITY_FRAME_SIZE, null);
        }
    }

    public void drawTransition(Graphics2D g2d) {

        // --- 1. AGGIORNAMENTO TRASPARENZA ---
        if (ControllerForView.getInstance().isTransitioning()) {
            if (transitionAlpha < Config.MAX_ALPHA) {
                transitionAlpha += Config.FADE_SPEED;
                if (transitionAlpha > Config.MAX_ALPHA)
                    transitionAlpha = Config.MAX_ALPHA;
            }
        } else {
            if (transitionAlpha > Config.MIN_ALPHA) {
                transitionAlpha -= Config.FADE_SPEED;
                if (transitionAlpha < Config.MIN_ALPHA)
                    transitionAlpha = Config.MIN_ALPHA;
            }
        }

        // --- 2. DISEGNO DEL RETTANGOLO ---
        if (transitionAlpha > Config.MIN_ALPHA) {
            g2d.setColor(new Color(0.0f, 0.0f, 0.0f, transitionAlpha));

            // Prendi larghezza e altezza direttamente dal Config!
            g2d.fillRect(0, 0, Config.WINDOW_PREFERRED_WIDTH, Config.WINDOW_PREFERRED_HEIGHT);
        }
    }

    /**
     * Disegna l'effetto di transizione.
     * Da chiamare come ULTIMA istruzione nel tuo metodo di rendering principale.
     */

    // @Override public int getDrawingWidth() { return Config.GRID_OFFSET_X + 960
    // }//Config.GAME_PANEL_WIDTH; }
    // @Override public int getDrawingHeight() { return Config.GRID_OFFSET_Y +932;}
    // //Config.GAME_PANEL_HEIGHT; }

    @Override
    public int getDrawingWidth() {
        return Config.WINDOW_PREFERRED_WIDTH;
    }

    @Override
    public int getDrawingHeight() {
        return Config.WINDOW_PREFERRED_HEIGHT;
    }

    private int getFramesForState(PlayerState state) {
        String s = state.name();
        if (s.contains("ATTACK") || s.contains("HURT") || s.contains("DYING"))
            return Config.PLAYER_ATTACK_FRAMES;
        else if (s.contains("RUN"))
            return Config.PLAYER_RUN_FRAMES;
        else
            return Config.PLAYER_IDLE_FRAMES;
    }

    private void drawCollectibles(Graphics2D g2d) {
        int count = controller.ControllerForView.getInstance().getCollectibleCount();
        view.SpriteManager sm = view.SpriteManager.getInstance(); // Recuperiamo lo SpriteManager

        for (int i = 0; i < count; i++) {
            // Calcolo delle coordinate mantenuto identico al tuo
            int screenX = (int) (controller.ControllerForView.getInstance().getCollectibleX(i)
                    * utils.Config.TILE_SIZE) + utils.Config.GRID_OFFSET_X;
            int screenY = (int) (controller.ControllerForView.getInstance().getCollectibleY(i)
                    * utils.Config.TILE_SIZE) + utils.Config.GRID_OFFSET_Y;

            utils.ItemType type = controller.ControllerForView.getInstance().getCollectibleType(i);

            java.awt.image.BufferedImage sprite = null;

            // Associamo ogni ItemType allo sprite corretto caricato nel ResourceLoader
            switch (type) {
                case AMMO_BOMB -> sprite = sm.getSprite("CONSUMABLES", 0);
                case AMMO_AURA -> sprite = sm.getSprite("CONSUMABLES", 1);
                case POWER_SHIELD -> sprite = sm.getSprite("POWER_UPS", 0);
                case POWER_RADIUS -> sprite = sm.getSprite("POWER_UPS", 1);
                case POWER_SPEED -> sprite = sm.getSprite("POWER_UPS", 2);
            }

            // Se lo sprite è stato trovato, lo disegniamo sulla mappa
            if (sprite != null) {
                // Disegniamo lo sprite forzandolo alla dimensione del TILE_SIZE
                g2d.drawImage(sprite, screenX, screenY, utils.Config.TILE_SIZE, utils.Config.TILE_SIZE, null);
            }
        }
    }

    /**
     * TASK 4 – Disegna la barra HP del Boss centrata in basso.
     * Visibile solo in Zona 2 (arena boss) e solo se il Boss è ancora vivo.
     */
    private void drawBossHUD(Graphics2D g2d) {
        // Visibile solo in zona 2
        if (controller.ControllerForView.getInstance().getCurrentTheme() == null) return;

        int bossHP    = controller.ControllerForView.getInstance().getBossHP();
        int bossMaxHP = controller.ControllerForView.getInstance().getBossMaxHP();
        if (bossMaxHP <= 0 || bossHP <= 0) return; // Nessun boss attivo o già sconfitto

        // --- Dimensioni e posizione della barra ---
        int barW    = 500;   // larghezza totale
        int barH    = 22;    // altezza barra
        int barX    = (utils.Config.WINDOW_PREFERRED_WIDTH - barW) / 2;
        int barY    = utils.Config.WINDOW_PREFERRED_HEIGHT - 55; // 55 px dal basso

        float ratio = Math.max(0f, Math.min(1f, (float) bossHP / bossMaxHP));

        // 1. Sfondo scuro (bordo)
        g2d.setColor(new Color(10, 10, 10, 210));
        g2d.fillRoundRect(barX - 4, barY - 26, barW + 8, barH + 34, 12, 12);

        // 2. Etichetta "GOBLIN BOSS"
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        java.awt.FontMetrics fm = g2d.getFontMetrics();
        String label = "GOBLIN BOSS  " + bossHP + " / " + bossMaxHP;
        int labelX = barX + (barW - fm.stringWidth(label)) / 2;
        g2d.drawString(label, labelX, barY - 6);

        // 3. Sfondo grigio della barra
        g2d.setColor(new Color(60, 60, 60));
        g2d.fillRect(barX, barY, barW, barH);

        // 4. Barra rossa proporzionale agli HP rimanenti
        //    Diventa arancione sotto il 50% (enrage) come segnale visivo
        Color barColor = (ratio > 0.5f) ? new Color(200, 30, 30) : new Color(220, 100, 0);
        g2d.setColor(barColor);
        g2d.fillRect(barX, barY, (int)(barW * ratio), barH);

        // 5. Bordo esterno della barra
        g2d.setColor(new Color(180, 20, 20));
        g2d.drawRect(barX, barY, barW, barH);
    }
}