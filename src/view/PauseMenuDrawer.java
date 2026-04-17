package view;

import controller.PauseController;
import model.PauseModel;
import utils.Config;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Disegna l'overlay del menu di pausa sul contesto {@code Graphics2D} del gioco.
 *
 * <h3>Responsabilità (View pura)</h3>
 * <ul>
 *   <li>Rendering Java2D dell'intero pannello: nessun componente Swing dinamico.</li>
 *   <li>Nessuna logica di business: identificazione dei click → {@link PauseController}.</li>
 * </ul>
 *
 * <h3>Layout</h3>
 * <pre>
 *   [CONTROLS — 7 righe]
 *   ↑ Move Up      [UP]
 *   ↓ Move Down    [DOWN]
 *   ← Move Left    [LEFT]
 *   → Move Right   [RIGHT]
 *   💣 Fire Sphere [SPACE]
 *   ○  Aura Spell  [X]
 *   🔧 Staff Attack [Z]
 *        [RESET DEFAULTS]
 *   ─────────────────────
 *   [AUDIO]  🔊  🔇
 *   ─────────────────────
 *   [RETURN TO MAIN MENU]
 *   [QUIT GAME]
 *   [RESUME]
 * </pre>
 */
public class PauseMenuDrawer {

    // =========================================================================
    // Singleton
    // =========================================================================
    private static PauseMenuDrawer instance;
    public static PauseMenuDrawer getInstance() {
        if (instance == null) instance = new PauseMenuDrawer();
        return instance;
    }

    // =========================================================================
    // Dimensioni pannello
    // =========================================================================
    private static final int PANEL_W  = 440;
    private static final int PANEL_H  = 700;
    private static final int CORNER_R = 16;

    // =========================================================================
    // Palette colori
    // =========================================================================
    private static final Color BG_DARK         = new Color(28, 18, 10, 240);
    private static final Color BORDER_GOLD     = new Color(200, 165, 70);
    private static final Color BORDER_INNER    = new Color(120, 90, 30);
    private static final Color SECTION_YELLOW  = new Color(255, 210, 60);
    private static final Color TEXT_WHITE      = new Color(240, 235, 220);
    private static final Color TEXT_GRAY       = new Color(130, 120, 100);
    private static final Color KEYBIND_BG      = new Color(50, 38, 20, 220);
    private static final Color KEYBIND_BORDER  = new Color(160, 130, 50);
    private static final Color KEYBIND_ACTIVE  = new Color(90, 70, 30, 240);
    private static final Color KEYBIND_ACT_BR  = new Color(255, 210, 60);
    private static final Color BTN_RED         = new Color(155, 25, 25);
    private static final Color BTN_RED_DARK    = new Color(100, 15, 15);
    private static final Color BTN_GREEN       = new Color(30, 130, 40);
    private static final Color BTN_GREEN_DARK  = new Color(15, 85, 25);
    private static final Color BTN_ORANGE      = new Color(185, 95, 10);
    private static final Color BTN_ORANGE_DARK = new Color(130, 60, 5);
    private static final Color BTN_SECONDARY   = new Color(60, 50, 30, 220);
    private static final Color AUDIO_ON_COLOR  = new Color(220, 200, 100);
    private static final Color AUDIO_OFF_COLOR = new Color(110, 100, 80);

    // =========================================================================
    // Rettangoli cliccabili (calcolati in draw())
    // =========================================================================
    private final Rectangle[] keybindRects = new Rectangle[PauseModel.ACTION_COUNT]; // 7
    private Rectangle resetRect;
    private Rectangle audioOnRect;
    private Rectangle audioOffRect;
    private Rectangle mainMenuRect;
    private Rectangle quitRect;
    private Rectangle resumeRect;

    // =========================================================================
    // Font cache
    // =========================================================================
    private final Font fontTitle;
    private final Font fontSection;
    private final Font fontLabel;
    private final Font fontKey;
    private final Font fontBtn;
    private final Font fontSmall;

