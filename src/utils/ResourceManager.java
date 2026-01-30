package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * ResourceManager gestisce il caricamento delle risorse (immagini, file).
 *
 * USA getResourceAsStream() per compatibilità:
 * - ✅ Funziona durante sviluppo (IDE)
 * - ✅ Funziona in JAR compilato
 * - ✅ Cross-platform (Windows, Linux, macOS)
 *
 * NON usa File o percorsi assoluti perché:
 * - ❌ File non può leggere dentro archivi JAR
 * - ❌ Percorsi assoluti cambiano tra OS
 *
 * POSIZIONE: goblinhunter/utils/ResourceManager.java
 */
public class ResourceManager {

    /**
     * Carica un'immagine da un percorso relativo.
     *
     * @param relativePath Percorso relativo (es. "/resources/tiles/floor.png")
     * @return BufferedImage caricata, o null se fallisce
     *
     * ESEMPIO:
     *   BufferedImage img = ResourceManager.loadImage("/resources/tiles/floor.png");
     */
    public static BufferedImage loadImage(String relativePath) {
        try {
            // getResourceAsStream usa il ClassLoader per trovare la risorsa
            // Funziona sia su filesystem (sviluppo) che dentro JAR (produzione)
            InputStream inputStream = ResourceManager.class.getResourceAsStream(relativePath);

            if (inputStream == null) {
                System.err.println("ResourceManager: Risorsa non trovata: " + relativePath);
                return null;
            }

            // ImageIO può leggere da InputStream (non serve File!)
            BufferedImage image = ImageIO.read(inputStream);
            inputStream.close();

            if (image != null) {
                System.out.println("ResourceManager: ✅ Caricata: " + relativePath);
            } else {
                System.err.println("ResourceManager: ❌ Impossibile decodificare: " + relativePath);
            }

            return image;

        } catch (IOException e) {
            System.err.println("ResourceManager: ❌ Errore caricamento " + relativePath);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Verifica se una risorsa esiste.
     *
     * @param relativePath Percorso relativo della risorsa
     * @return true se la risorsa esiste, false altrimenti
     *
     * ESEMPIO:
     *   if (ResourceManager.resourceExists("/resources/tiles/floor.png")) {
     *       // La risorsa c'è!
     *   }
     */
    public static boolean resourceExists(String relativePath) {
        InputStream inputStream = ResourceManager.class.getResourceAsStream(relativePath);

        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                // Ignora errori di chiusura /// eventualmente mettere printstacktrace
            }
            return true;
        }

        return false;
    }

    /**
     * Ottiene uno stream di una risorsa per operazioni custom.
     * ATTENZIONE: Il chiamante DEVE chiudere lo stream dopo l'uso!
     *
     * @param relativePath Percorso relativo della risorsa
     * @return InputStream della risorsa, o null se non trovata
     *
     * ESEMPIO:
     *   InputStream stream = ResourceManager.getResourceStream("/data/level1.txt");
     *   if (stream != null) {
     *       BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
     *       // ... leggi il file ...
     *       stream.close(); // IMPORTANTE!
     *   }
     */
    public static InputStream getResourceStream(String relativePath) {
        InputStream inputStream = ResourceManager.class.getResourceAsStream(relativePath);

        if (inputStream == null) {
            System.err.println("ResourceManager: Stream non disponibile per: " + relativePath);
        }

        return inputStream;
    }

    /**
     * Stampa informazioni di debug sul sistema.
     * Utile per verificare configurazione e troubleshooting.
     */
    public static void printDebugInfo() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   ResourceManager - Debug Info         ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Sistema Operativo: " + System.getProperty("os.name"));
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("ClassLoader: " + ResourceManager.class.getClassLoader());

        try {
            String location = ResourceManager.class.getProtectionDomain()
                    .getCodeSource().getLocation().toString();
            System.out.println("Class Location: " + location);

            if (location.endsWith(".jar")) {
                System.out.println("📦 Running from JAR");
            } else {
                System.out.println("🔧 Running from IDE/Filesystem");
            }
        } catch (Exception e) {
            System.out.println("⚠️  Cannot determine location");
        }

        System.out.println("════════════════════════════════════════");
    }

} // end class