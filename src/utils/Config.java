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
    public static final double ENTITY_LOGICAL_SPEED = 0.05;

    // Hitbox espressa in Unità Mondo (es. 0.5 unità = 32 pixel se tile è 64)
    public static final double ENTITY_LOGICAL_HITBOX_WIDTH = 0.5;
    public static final double ENTITY_LOGICAL_HITBOX_HEIGHT = 0.3;



    // --- LOGICA NEMICI (GOBLIN) ---
    public static final double GOBLIN_COMMON_SPEED = 0.03;    // Velocità del Goblin base
    // HITBOX GOBLIN (Leggermente più piccoli della cella per non incastrarsi)
    public static final double GOBLIN_HITBOX_WIDTH = 0.8; // BOSS
    public static final double GOBLIN_HITBOX_HEIGHT = 0.8; //BOSS
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

    // --- CONFIGURAZIONE ANIMAZIONE PLAYER ---
    public static final int PLAYER_RUN_FRAMES = 12;   // Numero frame corsa
    public static final int PLAYER_IDLE_FRAMES = 16;  // Numero frame riposo
    public static final int PLAYER_ATTACK_FRAMES = 10;

    // --- RISORSE ---
    public static final String PLAYER1_SHEET = "/wizardmale.png";
    public static final int ENTITY_FRAME_SIZE = 128;
    public static final String MAIN_SHEET = "/MapItems.png"; // Il tuo file unico
    public static final String GOBLIN_SHEET = "/goblins.png";

    // Numero di frame per l'animazione di corsa (dall'immagine vedo da 000 a 011 -> 12 frame)
    public static final int GOBLIN_RUN_FRAMES = 12;
    public static final int GOBLIN_IDLE_FRAMES = 1; // Per ora usiamo 1 frame se stanno fermi (o usiamo il primo della corsa)
    // Quanti frame dura ogni azione
    public static final int SHOOTER_ATTACK_FRAMES = 2;


    // --- 1. COMMON GOBLIN (Righe 0-3) ---
    public static final int COMMON_BACK_RUNNING    = 0;   // Riga 0
    public static final int COMMON_FRONT_RUNNING  = 12;  // Riga 1
    public static final int COMMON_LEFT_RUNNING= 24;  // Riga 2
    public static final int COMMON_RIGHT_RUNNING = 36;  // Riga 3

    // --- 2. SHOOTER GOBLIN: MOVIMENTO (Righe 4-7) --
    public static final int SHOOTER_BACK_ATTACKING = 48;
    public static final int SHOOTER_FRONT_ATTACKING = 78;
    public static final int SHOOTER_BACK_IDLE    = 50; // Riga 4
    public static final int SHOOTER_BACK_RUNNING  = 66; // Riga 5
    public static final int SHOOTER_RUN_START_LEFT  = 72; // Riga 6
    public static final int SHOOTER_RUN_START_RIGHT = 84; // Riga 7

    // --- 3. SHOOTER GOBLIN: ATTACCO (Righe 8-11) ---
    public static final int SHOOTER_ATTACK_START_UP    = 96;  // Riga 8
    public static final int SHOOTER_ATTACK_START_DOWN  = 108; // Riga 9
    public static final int SHOOTER_ATTACK_START_LEFT  = 120; // Riga 10
    public static final int SHOOTER_ATTACK_START_RIGHT = 132; // Riga 11

    // --- 4. CHASER (HUNTER) GOBLIN (Righe 12-15) ---
    public static final int HUNTER_START_UP    = 144; // Riga 12
    public static final int HUNTER_START_DOWN  = 156; // Riga 13
    public static final int HUNTER_START_LEFT  = 168; // Riga 14
    public static final int HUNTER_START_RIGHT = 180; // Riga 15

    
    public static final int CELL_EMPTY = 0;
    public static final int CELL_INDESTRUCTIBLE_BLOCK = 1;
    public static final int CELL_DESTRUCTIBLE_BLOCK = 2;
    public static final String ITEM_SHEET = "/Items.png";
    public static final int BOMB_SPRITE_START = 0;
    public static final int BOMB_FRAMES = 8;
    public static final int BOMB_ANIM_FRAME_DURATION = 100;
    // --- FUOCO ESPLOSIONE (Consumabili.png - Riga 1) ---
    public static final int DESTRUCTION_START = 3;
    public static final int DESTRUCTION_FRAMES = 3;    // Caricherà le colonne 3, 4, 5

    // Durata visiva: 150ms per frame
    public static final int DESTRUCTION_FRAME_DURATION = 150;


    // --- MAPPA & DISTRUZIONE (MapItems.png) ---
    // Celle Statiche






    // --- LOGICA BOMBE ---
    public static final int BOMB_DETONATION_TICKS = 180; // 3 secondi a 60 FPS
    public static final int INITIAL_MAX_BOMBS = 1;       // Numero di bombe iniziali
    public static final int DEFAULT_BOMB_RADIUS = 1;    // Raggio iniziale (1 cella in croce)
    public static final int ANIMATION_DELAY = 50;


    // --- AI & PERCEZIONE (ChasingGoblin) ---
    public static final int SMELL_THRESHOLD_DISTANCE = 6; // Raggio olfatto
    public static final int SMELL_BLOCK_PENALTY = 3;      // Quanto i muri bloccano l'odore
    public static final int SAFE_DISTANCE_FROM_BOMB = 2;  // Raggio fuga bombe
    public static final int MIN_SPAWN_DISTANCE = 5;       // Spawn sicuro

    // --- SHOOTER GOBLIN (Estende Chasing) ---
    public static final double SHOOTER_SPEED_AIMING = 0.0; // Fermo quando mira
    public static final double SHOOTER_SPEED_CHASE = 0.06; // Veloce quando insegue
    public static final int SHOOTER_MAX_AMMO = 2;
    public static final int SHOOTER_RELOAD_TIME = 180;     // 3 secondi
    public static final int SHOOTER_TELEGRAPH_TIME = 30;   // 0.5 secondi pre-sparo

    // Villaggio in rovina
    // Coordinate (Colonna, Riga) nello sheet
    public static final int TILE_FLOOR_COL = 1;
    public static final int TILE_FLOOR_ROW = 0;
    public static final int TILE_WALL_IND_COL = 0;
    public static final int TILE_WALL_IND_ROW =0;
    public static final int TILE_WALL_DEST_COL = 2;
    public static final int TILE_WALL_DEST_ROW = 0;

    // In src/utils/Config.java

// ... (altre costanti)

    // --- SLIDING / CORNER CORRECTION ---


    // Velocità con cui il player viene riallineato (di solito uguale alla velocità di movimento)
    public static final double CORNER_ALIGN_SPEED = ENTITY_LOGICAL_SPEED * 1.5;
    // --- LOGICA ESPLOSIONE E FUOCO (Model) ---


    // FUOCO
    // Quanto dura il fuoco nel mondo di gioco? (30 tick = 0.5 secondi a 60 FPS)
    public static final int FIRE_DURATION_TICKS = 30;

    // --- ANIMAZIONE FUOCO (View) ---

}

