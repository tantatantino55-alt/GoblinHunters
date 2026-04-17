package controller;

public interface IControllerForModel {
    public void updateGame();
    public void startGameLoop();

    // --- PAUSE ---
    /** Returns true when the game logic is frozen (pause screen active). */
    boolean isPaused();
    /** Freeze or unfreeze game logic. */
    void setPaused(boolean paused);
}