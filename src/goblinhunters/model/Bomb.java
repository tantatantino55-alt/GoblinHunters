package goblinhunters.model;

public class Bomb extends Entity {

    // Bomb uses grid row/col rather than the double x/y from Entity
    private final int row;
    private final int col;
    private int timer;
    private final int radius;
    private boolean exploded;
    private final long creationTime; // used by the View for sprite animation timing

    public Bomb(int row, int col, int timer, int radius) {
        super();
        this.row = row;
        this.col = col;
        this.timer = timer;
        this.radius = radius;
        this.exploded = false;
        this.creationTime = System.currentTimeMillis();
    }

    public void updateDetonationTimer() {
        if (timer > 0) {
            timer--;
        } else {
            exploded = true;
        }
    }

    public void detonate() {
        this.timer = 0;
        this.exploded = true;
    }

    public int getRow()           { return row; }
    public int getCol()           { return col; }
    public int getRadius()        { return radius; }
    public boolean isExploded()   { return exploded; }
    public long getCreationTime() { return creationTime; }
}
