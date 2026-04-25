package utils;

/**
 * Facade di compatibilità.
 * Tutte le costanti sono state migrate in:
 *   - {@link LogicConfig}  → logica di gioco (Model / Controller)
 *   - {@link ViewConfig}   → rendering e risorse grafiche (View)
 *
 * Questa classe reindirizza ogni costante alla nuova sorgente, così
 * il codice esistente che importa Config.XYZ continua a compilare
 * senza modifiche. Nel tempo conviene aggiornare i file uno per uno
 * per importare direttamente LogicConfig o ViewConfig.
 */
public class Config {

    // =====================================================================
    // LOGICA DI GIOCO  →  LogicConfig
    // =====================================================================
    public static final int GRID_WIDTH                  = LogicConfig.GRID_WIDTH;
    public static final int GRID_HEIGHT                 = LogicConfig.GRID_HEIGHT;

    public static final double ENTITY_LOGICAL_SPEED         = LogicConfig.ENTITY_LOGICAL_SPEED;
    public static final double ENTITY_LOGICAL_HITBOX_WIDTH  = LogicConfig.ENTITY_LOGICAL_HITBOX_WIDTH;
    public static final double ENTITY_LOGICAL_HITBOX_HEIGHT = LogicConfig.ENTITY_LOGICAL_HITBOX_HEIGHT;

    public static final double MIN_LOGICAL_X            = LogicConfig.MIN_LOGICAL_X;
    public static final double MAX_LOGICAL_X            = LogicConfig.MAX_LOGICAL_X;
    public static final double MIN_LOGICAL_Y            = LogicConfig.MIN_LOGICAL_Y;
    public static final double MAX_LOGICAL_Y            = LogicConfig.MAX_LOGICAL_Y;

    public static final double CORNER_CORRECTION_SPEED  = LogicConfig.CORNER_CORRECTION_SPEED;
    public static final double CORNER_ALIGN_SPEED        = LogicConfig.CORNER_ALIGN_SPEED;
    public static final double MAGNET_TOLERANCE          = LogicConfig.MAGNET_TOLERANCE;
    public static final double CENTER_TOLERANCE          = LogicConfig.CENTER_TOLERANCE;

    public static final double GOBLIN_HITBOX_WIDTH       = LogicConfig.GOBLIN_HITBOX_WIDTH;
    public static final double GOBLIN_HITBOX_HEIGHT      = LogicConfig.GOBLIN_HITBOX_HEIGHT;

    public static final int    MAX_ENEMIES_ON_MAP        = LogicConfig.MAX_ENEMIES_ON_MAP;
    public static final long   SPAWN_INTERVAL_MS         = LogicConfig.SPAWN_INTERVAL_MS;
    public static final int    SPAWN_SAFE_DISTANCE       = LogicConfig.SPAWN_SAFE_DISTANCE;
    public static final double MIN_SPAWN_DISTANCE        = LogicConfig.MIN_SPAWN_DISTANCE;

    public static final int    SMELL_THRESHOLD_DISTANCE  = LogicConfig.SMELL_THRESHOLD_DISTANCE;
    public static final int    SMELL_BLOCK_PENALTY       = LogicConfig.SMELL_BLOCK_PENALTY;
    public static final int    SAFE_DISTANCE_FROM_BOMB   = LogicConfig.SAFE_DISTANCE_FROM_BOMB;

    public static final int    SHOOTER_TELEGRAPH_TIME    = LogicConfig.SHOOTER_TELEGRAPH_TIME;
    public static final double SHOOTER_SPEED_AIMING      = LogicConfig.SHOOTER_SPEED_AIMING;
    public static final double SHOOTER_SPEED_CHASE       = LogicConfig.SHOOTER_SPEED_CHASE;
    public static final int    SHOOTER_MAX_AMMO          = LogicConfig.SHOOTER_MAX_AMMO;
    public static final int    SHOOTER_RELOAD_TIME       = LogicConfig.SHOOTER_RELOAD_TIME;

    public static final double GOBLIN_COMMON_SPEED       = LogicConfig.GOBLIN_COMMON_SPEED;

    public static final int    AuraProjectileSpeed       = LogicConfig.AuraProjectileSpeed;
    public static final int    BoneProjectileSpeed       = LogicConfig.BoneProjectileSpeed;

    public static final int    INITIAL_LIVES             = LogicConfig.INITIAL_LIVES;
    public static final long   INVINCIBILITY_DURATION_MS = LogicConfig.INVINCIBILITY_DURATION_MS;

