import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class FindCrystals {
    public static void main(String[] args) throws Exception {
        File inputFile = new File("c:\\Users\\OTHMANE\\IdeaProjects\\GoblinHunters\\images\\StartGame.png");
        BufferedImage img = ImageIO.read(inputFile);
        
        int width = img.getWidth();
        // The top crystals are likely between y = 150 and y = 250.
        // Let's sum the brightness of the columns in this Y range.
        int[] columnBrightness = new int[width];
        for (int x = 0; x < width; x++) {
            int sum = 0;
            for (int y = 150; y < 250; y++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                // Emphasize bright pixels to find the gems
                if (r + g + b > 400) {
                    sum += (r + g + b);
                }
            }
            columnBrightness[x] = sum;
        }
        
        // Find 4 distinct peaks separated by at least 150 pixels.
        for (int i = 0; i < 4; i++) {
            int maxVal = 0;
            int maxIdx = -1;
            for (int x = 0; x < width; x++) {
                if (columnBrightness[x] > maxVal) {
                    maxVal = columnBrightness[x];
                    maxIdx = x;
                }
            }
            System.out.println("Peak " + (i+1) + " at X=" + maxIdx);
            // Suppress this region so we can find the next peak
            for (int x = Math.max(0, maxIdx - 80); x < Math.min(width, maxIdx + 80); x++) {
                columnBrightness[x] = 0;
            }
        }
    }
}
