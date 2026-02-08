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

        // Carichiamo le animazioni del Player
        loadPlayerAnimations(sm);

        // In futuro qui potrai aggiungere:
        // loadEnemyAnimations(sm);
        // loadItemSprites(sm);
    }

    private void loadPlayerAnimations(SpriteManager sm) {
        String sheet = Config.PLAYER1_SHEET;
        int size = Config.PLAYER_FRAME_SIZE;

        // --- FRONTE (Giù) -> FRONT ---
        // Nel file PNG le animazioni frontali partono dalla riga 5 (Indici 58+)
        sm.loadAnimation(PlayerState.ATTACK_FRONT, sheet, 58, 10, size);
        sm.loadAnimation(PlayerState.HURT_FRONT,   sheet, 68, 10, size);
        sm.loadAnimation(PlayerState.IDLE_FRONT,   sheet, 78, 16, size);
        sm.loadAnimation(PlayerState.RUN_FRONT,    sheet, 94, 12, size);

        // --- RETRO (Su) -> BACK ---
        // Nel file PNG le animazioni posteriori sono le prime in alto (Indici 0+)
        sm.loadAnimation(PlayerState.ATTACK_BACK, sheet, 0,  10, size);
        sm.loadAnimation(PlayerState.HURT_BACK,   sheet, 10, 10, size);
        sm.loadAnimation(PlayerState.IDLE_BACK,   sheet, 20, 16, size);
        sm.loadAnimation(PlayerState.RUN_BACK,    sheet, 36, 12, size);

        // --- SINISTRA -> LEFT ---
        sm.loadAnimation(PlayerState.ATTACK_LEFT,  sheet, 106, 10, size);
        sm.loadAnimation(PlayerState.HURT_LEFT,    sheet, 116, 10, size);
        sm.loadAnimation(PlayerState.IDLE_LEFT,    sheet, 126, 16, size);
        sm.loadAnimation(PlayerState.RUN_LEFT,     sheet, 142, 12, size);

        // --- DESTRA -> RIGHT ---
        sm.loadAnimation(PlayerState.ATTACK_RIGHT, sheet, 154, 10, size);
        sm.loadAnimation(PlayerState.HURT_RIGHT,   sheet, 164, 10, size);
        sm.loadAnimation(PlayerState.IDLE_RIGHT,   sheet, 174, 16, size);
        sm.loadAnimation(PlayerState.RUN_RIGHT,    sheet, 190, 12, size);

        // --- STATI SPECIALI ---
        sm.loadAnimation(PlayerState.DYING,        sheet, 48, 10, size);
    }
}