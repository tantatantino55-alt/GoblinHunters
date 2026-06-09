package goblinhunters.controller;

import goblinhunters.model.Model;
import goblinhunters.utils.Config;
import goblinhunters.utils.GameState;

public class ControllerForModel implements IControllerForModel, Runnable {

    // ==========================================================
    // static fields
    // ==========================================================

    private static ControllerForModel instance = null;

    // ==========================================================
    // instance state
    // ==========================================================

    private Thread gameThread;
    private boolean running = false;
    private volatile boolean paused = false;
    private int transitionTimer = 0;
    private final int MAX_TRANSITION_TICKS = Config.MAX_TRANSITION_TICKS;

    /** Current game state (MENU, PLAYING, GAME_OVER). */
    private volatile GameState gameState = GameState.MENU;

    @Override public boolean   isPaused()                   { return paused; }
    @Override public void      setPaused(boolean paused)    { this.paused = paused; }
    @Override public GameState getGameState()               { return gameState; }
    @Override public void      setGameState(GameState state){ this.gameState = state; }

    @Override
    public void resetGame() {
        this.paused = false;
        this.gameState = GameState.MENU;
        goblinhunters.model.Model.resetInstance();
    }

    private ControllerForModel() {}

    @Override
    public void updateGame() {
        if (Model.getInstance().isTransitioning()) {
            this.transitionTimer--;

            // at the midpoint (screen fully black) load the next map
            if (this.transitionTimer == MAX_TRANSITION_TICKS / 2) {
                int[][] nextMap = Model.getInstance().generateProceduralMap();
                Model.getInstance().prepareNextLevel(nextMap);
            }

            if (this.transitionTimer <= 0) {
                Model.getInstance().setTransitioning(false);
            }

            return;
        }

        Model.getInstance().updateGameLogic();

        if (Model.getInstance().isGameOverPending()) {
            Model.getInstance().clearGameOverPending();
            setGameState(GameState.GAME_OVER);
        }

        // when the model signals level complete, start the transition animation
        // rather than immediately generating the next map
        if (Model.getInstance().isLevelCompletedFlag()) {
            onLevelCompleted();
        }
    }

    public void onLevelCompleted() {
        if (!Model.getInstance().isTransitioning()) {
            Model.getInstance().setTransitioning(true);
            this.transitionTimer = MAX_TRANSITION_TICKS;
        }
    }

    @Override
    public void startGameLoop() {
        if (!running) {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000.0 / Config.FPS;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            // anti-lag cap: prevents unnatural acceleration after OS-level stalls
            if (delta > 3) {
                delta = 1;
            }

            boolean updated = false;

            while (delta >= 1) {
                if (gameState == GameState.PLAYING && !paused) {
                    updateGame();
                }
                delta--;
                updated = true;
            }

            if (updated) {
                ControllerForView.getInstance().requestRepaint();
            }

            // busy-wait with Thread.yield() instead of sleep() for frame-perfect timing;
            // avoids the ±1 ms jitter that sleep introduces on most JVMs
            while (System.nanoTime() - lastTime < nsPerTick) {
                Thread.yield();
            }
        }
    }

    // ==========================================================
    // static methods
    // ==========================================================

    public static IControllerForModel getInstance() {
        if (instance == null)
            instance = new ControllerForModel();
        return instance;
    }
}
