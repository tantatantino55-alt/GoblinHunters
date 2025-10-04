package goblinhunter.view;

import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame{

    //---------------------------------------------------------------
    // STATIC CONSTANTS
    //---------------------------------------------------------------
    private final static int WINDOW_PREFERRED_WIDTH = 400;
    private final static int WINDOW_PREFERRED_HEIGHT = 600;

    //---------------------------------------------------------------
    // INSTANCE ATTRIBUTES
    //---------------------------------------------------------------
    private GamePanel gamePanel;


    public MainGUI(){
        super("Goblin Hunter");


    }

    private void createGUI(){
        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.gamePanel = new GamePanel();
        Container contPane = this.getContentPane();
        contPane.setLayout(new BorderLayout());
        contPane.add(this.gamePanel, BorderLayout.CENTER);
        this.pack();
        this.setVisible(true);
    }
}
