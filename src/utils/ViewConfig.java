package utils;

/**
 * Costanti esclusivamente relative alla View:
 * dimensioni pixel, offset schermo, animazioni grafiche,
 * percorsi sprite sheet, indici frame, FPS, transizioni.
 * NON deve essere importata dal Model.
 */
public class ViewConfig {

    // --- DIMENSIONI RENDERIZZAZIONE (FISICHE) ---
    public static final int TILE_SIZE           = 64;
    public static final int GAME_PANEL_WIDTH    = LogicConfig.GRID_WIDTH  * TILE_SIZE;
    public static final int GAME_PANEL_HEIGHT   = LogicConfig.GRID_HEIGHT * TILE_SIZE;

    // --- FRAME RATE ---
    public static final int FPS                 = 60;
    public static final int GAME_LOOP_DELAY_MS  = 1000 / FPS;

    // --- OFFSET GRIGLIA E CORNICE ---
    public static final int FRAME_OFFSET_X      = 57;
    public static final int FRAME_OFFSET_Y      = 46;
    // Gli offset della griglia sono ora applicati globalmente via g2d.translate(57, 46) dal GamePanel
    public static final int GRID_OFFSET_X       = 121;
    public static final int GRID_OFFSET_Y       = 110;

    // --- DIMENSIONI CORNICE E FINESTRA ---
    //public static final int FRAME_WIDTH                = 960;
    //public static final int FRAME_HEIGHT               = 832;
    public static final int FRAME_WIDTH                = 1024;
    public static final int FRAME_HEIGHT              = 896;

    public static final int WINDOW_PREFERRED_WIDTH     = 1230;
    public static final int WINDOW_PREFERRED_HEIGHT    = 898;

    // --- PLAYER PIVOT ---
    public static final int PLAYER_PIVOT_Y = 102;

    // --- ANIMAZIONI PLAYER ---
    public static final int PLAYER_RUN_FRAMES    = 12;
    public static final int PLAYER_IDLE_FRAMES   = 16;
    public static final int PLAYER_ATTACK_FRAMES = 10;
    public static final int ANIMATION_DELAY      = 50;  // ms per frame IDLE/RUN/HURT
    public static final int ANIMATION_DELAY_STAFF_ATTACK = 15; // ms per frame bastone

    // --- LAMPEGGIO INVINCIBILITÀ ---
    public static final int FLICKER_DELAY_MS = 100; // Velocità del lampeggio visivo

    // --- TRANSIZIONE LIVELLO ---
    public static final int   MAX_TRANSITION_TICKS = 120;
    public static final float FADE_SPEED  = 0.02f;
    public static final float MAX_ALPHA   = 1.0f;
    public static final float MIN_ALPHA   = 0.0f;

    // --- RISORSE / SPRITE SHEET ---
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

    // --- ANIMAZIONE BOMBA ---
    public static final int BOMB_SPRITE_START       = 0;
    public static final int BOMB_FRAMES             = 8;
    public static final int BOMB_ANIM_FRAME_DURATION = 100;

    // --- PROIETTILI NEMICI (Items.png) ---
    public static final int BONE_DOWN_INDEX  = 8;
    public static final int BONE_LEFT_INDEX  = 9;
    public static final int BONE_RIGHT_INDEX = 10;
    public static final int BONE_UP_INDEX    = 11;

    // --- PROIETTILI PLAYER (Items.png) ---
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

    // --- ANIMAZIONE DISTRUZIONE CASSE ---
    public static final int DESTRUCTION_START          = 3;
    public static final int DESTRUCTION_FRAMES         = 3;
    public static final int DESTRUCTION_FRAME_DURATION = 150;

    // --- TIPI DI CELLA (usati dalla View per scegliere la tile giusta) ---
    // Ridichiarati qui per evitare dipendenza dalla View verso LogicConfig
    // nelle chiamate di tileManager.getTileImage(...)
    // NOTA: i valori devono restare allineati con quelli di LogicConfig
    public static final int CELL_EMPTY               = 0;
    public static final int CELL_INDESTRUCTIBLE_BLOCK = 1;
    public static final int CELL_DESTRUCTIBLE_BLOCK   = 2;
    public static final int CELL_CRACKED_FLOOR        = 3;
    public static final int CELL_LAVA_FLOOR           = 4;
    public static final int CELL_ORNAMENT             = 5;
    public static final int CELL_SKELETON_START       = 5;

    // --- COORDINATE SPRITESHEET TEMI ---
    // Villaggio
    public static final int VILLAGE_ROW           = 0;
    public static final int VILLAGE_FLOOR_COL     = 1;
    public static final int VILLAGE_WALL_IND_COL  = 0;
    public static final int VILLAGE_WALL_DEST_COL = 2;

    // Foresta
    public static final int FOREST_ROW            = 0;
    public static final int FOREST_FLOOR_COL      = 2;
    public static final int FOREST_WALL_IND_COL   = 0;
    public static final int FOREST_WALL_DEST_COL  = 1;

    // Caverna
    public static final int CAVE_ROW              = 0;
    public static final int CAVE_FLOOR_COL        = 3;
    public static final int CAVE_WALL_IND_COL     = 4;
    public static final int CAVE_WALL_DEST_COL    = 0;
    public static final int CAVE_FRAME_INDEX      = 5;
    public static final int CAVE_CRACKED_FLOOR_COL = 1;
    public static final int CAVE_LAVA_FLOOR_COL   = 2;

