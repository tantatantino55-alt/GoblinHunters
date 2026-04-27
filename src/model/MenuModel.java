package model;

import utils.CharacterType;

/**
 * Model del menu di selezione personaggio (MVC).
 *
 * Stato unico:
 * - {@code selectedIndex}: indice del personaggio selezionato con click (-1 = nessuno).
 *   La freccia selettore nella View punta al personaggio con questo indice.
 *
 * La conferma ({@link #confirmSelection()}) usa {@code selectedIndex}
 * per determinare con quale personaggio iniziare il gioco.
 *
 * NON conosce la View né il Controller.
 */
public class MenuModel {

    private static MenuModel instance = null;

    // --- STATO SELEZIONE (persistente, impostato dal click) ---
    private int selectedIndex  = -1;

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

    /**
     * Indice del personaggio selezionato con click (-1 se nessuno).
     * Usato dalla View per posizionare la freccia selettore.
     */
    public int getSelectedIndex()  { return selectedIndex; }

    /** Alias di compatibilità — restituisce lo stesso valore di getSelectedIndex(). */
    public int getClickedIndex()   { return selectedIndex; }

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

    /**
     * Seleziona un personaggio tramite click.
     * Aggiorna {@code selectedIndex}: la View sposterà la freccia.
     *
     * @param index indice del personaggio (0-3), oppure -1 per deselezionare.
     */
    public void selectCharacter(int index) {
        if (index >= 0 && index < CharacterType.values().length) {
            this.selectedIndex = index;
        }
    }

    /** Alias di compatibilità — delega a {@link #selectCharacter(int)}. */
    public void selectByClick(int frameIndex) {
        selectCharacter(frameIndex);
    }

    /**
     * Conferma la selezione e prepara la transizione.
     * Usa {@code selectedIndex} per determinare il personaggio confermato.
     */
    public void confirmSelection() {
        if (selectedIndex >= 0) {
            this.confirmedIndex = selectedIndex;
        }
    }

    /** Resetta lo stato del menu (per un eventuale ritorno al menu). */
    public void reset() {
        selectedIndex  = -1;
        confirmedIndex = -1;
    }
}
