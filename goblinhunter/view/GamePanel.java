package goblinhunter.view;

import goblinhunter.controller.ControllerForView;
import goblinhunter.utils.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePanel extends JPanel implements KeyListener {
    private AbstractDrawer drawer = null;

    //SCREEN SETTING
    private final static int CELL_SIZE = 48 ;// number of pixels
    private final static Dimension PREFERRED_SIZE = new Dimension(CELL_SIZE * ControllerForView.getInstance().getNumRows(),CELL_SIZE * ControllerForView.getInstance().getNumColumns());

    public GamePanel(AbstractDrawer drawer) {
        super();
        this.drawer = drawer;
        this.setPreferredSize(new Dimension(PREFERRED_SIZE));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // better rendering perfoormancehttps://code-with-me.global.jetbrains.com/AVoRUZpss0eu6vg2xWpIXA
        this.setFocusable(true); // Fondamentale per ricevere l'input da tastiera!
        this.addKeyListener(this);
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        /* add instructions for your specific drawing */
        drawer.draw(g);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                ControllerForView.getInstance().MoveLeft();
                this.repaint();
                System.out.println("Pressed key: VK_LEFT");
                break;
            case KeyEvent.VK_RIGHT:
                ControllerForView.getInstance().MoveRight();
                this.repaint();
                System.out.println("Pressed key: VK_RIGHT");
                break;
            case KeyEvent.VK_DOWN:
                ControllerForView.getInstance().MoveDown();
                this.repaint();
                System.out.println("Pressed key: VK_DOWN");
                break;
            case KeyEvent.VK_UP:
                ControllerForView.getInstance().MoveUp();
                this.repaint();
                System.out.println("Pressed key: VK_UP");
                break;
            case KeyEvent.VK_SPACE:
                ControllerForView.getInstance().PlaceBomb();
                this.repaint();
                System.out.println("Pressed key: VK_SPACE");
                break;
            //default: System.out.println("Use only the following keys: VK_LEFT, VK_RIGHT, VK_DOWN, VK_UP");
        }
    }


        @Override
        public void keyTyped (KeyEvent e){

        }


        @Override
        public void keyReleased (KeyEvent e){

        }
    }

