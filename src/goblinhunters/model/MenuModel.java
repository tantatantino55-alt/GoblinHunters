package goblinhunters.model;

import goblinhunters.utils.CharacterType;

/**
 * Character selection menu model (MVC).
 *
 * <p>Single source of truth for selection state:
 * - {@code selectedIndex}: index of the character selected by click (-1 = none).
 *   The selector arrow in the View points to the character at this index.</p>
 *
 * <p>{@link #confirmSelection()} uses {@code selectedIndex} to determine
 * which character starts the game.</p>
 */
public class MenuModel {

    private static MenuModel instance = null;

    private int selectedIndex  = -1; // persistent selection, set by click
    private int confirmedIndex = -1;

    private String  playerName = "";
    private boolean typingName = false;

    private MenuModel() {}

    public static MenuModel getInstance() {
        if (instance == null) instance = new MenuModel();
        return instance;
    }

    /**
     * Index of the character selected by click (-1 if none).
     * Used by the View to position the selector arrow.
     */
    public int getSelectedIndex()  { return selectedIndex; }

    /** Index of the confirmed character (-1 if not yet confirmed). */
    public int getConfirmedIndex() { return confirmedIndex; }

    public boolean isCharacterConfirmed() { return confirmedIndex >= 0; }

    /** Returns the confirmed CharacterType, or null if none has been confirmed. */
    public CharacterType getConfirmedCharacterType() {
        return confirmedIndex >= 0 ? CharacterType.fromIndex(confirmedIndex) : null;
    }

    /**
     * Selects a character by click.
     * Updates {@code selectedIndex} — the View will move the selector arrow.
     *
     * @param index character index (0-3), or -1 to deselect.
     */
    public void selectCharacter(int index) {
        if (index >= 0 && index < CharacterType.values().length) {
            this.selectedIndex = index;
        }
    }

    /**
     * Confirms the selection and prepares the transition.
     * Uses {@code selectedIndex} to determine the confirmed character.
     */
    public void confirmSelection() {
        if (selectedIndex >= 0) {
            this.confirmedIndex = selectedIndex;
        }
    }

    /** Resets the menu state (for returning to the menu). */
    public void reset() {
        selectedIndex  = -1;
        confirmedIndex = -1;
        playerName     = "";
        typingName     = false;
    }

    public String  getPlayerName()          { return playerName; }
    public boolean isTypingName()           { return typingName; }
    public void    setTypingName(boolean v) { typingName = v; }
    public boolean isNameValid()            { return !playerName.trim().isEmpty(); }

    public void appendNameChar(char c) {
        if (playerName.length() < 12) playerName += c;
    }

    public void deleteNameChar() {
        if (!playerName.isEmpty()) playerName = playerName.substring(0, playerName.length() - 1);
    }
}
