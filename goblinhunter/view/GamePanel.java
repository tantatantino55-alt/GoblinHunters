package goblinhunter.view;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    //SCREEN SETTING
    final int originalTileSize = 48 ;
    final int GRID_WIDTH = 13; //
    final int GRID_HEIGHT = 11; // DEVO FARE IN MODO CHE VENGONO OTTENUTE DAL MODEL PER RISPETTARE L ARCHIETETTURA MODEL VIEW CONTROLLER
    //SFRUTTERO IVIEW CONTROLLER CHE INVOCA UN METODO SU IVIEW CHE HA
    final int GAMEPANEL_ = originalTileSize * GRID_WIDTH;
    final int screenHeigth = originalTileSize * GRID_HEIGHT;

    public GamePanel(){
     this.setPreferredSize(new Dimension(GAMEPANEL_,screenHeigth));
     this.setBackground(Color.black);
     this.setDoubleBuffered(true); // better rendering perfoormance

    }

}
