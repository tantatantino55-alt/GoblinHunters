package view;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class SpriteManager {

    private static SpriteManager instance = null;

    private final Map<String, BufferedImage[]> animations;

    private final Map<String, BufferedImage> sheetsCache;

    private SpriteManager() {
        this.animations = new HashMap<>();
        this.sheetsCache = new HashMap<>();
    }


    public static SpriteManager getInstance() {
        if (instance == null)
            instance = new SpriteManager();
        return instance;
    }


    public void loadAnimation(String actionKey, String path, int startIdx, int count, int spriteSize) {

        BufferedImage masterSheet = sheetsCache.get(path);

        if (masterSheet == null) {
            masterSheet = ResourceManager.loadImage(path);
            if (masterSheet == null) return;

            sheetsCache.put(path, masterSheet);
            System.out.println("SpriteManager:  File caricato in cache: " + path);
        }

        int cols = masterSheet.getWidth() / spriteSize;
        animations.put(actionKey, extract(masterSheet, startIdx, count, cols, spriteSize));

        System.out.println("SpriteManager:  Animazione '" + actionKey + "' pronta.");
    }

    private BufferedImage[] extract(BufferedImage sheet, int startIdx, int count, int cols, int size) {
        BufferedImage[] frames = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            int currentIdx = startIdx + i;
            int col = currentIdx % cols;
            int row = currentIdx / cols;

            frames[i] = sheet.getSubimage(col * size, row * size, size, size);
        }
        return frames;
    }

    public BufferedImage getFrame(String actionKey, int frameIdx) {
        BufferedImage[] anim = animations.get(actionKey);
        if (anim != null && frameIdx < anim.length) return anim[frameIdx];
        return null;
    }
}

