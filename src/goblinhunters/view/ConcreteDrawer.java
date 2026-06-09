package goblinhunters.view;

import goblinhunters.controller.ControllerForView;
import goblinhunters.utils.Config;
import goblinhunters.utils.GameState;
import goblinhunters.utils.PlayerState;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ConcreteDrawer extends AbstractDrawer {

    private final TileManager tileManager;
    private final SpriteManager spriteManager;

    private float transitionAlpha = Config.MIN_ALPHA;
    private boolean fadingOut = true;

    public ConcreteDrawer() {
        this.tileManager = TileManager.getInstance();
        this.spriteManager = SpriteManager.getInstance();
    }

    // ==========================================================
    // Y-sorting support
    // ==========================================================

    private static class DrawableEntity {
        public final int y;
        public final Runnable drawAction;

        public DrawableEntity(int y, Runnable drawAction) {
            this.y = y;
            this.drawAction = drawAction;
        }
    }

    // ==========================================================
    // main draw dispatch
    // ==========================================================

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        if (ControllerForView.getInstance().getGameState() == GameState.MENU) {
            MenuDrawer.getInstance().draw(g2d);
            return;
        }

        drawMap(g2d);
        drawCracks(g2d);
        drawFire(g2d);
        drawPortal(g2d);
        drawLevelExitGate(g2d);

        java.util.List<DrawableEntity> sortedEntities = new java.util.ArrayList<>();

        gatherBombs(sortedEntities, g2d);
        gatherCollectibles(sortedEntities, g2d);
        gatherProjectiles(sortedEntities, g2d);
        gatherEnemies(sortedEntities, g2d);
        gatherDestructions(sortedEntities, g2d);

        PlayerState state = ControllerForView.getInstance().getPlayerState();
        if (state.name().startsWith("ATTACK")) {
            gatherStaffAttack(sortedEntities, g2d);
        } else {
            gatherPlayer(sortedEntities, g2d);
        }

        // ascending Y sort: lower Y (closer to top) drawn first
        java.util.Collections.sort(sortedEntities, java.util.Comparator.comparingInt(e -> e.y));

        for (DrawableEntity e : sortedEntities) {
            e.drawAction.run();
        }

        drawTransition(g2d);
        drawHUD(g2d);

        if (ControllerForView.getInstance().getGameState() == GameState.GAME_OVER) {
            GameOverDrawer.getInstance().draw(g2d);
        } else if (ControllerForView.getInstance().isPaused()) {
            PauseMenuDrawer.getInstance().draw(g2d,
                    ControllerForView.getInstance().getPauseController());
        }
    }

    // ==========================================================
    // portal rendering
    // ==========================================================

    private void drawPortal(Graphics2D g2d) {
        // classic portal (zones 0-1): appears when the containing block is destroyed
        if (ControllerForView.getInstance().isPortalRevealed()) {
            int pCol = ControllerForView.getInstance().getPortalCol();
            int pRow = ControllerForView.getInstance().getPortalRow();

            int screenX = (pCol * Config.TILE_SIZE) + Config.GRID_OFFSET_X;
            int screenY = (pRow * Config.TILE_SIZE) + Config.GRID_OFFSET_Y;

            g2d.setColor(new Color(138, 43, 226, 180));
            g2d.fillRect(screenX, screenY, Config.TILE_SIZE, Config.TILE_SIZE);

            if (System.currentTimeMillis() % 1000 < 500) {
                g2d.setColor(Color.MAGENTA);
                g2d.drawRect(screenX + 4, screenY + 4, Config.TILE_SIZE - 8, Config.TILE_SIZE - 8);
            }
        }

        // boss portal (zone 2): appears after the preparation phase ends
        if (ControllerForView.getInstance().isBossPortalActive()) {
            int pCol = ControllerForView.getInstance().getBossPortalCol();
            int pRow = ControllerForView.getInstance().getBossPortalRow();

            int screenX = (pCol * Config.TILE_SIZE) + Config.GRID_OFFSET_X;
            int screenY = (pRow * Config.TILE_SIZE) + Config.GRID_OFFSET_Y;

            g2d.setColor(new Color(138, 43, 226, 180));
            g2d.fillRect(screenX, screenY, Config.TILE_SIZE, Config.TILE_SIZE);

            if (System.currentTimeMillis() % 1000 < 500) {
                g2d.setColor(Color.MAGENTA);
                g2d.drawRect(screenX + 4, screenY + 4, Config.TILE_SIZE - 8, Config.TILE_SIZE - 8);
            }
        }
    }

    private void drawLevelExitGate(Graphics2D g2d) {
        if (ControllerForView.getInstance().isGateActive()) {

            int gateCol = ControllerForView.getInstance().getExitGateCol();
            int gateRow = ControllerForView.getInstance().getExitGateRow();

            int screenX = (gateCol * Config.TILE_SIZE) + Config.GRID_OFFSET_X;
            int screenY = (gateRow * Config.TILE_SIZE) + Config.GRID_OFFSET_Y;

            long startTime = ControllerForView.getInstance().getGateActivationTime();
            long elapsed = System.currentTimeMillis() - startTime;

            int totalFrames = 6;
            int currentFrame = (int) (elapsed / 150);

            if (currentFrame >= totalFrames) {
                currentFrame = totalFrames - 1;
            }

            BufferedImage sprite = SpriteManager.getInstance().getSprite("PORTAL_ANIM", currentFrame);

            if (sprite != null) {
                g2d.drawImage(sprite, screenX, screenY, Config.TILE_SIZE, Config.TILE_SIZE, null);
            }
        }
    }

    // ==========================================================
    // HUD
    // ==========================================================

    /**
     * Draws the right-side HUD: lives, timer, score, and icons for
     * consumables (bomb, aura) and power-ups (shield, radius, speed) with
     * grayscale + 50% alpha when inactive, full colour when active, and an
     * animated glow on the staff icon when usable.
     */
    private void drawHUD(Graphics2D g2d) {
        goblinhunters.controller.IControllerForView ctrl = goblinhunters.controller.ControllerForView.getInstance();
        int lives      = ctrl.getPlayerLives();
        int totalSec   = ctrl.getElapsedTimeInSeconds();
        int bombAmmo   = ctrl.getPlayerBombAmmo();
        int auraAmmo   = ctrl.getPlayerAuraAmmo();
        boolean shield = ctrl.hasPlayerShield();
        boolean radius = ctrl.hasPlayerMaxRadius();
        boolean speed  = ctrl.hasPlayerMaxSpeed();
        int score      = ctrl.getScore();

        String timeString = String.format("%02d:%02d", totalSec / 60, totalSec % 60);

        int panelX   = Config.HUD_PANEL_X;
        int panelW   = Config.HUD_PANEL_W;
        int currentY = Config.HUD_START_Y;

        float pulse = 0.75f + 0.25f * (float) Math.abs(Math.sin(System.currentTimeMillis() / 600.0));
        g2d.setColor(new Color(1.0f, 0.85f * pulse, 0.0f));
        g2d.setFont(new Font("Monospaced", Font.BOLD, 15));
        FontMetrics fmScore = g2d.getFontMetrics();
        String scoreLabel = "SCORE";
        g2d.drawString(scoreLabel, panelX + (panelW - fmScore.stringWidth(scoreLabel)) / 2, currentY);
        currentY += 18;
        g2d.setFont(new Font("Monospaced", Font.BOLD, 20));
        fmScore = g2d.getFontMetrics();
        String scoreVal = String.format("%06d", score);
        g2d.drawString(scoreVal, panelX + (panelW - fmScore.stringWidth(scoreVal)) / 2, currentY);
        currentY += 24;

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 20));
        FontMetrics fmStats = g2d.getFontMetrics();
        String livesStr = "LIVES: " + lives;
        g2d.drawString(livesStr, panelX + (panelW - fmStats.stringWidth(livesStr)) / 2, currentY);
        currentY += 18;
        String timeStr = "TIME: " + timeString;
        g2d.drawString(timeStr, panelX + (panelW - fmStats.stringWidth(timeStr)) / 2, currentY);
        currentY += 24;

        int iconSize = Config.HUD_ICON_SIZE;
        int iconGap  = Config.HUD_ICON_GAP;
        int totalConsW = iconSize + 28 + iconGap + iconSize + 28;
        int consX = panelX + (panelW - totalConsW) / 2 - 20;

        drawHudIcon(g2d, consX, currentY,
                goblinhunters.utils.ItemType.AMMO_BOMB, "HUD_FIRE_SPELL", 0,
                "HUD_FIRE_SPELL_gray",
                bombAmmo > 0, "x" + bombAmmo);

        drawHudIcon(g2d, consX + iconSize + 28 + iconGap, currentY,
                goblinhunters.utils.ItemType.AMMO_AURA, "HUD_AURA_SPELL", 0,
                "HUD_AURA_SPELL_gray",
                auraAmmo > 0, "x" + auraAmmo);
        currentY += iconSize + 12;

        int puSize = Config.HUD_POWER_SIZE;
        int puGap  = Config.HUD_POWER_GAP;
        int totalPuW = puSize * 3 + puGap * 2;
        int puX = panelX + (panelW - totalPuW) / 2;

        drawHudIcon(g2d, puX, currentY,
                goblinhunters.utils.ItemType.POWER_SHIELD, "POWER_UPS", 0,
                "POWER_UPS_0_gray",
                shield, null);
        puX += puSize + puGap;

        drawHudIcon(g2d, puX, currentY,
                goblinhunters.utils.ItemType.POWER_RADIUS, "POWER_UPS", 1,
                "POWER_UPS_1_gray",
                radius, null);
        puX += puSize + puGap;

        drawHudIcon(g2d, puX, currentY,
                goblinhunters.utils.ItemType.POWER_SPEED, "POWER_UPS", 2,
                "POWER_UPS_2_gray",
                speed, null);
        currentY += puSize + 14;

        boolean staffUsable = goblinhunters.controller.ControllerForView.getInstance().isStaffUsable();
        BufferedImage staffImg = spriteManager.getSprite("STAFF_ICON", 0);
        if (staffImg != null) {
            final int STAFF_SIZE = Config.HUD_STAFF_SIZE;
            int staffX = panelX + (panelW - STAFF_SIZE) / 2;

            Composite originalComp = g2d.getComposite();
            if (staffUsable) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                float glowPulse = 0.5f + 0.5f * (float) Math.abs(Math.sin(System.currentTimeMillis() / 400.0));
                g2d.setColor(new Color(1.0f, 0.85f, 0.0f, 0.25f * glowPulse));
                g2d.fillRoundRect(staffX - 4, currentY - 4, STAFF_SIZE + 8, STAFF_SIZE + 8, 8, 8);
            } else {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.30f));
            }
            g2d.drawImage(staffImg, staffX, currentY, STAFF_SIZE, STAFF_SIZE, null);
            g2d.setComposite(originalComp);

            if (staffUsable) {
                g2d.setFont(new Font("Monospaced", Font.BOLD, 11));
                g2d.setColor(new Color(255, 215, 0));
                FontMetrics fmStaff = g2d.getFontMetrics();
                String label = "[Z] Staff";
                g2d.drawString(label, panelX + (panelW - fmStaff.stringWidth(label)) / 2, currentY + STAFF_SIZE + 12);
            }
        }
    }

    /**
     * Draws a single HUD icon with active/inactive visual state.
     *
     * @param g2d        graphics context
     * @param x          top-left X of the icon area
     * @param y          top-left Y of the icon area
     * @param itemType   item type (used for HudItemAnimator)
     * @param colorKey   SpriteManager key for the colour version
     * @param colorFrame frame index for the colour version
     * @param grayKey    grayscale cache key
     * @param active     true = full colour and opacity, false = grayscale at 50%
     * @param counter    counter string (e.g. "x3"), or null if not needed
     */
    private void drawHudIcon(Graphics2D g2d,
                              int x, int y,
                              goblinhunters.utils.ItemType itemType,
                              String colorKey, int colorFrame,
                              String grayKey,
                              boolean active,
                              String counter) {

        final int BASE_SIZE = Config.HUD_ICON_BASE_SIZE;

        BufferedImage img;
        Composite originalComposite = g2d.getComposite();

        if (active) {
            img = spriteManager.getSprite(colorKey, colorFrame);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        } else {
            img = spriteManager.getGrayscale(grayKey);
            if (img == null) img = spriteManager.getSprite(colorKey, colorFrame); // fallback
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f));
        }

        int drawSize = BASE_SIZE;

        // sprites in items.png are not perfectly centred — manual offsets align them to their text counters
        int imgOffsetX  = 0;
        int imgOffsetY  = 0;
        int textOffsetX = 0;

        if ("HUD_FIRE_SPELL".equals(colorKey)) {
            imgOffsetX = 6;
            imgOffsetY = 8;
            textOffsetX = 2;
        } else if ("HUD_AURA_SPELL".equals(colorKey)) {
            imgOffsetX = 8;
            imgOffsetY = 4;
            textOffsetX = 6;
        }

        if (img != null) {
            g2d.drawImage(img, x + imgOffsetX, y + imgOffsetY, drawSize, drawSize, null);
        }

        g2d.setComposite(originalComposite);

        if (counter != null) {
            int textX = x + BASE_SIZE + 4 + textOffsetX;

            if (active) {
                g2d.setFont(new Font("Arial", Font.BOLD, 15));
                g2d.setColor(Color.WHITE);
            } else {
                g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                g2d.setColor(new Color(140, 140, 140));
            }
            FontMetrics fm = g2d.getFontMetrics();
            int correctedTextY = y + (BASE_SIZE / 2) + (fm.getAscent() / 2) - 2;
            g2d.drawString(counter, textX, correctedTextY);
        }
    }

    // ==========================================================
    // map rendering
    // ==========================================================

    /**
     * Draws the map in four ordered passes:
     * 0 — floor tiles, 1 — theme frame, 2 — walls/pillars/crates, 3 — large building ornaments.
     */
    private void drawMap(Graphics2D g2d) {
        String theme = goblinhunters.controller.ControllerForView.getInstance().getCurrentTheme();
        goblinhunters.view.TileManager.getInstance().setCurrentTheme(theme);
        int[][] gameAreaArray = goblinhunters.controller.ControllerForView.getInstance().getGameAreaArray();

        BufferedImage floorImg = tileManager.getTileImage(goblinhunters.utils.Config.CELL_EMPTY);
        BufferedImage frameImg = tileManager.getTileImage(goblinhunters.utils.Config.THEME_FRAME_INDEX);

        // pass 0: floor
        for (int row = 0; row < goblinhunters.utils.Config.GRID_HEIGHT; row++) {
            for (int col = 0; col < goblinhunters.utils.Config.GRID_WIDTH; col++) {
                int tileX = goblinhunters.utils.Config.GRID_OFFSET_X + col * goblinhunters.utils.Config.TILE_SIZE;
                int tileY = goblinhunters.utils.Config.GRID_OFFSET_Y + row * goblinhunters.utils.Config.TILE_SIZE;
                if (floorImg != null) {
                    g2d.drawImage(floorImg, tileX, tileY, goblinhunters.utils.Config.TILE_SIZE, goblinhunters.utils.Config.TILE_SIZE, null);
                }
            }
        }

        // pass 1: theme frame — drawn before buildings so buildings render on top
        if (frameImg != null) {
            g2d.drawImage(frameImg, goblinhunters.utils.Config.FRAME_OFFSET_X, goblinhunters.utils.Config.FRAME_OFFSET_Y, null);
        }

        // pass 2: walls, pillars, crates
        for (int row = 0; row < goblinhunters.utils.Config.GRID_HEIGHT; row++) {
            for (int col = 0; col < goblinhunters.utils.Config.GRID_WIDTH; col++) {
                int cellType = gameAreaArray[row][col];
                int tileX = goblinhunters.utils.Config.GRID_OFFSET_X + col * goblinhunters.utils.Config.TILE_SIZE;
                int tileY = goblinhunters.utils.Config.GRID_OFFSET_Y + row * goblinhunters.utils.Config.TILE_SIZE;

                if (cellType != goblinhunters.utils.Config.CELL_EMPTY && cellType != goblinhunters.utils.Config.CELL_ORNAMENT) {
                    // cells (0,4) and (0,9) are covered by building ornaments drawn in pass 3
                    if (row == 0 && (col == 4 || col == 9)) {
                        continue;
                    }

                    BufferedImage wallImg = tileManager.getTileImage(cellType);
                    if (wallImg != null) {
                        g2d.drawImage(wallImg, tileX, tileY, goblinhunters.utils.Config.TILE_SIZE, goblinhunters.utils.Config.TILE_SIZE, null);
                    }
                }
            }
        }

        // pass 3: large 2×2 building ornaments (CELL_ORNAMENT = 5)
        for (int row = 0; row < goblinhunters.utils.Config.GRID_HEIGHT; row++) {
            for (int col = 0; col < goblinhunters.utils.Config.GRID_WIDTH; col++) {
                int cellType = gameAreaArray[row][col];

                if (cellType == goblinhunters.utils.Config.CELL_ORNAMENT) {
                    int tileX = goblinhunters.utils.Config.GRID_OFFSET_X + col * goblinhunters.utils.Config.TILE_SIZE;
                    // shift up one tile so the base sits on row 0 and the top covers the frame border
                    int tileY = goblinhunters.utils.Config.GRID_OFFSET_Y + row * goblinhunters.utils.Config.TILE_SIZE - goblinhunters.utils.Config.TILE_SIZE;

                    if ("CAVE".equals(theme)) {
                        int frameIndex = (int) ((System.currentTimeMillis() / 100)
                                % goblinhunters.utils.Config.SKELETON_FRAMES_COUNT);
                        BufferedImage skeletonFrame = tileManager
                                .getTileImage(goblinhunters.utils.ViewConfig.CELL_SKELETON_START + frameIndex);
                        if (skeletonFrame != null) {
                            g2d.drawImage(skeletonFrame, tileX, tileY, 128, 128, null);
                        }
                    } else {
                        BufferedImage ornament = tileManager.getTileImage(goblinhunters.utils.Config.CELL_ORNAMENT);
                        if (ornament != null) {
                            g2d.drawImage(ornament, tileX, tileY, 128, 128, null);
                        }
                    }
                }
            }
        }
    }

    // ==========================================================
    // entity gathering (Y-sorted layer)
    // ==========================================================

    private void gatherPlayer(java.util.List<DrawableEntity> entities, Graphics2D g2d) {
        boolean isInvincible = ControllerForView.getInstance().isPlayerInvincible();

        if (isInvincible) {
            if ((System.currentTimeMillis() / Config.FLICKER_DELAY_MS) % 2 == 0) {
                return;
            }
        }

        PlayerState state = ControllerForView.getInstance().getPlayerState();
        double logX = ControllerForView.getInstance().getXCoordinatePlayer();
        double logY = ControllerForView.getInstance().getYCoordinatePlayer();
        long startTime = ControllerForView.getInstance().getPlayerStateStartTime();

        int totalFrames = getFramesForState(state);

        // CAST states use only the first 3 frames, not the full attack frame count
        if (state.name().startsWith("CAST")) {
            totalFrames = 3;
        }

        long timePassed = System.currentTimeMillis() - startTime;
        int currentFrame;

        if (state == PlayerState.DYING) {
            currentFrame = (int) (timePassed / Config.ANIMATION_DELAY);
            if (currentFrame >= totalFrames)
                currentFrame = totalFrames - 1;
        } else {
            currentFrame = (int) (timePassed / Config.ANIMATION_DELAY) % totalFrames;
        }

        BufferedImage sprite = spriteManager.getSprite(state, currentFrame);

        if (sprite != null) {
            int screenX = (int) (logX * Config.TILE_SIZE) + Config.GRID_OFFSET_X;
            int screenY = (int) (logY * Config.TILE_SIZE) + Config.GRID_OFFSET_Y;

            int drawX = screenX + (Config.TILE_SIZE - Config.ENTITY_FRAME_SIZE) / 2;
            int drawY = screenY + (Config.TILE_SIZE - Config.ENTITY_FRAME_SIZE);
            int feetY = drawY + Config.ENTITY_FRAME_SIZE;

            entities.add(new DrawableEntity(feetY, () -> {
                g2d.drawImage(sprite, drawX, drawY, Config.ENTITY_FRAME_SIZE, Config.ENTITY_FRAME_SIZE, null);
            }));
        }
    }

    private void gatherEnemies(java.util.List<DrawableEntity> entities, Graphics2D g2d) {
        int count = goblinhunters.controller.ControllerForView.getInstance().getEnemyCount();

        for (int i = 0; i < count; i++) {
            double x = goblinhunters.controller.ControllerForView.getInstance().getEnemyX(i);
            double y = goblinhunters.controller.ControllerForView.getInstance().getEnemyY(i);
            goblinhunters.utils.Direction dir = goblinhunters.controller.ControllerForView.getInstance().getEnemyDirection(i);
            goblinhunters.utils.EnemyType type = goblinhunters.controller.ControllerForView.getInstance().getEnemyType(i);

            if (goblinhunters.controller.ControllerForView.getInstance().isEnemyInvincible(i)) {
                if ((System.currentTimeMillis() / goblinhunters.utils.Config.FLICKER_DELAY_MS) % 2 == 0) {
                    continue;
                }
            }

            String prefix = switch (type) {
                case COMMON -> "COMMON";
                case HUNTER -> "HUNTER";
                case SHOOTER -> "SHOOTER";
                case BOSS -> "BOSS";
                default -> "COMMON";
            };

            String state = goblinhunters.controller.ControllerForView.getInstance().getEnemyState(i);
            int frames = goblinhunters.utils.Config.GOBLIN_RUN_FRAMES;

            if (type == goblinhunters.utils.EnemyType.BOSS) {
                switch (state) {
                    case "FURY", "EXHAUSTED" -> {
                        state = "RUN";
                        frames = goblinhunters.utils.Config.BOSS_RUN_FRAMES;
                    }
                    case "FURY_GUARD" -> {
                        state = "IDLE";
                        frames = goblinhunters.utils.Config.BOSS_IDLE_FRAMES;
                    }
                    case "TELEGRAPH" -> {
                        state = "ATTACK";
                        frames = goblinhunters.utils.Config.BOSS_ATTACK_FRAMES;
                    }
                    case "IDLE_EXHAUSTED" -> {
                        state = "IDLE";
                        frames = goblinhunters.utils.Config.BOSS_IDLE_FRAMES;
                    }
                    case "DYING" -> frames = goblinhunters.utils.Config.BOSS_DYING_FRAMES;
                }
            }

            if (type == goblinhunters.utils.EnemyType.SHOOTER) {
                switch (state) {
                    case "IDLE"   -> frames = goblinhunters.utils.Config.GOBLIN_IDLE_FRAMES;
                    case "ATTACK" -> frames = goblinhunters.utils.Config.SHOOTER_ATTACK_FRAMES;
                }
            }

            int currentFrame = 0;
            if (state.equals("DYING")) {
                long timePassed = System.currentTimeMillis()
                        - ControllerForView.getInstance().getEnemyStateStartTime(i);

                // flicker in the last second (1000–2000 ms) as a fade-out effect
                if (timePassed > 1000) {
                    if ((System.currentTimeMillis() / goblinhunters.utils.Config.FLICKER_DELAY_MS) % 2 == 0) {
                        continue;
                    }
                }

                currentFrame = (int) (timePassed / 150);
                if (currentFrame >= frames) {
                    currentFrame = frames - 1;
                }
            } else {
                currentFrame = (int) (System.currentTimeMillis() / 80) % frames;
            }

            // DYING animations have no directional variant in the sprite sheet
            String spriteKey = prefix + "_" + state + (state.equals("DYING") ? "" : "_" + dir.name());
            java.awt.image.BufferedImage sprite = goblinhunters.view.SpriteManager.getInstance().getSprite(spriteKey, currentFrame);

            if (sprite == null && state.equals("IDLE")) {
                spriteKey = prefix + "_RUN_" + dir.name();
                sprite = goblinhunters.view.SpriteManager.getInstance().getSprite(spriteKey, 0);
            }

            final java.awt.image.BufferedImage finalSprite = sprite;
            final String finalState = state;

            if (finalSprite != null) {
                int screenX = (int) (x * goblinhunters.utils.Config.TILE_SIZE) + goblinhunters.utils.Config.GRID_OFFSET_X;
                int screenY = (int) (y * goblinhunters.utils.Config.TILE_SIZE) + goblinhunters.utils.Config.GRID_OFFSET_Y;

                if (type == goblinhunters.utils.EnemyType.BOSS) {
                    int drawX = (screenX + 32) - 96;
                    int drawY = (screenY + 64) - 149;
                    int feetY = drawY + goblinhunters.utils.Config.BOSS_FRAME_SIZE;

                    entities.add(new DrawableEntity(feetY, () -> {
                        g2d.drawImage(finalSprite, drawX, drawY, goblinhunters.utils.Config.BOSS_FRAME_SIZE, goblinhunters.utils.Config.BOSS_FRAME_SIZE, null);

                        if (!finalState.equals("DYING")) {
                            int hp    = goblinhunters.controller.ControllerForView.getInstance().getBossHP();
                            int maxHp = goblinhunters.controller.ControllerForView.getInstance().getBossMaxHP();
                            if (hp > 0 && maxHp > 0) {
                                int barW = 60;
                                int barH = 6;
                                int barX = drawX + (goblinhunters.utils.Config.BOSS_FRAME_SIZE - barW) / 2;
                                int barY = drawY + 35;

                                float ratio = Math.max(0f, Math.min(1f, (float) hp / maxHp));

                                g2d.setColor(new Color(40, 40, 40, 200));
                                g2d.fillRect(barX, barY, barW, barH);

                                Color barColor = (ratio > 0.5f)
                                        ? new Color(220, 40, 40)
                                        : new Color(255, 120, 0);
                                g2d.setColor(barColor);
                                g2d.fillRect(barX, barY, (int)(barW * ratio), barH);

                                g2d.setColor(Color.BLACK);
                                g2d.drawRect(barX, barY, barW, barH);
                            }
                        }
                    }));
                } else {
                    int drawX = screenX + (goblinhunters.utils.Config.TILE_SIZE - 128) / 2;
                    int drawY = screenY + (goblinhunters.utils.Config.TILE_SIZE - 128);
                    int feetY = drawY + 128;

                    entities.add(new DrawableEntity(feetY, () -> {
                        g2d.drawImage(finalSprite, drawX, drawY, 128, 128, null);
                    }));
                }
            }
        }
    }

    /**
     * Draws boss crack-floor tiles as an overlay above the normal floor.
     * Uses a pulsing alpha so the hazard stays visible without obscuring
     * entities drawn afterwards.
     */
    private void drawCracks(Graphics2D g2d) {
        int count = goblinhunters.controller.ControllerForView.getInstance().getCrackCount();
        if (count == 0) return;

        BufferedImage crackTile = tileManager.getTileImage(goblinhunters.utils.Config.CELL_CRACKED_FLOOR);

        // pulsing alpha (0.55 – 0.85) to give the hazard a "live" appearance
        float pulse = 0.55f + 0.30f * (float) Math.abs(Math.sin(System.currentTimeMillis() / 400.0));
        Composite originalComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulse));

        for (int i = 0; i < count; i++) {
            int row = goblinhunters.controller.ControllerForView.getInstance().getCrackRow(i);
            int col = goblinhunters.controller.ControllerForView.getInstance().getCrackCol(i);

            int screenX = goblinhunters.utils.Config.GRID_OFFSET_X + col * goblinhunters.utils.Config.TILE_SIZE;
            int screenY = goblinhunters.utils.Config.GRID_OFFSET_Y + row * goblinhunters.utils.Config.TILE_SIZE;

            if (crackTile != null) {
                g2d.drawImage(crackTile, screenX, screenY, goblinhunters.utils.Config.TILE_SIZE, goblinhunters.utils.Config.TILE_SIZE, null);
            } else {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
                g2d.setColor(new Color(220, 80, 0));
                g2d.fillRect(screenX + 4, screenY + 4,
                        goblinhunters.utils.Config.TILE_SIZE - 8, goblinhunters.utils.Config.TILE_SIZE - 8);
            }
        }

        g2d.setComposite(originalComposite);
    }

    private void gatherBombs(java.util.List<DrawableEntity> entities, Graphics2D g2d) {
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
                int feetY = screenY + Config.TILE_SIZE;
                entities.add(new DrawableEntity(feetY, () -> {
                    g2d.drawImage(sprite, screenX, screenY, null);
                }));
            }
        }
    }

    private void gatherDestructions(java.util.List<DrawableEntity> entities, Graphics2D g2d) {
        int count = ControllerForView.getInstance().getDestructionCount();
        String theme = ControllerForView.getInstance().getCurrentTheme();
        String animKey = "FOREST".equals(theme) ? "BUSH_BREAK" : "CRATE_BREAK";

        for (int i = 0; i < count; i++) {
            int row = ControllerForView.getInstance().getDestructionRow(i);
            int col = ControllerForView.getInstance().getDestructionCol(i);
            int elapsed = ControllerForView.getInstance().getDestructionElapsedTime(i);

            int frameDur = "BUSH_BREAK".equals(animKey) ? 60 : Config.DESTRUCTION_FRAME_DURATION;
            int currentFrame = elapsed / frameDur;
            if (currentFrame >= Config.DESTRUCTION_FRAMES)
                currentFrame = Config.DESTRUCTION_FRAMES - 1;

            int screenX = Config.GRID_OFFSET_X + col * Config.TILE_SIZE;
            int screenY = Config.GRID_OFFSET_Y + row * Config.TILE_SIZE;

            BufferedImage sprite = SpriteManager.getInstance().getSprite(animKey, currentFrame);
            if (sprite != null) {
                int feetY = screenY + Config.TILE_SIZE;
                entities.add(new DrawableEntity(feetY, () -> {
                    g2d.drawImage(sprite, screenX, screenY, Config.TILE_SIZE, Config.TILE_SIZE, null);
                }));
            }
        }
    }

    private void drawFire(Graphics2D g2d) {
        int count = ControllerForView.getInstance().getFireCount();

        for (int i = 0; i < count; i++) {
            int r   = ControllerForView.getInstance().getFireRow(i);
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

    private void gatherProjectiles(java.util.List<DrawableEntity> entities, Graphics2D g2d) {
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
                    int screenX = (int) (x * Config.TILE_SIZE) + Config.GRID_OFFSET_X;
                    int screenY = (int) (y * Config.TILE_SIZE) + Config.GRID_OFFSET_Y;
                    int feetY = screenY + Config.TILE_SIZE;

                    entities.add(new DrawableEntity(feetY, () -> {
                        g2d.drawImage(sprite, screenX, screenY, Config.TILE_SIZE, Config.TILE_SIZE, null);
                    }));
                }
            }
        }
    }

    private void gatherStaffAttack(java.util.List<DrawableEntity> entities, Graphics2D g2d) {
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
            int screenX = (int) (logX * Config.TILE_SIZE) + Config.GRID_OFFSET_X;
            int screenY = (int) (logY * Config.TILE_SIZE) + Config.GRID_OFFSET_Y;

            int drawX = screenX + (Config.TILE_SIZE - Config.ENTITY_FRAME_SIZE) / 2;
            int drawY = screenY + (Config.TILE_SIZE - Config.ENTITY_FRAME_SIZE);
            int feetY = drawY + Config.ENTITY_FRAME_SIZE;

            entities.add(new DrawableEntity(feetY, () -> {
                g2d.drawImage(sprite, drawX, drawY, Config.ENTITY_FRAME_SIZE, Config.ENTITY_FRAME_SIZE, null);
            }));
        }
    }

    // ==========================================================
    // transition overlay
    // ==========================================================

    public void drawTransition(Graphics2D g2d) {
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

        if (transitionAlpha > Config.MIN_ALPHA) {
            g2d.setColor(new Color(0.0f, 0.0f, 0.0f, transitionAlpha));
            g2d.fillRect(0, 0, Config.WINDOW_PREFERRED_WIDTH, Config.WINDOW_PREFERRED_HEIGHT);
        }
    }

    // ==========================================================
    // AbstractDrawer overrides
    // ==========================================================

    @Override
    public int getDrawingWidth() {
        return Config.WINDOW_PREFERRED_WIDTH;
    }

    @Override
    public int getDrawingHeight() {
        return Config.WINDOW_PREFERRED_HEIGHT;
    }

    // ==========================================================
    // private helpers
    // ==========================================================

    private int getFramesForState(PlayerState state) {
        String s = state.name();
        if (s.contains("ATTACK") || s.contains("HURT") || s.contains("DYING"))
            return Config.PLAYER_ATTACK_FRAMES;
        else if (s.contains("RUN"))
            return Config.PLAYER_RUN_FRAMES;
        else
            return Config.PLAYER_IDLE_FRAMES;
    }

    private void gatherCollectibles(java.util.List<DrawableEntity> entities, Graphics2D g2d) {
        int count = goblinhunters.controller.ControllerForView.getInstance().getCollectibleCount();
        goblinhunters.view.SpriteManager sm = goblinhunters.view.SpriteManager.getInstance();

        for (int i = 0; i < count; i++) {
            int screenX = (int) (goblinhunters.controller.ControllerForView.getInstance().getCollectibleX(i)
                    * goblinhunters.utils.Config.TILE_SIZE) + goblinhunters.utils.Config.GRID_OFFSET_X;
            int screenY = (int) (goblinhunters.controller.ControllerForView.getInstance().getCollectibleY(i)
                    * goblinhunters.utils.Config.TILE_SIZE) + goblinhunters.utils.Config.GRID_OFFSET_Y;

            goblinhunters.utils.ItemType type = goblinhunters.controller.ControllerForView.getInstance().getCollectibleType(i);
            java.awt.image.BufferedImage sprite = null;

            switch (type) {
                case AMMO_BOMB -> sprite = sm.getSprite("CONSUMABLES", 1);
                case AMMO_AURA -> sprite = sm.getSprite("CONSUMABLES", 0);
                case POWER_SHIELD -> sprite = sm.getSprite("POWER_UPS", 0);
                case POWER_RADIUS -> sprite = sm.getSprite("POWER_UPS", 1);
                case POWER_SPEED -> sprite = sm.getSprite("POWER_UPS", 2);
            }

            final java.awt.image.BufferedImage finalSprite = sprite;

            if (finalSprite != null) {
                int feetY = screenY + goblinhunters.utils.Config.TILE_SIZE;
                entities.add(new DrawableEntity(feetY, () -> {
                    g2d.drawImage(finalSprite, screenX, screenY, goblinhunters.utils.Config.TILE_SIZE, goblinhunters.utils.Config.TILE_SIZE, null);
                }));
            }
        }
    }
}
