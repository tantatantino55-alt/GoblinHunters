package view;

import utils.Config;
import utils.PlayerState;

import java.awt.image.BufferedImage;

/**
 * Classe dedicata esclusivamente alla configurazione e al caricamento
 * iniziale delle risorse grafiche nel sistema.
 */
public class ResourceLoader {

    public void loadAllResources() {
        SpriteManager sm = SpriteManager.getInstance();


        loadPlayerAnimations(sm);
        loadBombResources(sm);
        loadDestructionAnimations(sm);
        loadFireResources(sm);
        loadShooterGoblinAnimations(sm);
        loadChasingGoblinAnimations(sm);
        loadCommonGoblinAnimations(sm);
        loadProjectiles(sm);
        loadMapThemes(sm);




    }

    private void loadPlayerAnimations(SpriteManager sm) {
        String sheet = Config.PLAYER1_SHEET;
        int size = Config.ENTITY_FRAME_SIZE;

        // --- FRONTE (Giù) -> FRONT ---
        sm.loadAnimation(PlayerState.ATTACK_FRONT, sheet, 58, 10, size);
        sm.loadAnimation(PlayerState.CAST_FRONT,   sheet, 58, 10, size); // Stesse immagini dell'attack!
        sm.loadAnimation(PlayerState.HURT_FRONT,   sheet, 68, 10, size);
        sm.loadAnimation(PlayerState.IDLE_FRONT,   sheet, 78, 16, size);
        sm.loadAnimation(PlayerState.RUN_FRONT,    sheet, 94, 12, size);

        // --- RETRO (Su) -> BACK ---
        sm.loadAnimation(PlayerState.ATTACK_BACK, sheet, 0, 10, size);
        sm.loadAnimation(PlayerState.CAST_BACK,   sheet, 0, 10, size); // Stesse immagini dell'attack!
        sm.loadAnimation(PlayerState.HURT_BACK,   sheet, 10, 10, size);
        sm.loadAnimation(PlayerState.IDLE_BACK,   sheet, 20, 16, size);
        sm.loadAnimation(PlayerState.RUN_BACK,    sheet, 36, 12, size);

        // --- SINISTRA -> LEFT ---
        sm.loadAnimation(PlayerState.ATTACK_LEFT, sheet, 106, 10, size);
        sm.loadAnimation(PlayerState.CAST_LEFT,   sheet, 106, 10, size); // Stesse immagini dell'attack!
        sm.loadAnimation(PlayerState.HURT_LEFT,   sheet, 116, 10, size);
        sm.loadAnimation(PlayerState.IDLE_LEFT,   sheet, 126, 16, size);
        sm.loadAnimation(PlayerState.RUN_LEFT,    sheet, 142, 12, size);

        // --- DESTRA -> RIGHT ---
        sm.loadAnimation(PlayerState.ATTACK_RIGHT, sheet, 154, 10, size);
        sm.loadAnimation(PlayerState.CAST_RIGHT,   sheet, 154, 10, size); // Stesse immagini dell'attack!
        sm.loadAnimation(PlayerState.HURT_RIGHT,   sheet, 164, 10, size);
        sm.loadAnimation(PlayerState.IDLE_RIGHT,   sheet, 174, 16, size);
        sm.loadAnimation(PlayerState.RUN_RIGHT,    sheet, 190, 12, size);

        // --- STATI SPECIALI ---
        sm.loadAnimation(PlayerState.DYING, sheet, 48, 10, size);
    }

    private void loadBombResources(SpriteManager sm) {
        sm.loadAnimation(
                "BOMB_ANIM",                // Chiave univoca
                Config.ITEM_SHEET,   // File (/Consumabili.png)// Colonna Start
                Config.BOMB_SPRITE_START,     // Riga (0)
                Config.BOMB_FRAMES,         // Numero Frame (8)
                64                          // Dimensione (64px)
        );
        System.out.println("ResourceLoader: Risorse Bomba caricate.");
    }
    private void loadDestructionAnimations(SpriteManager sm) {
        sm.loadAnimation(
                "CRATE_BREAK",                // Chiave univoca
                Config.VILLAGE_SHEET,             // File (/MapItems.png)
                Config.DESTRUCTION_START,
                Config.DESTRUCTION_FRAMES,    // Quanti frame caricare (3 -> Col 3, 4, 5)
                64                            // Dimensione Tile
        );
        System.out.println("ResourceLoader: Animazione Distruzione caricata (Frame 3-5).");
    }

