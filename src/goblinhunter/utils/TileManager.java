package goblinhunter.utils;

import java.awt.*;
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
        //loadTileImage(Config.CELL_DESTRUCTIBLE_BLOCK, Config.TILE_WALL_DESTRUCTIBLE);

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
            tileImages.put(cellType, createPlaceholderImage(cellType));
        }
    }

    /**
     * Crea un'immagine placeholder colorata per tiles mancanti.
     * Ogni tipo di cella ha un colore diverso per debug visivo.
     *
     * @param cellType Tipo di cella
     * @return BufferedImage 48x48 colorata
     */
    private BufferedImage createPlaceholderImage(int cellType) {
        BufferedImage placeholder = new BufferedImage(
                Config.TILE_SIZE,
                Config.TILE_SIZE,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = placeholder.createGraphics();

        // Colore diverso per ogni tipo di cella
        Color fillColor;
        switch (cellType) {
            case Config.CELL_EMPTY:
                fillColor = new Color(200, 200, 200); // Grigio chiaro
                break;
            case Config.CELL_INDESTRUCTIBLE_BLOCK:
                fillColor = new Color(80, 80, 80); // Grigio scuro
                break;
            case Config.CELL_DESTRUCTIBLE_BLOCK:
                fillColor = new Color(255, 165, 0); // Arancione
                break;
            default:
                fillColor = Color.MAGENTA; // Magenta = errore/tipo sconosciuto
        }

        // Riempie il quadrato
        g2d.setColor(fillColor);
        g2d.fillRect(0, 0, Config.TILE_SIZE, Config.TILE_SIZE);

        // Bordo nero
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, Config.TILE_SIZE - 1, Config.TILE_SIZE - 1);

        // Scrive il numero del tipo al centro (per debug)
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        String text = String.valueOf(cellType);
        int textX = (Config.TILE_SIZE - fm.stringWidth(text)) / 2;
        int textY = (Config.TILE_SIZE + fm.getAscent()) / 2;
        g2d.drawString(text, textX, textY);

        g2d.dispose();

        return placeholder;
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