package goblinhunter.model;

import goblinhunter.utils.ItemType;

public class Collectible {
    private final double x;
    private final double y;
    private final ItemType type;

    public Collectible(double x, double y, ItemType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public ItemType getType() { return type; }
}
