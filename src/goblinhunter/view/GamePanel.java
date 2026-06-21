package goblinhunter.view;

import goblinhunter.controller.ControllerForView;
import goblinhunter.controller.PauseController;
import goblinhunter.utils.Config;
import goblinhunter.utils.GameState;
import goblinhunter.utils.ViewConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel {
    private AbstractDrawer drawer = null;

    private boolean canPlaceBomb   = true;
    private boolean canShootAura   = true;
    private boolean canStaffAttack = true;

    /**
     * Action names in the ActionMap for key-press events.
     * Index matches PauseModel.ACTION_MOVE_UP … ACTION_STAFF.
     */
    private static final String[] PRESS_ACTIONS = {
            "moveUp", "moveDown", "moveLeft", "moveRight",
            "placeBomb", "shootAura", "staffAttack"
    };

    /**
     * Action names in the ActionMap for key-release events.
     * Index matches PRESS_ACTIONS.
     */
    private static final String[] RELEASE_ACTIONS = {
            "stopUp", "stopDown", "stopLeft", "stopRight",
            "resetBomb", "resetAura", "resetStaff"
    };

    /**
     * Local tracking of current keybindings (uppercase KeyStroke format).
     * Initialised from PauseModel defaults for consistency without a View→Model dependency.
     * Updated by {@link #applyKeyRebind} on every rebind.
     */
    private final String[] currentInputBindings = {
            "UP", "DOWN", "LEFT", "RIGHT", "SPACE", "X", "Z"
    };

    public GamePanel(AbstractDrawer drawer) {
        this.drawer = drawer;
        this.setPreferredSize(new Dimension(
                ViewConfig.WINDOW_PREFERRED_WIDTH,
                ViewConfig.WINDOW_PREFERRED_HEIGHT));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        setupKeyBindings();
        setupPauseControls();
        // passes the rebind callback so PauseController can update the InputMap
        // without knowing about GamePanel
        ControllerForView.getInstance().setKeyBindingApplier(this::applyKeyRebind);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        BufferedImage cabinetImg =
                SpriteManager.getInstance().getSprite("ARCADE_CABINET", 0);
        if (cabinetImg != null) {
            g.drawImage(cabinetImg, 0, 0,
                    ViewConfig.WINDOW_PREFERRED_WIDTH,
                    ViewConfig.WINDOW_PREFERRED_HEIGHT, this);
        }

        Graphics2D g2d = (Graphics2D) g.create();
        drawer.draw(g2d);
        g2d.dispose();
    }

    private void setupPauseControls() {

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (ControllerForView.getInstance().getGameState() == GameState.MENU) {
                    if (ControllerForView.getInstance().isMenuTypingName()) {
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                            ControllerForView.getInstance().menuDeleteNameChar();
                        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            ControllerForView.getInstance().menuSetTypingName(false);
                        }
                    }
                    return;
                }

                if (ControllerForView.getInstance().getGameState() == GameState.GAME_OVER) return;

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    boolean nowPaused = !ControllerForView.getInstance().isPaused();
                    ControllerForView.getInstance().setPaused(nowPaused);
                    if (!nowPaused) {
                        ControllerForView.getInstance().getPauseController().cancelAllRebinds();
                    }
                    return;
                }

                if (ControllerForView.getInstance().isPaused()) {
                    PauseController ctrl = ControllerForView.getInstance().getPauseController();
                    if (ctrl.isRebinding()) {
                        String keyName = KeyEvent.getKeyText(e.getKeyCode());
                        PauseMenuDrawer.getInstance().handleKeyForRebind(keyName, ctrl);
                    }
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (ControllerForView.getInstance().getGameState() == GameState.MENU
                        && ControllerForView.getInstance().isMenuTypingName()) {
                    char c = e.getKeyChar();
                    if (Character.isLetterOrDigit(c) || c == '_' || c == ' ') {
                        ControllerForView.getInstance().menuAppendNameChar(c);
                    }
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (ControllerForView.getInstance().getGameState() == GameState.MENU) {
                    handleMenuClick(e.getX(), e.getY());
                    return;
                }

                if (ControllerForView.getInstance().getGameState() == GameState.GAME_OVER) {
                    PauseMenuDrawer.ClickResult result = GameOverDrawer.getInstance().handleClick(e.getX(), e.getY());
                    switch (result) {
                        case QUIT                -> System.exit(0);
                        case RETURN_TO_MAIN_MENU -> ControllerForView.getInstance().resetGame();
                        default -> {}
                    }
                    return;
                }

                if (!ControllerForView.getInstance().isPaused()) return;

                PauseController ctrl = ControllerForView.getInstance().getPauseController();
                PauseMenuDrawer.ClickResult result = PauseMenuDrawer.getInstance().handleClick(
                        e.getX(), e.getY(), ctrl);

                // GamePanel is the orchestrator: it receives the ClickResult and handles all
                // lifecycle actions. PauseController is responsible only for its own internal state.
                switch (result) {
                    case RESUME -> {
                        ctrl.onResumeClicked();
                        ControllerForView.getInstance().setPaused(false);
                    }
                    case QUIT -> {
                        ctrl.onQuitClicked();
                        System.exit(0);
                    }
                    case RETURN_TO_MAIN_MENU -> {
                        ctrl.onReturnToMainMenuClicked();
                    }
                    default -> {}
                }
            }
        });
    }

    private void handleMenuClick(int x, int y) {
        MenuDrawer menuView = MenuDrawer.getInstance();

        if (menuView.getNameFieldRect() != null && menuView.getNameFieldRect().contains(x, y)) {
            ControllerForView.getInstance().menuSetTypingName(true);
            return;
        }
        ControllerForView.getInstance().menuSetTypingName(false);

        int characterIndex = menuView.getCharacterIndexAt(x, y);
        if (characterIndex >= 0) {
            ControllerForView.getInstance().menuHandleClick(characterIndex);
            return;
        }

        if (menuView.isStartGameButtonAt(x, y)) {
            ControllerForView.getInstance().menuConfirmSelection();
        }
    }

    private void setupKeyBindings() {
        InputMap im = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();
        double speed = Config.ENTITY_LOGICAL_SPEED;

        // --- RIGHT ---
        im.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        am.put("moveRight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (ControllerForView.getInstance().getGameState() != GameState.PLAYING || ControllerForView.getInstance().isPaused())
                    return;
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
                if (ControllerForView.getInstance().getGameState() != GameState.PLAYING || ControllerForView.getInstance().isPaused())
                    return;
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
                if (ControllerForView.getInstance().getGameState() != GameState.PLAYING || ControllerForView.getInstance().isPaused())
                    return;
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
                if (ControllerForView.getInstance().getGameState() != GameState.PLAYING || ControllerForView.getInstance().isPaused())
                    return;
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
                if (ControllerForView.getInstance().getGameState() != GameState.PLAYING || ControllerForView.getInstance().isPaused())
                    return;
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
                if (ControllerForView.getInstance().getGameState() != GameState.PLAYING || ControllerForView.getInstance().isPaused())
                    return;
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
                if (ControllerForView.getInstance().getGameState() != GameState.PLAYING || ControllerForView.getInstance().isPaused())
                    return;
                if (!ControllerForView.getInstance().isStaffUsable()) return;
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

    /**
     * Updates the Swing InputMap to reflect a rebind made in the pause menu.
     * Passed as a callback to {@code IControllerForView.setKeyBindingApplier()}
     * so the Controller can invoke it without knowing about this class.
     *
     * @param actionIndex action index 0-6 (matches PauseModel.ACTION_*)
     * @param newKeyName  new key in uppercase KeyStroke format, e.g. "W", "UP", "SPACE"
     */
    private void applyKeyRebind(int actionIndex, String newKeyName) {
        if (actionIndex < 0 || actionIndex >= PRESS_ACTIONS.length) return;

        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        String oldKey = currentInputBindings[actionIndex];

        im.remove(KeyStroke.getKeyStroke(oldKey));
        im.remove(KeyStroke.getKeyStroke("released " + oldKey));

        im.put(KeyStroke.getKeyStroke(newKeyName), PRESS_ACTIONS[actionIndex]);
        im.put(KeyStroke.getKeyStroke("released " + newKeyName), RELEASE_ACTIONS[actionIndex]);

        currentInputBindings[actionIndex] = newKeyName;
    }
}
