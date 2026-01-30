


package utils;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class SpriteManager {

    private static SpriteManager instance = null;

    // Cache per le animazioni finali (es. "PLAYER_WALK" -> array di frame)
    private final Map<String, BufferedImage[]> animations;

    // NUOVO: Cache per i fogli interi (es. "/path/to/sheet.png" -> BufferedImage intera)
    private final Map<String, BufferedImage> sheetsCache;

    private SpriteManager() {
        this.animations = new HashMap<>();
        this.sheetsCache = new HashMap<>();
    }

    public static SpriteManager getInstance() {
        if (instance == null) instance = new SpriteManager();
        return instance;
    }

    /*
     * Carica un'animazione in modo efficiente usando una cache per i file sorgente.
     */
public void loadAnimation(String actionKey, String path, int startIdx, int count, int spriteSize) {

    // 1. Cerchiamo lo spritesheet nella cache interna
    BufferedImage masterSheet = sheetsCache.get(path);

    // 2. Se non c'è, lo carichiamo tramite ResourceManager e lo salviamo nella cache
    if (masterSheet == null) {
        masterSheet = ResourceManager.loadImage(path);
        if (masterSheet == null) return;

        sheetsCache.put(path, masterSheet);
        System.out.println("SpriteManager: 💾 File caricato in cache: " + path);
    }

    // 3. Ora che l'immagine è in RAM (nuova o già presente), procediamo all'estrazione
    int cols = masterSheet.getWidth() / spriteSize;
    animations.put(actionKey, extract(masterSheet, startIdx, count, cols, spriteSize));

    System.out.println("SpriteManager: ✅ Animazione '" + actionKey + "' pronta.");
}

private BufferedImage[] extract(BufferedImage sheet, int startIdx, int count, int cols, int size) {
    BufferedImage[] frames = new BufferedImage[count];
    for (int i = 0; i < count; i++) {
        int currentIdx = startIdx + i;
        int col = currentIdx % cols;
        int row = currentIdx / cols;

        // subimage è velocissima perché punta ai dati già presenti in RAM
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

