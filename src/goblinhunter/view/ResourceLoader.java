package goblinhunter.view;

import goblinhunter.utils.Config;
import goblinhunter.utils.PlayerState;
import goblinhunter.utils.ViewConfig;

import java.awt.image.BufferedImage;

/** Loads and registers all game sprites, animations, and tile themes into SpriteManager/TileManager. */
public class ResourceLoader {

    public void loadAllResources() {
        SpriteManager sm = SpriteManager.getInstance();

        loadMenuResources(sm);
        loadPlayerAnimations(sm, Config.PLAYER1_SHEET);
        loadBombResources(sm);
        loadDestructionAnimations(sm);
        loadFireResources(sm);
        loadShooterGoblinAnimations(sm);
        loadChasingGoblinAnimations(sm);
        loadCommonGoblinAnimations(sm);
        loadBossGoblinAnimations(sm);
        loadProjectiles(sm);
        loadMapThemes(sm);
        loadPortalAnimation(sm);
        loadPowerUps(sm);
        loadConsumables(sm);
        loadHUDIcons(sm);
        buildGrayscaleHudIcons(sm);
    }

    /**
     * Loads all player animations from a sprite sheet.
     * Parameterised so any character sheet (same structure) can be swapped in.
     *
     * @param sm    SpriteManager to register animations into
     * @param sheet path to the sprite sheet (e.g. "/wizardmale.png")
     */
    private void loadPlayerAnimations(SpriteManager sm, String sheet) {
        int size = Config.ENTITY_FRAME_SIZE;

        // facing down
        sm.loadAnimation(PlayerState.ATTACK_FRONT, sheet, 58, 10, size);
        sm.loadAnimation(PlayerState.CAST_FRONT,   sheet, 58, 10, size); // same frames as attack
        sm.loadAnimation(PlayerState.HURT_FRONT,   sheet, 68, 10, size);
        sm.loadAnimation(PlayerState.IDLE_FRONT,   sheet, 78, 16, size);
        sm.loadAnimation(PlayerState.RUN_FRONT,    sheet, 94, 12, size);

        // facing up
        sm.loadAnimation(PlayerState.ATTACK_BACK, sheet, 0, 10, size);
        sm.loadAnimation(PlayerState.CAST_BACK,   sheet, 0, 10, size); // same frames as attack
        sm.loadAnimation(PlayerState.HURT_BACK,   sheet, 10, 10, size);
        sm.loadAnimation(PlayerState.IDLE_BACK,   sheet, 20, 16, size);
        sm.loadAnimation(PlayerState.RUN_BACK,    sheet, 36, 12, size);

        // facing left
        sm.loadAnimation(PlayerState.ATTACK_LEFT, sheet, 106, 10, size);
        sm.loadAnimation(PlayerState.CAST_LEFT,   sheet, 106, 10, size); // same frames as attack
        sm.loadAnimation(PlayerState.HURT_LEFT,   sheet, 116, 10, size);
        sm.loadAnimation(PlayerState.IDLE_LEFT,   sheet, 126, 16, size);
        sm.loadAnimation(PlayerState.RUN_LEFT,    sheet, 142, 12, size);

        // facing right
        sm.loadAnimation(PlayerState.ATTACK_RIGHT, sheet, 154, 10, size);
        sm.loadAnimation(PlayerState.CAST_RIGHT,   sheet, 154, 10, size); // same frames as attack
        sm.loadAnimation(PlayerState.HURT_RIGHT,   sheet, 164, 10, size);
        sm.loadAnimation(PlayerState.IDLE_RIGHT,   sheet, 174, 16, size);
        sm.loadAnimation(PlayerState.RUN_RIGHT,    sheet, 190, 12, size);

        sm.loadAnimation(PlayerState.DYING, sheet, 48, 10, size);
    }

    private void loadBombResources(SpriteManager sm) {
        sm.loadAnimation("BOMB_ANIM", Config.ITEM_SHEET, Config.BOMB_SPRITE_START, Config.BOMB_FRAMES, 64);
    }

    private void loadDestructionAnimations(SpriteManager sm) {
        sm.loadAnimation("CRATE_BREAK", Config.VILLAGE_SHEET, Config.DESTRUCTION_START, Config.DESTRUCTION_FRAMES, 64);
        sm.loadAnimation("BUSH_BREAK",  Config.FOREST_SHEET,  3, 3, 64);
    }

