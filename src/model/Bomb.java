package model;

/*
 * Rappresenta l'entità Bomba nel Modello.
 * Ancorata alla griglia discreta (row, col) per semplificare esplosioni e collisioni.
 */
public class Bomb extends Entity {

    private final int row;
    private final int col;
    private int timer;
    private final int radius;
    private boolean exploded;

    public Bomb(int row, int col, int timer, int radius) {
        this.row = row;
        this.col = col;
        this.timer = timer;
        this.radius = radius;
        this.exploded = false;
    }

    /*
     * Riduce il tempo mancante alla detonazione.
     * Il nome riflette specificamente l'azione di countdown per la bomba.
     */
    public void updateDetonationTimer() {
        if (timer > 0) {
            timer--;
        } else {
            exploded = true;
        }
    }

    // Getters per la logica e il disegno della View
    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getRadius() { return radius; }
    public boolean isExploded() { return exploded; }
}