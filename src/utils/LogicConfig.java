package utils;

/**
 * Costanti esclusivamente relative alla logica di gioco (Model/Controller):
 * dimensioni griglia, velocità, hitbox, spawn, IA, punteggio, bombe, fuoco.
 * NON deve contenere riferimenti a pixel, sprite, font o risorse grafiche.
 */
public class LogicConfig {

    // --- DIMENSIONI GRIGLIA (LOGICHE) ---
    public static final int GRID_WIDTH  = 13;
    public static final int GRID_HEIGHT = 11;

    // --- VELOCITÀ E HITBOX ENTITÀ ---
    public static final double ENTITY_LOGICAL_SPEED          = 0.05;
    public static final double ENTITY_LOGICAL_HITBOX_WIDTH   = 0.5;
    public static final double ENTITY_LOGICAL_HITBOX_HEIGHT  = 0.3;

    // --- CONFINI MONDO LOGICO ---
    public static final double MIN_LOGICAL_X = 0.0;
    public static final double MAX_LOGICAL_X = (double) GRID_WIDTH  - 1.0;
    public static final double MIN_LOGICAL_Y = 0.0;
    public static final double MAX_LOGICAL_Y = (double) GRID_HEIGHT - 1.0;

    // --- CORNER CORRECTION / LANE CENTERING ---
    public static final double CORNER_CORRECTION_SPEED = 0.05;
    public static final double CORNER_ALIGN_SPEED      = ENTITY_LOGICAL_SPEED * 1.5;
    public static final double MAGNET_TOLERANCE        = 0.40;
    public static final double CENTER_TOLERANCE        = 0.1;

    // --- GOBLIN HITBOX ---
    public static final double GOBLIN_HITBOX_WIDTH  = 0.8;
    public static final double GOBLIN_HITBOX_HEIGHT = 0.8;

    // --- SPAWN ---
    public static final int MAX_ENEMIES_ON_MAP  = 1;
    public static final long SPAWN_INTERVAL_MS  = 3000;
    public static final int SPAWN_SAFE_DISTANCE = 2;
    public static final double MIN_SPAWN_DISTANCE = 5.0;

    // --- AI NEMICI ---
    public static final int SMELL_THRESHOLD_DISTANCE = 6;
    public static final int SMELL_BLOCK_PENALTY      = 3;
    public static final int SAFE_DISTANCE_FROM_BOMB  = 2;

    // --- SHOOTER GOBLIN ---
    public static final int    SHOOTER_TELEGRAPH_TIME = 60;
    public static final double SHOOTER_SPEED_AIMING   = 0.0;
    public static final double SHOOTER_SPEED_CHASE    = 0.03;
    public static final int    SHOOTER_MAX_AMMO       = 2;
    public static final int    SHOOTER_RELOAD_TIME    = 180;

    // --- GOBLIN COMUNE ---
    public static final double GOBLIN_COMMON_SPEED = 0.03;

    // --- PROIETTILI (velocità logica) ---
    public static final int AuraProjectileSpeed = 2;
    public static final int BoneProjectileSpeed = 2;

    // --- VITE E DANNI ---
    public static final int  INITIAL_LIVES             = 99;
    public static final long INVINCIBILITY_DURATION_MS = 3000;

    // --- BOMBE ---
    public static final int BOMB_DETONATION_TICKS = 60;
    public static final int INITIAL_MAX_BOMBS     = 1;
    public static final int DEFAULT_BOMB_RADIUS   = 1;

    // --- FUOCO ---
    public static final int FIRE_DURATION_TICKS = 30;

    // --- TIPI DI CELLA (usati dal Model per la logica) ---
    public static final int CELL_EMPTY               = 0;
    public static final int CELL_INDESTRUCTIBLE_BLOCK = 1;
    public static final int CELL_DESTRUCTIBLE_BLOCK   = 2;
    public static final int CELL_CRACKED_FLOOR        = 3;
    public static final int CELL_LAVA_FLOOR           = 4;
    public static final int CELL_ORNAMENT             = 5;

    // --- PUNTEGGIO ---
    public static final int SCORE_CRATE           = 20;
    public static final int SCORE_COMMON_GOBLIN   = 100;
    public static final int SCORE_CHASING_GOBLIN  = 200;
    public static final int SCORE_SHOOTER_GOBLIN  = 300;
    public static final int SCORE_ZONE_CAP        = 1500;
    public static final int PERFECT_LEVEL_BONUS   = 2000;
    public static final int SCORE_BOSS_BASE       = 5000;
    public static final int MAX_BOSS_TIME_BONUS   = 5000;
    public static final int BOSS_BONUS_DECAY_PER_SEC = 50;

    // --- PORTALE BOSS (Zona 2) ---
    /** Riga del portale goblin nella mappa boss (facilmente modificabile). */
    public static final int BOSS_PORTAL_ROW = 0;
    /** Colonna del portale goblin nella mappa boss (facilmente modificabile). */
    public static final int BOSS_PORTAL_COL = 6;
    /** Intervallo tra uno spawn e l'altro dal portale boss (in millisecondi). */
    public static final long BOSS_PORTAL_SPAWN_INTERVAL_MS = 10_000;
    /** Numero massimo di chasing goblin vivi nella mappa boss. */
    public static final int BOSS_PORTAL_MAX_GOBLINS = 6;
}
