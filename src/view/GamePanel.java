package view;

import controller.ControllerForView;
import controller.PauseController;
import utils.Config;
import utils.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel {
    private AbstractDrawer drawer = null;

    private boolean canPlaceBomb = true;
    private boolean canShootAura = true;
    private boolean canStaffAttack = true;

    // =========================================================================
    // KEY BINDING RUNTIME STATE
    // =========================================================================

    /**
     * Nomi delle azioni nell'ActionMap per i tasti premuti.
     * Indice coerente con PauseModel.ACTION_MOVE_UP … ACTION_STAFF.
     */
    private static final String[] PRESS_ACTIONS = {
            "moveUp", "moveDown", "moveLeft", "moveRight",
            "placeBomb", "shootAura", "staffAttack"
    };

    /**
     * Nomi delle azioni nell'ActionMap per i tasti rilasciati.
     * Indice coerente con PRESS_ACTIONS.
     */
    private static final String[] RELEASE_ACTIONS = {
            "stopUp", "stopDown", "stopLeft", "stopRight",
            "resetBomb", "resetAura", "resetStaff"
    };

    /**
     * Tracking locale dei keybindings correnti (formato uppercase KeyStroke).
     * Inizializzati con gli stessi valori di default del PauseModel per garantire
     * la coerenza senza creare una dipendenza View → Model.
     * Aggiornati da {@link #applyKeyRebind} ad ogni rebind.
     */
    private final String[] currentInputBindings = {
            "UP", "DOWN", "LEFT", "RIGHT", "SPACE", "X", "Z"
    };

    public GamePanel(AbstractDrawer drawer) {
        this.drawer = drawer;
        // Obbliga il pannello ad avere le dimensioni ESATTE dell'immagine di sfondo
        this.setPreferredSize(new Dimension(1230, 832));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        setupMenuControls();   // Input per il menu di selezione personaggio
        setupKeyBindings();
        setupPauseControls();
        // Registra il callback: permette a PauseController di aggiornare l'InputMap
        // al momento del rebind, senza che il Controller conosca GamePanel.
        ControllerForView.getInstance().setKeyBindingApplier(this::applyKeyRebind);
    }

    // =========================================================================
    // MENU SELEZIONE PERSONAGGIO — Mouse hover/click + Tastiera
    // =========================================================================

    /**
     * Configura i listener per il menu di selezione personaggio.
     * Tutti gli input passano attraverso il Controller (MVC).
     */
    private void setupMenuControls() {
        // HOVER: aggiorna la selezione in base alla posizione del mouse
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (ControllerForView.getInstance().getGameState() != GameState.MENU) return;
                int frameIndex = MenuDrawer.getInstance().getFrameIndexAt(e.getX(), e.getY());
                ControllerForView.getInstance().setMenuHoveredIndex(frameIndex);
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        // Il Double Buffering è garantito da setDoubleBuffered(true) nel costruttore
        super.paintComponent(g);

        // 1. DISEGNO DELLO SFONDO (Cabinet Arcade) - Livello Inferiore
        java.awt.image.BufferedImage cabinetImg = view.SpriteManager.getInstance().getSprite("ARCADE_CABINET", 0);
        if (cabinetImg != null) {
            // Disegna coprendo l'intero ContentPane senza tagliare nulla
            g.drawImage(cabinetImg, 0, 0, 1230, 832, this);
        }

        // 2. DISEGNO AREA DI GIOCO (Griglia e HUD) - Livello Superiore
        // Creiamo una copia del Graphics per non inquinare il set originale
        Graphics2D g2d = (Graphics2D) g.create();

        // --- INCASTONAMENTO NELLO SCHERMO ---
        // Spostiamo il punto di origine (0,0) della griglia di gioco
        // esattamente alle coordinate (57, 46) in modo che cada dentro al monitor del
        // cabinato
        // g2d.translate(57, 46);

        drawer.draw(g2d);

        g2d.dispose();
    }

    // =========================================================================
    // PAUSE — ESC key + mouse routing
    // =========================================================================

    private void setupPauseControls() {

        // ESC: toggling della pausa e cancellazione rebind in corso.
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // In MENU state i tasti non fanno nulla (interazione solo mouse)
                if (ControllerForView.getInstance().getGameState() == GameState.MENU) return;

                // --- PLAYING STATE: pausa e rebind ---
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    boolean nowPaused = !ControllerForView.getInstance().isPaused();
                    ControllerForView.getInstance().setPaused(nowPaused);
                    if (!nowPaused) {
                        // Chiusura menu: annulla qualsiasi rebind in corso
                        ControllerForView.getInstance().getPauseController().cancelAllRebinds();
                    }
                    return;
                }

                // Cattura il tasto premuto durante un rebind (azione principale o WASD)
                if (ControllerForView.getInstance().isPaused()) {
                    PauseController ctrl = ControllerForView.getInstance().getPauseController();
                    if (ctrl.isRebinding()) {
                        String keyName = KeyEvent.getKeyText(e.getKeyCode());
                        PauseMenuDrawer.getInstance().handleKeyForRebind(keyName, ctrl);
                    }
                }
            }
        });

        // Click del mouse: routing basato sullo stato.
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // --- MENU STATE: click su riquadri personaggio o NEW GAME ---
                if (ControllerForView.getInstance().getGameState() == GameState.MENU) {
                    handleMenuClick(e.getX(), e.getY());
                    return;
                }

                // --- PLAYING STATE (pausa): routing tramite PauseMenuDrawer ---
                if (!ControllerForView.getInstance().isPaused())
                    return;

                PauseController ctrl = ControllerForView.getInstance().getPauseController();
                PauseMenuDrawer.ClickResult result = PauseMenuDrawer.getInstance().handleClick(e.getX(), e.getY(),
                        ctrl);

                // GamePanel è l'orchestratore: riceve il ClickResult e gestisce
                // tutte le azioni di lifecycle. PauseController si occupa solo
                // della pulizia del proprio stato interno.
                switch (result) {
                    case RESUME -> {
                        ctrl.onResumeClicked(); // pulizia rebind
                        ControllerForView.getInstance().setPaused(false); // lifecycle
                    }
                    case QUIT -> {
                        ctrl.onQuitClicked(); // pulizia stato interno
                        System.exit(0); // lifecycle
                    }
                    case RETURN_TO_MAIN_MENU -> {
                        ctrl.onReturnToMainMenuClicked(); // log TODO
                        // TODO: navigare verso la scena del Main Menu
                    }
                    // RESET_DEFAULTS, TOGGLE_AUDIO, REBIND_START, NONE
                    // → già gestiti internamente da PauseMenuDrawer + PauseController
                    default -> {
                        /* nessuna azione aggiuntiva necessaria */ }
                }
            }
        });
    }

    /**
     * Gestisce i click nella schermata del menu di selezione.
     * View (hit-testing) + Controller (azioni) = MVC.
     */
    private void handleMenuClick(int x, int y) {
        // 1. Click su un riquadro personaggio?
        int frameIndex = MenuDrawer.getInstance().getFrameIndexAt(x, y);
        if (frameIndex >= 0) {
            ControllerForView.getInstance().menuHandleClick(frameIndex);
            return;
        }

        // 2. Click sul pulsante "NEW GAME"?
        if (MenuDrawer.getInstance().isNewGameButtonAt(x, y)) {
            ControllerForView.getInstance().menuConfirmSelection();
        }
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
    // =========================================================================
    // KEY BINDING — RUNTIME REBIND
    // =========================================================================

    /**
     * Aggiorna l'InputMap di Swing per riflettere un rebind effettuato nel menu di
     * pausa.
     * <p>
     * Questo metodo è passato come callback a
     * {@code IControllerForView.setKeyBindingApplier()}
     * nel costruttore: il Controller lo chiama senza conoscere questa classe.
     * </p>
     *
     * <h3>Flusso</h3>
     * <ol>
     * <li>Rimuove il vecchio KeyStroke (press + released) dall'InputMap</li>
     * <li>Aggiunge il nuovo KeyStroke → stessa ActionName (l'ActionMap resta
     * invariata)</li>
     * <li>Aggiorna {@code currentInputBindings} per tenere traccia del binding
     * corrente</li>
     * </ol>
     *
     * @param actionIndex indice azione 0-6 (coerente con PauseModel.ACTION_*)
     * @param newKeyName  nuovo tasto in formato KeyStroke uppercase, es. "W", "UP",
     *                    "SPACE"
     */
    private void applyKeyRebind(int actionIndex, String newKeyName) {
        if (actionIndex < 0 || actionIndex >= PRESS_ACTIONS.length)
            return;

        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        String oldKey = currentInputBindings[actionIndex];

        // 1. Rimuovi il vecchio binding (press + released)
        im.remove(KeyStroke.getKeyStroke(oldKey));
        im.remove(KeyStroke.getKeyStroke("released " + oldKey));

        // 2. Aggiungi il nuovo binding (stessa ActionName → stessa logica)
        im.put(KeyStroke.getKeyStroke(newKeyName), PRESS_ACTIONS[actionIndex]);
        im.put(KeyStroke.getKeyStroke("released " + newKeyName), RELEASE_ACTIONS[actionIndex]);

        // 3. Aggiorna il tracking locale
        currentInputBindings[actionIndex] = newKeyName;
    }
}