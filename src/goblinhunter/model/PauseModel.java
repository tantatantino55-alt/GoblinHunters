package goblinhunter.model;

/**
 * Pause menu model.
 *
 * <p>Single source of truth for all game keybindings and audio state.
 * Contains no logic — only data and constants.</p>
 *
 * <h3>7 actions (indices 0-6)</h3>
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

    // singleton
    private static PauseModel instance;
    public static PauseModel getInstance() {
        if (instance == null) instance = new PauseModel();
        return instance;
    }

    // action indices — public constants so callers avoid magic numbers
    public static final int ACTION_MOVE_UP    = 0;
    public static final int ACTION_MOVE_DOWN  = 1;
    public static final int ACTION_MOVE_LEFT  = 2;
    public static final int ACTION_MOVE_RIGHT = 3;
    public static final int ACTION_BOMB       = 4;
    public static final int ACTION_AURA       = 5;
    public static final int ACTION_STAFF      = 6;
    public static final int ACTION_COUNT      = 7;

    /**
     * UI display labels for each action.
     * Index matches ACTION_MOVE_UP … ACTION_STAFF.
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
     * Default keybindings (KeyStroke format: uppercase, e.g. "UP", "SPACE", "X").
     * Index matches ACTION_MOVE_UP … ACTION_STAFF.
     * <p>Also used by {@code GamePanel} as the authoritative source for the initial InputMap.</p>
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

    // current state
    private String[] actionBindings;
    private boolean  audioEnabled;

    private PauseModel() {
        actionBindings = ACTION_DEFAULTS.clone();
        audioEnabled   = true;
    }

    // keybindings API

    /** Current keybind for the action at index {@code i} (uppercase KeyStroke format). */
    public String getActionBinding(int i) {
        return actionBindings[i];
    }

    /** Updates the keybind for an action (uppercase KeyStroke format). */
    public void setActionBinding(int i, String key) {
        if (i >= 0 && i < actionBindings.length) actionBindings[i] = key;
    }

    // audio API

    public boolean isAudioEnabled()           { return audioEnabled; }
    public void    setAudioEnabled(boolean v) { audioEnabled = v; }

    // reset

    /** Restores all keybindings to factory defaults. Audio state is not reset. */
    public void resetDefaults() {
        actionBindings = ACTION_DEFAULTS.clone();
    }
}
