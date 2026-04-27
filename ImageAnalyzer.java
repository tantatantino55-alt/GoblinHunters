import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageAnalyzer {
    public static void main(String[] args) throws Exception {
        File inputFile = new File("c:\\Users\\OTHMANE\\IdeaProjects\\GoblinHunters\\images\\StartGame.png");
        BufferedImage img = ImageIO.read(inputFile);
        
        Graphics2D g2d = img.createGraphics();
        
        // Draw old centers (scaled to 960) in RED
        int[] oldCenters = { 152, 342, 537, 736 };
        g2d.setColor(Color.RED);
        for (int x : oldCenters) {
            g2d.drawLine(x, 0, x, img.getHeight());
            g2d.drawString("OldC:" + x, x + 5, 100);
        }
        
        // Draw user's raw coordinates in BLUE
        int[] userCoords = { 43, 237, 427, 616 };
        g2d.setColor(Color.BLUE);
        for (int x : userCoords) {
            g2d.drawLine(x, 0, x, img.getHeight());
            g2d.drawString("User:" + x, x + 5, 150);
        }

        // Draw my previous selector coordinates in GREEN
        int[] mySelector = { 108, 302, 492, 681 };
        g2d.setColor(Color.GREEN);
        for (int x : mySelector) {
            g2d.drawLine(x, 0, x, img.getHeight());
            g2d.drawString("Sel:" + x, x + 5, 200);
        }
        
        g2d.dispose();
        
        File outputFile = new File("c:\\Users\\OTHMANE\\IdeaProjects\\GoblinHunters\\images\\StartGame_analyzed.png");
        ImageIO.write(img, "png", outputFile);
        System.out.println("Analyzed image saved to: " + outputFile.getAbsolutePath());
    }
}
