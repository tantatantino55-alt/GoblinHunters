package model;

/**
 * Classe base per tutte le entità di gioco.
 * Contiene gli attributi comuni: posizione (x, y) e velocità.
 */
public abstract class Entity {
    protected double x;
    protected double y;
    protected double speed;

    /** Costruttore con posizione iniziale. */
    protected Entity(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** Costruttore senza parametri (usato da entità con coordinate a griglia, es. Bomb). */
    protected Entity() {}

    public double getX() { return x; }
    public double getY() { return y; }
}