    public static final int    BOMB_DETONATION_TICKS     = LogicConfig.BOMB_DETONATION_TICKS;
    public static final int    INITIAL_MAX_BOMBS         = LogicConfig.INITIAL_MAX_BOMBS;
    public static final int    DEFAULT_BOMB_RADIUS       = LogicConfig.DEFAULT_BOMB_RADIUS;

    public static final int    FIRE_DURATION_TICKS       = LogicConfig.FIRE_DURATION_TICKS;

    public static final int    CELL_EMPTY                = LogicConfig.CELL_EMPTY;
    public static final int    CELL_INDESTRUCTIBLE_BLOCK = LogicConfig.CELL_INDESTRUCTIBLE_BLOCK;
    public static final int    CELL_DESTRUCTIBLE_BLOCK   = LogicConfig.CELL_DESTRUCTIBLE_BLOCK;
    public static final int    CELL_CRACKED_FLOOR        = LogicConfig.CELL_CRACKED_FLOOR;
    public static final int    CELL_LAVA_FLOOR           = LogicConfig.CELL_LAVA_FLOOR;
    public static final int    CELL_ORNAMENT             = LogicConfig.CELL_ORNAMENT;

    public static final int    SCORE_CRATE               = LogicConfig.SCORE_CRATE;
    public static final int    SCORE_COMMON_GOBLIN       = LogicConfig.SCORE_COMMON_GOBLIN;
    public static final int    SCORE_CHASING_GOBLIN      = LogicConfig.SCORE_CHASING_GOBLIN;
    public static final int    SCORE_SHOOTER_GOBLIN      = LogicConfig.SCORE_SHOOTER_GOBLIN;
    public static final int    SCORE_ZONE_CAP            = LogicConfig.SCORE_ZONE_CAP;
    public static final int    PERFECT_LEVEL_BONUS       = LogicConfig.PERFECT_LEVEL_BONUS;
    public static final int    SCORE_BOSS_BASE           = LogicConfig.SCORE_BOSS_BASE;
    public static final int    MAX_BOSS_TIME_BONUS       = LogicConfig.MAX_BOSS_TIME_BONUS;
    public static final int    BOSS_BONUS_DECAY_PER_SEC  = LogicConfig.BOSS_BONUS_DECAY_PER_SEC;

    // --- PORTALE BOSS (Zona 2) ---
    public static final int    BOSS_PORTAL_ROW                = LogicConfig.BOSS_PORTAL_ROW;
    public static final int    BOSS_PORTAL_COL                = LogicConfig.BOSS_PORTAL_COL;
    public static final long   BOSS_PORTAL_SPAWN_INTERVAL_MS  = LogicConfig.BOSS_PORTAL_SPAWN_INTERVAL_MS;
    public static final int    BOSS_PORTAL_MAX_GOBLINS        = LogicConfig.BOSS_PORTAL_MAX_GOBLINS;

    // =====================================================================
    // VIEW / RENDERING  →  ViewConfig
    // =====================================================================
    public static final int    TILE_SIZE                 = ViewConfig.TILE_SIZE;
    public static final int    GAME_PANEL_WIDTH          = ViewConfig.GAME_PANEL_WIDTH;
    public static final int    GAME_PANEL_HEIGHT         = ViewConfig.GAME_PANEL_HEIGHT;

    public static final int    FPS                       = ViewConfig.FPS;
    public static final int    GAME_LOOP_DELAY_MS        = ViewConfig.GAME_LOOP_DELAY_MS;

    public static final int    FRAME_OFFSET_X            = ViewConfig.FRAME_OFFSET_X;
    public static final int    FRAME_OFFSET_Y            = ViewConfig.FRAME_OFFSET_Y;
    public static final int    GRID_OFFSET_X             = ViewConfig.GRID_OFFSET_X;
    public static final int    GRID_OFFSET_Y             = ViewConfig.GRID_OFFSET_Y;

    public static final int    FRAME_WIDTH               = ViewConfig.FRAME_WIDTH;
    public static final int    FRAME_HEIGHT              = ViewConfig.FRAME_HEIGHT;
    public static final int    WINDOW_PREFERRED_WIDTH    = ViewConfig.WINDOW_PREFERRED_WIDTH;
    public static final int    WINDOW_PREFERRED_HEIGHT   = ViewConfig.WINDOW_PREFERRED_HEIGHT;

    public static final int    PLAYER_PIVOT_Y            = ViewConfig.PLAYER_PIVOT_Y;

