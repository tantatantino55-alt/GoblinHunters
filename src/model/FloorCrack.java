package model;

/**
 * Rappresenta una singola cella di pavimento crepato generata dall'attacco del Boss.
 * Le crepe sono gestite come overlay temporanei indipendenti dalla mappa logica
 * (gameAreaArray rimane invariato), in modo simile ad activeFire.
 */
class FloorCrack {

    /** Durata di una crepa in tick di gioco: 4 secondi * 60 FPS = 240 tick. */
    static final int CRACK_DURATION_TICKS = 240;

    final int row;
    final int col;
    int remainingTicks;

    FloorCrack(int row, int col) {
        this.row = row;
        this.col = col;
        this.remainingTicks = CRACK_DURATION_TICKS;
    }

    /** Decrementa il timer. Ritorna true se la crepa è scaduta e va rimossa. */
    boolean tick() {
        remainingTicks--;
        return remainingTicks <= 0;
    }

    /** Ritorna il numero di tick gia' trascorsi (utile per animazioni View). */
    int getElapsedTicks() {
        return CRACK_DURATION_TICKS - remainingTicks;
    }

    /**
     * Resetta il timer della crepa al valore indicato.
     * Usato da MapManager quando un nuovo attacco sovrascrive una crepa esistente
     * sulla stessa cella: il timer riparte da capo invece di essere ignorato.
     *
     * @param ticks nuova durata in tick (tipicamente CRACK_DURATION_TICKS)
     */
    void resetTicks(int ticks) {
        this.remainingTicks = ticks;
    }
}
