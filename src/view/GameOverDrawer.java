package view;

import controller.ControllerForView;
import utils.Config;

import java.awt.*;

/**
 * Disegna l'overlay del Game Over sul contesto {@code Graphics2D} del gioco.
 * Mantiene lo stesso stile visivo del PauseMenuDrawer.
 */
public class GameOverDrawer {

    // =========================================================================
    // Singleton
    // =========================================================================
    private static GameOverDrawer instance;
    public static GameOverDrawer getInstance() {
        if (instance == null) instance = new GameOverDrawer();
        return instance;
    }

    // =========================================================================
    // Dimensioni pannello (Più piccolo del Pause Menu)
    // =========================================================================
    private static final int PANEL_W  = 440;
    private static final int PANEL_H  = 350;
    private static final int CORNER_R = 16;

    // =========================================================================
    // Palette colori (Copiata da PauseMenuDrawer)
    // =========================================================================
    private static final Color BG_DARK         = new Color(28, 18, 10, 240);
    private static final Color BORDER_GOLD     = new Color(200, 165, 70);
    private static final Color BORDER_INNER    = new Color(120, 90, 30);
    private static final Color SECTION_YELLOW  = new Color(255, 210, 60);
    private static final Color TEXT_WHITE      = new Color(240, 235, 220);
    
    private static final Color BTN_RED         = new Color(155, 25, 25);
    private static final Color BTN_RED_DARK    = new Color(100, 15, 15);
    private static final Color BTN_ORANGE      = new Color(185, 95, 10);
    private static final Color BTN_ORANGE_DARK = new Color(130, 60, 5);

    // =========================================================================
    // Rettangoli cliccabili
    // =========================================================================
    private Rectangle mainMenuRect;
    private Rectangle quitRect;

    // =========================================================================
    // Font cache
    // =========================================================================
    private final Font fontTitle;
    private final Font fontScore;
    private final Font fontBtn;

    private GameOverDrawer() {
        fontTitle   = new Font("Monospaced", Font.BOLD, 36);
        fontScore   = new Font("Monospaced", Font.BOLD, 20);
        fontBtn     = new Font("Monospaced", Font.BOLD, 15);
    }

    // =========================================================================
    // PUBLIC API
    // =========================================================================

    public void draw(Graphics2D g2d) {
        int screenW = Config.WINDOW_PREFERRED_WIDTH;
        int screenH = Config.WINDOW_PREFERRED_HEIGHT;
        int px = (screenW - PANEL_W) / 2;
        int py = (screenH - PANEL_H) / 2;

        // Oscuramento sfondo (come in PauseMenuDrawer)
        Composite orig = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f));
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, screenW, screenH);
        g2d.setComposite(orig);

        drawPanel(g2d, px, py);

        int cy = py + 50;

        cy = drawTitle(g2d, px, cy);
        cy = drawDivider(g2d, px + 22, cy, PANEL_W - 44);
        cy += 20;
        
        cy = drawScore(g2d, px, cy);
        
        drawBottomButtons(g2d, px, py);
    }

    /**
     * Identifica l'elemento cliccato e lo restituisce come ClickResult.
     */
    public PauseMenuDrawer.ClickResult handleClick(int mx, int my) {
        if (quitRect     != null && quitRect.contains(mx, my))     return PauseMenuDrawer.ClickResult.QUIT;
        if (mainMenuRect != null && mainMenuRect.contains(mx, my)) return PauseMenuDrawer.ClickResult.RETURN_TO_MAIN_MENU;
        return PauseMenuDrawer.ClickResult.NONE;
    }

    // =========================================================================
    // DRAWING
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

    private int drawTitle(Graphics2D g2d, int panelX, int cy) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        String title = "GAME OVER";
        g2d.setFont(fontTitle);
        FontMetrics fm = g2d.getFontMetrics();
        int tx = panelX + (PANEL_W - fm.stringWidth(title)) / 2;
        
        // Rosso scuro ombra per dare un po' l'effetto neon drammatico
        g2d.setColor(new Color(150, 0, 0, 160));
        g2d.drawString(title, tx + 3, cy + fm.getAscent() + 3);
        
        g2d.setColor(new Color(255, 60, 60)); // Rosso acceso
        g2d.drawString(title, tx, cy + fm.getAscent());
        
        return cy + fm.getHeight() + 15;
    }

    private int drawDivider(Graphics2D g2d, int x, int cy, int w) {
        g2d.setColor(BORDER_GOLD);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawLine(x, cy, x + w, cy);
        g2d.setStroke(new BasicStroke(1f));
        return cy + 10;
    }

    private int drawScore(Graphics2D g2d, int panelX, int cy) {
        g2d.setFont(fontScore);
        int score = ControllerForView.getInstance().getScore();
        String text = "CURRENT SCORE: " + score;
        FontMetrics fm = g2d.getFontMetrics();
        int tx = panelX + (PANEL_W - fm.stringWidth(text)) / 2;
        
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.drawString(text, tx + 2, cy + fm.getAscent() + 2);
        
        g2d.setColor(SECTION_YELLOW);
        g2d.drawString(text, tx, cy + fm.getAscent());
        
        return cy + fm.getHeight() + 20;
    }

    private void drawBottomButtons(Graphics2D g2d, int px, int py) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int margin = 18;
        int btnH   = 40;
        int btnW   = PANEL_W - 2 * margin;
        int gap    = 12;

        int quitY     = py + PANEL_H - margin - btnH;
        int mainMenuY = quitY   - btnH - gap;

        mainMenuRect = new Rectangle(px + margin, mainMenuY, btnW, btnH);
        quitRect     = new Rectangle(px + margin, quitY,     btnW, btnH);

        drawButton(g2d, px + margin, mainMenuY, btnW, btnH, BTN_ORANGE, BTN_ORANGE_DARK, "RETURN TO MAIN MENU");
        drawButton(g2d, px + margin, quitY,     btnW, btnH, BTN_RED,    BTN_RED_DARK,    "EXIT GAME");
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
}