    public static final int    PLAYER_RUN_FRAMES         = ViewConfig.PLAYER_RUN_FRAMES;
    public static final int    PLAYER_IDLE_FRAMES        = ViewConfig.PLAYER_IDLE_FRAMES;
    public static final int    PLAYER_ATTACK_FRAMES      = ViewConfig.PLAYER_ATTACK_FRAMES;
    public static final int    ANIMATION_DELAY           = ViewConfig.ANIMATION_DELAY;
    public static final int    ANIMATION_DELAY_STAFF_ATTACK = ViewConfig.ANIMATION_DELAY_STAFF_ATTACK;

    public static final int    FLICKER_DELAY_MS          = ViewConfig.FLICKER_DELAY_MS;

    public static final int    MAX_TRANSITION_TICKS      = ViewConfig.MAX_TRANSITION_TICKS;
    public static final float  FADE_SPEED                = ViewConfig.FADE_SPEED;
    public static final float  MAX_ALPHA                 = ViewConfig.MAX_ALPHA;
    public static final float  MIN_ALPHA                 = ViewConfig.MIN_ALPHA;

    public static final String PLAYER1_SHEET             = ViewConfig.PLAYER1_SHEET;
    public static final int    ENTITY_FRAME_SIZE         = ViewConfig.ENTITY_FRAME_SIZE;
    public static final String VILLAGE_SHEET             = ViewConfig.VILLAGE_SHEET;
    public static final String VILLAGE_FRAME             = ViewConfig.VILLAGE_FRAME;
    public static final int    VILLAGE_FRAME_INDEX       = ViewConfig.VILLAGE_FRAME_INDEX;
    public static final String FOREST_SHEET              = ViewConfig.FOREST_SHEET;
    public static final String FOREST_FRAME              = ViewConfig.FOREST_FRAME;
    public static final int    FOREST_FRAME_INDEX        = ViewConfig.FOREST_FRAME_INDEX;
    public static final int    THEME_FRAME_INDEX         = ViewConfig.THEME_FRAME_INDEX;
    public static final String CAVE_SHEET                = ViewConfig.CAVE_SHEET;
    public static final String CAVE_FRAME                = ViewConfig.CAVE_FRAME;
    public static final String ORNAMENTS_SHEET           = ViewConfig.ORNAMENTS_SHEET;
    public static final String CAVE_SKELETON_SHEET       = ViewConfig.CAVE_SKELETON_SHEET;
    public static final int    CAVE_BUILDING_SIZE        = ViewConfig.CAVE_BUILDING_SIZE;
    public static final int    SKELETON_FRAMES_COUNT     = ViewConfig.SKELETON_FRAMES_COUNT;
    public static final int    SKELETON_SHEET_COLS       = ViewConfig.SKELETON_SHEET_COLS;
    public static final String SHOOTERGOBLIN_SHEET       = ViewConfig.SHOOTERGOBLIN_SHEET;
    public static final String CHASING_GOBLIN_SHEET      = ViewConfig.CHASING_GOBLIN_SHEET;
    public static final String COMMON_GOBLIN             = ViewConfig.COMMON_GOBLIN;
    public static final String BOSS_GOBLIN_SHEET         = ViewConfig.BOSS_GOBLIN_SHEET;
    public static final String ITEM_SHEET                = ViewConfig.ITEM_SHEET;

    public static final int    BOMB_SPRITE_START          = ViewConfig.BOMB_SPRITE_START;
    public static final int    BOMB_FRAMES                = ViewConfig.BOMB_FRAMES;
    public static final int    BOMB_ANIM_FRAME_DURATION   = ViewConfig.BOMB_ANIM_FRAME_DURATION;

    public static final int    BONE_DOWN_INDEX            = ViewConfig.BONE_DOWN_INDEX;
    public static final int    BONE_LEFT_INDEX            = ViewConfig.BONE_LEFT_INDEX;
    public static final int    BONE_RIGHT_INDEX           = ViewConfig.BONE_RIGHT_INDEX;
    public static final int    BONE_UP_INDEX              = ViewConfig.BONE_UP_INDEX;

    public static final int    AURA_DOWN_INDEX            = ViewConfig.AURA_DOWN_INDEX;
    public static final int    AURA_LEFT_INDEX            = ViewConfig.AURA_LEFT_INDEX;
    public static final int    AURA_RIGHT_INDEX           = ViewConfig.AURA_RIGHT_INDEX;
    public static final int    AURA_UP_INDEX              = ViewConfig.AURA_UP_INDEX;
    public static final int    AURA_FRAMES                = ViewConfig.AURA_FRAMES;
    public static final int    AURA_ANIM_SPEED            = ViewConfig.AURA_ANIM_SPEED;

