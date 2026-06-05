package view;

import model.MenuModel;
import utils.CharacterType;
import utils.ViewConfig;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Componente View del menu di selezione personaggio (MVC).
 *
 * Responsabilità ESCLUSIVE della View:
 * - Rendering dello sfondo (StartGame.png)
 * - Rendering della freccia selettore arcade sopra il personaggio selezionato
 * - Rendering del nome del personaggio e delle istruzioni
 * - Hit-testing: dato un punto (x,y) in coordinate schermo, determina su
 *   quale personaggio o pulsante si trova (restituisce un indice al Controller)
 *
 * NON modifica il Model direttamente: tutto passa attraverso il Controller.
 */
public class MenuDrawer {

    private static MenuDrawer instance = null;

    private MenuDrawer() {}

    public static MenuDrawer getInstance() {
        if (instance == null) instance = new MenuDrawer();
        return instance;
    }

    // =========================================================================
    // RENDERING PRINCIPALE
    // =========================================================================

    /**
     * Disegna l'intera schermata del menu di selezione.
     * Legge lo stato dal Model (sola lettura) per decidere cosa mostrare.
     */
    public void draw(Graphics2D g2d) {
        MenuModel model = MenuModel.getInstance();
        SpriteManager sm = SpriteManager.getInstance();

        int selected = model.getSelectedIndex();

        // 1. SFONDO: StartGame.png scalato nell'area del Cabinet
        BufferedImage bg = sm.getSprite("MENU_BG", 0);
        if (bg != null) {
            g2d.drawImage(bg,
                    ViewConfig.MENU_DRAW_X, ViewConfig.MENU_DRAW_Y,
                    ViewConfig.MENU_DRAW_W, ViewConfig.MENU_DRAW_H, null);
        }

        // 2. FRECCIA SELETTORE: punta al personaggio selezionato (click)
        if (selected >= 0 && selected < CharacterType.values().length) {
            drawSelectionArrow(g2d, selected);
        }

        // 3. NOME PERSONAGGIO: mostra il nome sotto il personaggio selezionato
        if (selected >= 0) {
            drawSelectedName(g2d, selected);
        }

        // 4. ISTRUZIONI
        drawInstructions(g2d, selected);
    }

    // =========================================================================
    // FRECCIA SELETTORE STILE ARCADE 2D
    // =========================================================================

