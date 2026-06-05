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

    // --- Palette (identica a PauseMenuDrawer) ---
    private static final Color BG_DARK      = new Color(28, 18, 10, 220);
    private static final Color BORDER_GOLD  = new Color(200, 165, 70);
    private static final Color BORDER_INNER = new Color(120, 90, 30);
    private static final Color BORDER_RED   = new Color(200, 50, 50);
    private static final Color LABEL_YELLOW = new Color(255, 210, 60);
    private static final Color TEXT_CREAM   = new Color(240, 235, 220);
    private static final Color ERROR_RED    = new Color(220, 60, 60);

    // --- Campo nome ---
    private static final int NAME_FIELD_W = 360;
    private static final int NAME_FIELD_H = 38;

    private Rectangle nameFieldRect  = null;
    private boolean   showNameError  = false;

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

        // 4. CAMPO NOME GIOCATORE
        drawNameInput(g2d);
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


    // =========================================================================
    // CAMPO NOME GIOCATORE
    // =========================================================================

    private void drawNameInput(Graphics2D g2d) {
        MenuModel model = MenuModel.getInstance();

        int fieldX = ViewConfig.MENU_DRAW_X + (ViewConfig.MENU_DRAW_W - NAME_FIELD_W) / 2;
        int fieldY = ViewConfig.MENU_DRAW_Y + ViewConfig.CHAR_FRAME_Y + ViewConfig.CHAR_FRAME_H + 80;

        nameFieldRect = new Rectangle(fieldX, fieldY, NAME_FIELD_W, NAME_FIELD_H);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- Label "NOME:" ---
        g2d.setFont(fontNameLabel);
        g2d.setColor(LABEL_YELLOW);
        g2d.drawString("INSERT YOUR NICKNAME:", fieldX, fieldY - 6);

        // --- Sfondo campo ---
        g2d.setColor(BG_DARK);
        g2d.fillRoundRect(fieldX, fieldY, NAME_FIELD_W, NAME_FIELD_H, 8, 8);

        // --- Bordo (gold se attivo, rosso se errore, inner altrimenti) ---
        Color borderColor;
        float strokeW;
        if (showNameError) {
            borderColor = BORDER_RED;
            strokeW = 2f;
        } else if (model.isTypingName()) {
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

        // --- Testo digitato + cursore lampeggiante ---
        g2d.setFont(fontNameText);
        g2d.setColor(TEXT_CREAM);
        FontMetrics fm = g2d.getFontMetrics();
        int textY = fieldY + (NAME_FIELD_H - fm.getHeight()) / 2 + fm.getAscent();
        String name = model.getPlayerName();
        String display = name;
        if (model.isTypingName() && System.currentTimeMillis() % 800 < 400) {
            display = name + "|";
        }
        g2d.drawString(display, fieldX + 10, textY);

        // --- Messaggio errore ---
        if (showNameError) {
            g2d.setFont(fontNameError);
            g2d.setColor(ERROR_RED);
            g2d.drawString("⚠  Required field", fieldX, fieldY + NAME_FIELD_H + 14);
        }
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
