package goblinhunter.view;

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

    public static IView getInstance() {
        if (instance == null)
            instance = new View();
        return instance;
    }

} // end class
