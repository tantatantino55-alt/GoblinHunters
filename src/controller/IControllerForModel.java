package controller;

import utils.GameState;

public interface IControllerForModel {
    public void updateGame();
    public void startGameLoop();

    // --- PAUSE ---
    /** Returns true when the game logic is frozen (pause screen active). */
    boolean isPaused();
    /** Freeze or unfreeze game logic. */
    void setPaused(boolean paused);

    // --- GAME STATE ---
    /** Ritorna lo stato corrente del gioco (MENU o PLAYING). */
    GameState getGameState();
    /** Imposta lo stato del gioco. */
    void setGameState(GameState state);
}