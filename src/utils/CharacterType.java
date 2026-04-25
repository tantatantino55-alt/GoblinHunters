package utils;

/**
 * Enumerazione dei personaggi selezionabili.
 * L'ordine corrisponde alla disposizione visiva nell'immagine StartGame.png
 * (da sinistra a destra): Male, Goblin, Veteran, Female.
 *
 * Ogni valore mappa un ID (0-3) al percorso dello spritesheet.
 * La struttura dei frame è IDENTICA per tutti gli sheet,
 * quindi basta parametrizzare il path per caricare le animazioni.
 */
public enum CharacterType {

    MALE("/wizardmale.png",       "Male Wizard"),
    GOBLIN("/GoblinWizard.png",   "Goblin Wizard"),
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

    /**
     * Converte un indice numerico (0-3) nel CharacterType corrispondente.
     * Valori fuori range vengono clampati.
     */
    public static CharacterType fromIndex(int index) {
        CharacterType[] v = values();
        return v[Math.max(0, Math.min(index, v.length - 1))];
    }
}
