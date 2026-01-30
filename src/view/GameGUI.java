package view;

import utils.Config;

import javax.swing.*;
import java.awt.*;

public class GameGUI extends JFrame{



    //---------------------------------------------------------------
    // INSTANCE ATTRIBUTES
    //---------------------------------------------------------------
    private GamePanel gamePanel;
    private AbstractDrawer drawer;


    public GameGUI(AbstractDrawer drawer){
        super("Goblin Hunter");

        this.createGUI(drawer);
    }

    private void createGUI(AbstractDrawer drawer){
        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(Config.WINDOW_PREFERRED_WIDTH, Config.WINDOW_PREFERRED_HEIGHT));
        this.setResizable(false);
        this.gamePanel = new GamePanel(drawer);
        Container contPane = this.getContentPane();
        contPane.setLayout(new BorderLayout());
        contPane.add(this.gamePanel,BorderLayout.CENTER);
        this.pack();
        this.setVisible(true);
    }

    // Metodo per esporre il pannello di gioco
    public GamePanel getGamePanel() {
        return gamePanel;
    }
}
