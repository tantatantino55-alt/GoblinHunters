package goblinhunters.utils;

/** View constants (rendering, sprites, fonts, animations). Never imported by Model. */
public class ViewConfig {

    // rendering dimensions
    public static final int TILE_SIZE           = 64;
    public static final int GAME_PANEL_WIDTH    = LogicConfig.GRID_WIDTH  * TILE_SIZE;
    public static final int GAME_PANEL_HEIGHT   = LogicConfig.GRID_HEIGHT * TILE_SIZE;

    // frame rate
    public static final int FPS                = 60;
    public static final int GAME_LOOP_DELAY_MS = 1000 / FPS;

    // frame and grid offsets
    public static final int FRAME_OFFSET_X = 57;
    public static final int FRAME_OFFSET_Y = 46;
    // grid offsets are applied globally via g2d.translate(57, 46) in GamePanel
    public static final int GRID_OFFSET_X  = 121;
    public static final int GRID_OFFSET_Y  = 110;

    // frame and window size
    public static final int FRAME_WIDTH             = 1024;
    public static final int FRAME_HEIGHT            = 896;
    public static final int WINDOW_PREFERRED_WIDTH  = 1230;
    public static final int WINDOW_PREFERRED_HEIGHT = 898;

    // player pivot
    public static final int PLAYER_PIVOT_Y = 102;

    // player animations
    public static final int PLAYER_RUN_FRAMES    = 12;
    public static final int PLAYER_IDLE_FRAMES   = 16;
    public static final int PLAYER_ATTACK_FRAMES = 10;
    public static final int ANIMATION_DELAY      = 50;  // ms per frame (idle / run / hurt)
    public static final int ANIMATION_DELAY_STAFF_ATTACK = 15; // ms per frame (staff swing)

    // invincibility flicker
    public static final int FLICKER_DELAY_MS = 100;

    // level transition
    public static final int   MAX_TRANSITION_TICKS = 120;
    public static final float FADE_SPEED  = 0.02f;
    public static final float MAX_ALPHA   = 1.0f;
    public static final float MIN_ALPHA   = 0.0f;

    // sprite sheet paths and frame sizes
    public static final String PLAYER1_SHEET        = "/wizardmale.png";
    public static final int    ENTITY_FRAME_SIZE    = 128;

    public static final String VILLAGE_SHEET        = "/Village.png";
    public static final String VILLAGE_FRAME        = "/VillageFrame.png";
    public static final int    VILLAGE_FRAME_INDEX  = 3;

    public static final String FOREST_SHEET         = "/Forest.png";
    public static final String FOREST_FRAME         = "/ForestFrame.png";
    public static final int    FOREST_FRAME_INDEX   = 4;
    public static final int    THEME_FRAME_INDEX    = 30;

    public static final String CAVE_SHEET           = "/Cave.png";
    public static final String CAVE_FRAME           = "/CaveFrame.png";

    public static final String ORNAMENTS_SHEET      = "/Ornaments.png";
    public static final String CAVE_SKELETON_SHEET  = "/CaveSkeleton.png";
    public static final int    CAVE_BUILDING_SIZE   = 128;
    public static final int    SKELETON_FRAMES_COUNT = 18;
    public static final int    SKELETON_SHEET_COLS  = 8;

    public static final String SHOOTERGOBLIN_SHEET  = "/ShooterGoblin.png";
    public static final String CHASING_GOBLIN_SHEET = "/ChasingGoblin.png";
    public static final String COMMON_GOBLIN        = "/CommonGoblin.png";
    public static final String BOSS_GOBLIN_SHEET    = "/GoblinBoss.png";

    public static final String ITEM_SHEET           = "/Items.png";

    // bomb animation
    public static final int BOMB_SPRITE_START        = 0;
    public static final int BOMB_FRAMES              = 8;
    public static final int BOMB_ANIM_FRAME_DURATION = 100;

    // enemy projectiles (Items.png)
    public static final int BONE_DOWN_INDEX  = 8;
    public static final int BONE_LEFT_INDEX  = 9;
    public static final int BONE_RIGHT_INDEX = 10;
    public static final int BONE_UP_INDEX    = 11;

