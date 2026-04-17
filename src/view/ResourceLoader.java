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
        loadBossGoblinAnimations(sm);
        loadProjectiles(sm);
        loadMapThemes(sm);
        loadPortalAnimation(sm);
        loadPowerUps(sm);
        loadConsumables(sm);
        loadHUDIcons(sm);

        // --- Grayscale HUD: genera una sola volta le versioni "non raccolto" ---
        buildGrayscaleHudIcons(sm);
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
    private void loadBossGoblinAnimations(SpriteManager sm) {
        String sheet = Config.BOSS_GOBLIN_SHEET;
        int size = Config.BOSS_FRAME_SIZE; // Dimensione specifica dei frame del Boss

        // --- BACK (SU) ---
        sm.loadAnimation("BOSS_ATTACK_UP",   sheet, Config.BOSS_ATTACK_BACK_START, Config.BOSS_ATTACK_FRAMES, size);
        sm.loadAnimation("BOSS_IDLE_UP",     sheet, Config.BOSS_IDLE_BACK_START,   Config.BOSS_IDLE_FRAMES,   size);
        sm.loadAnimation("BOSS_RUN_UP",      sheet, Config.BOSS_RUN_BACK_START,    Config.BOSS_RUN_FRAMES,    size);

        // --- DYING (MORTE) ---
        // Aggiunta specifica per il boss, indipendente dalla direzione
        sm.loadAnimation("BOSS_DYING",       sheet, Config.BOSS_DYING_START,       Config.BOSS_DYING_FRAMES,  size);

        // --- FRONT (GIÙ) ---
        sm.loadAnimation("BOSS_ATTACK_DOWN", sheet, Config.BOSS_ATTACK_FRONT_START, Config.BOSS_ATTACK_FRAMES, size);
        sm.loadAnimation("BOSS_IDLE_DOWN",   sheet, Config.BOSS_IDLE_FRONT_START,   Config.BOSS_IDLE_FRAMES,   size);
        sm.loadAnimation("BOSS_RUN_DOWN",    sheet, Config.BOSS_RUN_FRONT_START,    Config.BOSS_RUN_FRAMES,    size);

        // --- LEFT (SINISTRA) ---
        sm.loadAnimation("BOSS_ATTACK_LEFT", sheet, Config.BOSS_ATTACK_LEFT_START, Config.BOSS_ATTACK_FRAMES, size);
        sm.loadAnimation("BOSS_IDLE_LEFT",   sheet, Config.BOSS_IDLE_LEFT_START,   Config.BOSS_IDLE_FRAMES,   size);
        sm.loadAnimation("BOSS_RUN_LEFT",    sheet, Config.BOSS_RUN_LEFT_START,    Config.BOSS_RUN_FRAMES,    size);

        // --- RIGHT (DESTRA) ---
        sm.loadAnimation("BOSS_ATTACK_RIGHT", sheet, Config.BOSS_ATTACK_RIGHT_START, Config.BOSS_ATTACK_FRAMES, size);
        sm.loadAnimation("BOSS_IDLE_RIGHT",   sheet, Config.BOSS_IDLE_RIGHT_START,   Config.BOSS_IDLE_FRAMES,   size);
        sm.loadAnimation("BOSS_RUN_RIGHT",    sheet, Config.BOSS_RUN_RIGHT_START,    Config.BOSS_RUN_FRAMES,    size);
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

    // ---------------------------------------------------------
    // 1. TEMA VILLAGGIO
    // ---------------------------------------------------------
    private void loadVillageTheme(SpriteManager sm) {
        int size = utils.Config.TILE_SIZE;
        BufferedImage[] villageTiles = new BufferedImage[utils.Config.THEME_FRAME_INDEX + 1];

        villageTiles[utils.Config.CELL_EMPTY] = sm.extractTile(Config.VILLAGE_SHEET, utils.Config.VILLAGE_FLOOR_COL, utils.Config.VILLAGE_ROW, size, size);
        villageTiles[utils.Config.CELL_INDESTRUCTIBLE_BLOCK] = sm.extractTile(utils.Config.VILLAGE_SHEET, utils.Config.VILLAGE_WALL_IND_COL, utils.Config.VILLAGE_ROW, size, size);
        villageTiles[utils.Config.CELL_DESTRUCTIBLE_BLOCK] = sm.extractTile(utils.Config.VILLAGE_SHEET, utils.Config.VILLAGE_WALL_DEST_COL, utils.Config.VILLAGE_ROW, size, size);

        // Ornamento: Torre (128x128) - Seconda immagine nello sheet Ornaments
        villageTiles[utils.Config.CELL_ORNAMENT] = sm.extractTile(utils.Config.ORNAMENTS_SHEET, 1, 0, 128, 128);

        villageTiles[utils.Config.THEME_FRAME_INDEX] = view.ResourceManager.loadImage(utils.Config.VILLAGE_FRAME );
        TileManager.getInstance().loadTheme("VILLAGE", villageTiles);
    }

    // ---------------------------------------------------------
    // 2. TEMA FORESTA
    // ---------------------------------------------------------
    private void loadForestTheme(SpriteManager sm) {
        int size = utils.Config.TILE_SIZE;
        BufferedImage[] forestTiles = new BufferedImage[utils.Config.THEME_FRAME_INDEX + 1];

        forestTiles[utils.Config.CELL_EMPTY] = sm.extractTile(utils.Config.FOREST_SHEET, utils.Config.FOREST_FLOOR_COL, utils.Config.FOREST_ROW, size, size);
        forestTiles[utils.Config.CELL_INDESTRUCTIBLE_BLOCK] = sm.extractTile(utils.Config.FOREST_SHEET, utils.Config.FOREST_WALL_IND_COL, utils.Config.FOREST_ROW, size, size);
        forestTiles[utils.Config.CELL_DESTRUCTIBLE_BLOCK] = sm.extractTile(utils.Config.FOREST_SHEET, utils.Config.FOREST_WALL_DEST_COL, utils.Config.FOREST_ROW, size, size);

        // Ornamento: Albero Gigante (128x128) - Prima immagine nello sheet Ornaments
        forestTiles[utils.Config.CELL_ORNAMENT] = sm.extractTile(utils.Config.ORNAMENTS_SHEET, 0, 0, 128, 128);

        forestTiles[utils.Config.THEME_FRAME_INDEX] = view.ResourceManager.loadImage(utils.Config.FOREST_FRAME );
        TileManager.getInstance().loadTheme("FOREST", forestTiles);
    }

    // ---------------------------------------------------------
    // 3. TEMA CAVERNA
    // ---------------------------------------------------------
    private void loadCaveTheme(SpriteManager sm) {
        int size = utils.Config.TILE_SIZE;
        int bSize = utils.Config.CAVE_BUILDING_SIZE; // 128 pixel per l'edificio gigante

        // Array del tema (fino all'indice 30 per la cornice)
        BufferedImage[] caveTiles = new BufferedImage[utils.Config.THEME_FRAME_INDEX + 1];

        // --- A. CARICAMENTO BLOCCHI BASE (0-4) ---
        caveTiles[utils.Config.CELL_EMPTY] = sm.extractTile(utils.Config.CAVE_SHEET , utils.Config.CAVE_FLOOR_COL, utils.Config.CAVE_ROW, size, size);
        caveTiles[utils.Config.CELL_INDESTRUCTIBLE_BLOCK] = sm.extractTile(utils.Config.CAVE_SHEET , utils.Config.CAVE_WALL_IND_COL, utils.Config.CAVE_ROW, size, size);
        caveTiles[utils.Config.CELL_DESTRUCTIBLE_BLOCK] = sm.extractTile(utils.Config.CAVE_SHEET , utils.Config.CAVE_WALL_DEST_COL, utils.Config.CAVE_ROW, size, size);
        caveTiles[utils.Config.CELL_CRACKED_FLOOR] = sm.extractTile(utils.Config.CAVE_SHEET , utils.Config.CAVE_CRACKED_FLOOR_COL, utils.Config.CAVE_ROW, size, size);
        caveTiles[utils.Config.CELL_LAVA_FLOOR] = sm.extractTile(utils.Config.CAVE_SHEET , utils.Config.CAVE_LAVA_FLOOR_COL, utils.Config.CAVE_ROW, size, size);

        // In src/view/ResourceLoader.java -> loadCaveTheme()
        // --- B. CARICAMENTO EDIFICIO ANIMATO ---
        sm.loadAnimation("CAVE_BUILDING", utils.Config.CAVE_SKELETON_SHEET, 0, utils.Config.SKELETON_FRAMES_COUNT, bSize);

        for (int i = 0; i < utils.Config.SKELETON_FRAMES_COUNT; i++) {
            // Mettiamo i frame a partire dall'indice 5 (CELL_SKELETON_START)
            caveTiles[utils.Config.CELL_SKELETON_START + i] = sm.getSprite("CAVE_BUILDING", i);
        }

        // --- C. CORNICE (30) ---
        caveTiles[utils.Config.THEME_FRAME_INDEX] = ResourceManager.loadImage(utils.Config.CAVE_FRAME );

        // Salviamo tutto nel TileManager
        TileManager.getInstance().loadTheme("CAVE", caveTiles);
    }
    private void loadPortalAnimation(SpriteManager sm) {
        sm.loadAnimation(
                "PORTAL_ANIM",                // Chiave univoca per l'animazione del portale
                Config.ITEM_SHEET,            // File (/Items.png)
                69,                           // Indice lineare di partenza
                6,                            // Quanti frame caricare (56, 57, 58, 59)
                64                            // Dimensione Tile in pixel
        );
        System.out.println("ResourceLoader: Animazione Portale caricata (Frame 56-59).");
    }

    private void loadPowerUps(SpriteManager sm) {
        sm.loadAnimation(
                "POWER_UPS",                  // Chiave univoca (indici 0, 1, 2)
                Config.ITEM_SHEET,            // File (/Items.png)
                75,                           // Indice lineare di partenza
                3,                            // Quanti frame caricare (60, 61, 62)
                64                            // Dimensione Tile in pixel
        );
        System.out.println("ResourceLoader: Power Ups caricati (Frame 60-62).");
    }

    private void loadConsumables(SpriteManager sm) {
        sm.loadAnimation(
                "CONSUMABLES",                // Chiave univoca (indici 0, 1)
                Config.ITEM_SHEET,            // File (/Items.png)
                78,                           // Indice lineare di partenza
                2,                            // Quanti frame caricare (63, 64)
                64                            // Dimensione Tile in pixel
        );
        System.out.println("ResourceLoader: Consumabili caricati (Frame 63-64).");
    }

    private void loadHUDIcons(SpriteManager sm) {
        // Sfondo Cabinato (JFrame Background) collegato globalmente
        sm.loadSingleImage("ARCADE_CABINET", "/cabinet arcade.png");

        // L'utente ha chiesto: Fire Spell prima tile = indice 0
        sm.loadAnimation("HUD_FIRE_SPELL", Config.ITEM_SHEET, 0, 1, 64);
        
        // Aura Spell tile 33
        sm.loadAnimation("HUD_AURA_SPELL", Config.ITEM_SHEET, 33, 1, 64);
        
        // Immagine del bastone
        sm.loadSingleImage("STAFF_ICON", "/staff_icon.png");
        
        System.out.println("ResourceLoader: Icone HUD e Menu aggiornate (Fire, Aura, Staff).");
    }

    /**
     * Pre-genera le versioni in scala di grigi di tutti gli sprite HUD.
     * Chiamato UNA SOLA VOLTA al termine del caricamento normali risorse.
     * Convenzione chiave: "CONSUMABLES_0_gray", "POWER_UPS_2_gray", ecc.
     */
    private void buildGrayscaleHudIcons(SpriteManager sm) {
        // Icone aggiornate HUD
        sm.buildGrayscale("HUD_FIRE_SPELL", 0, "HUD_FIRE_SPELL_gray");
        sm.buildGrayscale("HUD_AURA_SPELL", 0, "HUD_AURA_SPELL_gray");
        
        // Consumabili map drops (eredità vecchio sistema)
        sm.buildGrayscale("CONSUMABLES", 0, "CONSUMABLES_0_gray");
        sm.buildGrayscale("CONSUMABLES", 1, "CONSUMABLES_1_gray");
        // Power-up:    frame 0 = scudo,  frame 1 = raggio, frame 2 = velocità
        sm.buildGrayscale("POWER_UPS",   0, "POWER_UPS_0_gray");
        sm.buildGrayscale("POWER_UPS",   1, "POWER_UPS_1_gray");
        sm.buildGrayscale("POWER_UPS",   2, "POWER_UPS_2_gray");
        System.out.println("ResourceLoader: Icone HUD grayscale generate.");
    }

}
