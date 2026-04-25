package view;

import model.MenuModel;
import utils.CharacterType;
import utils.ViewConfig;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Componente View del menu di selezione personaggio (MVC).
 *
 * Effetti visivi:
 * - Hover glow (pulsante, temporaneo): quando il mouse è sopra un riquadro
 * - Selection border (persistente, dorato): quando un personaggio è stato cliccato
 * - Combinazione: se hover e selezione coincidono, gli effetti si sommano
 *
 * I personaggi sono già presenti nell'immagine StartGame.png.
 */
public class MenuDrawer {

    private static MenuDrawer instance = null;

    private MenuDrawer() {}

    public static MenuDrawer getInstance() {
        if (instance == null) instance = new MenuDrawer();
        return instance;
    }

    // =========================================================================
    // RENDERING
    // =========================================================================

    public void draw(Graphics2D g2d) {
        MenuModel model = MenuModel.getInstance();
        SpriteManager sm = SpriteManager.getInstance();

        int hovered = model.getHoveredIndex();
        int clicked = model.getClickedIndex();

        // 1. SFONDO: StartGame.png scalato nell'area del Cabinet
        BufferedImage bg = sm.getSprite("MENU_BG", 0);
        if (bg != null) {
            g2d.drawImage(bg,
                    ViewConfig.MENU_DRAW_X, ViewConfig.MENU_DRAW_Y,
                    ViewConfig.MENU_DRAW_W, ViewConfig.MENU_DRAW_H, null);
        }

        // 2. SELEZIONE PERSISTENTE: bordo dorato sul personaggio cliccato
        if (clicked >= 0 && clicked < CharacterType.values().length) {
            drawSelectionBorder(g2d, clicked);
        }

        // 3. HOVER GLOW: effetto temporaneo sul riquadro sotto il cursore
        if (hovered >= 0 && hovered < CharacterType.values().length) {
            drawHoverGlow(g2d, hovered);
        }

        // 4. NOME PERSONAGGIO: mostra il nome del personaggio selezionato (click)
        if (clicked >= 0) {
            drawSelectedName(g2d, clicked);
        }

        // 5. ISTRUZIONI
        drawInstructions(g2d, clicked);
    }

    // =========================================================================
    // EFFETTO SELEZIONE PERSISTENTE (click)
    // =========================================================================

    /**
     * Bordo dorato spesso + lieve glow fisso sul riquadro del personaggio
     * selezionato con click. Rimane visibile anche quando il mouse si sposta.
     */
    private void drawSelectionBorder(Graphics2D g2d, int index) {
        int sx = ViewConfig.MENU_DRAW_X + ViewConfig.CHAR_FRAME_X[index];
        int sy = ViewConfig.MENU_DRAW_Y + ViewConfig.CHAR_FRAME_Y;
        int sw = ViewConfig.CHAR_FRAME_W;
        int sh = ViewConfig.CHAR_FRAME_H;

        // Glow fisso (leggero)
        Composite original = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
        g2d.setColor(new Color(255, 215, 0)); // Oro
        g2d.fillRect(sx, sy, sw, sh);
        g2d.setComposite(original);

        // Bordo dorato spesso (indicatore di conferma)
        g2d.setColor(new Color(255, 215, 0, 230));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRect(sx - 3, sy - 3, sw + 6, sh + 6);
        g2d.setStroke(new BasicStroke(1));
    }

    // =========================================================================
    // EFFETTO HOVER (cursore sopra il riquadro)
    // =========================================================================

    /**
     * Overlay bianco pulsante + bordo sottile.
     * Effetto temporaneo che segue il cursore.
     */
    private void drawHoverGlow(Graphics2D g2d, int index) {
        int sx = ViewConfig.MENU_DRAW_X + ViewConfig.CHAR_FRAME_X[index];
        int sy = ViewConfig.MENU_DRAW_Y + ViewConfig.CHAR_FRAME_Y;
        int sw = ViewConfig.CHAR_FRAME_W;
        int sh = ViewConfig.CHAR_FRAME_H;

        // Glow pulsante bianco
        float pulse = 0.06f + 0.06f
                * (float) Math.abs(Math.sin(System.currentTimeMillis() / 300.0));
        Composite original = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulse));
        g2d.setColor(Color.WHITE);
        g2d.fillRect(sx, sy, sw, sh);
        g2d.setComposite(original);

        // Bordo bianco sottile
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(sx - 1, sy - 1, sw + 2, sh + 2);
        g2d.setStroke(new BasicStroke(1));
    }

    // =========================================================================
    // TESTO
    // =========================================================================

    /**
     * Mostra il nome del personaggio selezionato sotto il suo riquadro.
     */
    private void drawSelectedName(Graphics2D g2d, int index) {
        CharacterType type = CharacterType.fromIndex(index);
        String name = type.getDisplayName();

        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(new Color(255, 215, 0)); // Oro
        FontMetrics fm = g2d.getFontMetrics();

        int sx = ViewConfig.MENU_DRAW_X + ViewConfig.CHAR_FRAME_X[index];
        int nameY = ViewConfig.MENU_DRAW_Y + ViewConfig.CHAR_FRAME_Y
                + ViewConfig.CHAR_FRAME_H + 20;
        int textX = sx + (ViewConfig.CHAR_FRAME_W - fm.stringWidth(name)) / 2;
        g2d.drawString(name, textX, nameY);
    }

    /**
     * Istruzioni in basso: cambiano in base allo stato di selezione.
     */
    private void drawInstructions(Graphics2D g2d, int clickedIndex) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 13));
        g2d.setColor(new Color(210, 210, 210));

        String instructions;
        if (clickedIndex >= 0) {
            instructions = "Clicca START GAME per iniziare!";
        } else {
            instructions = "Clicca un personaggio per selezionarlo";
        }

        FontMetrics fm = g2d.getFontMetrics();
        int tx = ViewConfig.MENU_DRAW_X
                + (ViewConfig.MENU_DRAW_W - fm.stringWidth(instructions)) / 2;
        int ty = ViewConfig.MENU_DRAW_Y + ViewConfig.MENU_DRAW_H - 25;
        g2d.drawString(instructions, tx, ty);
    }

    // =========================================================================
    // HIT-TESTING
    // =========================================================================

    /**
     * Determina quale riquadro personaggio si trova alle coordinate date.
     * @return indice (0-3), oppure -1 se fuori dai riquadri.
     */
    public int getFrameIndexAt(int mouseX, int mouseY) {
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

    /**
     * Verifica se le coordinate cadono sul pulsante "Start Game" / "NEW GAME".
     */
    public boolean isNewGameButtonAt(int mouseX, int mouseY) {
        int bx = ViewConfig.MENU_DRAW_X + ViewConfig.NEW_GAME_BTN_X;
        int by = ViewConfig.MENU_DRAW_Y + ViewConfig.NEW_GAME_BTN_Y;
        return mouseX >= bx && mouseX <= bx + ViewConfig.NEW_GAME_BTN_W
                && mouseY >= by && mouseY <= by + ViewConfig.NEW_GAME_BTN_H;
    }
}