    // player projectiles (Items.png)
    public static final int AURA_DOWN_INDEX  = 12;
    public static final int AURA_LEFT_INDEX  = 13;
    public static final int AURA_RIGHT_INDEX = 14;
    public static final int AURA_UP_INDEX    = 15;
    public static final int AURA_FRAMES      = 12;
    public static final int AURA_ANIM_SPEED  = 50;

    public static final int AURA_RIGHT_START = 21;
    public static final int AURA_LEFT_START  = 33;
    public static final int AURA_DOWN_START  = 45;
    public static final int AURA_UP_START    = 57;

    // crate destruction animation
    public static final int DESTRUCTION_START          = 3;
    public static final int DESTRUCTION_FRAMES         = 3;
    public static final int DESTRUCTION_FRAME_DURATION = 150;

    // cell type codes (redeclared here so View never imports LogicConfig directly)
    // values must stay in sync with LogicConfig cell constants
    public static final int CELL_EMPTY                = 0;
    public static final int CELL_INDESTRUCTIBLE_BLOCK = 1;
    public static final int CELL_DESTRUCTIBLE_BLOCK   = 2;
    public static final int CELL_CRACKED_FLOOR        = 3;
    public static final int CELL_LAVA_FLOOR           = 4;
    public static final int CELL_ORNAMENT             = 5;
    public static final int CELL_SKELETON_START       = 5;

    // theme sprite sheet coordinates
    // Village
    public static final int VILLAGE_ROW           = 0;
    public static final int VILLAGE_FLOOR_COL     = 1;
    public static final int VILLAGE_WALL_IND_COL  = 0;
    public static final int VILLAGE_WALL_DEST_COL = 2;

    // Forest
    public static final int FOREST_ROW            = 0;
    public static final int FOREST_FLOOR_COL      = 2;
    public static final int FOREST_WALL_IND_COL   = 0;
    public static final int FOREST_WALL_DEST_COL  = 1;

    // Cave
    public static final int CAVE_ROW               = 0;
    public static final int CAVE_FLOOR_COL         = 3;
    public static final int CAVE_WALL_IND_COL      = 4;
    public static final int CAVE_WALL_DEST_COL     = 0;
    public static final int CAVE_FRAME_INDEX       = 5;
    public static final int CAVE_CRACKED_FLOOR_COL = 1;
    public static final int CAVE_LAVA_FLOOR_COL    = 2;

    // goblin animation frame counts
    public static final int SHOOTER_ROW_WIDTH     = 30;
    public static final int SHOOTER_ATTACK_FRAMES = 2;
    public static final int GOBLIN_IDLE_FRAMES    = 16;
    public static final int GOBLIN_RUN_FRAMES     = 12;

    // Common Goblin frame starts
    public static final int COMMON_RUN_BACK_START  = 0;
    public static final int COMMON_RUN_FRONT_START = 12;
    public static final int COMMON_RUN_LEFT_START  = 24;
    public static final int COMMON_RUN_RIGHT_START = 36;

    // Chasing Goblin frame starts
    public static final int CHASING_IDLE_BACK_START  = 0;
    public static final int CHASING_RUN_BACK_START   = 16;
    public static final int CHASING_IDLE_FRONT_START = 28;
    public static final int CHASING_RUN_FRONT_START  = 44;
    public static final int CHASING_IDLE_LEFT_START  = 56;
    public static final int CHASING_RUN_LEFT_START   = 72;
    public static final int CHASING_IDLE_RIGHT_START = 84;
    public static final int CHASING_RUN_RIGHT_START  = 100;

    // Shooter Goblin frame starts
    public static final int SHOOTER_ATTACK_BACK_START  = 0;
    public static final int SHOOTER_IDLE_BACK_START    = 2;
    public static final int SHOOTER_RUN_BACK_START     = 18;
    public static final int SHOOTER_ATTACK_FRONT_START = 30;
    public static final int SHOOTER_IDLE_FRONT_START   = 32;
    public static final int SHOOTER_RUN_FRONT_START    = 48;
    public static final int SHOOTER_ATTACK_LEFT_START  = 60;
    public static final int SHOOTER_IDLE_LEFT_START    = 62;
    public static final int SHOOTER_RUN_LEFT_START     = 78;
    public static final int SHOOTER_ATTACK_RIGHT_START = 90;
    public static final int SHOOTER_IDLE_RIGHT_START   = 92;
    public static final int SHOOTER_RUN_RIGHT_START    = 108;

