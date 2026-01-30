package utils;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * TileManager gestisce il caricamento e l'accesso alle immagini delle tiles.
 *
 * Usa ResourceManager per garantire compatibilità JAR.
 * Pattern Singleton per caricare le risorse una sola volta.
 *
 * POSIZIONE: goblinhunter/utils/TileManager.java
 */
public class TileManager {

    // ---------------------------------------------------------------
    // SINGLETON
    // ---------------------------------------------------------------
    private static TileManager instance = null;

    // ---------------------------------------------------------------
    // ATTRIBUTES
    // ---------------------------------------------------------------
    // Mappa: Tipo cella (int) → Immagine tile (BufferedImage)
    private Map<Integer, BufferedImage> tileImages;

    // ---------------------------------------------------------------
    // CONSTRUCTOR (privato per Singleton)
    // ---------------------------------------------------------------
    private TileManager() {
        this.tileImages = new HashMap<>();
        loadTileImages();
    }

    /**
     * Carica tutte le immagini delle tiles all'avvio.
     * Se un'immagine non viene trovata, crea un placeholder colorato.
     */
    private void loadTileImages() {
        System.out.println("TileManager: Inizio caricamento tiles...");

        // Carica tile per ogni tipo di cella
        loadTileImage(Config.CELL_EMPTY, Config.TILE_FLOOR);
        loadTileImage(Config.CELL_INDESTRUCTIBLE_BLOCK, Config.TILE_WALL_INDESTRUCTIBLE);
        loadTileImage(Config.CELL_DESTRUCTIBLE_BLOCK, Config.TILE_WALL_DESTRUCTIBLE);

        System.out.println("TileManager: ✅ Caricamento completato (" + tileImages.size() + " tiles)");
    }

    /**
     * Carica una singola immagine tile usando ResourceManager.
     * Se il caricamento fallisce, crea un placeholder colorato.
     *
     * @param cellType Tipo di cella (Config.CELL_*)
     * @param resourcePath Percorso della risorsa (Config.TILE_*)
     */
    private void loadTileImage(int cellType, String resourcePath) {
        // Usa ResourceManager (compatibile JAR!)
        BufferedImage img = ResourceManager.loadImage(resourcePath);

        if (img != null) {
            tileImages.put(cellType, img);
            System.out.println("  → Tile tipo " + cellType + " caricata");
        } else {
            // Fallback: crea placeholder se l'immagine manca
            System.err.println("  → Tile tipo " + cellType + " NON trovata, uso placeholder");
            //tileImages.put(cellType, createPlaceholderImage(cellType));
        }
    }



    /**
     * Ottiene l'immagine di una tile dato il tipo di cella.
     *
     * @param cellType Tipo di cella (Config.CELL_*)
     * @return BufferedImage della tile, o null se non trovata
     *
     * ESEMPIO:
     *   BufferedImage tile = TileManager.getInstance().getTileImage(Config.CELL_EMPTY);
     */
    public BufferedImage getTileImage(int cellType) {
        return tileImages.get(cellType);
    }

    /**
     * Verifica se un'immagine tile è stata caricata.
     *
     * @param cellType Tipo di cella
     * @return true se l'immagine esiste, false altrimenti
     */
    public boolean hasTileImage(int cellType) {
        return tileImages.containsKey(cellType);
    }

    /**
     * Restituisce il numero di tiles caricate.
     *
     * @return Numero di tiles nella mappa
     */
    public int getTileCount() {
        return tileImages.size();
    }

    // ---------------------------------------------------------------
    // SINGLETON GETTER
    // ---------------------------------------------------------------
    /**
     * Ottiene l'istanza singleton di TileManager.
     * Le tiles vengono caricate al primo accesso.
     *
     * @return Istanza unica di TileManager
     */
    public static TileManager getInstance() {
        if (instance == null) {
            instance = new TileManager();
        }
        return instance;
    }


} // end class