package model;

public class Player extends Entity {
    private double xCoordinate;
    private double yCoordinate;
    private double deltaX = 0;
    private double deltaY = 0;

    private String currentAction = "PLAYER_FRONT_IDLE";
    private int frameIndex = 0;
    private int totalFrames = 1;
    private int animationTick = 0;
    private int animationSpeed = 3;

    public Player(double startX, double startY) {
        this.xCoordinate = startX;
        this.yCoordinate = startY;
    }

    public void updateAnimation() {
        animationTick++;
        if (animationTick >= animationSpeed) {
            animationTick = 0;
            frameIndex = (frameIndex + 1) % totalFrames;
        }
    }

    public void setAction(String newAction, int framesCount) {
        if (!this.currentAction.equals(newAction)) {
            this.currentAction = newAction;
            this.totalFrames = (framesCount > 0) ? framesCount : 1;
            this.frameIndex = 0;
            this.animationTick = 0;
        }
    }

    public double getXCoordinate() { return xCoordinate; }
    public double getYCoordinate() { return yCoordinate; }
    public void setXCoordinate(double x) { this.xCoordinate = x; }
    public void setYCoordinate(double y) { this.yCoordinate = y; }
    public void setDelta(double dx, double dy) { this.deltaX = dx; this.deltaY = dy; }
    public double getDeltaX() { return deltaX; }
    public double getDeltaY() { return deltaY; }
    public String getCurrentAction() { return currentAction; }
    public int getFrameIndex() { return frameIndex; }
}