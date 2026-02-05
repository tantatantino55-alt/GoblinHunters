package utils;

/**
 * Enum per distinguere le tipologie di nemici.
 * Usato dalla View per decidere quale Sprite/Colore disegnare
 * e dal Model per logiche specifiche (es. chi droppa cosa).
 */
public enum EnemyType {
    COMMON,   // Goblin base (Movimento casuale)
    HUNTER,   // Goblin cacciatore (Insegue il player)
    SHOOTER,  // Goblin tiratore (Spara a vista - Requisito futuro)
    BOSS      // Boss di fine livello (Requisito futuro)
}