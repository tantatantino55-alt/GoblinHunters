package goblinhunter.utils;

public enum PlayerState {

    // facing down
    IDLE_FRONT,
    RUN_FRONT,
    ATTACK_FRONT,
    HURT_FRONT,
    CAST_FRONT,

    // facing up
    IDLE_BACK,
    RUN_BACK,
    ATTACK_BACK,
    HURT_BACK,
    CAST_BACK,

    // facing left
    IDLE_LEFT,
    RUN_LEFT,
    ATTACK_LEFT,
    HURT_LEFT,
    CAST_LEFT,

    // facing right
    IDLE_RIGHT,
    RUN_RIGHT,
    ATTACK_RIGHT,
    HURT_RIGHT,
    CAST_RIGHT,

    // special states
    DYING,
    DEAD;
}
