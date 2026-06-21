package goblinhunters.view;

import goblinhunters.controller.ControllerForView;
import goblinhunters.controller.IControllerForView;
import goblinhunters.utils.Config;

import java.awt.*;
import java.awt.image.BufferedImage;

public class HudDrawer {

    private static HudDrawer instance = null;

    private HudDrawer() {}

    public static HudDrawer getInstance() {
        if (instance == null) instance = new HudDrawer();
        return instance;
    }

    public void draw(Graphics2D g2d) {
        IControllerForView ctrl = ControllerForView.getInstance();
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
                "HUD_FIRE_SPELL", 0,
                "HUD_FIRE_SPELL_gray",
                bombAmmo > 0, "x" + bombAmmo);

        drawHudIcon(g2d, consX + iconSize + 28 + iconGap, currentY,
                "HUD_AURA_SPELL", 0,
                "HUD_AURA_SPELL_gray",
                auraAmmo > 0, "x" + auraAmmo);
        currentY += iconSize + 12;

        int puSize = Config.HUD_POWER_SIZE;
        int puGap  = Config.HUD_POWER_GAP;
        int totalPuW = puSize * 3 + puGap * 2;
        int puX = panelX + (panelW - totalPuW) / 2;

        drawHudIcon(g2d, puX, currentY,
                "POWER_UPS", 0,
                "POWER_UPS_0_gray",
                shield, null);
        puX += puSize + puGap;

        drawHudIcon(g2d, puX, currentY,
                "POWER_UPS", 1,
                "POWER_UPS_1_gray",
                radius, null);
        puX += puSize + puGap;

        drawHudIcon(g2d, puX, currentY,
                "POWER_UPS", 2,
                "POWER_UPS_2_gray",
                speed, null);
        currentY += puSize + 14;

        boolean staffUsable = ctrl.isStaffUsable();
        BufferedImage staffImg = SpriteManager.getInstance().getSprite("STAFF_ICON", 0);
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
            currentY += STAFF_SIZE + (staffUsable ? 18 : 6);
        }

        // ESC menu hint — always visible during gameplay
        g2d.setFont(new Font("Monospaced", Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        FontMetrics fmEsc = g2d.getFontMetrics();
        String escLabel = "[ESC] MENU";
        g2d.drawString(escLabel, panelX + (panelW - fmEsc.stringWidth(escLabel)) / 2, currentY);
    }

    private void drawHudIcon(Graphics2D g2d,
                              int x, int y,
                              String colorKey, int colorFrame,
                              String grayKey,
                              boolean active,
                              String counter) {

        final int BASE_SIZE = Config.HUD_ICON_BASE_SIZE;

        BufferedImage img;
        Composite originalComposite = g2d.getComposite();

        if (active) {
            img = SpriteManager.getInstance().getSprite(colorKey, colorFrame);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        } else {
            img = SpriteManager.getInstance().getGrayscale(grayKey);
            if (img == null) img = SpriteManager.getInstance().getSprite(colorKey, colorFrame);
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
}
