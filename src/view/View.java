package view;

import utils.Config;
import utils.PlayerState;

import javax.swing.*;

public class View implements IView{

    //---------------------------------------------------------------
    // STATIC FIELDS
    //---------------------------------------------------------------
    private static View instance = null;

    //---------------------------------------------------------------
    // INSTANCE ATTRIBUTES
    //---------------------------------------------------------------
    protected GameGUI gameGUI = null;

    private View() {
    //TO-DO
    }
    public void openGameGUI() {

        final AbstractDrawer drawer = new ConcreteDrawer();

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (gameGUI == null)
                    gameGUI = new GameGUI(drawer);
                gameGUI.setVisible(true);
            }
        });
    }

    public void closeGameGUI() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (gameGUI != null)
                    gameGUI.setVisible(false);
            }
        });
    }


    @Override
    public void requestRepaint() {
        if (gameGUI != null) {
            SwingUtilities.invokeLater(() -> gameGUI.getGamePanel().repaint());
        }
    }

    public static IView getInstance() {
        if (instance == null)
            instance = new View();
        return instance;
    }

    @Override
    public void setupResources() {
        SpriteManager sm = SpriteManager.getInstance();

        // Assicurati che questi parametri siano corretti nel tuo Config
        String sheet = Config.PLAYER1_SHEET;
        int size = Config.PLAYER_FRAME_SIZE;

        // --- RETRO (Su) ---
        sm.loadAnimation(PlayerState.ATTACK_BACK, sheet, 0, 10, size);
        sm.loadAnimation(PlayerState.HURT_BACK,   sheet, 10, 10, size);
        sm.loadAnimation(PlayerState.IDLE_BACK,   sheet, 20, 16, size);
        sm.loadAnimation(PlayerState.RUN_BACK,    sheet, 36, 12, size);

        // --- MORTE (Speciale) ---
        sm.loadAnimation(PlayerState.DYING,       sheet, 48, 10, size);

        // --- FRONTE (Giù) ---
        sm.loadAnimation(PlayerState.ATTACK_FRONT, sheet, 58, 10, size);
        sm.loadAnimation(PlayerState.HURT_FRONT,   sheet, 68, 10, size);
        sm.loadAnimation(PlayerState.IDLE_FRONT,   sheet, 78, 16, size);
        sm.loadAnimation(PlayerState.RUN_FRONT,    sheet, 94, 12, size);

        // --- SINISTRA ---
        sm.loadAnimation(PlayerState.ATTACK_LEFT,  sheet, 106, 10, size);
        sm.loadAnimation(PlayerState.HURT_LEFT,    sheet, 116, 10, size);
        sm.loadAnimation(PlayerState.IDLE_LEFT,    sheet, 126, 16, size);
        sm.loadAnimation(PlayerState.RUN_LEFT,     sheet, 142, 12, size);

        // --- DESTRA ---
        sm.loadAnimation(PlayerState.ATTACK_RIGHT, sheet, 154, 10, size);
        sm.loadAnimation(PlayerState.HURT_RIGHT,   sheet, 164, 10, size);
        sm.loadAnimation(PlayerState.IDLE_RIGHT,   sheet, 174, 16, size);
        sm.loadAnimation(PlayerState.RUN_RIGHT,    sheet, 190, 12, size);

        System.out.println("Risorse Player caricate correttamente.");
    }

} // end class
