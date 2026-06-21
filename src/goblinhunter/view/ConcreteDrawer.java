package goblinhunter.view;

import goblinhunter.controller.ControllerForView;
import goblinhunter.utils.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ConcreteDrawer extends AbstractDrawer {

    private final TileManager tileManager;
    private final SpriteManager spriteManager;

    private float transitionAlpha = Config.MIN_ALPHA;

    public ConcreteDrawer() {
        this.tileManager = TileManager.getInstance();
        this.spriteManager = SpriteManager.getInstance();
    }

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

        List<DrawableEntity> sortedEntities = new ArrayList<>();

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
        Collections.sort(sortedEntities, Comparator.comparingInt(e -> e.y));

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

    /**
     * Draws the map in four ordered passes:
     * 0 — floor tiles, 1 — theme frame, 2 — walls/pillars/crates, 3 — large building ornaments.
     */
    private void drawMap(Graphics2D g2d) {
        String theme = ControllerForView.getInstance().getCurrentTheme();
        tileManager.setCurrentTheme(theme);
        int[][] gameAreaArray = ControllerForView.getInstance().getGameAreaArray();

        BufferedImage floorImg = tileManager.getTileImage(Config.CELL_EMPTY);
        BufferedImage frameImg = tileManager.getTileImage(Config.THEME_FRAME_INDEX);

        // pass 0: floor
        for (int row = 0; row < Config.GRID_HEIGHT; row++) {
            for (int col = 0; col < Config.GRID_WIDTH; col++) {
                int tileX = Config.GRID_OFFSET_X + col * Config.TILE_SIZE;
                int tileY = Config.GRID_OFFSET_Y + row * Config.TILE_SIZE;
                if (floorImg != null) {
                    g2d.drawImage(floorImg, tileX, tileY, Config.TILE_SIZE, Config.TILE_SIZE, null);
                }
            }
        }

        // pass 1: theme frame — drawn before buildings so buildings render on top
        if (frameImg != null) {
            g2d.drawImage(frameImg, Config.FRAME_OFFSET_X, Config.FRAME_OFFSET_Y, null);
        }

        // pass 2: walls, pillars, crates
        for (int row = 0; row < Config.GRID_HEIGHT; row++) {
            for (int col = 0; col < Config.GRID_WIDTH; col++) {
                int cellType = gameAreaArray[row][col];
                int tileX = Config.GRID_OFFSET_X + col * Config.TILE_SIZE;
                int tileY = Config.GRID_OFFSET_Y + row * Config.TILE_SIZE;

                if (cellType != Config.CELL_EMPTY && cellType != Config.CELL_ORNAMENT) {
                    // cells (0,4) and (0,9) are covered by building ornaments drawn in pass 3
                    if (row == 0 && (col == 4 || col == 9)) {
                        continue;
                    }

                    BufferedImage wallImg = tileManager.getTileImage(cellType);
                    if (wallImg != null) {
                        g2d.drawImage(wallImg, tileX, tileY, Config.TILE_SIZE, Config.TILE_SIZE, null);
                    }
                }
            }
        }

        // pass 3: large 2×2 building ornaments (CELL_ORNAMENT = 5)
        for (int row = 0; row < Config.GRID_HEIGHT; row++) {
            for (int col = 0; col < Config.GRID_WIDTH; col++) {
                int cellType = gameAreaArray[row][col];

                if (cellType == Config.CELL_ORNAMENT) {
                    int tileX = Config.GRID_OFFSET_X + col * Config.TILE_SIZE;
                    // shift up one tile so the base sits on row 0 and the top covers the frame border
                    int tileY = Config.GRID_OFFSET_Y + row * Config.TILE_SIZE - Config.TILE_SIZE;

                    if ("CAVE".equals(theme)) {
                        int frameIndex = (int) ((System.currentTimeMillis() / 100)
                                % Config.SKELETON_FRAMES_COUNT);
                        BufferedImage skeletonFrame = tileManager
                                .getTileImage(ViewConfig.CELL_SKELETON_START + frameIndex);
                        if (skeletonFrame != null) {
                            g2d.drawImage(skeletonFrame, tileX, tileY, 128, 128, null);
                        }
                    } else {
                        BufferedImage ornament = tileManager.getTileImage(Config.CELL_ORNAMENT);
                        if (ornament != null) {
                            g2d.drawImage(ornament, tileX, tileY, 128, 128, null);
                        }
                    }
                }
            }
        }
    }

    private void gatherBombs(List<DrawableEntity> entities, Graphics2D g2d) {
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

    private void gatherDestructions(List<DrawableEntity> entities, Graphics2D g2d) {
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

    private void gatherProjectiles(List<DrawableEntity> entities, Graphics2D g2d) {
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

    private void gatherCollectibles(List<DrawableEntity> entities, Graphics2D g2d) {
        int count = ControllerForView.getInstance().getCollectibleCount();
        SpriteManager sm = SpriteManager.getInstance();

        for (int i = 0; i < count; i++) {
            int screenX = (int) (ControllerForView.getInstance().getCollectibleX(i)
                    * Config.TILE_SIZE) + Config.GRID_OFFSET_X;
            int screenY = (int) (ControllerForView.getInstance().getCollectibleY(i)
                    * Config.TILE_SIZE) + Config.GRID_OFFSET_Y;

            ItemType type = ControllerForView.getInstance().getCollectibleType(i);
            BufferedImage sprite = null;

            switch (type) {
                case AMMO_BOMB -> sprite = sm.getSprite("CONSUMABLES", 1);
                case AMMO_AURA -> sprite = sm.getSprite("CONSUMABLES", 0);
                case POWER_SHIELD -> sprite = sm.getSprite("POWER_UPS", 0);
                case POWER_RADIUS -> sprite = sm.getSprite("POWER_UPS", 1);
                case POWER_SPEED -> sprite = sm.getSprite("POWER_UPS", 2);
            }

            final BufferedImage finalSprite = sprite;

            if (finalSprite != null) {
                int feetY = screenY + Config.TILE_SIZE;
                entities.add(new DrawableEntity(feetY, () -> {
                    g2d.drawImage(finalSprite, screenX, screenY, Config.TILE_SIZE, Config.TILE_SIZE, null);
                }));
            }
        }
    }

    /**
     * Draws boss crack-floor tiles as an overlay above the normal floor.
     * Uses a pulsing alpha so the hazard stays visible without obscuring
     * entities drawn afterwards.
     */
    private void drawCracks(Graphics2D g2d) {
        int count = ControllerForView.getInstance().getCrackCount();
        if (count == 0) return;

        BufferedImage crackTile = tileManager.getTileImage(Config.CELL_CRACKED_FLOOR);

        // pulsing alpha (0.55 – 0.85) to give the hazard a "live" appearance
        float pulse = 0.55f + 0.30f * (float) Math.abs(Math.sin(System.currentTimeMillis() / 400.0));
        Composite originalComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulse));

        for (int i = 0; i < count; i++) {
            int row = ControllerForView.getInstance().getCrackRow(i);
            int col = ControllerForView.getInstance().getCrackCol(i);

            int screenX = Config.GRID_OFFSET_X + col * Config.TILE_SIZE;
            int screenY = Config.GRID_OFFSET_Y + row * Config.TILE_SIZE;

            if (crackTile != null) {
                g2d.drawImage(crackTile, screenX, screenY, Config.TILE_SIZE, Config.TILE_SIZE, null);
            } else {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
                g2d.setColor(new Color(220, 80, 0));
                g2d.fillRect(screenX + 4, screenY + 4,
                        Config.TILE_SIZE - 8, Config.TILE_SIZE - 8);
            }
        }

        g2d.setComposite(originalComposite);
    }

    private void drawTransition(Graphics2D g2d) {
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

    @Override
    public int getDrawingWidth() {
        return Config.WINDOW_PREFERRED_WIDTH;
    }

    @Override
    public int getDrawingHeight() {
        return Config.WINDOW_PREFERRED_HEIGHT;
    }
}
