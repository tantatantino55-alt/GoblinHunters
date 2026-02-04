package utils;

public class Config {
    // Dimensioni logiche (Celle)
    public static final int GRID_WIDTH = 13;
    public static final int GRID_HEIGHT = 11;

    // Dimensioni fisiche (Pixel)
    public static final int TILE_SIZE = 64;
    public static final int GAME_PANEL_WIDTH = GRID_WIDTH * TILE_SIZE;
    public static final int GAME_PANEL_HEIGHT = GRID_HEIGHT * TILE_SIZE;
    public static final int WINDOW_PREFERRED_WIDTH = 1152;
    public static final int WINDOW_PREFERRED_HEIGHT = 896;

    // Game Loop
    public static final int FPS = 60;
    public static final int GAME_LOOP_DELAY_MS = 1000 / FPS;



    // --- LOGICA PLAYER (Unità Mondo) ---
    // 0.05 unità = circa 3 pixel. È la velocità logica pura.
    public static final double PLAYER_LOGICAL_SPEED = 0.05;

    /*
    // Per il test a 1 cifra decimale, usa 0.1
    public static final double PLAYER_LOGICAL_SPEED = 0.1;
*/


    // Hitbox (manteniamo i pixel per il calcolo del rapporto nel Model)
    public static final int PLAYER_HITBOX_WIDTH = 32;
    public static final int PLAYER_HITBOX_HEIGHT = 16;
    public static final int PLAYER_PIVOT_Y = 102;

    // Offset Grafici (Solo per la View)
    public static final int GRID_OFFSET_X = 160;
    public static final int GRID_OFFSET_Y = 46;

    // Costanti Celle
    public static final int CELL_EMPTY = 0;
    public static final int CELL_INDESTRUCTIBLE_BLOCK = 1;
    public static final int CELL_DESTRUCTIBLE_BLOCK = 2;

    // Risorse
    public static final String PLAYER1_SHEET ="/wizardmale.png";
    public static final int PLAYER_FRAME_SIZE = 128;
    public static final String TILE_FLOOR = "/colonnasabbia.png";
    public static final String TILE_WALL_INDESTRUCTIBLE = "/murosabbia.png";
    public static final String TILE_WALL_DESTRUCTIBLE = "/castellosabbia.png";
}