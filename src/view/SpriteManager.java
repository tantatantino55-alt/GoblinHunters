package view;

import utils.PlayerState;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class SpriteManager {

    private static SpriteManager instance = null;

    private final Map<Object, BufferedImage[]> animations;
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

    public void loadAnimation(Object key, String path, int startIdx, int count, int spriteSize) {
        BufferedImage masterSheet = sheetsCache.get(path);

        if (masterSheet == null) {
            masterSheet = ResourceManager.loadImage(path);
            // FIX 1: Controllo robusto se l'immagine non esiste
            if (masterSheet == null) {
                System.err.println("SpriteManager: ERRORE FATALE! Immagine non trovata: " + path);
                return; // Esce senza fare calcoli che farebbero crashare tutto
            }
            sheetsCache.put(path, masterSheet);
        }

        // FIX 2: Prevenzione divisione per zero
        if (masterSheet.getWidth() < spriteSize) {
            System.err.println("SpriteManager: ERRORE! L'immagine " + path + " è troppo piccola per sprite di size " + spriteSize);
            return;
        }

        int cols = masterSheet.getWidth() / spriteSize;

        // FIX 3: Un ulteriore controllo di sicurezza
        if (cols == 0) cols = 1;

        animations.put(key, extract(masterSheet, startIdx, count, cols, spriteSize));
        // Debug opzionale per confermare il caricamento
        // System.out.println("Caricata animazione: " + key);
    }

    private BufferedImage[] extract(BufferedImage sheet, int startIdx, int count, int cols, int size) {
        BufferedImage[] frames = new BufferedImage[count];

        for (int i = 0; i < count; i++) {
            int currentIdx = startIdx + i;

            int col = currentIdx % cols;
            int row = currentIdx / cols;

            // FIX 4: Controllo bordi migliorato
            // Verifica che il rettangolo che stiamo per tagliare esista davvero nell'immagine
            if (col * size + size > sheet.getWidth() || row * size + size > sheet.getHeight()) {
                System.err.println("SpriteManager: Errore! Frame " + i + " (Indice " + currentIdx + ") è fuori dall'immagine!");
                // Invece di 'continue' (che lascia null), usiamo un'immagine vuota o nera per evitare crash al disegno
                frames[i] = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            } else {
                frames[i] = sheet.getSubimage(col * size, row * size, size, size);
            }
        }
        return frames;
    }

    public BufferedImage getSprite(Object key, int frameIdx) {
        BufferedImage[] anim = animations.get(key);

        if (anim != null && anim.length > 0) {
            // FIX 5: Math.abs per gestire casi strani di frame negativi (non si sa mai)
            return anim[Math.abs(frameIdx) % anim.length];
        }
        return null;
    }

    public BufferedImage getPlayerSprite(PlayerState state, int frameIdx) {
        return getSprite(state, frameIdx);
    }
}