    // --- GOBLIN ANIMAZIONI (frame count) ---
    public static final int SHOOTER_ROW_WIDTH    = 30;
    public static final int SHOOTER_ATTACK_FRAMES = 2;
    public static final int GOBLIN_IDLE_FRAMES   = 16;
    public static final int GOBLIN_RUN_FRAMES    = 12;

    // COMMON GOBLIN
    public static final int COMMON_RUN_BACK_START  = 0;
    public static final int COMMON_RUN_FRONT_START = 12;
    public static final int COMMON_RUN_LEFT_START  = 24;
    public static final int COMMON_RUN_RIGHT_START = 36;

    // CHASING GOBLIN
    public static final int CHASING_IDLE_BACK_START  = 0;
    public static final int CHASING_RUN_BACK_START   = 16;
    public static final int CHASING_IDLE_FRONT_START = 28;
    public static final int CHASING_RUN_FRONT_START  = 44;
    public static final int CHASING_IDLE_LEFT_START  = 56;
    public static final int CHASING_RUN_LEFT_START   = 72;
    public static final int CHASING_IDLE_RIGHT_START = 84;
    public static final int CHASING_RUN_RIGHT_START  = 100;

    // SHOOTER GOBLIN
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

    // BOSS GOBLIN
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
    // MENU SELEZIONE PERSONAGGIO
    // =====================================================================
    //
    // ⚙️ GUIDA AL TUNING VISIVO
    //
    // Le coordinate sono RELATIVE all'area di disegno del menu (MENU_DRAW_*).
    // Per convertire in coordinate schermo assolute:
    //   screenX = MENU_DRAW_X + CHAR_FRAME_X[i]
    //   screenY = MENU_DRAW_Y + CHAR_FRAME_Y
    //
    // Se i riquadri non sono allineati ai personaggi nell'immagine:
    //   1. Modifica CHAR_FRAME_X[i] per spostare orizzontalmente ciascun riquadro
    //   2. Modifica CHAR_FRAME_Y per spostare verticalmente tutti i riquadri
    //   3. Modifica CHAR_FRAME_W / CHAR_FRAME_H per ridimensionare i rettangoli
    //
    // Se il pulsante "NEW GAME" non risponde al click:
    //   4. Modifica NEW_GAME_BTN_X/Y/W/H per spostare/ridimensionare la hitbox
    //
    // Se l'intera immagine è fuori posizione nel cabinet:
    //   5. Modifica MENU_DRAW_X/Y per spostare l'immagine
    //   6. Modifica MENU_DRAW_W/H per riscalare l'immagine
    // =====================================================================

    /** Immagine di sfondo del menu di selezione. */
    public static final String START_GAME_BG = "/StartGame.png";

    /** Area di disegno del menu dentro al Cabinet Arcade (centrato nello schermo). */
    public static final int MENU_DRAW_X = FRAME_OFFSET_X; // allineato all'inizio dello schermo del cabinato
    public static final int MENU_DRAW_Y = FRAME_OFFSET_Y; // offset Y dal bordo superiore (allineato alla cornice)
    public static final int MENU_DRAW_W = 960;            // larghezza reale di StartGame.png (= area schermo)
    public static final int MENU_DRAW_H = 832;            // altezza reale dell'area di disegno (px)

    /**
     * Hitbox dei 4 riquadri personaggio.
     * Ordine sx→dx: Male Wizard, Goblin Wizard, Veteran Wizard, Female Wizard.
     * CHAR_FRAME_X[i] = posizione X del riquadro i-esimo (relativa a MENU_DRAW_X).
     */
    public static final int[] CHAR_FRAME_X = { 128, 318, 508, 698 };  // posizioni X reali dei riquadri
    public static final int   CHAR_FRAME_Y = 265;   // posizione Y comune (scalata per 832)
    public static final int   CHAR_FRAME_W = 130;   // larghezza di ciascun riquadro (px)
    public static final int   CHAR_FRAME_H = 270;   // altezza di ciascun riquadro scalata per 832

    /**
     * Coordinate X del centro di ciascun personaggio per il puntatore/freccia selettore.
     * Calcolate come CHAR_FRAME_X[i] + CHAR_FRAME_W / 2.
     * Coordinate relative a FRAME_OFFSET_X (la cornice delle mappe).
     */
    public static final int[] CHAR_SELECTOR_X = {
            CHAR_FRAME_X[0] + CHAR_FRAME_W / 2,   // 108
            CHAR_FRAME_X[1] + CHAR_FRAME_W / 2,   // 302
            CHAR_FRAME_X[2] + CHAR_FRAME_W / 2,   // 492
            CHAR_FRAME_X[3] + CHAR_FRAME_W / 2    // 681
    };

    /**
     * Hitbox del pulsante "NEW GAME" / "Start Game".
     * Coordinate relative a MENU_DRAW_X / MENU_DRAW_Y.
     */
    public static final int NEW_GAME_BTN_X = 350;   // posizione X del pulsante (centrato in 960px)
    public static final int NEW_GAME_BTN_Y = 655;   // posizione Y del pulsante (scalata per 832)
    public static final int NEW_GAME_BTN_W = 260;   // larghezza del pulsante (px)
    public static final int NEW_GAME_BTN_H = 78;    // altezza del pulsante scalata
}