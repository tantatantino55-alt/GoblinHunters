package utils;

/**
 * Stati principali del gioco.
 * Utilizzato dal Controller per determinare quale logica eseguire
 * nel game loop (tick) e quale schermata renderizzare (render).
 */
public enum GameState {
    MENU,
    PLAYING,
    GAME_OVER
}