    private void loadFireResources(SpriteManager sm) {
        String sheet = Config.ITEM_SHEET;
        int size = 64;
        sm.loadAnimation("FIRE_0", sheet, 12, 1, size); // center
        sm.loadAnimation("FIRE_1", sheet, 13, 1, size); // end down
        sm.loadAnimation("FIRE_2", sheet, 14, 1, size); // central left
        sm.loadAnimation("FIRE_3", sheet, 15, 1, size); // central right
        sm.loadAnimation("FIRE_4", sheet, 16, 1, size); // central up
        sm.loadAnimation("FIRE_5", sheet, 17, 1, size); // central down
        sm.loadAnimation("FIRE_6", sheet, 18, 1, size); // end left
        sm.loadAnimation("FIRE_7", sheet, 19, 1, size); // end right
        sm.loadAnimation("FIRE_8", sheet, 20, 1, size); // end up
    }

    private void loadShooterGoblinAnimations(SpriteManager sm) {
        String sheet = Config.SHOOTERGOBLIN_SHEET;
        int size = Config.ENTITY_FRAME_SIZE;

        // facing up
        sm.loadAnimation("SHOOTER_ATTACK_UP",   sheet, Config.SHOOTER_ATTACK_BACK_START,  Config.SHOOTER_ATTACK_FRAMES, size);
        sm.loadAnimation("SHOOTER_IDLE_UP",     sheet, Config.SHOOTER_IDLE_BACK_START,    Config.GOBLIN_IDLE_FRAMES,    size);
        sm.loadAnimation("SHOOTER_RUN_UP",      sheet, Config.SHOOTER_RUN_BACK_START,     Config.GOBLIN_RUN_FRAMES,     size);

        // facing down
        sm.loadAnimation("SHOOTER_ATTACK_DOWN", sheet, Config.SHOOTER_ATTACK_FRONT_START, Config.SHOOTER_ATTACK_FRAMES, size);
        sm.loadAnimation("SHOOTER_IDLE_DOWN",   sheet, Config.SHOOTER_IDLE_FRONT_START,   Config.GOBLIN_IDLE_FRAMES,    size);
        sm.loadAnimation("SHOOTER_RUN_DOWN",    sheet, Config.SHOOTER_RUN_FRONT_START,    Config.GOBLIN_RUN_FRAMES,     size);

        // facing left
        sm.loadAnimation("SHOOTER_ATTACK_LEFT", sheet, Config.SHOOTER_ATTACK_LEFT_START,  Config.SHOOTER_ATTACK_FRAMES, size);
        sm.loadAnimation("SHOOTER_IDLE_LEFT",   sheet, Config.SHOOTER_IDLE_LEFT_START,    Config.GOBLIN_IDLE_FRAMES,    size);
        sm.loadAnimation("SHOOTER_RUN_LEFT",    sheet, Config.SHOOTER_RUN_LEFT_START,     Config.GOBLIN_RUN_FRAMES,     size);

        // facing right
        sm.loadAnimation("SHOOTER_ATTACK_RIGHT", sheet, Config.SHOOTER_ATTACK_RIGHT_START, Config.SHOOTER_ATTACK_FRAMES, size);
        sm.loadAnimation("SHOOTER_IDLE_RIGHT",   sheet, Config.SHOOTER_IDLE_RIGHT_START,   Config.GOBLIN_IDLE_FRAMES,    size);
        sm.loadAnimation("SHOOTER_RUN_RIGHT",    sheet, Config.SHOOTER_RUN_RIGHT_START,    Config.GOBLIN_RUN_FRAMES,     size);
    }

    private void loadCommonGoblinAnimations(SpriteManager sm) {
        String sheet = Config.COMMON_GOBLIN;
        int size = Config.ENTITY_FRAME_SIZE;
        int runFrames = Config.GOBLIN_RUN_FRAMES;

        // facing up
        sm.loadAnimation("COMMON_RUN_UP",     sheet, Config.COMMON_RUN_BACK_START,   runFrames, size);
        sm.loadAnimation("COMMON_IDLE_UP",    sheet, Config.COMMON_RUN_BACK_START,   1, size); // fallback

        // facing down
        sm.loadAnimation("COMMON_RUN_DOWN",   sheet, Config.COMMON_RUN_FRONT_START,  runFrames, size);
        sm.loadAnimation("COMMON_IDLE_DOWN",  sheet, Config.COMMON_RUN_FRONT_START,  1, size); // fallback

        // facing left
        sm.loadAnimation("COMMON_RUN_LEFT",   sheet, Config.COMMON_RUN_LEFT_START,   runFrames, size);
        sm.loadAnimation("COMMON_IDLE_LEFT",  sheet, Config.COMMON_RUN_LEFT_START,   1, size); // fallback

        // facing right
        sm.loadAnimation("COMMON_RUN_RIGHT",  sheet, Config.COMMON_RUN_RIGHT_START,  runFrames, size);
        sm.loadAnimation("COMMON_IDLE_RIGHT", sheet, Config.COMMON_RUN_RIGHT_START,  1, size); // fallback
    }

