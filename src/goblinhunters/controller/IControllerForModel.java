package goblinhunters.controller;

import goblinhunters.utils.GameState;

public interface IControllerForModel {
    void updateGame();
    void startGameLoop();

    /** Returns true when the game logic is frozen (pause screen active). */
    boolean isPaused();
    /** Freeze or unfreeze game logic. */
    void setPaused(boolean paused);

    /** Returns the current game state (MENU, PLAYING, GAME_OVER). */
    GameState getGameState();
    /** Sets the game state. */
    void setGameState(GameState state);

    /** Resets the game loop back to the main menu. */
    void resetGame();
}
