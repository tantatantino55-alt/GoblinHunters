package goblinhunter.view;

import javax.swing.*;
import java.awt.*;

public class GameGUI extends JFrame{

    //---------------------------------------------------------------
    // STATIC CONSTANTS
    //---------------------------------------------------------------
    private final static int WINDOW_PREFERRED_WIDTH = 400;
    private final static int WINDOW_PREFERRED_HEIGHT = 600;

    //---------------------------------------------------------------
    // INSTANCE ATTRIBUTES
    //---------------------------------------------------------------
    private GamePanel gamePanel;
    private AbstractDrawer drawer;


    public GameGUI(AbstractDrawer drawer){
        super("Goblin Hunter");


    }

    private void createGUI(){
        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(WINDOW_PREFERRED_WIDTH, WINDOW_PREFERRED_HEIGHT));
        this.setResizable(false);
        this.gamePanel = new GamePanel();
        Container contPane = this.getContentPane();
        contPane.setLayout(new BorderLayout());
        contPane.add(this.gamePanel, BorderLayout.CENTER);
        this.pack();
        this.setVisible(true);
    }
}
