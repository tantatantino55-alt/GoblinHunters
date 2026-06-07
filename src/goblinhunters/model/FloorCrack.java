package goblinhunters.model;

class FloorCrack {

    // 2.5 s at 60 FPS — short enough that players can move off before the lava appears
    static final int CRACK_DURATION_TICKS = 150;

    final int row;
    final int col;
    int remainingTicks;

    FloorCrack(int row, int col) {
        this.row = row;
        this.col = col;
        this.remainingTicks = CRACK_DURATION_TICKS;
    }

    void resetTicks(int ticks) {
        this.remainingTicks = ticks;
    }

    boolean tick() {
        remainingTicks--;
        return remainingTicks <= 0;
    }

}
