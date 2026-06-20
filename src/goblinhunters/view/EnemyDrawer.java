package goblinhunters.view;

import goblinhunters.controller.ControllerForView;
import goblinhunters.utils.Config;
import goblinhunters.utils.Direction;
import goblinhunters.utils.EnemyType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class EnemyDrawer {

    private static EnemyDrawer instance = null;

    private EnemyDrawer() {}

    public static EnemyDrawer getInstance() {
        if (instance == null) instance = new EnemyDrawer();
        return instance;
    }

    // ==========================================================
    // public API
    // ==========================================================

    public void gather(List<DrawableEntity> entities, Graphics2D g2d) {
        int count = ControllerForView.getInstance().getEnemyCount();

        for (int i = 0; i < count; i++) {
            double x      = ControllerForView.getInstance().getEnemyX(i);
            double y      = ControllerForView.getInstance().getEnemyY(i);
            Direction dir = ControllerForView.getInstance().getEnemyDirection(i);
            EnemyType type = ControllerForView.getInstance().getEnemyType(i);

            if (ControllerForView.getInstance().isEnemyInvincible(i)) {
                if ((System.currentTimeMillis() / Config.FLICKER_DELAY_MS) % 2 == 0) {
                    continue;
                }
            }

            String prefix    = resolveTypePrefix(type);
            String logicState = ControllerForView.getInstance().getEnemyState(i);
            String state     = (type == EnemyType.BOSS) ? resolveBossVisualState(logicState) : logicState;
            int frames       = resolveFrameCount(type, state);

            if (state.equals("DYING")) {
                long timePassed = System.currentTimeMillis()
                        - ControllerForView.getInstance().getEnemyStateStartTime(i);

                // flicker in the last second (1000–2000 ms) as a fade-out effect
                if (timePassed > 1000) {
                    if ((System.currentTimeMillis() / Config.FLICKER_DELAY_MS) % 2 == 0) {
                        continue;
                    }
                }
            }

            long stateStart  = ControllerForView.getInstance().getEnemyStateStartTime(i);
            int currentFrame = computeCurrentFrame(state, frames, stateStart);

            String spriteKey = buildSpriteKey(prefix, state, dir);
            BufferedImage sprite = SpriteManager.getInstance().getSprite(spriteKey, currentFrame);

            if (sprite == null && state.equals("IDLE")) {
                spriteKey = prefix + "_RUN_" + dir.name();
                sprite = SpriteManager.getInstance().getSprite(spriteKey, 0);
            }

            final BufferedImage finalSprite = sprite;
            final String finalState = state;

            if (finalSprite != null) {
                int screenX = (int) (x * Config.TILE_SIZE) + Config.GRID_OFFSET_X;
                int screenY = (int) (y * Config.TILE_SIZE) + Config.GRID_OFFSET_Y;

                if (type == EnemyType.BOSS) {
                    int drawX = (screenX + 32) - 96;
                    int drawY = (screenY + 64) - 149;
                    int feetY = drawY + Config.BOSS_FRAME_SIZE;

                    entities.add(new DrawableEntity(feetY, () -> {
                        g2d.drawImage(finalSprite, drawX, drawY,
                                Config.BOSS_FRAME_SIZE, Config.BOSS_FRAME_SIZE, null);
                        if (!finalState.equals("DYING")) {
                            drawBossHpBar(g2d, drawX, drawY);
                        }
                    }));
                } else {
                    int drawX = screenX + (Config.TILE_SIZE - 128) / 2;
                    int drawY = screenY + (Config.TILE_SIZE - 128);
                    int feetY = drawY + 128;

                    entities.add(new DrawableEntity(feetY, () -> {
                        g2d.drawImage(finalSprite, drawX, drawY, 128, 128, null);
                    }));
                }
            }
        }
    }

    // ==========================================================
    // private helpers
    // ==========================================================

    private String resolveTypePrefix(EnemyType type) {
        return switch (type) {
            case COMMON  -> "COMMON";
            case HUNTER  -> "HUNTER";
            case SHOOTER -> "SHOOTER";
            case BOSS    -> "BOSS";
            default      -> "COMMON";
        };
    }

    private String resolveBossVisualState(String logicState) {
        return switch (logicState) {
            case "FURY", "EXHAUSTED" -> "RUN";
            case "FURY_GUARD"        -> "IDLE";
            case "TELEGRAPH"         -> "ATTACK";
            case "IDLE_EXHAUSTED"    -> "IDLE";
            default                  -> logicState;
        };
    }

    private int resolveFrameCount(EnemyType type, String visualState) {
        if (type == EnemyType.BOSS) {
            return switch (visualState) {
                case "RUN"    -> Config.BOSS_RUN_FRAMES;
                case "IDLE"   -> Config.BOSS_IDLE_FRAMES;
                case "ATTACK" -> Config.BOSS_ATTACK_FRAMES;
                case "DYING"  -> Config.BOSS_DYING_FRAMES;
                default       -> Config.BOSS_RUN_FRAMES;
            };
        }
        if (type == EnemyType.SHOOTER) {
            return switch (visualState) {
                case "IDLE"   -> Config.GOBLIN_IDLE_FRAMES;
                case "ATTACK" -> Config.SHOOTER_ATTACK_FRAMES;
                default       -> Config.GOBLIN_RUN_FRAMES;
            };
        }
        return Config.GOBLIN_RUN_FRAMES;
    }

    private int computeCurrentFrame(String state, int frames, long startTime) {
        if (state.equals("DYING")) {
            long timePassed = System.currentTimeMillis() - startTime;
            int frame = (int) (timePassed / 150);
            return Math.min(frame, frames - 1);
        }
        return (int) (System.currentTimeMillis() / 80) % frames;
    }

    private String buildSpriteKey(String prefix, String state, Direction dir) {
        return prefix + "_" + state + (state.equals("DYING") ? "" : "_" + dir.name());
    }

    private void drawBossHpBar(Graphics2D g2d, int drawX, int drawY) {
        int hp    = ControllerForView.getInstance().getBossHP();
        int maxHp = ControllerForView.getInstance().getBossMaxHP();
        if (hp <= 0 || maxHp <= 0) return;

        int barW = 60;
        int barH = 6;
        int barX = drawX + (Config.BOSS_FRAME_SIZE - barW) / 2;
        int barY = drawY + 35;

        float ratio = Math.max(0f, Math.min(1f, (float) hp / maxHp));

        g2d.setColor(new Color(40, 40, 40, 200));
        g2d.fillRect(barX, barY, barW, barH);

        Color barColor = (ratio > 0.5f)
                ? new Color(220, 40, 40)
                : new Color(255, 120, 0);
        g2d.setColor(barColor);
        g2d.fillRect(barX, barY, (int) (barW * ratio), barH);

        g2d.setColor(Color.BLACK);
        g2d.drawRect(barX, barY, barW, barH);
    }
}
