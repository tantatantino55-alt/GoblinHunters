package goblinhunters.utils;

/**
 * Selectable player characters.
 * Order matches left-to-right layout in StartGame.png.
 * All sheets share the same frame structure, so swapping the path is enough.
 */
public enum CharacterType {

    MALE("/wizardmale.png",       "Male Wizard"),
    GOBLIN("/GoblinWizard.png",   "Skeleton Wizard"),
    VETERAN("/VeteranWizard.png",  "Veteran Wizard"),
    FEMALE("/FemaleWizard.png",   "Female Wizard");

    private final String sheetPath;
    private final String displayName;

    CharacterType(String sheetPath, String displayName) {
        this.sheetPath   = sheetPath;
        this.displayName = displayName;
    }

    public String getSheetPath()   { return sheetPath; }
    public String getDisplayName() { return displayName; }

    /** Converts a 0-3 index to the corresponding CharacterType; clamps out-of-range values. */
    public static CharacterType fromIndex(int index) {
        CharacterType[] v = values();
        return v[Math.max(0, Math.min(index, v.length - 1))];
    }
}
