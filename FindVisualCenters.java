import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class FindVisualCenters {
    public static void main(String[] args) throws Exception {
        File inputFile = new File("c:\\Users\\OTHMANE\\IdeaProjects\\GoblinHunters\\images\\StartGame.png");
        BufferedImage img = ImageIO.read(inputFile);
        
        int width = img.getWidth();
        // The image is 960 wide. Let's print a downsampled array of brightness.
        // Downsample by a factor of 10 to fit in 96 characters per line.
        int factor = 10;
        int downsampledWidth = width / factor;
        
        for (int y = 200; y < 600; y += 50) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Y=%03d: ", y));
            for (int dx = 0; dx < downsampledWidth; dx++) {
                int startX = dx * factor;
                int sumBrightness = 0;
                for (int i = 0; i < factor; i++) {
                    int rgb = img.getRGB(startX + i, y);
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    sumBrightness += (r + g + b) / 3;
                }
                int avgBrightness = sumBrightness / factor;
                
                // Map brightness to ASCII
                char c;
                if (avgBrightness < 30) c = ' ';
                else if (avgBrightness < 80) c = '.';
                else if (avgBrightness < 150) c = '*';
                else if (avgBrightness < 200) c = 'O';
                else c = '#';
                sb.append(c);
            }
            System.out.println(sb.toString());
        }
    }
}