    private void loadChasingGoblinAnimations(SpriteManager sm) {
        String sheet = Config.CHASING_GOBLIN_SHEET;
        int size = Config.ENTITY_FRAME_SIZE;
        int idleFrames = Config.GOBLIN_IDLE_FRAMES;
        int runFrames  = Config.GOBLIN_RUN_FRAMES;

        // facing up
        sm.loadAnimation("HUNTER_IDLE_UP",    sheet, Config.CHASING_IDLE_BACK_START,  idleFrames, size);
        sm.loadAnimation("HUNTER_RUN_UP",     sheet, Config.CHASING_RUN_BACK_START,   runFrames,  size);

        // facing down
        sm.loadAnimation("HUNTER_IDLE_DOWN",  sheet, Config.CHASING_IDLE_FRONT_START, idleFrames, size);
        sm.loadAnimation("HUNTER_RUN_DOWN",   sheet, Config.CHASING_RUN_FRONT_START,  runFrames,  size);

        // facing left
        sm.loadAnimation("HUNTER_IDLE_LEFT",  sheet, Config.CHASING_IDLE_LEFT_START,  idleFrames, size);
        sm.loadAnimation("HUNTER_RUN_LEFT",   sheet, Config.CHASING_RUN_LEFT_START,   runFrames,  size);

        // facing right
        sm.loadAnimation("HUNTER_IDLE_RIGHT", sheet, Config.CHASING_IDLE_RIGHT_START, idleFrames, size);
        sm.loadAnimation("HUNTER_RUN_RIGHT",  sheet, Config.CHASING_RUN_RIGHT_START,  runFrames,  size);
    }

    private void loadBossGoblinAnimations(SpriteManager sm) {
        String sheet = Config.BOSS_GOBLIN_SHEET;
        int size = Config.BOSS_FRAME_SIZE;

        // facing up
        sm.loadAnimation("BOSS_ATTACK_UP",   sheet, Config.BOSS_ATTACK_BACK_START, Config.BOSS_ATTACK_FRAMES, size);
        sm.loadAnimation("BOSS_IDLE_UP",     sheet, Config.BOSS_IDLE_BACK_START,   Config.BOSS_IDLE_FRAMES,   size);
        sm.loadAnimation("BOSS_RUN_UP",      sheet, Config.BOSS_RUN_BACK_START,    Config.BOSS_RUN_FRAMES,    size);

        // direction-independent dying animation
        sm.loadAnimation("BOSS_DYING",       sheet, Config.BOSS_DYING_START,       Config.BOSS_DYING_FRAMES,  size);

        // facing down
        sm.loadAnimation("BOSS_ATTACK_DOWN", sheet, Config.BOSS_ATTACK_FRONT_START, Config.BOSS_ATTACK_FRAMES, size);
        sm.loadAnimation("BOSS_IDLE_DOWN",   sheet, Config.BOSS_IDLE_FRONT_START,   Config.BOSS_IDLE_FRAMES,   size);
        sm.loadAnimation("BOSS_RUN_DOWN",    sheet, Config.BOSS_RUN_FRONT_START,    Config.BOSS_RUN_FRAMES,    size);

        // facing left
        sm.loadAnimation("BOSS_ATTACK_LEFT", sheet, Config.BOSS_ATTACK_LEFT_START, Config.BOSS_ATTACK_FRAMES, size);
        sm.loadAnimation("BOSS_IDLE_LEFT",   sheet, Config.BOSS_IDLE_LEFT_START,   Config.BOSS_IDLE_FRAMES,   size);
        sm.loadAnimation("BOSS_RUN_LEFT",    sheet, Config.BOSS_RUN_LEFT_START,    Config.BOSS_RUN_FRAMES,    size);

        // facing right
        sm.loadAnimation("BOSS_ATTACK_RIGHT", sheet, Config.BOSS_ATTACK_RIGHT_START, Config.BOSS_ATTACK_FRAMES, size);
        sm.loadAnimation("BOSS_IDLE_RIGHT",   sheet, Config.BOSS_IDLE_RIGHT_START,   Config.BOSS_IDLE_FRAMES,   size);
        sm.loadAnimation("BOSS_RUN_RIGHT",    sheet, Config.BOSS_RUN_RIGHT_START,    Config.BOSS_RUN_FRAMES,    size);
    }