    public static final int    AURA_RIGHT_START           = ViewConfig.AURA_RIGHT_START;
    public static final int    AURA_LEFT_START            = ViewConfig.AURA_LEFT_START;
    public static final int    AURA_DOWN_START            = ViewConfig.AURA_DOWN_START;
    public static final int    AURA_UP_START              = ViewConfig.AURA_UP_START;

    public static final int    DESTRUCTION_START          = ViewConfig.DESTRUCTION_START;
    public static final int    DESTRUCTION_FRAMES         = ViewConfig.DESTRUCTION_FRAMES;
    public static final int    DESTRUCTION_FRAME_DURATION = ViewConfig.DESTRUCTION_FRAME_DURATION;

    public static final int    CELL_SKELETON_START        = ViewConfig.CELL_SKELETON_START;

    public static final int    VILLAGE_ROW                = ViewConfig.VILLAGE_ROW;
    public static final int    VILLAGE_FLOOR_COL          = ViewConfig.VILLAGE_FLOOR_COL;
    public static final int    VILLAGE_WALL_IND_COL       = ViewConfig.VILLAGE_WALL_IND_COL;
    public static final int    VILLAGE_WALL_DEST_COL      = ViewConfig.VILLAGE_WALL_DEST_COL;

    public static final int    FOREST_ROW                 = ViewConfig.FOREST_ROW;
    public static final int    FOREST_FLOOR_COL           = ViewConfig.FOREST_FLOOR_COL;
    public static final int    FOREST_WALL_IND_COL        = ViewConfig.FOREST_WALL_IND_COL;
    public static final int    FOREST_WALL_DEST_COL       = ViewConfig.FOREST_WALL_DEST_COL;

    public static final int    CAVE_ROW                   = ViewConfig.CAVE_ROW;
    public static final int    CAVE_FLOOR_COL             = ViewConfig.CAVE_FLOOR_COL;
    public static final int    CAVE_WALL_IND_COL          = ViewConfig.CAVE_WALL_IND_COL;
    public static final int    CAVE_WALL_DEST_COL         = ViewConfig.CAVE_WALL_DEST_COL;
    public static final int    CAVE_FRAME_INDEX           = ViewConfig.CAVE_FRAME_INDEX;
    public static final int    CAVE_CRACKED_FLOOR_COL     = ViewConfig.CAVE_CRACKED_FLOOR_COL;
    public static final int    CAVE_LAVA_FLOOR_COL        = ViewConfig.CAVE_LAVA_FLOOR_COL;

    public static final int    SHOOTER_ROW_WIDTH          = ViewConfig.SHOOTER_ROW_WIDTH;
    public static final int    SHOOTER_ATTACK_FRAMES      = ViewConfig.SHOOTER_ATTACK_FRAMES;
    public static final int    GOBLIN_IDLE_FRAMES         = ViewConfig.GOBLIN_IDLE_FRAMES;
    public static final int    GOBLIN_RUN_FRAMES          = ViewConfig.GOBLIN_RUN_FRAMES;

    public static final int    COMMON_RUN_BACK_START      = ViewConfig.COMMON_RUN_BACK_START;
    public static final int    COMMON_RUN_FRONT_START     = ViewConfig.COMMON_RUN_FRONT_START;
    public static final int    COMMON_RUN_LEFT_START      = ViewConfig.COMMON_RUN_LEFT_START;
    public static final int    COMMON_RUN_RIGHT_START     = ViewConfig.COMMON_RUN_RIGHT_START;

    public static final int    CHASING_IDLE_BACK_START    = ViewConfig.CHASING_IDLE_BACK_START;
    public static final int    CHASING_RUN_BACK_START     = ViewConfig.CHASING_RUN_BACK_START;
    public static final int    CHASING_IDLE_FRONT_START   = ViewConfig.CHASING_IDLE_FRONT_START;
    public static final int    CHASING_RUN_FRONT_START    = ViewConfig.CHASING_RUN_FRONT_START;
    public static final int    CHASING_IDLE_LEFT_START    = ViewConfig.CHASING_IDLE_LEFT_START;
    public static final int    CHASING_RUN_LEFT_START     = ViewConfig.CHASING_RUN_LEFT_START;
    public static final int    CHASING_IDLE_RIGHT_START   = ViewConfig.CHASING_IDLE_RIGHT_START;
    public static final int    CHASING_RUN_RIGHT_START    = ViewConfig.CHASING_RUN_RIGHT_START;

