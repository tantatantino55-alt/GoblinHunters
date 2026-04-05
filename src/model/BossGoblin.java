package model;

import utils.Config;
import utils.Direction;
import utils.EnemyType;

public class BossGoblin extends Enemy {

    /**
     * Stati della macchina a stati del Boss.
     * IDLE_EXHAUSTED = riposo totale al centro (fase B di EXHAUSTED).
     */
    public enum BossState { FURY, TELEGRAPH, EXHAUSTED, IDLE_EXHAUSTED, DYING }

    private BossState currentState = BossState.FURY;
    private static final long I_FRAME_DURATION = 1000; // 1 secondo di invulnerabilità
    public static final int MAX_HP = 15;

    public BossGoblin(double startX, double startY) {
        super(startX, startY, Config.GOBLIN_COMMON_SPEED * 1.5, EnemyType.BOSS);
        this.hp = MAX_HP;
    }

    @Override
    public EnemyType getType() {
        return EnemyType.BOSS;
    }

    /** Espone gli HP correnti per la HUD. */
    public int getHP() { return hp; }

    /** Espone gli HP massimi per la HUD. */
    public int getMaxHP() { return MAX_HP; }

    @Override
    public boolean isInvincible() {
        return (System.currentTimeMillis() - lastHitTime) < I_FRAME_DURATION;
    }

    @Override
    public String getEnemyState() {
        return currentState.name();
    }

    private void changeState(BossState newState) {
        this.currentState = newState;
        this.stateStartTime = System.currentTimeMillis();
    }

    @Override
    public boolean takeDamage(int damage) {
        if (isDead || isInvincible()) return false;

        hp -= damage;
        System.out.println("Boss colpito! HP: " + hp + "/" + MAX_HP);
        lastHitTime = System.currentTimeMillis();

        if (hp <= 0) {
            hp = 0;
            isDead = true;
            changeState(BossState.DYING);
            return true;
        }
        return false;
    }

    @Override
    public void updateBehavior() {
        if (isDead) return;

        long elapsed = System.currentTimeMillis() - stateStartTime;
        Model model = (Model) Model.getInstance();
        double pX = model.xCoordinatePlayer();
        double pY = model.yCoordinatePlayer();

        switch (currentState) {

            // ---------------------------------------------------------------
            // FURY: corre sul perimetro; se si allinea al player → TELEGRAPH
            // ---------------------------------------------------------------
            case FURY:
                // 1. Controllo allineamento (switch a TELEGRAPH)
                if (Math.abs(this.x - pX) < 0.5 || Math.abs(this.y - pY) < 0.5) {
                    changeState(BossState.TELEGRAPH);
                    // Si gira verso il player per il caricamento
                    if (Math.abs(this.x - pX) < 0.5) {
                        this.currentDirection = (pY > this.y) ? Direction.DOWN : Direction.UP;
                    } else {
                        this.currentDirection = (pX > this.x) ? Direction.RIGHT : Direction.LEFT;
                    }
                    break;
                }

                // 2. Corsa sul perimetro (X tra 3.1-8.9, Y tra 2.1-7.9)
                double s = this.speed;
                double nextX = this.x;
                double nextY = this.y;

                if      (this.y <= 2.1 && this.x < 8.9) { nextX += s; this.currentDirection = Direction.RIGHT; }
                else if (this.x >= 8.9 && this.y < 7.9) { nextY += s; this.currentDirection = Direction.DOWN;  }
                else if (this.y >= 7.9 && this.x > 3.1) { nextX -= s; this.currentDirection = Direction.LEFT;  }
                else if (this.x <= 3.1 && this.y > 2.1) { nextY -= s; this.currentDirection = Direction.UP;    }
                else                                      { nextY -= s; this.currentDirection = Direction.UP;    } // fallback

                this.x = nextX;
                this.y = nextY;
                break;

            // ---------------------------------------------------------------
            // TELEGRAPH: carica 1 secondo mostrando animazione ATTACK,
            // poi lancia la crack-wave e va in EXHAUSTED.
            // ---------------------------------------------------------------
            case TELEGRAPH:
                // Il boss sta fermo e "carica" — nessun movimento.
                // TASK 1: il wave scatta SOLO quando il secondo è scaduto.
                if (elapsed > 1000) {
                    int bossRow = (int) Math.round(this.y);
                    int bossCol = (int) Math.round(this.x);
                    model.getMapManager().spawnCrackWave(
                        bossRow, bossCol,
                        this.currentDirection,
                        model.getGameAreaArray()
                    );
                    System.out.println("BOSS WAVE lanciata! Dir: " + this.currentDirection
                        + " da [" + bossRow + ", " + bossCol + "]");
                    changeState(BossState.EXHAUSTED);
                }
                break;

            // ---------------------------------------------------------------
            // EXHAUSTED – Fase A: barcolla verso il centro (6.0, 5.0).
            // Quando arriva (distanza < 0.1) → IDLE_EXHAUSTED (Fase B).
            // ---------------------------------------------------------------
            case EXHAUSTED:
                this.speed = Config.GOBLIN_COMMON_SPEED * 0.3;
                double dx = 6.0 - this.x;
                double dy = 5.0 - this.y;
                double length = Math.sqrt(dx * dx + dy * dy);

                if (length > 0.1) {
                    // TASK 2: aggiorna la direzione in base alla traiettoria reale
                    if (Math.abs(dx) >= Math.abs(dy)) {
                        this.currentDirection = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
                    } else {
                        this.currentDirection = (dy > 0) ? Direction.DOWN : Direction.UP;
                    }
                    this.x += (dx / length) * this.speed;
                    this.y += (dy / length) * this.speed;
                } else {
                    // TASK 3 – Fase B: al centro → fermo in IDLE_EXHAUSTED
                    this.x = 6.0;
                    this.y = 5.0;
                    setDelta(0, 0);
                    changeState(BossState.IDLE_EXHAUSTED);
                }
                break;

            // ---------------------------------------------------------------
            // IDLE_EXHAUSTED – Fase B: riposo totale al centro.
            // 3 s normali, 1.5 s se HP <= 50%.
            // ---------------------------------------------------------------
            case IDLE_EXHAUSTED:
                // Rimane fermo — nessun movimento.
                long restTime = (hp <= MAX_HP / 2) ? 1500L : 3000L;
                if (elapsed > restTime) {
                    this.speed = Config.GOBLIN_COMMON_SPEED * 1.5; // Ripristina velocità
                    changeState(BossState.FURY);
                }
                break;

            // ---------------------------------------------------------------
            // DYING: nessuna azione
            // ---------------------------------------------------------------
            case DYING:
                break;
        }
    }

    /** Helper: imposta la velocità di movimento a 0 (boss fermo). */
    private void setDelta(double dx, double dy) {
        // Nel BossGoblin il movimento è gestito direttamente via this.x/y,
        // non tramite delta separati; questo metodo è un no-op che documenta
        // l'intenzione di fermare il boss.
    }
}