    private void loadProjectiles(SpriteManager sm) {
        String sheet = Config.ITEM_SHEET;
        int size = 64;

        // bone projectiles (enemy)
        sm.loadAnimation("BONE_DOWN",  sheet, Config.BONE_DOWN_INDEX,  1, size);
        sm.loadAnimation("BONE_LEFT",  sheet, Config.BONE_LEFT_INDEX,  1, size);
        sm.loadAnimation("BONE_RIGHT", sheet, Config.BONE_RIGHT_INDEX, 1, size);
        sm.loadAnimation("BONE_UP",    sheet, Config.BONE_UP_INDEX,    1, size);

        // aura projectiles (player) — 12-frame animations
        sm.loadAnimation("AURA_LEFT",  sheet, Config.AURA_LEFT_START,  Config.AURA_FRAMES, size);
        sm.loadAnimation("AURA_RIGHT", sheet, Config.AURA_RIGHT_START, Config.AURA_FRAMES, size);
        sm.loadAnimation("AURA_DOWN",  sheet, Config.AURA_DOWN_START,  Config.AURA_FRAMES, size);
        sm.loadAnimation("AURA_UP",    sheet, Config.AURA_UP_START,    Config.AURA_FRAMES, size);
    }

    private void loadMapThemes(SpriteManager sm) {
        loadVillageTheme(sm);
        loadForestTheme(sm);
        loadCaveTheme(sm);
    }

    private void loadVillageTheme(SpriteManager sm) {
        int size = Config.TILE_SIZE;
        BufferedImage[] tiles = new BufferedImage[Config.THEME_FRAME_INDEX + 1];

        tiles[Config.CELL_EMPTY]               = sm.extractTile(Config.VILLAGE_SHEET, Config.VILLAGE_FLOOR_COL,    Config.VILLAGE_ROW, size, size);
        tiles[Config.CELL_INDESTRUCTIBLE_BLOCK] = sm.extractTile(Config.VILLAGE_SHEET, Config.VILLAGE_WALL_IND_COL,  Config.VILLAGE_ROW, size, size);
        tiles[Config.CELL_DESTRUCTIBLE_BLOCK]   = sm.extractTile(Config.VILLAGE_SHEET, Config.VILLAGE_WALL_DEST_COL, Config.VILLAGE_ROW, size, size);
        tiles[Config.CELL_ORNAMENT]             = sm.extractTile(Config.ORNAMENTS_SHEET, 1, 0, 128, 128); // tower (2nd sprite)
        tiles[Config.THEME_FRAME_INDEX]         = ResourceManager.loadImage(Config.VILLAGE_FRAME);
        TileManager.getInstance().loadTheme("VILLAGE", tiles);
    }

    private void loadForestTheme(SpriteManager sm) {
        int size = Config.TILE_SIZE;
        BufferedImage[] tiles = new BufferedImage[Config.THEME_FRAME_INDEX + 1];

        tiles[Config.CELL_EMPTY]               = sm.extractTile(Config.FOREST_SHEET, Config.FOREST_FLOOR_COL,    Config.FOREST_ROW, size, size);
        tiles[Config.CELL_INDESTRUCTIBLE_BLOCK] = sm.extractTile(Config.FOREST_SHEET, Config.FOREST_WALL_IND_COL,  Config.FOREST_ROW, size, size);
        tiles[Config.CELL_DESTRUCTIBLE_BLOCK]   = sm.extractTile(Config.FOREST_SHEET, Config.FOREST_WALL_DEST_COL, Config.FOREST_ROW, size, size);
        tiles[Config.CELL_ORNAMENT]             = sm.extractTile(Config.ORNAMENTS_SHEET, 0, 0, 128, 128); // giant tree (1st sprite)
        tiles[Config.THEME_FRAME_INDEX]         = ResourceManager.loadImage(Config.FOREST_FRAME);
        TileManager.getInstance().loadTheme("FOREST", tiles);
    }

