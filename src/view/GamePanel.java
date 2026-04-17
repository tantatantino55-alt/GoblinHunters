package view;

import controller.ControllerForView;
import utils.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePanel extends JPanel {
    private AbstractDrawer drawer = null;

    private boolean canPlaceBomb    = true;
    private boolean canShootAura    = true;
    private boolean canStaffAttack  = true;

    public GamePanel(AbstractDrawer drawer) {
        this.drawer = drawer;
        this.setPreferredSize(new Dimension(Config.GAME_PANEL_WIDTH, Config.GAME_PANEL_HEIGHT));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        setupKeyBindings();
        setupPauseControls();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawer.draw(g);
        // Toolkit.getDefaultToolkit().sync(); // V-Sync (disabled)
    }

    // =========================================================================
    // PAUSE — ESC key + mouse routing
    // =========================================================================

    private void setupPauseControls() {

        // ESC toggles pause; also feeds key-presses into the rebind system.
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    boolean nowPaused = !ControllerForView.getInstance().isPaused();
                    ControllerForView.getInstance().setPaused(nowPaused);
                    if (!nowPaused) {
                        // Closing menu cancels any pending rebind
                        PauseMenuDrawer.getInstance().cancelRebind();
                    }
                    return;
                }

                // Route key press to the rebind handler when listening for a new key
                if (ControllerForView.getInstance().isPaused()
                        && PauseMenuDrawer.getInstance().isRebinding()) {
                    String keyName = KeyEvent.getKeyText(e.getKeyCode());
                    PauseMenuDrawer.getInstance().handleKeyForRebind(keyName, PauseState.getInstance());
                }
            }
        });

        // Mouse clicks are routed through PauseMenuDrawer while paused.
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!ControllerForView.getInstance().isPaused()) return;

                PauseMenuDrawer.ClickResult result =
                        PauseMenuDrawer.getInstance().handleClick(
                                e.getX(), e.getY(), PauseState.getInstance());

                switch (result) {
                    case RESUME -> {
                        ControllerForView.getInstance().setPaused(false);
                        PauseMenuDrawer.getInstance().cancelRebind();
                    }
                    case QUIT -> System.exit(0);
                    // REBIND_START, RESET_DEFAULTS, NONE — drawer manages internally
                    default -> { /* nothing */ }
                }
            }
        });
    }

    // =========================================================================
    // GAMEPLAY KEY BINDINGS
    // =========================================================================

    private void setupKeyBindings() {
        InputMap im = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();
        double speed = Config.ENTITY_LOGICAL_SPEED;

        // --- RIGHT ---
        im.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        am.put("moveRight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (ControllerForView.getInstance().isPaused()) return;
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

        // --- LEFT ---
        im.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        am.put("moveLeft", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (ControllerForView.getInstance().isPaused()) return;
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

        // --- UP ---
        im.put(KeyStroke.getKeyStroke("UP"), "moveUp");
        am.put("moveUp", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (ControllerForView.getInstance().isPaused()) return;
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

        // --- DOWN ---
        im.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        am.put("moveDown", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (ControllerForView.getInstance().isPaused()) return;
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

        // --- SPACE — Place Bomb ---
        im.put(KeyStroke.getKeyStroke("SPACE"), "placeBomb");
        am.put("placeBomb", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ControllerForView.getInstance().isPaused()) return;
                if (canPlaceBomb) {
                    ControllerForView.getInstance().placeBomb();
                    canPlaceBomb = false;
                }
            }
        });
        im.put(KeyStroke.getKeyStroke("released SPACE"), "resetBomb");
        am.put("resetBomb", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canPlaceBomb = true;
            }
        });

        // --- X — Shoot Aura ---
        im.put(KeyStroke.getKeyStroke("X"), "shootAura");
        am.put("shootAura", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ControllerForView.getInstance().isPaused()) return;
                if (canShootAura) {
                    ControllerForView.getInstance().playerShoot();
                    canShootAura = false;
                }
            }
        });
        im.put(KeyStroke.getKeyStroke("released X"), "resetAura");
        am.put("resetAura", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canShootAura = true;
            }
        });

        // --- Z — Staff Attack ---
        im.put(KeyStroke.getKeyStroke("Z"), "staffAttack");
        am.put("staffAttack", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ControllerForView.getInstance().isPaused()) return;
                if (canStaffAttack) {
                    ControllerForView.getInstance().staffAttack();
                    canStaffAttack = false;
                }
            }
        });
        im.put(KeyStroke.getKeyStroke("released Z"), "resetStaff");
        am.put("resetStaff", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canStaffAttack = true;
            }
        });
    }
}