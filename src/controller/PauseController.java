package controller;

import model.PauseModel;

/**
 * Controller dedicato al menu di pausa.
 *
 * <h3>Responsabilità</h3>
 * <ul>
 *   <li>Gestione dello stato di rebind (singolo indice 0-6)</li>
 *   <li>Al commit di un rebind: aggiorna {@link PauseModel} E propaga la modifica
 *       all'InputMap di Swing tramite {@code IControllerForView.applyKeyBinding()}</li>
 *   <li>Toggle audio, reset defaults</li>
 *   <li>Nessuna dipendenza da {@code ControllerForModel} o da classi View</li>
 * </ul>
 */
public class PauseController {

    // =========================================================================
    // Singleton
    // =========================================================================
    private static PauseController instance;
    public static PauseController getInstance() {
        if (instance == null) instance = new PauseController();
        return instance;
    }
    private PauseController() {}

    // =========================================================================
    // Stato rebind — indice azione (0-6), -1 = nessuna
    // =========================================================================
    private int rebindingRow = -1;

    // =========================================================================
    // API — Pulsanti di navigazione
    // =========================================================================

    /**
     * Pulizia dello stato interno quando il gioco viene ripreso.
     * Il {@code setPaused(false)} è eseguito dall'orchestratore ({@code GamePanel}).
     */
    public void onResumeClicked() {
        cancelRebind();
    }

    /**
     * Pulizia dello stato interno prima dell'uscita.
     * Il {@code System.exit(0)} è eseguito dall'orchestratore ({@code GamePanel}).
     */
    public void onQuitClicked() {
        cancelRebind();
    }

    /**
     * Torna al menu principale e resetta completamente lo stato di gioco.
     */
    public void onReturnToMainMenuClicked() {
        System.out.println("[PauseController] Ritorno al Main Menu richiesto...");
        cancelRebind(); // Pulisce eventuali rebind in corso
        ControllerForView.getInstance().resetGame();
    }

    // =========================================================================
    // API — Reset defaults
    // =========================================================================

    /** Ripristina tutti i keybindings ai valori di fabbrica. */
    public void onResetDefaultsClicked() {
        cancelRebind();
        PauseModel.getInstance().resetDefaults();
        // Propaga tutti i reset all'InputMap di Swing
        for (int i = 0; i < PauseModel.ACTION_COUNT; i++) {
            ControllerForView.getInstance().applyKeyBinding(i, PauseModel.ACTION_DEFAULTS[i]);
        }
    }

    // =========================================================================
    // API — Rebind unificato (un solo indice 0-6)
    // =========================================================================

    /**
     * Avvia (o toggling off) la modalità rebind per l'azione {@code row}.
     *
     * @param row indice azione 0-6 (vedi {@link PauseModel} costanti ACTION_*)
     */
    public void startRebind(int row) {
        if (rebindingRow == row) {
            rebindingRow = -1; // toggle off: cliccare la stessa riga due volte annulla
        } else {
            rebindingRow = row;
        }
    }

    /**
     * Conferma il nuovo tasto per l'azione correntemente in rebind.
     * <ol>
     *   <li>Normalizza {@code rawKeyName} in uppercase (formato KeyStroke)</li>
     *   <li>Aggiorna {@link PauseModel}</li>
     *   <li>Propaga la modifica all'InputMap di Swing via {@code IControllerForView}</li>
     * </ol>
     *
     * @param rawKeyName nome del tasto come restituito da {@code KeyEvent.getKeyText()}
     */
    public void commitRebind(String rawKeyName) {
        if (rebindingRow >= 0) {
            String normalized = rawKeyName.toUpperCase();
            PauseModel.getInstance().setActionBinding(rebindingRow, normalized);
            // Propaga il rebind al sistema reale di Input (InputMap in GamePanel)
            ControllerForView.getInstance().applyKeyBinding(rebindingRow, normalized);
            rebindingRow = -1;
        }
    }

    /** {@code true} se c'è un'azione in attesa di un nuovo tasto. */
    public boolean isRebinding() {
        return rebindingRow >= 0;
    }

    /** Riga in rebind (-1 se nessuna). */
    public int getRebindingRow() {
        return rebindingRow;
    }

    /** Annulla il rebind in corso senza salvare. */
    public void cancelRebind() {
        rebindingRow = -1;
    }

    /** Alias per compatibilità con chiamate da GamePanel (chiusura menu tramite ESC). */
    public void cancelAllRebinds() {
        cancelRebind();
    }

    // =========================================================================
    // API — Audio toggle
    // =========================================================================

    /** Toggle audio. La View chiama questo metodo quando l'utente clicca l'icona. */
    public void setAudioEnabled(boolean enabled) {
        PauseModel.getInstance().setAudioEnabled(enabled);
    }

    // =========================================================================
    // API — Getter per la View (delega al Model)
    // =========================================================================

    /** Keybind corrente per l'azione {@code i} (formato uppercase, visualizzazione nella UI). */
    public String getActionLabel(int i) {
        return PauseModel.getInstance().getActionBinding(i);
    }

    /** {@code true} se l'audio è attivo. */
    public boolean isAudioEnabled() {
        return PauseModel.getInstance().isAudioEnabled();
    }
}
