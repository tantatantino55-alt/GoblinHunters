package view;

import utils.Config;

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
        sm.loadAnimation("PLAYER_BACK_ATTACKING", Config.PLAYER1_SHEET, 0, 10, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_BACK_HURT", Config.PLAYER1_SHEET, 10, 10, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_BACK_IDLE", Config.PLAYER1_SHEET, 20, 16, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_BACK_RUNNING", Config.PLAYER1_SHEET, 36, 12, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_DYING", Config.PLAYER1_SHEET, 48, 10, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_FRONT_ATTACKING", Config.PLAYER1_SHEET, 58, 10, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_FRONT_HURT", Config.PLAYER1_SHEET, 68, 10, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_FRONT_IDLE", Config.PLAYER1_SHEET, 78, 16, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_FRONT_RUNNING", Config.PLAYER1_SHEET, 94, 12, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_LEFT_ATTACKING", Config.PLAYER1_SHEET, 106, 10, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_LEFT_HURT", Config.PLAYER1_SHEET, 116, 10, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_LEFT_IDLE", Config.PLAYER1_SHEET, 126, 16, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_LEFT_RUNNING", Config.PLAYER1_SHEET, 142, 12, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_RIGHT_ATTACKING", Config.PLAYER1_SHEET, 154, 10, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_RIGHT_HURT", Config.PLAYER1_SHEET, 164, 10, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_RIGHT_IDLE", Config.PLAYER1_SHEET, 174, 16, Config.PLAYER_FRAME_SIZE);
        sm.loadAnimation("PLAYER_RIGHT_RUNNING", Config.PLAYER1_SHEET, 190, 12, Config.PLAYER_FRAME_SIZE);
    }

} // end class
