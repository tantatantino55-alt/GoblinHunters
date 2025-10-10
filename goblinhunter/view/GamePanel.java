package goblinhunter.view;

import goblinhunter.controller.ControllerForView;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    //SCREEN SETTING
    private final static int CELL_SIZE = 48 ;// number of pixels
    private final static Dimension PREFERRED_SIZE = new Dimension(CELL_SIZE * ControllerForView.getInstance().getNumRows(),CELL_SIZE * ControllerForView.getInstance().getNumColumns());

    public GamePanel(){
     this.setPreferredSize(new Dimension(GAMEPANEL_,screenHeigth));
     this.setBackground(Color.black);
     this.setDoubleBuffered(true); // better rendering perfoormance

    }

}
