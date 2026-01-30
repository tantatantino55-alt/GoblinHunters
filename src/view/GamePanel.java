package view;

import controller.ControllerForView;
import utils.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GamePanel extends JPanel {
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
        //this.addKeyListener(this);
        setupKeyBindings();
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        /* add instructions for your specific drawing */
        drawer.draw(g);
    }


    private void setupKeyBindings() {
            // La mappa che associa i tasti (KeyStroke) a un'azione (Stringa)
            InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            // La mappa che associa l'azione (Stringa) al codice da eseguire (AbstractAction)
            ActionMap actionMap = this.getActionMap();

            // La velocità fissa di movimento
            final int speed = Config.PLAYER_SPEED;

            // -----------------------------------------------------------------
            // 1. ASSE X: DESTRA e SINISTRA
            // -----------------------------------------------------------------

            // AZIONE: Premere la freccia DESTRA (KEY_PRESSED)
            inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
            actionMap.put("moveRight", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Imposta la X a +speed, mantenendo l'attuale Y
                    ControllerForView.getInstance().setPlayerMovement(
                            speed,
                            ControllerForView.getInstance().getPlayer().getDeltaY()
                    );
                }
            });

            // AZIONE: Rilasciare la freccia DESTRA (KEY_RELEASED)
            inputMap.put(KeyStroke.getKeyStroke("released RIGHT"), "stopRight");
            actionMap.put("stopRight", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Se il giocatore stava andando a destra (deltaX > 0), lo ferma su X.
                    if (ControllerForView.getInstance().getPlayer().getDeltaX() > 0) {
                        ControllerForView.getInstance().setPlayerMovement(
                                0,
                                ControllerForView.getInstance().getPlayer().getDeltaY()
                        );
                    }
                }
            });

            // AZIONE: Premere la freccia SINISTRA (KEY_PRESSED)
            inputMap.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
            actionMap.put("moveLeft", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Imposta la X a -speed, mantenendo l'attuale Y
                    ControllerForView.getInstance().setPlayerMovement(
                            -speed,
                            ControllerForView.getInstance().getDeltaY()
                    );
                }
            });

            // AZIONE: Rilasciare la freccia SINISTRA (KEY_RELEASED)
            inputMap.put(KeyStroke.getKeyStroke("released LEFT"), "stopLeft");
            actionMap.put("stopLeft", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Se il giocatore stava andando a sinistra (deltaX < 0), lo ferma su X.
                    if (ControllerForView.getInstance().getPlayer().getDeltaX() < 0) {
                        ControllerForView.getInstance().setPlayerMovement(
                                0,
                                ControllerForView.getInstance().getPlayer().getDeltaY()
                        );
                    }
                }
            });

        // -----------------------------------------------------------------
        // 2. ASSE Y: SU e GIÙ
        // -----------------------------------------------------------------

        // AZIONE: Premere la freccia SU (KEY_PRESSED)
        inputMap.put(KeyStroke.getKeyStroke("UP"), "moveUp");
        actionMap.put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Imposta la Y a -speed, mantenendo l'attuale X
                ControllerForView.getInstance().setPlayerMovement(
                        ControllerForView.getInstance().getPlayer().getDeltaX(),
                        -speed
                );
            }
        });

        // AZIONE: Rilasciare la freccia SU (KEY_RELEASED)
        inputMap.put(KeyStroke.getKeyStroke("released UP"), "stopUp");
        actionMap.put("stopUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Se il giocatore stava andando in su (deltaY < 0), lo ferma su Y.
                if (ControllerForView.getInstance().getPlayer().getDeltaY() < 0) {
                    ControllerForView.getInstance().setPlayerMovement(
                            ControllerForView.getInstance().getDeltaX(),
                            0
                    );
                }
            }
        });

        // AZIONE: Premere la freccia GIÙ (KEY_PRESSED)
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        actionMap.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Imposta la Y a +speed, mantenendo l'attuale X
                ControllerForView.getInstance().setPlayerMovement(
                        ControllerForView.getInstance().getDeltaX(),
                        speed
                );
            }
        });

        // AZIONE: Rilasciare la freccia GIÙ (KEY_RELEASED)
        inputMap.put(KeyStroke.getKeyStroke("released DOWN"), "stopDown");
        actionMap.put("stopDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Se il giocatore stava andando in giù (deltaY > 0), lo ferma su Y.
                if (ControllerForView.getInstance().getPlayer().getDeltaY() > 0) {
                    ControllerForView.getInstance().setPlayerMovement(
                            ControllerForView.getInstance().getDeltaX(),
                            0
                    );
                }
            }
        });

        // -----------------------------------------------------------------
        // 3. AZIONI (BOMBA/SPAZIO)
        // -----------------------------------------------------------------

        // AZIONE: Premere la barra SPAZIATRICE (KEY_PRESSED)
        inputMap.put(KeyStroke.getKeyStroke("SPACE"), "placeBomb");
        actionMap.put("placeBomb", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ControllerForView.getInstance().PlaceBomb();
            }
        });
    }

    /*
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

     */
    }

