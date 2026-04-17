package model;

/**
 * Model del menu di pausa.
 *
 * <p>Fonte di verità unica per tutti i keybindings di gioco e per lo stato audio.
 * Non contiene logica: solo dati e constanti.</p>
 *
 * <h3>7 azioni (indici 0-6)</h3>
 * <pre>
 *   0 = Move Up       (default: UP)
 *   1 = Move Down     (default: DOWN)
 *   2 = Move Left     (default: LEFT)
 *   3 = Move Right    (default: RIGHT)
 *   4 = Fire Sphere   (default: SPACE)
 *   5 = Aura Spell    (default: X)
 *   6 = Staff Attack  (default: Z)
 * </pre>
 */
public class PauseModel {

    // =========================================================================
    // Singleton
    // =========================================================================
    private static PauseModel instance;
    public static PauseModel getInstance() {
        if (instance == null) instance = new PauseModel();
        return instance;
    }

    // =========================================================================
    // Indici azioni — costanti pubbliche per uso esterno senza magic numbers
    // =========================================================================
    public static final int ACTION_MOVE_UP    = 0;
    public static final int ACTION_MOVE_DOWN  = 1;
    public static final int ACTION_MOVE_LEFT  = 2;
    public static final int ACTION_MOVE_RIGHT = 3;
    public static final int ACTION_BOMB       = 4;
    public static final int ACTION_AURA       = 5;
    public static final int ACTION_STAFF      = 6;
    public static final int ACTION_COUNT      = 7;

    /**
     * Etichette da mostrare nella UI per ciascuna azione.
     * Indice coerente con ACTION_MOVE_UP … ACTION_STAFF.
     */
    public static final String[] ACTION_LABELS = {
            "Move Up",
            "Move Down",
            "Move Left",
            "Move Right",
            "Fire Sphere",
            "Aura Spell",
            "Staff Attack"
    };

    /**
     * Keybindings di default (formato KeyStroke: uppercase, es. "UP", "SPACE", "X").
     * Indice coerente con ACTION_MOVE_UP … ACTION_STAFF.
     * <p>Usato anche da {@code GamePanel} come fonte di verità per l'InputMap iniziale.</p>
     */
    public static final String[] ACTION_DEFAULTS = {
            "UP",
            "DOWN",
            "LEFT",
            "RIGHT",
            "SPACE",
            "X",
            "Z"
    };

    // =========================================================================
    // Stato corrente
    // =========================================================================
    private String[] actionBindings;
    private boolean  audioEnabled;

    private PauseModel() {
        actionBindings = ACTION_DEFAULTS.clone();
        audioEnabled   = true;
    }

    // =========================================================================
    // API — Keybindings
    // =========================================================================

    /** Keybind corrente per l'azione all'indice {@code i} (formato uppercase KeyStroke). */
    public String getActionBinding(int i) {
        return actionBindings[i];
    }

    /** Aggiorna il keybind di un'azione (formato uppercase KeyStroke). */
    public void setActionBinding(int i, String key) {
        if (i >= 0 && i < actionBindings.length) actionBindings[i] = key;
    }

    // =========================================================================
    // API — Audio
    // =========================================================================

    public boolean isAudioEnabled()            { return audioEnabled; }
    public void    setAudioEnabled(boolean v)  { audioEnabled = v; }

    // =========================================================================
    // Reset
    // =========================================================================

    /** Ripristina tutti i keybindings ai valori di fabbrica. L'audio non viene resettato. */
    public void resetDefaults() {
        actionBindings = ACTION_DEFAULTS.clone();
    }
}
