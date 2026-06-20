package goblinhunters.view;

class DrawableEntity {
    public final int y;
    public final Runnable drawAction;

    DrawableEntity(int y, Runnable drawAction) {
        this.y = y;
        this.drawAction = drawAction;
    }
}
