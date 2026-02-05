package view;

import utils.Config;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class TileManager {

    private static TileManager instance = null;

    // ---------------------------------------------------------------
    // ATTRIBUTES
    // ---------------------------------------------------------------
    private Map<Integer, BufferedImage> tileImages;

    // ---------------------------------------------------------------
    // CONSTRUCTOR
    // ---------------------------------------------------------------
    private TileManager() {
        this.tileImages = new HashMap<>();
        loadTileImages();
    }

    private void loadTileImages() {
        System.out.println("TileManager: Inizio caricamento tiles...");

        loadTileImage(Config.CELL_EMPTY, Config.TILE_FLOOR);
        loadTileImage(Config.CELL_INDESTRUCTIBLE_BLOCK, Config.TILE_WALL_INDESTRUCTIBLE);
        loadTileImage(Config.CELL_DESTRUCTIBLE_BLOCK, Config.TILE_WALL_DESTRUCTIBLE);

        System.out.println("TileManager: Caricamento completato (" + tileImages.size() + " tiles)");
    }

    private void loadTileImage(int cellType, String resourcePath) {
        // Usa ResourceManager (compatibile JAR!)
        BufferedImage img = ResourceManager.loadImage(resourcePath);

        if (img != null) {
            tileImages.put(cellType, img);
            System.out.println("  → Tile tipo " + cellType + " caricata");
        } else {
            System.err.println("  → Tile tipo " + cellType + " NON trovata");
        }
    }



    public BufferedImage getTileImage(int cellType) {
        return tileImages.get(cellType);
    }

    public boolean hasTileImage(int cellType) {
        return tileImages.containsKey(cellType);
    }

    public int getTileCount() {
        return tileImages.size();
    }

    public static TileManager getInstance() {
        if (instance == null) {
            instance = new TileManager();
        }
        return instance;
    }


} // end class