    public static final int    SHOOTER_ATTACK_BACK_START  = ViewConfig.SHOOTER_ATTACK_BACK_START;
    public static final int    SHOOTER_IDLE_BACK_START    = ViewConfig.SHOOTER_IDLE_BACK_START;
    public static final int    SHOOTER_RUN_BACK_START     = ViewConfig.SHOOTER_RUN_BACK_START;
    public static final int    SHOOTER_ATTACK_FRONT_START = ViewConfig.SHOOTER_ATTACK_FRONT_START;
    public static final int    SHOOTER_IDLE_FRONT_START   = ViewConfig.SHOOTER_IDLE_FRONT_START;
    public static final int    SHOOTER_RUN_FRONT_START    = ViewConfig.SHOOTER_RUN_FRONT_START;
    public static final int    SHOOTER_ATTACK_LEFT_START  = ViewConfig.SHOOTER_ATTACK_LEFT_START;
    public static final int    SHOOTER_IDLE_LEFT_START    = ViewConfig.SHOOTER_IDLE_LEFT_START;
    public static final int    SHOOTER_RUN_LEFT_START     = ViewConfig.SHOOTER_RUN_LEFT_START;
    public static final int    SHOOTER_ATTACK_RIGHT_START = ViewConfig.SHOOTER_ATTACK_RIGHT_START;
    public static final int    SHOOTER_IDLE_RIGHT_START   = ViewConfig.SHOOTER_IDLE_RIGHT_START;
    public static final int    SHOOTER_RUN_RIGHT_START    = ViewConfig.SHOOTER_RUN_RIGHT_START;

    public static final int    BOSS_ATTACK_FRAMES         = ViewConfig.BOSS_ATTACK_FRAMES;
    public static final int    BOSS_IDLE_FRAMES           = ViewConfig.BOSS_IDLE_FRAMES;
    public static final int    BOSS_RUN_FRAMES            = ViewConfig.BOSS_RUN_FRAMES;
    public static final int    BOSS_DYING_FRAMES          = ViewConfig.BOSS_DYING_FRAMES;
    public static final int    BOSS_FRAME_SIZE            = ViewConfig.BOSS_FRAME_SIZE;

    public static final int    BOSS_ATTACK_BACK_START     = ViewConfig.BOSS_ATTACK_BACK_START;
    public static final int    BOSS_IDLE_BACK_START       = ViewConfig.BOSS_IDLE_BACK_START;
    public static final int    BOSS_RUN_BACK_START        = ViewConfig.BOSS_RUN_BACK_START;
    public static final int    BOSS_DYING_START           = ViewConfig.BOSS_DYING_START;
    public static final int    BOSS_ATTACK_FRONT_START    = ViewConfig.BOSS_ATTACK_FRONT_START;
    public static final int    BOSS_IDLE_FRONT_START      = ViewConfig.BOSS_IDLE_FRONT_START;
    public static final int    BOSS_RUN_FRONT_START       = ViewConfig.BOSS_RUN_FRONT_START;
    public static final int    BOSS_ATTACK_LEFT_START     = ViewConfig.BOSS_ATTACK_LEFT_START;
    public static final int    BOSS_IDLE_LEFT_START       = ViewConfig.BOSS_IDLE_LEFT_START;
    public static final int    BOSS_RUN_LEFT_START        = ViewConfig.BOSS_RUN_LEFT_START;
    public static final int    BOSS_ATTACK_RIGHT_START    = ViewConfig.BOSS_ATTACK_RIGHT_START;
    public static final int    BOSS_IDLE_RIGHT_START      = ViewConfig.BOSS_IDLE_RIGHT_START;
    public static final int    BOSS_RUN_RIGHT_START       = ViewConfig.BOSS_RUN_RIGHT_START;

    // =====================================================================
    // MENU SELEZIONE PERSONAGGIO  →  ViewConfig
    // =====================================================================
    public static final String START_GAME_BG               = ViewConfig.START_GAME_BG;
    public static final int    MENU_DRAW_X                 = ViewConfig.MENU_DRAW_X;
    public static final int    MENU_DRAW_Y                 = ViewConfig.MENU_DRAW_Y;
    public static final int    MENU_DRAW_W                 = ViewConfig.MENU_DRAW_W;
    public static final int    MENU_DRAW_H                 = ViewConfig.MENU_DRAW_H;
    public static final int[]  CHAR_FRAME_X                = ViewConfig.CHAR_FRAME_X;
    public static final int    CHAR_FRAME_Y                = ViewConfig.CHAR_FRAME_Y;
    public static final int    CHAR_FRAME_W                = ViewConfig.CHAR_FRAME_W;
    public static final int    CHAR_FRAME_H                = ViewConfig.CHAR_FRAME_H;
}
