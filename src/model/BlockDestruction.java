package model;

public class BlockDestruction {
    private final int row;
    private final int col;
    private final long creationTime;

    public BlockDestruction(int row, int col) {
        this.row = row;
        this.col = col;
        this.creationTime = System.currentTimeMillis();
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public long getCreationTime() { return creationTime; }
}