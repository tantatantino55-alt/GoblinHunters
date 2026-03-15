package view;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestisce le immagini per le celle statiche della mappa (Muri, Pavimenti, ecc.).
 * Ora supporta i "Temi" (es. VILLAGE, FOREST).
 */
public class TileManager {

    private static TileManager instance = null;

    // Mappa: Chiave = Nome Tema ("VILLAGE"), Valore = Array di immagini
    private final Map<String, BufferedImage[]> themeMap;

    // Tema attualmente in uso per il livello (default a VILLAGE)
    private String currentTheme = "VILLAGE";

    private TileManager() {
        this.themeMap = new HashMap<>();
        // NIENTE CARICAMENTO QUI! Ora ci pensa il ResourceLoader.
    }

    public static TileManager getInstance() {
        if (instance == null) {
            instance = new TileManager();
        }
        return instance;
    }

    /**
     * Registra un nuovo tema nel TileManager.
     */
    public void loadTheme(String themeName, BufferedImage[] tiles) {
        themeMap.put(themeName, tiles);
    }

    /**
     * Cambia il tema grafico in tempo reale.
     */
    public void setCurrentTheme(String themeName) {
        if (themeMap.containsKey(themeName)) {
            this.currentTheme = themeName;
        } else {
            System.err.println("TileManager: Tema '" + themeName + "' non trovato!");
        }
    }

    /**
     * Restituisce l'immagine corretta in base al tipo di cella e al tema corrente.
     * Ricorda: 0 = Vuoto, 1 = Indistruttibile, 2 = Distruttibile
     */
    public BufferedImage getTileImage(int cellType) {
        BufferedImage[] tiles = themeMap.get(currentTheme);

        // Se il tema esiste e il cellType rientra nei limiti dell'array (0, 1, 2)
        if (tiles != null && cellType >= 0 && cellType < tiles.length) {
            return tiles[cellType];
        }
        return null;
    }

    public boolean hasTileImage(int cellType) {
        BufferedImage[] tiles = themeMap.get(currentTheme);
        return tiles != null && cellType >= 0 && cellType < tiles.length && tiles[cellType] != null;
    }
}