    private void loadFireResources(SpriteManager sm) {
        String sheet = Config.ITEM_SHEET;
        int size = 64;
        // Carichiamo i 9 sprite come animazioni da 1 frame ciascuna per semplicità
        sm.loadAnimation("FIRE_0", sheet, 12, 1, size); // Center
        sm.loadAnimation("FIRE_1", sheet, 13, 1, size); // End Down (2nd)
        sm.loadAnimation("FIRE_2", sheet, 14, 1, size); // Central Left (3rd)
        sm.loadAnimation("FIRE_3", sheet, 15, 1, size); // Central Right (4th)
        sm.loadAnimation("FIRE_4", sheet, 16, 1, size); // Central Up (5th)
        sm.loadAnimation("FIRE_5", sheet, 17, 1, size); // Central Down (6th)
        sm.loadAnimation("FIRE_6", sheet, 18, 1, size); // End Left (7th)
        sm.loadAnimation("FIRE_7", sheet, 19, 1, size); // End Right (8th)
        sm.loadAnimation("FIRE_8", sheet, 20, 1, size); // End Up (9th - Row 3 Col 0)
    }
    private void loadShooterGoblinAnimations(SpriteManager sm) {
        String sheet = Config.SHOOTERGOBLIN_SHEET;
        int size = Config.ENTITY_FRAME_SIZE;

        // --- BACK (SU) ---
        sm.loadAnimation("SHOOTER_ATTACK_UP",   sheet, Config.SHOOTER_ATTACK_BACK_START, Config.SHOOTER_ATTACK_FRAMES, size);
        sm.loadAnimation("SHOOTER_IDLE_UP",     sheet, Config.SHOOTER_IDLE_BACK_START,   Config.GOBLIN_IDLE_FRAMES,    size);
        sm.loadAnimation("SHOOTER_RUN_UP",      sheet, Config.SHOOTER_RUN_BACK_START,    Config.GOBLIN_RUN_FRAMES,     size);

        // --- FRONT (GIÙ) ---
        sm.loadAnimation("SHOOTER_ATTACK_DOWN", sheet, Config.SHOOTER_ATTACK_FRONT_START, Config.SHOOTER_ATTACK_FRAMES, size);
        sm.loadAnimation("SHOOTER_IDLE_DOWN",   sheet, Config.SHOOTER_IDLE_FRONT_START,   Config.GOBLIN_IDLE_FRAMES,    size);
        sm.loadAnimation("SHOOTER_RUN_DOWN",    sheet, Config.SHOOTER_RUN_FRONT_START,    Config.GOBLIN_RUN_FRAMES,     size);

        // --- LEFT (SINISTRA) ---
        sm.loadAnimation("SHOOTER_ATTACK_LEFT", sheet, Config.SHOOTER_ATTACK_LEFT_START, Config.SHOOTER_ATTACK_FRAMES, size);
        sm.loadAnimation("SHOOTER_IDLE_LEFT",   sheet, Config.SHOOTER_IDLE_LEFT_START,   Config.GOBLIN_IDLE_FRAMES,    size);
        sm.loadAnimation("SHOOTER_RUN_LEFT",    sheet, Config.SHOOTER_RUN_LEFT_START,    Config.GOBLIN_RUN_FRAMES,     size);

        // --- RIGHT (DESTRA) ---
        sm.loadAnimation("SHOOTER_ATTACK_RIGHT", sheet, Config.SHOOTER_ATTACK_RIGHT_START, Config.SHOOTER_ATTACK_FRAMES, size);
        sm.loadAnimation("SHOOTER_IDLE_RIGHT",   sheet, Config.SHOOTER_IDLE_RIGHT_START,   Config.GOBLIN_IDLE_FRAMES,    size);
        sm.loadAnimation("SHOOTER_RUN_RIGHT",    sheet, Config.SHOOTER_RUN_RIGHT_START,    Config.GOBLIN_RUN_FRAMES,     size);
    }
    private void loadCommonGoblinAnimations(SpriteManager sm) {
        String sheet = Config.COMMON_GOBLIN;
        int size = Config.ENTITY_FRAME_SIZE;
        int runFrames = Config.GOBLIN_RUN_FRAMES;

        // --- BACK ---
        sm.loadAnimation("COMMON_RUN_UP",     sheet, Config.COMMON_RUN_BACK_START,   runFrames, size);
        sm.loadAnimation("COMMON_IDLE_UP",    sheet, Config.COMMON_RUN_BACK_START,   runFrames, size); // Fallback

        // --- FRONT ---
        sm.loadAnimation("COMMON_RUN_DOWN",   sheet, Config.COMMON_RUN_FRONT_START,  runFrames, size);
        sm.loadAnimation("COMMON_IDLE_DOWN",  sheet, Config.COMMON_RUN_FRONT_START,  runFrames, size); // Fallback

        // --- LEFT ---
        sm.loadAnimation("COMMON_RUN_LEFT",   sheet, Config.COMMON_RUN_LEFT_START,   runFrames, size);
        sm.loadAnimation("COMMON_IDLE_LEFT",  sheet, Config.COMMON_RUN_LEFT_START,   runFrames, size); // Fallback

        // --- RIGHT ---
        sm.loadAnimation("COMMON_RUN_RIGHT",  sheet, Config.COMMON_RUN_RIGHT_START,  runFrames, size);
        sm.loadAnimation("COMMON_IDLE_RIGHT", sheet, Config.COMMON_RUN_RIGHT_START,  runFrames, size); // Fallback
    }

