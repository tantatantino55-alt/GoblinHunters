package goblinhunters.controller;

import goblinhunters.model.PauseModel;
import goblinhunters.view.AudioManager;

public class PauseController {

    public static final int      ACTION_MOVE_UP    = PauseModel.ACTION_MOVE_UP;
    public static final int      ACTION_MOVE_DOWN  = PauseModel.ACTION_MOVE_DOWN;
    public static final int      ACTION_MOVE_LEFT  = PauseModel.ACTION_MOVE_LEFT;
    public static final int      ACTION_MOVE_RIGHT = PauseModel.ACTION_MOVE_RIGHT;
    public static final int      ACTION_BOMB       = PauseModel.ACTION_BOMB;
    public static final int      ACTION_AURA       = PauseModel.ACTION_AURA;
    public static final int      ACTION_STAFF      = PauseModel.ACTION_STAFF;
    public static final int      ACTION_COUNT      = PauseModel.ACTION_COUNT;
    public static final String[] ACTION_LABELS     = PauseModel.ACTION_LABELS;
    public static final String[] ACTION_DEFAULTS   = PauseModel.ACTION_DEFAULTS;

    private static PauseController instance;

    public static PauseController getInstance() {
        if (instance == null) instance = new PauseController();
        return instance;
    }

    private PauseController() {}

    private int rebindingRow = -1;

    /**
     * Cleans up internal state when the game is resumed.
     * The actual {@code setPaused(false)} is performed by the orchestrator (GamePanel).
     */
    public void onResumeClicked() {
        cancelRebind();
    }

    /**
     * Cleans up internal state before exit.
     * The actual {@code System.exit(0)} is performed by the orchestrator (GamePanel).
     */
    public void onQuitClicked() {
        cancelRebind();
    }

    /** Returns to the main menu and fully resets game state. */
    public void onReturnToMainMenuClicked() {
        cancelRebind();
        ControllerForView.getInstance().resetGame();
    }

    /** Restores all keybindings to factory defaults. */
    public void onResetDefaultsClicked() {
        cancelRebind();
        PauseModel.getInstance().resetDefaults();
        for (int i = 0; i < PauseModel.ACTION_COUNT; i++) {
            ControllerForView.getInstance().applyKeyBinding(i, PauseModel.ACTION_DEFAULTS[i]);
        }
    }

    /**
     * Starts (or toggles off) rebind mode for action {@code row}.
     *
     * @param row action index 0-6 (see {@link PauseModel} ACTION_* constants)
     */
    public void startRebind(int row) {
        if (rebindingRow == row) {
            rebindingRow = -1; // clicking the same row twice cancels
        } else {
            rebindingRow = row;
        }
    }

    /**
     * Confirms the new key for the currently pending rebind:
     * normalises to uppercase, updates {@link PauseModel}, and propagates
     * the change to the Swing InputMap via {@code IControllerForView}.
     *
     * @param rawKeyName key name as returned by {@code KeyEvent.getKeyText()}
     */
    public void commitRebind(String rawKeyName) {
        if (rebindingRow >= 0) {
            String normalized = rawKeyName.toUpperCase();
            PauseModel.getInstance().setActionBinding(rebindingRow, normalized);
            ControllerForView.getInstance().applyKeyBinding(rebindingRow, normalized);
            rebindingRow = -1;
        }
    }

    /** Returns true if an action is waiting for a new key. */
    public boolean isRebinding() {
        return rebindingRow >= 0;
    }

    /** The row currently being rebound, or -1 if none. */
    public int getRebindingRow() {
        return rebindingRow;
    }

    /** Cancels the current rebind without saving. */
    public void cancelRebind() {
        rebindingRow = -1;
    }

    /** Alias for {@link #cancelRebind()} — called by GamePanel on ESC-close. */
    public void cancelAllRebinds() {
        cancelRebind();
    }

    /** Enables or disables audio. Called by the View when the user clicks the icon. */
    public void setAudioEnabled(boolean enabled) {
        PauseModel.getInstance().setAudioEnabled(enabled);
        AudioManager.getInstance().setMuted(!enabled);
    }

    /** Current keybind for action {@code i} (uppercase format for UI display). */
    public String getActionLabel(int i) {
        return PauseModel.getInstance().getActionBinding(i);
    }

    /** Returns true if audio is enabled. */
    public boolean isAudioEnabled() {
        return PauseModel.getInstance().isAudioEnabled();
    }
}
