package view;

import utils.PlayerState;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class SpriteManager {

    private static SpriteManager instance = null;

    private final Map<Object, BufferedImage[]> animations = new HashMap<>();
    private final Map<String, BufferedImage> sheetsCache = new HashMap<>();

    private SpriteManager() {}

    public static SpriteManager getInstance() {
        if (instance == null) instance = new SpriteManager();
        return instance;
    }

    // ========================================================================
    // 1. CARICAMENTO ANIMAZIONI (Versione "Bulletproof" / A prova di bomba)
    // ========================================================================
    public void loadAnimation(Object key, String path, int startLinearIndex, int count, int size) {
        BufferedImage sheet = loadSheet(path);
        if (sheet == null) return;

        // Calcoliamo quante colonne ha il foglio (es. larghezza 1024 / size 128 = 8 colonne)
        int colsPerRow = sheet.getWidth() / size;
        if (colsPerRow == 0) colsPerRow = 1; // Sicurezza

        BufferedImage[] frames = new BufferedImage[count];

        for (int i = 0; i < count; i++) {
            // Calcoliamo l'indice assoluto del frame corrente (es. 36, 37, 38...)
            int currentIndex = startLinearIndex + i;

            // MATEMATICA CRUCIALE:
            // Calcoliamo riga e colonna PER OGNI FRAME.
            // Se finisce la riga, questo calcolo lo fa andare automaticamente a capo.
            int col = currentIndex % colsPerRow;
            int row = currentIndex / colsPerRow;

            // Coordinate pixel
            int x = col * size;
            int y = row * size;

            // Controllo Bordi (Se l'indice chiede un pezzo fuori dall'immagine)
            if (x + size > sheet.getWidth() || y + size > sheet.getHeight()) {
                // Crea un frame vuoto trasparente invece di crashare
                // System.err.println("SpriteManager: Frame fuori bordo per " + key + " indice " + i);
                frames[i] = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            } else {
                frames[i] = sheet.getSubimage(x, y, size, size);
            }
        }

        animations.put(key, frames);
    }

    // ========================================================================
    // 2. ESTRAZIONE TILE STATICHE (Per i muri)
    // ========================================================================
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

    // ========================================================================
    // 3. RECUPERO SPRITE
    // ========================================================================
    public BufferedImage getSprite(Object key, int frameIdx) {
        BufferedImage[] anim = animations.get(key);
        if (anim != null && anim.length > 0) {
            return anim[Math.abs(frameIdx) % anim.length];
        }
        return null; // O ritorna un placeholder rosa per debug
    }

    /** Helper per il Player (Compatibilità con ConcreteDrawer) */
    public BufferedImage getPlayerSprite(PlayerState state, int frameIdx) {
        return getSprite(state, frameIdx);
    }

    // ========================================================================
    // 4. METODI PRIVATI (Loading & Cache)
    // ========================================================================
    private BufferedImage loadSheet(String path) {
        if (!sheetsCache.containsKey(path)) {
            BufferedImage sheet = ResourceManager.loadImage(path);
            if (sheet == null) {
                System.err.println("SpriteManager: ERRORE! Immagine non trovata: " + path);
                return null;
            }
            sheetsCache.put(path, sheet);
        }
        return sheetsCache.get(path);
    }
}