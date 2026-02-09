package view;

import utils.Config;
import utils.PlayerState;

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




    }

    private void loadPlayerAnimations(SpriteManager sm) {
        String sheet = Config.PLAYER1_SHEET;
        int size = Config.PLAYER_FRAME_SIZE;

        // --- FRONTE (Giù) -> FRONT ---
        // Nel file PNG le animazioni frontali partono dalla riga 5 (Indici 58+)
        sm.loadAnimation(PlayerState.ATTACK_FRONT, sheet, 58, 10, size);
        sm.loadAnimation(PlayerState.HURT_FRONT, sheet, 68, 10, size);
        sm.loadAnimation(PlayerState.IDLE_FRONT, sheet, 78, 16, size);
        sm.loadAnimation(PlayerState.RUN_FRONT, sheet, 94, 12, size);

        // --- RETRO (Su) -> BACK ---
        // Nel file PNG le animazioni posteriori sono le prime in alto (Indici 0+)
        sm.loadAnimation(PlayerState.ATTACK_BACK, sheet, 0, 10, size);
        sm.loadAnimation(PlayerState.HURT_BACK, sheet, 10, 10, size);
        sm.loadAnimation(PlayerState.IDLE_BACK, sheet, 20, 16, size);
        sm.loadAnimation(PlayerState.RUN_BACK, sheet, 36, 12, size);

        // --- SINISTRA -> LEFT ---
        sm.loadAnimation(PlayerState.ATTACK_LEFT, sheet, 106, 10, size);
        sm.loadAnimation(PlayerState.HURT_LEFT, sheet, 116, 10, size);
        sm.loadAnimation(PlayerState.IDLE_LEFT, sheet, 126, 16, size);
        sm.loadAnimation(PlayerState.RUN_LEFT, sheet, 142, 12, size);

        // --- DESTRA -> RIGHT ---
        sm.loadAnimation(PlayerState.ATTACK_RIGHT, sheet, 154, 10, size);
        sm.loadAnimation(PlayerState.HURT_RIGHT, sheet, 164, 10, size);
        sm.loadAnimation(PlayerState.IDLE_RIGHT, sheet, 174, 16, size);
        sm.loadAnimation(PlayerState.RUN_RIGHT, sheet, 190, 12, size);

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
                Config.MAIN_SHEET,             // File (/MapItems.png)
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
        sm.loadAnimation("FIRE_0", sheet, 24, 1, size); // Center
        sm.loadAnimation("FIRE_1", sheet, 25, 1, size); // End Down (2nd)
        sm.loadAnimation("FIRE_2", sheet, 26, 1, size); // Central Left (3rd)
        sm.loadAnimation("FIRE_3", sheet, 27, 1, size); // Central Right (4th)
        sm.loadAnimation("FIRE_4", sheet, 28, 1, size); // Central Up (5th)
        sm.loadAnimation("FIRE_5", sheet, 29, 1, size); // Central Down (6th)
        sm.loadAnimation("FIRE_6", sheet, 30, 1, size); // End Left (7th)
        sm.loadAnimation("FIRE_7", sheet, 31, 1, size); // End Right (8th)
        sm.loadAnimation("FIRE_8", sheet, 32, 1, size); // End Up (9th - Row 3 Col 0)
    }
}

