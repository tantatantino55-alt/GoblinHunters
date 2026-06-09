package goblinhunters.view;

import javax.swing.*;

public class View implements IView {

    private static View instance = null;

    protected GameGUI gameGUI = null;

    private View() {}

    @Override
    public void openGameGUI() {
        final AbstractDrawer drawer = new ConcreteDrawer();
        SwingUtilities.invokeLater(() -> {
            if (gameGUI == null)
                gameGUI = new GameGUI(drawer);
            gameGUI.setVisible(true);
        });
    }

    @Override
    public void closeGameGUI() {
        SwingUtilities.invokeLater(() -> {
            if (gameGUI != null)
                gameGUI.setVisible(false);
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
        ResourceLoader loader = new ResourceLoader();
        loader.loadAllResources();
    }
}
