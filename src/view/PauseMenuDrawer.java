package view;

import utils.Config;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * Draws the pause-menu overlay on the game's Graphics2D context.
 *
 * All rendering is pure Java2D — no dynamic Swing component reloading.
 * Keybind buttons, audio icon, and main buttons are all painted here.
 *
 * Call {@link #draw(Graphics2D, PauseState)} from ConcreteDrawer when the game is paused.
 * Call {@link #handleClick(int, int, PauseState)} from the mouse-listener.
 */
public class PauseMenuDrawer {

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------
    private static PauseMenuDrawer instance;
    public static PauseMenuDrawer getInstance() {
        if (instance == null) instance = new PauseMenuDrawer();
        return instance;
    }

    // -------------------------------------------------------------------------
    // Layout constants  (all in screen pixels)
    // -------------------------------------------------------------------------
    private static final int PANEL_W   = 420;
    private static final int PANEL_H   = 560;
    private static final int CORNER_R  = 16;

    // Colours
    private static final Color BG_DARK      = new Color(28, 18, 10, 235);
    private static final Color BORDER_GOLD  = new Color(200, 165, 70);
    private static final Color BORDER_INNER = new Color(120, 90, 30);
    private static final Color SECTION_YELLOW = new Color(255, 210, 60);
    private static final Color TEXT_WHITE   = new Color(240, 235, 220);
    private static final Color KEYBIND_BG   = new Color(50, 38, 20, 220);
    private static final Color KEYBIND_BORDER = new Color(160, 130, 50);
    private static final Color KEYBIND_HOVER  = new Color(90, 70, 30, 220);
    private static final Color BTN_RED        = new Color(155, 25, 25);
    private static final Color BTN_RED_DARK   = new Color(100, 15, 15);
    private static final Color BTN_GREEN      = new Color(30, 130, 40);
    private static final Color BTN_GREEN_DARK = new Color(15, 85, 25);
    private static final Color BTN_SECONDARY  = new Color(60, 50, 30, 220);

    // -------------------------------------------------------------------------
    // Computed layout (populated once in draw)
    // -------------------------------------------------------------------------
    /** Screen-space rectangle of each keybind button [4 rows]. */
    private final Rectangle[] keybindRects = new Rectangle[4];
    /** Screen-space rectangle of "RESET DEFAULTS" button. */
    private Rectangle resetRect;
    /** Screen-space rectangle of "QUIT GAME" button. */
    private Rectangle quitRect;
    /** Screen-space rectangle of "RESUME" button. */
    private Rectangle resumeRect;

    /** Index of the keybind row currently being re-assigned (−1 = none). */
    private int rebindingRow = -1;

    // -------------------------------------------------------------------------
    // Font cache
    // -------------------------------------------------------------------------
    private Font fontTitle;
    private Font fontSection;
    private Font fontLabel;
    private Font fontKey;
    private Font fontBtn;

    private PauseMenuDrawer() {
        fontTitle   = new Font("Monospaced", Font.BOLD, 30);
        fontSection = new Font("Monospaced", Font.BOLD, 13);
        fontLabel   = new Font("Monospaced", Font.PLAIN, 13);
        fontKey     = new Font("Monospaced", Font.BOLD, 12);
        fontBtn     = new Font("Monospaced", Font.BOLD, 16);
    }

    // =========================================================================
    // PUBLIC API
    // =========================================================================

    /**
     * Main entry point — call each repaint cycle when the game is paused.
     */
    public void draw(Graphics2D g2d, PauseState state) {
        // 1. Compute panel origin (centred on screen)
        int screenW = Config.WINDOW_PREFERRED_WIDTH;
        int screenH = Config.WINDOW_PREFERRED_HEIGHT;
        int px = (screenW - PANEL_W) / 2;
        int py = (screenH - PANEL_H) / 2;

        // 2. Semi-transparent darkening of the whole screen
        Composite orig = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f));
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, screenW, screenH);
        g2d.setComposite(orig);

        // 3. Draw the framed panel
        drawPanel(g2d, px, py);

        // 4. Inner content
        int contentX = px + 24;
        int cy       = py + 30;

        // 4a. Title
        cy = drawTitle(g2d, px, cy);

        // 4b. Divider
        cy = drawDivider(g2d, contentX, cy, PANEL_W - 48);
        cy += 8;

        // 4c. CONTROLS section
        cy = drawControlsSection(g2d, contentX, cy, state);
        cy += 6;

        // 4d. Divider
        cy = drawDivider(g2d, contentX, cy, PANEL_W - 48);
        cy += 8;

        // 4e. AUDIO section
        cy = drawAudioSection(g2d, contentX, cy, state, px);

        // 4f. Bottom buttons (absolute positions at the bottom of the panel)
        drawBottomButtons(g2d, px, py);
    }

    /**
     * Mouse-click handler.
     * Returns an {@link ClickResult} describing what was clicked.
     */
    public ClickResult handleClick(int mx, int my, PauseState state) {
        if (resumeRect != null && resumeRect.contains(mx, my)) return ClickResult.RESUME;
        if (quitRect   != null && quitRect.contains(mx, my))   return ClickResult.QUIT;
        if (resetRect  != null && resetRect.contains(mx, my)) {
            state.resetDefaults();
            rebindingRow = -1;
            return ClickResult.RESET_DEFAULTS;
        }
        // Keybind boxes
        for (int i = 0; i < keybindRects.length; i++) {
            if (keybindRects[i] != null && keybindRects[i].contains(mx, my)) {
                if (rebindingRow == i) {
                    rebindingRow = -1; // toggle off
                } else {
                    rebindingRow = i;  // start listening
                }
                return ClickResult.REBIND_START;
            }
        }
        return ClickResult.NONE;
    }

    /**
     * Key-press handler — call when a key is pressed and {@link #isRebinding()} is true.
     */
    public void handleKeyForRebind(String keyName, PauseState state) {
        if (rebindingRow >= 0 && rebindingRow < state.keyLabels.length) {
            state.keyLabels[rebindingRow] = keyName;
            rebindingRow = -1;
        }
    }

    public boolean isRebinding() { return rebindingRow >= 0; }
    public int     getRebindingRow() { return rebindingRow; }
    /** Cancels any in-progress rebind (e.g. when menu closes via ESC or RESUME). */
    public void    cancelRebind() { rebindingRow = -1; }


    // =========================================================================
    // PRIVATE DRAWING HELPERS
    // =========================================================================

    private void drawPanel(Graphics2D g2d, int px, int py) {
        RenderingHints hints = g2d.getRenderingHints();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Shadow
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRoundRect(px + 6, py + 6, PANEL_W, PANEL_H, CORNER_R + 4, CORNER_R + 4);

        // Background fill
        g2d.setColor(BG_DARK);
        g2d.fillRoundRect(px, py, PANEL_W, PANEL_H, CORNER_R, CORNER_R);

        // Outer gold border (3px)
        g2d.setColor(BORDER_GOLD);
        g2d.setStroke(new BasicStroke(3f));
        g2d.drawRoundRect(px, py, PANEL_W, PANEL_H, CORNER_R, CORNER_R);

        // Inner thin border (1px, inset 5)
        g2d.setColor(BORDER_INNER);
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawRoundRect(px + 5, py + 5, PANEL_W - 10, PANEL_H - 10, CORNER_R - 4, CORNER_R - 4);

        // Corner ornaments (small filled circles at corners)
        int cr = 6;
        g2d.setColor(BORDER_GOLD);
        g2d.fillOval(px - cr / 2,             py - cr / 2,             cr * 2, cr * 2);
        g2d.fillOval(px + PANEL_W - cr + cr/2 - 6, py - cr / 2,        cr * 2, cr * 2);
        g2d.fillOval(px - cr / 2,             py + PANEL_H - cr + cr/2 - 6, cr * 2, cr * 2);
        g2d.fillOval(px + PANEL_W - cr + cr/2 - 6, py + PANEL_H - cr + cr/2 - 6, cr * 2, cr * 2);

        g2d.setStroke(new BasicStroke(1f));
        g2d.setRenderingHints(hints);
    }

    private int drawTitle(Graphics2D g2d, int panelX, int cy) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        String title = "|| PAUSE";
        g2d.setFont(fontTitle);
        FontMetrics fm = g2d.getFontMetrics();
        int tx = panelX + (PANEL_W - fm.stringWidth(title)) / 2;
        // Shadow
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.drawString(title, tx + 2, cy + fm.getAscent() + 2);
        // White text
        g2d.setColor(TEXT_WHITE);
        g2d.drawString(title, tx, cy + fm.getAscent());
        return cy + fm.getHeight() + 8;
    }

    private int drawDivider(Graphics2D g2d, int x, int cy, int w) {
        g2d.setColor(BORDER_GOLD);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawLine(x, cy, x + w, cy);
        g2d.setStroke(new BasicStroke(1f));
        return cy + 4;
    }

    // -------------------------------------------------------------------------
    // CONTROLS SECTION
    // -------------------------------------------------------------------------

    /** Row definitions for the controls section. */
    private static final String[] ACTION_LABELS = {
        "Move",
        "Fire Sphere",
        "Aura Spell",
        "Staff Attack"
    };
    private static final String[] DEFAULT_KEYS = {
        "W A S D",
        "SPACE",
        "X",
        "Z"
    };

    private int drawControlsSection(Graphics2D g2d, int cx, int cy, PauseState state) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Section header
        g2d.setFont(fontSection);
        g2d.setColor(SECTION_YELLOW);
        g2d.drawString("CONTROLS", cx, cy + 14);
        cy += 22;

        int iconSize   = 38;
        int rowH       = 52;
        int keybindW   = 96;
        int keybindH   = 30;
        int iconX      = cx;
        int labelX     = cx + iconSize + 10;
        int keybindX   = cx + PANEL_W - 48 - keybindW;

        for (int i = 0; i < 4; i++) {
            int rowY = cy + i * rowH;
            int midY = rowY + rowH / 2;

            // Icon
            drawActionIcon(g2d, i, iconX, rowY + (rowH - iconSize) / 2, iconSize);

            // Action label
            g2d.setFont(fontLabel);
            g2d.setColor(TEXT_WHITE);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(ACTION_LABELS[i], labelX, midY + fm.getAscent() / 2 - 1);

            // Keybind box
            int bx = keybindX;
            int by = midY - keybindH / 2;
            keybindRects[i] = new Rectangle(bx, by, keybindW, keybindH);

            boolean isRebinding = (rebindingRow == i);
            Color boxBg     = isRebinding ? new Color(90, 70, 30, 240) : KEYBIND_BG;
            Color boxBorder = isRebinding ? new Color(255, 210, 60)    : KEYBIND_BORDER;

            g2d.setColor(boxBg);
            g2d.fillRoundRect(bx, by, keybindW, keybindH, 8, 8);
            g2d.setColor(boxBorder);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(bx, by, keybindW, keybindH, 8, 8);
            g2d.setStroke(new BasicStroke(1f));

            String keyText = isRebinding ? "Press key..." : state.keyLabels[i];
            g2d.setFont(fontKey);
            g2d.setColor(isRebinding ? new Color(255, 220, 80) : TEXT_WHITE);
            FontMetrics kfm = g2d.getFontMetrics();
            int ktx = bx + (keybindW - kfm.stringWidth(keyText)) / 2;
            int kty = by + (keybindH - kfm.getHeight()) / 2 + kfm.getAscent();
            g2d.drawString(keyText, ktx, kty);
        }

        cy += 4 * rowH + 4;

        // "RESET DEFAULTS" button
        int rdW = 180;
        int rdH = 26;
        int rdX = cx + (PANEL_W - 48 - rdW) / 2;
        int rdY = cy;
        resetRect = new Rectangle(rdX, rdY, rdW, rdH);

        g2d.setColor(BTN_SECONDARY);
        g2d.fillRoundRect(rdX, rdY, rdW, rdH, 8, 8);
        g2d.setColor(BORDER_GOLD);
        g2d.setStroke(new BasicStroke(1.2f));
        g2d.drawRoundRect(rdX, rdY, rdW, rdH, 8, 8);
        g2d.setStroke(new BasicStroke(1f));

        g2d.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2d.setColor(new Color(210, 180, 80));
        FontMetrics rfm = g2d.getFontMetrics();
        String rdLabel = "RESET DEFAULTS";
        g2d.drawString(rdLabel,
                rdX + (rdW - rfm.stringWidth(rdLabel)) / 2,
                rdY + (rdH - rfm.getHeight()) / 2 + rfm.getAscent());

        cy += rdH + 10;
        return cy;
    }

    // -------------------------------------------------------------------------
    // AUDIO SECTION
    // -------------------------------------------------------------------------

    private int drawAudioSection(Graphics2D g2d, int cx, int cy, PauseState state, int panelX) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Section header
        g2d.setFont(fontSection);
        g2d.setColor(SECTION_YELLOW);
        g2d.drawString("AUDIO", cx, cy + 14);
        cy += 22;

        int iconSz = 36;
        int midY   = cy + iconSz / 2;

        // Speaker icon (drawn procedurally)
        drawSpeakerIcon(g2d, cx, cy, iconSz, state.muted);

        // "AUDIO" label
        g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2d.setColor(TEXT_WHITE);
        g2d.drawString("AUDIO", cx + iconSz + 10, midY + 5);

        if (state.muted) {
            // Red diagonal cross-out
            g2d.setColor(new Color(210, 30, 30));
            g2d.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(cx - 2, cy - 2, cx + iconSz + 2, cy + iconSz + 2);
            g2d.setStroke(new BasicStroke(1f));

            g2d.setFont(new Font("Monospaced", Font.BOLD, 11));
            g2d.setColor(new Color(210, 60, 60));
            g2d.drawString("(MUTED)", cx + iconSz + 68, midY + 5);
        }

        cy += iconSz + 12;
        return cy;
    }

    /**
     * Draws a simple speaker icon using basic shapes.
     * If muted, the icon is rendered in grey.
     */
    private void drawSpeakerIcon(Graphics2D g2d, int x, int y, int size, boolean muted) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color base = muted ? new Color(120, 110, 90) : new Color(220, 200, 100);
        g2d.setColor(base);

        // Speaker body (trapezoid)
        int bw = size / 3;
        int bh = size / 2;
        int bx = x + 2;
        int by = y + (size - bh) / 2;
        int[] xs = { bx, bx + bw, bx + bw, bx };
        int[] ys = { by + bh / 4, by, by + bh, by + bh - bh / 4 };
        g2d.fillPolygon(xs, ys, 4);

        // Cone (triangle to the right)
        int cx2 = bx + bw;
        int[] cxs = { cx2, cx2 + size / 3, cx2 + size / 3, cx2 };
        int[] cys = { by, by - size / 6, by + bh + size / 6, by + bh };
        g2d.fillPolygon(cxs, cys, 4);

        if (!muted) {
            // Sound waves
            g2d.setColor(new Color(220, 200, 100, 200));
            g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int wx = cx2 + size / 3 + 3;
            int wy = y + size / 2;
            g2d.drawArc(wx,     wy - 6,  10, 12, -60, 120);
            g2d.drawArc(wx + 5, wy - 10, 14, 20, -60, 120);
            g2d.setStroke(new BasicStroke(1f));
        }
    }

    // -------------------------------------------------------------------------
    // ACTION ICONS
    // -------------------------------------------------------------------------

    /**
     * Draws the icon for a given action row using sprites already loaded in SpriteManager.
     * Row 0 = Move (WASD keys drawn), 1 = Fire Sphere, 2 = Aura Spell, 3 = Staff.
     */
    private void drawActionIcon(Graphics2D g2d, int row, int x, int y, int size) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        SpriteManager sm = SpriteManager.getInstance();

        switch (row) {
            case 0 -> drawWASDIcon(g2d, x, y, size);
            case 1 -> {
                // Fire Sphere = Bomb sprite (CONSUMABLES frame 0)
                BufferedImage img = sm.getSprite("CONSUMABLES", 0);
                drawSpriteIcon(g2d, img, x, y, size);
            }
            case 2 -> {
                // Aura Spell = Aura projectile RIGHT sprite (AURA_RIGHT frame 0)
                BufferedImage img = sm.getSprite("AURA_RIGHT", 0);
                drawSpriteIcon(g2d, img, x, y, size);
            }
            case 3 -> {
                // Staff = dedicated staff icon; fall back to procedural drawing
                BufferedImage img = sm.getSprite("STAFF_ICON", 0);
                if (img != null) {
                    drawSpriteIcon(g2d, img, x, y, size);
                } else {
                    drawStaffIcon(g2d, x, y, size);
                }
            }
        }
    }

    private void drawSpriteIcon(Graphics2D g2d, BufferedImage img, int x, int y, int size) {
        if (img != null) {
            g2d.drawImage(img, x, y, size, size, null);
        } else {
            // Fallback: coloured circle
            g2d.setColor(new Color(180, 140, 60, 180));
            g2d.fillOval(x, y, size, size);
        }
    }

    /**
     * Draws a magic staff icon procedurally:
     * brown diagonal pole, yellow band near the orb, cyan glowing orb at top-right.
     * Matches the reference asset provided (brown wand with blue-glowing head).
     */
    private void drawStaffIcon(Graphics2D g2d, int x, int y, int size) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Pole — thick brown diagonal line from bottom-left to upper-right
        int poleThick = Math.max(4, size / 8);
        g2d.setColor(new Color(120, 75, 30));
        g2d.setStroke(new BasicStroke(poleThick, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int x1 = x + size / 6;
        int y1 = y + size - size / 6;
        int x2 = x + size - size / 4;
        int y2 = y + size / 3;
        g2d.drawLine(x1, y1, x2, y2);
        g2d.setStroke(new BasicStroke(1f));

        // Yellow band near the orb end
        int bw = poleThick + 4;
        int bx = x2 - bw / 2;
        int by = y2 + 2;
        g2d.setColor(new Color(240, 200, 30));
        g2d.fillOval(bx, by, bw, bw / 2 + 2);

        // Cyan orb glow (outer halo)
        int orbR = size / 4;
        int orbX = x2 - orbR / 2;
        int orbY = y + size / 8 - orbR / 2;
        g2d.setColor(new Color(100, 220, 255, 90));
        g2d.fillOval(orbX - 3, orbY - 3, orbR + 6, orbR + 6);

        // Cyan orb core
        g2d.setColor(new Color(80, 200, 255));
        g2d.fillOval(orbX, orbY, orbR, orbR);

        // Orb highlight
        g2d.setColor(new Color(210, 245, 255, 200));
        g2d.fillOval(orbX + orbR / 5, orbY + orbR / 6, orbR / 3, orbR / 3);
    }


    /** Draws a compact WASD key group icon. */
    private void drawWASDIcon(Graphics2D g2d, int x, int y, int size) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int ks = size / 3 - 1; // key size
        int gap = 1;

        // 3 rows: [W], [A S D]
        int[][] grid = {
            { x + ks + gap, y             }, // W  (top centre)
            { x,            y + ks + gap  }, // A  (mid left)
            { x + ks + gap, y + ks + gap  }, // S  (mid centre)
            { x + 2*(ks + gap), y + ks + gap }, // D (mid right)
        };
        String[] letters = { "W", "A", "S", "D" };

        for (int i = 0; i < 4; i++) {
            int kx = grid[i][0];
            int ky = grid[i][1];
            // Key background
            g2d.setColor(KEYBIND_BG);
            g2d.fillRoundRect(kx, ky, ks, ks, 4, 4);
            g2d.setColor(KEYBIND_BORDER);
            g2d.setStroke(new BasicStroke(1.2f));
            g2d.drawRoundRect(kx, ky, ks, ks, 4, 4);
            g2d.setStroke(new BasicStroke(1f));
            // Letter
            g2d.setFont(new Font("Monospaced", Font.BOLD, ks - 4));
            g2d.setColor(TEXT_WHITE);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(letters[i],
                    kx + (ks - fm.stringWidth(letters[i])) / 2,
                    ky + (ks - fm.getHeight()) / 2 + fm.getAscent());
        }
    }

    // -------------------------------------------------------------------------
    // BOTTOM BUTTONS
    // -------------------------------------------------------------------------

    private void drawBottomButtons(Graphics2D g2d, int px, int py) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int margin  = 20;
        int btnH    = 44;
        int quitW   = PANEL_W - 2 * margin;
        int resumeW = PANEL_W - 2 * margin;

        int quitX   = px + margin;
        int quitY   = py + PANEL_H - margin - btnH * 2 - 10;
        int resumeX = px + margin;
        int resumeY = py + PANEL_H - margin - btnH;

        quitRect   = new Rectangle(quitX,   quitY,   quitW, btnH);
        resumeRect = new Rectangle(resumeX, resumeY, resumeW, btnH);

        // QUIT GAME button (red)
        drawButton(g2d, quitX, quitY, quitW, btnH,
                BTN_RED, BTN_RED_DARK, "QUIT GAME");

        // RESUME button (green, more prominent)
        drawButton(g2d, resumeX, resumeY, resumeW, btnH,
                BTN_GREEN, BTN_GREEN_DARK, "RESUME");
    }

    private void drawButton(Graphics2D g2d,
                            int x, int y, int w, int h,
                            Color top, Color bottom, String label) {
        // Gradient fill
        GradientPaint gp = new GradientPaint(x, y, top, x, y + h, bottom);
        g2d.setPaint(gp);
        g2d.fillRoundRect(x, y, w, h, 12, 12);

        // Border
        g2d.setPaint(null);
        g2d.setColor(top.brighter());
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(x, y, w, h, 12, 12);
        g2d.setStroke(new BasicStroke(1f));

        // Label
        g2d.setFont(fontBtn);
        g2d.setColor(TEXT_WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        int tx = x + (w - fm.stringWidth(label)) / 2;
        int ty = y + (h - fm.getHeight()) / 2 + fm.getAscent();
        // Shadow
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.drawString(label, tx + 1, ty + 1);
        g2d.setColor(TEXT_WHITE);
        g2d.drawString(label, tx, ty);
    }

    // =========================================================================
    // RESULT ENUM
    // =========================================================================
    public enum ClickResult {
        NONE, RESUME, QUIT, REBIND_START, RESET_DEFAULTS
    }
}
