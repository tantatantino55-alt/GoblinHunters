package view;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestisce il caricamento e il ritaglio (slicing) delle risorse grafiche.
 * Supporta sia animazioni (strisce di sprite) che tile statiche (singoli riquadri).
 */
public class SpriteManager {

    private static SpriteManager instance = null;

    // Cache per le animazioni già ritagliate (Array di immagini)
    private final Map<Object, BufferedImage[]> animations;

    // Cache per i file sorgente interi (per non ricaricare il PNG dal disco 100 volte)
    private final Map<String, BufferedImage> sheetsCache;

    private SpriteManager() {
        this.animations = new HashMap<>();
        this.sheetsCache = new HashMap<>();
    }

    public static SpriteManager getInstance() {
        if (instance == null) {
            instance = new SpriteManager();
        }
        return instance;
    }

    /**
     * Carica un'immagine intera in memoria (o la recupera dalla cache).
     */
    private BufferedImage loadSheet(String path) {
        if (!sheetsCache.containsKey(path)) {
            BufferedImage sheet = ResourceManager.loadImage(path);
            if (sheet == null) {
                System.err.println("SpriteManager: ERRORE FATALE! Sheet non trovato: " + path);
                return null;
            }
            sheetsCache.put(path, sheet);
        }
        return sheetsCache.get(path);
    }

    /**
     * METODO NUOVO: Estrae una SINGOLA tile statica da uno sheet.
     * Utile per TileManager (muri, pavimenti, oggetti fermi).
     *
     * @param path Percorso del file (es. "/dungeon.png")
     * @param col Indice colonna (0, 1, 2...)
     * @param row Indice riga (0, 1, 2...)
     * @param width Larghezza del ritaglio
     * @param height Altezza del ritaglio
     * @return L'immagine ritagliata o null se errore
     */
    public BufferedImage extractTile(String path, int col, int row, int width, int height) {
        BufferedImage sheet = loadSheet(path);
        if (sheet == null) return null;

        // Calcolo coordinate pixel
        int x = col * width;
        int y = row * height;

        // Controllo bordi per sicurezza
        if (x + width > sheet.getWidth() || y + height > sheet.getHeight()) {
            System.err.println("SpriteManager: Errore ritaglio Tile statico (" + col + "," + row + ") fuori dai bordi in " + path);
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); // Ritorna vuoto per non crashare
        }

        return sheet.getSubimage(x, y, width, height);
    }

    /**
     * Carica una sequenza di animazione (striscia orizzontale).
     *
     * @param key Chiave univoca per salvare l'animazione (es. PlayerState.IDLE)
     * @param path Percorso file
     * @param startCol Colonna di partenza
     * @param row Riga dell'animazione
     * @param count Numero di frame da prendere
     * @param size Dimensione (lato) del frame quadrato
     */
    public void loadAnimation(Object key, String path, int startCol, int row, int count, int size) {
        BufferedImage sheet = loadSheet(path);
        if (sheet == null) return;

        BufferedImage[] frames = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            int col = startCol + i;

            // Verifica bordi
            if ((col * size) + size > sheet.getWidth() || (row * size) + size > sheet.getHeight()) {
                System.err.println("SpriteManager: Frame animazione fuori bordo! Key: " + key);
                frames[i] = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            } else {
                frames[i] = sheet.getSubimage(col * size, row * size, size, size);
            }
        }
        animations.put(key, frames);
    }

    /**
     * Recupera un frame specifico di un'animazione.
     */
    public BufferedImage getSprite(Object key, int frameIdx) {
        BufferedImage[] anim = animations.get(key);
        if (anim != null && anim.length > 0) {
            // Usa il modulo per ciclare l'animazione all'infinito
            return anim[Math.abs(frameIdx) % anim.length];
        }
        return null;
    }
}