package goblinhunter.view;

import goblinhunter.controller.ControllerForView;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private AbstractDrawer drawer = null;

    //SCREEN SETTING
    private final static int CELL_SIZE = 48 ;// number of pixels
    private final static Dimension PREFERRED_SIZE = new Dimension(CELL_SIZE * ControllerForView.getInstance().getNumRows(),CELL_SIZE * ControllerForView.getInstance().getNumColumns());

    public GamePanel(AbstractDrawer drawer){
        super();
        this.drawer = drawer;
        this.setPreferredSize(new Dimension(PREFERRED_SIZE));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // better rendering perfoormance

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        /* add instructions for your specific drawing */
        drawer.draw(g);
    }

}