    /**
     * Disegna una freccia/puntatore verso il basso in stile pixel-art arcade,
     * posizionata sopra il personaggio selezionato. La freccia ha un'animazione
     * di "bobbing" verticale per dare un feedback dinamico tipico dei giochi 2D.
     *
     * Le coordinate X sono calcolate a partire dal game area array (la cornice
     * delle mappe) usando le posizioni: 43, 237, 427, 616.
     *
     * @param index indice del personaggio selezionato (0-3)
     */
    private void drawSelectionArrow(Graphics2D g2d, int index) {
        // Centro orizzontale del personaggio in coordinate schermo assolute.
        // CHAR_SELECTOR_X[i] è relativo a FRAME_OFFSET_X.
        int cx = ViewConfig.FRAME_OFFSET_X + ViewConfig.CHAR_SELECTOR_X[index];

        // Y base: appena sopra i riquadri personaggio
        int baseY = ViewConfig.MENU_DRAW_Y + ViewConfig.CHAR_FRAME_Y - 10;

        // --- ANIMAZIONE BOBBING (oscillazione verticale) ---
        double bobOffset = 6.0 * Math.sin(System.currentTimeMillis() / 250.0);
        int arrowY = baseY + (int) bobOffset;

        // --- DIMENSIONI FRECCIA ---
        int arrowW = 24;  // larghezza base del triangolo
        int arrowH = 18;  // altezza del triangolo
        int stemW  = 8;   // larghezza dello stelo
        int stemH  = 12;  // altezza dello stelo

        Composite originalComposite = g2d.getComposite();

        // --- OMBRA (offset di 2px, scura) ---
        int shadowOff = 2;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        g2d.setColor(Color.BLACK);
        g2d.fillRect(cx - stemW / 2 + shadowOff, arrowY - stemH + shadowOff, stemW, stemH);
        int[] sxShadow = { cx - arrowW / 2 + shadowOff, cx + arrowW / 2 + shadowOff, cx + shadowOff };
        int[] syShadow = { arrowY + shadowOff, arrowY + shadowOff, arrowY + arrowH + shadowOff };
        g2d.fillPolygon(sxShadow, syShadow, 3);
        g2d.setComposite(originalComposite);

        // --- CORPO FRECCIA (colore principale: giallo/oro arcade pulsante) ---
        float pulse = 0.85f + 0.15f * (float) Math.abs(Math.sin(System.currentTimeMillis() / 300.0));
        Color arrowColor = new Color(
                Math.min(255, (int)(255 * pulse)),
                Math.min(255, (int)(200 * pulse)),
                0
        );
        g2d.setColor(arrowColor);

        // Stelo
        g2d.fillRect(cx - stemW / 2, arrowY - stemH, stemW, stemH);

        // Punta (triangolo verso il basso)
        int[] triX = { cx - arrowW / 2, cx + arrowW / 2, cx };
        int[] triY = { arrowY, arrowY, arrowY + arrowH };
        g2d.fillPolygon(triX, triY, 3);

        // --- BORDO PIXEL-ART (contorno scuro) ---
        g2d.setColor(new Color(120, 80, 0));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRect(cx - stemW / 2, arrowY - stemH, stemW, stemH);
        g2d.drawPolygon(triX, triY, 3);
        g2d.setStroke(new BasicStroke(1));

        // --- HIGHLIGHT INTERNO (profondità pixel-art) ---
        g2d.setColor(new Color(255, 255, 180, 160));
        g2d.fillRect(cx - stemW / 2 + 2, arrowY - stemH + 2, stemW - 4, 3);

        // --- PARTICELLE SCINTILLANTI ai lati ---
        float sparkAlpha = 0.3f + 0.7f * (float) Math.abs(Math.sin(System.currentTimeMillis() / 200.0));
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sparkAlpha));
        g2d.setColor(new Color(255, 255, 100));
        int sparkSize = 3;
        g2d.fillRect(cx - arrowW / 2 - 4, arrowY + arrowH / 2, sparkSize, sparkSize);
        g2d.fillRect(cx + arrowW / 2 + 2, arrowY + arrowH / 2, sparkSize, sparkSize);
        g2d.setComposite(originalComposite);
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
    private void drawInstructions(Graphics2D g2d, int selectedIndex) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 13));
        g2d.setColor(new Color(210, 210, 210));

        String instructions;
        if (selectedIndex >= 0) {
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
    // HIT-TESTING (usato dal Controller per determinare il target del click)
    // =========================================================================

    /**
     * Determina quale personaggio si trova alle coordinate schermo date.
     *
     * <p>Questo metodo è esposto alla View/Controller per tradurre le coordinate
     * pixel del mouse in un indice logico. Il Controller usa il risultato per
     * aggiornare il Model.</p>
     *
     * @param mouseX coordinata X del click (schermo assoluto)
     * @param mouseY coordinata Y del click (schermo assoluto)
     * @return indice del personaggio (0-3), oppure -1 se fuori da tutti i riquadri.
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

    /** Alias di compatibilità per getCharacterIndexAt. */
    public int getFrameIndexAt(int mouseX, int mouseY) {
        return getCharacterIndexAt(mouseX, mouseY);
    }

    /**
     * Verifica se le coordinate cadono sul pulsante "Start Game" / "NEW GAME".
     *
     * @param mouseX coordinata X del click (schermo assoluto)
     * @param mouseY coordinata Y del click (schermo assoluto)
     * @return true se il click è sul pulsante.
     */
    public boolean isStartGameButtonAt(int mouseX, int mouseY) {
        int bx = ViewConfig.MENU_DRAW_X + ViewConfig.NEW_GAME_BTN_X;
        int by = ViewConfig.MENU_DRAW_Y + ViewConfig.NEW_GAME_BTN_Y;
        return mouseX >= bx && mouseX <= bx + ViewConfig.NEW_GAME_BTN_W
                && mouseY >= by && mouseY <= by + ViewConfig.NEW_GAME_BTN_H;
    }

    /** Alias di compatibilità per isStartGameButtonAt. */
    public boolean isNewGameButtonAt(int mouseX, int mouseY) {
        return isStartGameButtonAt(mouseX, mouseY);
    }
}
