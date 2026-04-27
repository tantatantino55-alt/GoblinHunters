import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class FindCenters {
    public static void main(String[] args) throws Exception {
        File inputFile = new File("c:\\Users\\OTHMANE\\IdeaProjects\\GoblinHunters\\images\\StartGame.png");
        BufferedImage img = ImageIO.read(inputFile);
        
        // Scan a horizontal line at Y = 400 (roughly middle of the portraits)
        int y = 400;
        int width = img.getWidth();
        StringBuilder sb = new StringBuilder();
        
        for (int x = 0; x < width; x++) {
            int rgb = img.getRGB(x, y);
            int r = (rgb >> 16) & 0xff;
            int g = (rgb >> 8) & 0xff;
            int b = rgb & 0xff;
            
            // To find the boundaries of the ornate frames, we can look for brightness
            // Background is dark blue/purple cave.
            // Ornate frames are bright gold/brown.
            int brightness = (r + g + b) / 3;
            
            if (brightness > 100) {
                sb.append("#"); // Frame or character
            } else {
                sb.append("."); // Background
            }
        }
        
        System.out.println("Line at Y=" + y + ":");
        // Print in blocks of 100 for readability
        for (int i = 0; i < width; i += 100) {
            int end = Math.min(i + 100, width);
            System.out.printf("%4d: %s\n", i, sb.substring(i, end));
        }
    }
}
