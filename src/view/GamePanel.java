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
            Config.GAME_PANEL_WIDTH,
            Config.GAME_PANEL_HEIGHT
    );
    private final static int CELL_SIZE = Config.TILE_SIZE;

    public GamePanel(AbstractDrawer drawer) {
        super();
        this.drawer = drawer;
        this.setPreferredSize(PREFERRED_SIZE);
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); //
        this.setFocusable(true);
        setupKeyBindings();
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawer.draw(g);
    }


    private void setupKeyBindings() {
            InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap actionMap = this.getActionMap();
            final int speed = Config.PLAYER_SPEED;

            // -----------------------------------------------------------------
            // 1. ASSE X: DESTRA e SINISTRA
            // -----------------------------------------------------------------


            inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
            actionMap.put("moveRight", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ControllerForView.getInstance().setPlayerMovement(
                            speed,
                            ControllerForView.getInstance().getDeltaY()
                    );
                }
            });


            inputMap.put(KeyStroke.getKeyStroke("released RIGHT"), "stopRight");
            actionMap.put("stopRight", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (ControllerForView.getInstance().getDeltaX() > 0) {
                        ControllerForView.getInstance().setPlayerMovement(
                                0,
                                ControllerForView.getInstance().getDeltaY()
                        );
                    }
                }
            });


            inputMap.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
            actionMap.put("moveLeft", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ControllerForView.getInstance().setPlayerMovement(
                            -speed,
                            ControllerForView.getInstance().getDeltaY()
                    );
                }
            });


            inputMap.put(KeyStroke.getKeyStroke("released LEFT"), "stopLeft");
            actionMap.put("stopLeft", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (ControllerForView.getInstance().getDeltaX() < 0) {
                        ControllerForView.getInstance().setPlayerMovement(
                                0,
                                ControllerForView.getInstance().getDeltaY()
                        );
                    }
                }
            });

        // -----------------------------------------------------------------
        // 2. ASSE Y: SU e GIÙ
        // -----------------------------------------------------------------


        inputMap.put(KeyStroke.getKeyStroke("UP"), "moveUp");
        actionMap.put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ControllerForView.getInstance().setPlayerMovement(
                        ControllerForView.getInstance().getDeltaX(),
                        -speed
                );
            }
        });


        inputMap.put(KeyStroke.getKeyStroke("released UP"), "stopUp");
        actionMap.put("stopUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ControllerForView.getInstance().getDeltaY() < 0) {
                    ControllerForView.getInstance().setPlayerMovement(
                            ControllerForView.getInstance().getDeltaX(),
                            0
                    );
                }
            }
        });


        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        actionMap.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ControllerForView.getInstance().setPlayerMovement(
                        ControllerForView.getInstance().getDeltaX(),
                        speed
                );
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("released DOWN"), "stopDown");
        actionMap.put("stopDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ControllerForView.getInstance().getDeltaY() > 0) {
                    ControllerForView.getInstance().setPlayerMovement(
                            ControllerForView.getInstance().getDeltaX(),
                            0
                    );
                }
            }
        });

        // -----------------------------------------------------------------
        // 3. AZIONI
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

}

