package model;

import utils.CharacterType;

/**
 * Model del menu di selezione personaggio (MVC).
 *
 * Gestisce DUE stati indipendenti:
 * - {@code hoveredIndex}: riquadro sotto il cursore (effetto glow temporaneo)
 * - {@code clickedIndex}: personaggio selezionato con click (bordo persistente)
 *
 * La conferma ({@link #confirmSelection()}) usa {@code clickedIndex},
 * così spostare il mouse verso il pulsante "Start Game" non perde la selezione.
 *
 * NON conosce la View né il Controller.
 */
public class MenuModel {

    private static MenuModel instance = null;

    // --- STATO HOVER (segue il cursore, temporaneo) ---
    private int hoveredIndex = -1;

    // --- STATO SELEZIONE (persistente, impostato dal click) ---
    private int clickedIndex  = -1;

    // --- CONFERMA ---
    private int confirmedIndex = -1;

    private MenuModel() {}

    public static MenuModel getInstance() {
        if (instance == null) instance = new MenuModel();
        return instance;
    }

    // =========================================================================
    // QUERY
    // =========================================================================

    /** Indice del riquadro sotto il cursore (-1 se nessuno). */
    public int getHoveredIndex()   { return hoveredIndex; }

    /** Indice del personaggio selezionato con click (-1 se nessuno). */
    public int getClickedIndex()   { return clickedIndex; }

    /** Indice del personaggio confermato (-1 se non confermato). */
    public int getConfirmedIndex() { return confirmedIndex; }

    public boolean isCharacterConfirmed() { return confirmedIndex >= 0; }

    /**
     * Ritorna il CharacterType confermato, o null se nessuno è stato confermato.
     */
    public CharacterType getConfirmedCharacterType() {
        return confirmedIndex >= 0 ? CharacterType.fromIndex(confirmedIndex) : null;
    }

    // =========================================================================
    // COMANDI (chiamati dal Controller)
    // =========================================================================

    /** Aggiorna l'hover in base alla posizione del mouse. */
    public void setHoveredIndex(int index) {
        this.hoveredIndex = index;
    }

    /**
     * Seleziona un personaggio tramite click sul riquadro.
     * Il clickedIndex persiste anche quando il mouse si sposta altrove.
     */
    public void selectByClick(int frameIndex) {
        if (frameIndex >= 0 && frameIndex < CharacterType.values().length) {
            this.clickedIndex = frameIndex;
        }
    }

    /**
     * Conferma la selezione e prepara la transizione.
     * Usa {@code clickedIndex} (NON hoveredIndex) per evitare
     * che lo spostamento del mouse verso il pulsante annulli la scelta.
     */
    public void confirmSelection() {
        if (clickedIndex >= 0) {
            this.confirmedIndex = clickedIndex;
        }
    }

    /** Resetta lo stato del menu (per un eventuale ritorno al menu). */
    public void reset() {
        hoveredIndex   = -1;
        clickedIndex   = -1;
        confirmedIndex = -1;
    }
}
