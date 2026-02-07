package view;

import utils.Config;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestisce le immagini per le celle statiche della mappa (Muri, Pavimenti, ecc.).
 * Usa SpriteManager per estrarre le texture da un unico foglio.
 */
public class TileManager {

    private static TileManager instance = null;
    private final Map<Integer, BufferedImage> tileImages;

    private TileManager() {
        this.tileImages = new HashMap<>();
        loadTilesFromSheet();
    }

    public static TileManager getInstance() {
        if (instance == null) {
            instance = new TileManager();
        }
        return instance;
    }

    /**
     * Carica i tasselli (tiles) usando lo SpriteManager.
     * Le coordinate (COL/ROW) devono essere definite in Config.
     */
    private void loadTilesFromSheet() {
        System.out.println("TileManager: Estrazione tiles dallo Sheet unico...");

        SpriteManager sm = SpriteManager.getInstance();

        // Dimensione della tile sul file PNG (potrebbe essere diversa da quella a schermo!)
        // Se il tuo sprite sheet è 32x32 ma il gioco è 64x64, qui metti 32 (o Config.TILE_SIZE_ON_SHEET)
        // Se è 1:1, usa Config.TILE_SIZE
        int size = Config.TILE_SIZE;

        // 1. Pavimento (Vuoto)
        BufferedImage floor = sm.extractTile(
                Config.MAIN_SHEET,
                Config.TILE_FLOOR_COL,
                Config.TILE_FLOOR_ROW,
                size, size
        );
        if (floor != null) tileImages.put(Config.CELL_EMPTY, floor);

        // 2. Muro Indistruttibile
        BufferedImage wallInd = sm.extractTile(
                Config.MAIN_SHEET,
                Config.TILE_WALL_IND_COL,
                Config.TILE_WALL_IND_ROW,
                size, size
        );
        if (wallInd != null) tileImages.put(Config.CELL_INDESTRUCTIBLE_BLOCK, wallInd);

        // 3. Muro Distruttibile
        BufferedImage wallDest = sm.extractTile(
                Config.MAIN_SHEET,
                Config.TILE_WALL_DEST_COL,
                Config.TILE_WALL_DEST_ROW,
                size, size
        );
        if (wallDest != null) tileImages.put(Config.CELL_DESTRUCTIBLE_BLOCK, wallDest);

        System.out.println("TileManager: Caricamento completato. Tiles caricate: " + tileImages.size());
    }

    public BufferedImage getTileImage(int cellType) {
        return tileImages.get(cellType);
    }

    public boolean hasTileImage(int cellType) {
        return tileImages.containsKey(cellType);
    }
}