    private void loadChasingGoblinAnimations(SpriteManager sm) {
        String sheet = Config.CHASING_GOBLIN_SHEET;
        int size = Config.ENTITY_FRAME_SIZE;
        int idleFrames = Config.GOBLIN_IDLE_FRAMES;
        int runFrames = Config.GOBLIN_RUN_FRAMES;

        // --- BACK ---
        sm.loadAnimation("HUNTER_IDLE_UP",    sheet, Config.CHASING_IDLE_BACK_START,  idleFrames, size);
        sm.loadAnimation("HUNTER_RUN_UP",     sheet, Config.CHASING_RUN_BACK_START,   runFrames,  size);

        // --- FRONT ---
        sm.loadAnimation("HUNTER_IDLE_DOWN",  sheet, Config.CHASING_IDLE_FRONT_START, idleFrames, size);
        sm.loadAnimation("HUNTER_RUN_DOWN",   sheet, Config.CHASING_RUN_FRONT_START,  runFrames,  size);

        // --- LEFT ---
        sm.loadAnimation("HUNTER_IDLE_LEFT",  sheet, Config.CHASING_IDLE_LEFT_START,  idleFrames, size);
        sm.loadAnimation("HUNTER_RUN_LEFT",   sheet, Config.CHASING_RUN_LEFT_START,   runFrames,  size);

        // --- RIGHT ---
        sm.loadAnimation("HUNTER_IDLE_RIGHT", sheet, Config.CHASING_IDLE_RIGHT_START, idleFrames, size);
        sm.loadAnimation("HUNTER_RUN_RIGHT",  sheet, Config.CHASING_RUN_RIGHT_START,  runFrames,  size);
    }
    private void loadProjectiles(SpriteManager sm) {
        String sheet = Config.ITEM_SHEET; // "/Items.png"
        int size = 64;

        // 1. OSSA (Proiettili Goblin)
        sm.loadAnimation("BONE_DOWN",  sheet, Config.BONE_DOWN_INDEX,  1, size);
        sm.loadAnimation("BONE_LEFT",  sheet, Config.BONE_LEFT_INDEX,  1, size);
        sm.loadAnimation("BONE_RIGHT", sheet, Config.BONE_RIGHT_INDEX, 1, size);
        sm.loadAnimation("BONE_UP",    sheet, Config.BONE_UP_INDEX,    1, size);


        // 2. AURA (Proiettili Player) - Animazioni a 12 Frame
        // Carichiamo usando gli START index definiti in Config
        sm.loadAnimation("AURA_LEFT",  sheet, Config.AURA_LEFT_START,  Config.AURA_FRAMES, size);
        sm.loadAnimation("AURA_RIGHT", sheet, Config.AURA_RIGHT_START, Config.AURA_FRAMES, size);
        sm.loadAnimation("AURA_DOWN",  sheet, Config.AURA_DOWN_START,  Config.AURA_FRAMES, size);
        sm.loadAnimation("AURA_UP",    sheet, Config.AURA_UP_START,    Config.AURA_FRAMES, size);

        System.out.println("ResourceLoader: Proiettili Aura caricati (12 frame).");

    }

    private void loadMapThemes(SpriteManager sm) {
        System.out.println("ResourceLoader: Inizio caricamento di tutti i temi mappa...");

        // Chiamiamo i metodi operai uno per uno
        loadVillageTheme(sm);
        loadForestTheme(sm);
        loadCaveTheme(sm);

        System.out.println("ResourceLoader: Tutti i temi mappa caricati con successo!");
    }

    // ==========================================================
    // METODI OPERAI (Logica specifica per ogni singolo tema)
    // ==========================================================

