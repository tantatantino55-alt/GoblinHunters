package view;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ResourceManager {

    public static BufferedImage loadImage(String relativePath) {
        try {
            // getResourceAsStream usa il ClassLoader per trovare la risorsa
            InputStream inputStream = ResourceManager.class.getResourceAsStream(relativePath);

            if (inputStream == null) {
                System.err.println("ResourceManager: Risorsa non trovata: " + relativePath);
                return null;
            }

            BufferedImage image = ImageIO.read(inputStream);
            inputStream.close();

            if (image != null) {
                System.out.println("ResourceManager: Caricata: " + relativePath);
            } else {
                System.err.println("ResourceManager: Impossibile decodificare: " + relativePath);
            }

            return image;

        } catch (IOException e) {
            System.err.println("ResourceManager:  Errore caricamento " + relativePath);
            e.printStackTrace();
            return null;
        }
    }

    public static boolean resourceExists(String relativePath) {
        InputStream inputStream = ResourceManager.class.getResourceAsStream(relativePath);

        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public static InputStream getResourceStream(String relativePath) {
        InputStream inputStream = ResourceManager.class.getResourceAsStream(relativePath);

        if (inputStream == null) {
            System.err.println("ResourceManager: Stream non disponibile per: " + relativePath);
        }

        return inputStream;
    }


} // end class