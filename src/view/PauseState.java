package view;

/**
 * Mutable state bag shared between the pause-menu drawer and the key/mouse handlers.
 *
 * Singleton — obtain via {@link #getInstance()}.
 *
 * Holds:
 *  - current keybind labels for the 4 actions
 *  - muted flag for audio
 */
public class PauseState {

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------
    private static PauseState instance;
    public static PauseState getInstance() {
        if (instance == null) instance = new PauseState();
        return instance;
    }

    // Default key labels (must match the actual GamePanel bindings)
    public static final String[] DEFAULTS = {
        "W A S D",
        "SPACE",
        "X",
        "Z"
    };

    /** Displayed keybind labels — user can change these via click-to-rebind. */
    public String[] keyLabels;

    /** Whether sound is muted. */
    public boolean muted = false;

    private PauseState() {
        keyLabels = DEFAULTS.clone();
    }

    /** Restore factory defaults without creating a new object. */
    public void resetDefaults() {
        keyLabels = DEFAULTS.clone();
    }
}
