package view;

import controller.ControllerForView;
import utils.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GamePanel extends JPanel {
    private AbstractDrawer drawer = null;

    private boolean canPlaceBomb = true;
    private boolean canShootAura = true;
    private boolean canStaffAttack = true;

    public GamePanel(AbstractDrawer drawer) {
        this.drawer = drawer;
        this.setPreferredSize(new Dimension(Config.GAME_PANEL_WIDTH, Config.GAME_PANEL_HEIGHT));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        setupKeyBindings();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawer.draw(g);

        // Sincronizza il disegno grafico con il refresh del monitor (V-Sync)
        //Toolkit.getDefaultToolkit().sync();
    }

    private void setupKeyBindings() {
        InputMap im = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();
        double speed = Config.ENTITY_LOGICAL_SPEED;

        // Destra
        im.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        am.put("moveRight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ControllerForView.getInstance().setPlayerMovement(speed, ControllerForView.getInstance().getDeltaY());
            }
        });
        im.put(KeyStroke.getKeyStroke("released RIGHT"), "stopRight");
        am.put("stopRight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (ControllerForView.getInstance().getDeltaX() > 0)
                    ControllerForView.getInstance().setPlayerMovement(0, ControllerForView.getInstance().getDeltaY());
            }
        });

        // Sinistra
        im.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        am.put("moveLeft", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ControllerForView.getInstance().setPlayerMovement(-speed, ControllerForView.getInstance().getDeltaY());
            }
        });
        im.put(KeyStroke.getKeyStroke("released LEFT"), "stopLeft");
        am.put("stopLeft", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (ControllerForView.getInstance().getDeltaX() < 0)
                    ControllerForView.getInstance().setPlayerMovement(0, ControllerForView.getInstance().getDeltaY());
            }
        });

        // Su
        im.put(KeyStroke.getKeyStroke("UP"), "moveUp");
        am.put("moveUp", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ControllerForView.getInstance().setPlayerMovement(ControllerForView.getInstance().getDeltaX(), -speed);
            }
        });
        im.put(KeyStroke.getKeyStroke("released UP"), "stopUp");
        am.put("stopUp", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (ControllerForView.getInstance().getDeltaY() < 0)
                    ControllerForView.getInstance().setPlayerMovement(ControllerForView.getInstance().getDeltaX(), 0);
            }
        });

        // Giù
        im.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        am.put("moveDown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ControllerForView.getInstance().setPlayerMovement(ControllerForView.getInstance().getDeltaX(), speed);
            }
        });
        im.put(KeyStroke.getKeyStroke("released DOWN"), "stopDown");
        am.put("stopDown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (ControllerForView.getInstance().getDeltaY() > 0)
                    ControllerForView.getInstance().setPlayerMovement(ControllerForView.getInstance().getDeltaX(), 0);
            }
        });

        // --- PRESSIONE SPAZIO ---
        im.put(KeyStroke.getKeyStroke("SPACE"), "placeBomb");
        am.put("placeBomb", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Piazziamo la bomba solo se il tasto era stato precedentemente rilasciato
                if (canPlaceBomb) {
                    ControllerForView.getInstance().placeBomb();
                    canPlaceBomb = false; // Blocchiamo ulteriori attivazioni
                }
            }
        });

// --- RILASCIO SPAZIO ---
        im.put(KeyStroke.getKeyStroke("released SPACE"), "resetBomb");
        am.put("resetBomb", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canPlaceBomb = true; // Sblocchiamo la possibilità di piazzare una nuova bomba
            }
        });

        //colpo d'aura
        im.put(KeyStroke.getKeyStroke("X"), "shootAura");
        am.put("shootAura", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (canShootAura) {
                    ControllerForView.getInstance().playerShoot();
                    canShootAura = false; // Blocchiamo fino al rilascio
                }
            }
        });

        // --- NUOVO: RILASCIO TASTO X ---
        im.put(KeyStroke.getKeyStroke("released X"), "resetAura");
        am.put("resetAura", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canShootAura = true; // Sblocchiamo il colpo
            }
        });

        // --- COLPO DEL BASTONE (TASTO Z) ---
        im.put(KeyStroke.getKeyStroke("Z"), "staffAttack");
        am.put("staffAttack", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Può colpire sempre, l'unico limite è il rilascio del tasto
                if (canStaffAttack) {
                    ControllerForView.getInstance().staffAttack();
                    canStaffAttack = false; // Blocca ulteriori colpi istantanei
                }
            }
        });

// --- RILASCIO TASTO Z ---
        im.put(KeyStroke.getKeyStroke("released Z"), "resetStaff");
        am.put("resetStaff", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canStaffAttack = true; // Sblocca per il prossimo colpo
            }
        });
    }
}