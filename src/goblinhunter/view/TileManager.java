package goblinhunter.view;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/** Manages tile images for static map cells and supports theme switching (VILLAGE, FOREST, CAVE). */
public class TileManager {

    private static TileManager instance = null;

    private final Map<String, BufferedImage[]> themeMap;
    private String currentTheme = "VILLAGE";

    private TileManager() {
        this.themeMap = new HashMap<>();
    }

    public static TileManager getInstance() {
        if (instance == null) {
            instance = new TileManager();
        }
        return instance;
    }

    /** Registers a theme's tile array under the given name. */
    public void loadTheme(String themeName, BufferedImage[] tiles) {
        themeMap.put(themeName, tiles);
    }

    /** Switches the active rendering theme. */
    public void setCurrentTheme(String themeName) {
        if (themeMap.containsKey(themeName)) {
            this.currentTheme = themeName;
        } else {
            System.err.println("TileManager: Theme '" + themeName + "' not found.");
        }
    }

    /**
     * Returns the tile image for the given cell type under the current theme.
     * Cell types: 0 = empty, 1 = indestructible, 2 = destructible.
     */
    public BufferedImage getTileImage(int cellType) {
        BufferedImage[] tiles = themeMap.get(currentTheme);
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
