package model;

class FloorCrack {

    /** Durata ridotta a 2.5 secondi (150 tick @ 60FPS) per facilitare il movimento. */
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

    int getElapsedTicks() {
        return CRACK_DURATION_TICKS - remainingTicks;
    }
}
