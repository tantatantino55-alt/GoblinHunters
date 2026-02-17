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

    // --- NUOVE COSTANTI DA AGGIUNGERE ---
    public static final int SMELL_THRESHOLD_DISTANCE = 6; // Distanza massima inseguimento
    public static final int SHOOTER_TELEGRAPH_TIME = 60;  // Tempo di mira (tick)
    public static final double MIN_SPAWN_DISTANCE = 5.0;

// src/utils/Config.java

    // --- LANE CENTERING / CORNER CORRECTION ---
// Quanto vicino al centro (0.0) deve essere l'asse opposto per permettere il movimento (es. 0.05 = 5% di pixel)

    // Entro quale distanza l'entità viene "attratta" dal centro della corsia (0.45 = quasi mezza cella)

    // Velocità di correzione automatica (spesso uguale o leggermente superiore alla velocità di movimento)
    public static final double CORNER_CORRECTION_SPEED = 0.05;

    // --- LOGICA NEMICI (GOBLIN) ---
    // HITBOX GOBLIN (Leggermente più piccoli della cella per non incastrarsi)
    public static final double GOBLIN_HITBOX_WIDTH = 0.8; // BOSS
    public static final double GOBLIN_HITBOX_HEIGHT = 0.8; //BOSS
    // Altezza logica
    // --- PARAMETRI DI SPAWN ---
    public static final int MAX_ENEMIES_ON_MAP = 1;           // Requisito: Max 4-6
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
    public static final String SHOOTERGOBLIN_SHEET = "/ShooterGoblin.png";
    public static final String CHASING_GOBLIN_SHEET = "/ChasingGoblin.png";
    public static final String COMMON_GOBLIN = "/CommonGoblin.png";



    // --- 1. COMMON GOBLIN (File: common.png) ---
// Parte da 0
    public static final int COMMON_RUN_BACK_START   = 0;
    public static final int COMMON_RUN_FRONT_START  = 12;
    public static final int COMMON_RUN_LEFT_START   = 24;
    public static final int COMMON_RUN_RIGHT_START  = 36;
// Totale usato: 48 frame. Fine del file.

    // --- 2. CHASING GOBLIN (File: chasing.png) ---
// ATTENZIONE: Parte da 0, NON da 48! È un file nuovo!
    public static final int CHASING_IDLE_BACK_START  = 0;  // Era 48 -> Diventa 0
    public static final int CHASING_RUN_BACK_START   = 16; // 0 + 16

    public static final int CHASING_IDLE_FRONT_START = 28; // 16 + 12
    public static final int CHASING_RUN_FRONT_START  = 44;

    public static final int CHASING_IDLE_LEFT_START  = 56;
    public static final int CHASING_RUN_LEFT_START   = 72;

    public static final int CHASING_IDLE_RIGHT_START = 84;
    public static final int CHASING_RUN_RIGHT_START  = 100;

    // --- 3. SHOOTER GOBLIN (File: shooter.png) ---