    // Boss Goblin frame counts and starts
    public static final int BOSS_ATTACK_FRAMES = 10;
    public static final int BOSS_IDLE_FRAMES   = 16;
    public static final int BOSS_RUN_FRAMES    = 12;
    public static final int BOSS_DYING_FRAMES  = 10;
    public static final int BOSS_FRAME_SIZE    = 192;

    public static final int BOSS_ATTACK_BACK_START  = 0;
    public static final int BOSS_IDLE_BACK_START    = 10;
    public static final int BOSS_RUN_BACK_START     = 26;
    public static final int BOSS_DYING_START        = 38;
    public static final int BOSS_ATTACK_FRONT_START = 48;
    public static final int BOSS_IDLE_FRONT_START   = 58;
    public static final int BOSS_RUN_FRONT_START    = 74;
    public static final int BOSS_ATTACK_LEFT_START  = 86;
    public static final int BOSS_IDLE_LEFT_START    = 96;
    public static final int BOSS_RUN_LEFT_START     = 112;
    public static final int BOSS_ATTACK_RIGHT_START = 124;
    public static final int BOSS_IDLE_RIGHT_START   = 134;
    public static final int BOSS_RUN_RIGHT_START    = 150;

    // =====================================================================
    // character selection menu
    // =====================================================================

    public static final String START_GAME_BG = "/StartGame.png";

    /** Drawing area of the menu inside the arcade cabinet, aligned to the screen edge. */
    public static final int MENU_DRAW_X = FRAME_OFFSET_X;
    public static final int MENU_DRAW_Y = FRAME_OFFSET_Y;
    public static final int MENU_DRAW_W = 960; // exact width of StartGame.png
    public static final int MENU_DRAW_H = 832; // exact height of the drawable area

    /**
     * Hitboxes for the 4 character frames (left → right: Male, Goblin, Veteran, Female).
     * X positions are relative to MENU_DRAW_X; all frames share the same Y.
     */
    public static final int[] CHAR_FRAME_X = { 128, 318, 508, 698 };
    public static final int   CHAR_FRAME_Y = 265;
    public static final int   CHAR_FRAME_W = 130;
    public static final int   CHAR_FRAME_H = 270;

    /**
     * X center of each character, used for the selector arrow.
     * Computed as CHAR_FRAME_X[i] + CHAR_FRAME_W / 2, relative to FRAME_OFFSET_X.
     */
    public static final int[] CHAR_SELECTOR_X = {
            CHAR_FRAME_X[0] + CHAR_FRAME_W / 2,
            CHAR_FRAME_X[1] + CHAR_FRAME_W / 2,
            CHAR_FRAME_X[2] + CHAR_FRAME_W / 2,
            CHAR_FRAME_X[3] + CHAR_FRAME_W / 2
    };

    /** Hitbox for the "NEW GAME" / "Start Game" button, relative to MENU_DRAW_X/Y. */
    public static final int NEW_GAME_BTN_X = 350;
    public static final int NEW_GAME_BTN_Y = 655;
    public static final int NEW_GAME_BTN_W = 260;
    public static final int NEW_GAME_BTN_H = 78;

    // =====================================================================
    // HUD layout
    // =====================================================================

    /** X position of the right-side HUD panel on screen. */
    public static final int HUD_PANEL_X = 1072;
    /** Width of the right-side HUD panel. */
    public static final int HUD_PANEL_W = 145;
    /** Y position where the HUD content starts rendering. */
    public static final int HUD_START_Y = 200;
    /** Size (W and H) of each consumable icon slot. */
    public static final int HUD_ICON_SIZE = 36;
    /** Horizontal gap between consumable icons. */
    public static final int HUD_ICON_GAP = 6;
    /** Size of each power-up icon slot. */
    public static final int HUD_POWER_SIZE = 36;
    /** Horizontal gap between power-up icons. */
    public static final int HUD_POWER_GAP = 8;
    /** Size of the staff icon. */
    public static final int HUD_STAFF_SIZE = 40;
    /** Base draw size for HUD icon images inside drawHudIcon(). */
    public static final int HUD_ICON_BASE_SIZE = 48;
}