    private void loadVillageTheme(SpriteManager sm) {
        int size = utils.Config.TILE_SIZE;
        BufferedImage[] villageTiles = new BufferedImage[4];

        // 0: Pavimento
        villageTiles[utils.Config.CELL_EMPTY] = sm.extractTile(
                Config.VILLAGE_SHEET, utils.Config.VILLAGE_FLOOR_COL, utils.Config.VILLAGE_ROW, size, size);

        // 1: Muro Indistruttibile
        villageTiles[utils.Config.CELL_INDESTRUCTIBLE_BLOCK] = sm.extractTile(
                Config.VILLAGE_SHEET, utils.Config.VILLAGE_WALL_IND_COL, utils.Config.VILLAGE_ROW, size, size);

        // 2: Muro Distruttibile
        villageTiles[utils.Config.CELL_DESTRUCTIBLE_BLOCK] = sm.extractTile(
                Config.VILLAGE_SHEET, utils.Config.VILLAGE_WALL_DEST_COL, utils.Config.VILLAGE_ROW, size, size);

        // 3: Cornice
        villageTiles[utils.Config.THEME_FRAME_INDEX] = view.ResourceManager.loadImage(utils.Config.VILLAGE_FRAME);

        TileManager.getInstance().loadTheme("VILLAGE", villageTiles);
        System.out.println("  -> Tema 'VILLAGE' caricato.");
    }

    private void loadForestTheme(SpriteManager sm) {
        int size = utils.Config.TILE_SIZE;
        BufferedImage[] forestTiles = new BufferedImage[4];

        // 0: Pavimento
        forestTiles[utils.Config.CELL_EMPTY] = sm.extractTile(
                utils.Config.FOREST_SHEET, utils.Config.FOREST_FLOOR_COL, utils.Config.FOREST_ROW, size, size);

        // 1: Muro Indistruttibile
        forestTiles[utils.Config.CELL_INDESTRUCTIBLE_BLOCK] = sm.extractTile(
                utils.Config.FOREST_SHEET, utils.Config.FOREST_WALL_IND_COL, utils.Config.FOREST_ROW, size, size);

        // 2: Muro Distruttibile
        forestTiles[utils.Config.CELL_DESTRUCTIBLE_BLOCK] = sm.extractTile(
                utils.Config.FOREST_SHEET, utils.Config.FOREST_WALL_DEST_COL, utils.Config.FOREST_ROW, size, size);

        // 3: Cornice
        forestTiles[utils.Config.THEME_FRAME_INDEX] = view.ResourceManager.loadImage(utils.Config.FOREST_FRAME);

        TileManager.getInstance().loadTheme("FOREST", forestTiles);
        System.out.println("  -> Tema 'FOREST' caricato.");
    }

    private void loadCaveTheme(SpriteManager sm) {
        int size = utils.Config.TILE_SIZE;

        // ARRAY PIÙ GRANDE! Adesso è 11 per fare spazio alla cornice all'indice 10
        BufferedImage[] caveTiles = new BufferedImage[11];

        // 0: Pavimento
        caveTiles[utils.Config.CELL_EMPTY] = sm.extractTile(
                utils.Config.CAVE_SHEET, utils.Config.CAVE_FLOOR_COL, utils.Config.CAVE_ROW, size, size);

        // 1: Muro Indistruttibile
        caveTiles[utils.Config.CELL_INDESTRUCTIBLE_BLOCK] = sm.extractTile(
                utils.Config.CAVE_SHEET, utils.Config.CAVE_WALL_IND_COL, utils.Config.CAVE_ROW, size, size);

        // 2: Muro Distruttibile
        caveTiles[utils.Config.CELL_DESTRUCTIBLE_BLOCK] = sm.extractTile(
                utils.Config.CAVE_SHEET, utils.Config.CAVE_WALL_DEST_COL, utils.Config.CAVE_ROW, size, size);

        // ---------------------------------------------------------
        // NUOVI BLOCCHI CAVERNA
        // ---------------------------------------------------------
        // 3: Pavimento con Crepe
        caveTiles[utils.Config.CELL_CRACKED_FLOOR] = sm.extractTile(
                utils.Config.CAVE_SHEET, utils.Config.CAVE_CRACKED_FLOOR_COL, utils.Config.CAVE_ROW, size, size);

        // 4: Pavimento con Lava
        caveTiles[utils.Config.CELL_LAVA_FLOOR] = sm.extractTile(
                utils.Config.CAVE_SHEET, utils.Config.CAVE_LAVA_FLOOR_COL, utils.Config.CAVE_ROW, size, size);

        // ---------------------------------------------------------
        // 10: Cornice
        caveTiles[utils.Config.CAVE_FRAME_INDEX] = view.ResourceManager.loadImage(utils.Config.CAVE_FRAME);

        TileManager.getInstance().loadTheme("CAVE", caveTiles);
        System.out.println("  -> Tema 'CAVE' caricato (con lava e crepe).");
    }


}