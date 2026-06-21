package goblinhunter.view;

import goblinhunter.controller.ControllerForView;
import goblinhunter.controller.IControllerForView;
import goblinhunter.utils.CharacterType;
import goblinhunter.utils.ViewConfig;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MenuDrawer {

    private static MenuDrawer instance = null;

    private static final Color BG_DARK      = new Color(28, 18, 10, 220);
    private static final Color BORDER_GOLD  = new Color(200, 165, 70);
    private static final Color BORDER_INNER = new Color(120, 90, 30);
    private static final Color BORDER_RED   = new Color(200, 50, 50);
    private static final Color LABEL_YELLOW = new Color(255, 210, 60);
    private static final Color TEXT_CREAM   = new Color(240, 235, 220);
    private static final Color ERROR_RED    = new Color(220, 60, 60);

    private static final int NAME_FIELD_W = 360;
    private static final int NAME_FIELD_H = 38;

    private Rectangle nameFieldRect = null;
    private boolean   showNameError = false;

    private final Font fontNameLabel;
    private final Font fontNameText;
    private final Font fontNameError;

    private MenuDrawer() {
        fontNameLabel = new Font("Monospaced", Font.BOLD, 12);
        fontNameText  = new Font("Monospaced", Font.BOLD, 16);
        fontNameError = new Font("Monospaced", Font.BOLD, 11);
    }

    public Rectangle getNameFieldRect()          { return nameFieldRect; }
    public void      setShowNameError(boolean v) { showNameError = v; }

    public static MenuDrawer getInstance() {
        if (instance == null) instance = new MenuDrawer();
        return instance;
    }

    /** Draws the full character-selection screen. Reads state from Model (read-only). */
    public void draw(Graphics2D g2d) {
        IControllerForView ctrl = ControllerForView.getInstance();
        SpriteManager sm = SpriteManager.getInstance();

        int selected = ctrl.getMenuSelectedIndex();

        BufferedImage bg = sm.getSprite("MENU_BG", 0);
        if (bg != null) {
            g2d.drawImage(bg,
                    ViewConfig.MENU_DRAW_X, ViewConfig.MENU_DRAW_Y,
                    ViewConfig.MENU_DRAW_W, ViewConfig.MENU_DRAW_H, null);
        }

        if (selected >= 0 && selected < CharacterType.values().length) {
            drawSelectionArrow(g2d, selected);
        }

        if (selected >= 0) {
            drawSelectedName(g2d, selected);
        }

        drawNameInput(g2d);
    }

    /**
     * Draws a pixel-art arcade-style down-arrow above the selected character.
     * Uses time-based bobbing and a pulsing colour for visual feedback.
     *
     * @param index selected character index (0-3)
     */
    private void drawSelectionArrow(Graphics2D g2d, int index) {
        int cx    = ViewConfig.FRAME_OFFSET_X + ViewConfig.CHAR_SELECTOR_X[index];
        int baseY = ViewConfig.MENU_DRAW_Y + ViewConfig.CHAR_FRAME_Y - 10;

        // vertical bobbing — ±6 px on a 250 ms sine cycle
        double bobOffset = 6.0 * Math.sin(System.currentTimeMillis() / 250.0);
        int arrowY = baseY + (int) bobOffset;

        int arrowW = 24;
        int arrowH = 18;
        int stemW  = 8;
        int stemH  = 12;

        Composite originalComposite = g2d.getComposite();

        // drop shadow
        int shadowOff = 2;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        g2d.setColor(Color.BLACK);
        g2d.fillRect(cx - stemW / 2 + shadowOff, arrowY - stemH + shadowOff, stemW, stemH);
        int[] sxShadow = { cx - arrowW / 2 + shadowOff, cx + arrowW / 2 + shadowOff, cx + shadowOff };
        int[] syShadow = { arrowY + shadowOff, arrowY + shadowOff, arrowY + arrowH + shadowOff };
        g2d.fillPolygon(sxShadow, syShadow, 3);
        g2d.setComposite(originalComposite);

        float pulse = 0.85f + 0.15f * (float) Math.abs(Math.sin(System.currentTimeMillis() / 300.0));
        Color arrowColor = new Color(
                Math.min(255, (int)(255 * pulse)),
                Math.min(255, (int)(200 * pulse)),
                0
        );
        g2d.setColor(arrowColor);

        g2d.fillRect(cx - stemW / 2, arrowY - stemH, stemW, stemH);

        int[] triX = { cx - arrowW / 2, cx + arrowW / 2, cx };
        int[] triY = { arrowY, arrowY, arrowY + arrowH };
        g2d.fillPolygon(triX, triY, 3);

        g2d.setColor(new Color(120, 80, 0));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRect(cx - stemW / 2, arrowY - stemH, stemW, stemH);
        g2d.drawPolygon(triX, triY, 3);
        g2d.setStroke(new BasicStroke(1));

        g2d.setColor(new Color(255, 255, 180, 160));
        g2d.fillRect(cx - stemW / 2 + 2, arrowY - stemH + 2, stemW - 4, 3);

        float sparkAlpha = 0.3f + 0.7f * (float) Math.abs(Math.sin(System.currentTimeMillis() / 200.0));
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sparkAlpha));
        g2d.setColor(new Color(255, 255, 100));
        int sparkSize = 3;
        g2d.fillRect(cx - arrowW / 2 - 4, arrowY + arrowH / 2, sparkSize, sparkSize);
        g2d.fillRect(cx + arrowW / 2 + 2, arrowY + arrowH / 2, sparkSize, sparkSize);
        g2d.setComposite(originalComposite);
    }

    private void drawSelectedName(Graphics2D g2d, int index) {
        CharacterType type = CharacterType.fromIndex(index);
        String name = type.getDisplayName();

        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(new Color(255, 215, 0));
        FontMetrics fm = g2d.getFontMetrics();

        int sx    = ViewConfig.MENU_DRAW_X + ViewConfig.CHAR_FRAME_X[index];
        int nameY = ViewConfig.MENU_DRAW_Y + ViewConfig.CHAR_FRAME_Y + ViewConfig.CHAR_FRAME_H + 20;
        int textX = sx + (ViewConfig.CHAR_FRAME_W - fm.stringWidth(name)) / 2;
        g2d.drawString(name, textX, nameY);
    }

    private void drawNameInput(Graphics2D g2d) {
        IControllerForView ctrl = ControllerForView.getInstance();

        int fieldX = ViewConfig.MENU_DRAW_X + (ViewConfig.MENU_DRAW_W - NAME_FIELD_W) / 2;
        int fieldY = ViewConfig.MENU_DRAW_Y + ViewConfig.CHAR_FRAME_Y + ViewConfig.CHAR_FRAME_H + 80;

        nameFieldRect = new Rectangle(fieldX, fieldY, NAME_FIELD_W, NAME_FIELD_H);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setFont(fontNameLabel);
        g2d.setColor(LABEL_YELLOW);
        g2d.drawString("INSERT YOUR NICKNAME:", fieldX, fieldY - 6);

        g2d.setColor(BG_DARK);
        g2d.fillRoundRect(fieldX, fieldY, NAME_FIELD_W, NAME_FIELD_H, 8, 8);

        Color borderColor;
        float strokeW;
        if (showNameError) {
            borderColor = BORDER_RED;
            strokeW = 2f;
        } else if (ctrl.isMenuTypingName()) {
            borderColor = BORDER_GOLD;
            strokeW = 2f;
        } else {
            borderColor = BORDER_INNER;
            strokeW = 1.5f;
        }
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(strokeW));
        g2d.drawRoundRect(fieldX, fieldY, NAME_FIELD_W, NAME_FIELD_H, 8, 8);
        g2d.setStroke(new BasicStroke(1f));

        g2d.setFont(fontNameText);
        g2d.setColor(TEXT_CREAM);
        FontMetrics fm = g2d.getFontMetrics();
        int textY = fieldY + (NAME_FIELD_H - fm.getHeight()) / 2 + fm.getAscent();
        String name = ctrl.getMenuPlayerName();
        // blinking cursor: visible for first 400 ms of each 800 ms cycle
        String display = (ctrl.isMenuTypingName() && System.currentTimeMillis() % 800 < 400)
                ? name + "|"
                : name;
        g2d.drawString(display, fieldX + 10, textY);

        if (showNameError) {
            g2d.setFont(fontNameError);
            g2d.setColor(ERROR_RED);
            g2d.drawString("⚠  Required field", fieldX, fieldY + NAME_FIELD_H + 14);
        }
    }

    /**
     * Returns the index (0-3) of the character frame that contains the given
     * screen coordinates, or -1 if none.
     */
    public int getCharacterIndexAt(int mouseX, int mouseY) {
        for (int i = 0; i < CharacterType.values().length; i++) {
            int sx = ViewConfig.MENU_DRAW_X + ViewConfig.CHAR_FRAME_X[i];
            int sy = ViewConfig.MENU_DRAW_Y + ViewConfig.CHAR_FRAME_Y;

            if (mouseX >= sx && mouseX <= sx + ViewConfig.CHAR_FRAME_W
                    && mouseY >= sy && mouseY <= sy + ViewConfig.CHAR_FRAME_H) {
                return i;
            }
        }
        return -1;
    }

    /** Returns true if the coordinates land on the Start Game button. */
    public boolean isStartGameButtonAt(int mouseX, int mouseY) {
        int bx = ViewConfig.MENU_DRAW_X + ViewConfig.NEW_GAME_BTN_X;
        int by = ViewConfig.MENU_DRAW_Y + ViewConfig.NEW_GAME_BTN_Y;
        return mouseX >= bx && mouseX <= bx + ViewConfig.NEW_GAME_BTN_W
                && mouseY >= by && mouseY <= by + ViewConfig.NEW_GAME_BTN_H;
    }

}
