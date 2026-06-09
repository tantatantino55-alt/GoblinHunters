package goblinhunters.view;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class SpriteManager {

    private static SpriteManager instance = null;

    private final Map<Object, BufferedImage[]> animations   = new HashMap<>();
    private final Map<String, BufferedImage>   sheetsCache  = new HashMap<>();
    // grayscale versions generated once at load time, never rebuilt per-frame
    private final Map<String, BufferedImage>   grayscaleCache = new HashMap<>();

    private SpriteManager() {}

    public static SpriteManager getInstance() {
        if (instance == null) instance = new SpriteManager();
        return instance;
    }

    // ==========================================================
    // animation loading
    // ==========================================================

    public void loadAnimation(Object key, String path, int startLinearIndex, int count, int size) {
        BufferedImage sheet = loadSheet(path);
        if (sheet == null) return;

        int colsPerRow = sheet.getWidth() / size;
        if (colsPerRow == 0) colsPerRow = 1;

        BufferedImage[] frames = new BufferedImage[count];

        for (int i = 0; i < count; i++) {
            int currentIndex = startLinearIndex + i;
            int col = currentIndex % colsPerRow;
            int row = currentIndex / colsPerRow;
            int x = col * size;
            int y = row * size;

            if (x + size > sheet.getWidth() || y + size > sheet.getHeight()) {
                frames[i] = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            } else {
                frames[i] = sheet.getSubimage(x, y, size, size);
            }
        }

        animations.put(key, frames);
    }

    // ==========================================================
    // single-image loading
    // ==========================================================

    public void loadSingleImage(Object key, String path) {
        BufferedImage image = loadSheet(path);
        if (image != null) {
            animations.put(key, new BufferedImage[]{ image });
        }
    }

    // ==========================================================
    // tile extraction
    // ==========================================================

    public BufferedImage extractTile(String path, int col, int row, int width, int height) {
        BufferedImage sheet = loadSheet(path);
        if (sheet == null) return null;

        int x = col * width;
        int y = row * height;

        if (x + width > sheet.getWidth() || y + height > sheet.getHeight()) {
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        return sheet.getSubimage(x, y, width, height);
    }

    // ==========================================================
    // sprite retrieval
    // ==========================================================

    public BufferedImage getSprite(Object key, int frameIdx) {
        BufferedImage[] anim = animations.get(key);
        if (anim != null && anim.length > 0) {
            return anim[Math.abs(frameIdx) % anim.length];
        }
        return null;
    }

    // ==========================================================
    // grayscale cache (built once by ResourceLoader at startup)
    // ==========================================================

    /**
     * Converts a loaded frame to grayscale and stores it under {@code cacheKey}.
     * Must be called only during resource loading — never per frame.
     *
     * @param key      animation key (e.g. "POWER_UPS")
     * @param frameIdx frame index to convert
     * @param cacheKey retrieval key (e.g. "POWER_UPS_0_gray")
     */
    public void buildGrayscale(Object key, int frameIdx, String cacheKey) {
        BufferedImage src = getSprite(key, frameIdx);
        if (src == null) return;

        BufferedImage gray = new BufferedImage(
                src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);

        java.awt.image.ColorConvertOp op = new java.awt.image.ColorConvertOp(
                java.awt.color.ColorSpace.getInstance(java.awt.color.ColorSpace.CS_GRAY), null);
        op.filter(src, gray);
        grayscaleCache.put(cacheKey, gray);
    }

    /** Returns a pre-built grayscale image, or null if buildGrayscale was not called for this key. */
    public BufferedImage getGrayscale(String cacheKey) {
        return grayscaleCache.get(cacheKey);
    }

    // ==========================================================
    // private helpers
    // ==========================================================

    private BufferedImage loadSheet(String path) {
        if (!sheetsCache.containsKey(path)) {
            BufferedImage sheet = ResourceManager.loadImage(path);
            if (sheet == null) {
                System.err.println("SpriteManager: Image not found: " + path);
                return null;
            }
            sheetsCache.put(path, sheet);
        }
        return sheetsCache.get(path);
    }
}
