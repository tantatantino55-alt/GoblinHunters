package utils;

public class Config {
    // --- DIMENSIONI GRIGLIA (LOGICHE) ---
    public static final int GRID_WIDTH = 13;
    public static final int GRID_HEIGHT = 11;

    // --- DIMENSIONI RENDERIZZAZIONE (FISICHE - Usate solo dalla View) ---
    public static final int TILE_SIZE = 64;
    public static final int GAME_PANEL_WIDTH = GRID_WIDTH * TILE_SIZE;
    public static final int GAME_PANEL_HEIGHT = GRID_HEIGHT * TILE_SIZE;
    public static final int WINDOW_PREFERRED_WIDTH = 1152;
    public static final int WINDOW_PREFERRED_HEIGHT = 896;

    // --- LOGICA PLAYER (Unità Mondo - PURE MVC) ---
    public static final double PLAYER_LOGICAL_SPEED = 0.05;

    // Hitbox espressa in Unità Mondo (es. 0.5 unità = 32 pixel se tile è 64)
    public static final double PLAYER_LOGICAL_HITBOX_WIDTH = 0.5;
    public static final double PLAYER_LOGICAL_HITBOX_HEIGHT = 0.25;

// --- LOGICA NEMICI (GOBLIN) ---
    public static final double GOBLIN_COMMON_SPEED = 0.03;    // Velocità del Goblin base
    public static final double GOBLIN_HITBOX_WIDTH = 0.5;     // Larghezza logica (metà cella)
    public static final double GOBLIN_HITBOX_HEIGHT = 0.5;
    // Altezza logica
    // --- PARAMETRI DI SPAWN ---
    public static final int MAX_ENEMIES_ON_MAP = 6;           // Requisito: Max 4-6
    public static final long SPAWN_INTERVAL_MS = 3000;        // Ogni 3 secondi
    public static final int SPAWN_SAFE_DISTANCE = 2;


    // Confini logici del mondo (da 0.0 a 13.0/11.0)
    public static final double MIN_LOGICAL_X = 0.0;
    public static final double MAX_LOGICAL_X = (double) GRID_WIDTH - 1.0;
    public static final double MIN_LOGICAL_Y = 0.0;
    public static final double MAX_LOGICAL_Y = (double) GRID_HEIGHT - 1.0;

    // --- PARAMETRI RENDER (Solo View) ---
    public static final int GRID_OFFSET_X = 160;
    public static final int GRID_OFFSET_Y = 46;
    public static final int PLAYER_PIVOT_Y = 102;
    public static final int FPS = 60;
    public static final int GAME_LOOP_DELAY_MS = 1000 / FPS;

    // --- RISORSE ---
    public static final String PLAYER1_SHEET ="/wizardmale.png";
    public static final int PLAYER_FRAME_SIZE = 128;
    public static final String TILE_FLOOR = "/colonnasabbia.png";
    public static final String TILE_WALL_INDESTRUCTIBLE = "/murosabbia.png";
    public static final String TILE_WALL_DESTRUCTIBLE = "/castellosabbia.png";
    public static final int CELL_EMPTY = 0;
    public static final int CELL_INDESTRUCTIBLE_BLOCK = 1;
    public static final int CELL_DESTRUCTIBLE_BLOCK = 2;
    // --- RISORSE BOMBA (Placeholder) ---
    public static final String BOMB_SHEET = "/bomb.png"; // Nome file da decidere
    public static final int BOMB_FRAME_SIZE = 64;       // Dimensione sprite (es. 64x64)
    public static final int BOMB_ANIMATION_FRAMES = 1;  // Numero di frame iniziali

    // --- LOGICA BOMBE ---
    public static final int BOMB_DETONATION_TICKS = 180; // 3 secondi a 60 FPS
    public static final int INITIAL_MAX_BOMBS = 1;       // Numero di bombe iniziali
    public static final int DEFAULT_BOMB_RADIUS = 1;    // Raggio iniziale (1 cella in croce)
}