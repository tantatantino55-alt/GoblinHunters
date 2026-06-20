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

    public ConcreteDrawer() {
        this.tileManager = TileManager.getInstance();
        this.spriteManager = SpriteManager.getInstance();
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
        EnemyDrawer.getInstance().gather(sortedEntities, g2d);
        gatherDestructions(sortedEntities, g2d);

        PlayerState state = ControllerForView.getInstance().getPlayerState();
        if (state.name().startsWith("ATTACK")) {
            PlayerDrawer.getInstance().gatherStaffAttack(sortedEntities, g2d);
        } else {
            PlayerDrawer.getInstance().gather(sortedEntities, g2d);
        }

        // ascending Y sort: lower Y (closer to top) drawn first
        java.util.Collections.sort(sortedEntities, java.util.Comparator.comparingInt(e -> e.y));

        for (DrawableEntity e : sortedEntities) {
            e.drawAction.run();
        }

        drawTransition(g2d);
        HudDrawer.getInstance().draw(g2d);

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

    // ==========================================================
    // boss crack-floor overlay
    // ==========================================================

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
}