// ATTENZIONE: Parte da 0, NON da 160!
// Struttura riga: 2 (Attack) + 16 (Idle) + 12 (Run) = 30 frame.
    public static final int SHOOTER_ROW_WIDTH = 30; // Fondamentale per il calcolo riga
    // 1. NUMERO DI FRAME PER AZIONE (Le variabili richieste)
    public static final int SHOOTER_ATTACK_FRAMES = 2;
    public static final int GOBLIN_IDLE_FRAMES   = 16;
    public static final int GOBLIN_RUN_FRAMES    = 12;
    // Riga 0 (Back) -> Indice 0
    public static final int SHOOTER_ATTACK_BACK_START = 0;
    public static final int SHOOTER_IDLE_BACK_START   = 2;
    public static final int SHOOTER_RUN_BACK_START    = 18;

    // Riga 1 (Front) -> Indice 30 (inizio riga successiva)
    public static final int SHOOTER_ATTACK_FRONT_START = 30;
    public static final int SHOOTER_IDLE_FRONT_START   = 32;
    public static final int SHOOTER_RUN_FRONT_START    = 48;

    // Riga 2 (Left) -> Indice 60
    public static final int SHOOTER_ATTACK_LEFT_START  = 60;
    public static final int SHOOTER_IDLE_LEFT_START    = 62;
    public static final int SHOOTER_RUN_LEFT_START     = 78;

    // Riga 3 (Right) -> Indice 90
    public static final int SHOOTER_ATTACK_RIGHT_START = 90;
    public static final int SHOOTER_IDLE_RIGHT_START   = 92;
    public static final int SHOOTER_RUN_RIGHT_START    = 108;

    
    public static final int CELL_EMPTY = 0;
    public static final int CELL_INDESTRUCTIBLE_BLOCK = 1;
    public static final int CELL_DESTRUCTIBLE_BLOCK = 2;
    public static final String ITEM_SHEET = "/Items.png";
    public static final int BOMB_SPRITE_START = 0;
    public static final int BOMB_FRAMES = 8;
    public static final int BOMB_ANIM_FRAME_DURATION = 100;



    // --- PROIETTILI NEMICI (Items.png - Riga 0) ---
    public static final int BONE_DOWN_INDEX  = 8;
    public static final int BONE_LEFT_INDEX  = 9;
    public static final int BONE_RIGHT_INDEX = 10;
    public static final int BONE_UP_INDEX    = 11;

    // --- PROIETTILI PLAYER (Items.png - Riga 0) ---
    // Subito dopo le ossa
    public static final int AURA_DOWN_INDEX  = 12;
    public static final int AURA_LEFT_INDEX  = 13;
    public static final int AURA_RIGHT_INDEX = 14;
    public static final int AURA_UP_INDEX    = 15;



    // --- FUOCO ESPLOSIONE (Consumabili.png - Riga 1) ---
    public static final int DESTRUCTION_START = 3;
    public static final int DESTRUCTION_FRAMES = 3;    // Caricherà le colonne 3, 4, 5

    // Durata visiva: 150ms per frame
    public static final int DESTRUCTION_FRAME_DURATION = 150;


    // --- MAPPA & DISTRUZIONE (MapItems.png) ---
    // Celle Statiche






    // --- LOGICA VITE E DANNI ---
    public static final int INITIAL_LIVES = 99;           // Iniziamo con molte vite come richiesto
    public static final long INVINCIBILITY_DURATION_MS = 3000; // 3 secondi di "invisibilità"
    public static final int FLICKER_DELAY_MS = 100;       // Velocità del lampeggio visivo

    // --- LOGICA BOMBE ---
    public static final int BOMB_DETONATION_TICKS = 60; // 3 secondi a 60 FPS
    public static final int INITIAL_MAX_BOMBS = 1;       // Numero di bombe iniziali
    public static final int DEFAULT_BOMB_RADIUS = 1;    // Raggio iniziale (1 cella in croce)
    public static final int ANIMATION_DELAY = 50;


    // --- AI & PERCEZIONE (ChasingGoblin) ---
    public static final int SMELL_BLOCK_PENALTY = 3;      // Quanto i muri bloccano l'odore
    public static final int SAFE_DISTANCE_FROM_BOMB = 2;  // Raggio fuga bombe

    // --- SHOOTER GOBLIN (Estende Chasing) ---
    public static final double SHOOTER_SPEED_AIMING = 0.0; // Fermo quando mira
    public static final double SHOOTER_SPEED_CHASE = 0.03; // Veloce quando insegue
    public static final int SHOOTER_MAX_AMMO = 2;
    public static final int SHOOTER_RELOAD_TIME = 180;     // 3 secondi

    // Villaggio in rovina
    // Coordinate (Colonna, Riga) nello sheet
    public static final int TILE_FLOOR_COL = 1;
    public static final int TILE_FLOOR_ROW = 0;
    public static final int TILE_WALL_IND_COL = 0;
    public static final int TILE_WALL_IND_ROW =0;
    public static final int TILE_WALL_DEST_COL = 2;
    public static final int TILE_WALL_DEST_ROW = 0;
    // In src/utils/Config.java
    public static final double MAGNET_TOLERANCE = 0.40; // Più alto = più facile imboccare i corridoi
    public static final double GOBLIN_COMMON_SPEED = 0.03; // Leggermente più veloce per fluidità
    public static final double CENTER_TOLERANCE = 0.1;
    // In src/utils/Config.java
    public static final int AURA_FRAMES = 12; // 12 immagini per movimento
    public static final int AURA_ANIM_SPEED = 50; // Velocità animazione (ms per frame)

    // Indici di partenza (Linear Index = Row * Columns + Col)
    // Riga 3 (Right) -> 3 * 12 = 36
    // Riga 4 (Left)  -> 4 * 12 = 48
    // Riga 5 (Down)  -> 5 * 12 = 60
    // Riga 6 (Up)    -> 6 * 12 = 72

    public static final int AURA_RIGHT_START = 21;
    public static final int AURA_LEFT_START  = 33;
    public static final int AURA_DOWN_START  = 45;
    public static final int AURA_UP_START    = 57;

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