    private PauseMenuDrawer() {
        fontTitle   = new Font("Monospaced", Font.BOLD, 28);
        fontSection = new Font("Monospaced", Font.BOLD, 12);
        fontLabel   = new Font("Monospaced", Font.PLAIN, 11);
        fontKey     = new Font("Monospaced", Font.BOLD, 11);
        fontBtn     = new Font("Monospaced", Font.BOLD, 15);
        fontSmall   = new Font("Monospaced", Font.BOLD, 10);
    }

    // =========================================================================
    // PUBLIC API ——— entry points chiamati da ConcreteDrawer e GamePanel
    // =========================================================================

    /** Disegna l'intero overlay del menu di pausa. Chiamato ad ogni repaint durante la pausa. */
    public void draw(Graphics2D g2d, PauseController ctrl) {
        int screenW = Config.WINDOW_PREFERRED_WIDTH;
        int screenH = Config.WINDOW_PREFERRED_HEIGHT;
        int px = (screenW - PANEL_W) / 2;
        int py = (screenH - PANEL_H) / 2;

        // Oscuramento sfondo
        Composite orig = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f));
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, screenW, screenH);
        g2d.setComposite(orig);

        drawPanel(g2d, px, py);

        int cx = px + 22;
        int cy = py + 28;

        cy = drawTitle(g2d, px, cy);
        cy = drawDivider(g2d, cx, cy, PANEL_W - 44);
        cy += 6;
        cy = drawControlsSection(g2d, cx, cy, ctrl);   // unico pannello 7 righe
        cy += 4;
        cy = drawDivider(g2d, cx, cy, PANEL_W - 44);
        cy += 6;
        drawAudioSection(g2d, cx, cy, ctrl);
        drawBottomButtons(g2d, px, py);
    }

    /**
     * Identifica l'elemento cliccato e lo restituisce come {@link ClickResult}.
     *
     * <p>Le azioni di lifecycle (RESUME / QUIT / MAIN_MENU) NON sono eseguite qui —
     * le esegue {@code GamePanel} che riceve il ClickResult.</p>
     *
     * <p>Le azioni interne al menu (rebind, reset, audio) sono delegate al {@code ctrl}.</p>
     */
    public ClickResult handleClick(int mx, int my, PauseController ctrl) {
        // Pulsanti di navigazione — solo identificazione
        if (resumeRect   != null && resumeRect.contains(mx, my))   return ClickResult.RESUME;
        if (quitRect     != null && quitRect.contains(mx, my))     return ClickResult.QUIT;
        if (mainMenuRect != null && mainMenuRect.contains(mx, my)) return ClickResult.RETURN_TO_MAIN_MENU;

        // Reset defaults — azione interna
        if (resetRect != null && resetRect.contains(mx, my)) {
            ctrl.onResetDefaultsClicked();
            return ClickResult.RESET_DEFAULTS;
        }

        // Toggle audio — azione interna
        if (audioOnRect  != null && audioOnRect.contains(mx, my))  { ctrl.setAudioEnabled(true);  return ClickResult.TOGGLE_AUDIO; }
        if (audioOffRect != null && audioOffRect.contains(mx, my)) { ctrl.setAudioEnabled(false); return ClickResult.TOGGLE_AUDIO; }

        // Keybind — 7 azioni unificate
        for (int i = 0; i < keybindRects.length; i++) {
            if (keybindRects[i] != null && keybindRects[i].contains(mx, my)) {
                ctrl.startRebind(i);
                return ClickResult.REBIND_START;
            }
        }

        return ClickResult.NONE;
    }

    /**
     * Gestisce un tasto premuto durante la modalità rebind.
     * La View cattura il keyName grezzo e lo passa al Controller — non sa cosa farne.
     */
    public void handleKeyForRebind(String keyName, PauseController ctrl) {
        ctrl.commitRebind(keyName);
    }

    // =========================================================================
    // DRAWING — Panel frame
    // =========================================================================

    private void drawPanel(Graphics2D g2d, int px, int py) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Ombra
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRoundRect(px + 6, py + 6, PANEL_W, PANEL_H, CORNER_R + 4, CORNER_R + 4);
        // Sfondo
        g2d.setColor(BG_DARK);
        g2d.fillRoundRect(px, py, PANEL_W, PANEL_H, CORNER_R, CORNER_R);
        // Bordo gold esterno
        g2d.setColor(BORDER_GOLD);
        g2d.setStroke(new BasicStroke(3f));
        g2d.drawRoundRect(px, py, PANEL_W, PANEL_H, CORNER_R, CORNER_R);
        // Bordo interno
        g2d.setColor(BORDER_INNER);
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawRoundRect(px + 5, py + 5, PANEL_W - 10, PANEL_H - 10, CORNER_R - 4, CORNER_R - 4);
        // Angoli ornamentali
        int cr = 7;
        g2d.setColor(BORDER_GOLD);
        g2d.fillOval(px - cr,          py - cr,          cr * 2, cr * 2);
        g2d.fillOval(px + PANEL_W - cr, py - cr,          cr * 2, cr * 2);
        g2d.fillOval(px - cr,          py + PANEL_H - cr, cr * 2, cr * 2);
        g2d.fillOval(px + PANEL_W - cr, py + PANEL_H - cr, cr * 2, cr * 2);
        g2d.setStroke(new BasicStroke(1f));
    }

    // =========================================================================
    // DRAWING — Title
    // =========================================================================

    private int drawTitle(Graphics2D g2d, int panelX, int cy) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        String title = "|| PAUSE";
        g2d.setFont(fontTitle);
        FontMetrics fm = g2d.getFontMetrics();
        int tx = panelX + (PANEL_W - fm.stringWidth(title)) / 2;
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.drawString(title, tx + 2, cy + fm.getAscent() + 2);
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

    // =========================================================================
    // DRAWING — CONTROLS section — pannello unico, 7 azioni
    // =========================================================================

    private int drawControlsSection(Graphics2D g2d, int cx, int cy, PauseController ctrl) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Header
        g2d.setFont(fontSection);
        g2d.setColor(SECTION_YELLOW);
        g2d.drawString("CONTROLS", cx, cy + 13);
        cy += 20;

        int iconSize  = 30;
        int rowH      = 40;
        int keybindW  = 92;
        int keybindH  = 26;
        int iconX     = cx;
        int labelX    = cx + iconSize + 8;
        int keybindX  = cx + PANEL_W - 44 - keybindW;

        for (int i = 0; i < PauseModel.ACTION_COUNT; i++) {
            int rowY = cy + i * rowH;
            int midY = rowY + rowH / 2;

            // Separator leggero tra i gruppi (dopo il 4° → tra movimento e azioni)
            if (i == 4) {
                g2d.setColor(new Color(120, 90, 30, 80));
                g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
                        0, new float[]{3, 4}, 0));
                g2d.drawLine(cx, rowY - 4, cx + PANEL_W - 44, rowY - 4);
                g2d.setStroke(new BasicStroke(1f));
            }

            // Icona a sinistra
            drawActionIcon(g2d, i, iconX, rowY + (rowH - iconSize) / 2, iconSize);

            // Etichetta azione
            g2d.setFont(fontLabel);
            g2d.setColor(TEXT_WHITE);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(PauseModel.ACTION_LABELS[i], labelX, midY + fm.getAscent() / 2 - 1);

            // Box keybind
            int bx = keybindX;
            int by = midY - keybindH / 2;
            keybindRects[i] = new Rectangle(bx, by, keybindW, keybindH);

            boolean rebinding = (ctrl.getRebindingRow() == i);
            drawKeybindBox(g2d, bx, by, keybindW, keybindH,
                    rebinding ? "PRESS KEY..." : ctrl.getActionLabel(i),
                    rebinding);
        }

        cy += PauseModel.ACTION_COUNT * rowH + 4;

        // Bottone RESET DEFAULTS
        int rdW = 170;
        int rdH = 26;
        int rdX = cx + (PANEL_W - 44 - rdW) / 2;
        resetRect = new Rectangle(rdX, cy, rdW, rdH);

        g2d.setColor(BTN_SECONDARY);
        g2d.fillRoundRect(rdX, cy, rdW, rdH, 8, 8);
        g2d.setColor(BORDER_GOLD);
        g2d.setStroke(new BasicStroke(1.2f));
        g2d.drawRoundRect(rdX, cy, rdW, rdH, 8, 8);
        g2d.setStroke(new BasicStroke(1f));

        g2d.setFont(fontSmall);
        g2d.setColor(new Color(210, 180, 80));
        FontMetrics rfm = g2d.getFontMetrics();
        String rdLabel = "RESET DEFAULTS";
        g2d.drawString(rdLabel,
                rdX + (rdW - rfm.stringWidth(rdLabel)) / 2,
                cy + (rdH - rfm.getHeight()) / 2 + rfm.getAscent());

        cy += rdH + 8;
        return cy;
    }

    // =========================================================================
    // DRAWING — AUDIO section
    // =========================================================================

    private int drawAudioSection(Graphics2D g2d, int cx, int cy, PauseController ctrl) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setFont(fontSection);
        g2d.setColor(SECTION_YELLOW);
        g2d.drawString("AUDIO", cx, cy + 13);
        cy += 18;

        boolean audioOn = ctrl.isAudioEnabled();

        int sizeOn  = audioOn ? 42 : 28;
        int sizeOff = audioOn ? 28 : 42;

        int iconOnX  = cx;
        int iconOnY  = cy + (42 - sizeOn) / 2;
        int iconOffX = cx + 52;
        int iconOffY = cy + (42 - sizeOff) / 2;

        // Sfondo selezione
        g2d.setColor(new Color(100, 80, 20, audioOn ? 130 : 0));
        g2d.fillRoundRect(iconOnX - 3, cy - 2, sizeOn + 6, 44, 6, 6);
        g2d.setColor(new Color(100, 80, 20, audioOn ? 0 : 130));
        g2d.fillRoundRect(iconOffX - 3, cy - 2, sizeOff + 6, 44, 6, 6);

        // Icona ON
        Composite origComp = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, audioOn ? 1.0f : 0.45f));
        drawSpeakerIcon(g2d, iconOnX, iconOnY, sizeOn, false);

        // Icona OFF
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, audioOn ? 0.45f : 1.0f));
        drawSpeakerIcon(g2d, iconOffX, iconOffY, sizeOff, true);
        g2d.setComposite(origComp);

        audioOnRect  = new Rectangle(iconOnX  - 3, cy - 2, sizeOn  + 6, 46);
        audioOffRect = new Rectangle(iconOffX - 3, cy - 2, sizeOff + 6, 46);

        // Stato testuale
        g2d.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2d.setColor(audioOn ? AUDIO_ON_COLOR : AUDIO_OFF_COLOR);
        g2d.drawString(audioOn ? "ON" : "OFF", cx + 104, cy + 26);

        cy += 48;
        return cy;
    }

    // =========================================================================
    // DRAWING — Bottom buttons
    // =========================================================================

    private void drawBottomButtons(Graphics2D g2d, int px, int py) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int margin = 18;
        int btnH   = 40;
        int btnW   = PANEL_W - 2 * margin;
        int gap    = 8;

        int resumeY   = py + PANEL_H - margin - btnH;
        int quitY     = resumeY - btnH - gap;
        int mainMenuY = quitY   - btnH - gap;

        resumeRect   = new Rectangle(px + margin, resumeY,   btnW, btnH);
        quitRect     = new Rectangle(px + margin, quitY,     btnW, btnH);
        mainMenuRect = new Rectangle(px + margin, mainMenuY, btnW, btnH);

        drawButton(g2d, px + margin, mainMenuY, btnW, btnH, BTN_ORANGE, BTN_ORANGE_DARK, "RETURN TO MAIN MENU");
        drawButton(g2d, px + margin, quitY,     btnW, btnH, BTN_RED,    BTN_RED_DARK,    "QUIT GAME");
        drawButton(g2d, px + margin, resumeY,   btnW, btnH, BTN_GREEN,  BTN_GREEN_DARK,  "RESUME");
    }

    private void drawButton(Graphics2D g2d, int x, int y, int w, int h,
                            Color top, Color bottom, String label) {
        g2d.setPaint(new GradientPaint(x, y, top, x, y + h, bottom));
        g2d.fillRoundRect(x, y, w, h, 12, 12);
        g2d.setPaint(null);
        g2d.setColor(top.brighter());
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(x, y, w, h, 12, 12);
        g2d.setStroke(new BasicStroke(1f));

        g2d.setFont(fontBtn);
        FontMetrics fm = g2d.getFontMetrics();
        int tx = x + (w - fm.stringWidth(label)) / 2;
        int ty = y + (h - fm.getHeight()) / 2 + fm.getAscent();
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.drawString(label, tx + 1, ty + 1);
        g2d.setColor(TEXT_WHITE);
        g2d.drawString(label, tx, ty);
    }

    // =========================================================================
    // DRAWING — Keybind box riutilizzabile
    // =========================================================================

    private void drawKeybindBox(Graphics2D g2d, int bx, int by, int bw, int bh,
                                String text, boolean active) {
        g2d.setColor(active ? KEYBIND_ACTIVE : KEYBIND_BG);
        g2d.fillRoundRect(bx, by, bw, bh, 8, 8);
        g2d.setColor(active ? KEYBIND_ACT_BR : KEYBIND_BORDER);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(bx, by, bw, bh, 8, 8);
        g2d.setStroke(new BasicStroke(1f));

        g2d.setFont(fontKey);
        g2d.setColor(active ? new Color(255, 220, 80) : TEXT_WHITE);
        FontMetrics kfm = g2d.getFontMetrics();
        String display = text;
        while (kfm.stringWidth(display) > bw - 6 && display.length() > 1)
            display = display.substring(0, display.length() - 1);
        g2d.drawString(display,
                bx + (bw - kfm.stringWidth(display)) / 2,
                by + (bh - kfm.getHeight()) / 2 + kfm.getAscent());
    }

    // =========================================================================
    // DRAWING — Action icons (7 azioni)
    // =========================================================================

    private void drawActionIcon(Graphics2D g2d, int row, int x, int y, int size) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        switch (row) {
            case PauseModel.ACTION_MOVE_UP    -> drawArrowIcon(g2d, x, y, size, 0);
            case PauseModel.ACTION_MOVE_DOWN  -> drawArrowIcon(g2d, x, y, size, 1);
            case PauseModel.ACTION_MOVE_LEFT  -> drawArrowIcon(g2d, x, y, size, 2);
            case PauseModel.ACTION_MOVE_RIGHT -> drawArrowIcon(g2d, x, y, size, 3);
            case PauseModel.ACTION_BOMB -> {
                BufferedImage img = SpriteManager.getInstance().getSprite("HUD_FIRE_SPELL", 0);
                drawSpriteIcon(g2d, img, x, y, size);
            }
            case PauseModel.ACTION_AURA -> {
                BufferedImage img = SpriteManager.getInstance().getSprite("HUD_AURA_SPELL", 0);
                drawSpriteIcon(g2d, img, x, y, size);
            }
            case PauseModel.ACTION_STAFF -> {
                BufferedImage img = SpriteManager.getInstance().getSprite("STAFF_ICON", 0);
                if (img != null) drawSpriteIcon(g2d, img, x, y, size);
                else             drawStaffIcon(g2d, x, y, size);
            }
        }
    }

    /**
     * Disegna un'icona a freccia direzionale procedurale.
     *
     * @param dir 0=Su, 1=Giù, 2=Sinistra, 3=Destra
     */
    private void drawArrowIcon(Graphics2D g2d, int x, int y, int size, int dir) {
        int cx = x + size / 2;
        int cy = y + size / 2;
        int r  = size / 2 - 3;

        // Background circle
        g2d.setColor(KEYBIND_BG);
        g2d.fillOval(x, y, size, size);
        g2d.setColor(KEYBIND_BORDER);
        g2d.setStroke(new BasicStroke(1.2f));
        g2d.drawOval(x, y, size, size);
        g2d.setStroke(new BasicStroke(1f));

        // Freccia (triangolo pieno)
        int[] xs, ys;
        switch (dir) {
            case 0 -> { xs = new int[]{cx, cx - r, cx + r}; ys = new int[]{cy - r, cy + r, cy + r}; }
            case 1 -> { xs = new int[]{cx, cx - r, cx + r}; ys = new int[]{cy + r, cy - r, cy - r}; }
            case 2 -> { xs = new int[]{cx - r, cx + r, cx + r}; ys = new int[]{cy, cy - r, cy + r}; }
            default -> { xs = new int[]{cx + r, cx - r, cx - r}; ys = new int[]{cy, cy - r, cy + r}; }
        }
        g2d.setColor(SECTION_YELLOW);
        g2d.fillPolygon(xs, ys, 3);
    }

    private void drawSpriteIcon(Graphics2D g2d, BufferedImage img, int x, int y, int size) {
        if (img != null) {
            g2d.drawImage(img, x, y, size, size, null);
        } else {
            g2d.setColor(new Color(180, 140, 60, 180));
            g2d.fillOval(x, y, size, size);
        }
    }

    private void drawStaffIcon(Graphics2D g2d, int x, int y, int size) {
        int poleThick = Math.max(4, size / 8);
        g2d.setColor(new Color(120, 75, 30));
        g2d.setStroke(new BasicStroke(poleThick, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int x1 = x + size / 6, y1 = y + size - size / 6;
        int x2 = x + size - size / 4, y2 = y + size / 3;
        g2d.drawLine(x1, y1, x2, y2);
        g2d.setStroke(new BasicStroke(1f));
        int bw = poleThick + 4;
        g2d.setColor(new Color(240, 200, 30));
        g2d.fillOval(x2 - bw / 2, y2 + 2, bw, bw / 2 + 2);
        int orbR = size / 4;
        int orbX = x2 - orbR / 2, orbY = y + size / 8 - orbR / 2;
        g2d.setColor(new Color(100, 220, 255, 90));
        g2d.fillOval(orbX - 3, orbY - 3, orbR + 6, orbR + 6);
        g2d.setColor(new Color(80, 200, 255));
        g2d.fillOval(orbX, orbY, orbR, orbR);
        g2d.setColor(new Color(210, 245, 255, 200));
        g2d.fillOval(orbX + orbR / 5, orbY + orbR / 6, orbR / 3, orbR / 3);
    }

    // =========================================================================
    // DRAWING — Speaker icon
    // =========================================================================

    private void drawSpeakerIcon(Graphics2D g2d, int x, int y, int size, boolean muted) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(muted ? new Color(120, 110, 90) : new Color(220, 200, 100));
        int bw = size / 3, bh = size / 2;
        int bx = x + 2, by = y + (size - bh) / 2;
        g2d.fillPolygon(new int[]{bx, bx + bw, bx + bw, bx},
                        new int[]{by + bh / 4, by, by + bh, by + bh - bh / 4}, 4);
        int cx2 = bx + bw;
        g2d.fillPolygon(new int[]{cx2, cx2 + size / 3, cx2 + size / 3, cx2},
                        new int[]{by, by - size / 6, by + bh + size / 6, by + bh}, 4);
        if (muted) {
            g2d.setColor(new Color(210, 30, 30));
            g2d.setStroke(new BasicStroke(Math.max(2f, size / 12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(x + size / 6, y + size / 6, x + size - size / 6, y + size - size / 6);
            g2d.drawLine(x + size - size / 6, y + size / 6, x + size / 6, y + size - size / 6);
            g2d.setStroke(new BasicStroke(1f));
        } else {
            g2d.setColor(new Color(220, 200, 100, 200));
            g2d.setStroke(new BasicStroke(Math.max(1.5f, size / 18f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawArc(cx2 + size / 3 + 2, y + size / 2 - 5,  9,  10, -60, 120);
            g2d.drawArc(cx2 + size / 3 + 6, y + size / 2 - 8, 12,  16, -60, 120);
            g2d.setStroke(new BasicStroke(1f));
        }
    }

    // =========================================================================
    // Enum risultati click
    // =========================================================================

    /**
     * Descrive l'elemento cliccato nel menu di pausa.
     * {@code GamePanel} usa questo enum per orchestrare le azioni di lifecycle.
     */
    public enum ClickResult {
        NONE,
        RESUME,
        QUIT,
        RETURN_TO_MAIN_MENU,
        REBIND_START,
        RESET_DEFAULTS,
        TOGGLE_AUDIO
    }
}
