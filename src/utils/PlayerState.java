package utils;

public enum PlayerState {
        // --- FRONTE (Giù) ---
        IDLE_FRONT,
        RUN_FRONT,
        ATTACK_FRONT,
        HURT_FRONT,
        CAST_FRONT,

        // --- RETRO (Su) ---
        IDLE_BACK,
        RUN_BACK,
        ATTACK_BACK,
        HURT_BACK,
        CAST_BACK,

        // --- SINISTRA ---
        IDLE_LEFT,
        RUN_LEFT,
        ATTACK_LEFT,
        HURT_LEFT,
        CAST_LEFT,

        // --- DESTRA ---
        IDLE_RIGHT,
        RUN_RIGHT,
        ATTACK_RIGHT,
        HURT_RIGHT,
        CAST_RIGHT,

        // --- STATI SPECIALI ---
        DYING,
        DEAD;

}
