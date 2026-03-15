package model;

import utils.ItemType;

public class Collectible {
    private final double x;
    private final double y;
    private final ItemType type;
    private final long spawnTime;

    public Collectible(double x, double y, ItemType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.spawnTime = System.currentTimeMillis();
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public ItemType getType() { return type; }
    public long getSpawnTime() { return spawnTime; }
}
