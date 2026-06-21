package goblinhunters.view;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ResourceManager {

    public static BufferedImage loadImage(String relativePath) {
        try (InputStream inputStream = ResourceManager.class.getResourceAsStream(relativePath)) {
            if (inputStream == null) {
                System.err.println("ResourceManager: Resource not found: " + relativePath);
                return null;
            }

            BufferedImage image = ImageIO.read(inputStream);

            if (image == null) {
                System.err.println("ResourceManager: Failed to decode: " + relativePath);
            }

            return image;

        } catch (IOException e) {
            System.err.println("ResourceManager: Load error: " + relativePath + " — " + e.getMessage());
            return null;
        }
    }

    public static InputStream getResourceStream(String relativePath) {
        InputStream inputStream = ResourceManager.class.getResourceAsStream(relativePath);
        if (inputStream == null) {
            System.err.println("ResourceManager: Stream unavailable for: " + relativePath);
        }
        return inputStream;
    }
}
