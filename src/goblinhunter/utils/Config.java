package goblinhunter.utils;

public class Config {
    // ====================================================================
    // 1. DIMENSIONI GRIGLIA (LOGICHE)
    // ====================================================================
    public static final int GRID_WIDTH = 13;    // Colonne (X)
    public static final int GRID_HEIGHT = 11;   // Righe (Y)

    // ====================================================================
    // 2. DIMENSIONI RENDERIZZAZIONE (FISICHE)
    // ====================================================================
    // Dimensione di una singola cella/tile in pixel.
    public static final int TILE_SIZE = 64;

    // Dimensioni calcolate del GamePanel (Griglia 13x11)
    public static final int GAME_PANEL_WIDTH = GRID_WIDTH * TILE_SIZE;  // 13 * 48 = 624 px
    public static final int GAME_PANEL_HEIGHT = GRID_HEIGHT * TILE_SIZE; // 11 * 48 = 528 px

    // ====================================================================
    // 3. DIMENSIONI FINESTRA (JFrame)
    // ====================================================================
    // Usate in GameGUI.java
    public static final int WINDOW_PREFERRED_WIDTH = 960;  // Precedentemente in GameGUI
    public static final int WINDOW_PREFERRED_HEIGHT = 800; // Precedentemente in GameGUI

    // ====================================================================
    // 4. GAME LOOP
    // ====================================================================
    // Frequenza di aggiornamento del gioco (Frame per secondo)
    public static final int FPS = 60;
    // Tempo di ritardo tra un aggiornamento e l'altro (in millisecondi)
    public static final int GAME_LOOP_DELAY_MS = 1000 / FPS;


    // ====================================================================
    // 5. PARAMETRI DI GIOCO (Model Logic)
    // ====================================================================
    // Player (Mago)
    public static final int PLAYER_SPEED = 4;
    //public static final int x = 168;
    //public static final int y = 136;
    public static final int PLAYER_INITIAL_LIVES = 5;
    public static final int SPELL_RANGE = 3;
    public static final int SPELL_RANGE_POWERUP = 6;
    public static final int POWERUP_DURATION_SECONDS = 10;

    // Nemici
    public static final int MAX_ENEMIES = 6; // Max nemici contemporaneamente
    // Goblin Boss (potresti aggiungere qui i parametri specifici)

    // Risorse e Power-up
    public static final int MAX_POWERUPS = 3;

    /* Percorsi delle risorse (sprite, sfondo....)
       ... (Aggiungerai qui i percorsi ai tuoi file)
    */


    // ====================================================================
    // 6. CELL TYPE
    // ====================================================================
    public static final int CELL_EMPTY = 0;             // Spazio vuoto, camminabile
    public static final int CELL_INDESTRUCTIBLE_BLOCK = 1; // blocco indistruttibile
    public static final int CELL_DESTRUCTIBLE_BLOCK = 2;   // Blocco distruttibile
    public static final int CELL_PLAYER_START = 3;      // Posizione iniziale del giocatore
    public static final int CELL_ENEMY_START = 4;       // Posizione iniziale dei nemici


    // ====================================================================
    // 7. COORDINATE
    // ====================================================================
    public static final int GRID_OFFSET_X = 0;
    public static final int GRID_OFFSET_Y = 0;

    public static final int MIN_X = GRID_OFFSET_X;
    public static final int MAX_X = GRID_OFFSET_X + GAME_PANEL_WIDTH - TILE_SIZE;
    public static final int MIN_Y = GRID_OFFSET_Y;
    public static final int MAX_Y = GRID_OFFSET_Y + GAME_PANEL_HEIGHT - TILE_SIZE;


    // ====================================================================
    // 8. SPRITESHEETS 
    // ====================================================================

    // --- TILES ---
    public static final String TILE_FLOOR = "/floor.png";
    public static final String TILE_WALL_INDESTRUCTIBLE = "/sand.png";
    //public static final String TILE_WALL_DESTRUCTIBLE = "/images/wall_destructible.png";

    //stringhe con il path delle sprite sheet


}