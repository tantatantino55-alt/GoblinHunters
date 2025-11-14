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
    private final static Dimension PREFERRED_SIZE = new Dimension(
            Config.GAME_PANEL_WIDTH,  // 624 px
            Config.GAME_PANEL_HEIGHT  // 528 px
    );
    private final static int CELL_SIZE = Config.TILE_SIZE;

    public GamePanel(AbstractDrawer drawer) {
        super();
        this.drawer = drawer;
        this.setPreferredSize(PREFERRED_SIZE);
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
        int delta = Config.PLAYER_SPEED;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                ControllerForView.getInstance().setPlayerMovement(-delta, 0);
                System.out.println("Pressed key: VK_LEFT");
                break;
            case KeyEvent.VK_RIGHT:
                ControllerForView.getInstance().setPlayerMovement(delta, 0);
                System.out.println("Pressed key: VK_RIGHT");
                break;
            case KeyEvent.VK_DOWN:
                ControllerForView.getInstance().setPlayerMovement(0, delta);
                System.out.println("Pressed key: VK_DOWN");
                break;
            case KeyEvent.VK_UP:
                ControllerForView.getInstance().setPlayerMovement(0, -delta);
                System.out.println("Pressed key: VK_UP");
                break;
            case KeyEvent.VK_SPACE:
                ControllerForView.getInstance().PlaceBomb();
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
        int currentDeltaX = ControllerForView.getInstance().getPlayer().getDeltaX();
        int currentDeltaY = ControllerForView.getInstance().getPlayer().getDeltaY();

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                // Se la direzione non è più X, la azzeriamo mantenendo Y
                if (currentDeltaX != 0)
                    ControllerForView.getInstance().setPlayerMovement(0, currentDeltaY);
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
                // Se la direzione non è più Y, la azzeriamo mantenendo X
                if (currentDeltaY != 0)
                    ControllerForView.getInstance().setPlayerMovement(currentDeltaX, 0);
                break;
        }
    }
    }

