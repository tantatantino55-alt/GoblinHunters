package goblinhunter.view;

import goblinhunter.controller.ControllerForView;
import goblinhunter.utils.Config;
import goblinhunter.utils.PlayerState;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class PlayerDrawer {

    private static PlayerDrawer instance = null;

    private PlayerDrawer() {}

    public static PlayerDrawer getInstance() {
        if (instance == null) instance = new PlayerDrawer();
        return instance;
    }

    public void gather(List<DrawableEntity> entities, Graphics2D g2d) {
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

        BufferedImage sprite = SpriteManager.getInstance().getSprite(state, currentFrame);

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

    public void gatherStaffAttack(List<DrawableEntity> entities, Graphics2D g2d) {
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

        BufferedImage sprite = SpriteManager.getInstance().getSprite(state, currentFrame);
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

    private int getFramesForState(PlayerState state) {
        String s = state.name();
        if (s.contains("ATTACK") || s.contains("HURT") || s.contains("DYING"))
            return Config.PLAYER_ATTACK_FRAMES;
        else if (s.contains("RUN"))
            return Config.PLAYER_RUN_FRAMES;
        else
            return Config.PLAYER_IDLE_FRAMES;
    }
}
