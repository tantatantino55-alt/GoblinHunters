package goblinhunters.utils;

/** Game-logic constants (Model / Controller only). No pixel, sprite, or font values here. */
public class LogicConfig {

    // grid dimensions
    public static final int GRID_WIDTH  = 13;
    public static final int GRID_HEIGHT = 11;

    // entity speed and hitbox
    public static final double ENTITY_LOGICAL_SPEED          = 0.04;
    public static final double ENTITY_LOGICAL_HITBOX_WIDTH   = 0.5;
    public static final double ENTITY_LOGICAL_HITBOX_HEIGHT  = 0.3;

    // logical world bounds
    public static final double MIN_LOGICAL_X = 0.0;
    public static final double MAX_LOGICAL_X = (double) GRID_WIDTH  - 1.0;
    public static final double MIN_LOGICAL_Y = 0.0;
    public static final double MAX_LOGICAL_Y = (double) GRID_HEIGHT - 1.0;

    // corner correction / lane centering
    public static final double CORNER_CORRECTION_SPEED = 0.05;
    public static final double CORNER_ALIGN_SPEED      = ENTITY_LOGICAL_SPEED * 1.5;
    public static final double MAGNET_TOLERANCE        = 0.40;
    public static final double CENTER_TOLERANCE        = 0.1;

    // goblin hitbox
    public static final double GOBLIN_HITBOX_WIDTH  = 0.8;
    public static final double GOBLIN_HITBOX_HEIGHT = 0.8;

    // spawn
    public static final long SPAWN_INTERVAL_MS  = 3000;
    public static final int SPAWN_SAFE_DISTANCE = 2;
    public static final double MIN_SPAWN_DISTANCE = 5.0;
    /** Minimum connected open cells required around a spawn point — prevents goblins from spawning inside tiny enclosed pockets. */
    public static final int MIN_SPAWN_OPEN_AREA = 6;

    // shooter goblin
    public static final int    SHOOTER_TELEGRAPH_TIME = 30;
    public static final double SHOOTER_SPEED_AIMING   = 0.0;
    public static final double SHOOTER_SPEED_CHASE    = 0.03;
    public static final int    SHOOTER_MAX_AMMO       = 2;
    public static final int    SHOOTER_RELOAD_TIME    = 180;

    // common goblin
    public static final double GOBLIN_COMMON_SPEED = 0.025;

    // projectile speeds (logical units per tick)
    public static final int AURA_PROJECTILE_SPEED = 2;
    public static final int BONE_PROJECTILE_SPEED = 2;

    // lives and damage
    public static final int  INITIAL_LIVES             = 6;
    public static final long INVINCIBILITY_DURATION_MS = 3000;

    // bombs
    public static final int BOMB_DETONATION_TICKS = 90;
    public static final int INITIAL_MAX_BOMBS     = 1;
    public static final int DEFAULT_BOMB_RADIUS   = 1;

    // aura
    /** Maximum storable aura shots; tune here to adjust difficulty. */
    public static final int MAX_AURA_AMMO = 7;

    // fire
    public static final int FIRE_DURATION_TICKS = 30;

    // cell type codes (used by Model for logic)
    public static final int CELL_EMPTY                = 0;
    public static final int CELL_INDESTRUCTIBLE_BLOCK = 1;
    public static final int CELL_DESTRUCTIBLE_BLOCK   = 2;
    public static final int CELL_CRACKED_FLOOR        = 3;
    public static final int CELL_LAVA_FLOOR           = 4;
    public static final int CELL_ORNAMENT             = 5;

    // score
    public static final int SCORE_CRATE           = 20;
    public static final int SCORE_COMMON_GOBLIN   = 100;
    public static final int SCORE_CHASING_GOBLIN  = 200;
    public static final int SCORE_SHOOTER_GOBLIN  = 300;
    public static final int SCORE_ZONE_CAP        = 1500;
    public static final int SCORE_BOSS_BASE       = 5000;
    public static final int MAX_BOSS_TIME_BONUS   = 5000;
    public static final int BOSS_BONUS_DECAY_PER_SEC = 50;

    // boss portal (zone 2)
    public static final int  BOSS_PORTAL_ROW               = 0;
    public static final int  BOSS_PORTAL_COL               = 6;
    public static final long BOSS_PORTAL_SPAWN_INTERVAL_MS = 14000;
    public static final int  BOSS_PORTAL_MAX_GOBLINS       = 2;
}
