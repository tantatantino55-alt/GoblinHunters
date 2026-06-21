package goblinhunter.model;

public abstract class Entity {
    protected double x;
    protected double y;
    protected double speed;

    protected Entity(double x, double y) {
        this.x = x;
        this.y = y;
    }

    protected Entity() {}

    public double getX() { return x; }
    public double getY() { return y; }
}