    private void loadCaveTheme(SpriteManager sm) {
        int size  = Config.TILE_SIZE;
        int bSize = Config.CAVE_BUILDING_SIZE;

        BufferedImage[] tiles = new BufferedImage[Config.THEME_FRAME_INDEX + 1];

        tiles[Config.CELL_EMPTY]               = sm.extractTile(Config.CAVE_SHEET, Config.CAVE_FLOOR_COL,       Config.CAVE_ROW, size, size);
        tiles[Config.CELL_INDESTRUCTIBLE_BLOCK] = sm.extractTile(Config.CAVE_SHEET, Config.CAVE_WALL_IND_COL,   Config.CAVE_ROW, size, size);
        tiles[Config.CELL_DESTRUCTIBLE_BLOCK]   = sm.extractTile(Config.CAVE_SHEET, Config.CAVE_WALL_DEST_COL,  Config.CAVE_ROW, size, size);
        tiles[Config.CELL_CRACKED_FLOOR]        = sm.extractTile(Config.CAVE_SHEET, Config.CAVE_CRACKED_FLOOR_COL, Config.CAVE_ROW, size, size);
        tiles[Config.CELL_LAVA_FLOOR]           = sm.extractTile(Config.CAVE_SHEET, Config.CAVE_LAVA_FLOOR_COL, Config.CAVE_ROW, size, size);

        sm.loadAnimation("CAVE_BUILDING", Config.CAVE_SKELETON_SHEET, 0, Config.SKELETON_FRAMES_COUNT, bSize);
        for (int i = 0; i < Config.SKELETON_FRAMES_COUNT; i++) {
            tiles[ViewConfig.CELL_SKELETON_START + i] = sm.getSprite("CAVE_BUILDING", i);
        }

        tiles[Config.THEME_FRAME_INDEX] = ResourceManager.loadImage(Config.CAVE_FRAME);
        TileManager.getInstance().loadTheme("CAVE", tiles);
    }

    private void loadPortalAnimation(SpriteManager sm) {
        sm.loadAnimation("PORTAL_ANIM", Config.ITEM_SHEET, 69, 6, 64);
    }

    private void loadPowerUps(SpriteManager sm) {
        sm.loadAnimation("POWER_UPS", Config.ITEM_SHEET, 75, 3, 64);
    }

    private void loadConsumables(SpriteManager sm) {
        sm.loadAnimation("CONSUMABLES", Config.ITEM_SHEET, 78, 2, 64);
    }

    private void loadHUDIcons(SpriteManager sm) {
        sm.loadSingleImage("ARCADE_CABINET", "/CabinetArcade.png");
        sm.loadAnimation("HUD_FIRE_SPELL", Config.ITEM_SHEET, 0,  1, 64);
        sm.loadAnimation("HUD_AURA_SPELL", Config.ITEM_SHEET, 33, 1, 64);
        sm.loadSingleImage("STAFF_ICON", "/staff_icon.png");
    }

    /**
     * Pre-builds grayscale versions of all HUD sprites.
     * Called once at the end of resource loading — never per frame.
     * Key convention: "CONSUMABLES_0_gray", "POWER_UPS_2_gray", etc.
     */
    private void buildGrayscaleHudIcons(SpriteManager sm) {
        sm.buildGrayscale("HUD_FIRE_SPELL", 0, "HUD_FIRE_SPELL_gray");
        sm.buildGrayscale("HUD_AURA_SPELL", 0, "HUD_AURA_SPELL_gray");
        sm.buildGrayscale("CONSUMABLES", 0, "CONSUMABLES_0_gray");
        sm.buildGrayscale("CONSUMABLES", 1, "CONSUMABLES_1_gray");
        // power-ups: frame 0 = shield, frame 1 = radius, frame 2 = speed
        sm.buildGrayscale("POWER_UPS", 0, "POWER_UPS_0_gray");
        sm.buildGrayscale("POWER_UPS", 1, "POWER_UPS_1_gray");
        sm.buildGrayscale("POWER_UPS", 2, "POWER_UPS_2_gray");
    }

    private void loadMenuResources(SpriteManager sm) {
        sm.loadSingleImage("MENU_BG", ViewConfig.START_GAME_BG);
    }

    /**
     * Reloads player animations with a different sprite sheet.
     * Called by the Controller after the player selects a character.
     * Overwrites previously loaded PlayerState.* animations.
     *
     * @param sheetPath path to the selected sprite sheet
     */
    public static void reloadPlayerAnimations(String sheetPath) {
        ResourceLoader loader = new ResourceLoader();
        loader.loadPlayerAnimations(SpriteManager.getInstance(), sheetPath);
    }
}
