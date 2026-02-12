package model;

public class Bomb extends Entity {

    private final int row;
    private final int col;
    private int timer; // Timer logico per l'esplosione (gameplay)
    private final int radius;
    private boolean exploded;

    // TIMESTAMP: Serve alla View per calcolare l'animazione,
    // ma per il Model è solo un dato temporale.
    private final long creationTime;

    public Bomb(int row, int col, int timer, int radius) {
        this.row = row;
        this.col = col;
        this.timer = timer;
        this.radius = radius;
        this.exploded = false;
        this.creationTime = System.currentTimeMillis(); // Segniamo l'orario di creazione
    }

    public void updateDetonationTimer() {
        // Solo logica di gameplay (Countdown)
        if (timer > 0) {
            timer--;
        } else {
            exploded = true;
        }
        // NESSUN calcolo di frame qui!
    }

    public void detonate() {
        this.timer = 0;
        this.exploded = true;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getRadius() { return radius; }
    public boolean isExploded() { return exploded; }
    public long getCreationTime() { return creationTime; }
}