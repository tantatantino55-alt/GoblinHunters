package model;

import utils.Config;
import utils.Direction;
import utils.EnemyType;

public class BossGoblin extends Enemy {

    /**
     * Stati della macchina a stati del Boss.
     * IDLE_EXHAUSTED = riposo totale al centro (boss arrivato, aspetta il timer).
     * DYING          = cadavere a terra; rimane nella lista fino al despawn.
     */
    public enum BossState { FURY, TELEGRAPH, EXHAUSTED, IDLE_EXHAUSTED, DYING }

    private BossState currentState = BossState.FURY;

    private static final long I_FRAME_DURATION  = 1000L; // ms invulnerabilita'
    public  static final int  MAX_HP            = 15;

    // --- TASK 2: contatore attacchi prima di andare in EXHAUSTED ---
    private int attackCounter = 0;
    private static final int ATTACKS_BEFORE_REST = 3;

    // Velocita' di corsa fissa (mai modificata durante EXHAUSTED)
    private final double runSpeed;

    public BossGoblin(double startX, double startY) {
        super(startX, startY, Config.GOBLIN_COMMON_SPEED * 1.5, EnemyType.BOSS);
        this.hp       = MAX_HP;
        this.runSpeed = this.speed;   // salviamo la velocita' nominale a costruzione
    }

    @Override public EnemyType getType()  { return EnemyType.BOSS; }

    /** Espone HP correnti (per HUD). */
    public int getHP()    { return hp; }
    /** Espone HP massimi (per HUD). */
    public int getMaxHP() { return MAX_HP; }

    @Override
    public boolean isInvincible() {
        return (System.currentTimeMillis() - lastHitTime) < I_FRAME_DURATION;
    }

    @Override
    public String getEnemyState() { return currentState.name(); }

    private void changeState(BossState newState) {
        this.currentState   = newState;
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

    // ----------------------------------------------------------------
    //  GAME LOOP PRINCIPALE
    // ----------------------------------------------------------------

    @Override
    public void updateBehavior() {
        // DYING: il cadavere e' immobile — nessuna logica
        if (currentState == BossState.DYING) return;
        if (isDead) return;

        long elapsed = System.currentTimeMillis() - stateStartTime;
        Model model  = (Model) Model.getInstance();
        double pX    = model.xCoordinatePlayer();
        double pY    = model.yCoordinatePlayer();

        switch (currentState) {

            // ----------------------------------------------------------------
            // FURY – corre sul perimetro.
            // Cooldown anti-spam: almeno 600 ms di corsa prima di potersi
            // allineare di nuovo al player.
            // ----------------------------------------------------------------
            case FURY: {
                // 1. Allineamento (solo dopo 600 ms dall'ingresso in FURY)
                if (elapsed > 600) {
                    boolean alignedX = Math.abs(this.x - pX) < 0.5;
                    boolean alignedY = Math.abs(this.y - pY) < 0.5;
                    if (alignedX || alignedY) {
                        changeState(BossState.TELEGRAPH);
                        if (alignedX) {
                            this.currentDirection = (pY > this.y) ? Direction.DOWN : Direction.UP;
                        } else {
                            this.currentDirection = (pX > this.x) ? Direction.RIGHT : Direction.LEFT;
                        }
                        break;
                    }
                }

                // 2. Corsa sul perimetro interno (X 3.1-8.9, Y 2.1-7.9)
                double nextX = this.x;
                double nextY = this.y;

                if      (this.y <= 2.1 && this.x < 8.9) { nextX += runSpeed; this.currentDirection = Direction.RIGHT; }
                else if (this.x >= 8.9 && this.y < 7.9) { nextY += runSpeed; this.currentDirection = Direction.DOWN;  }
                else if (this.y >= 7.9 && this.x > 3.1) { nextX -= runSpeed; this.currentDirection = Direction.LEFT;  }
                else if (this.x <= 3.1 && this.y > 2.1) { nextY -= runSpeed; this.currentDirection = Direction.UP;    }
                else                                     { nextY -= runSpeed; this.currentDirection = Direction.UP;    }

                this.x = nextX;
                this.y = nextY;
                break;
            }

            // ----------------------------------------------------------------
            // TELEGRAPH – fermo per 1 s (animazione ATTACK), poi lancia wave.
            // Contatore attacchi: dopo ATTACKS_BEFORE_REST colpi → EXHAUSTED.
            // Altrimenti torna in FURY per un altro giro sul perimetro.
            // ----------------------------------------------------------------
            case TELEGRAPH: {
                if (elapsed > 1000) {
                    int bossRow = (int) Math.round(this.y);
                    int bossCol = (int) Math.round(this.x);
                    model.getMapManager().spawnCrackWave(
                        bossRow, bossCol,
                        this.currentDirection,
                        model.getGameAreaArray()
                    );
                    System.out.println("BOSS WAVE #" + (attackCounter + 1)
                        + " lanciata! Dir=" + this.currentDirection
                        + " [" + bossRow + "," + bossCol + "]");

                    attackCounter++;

                    if (attackCounter >= ATTACKS_BEFORE_REST) {
                        attackCounter = 0;
                        changeState(BossState.EXHAUSTED);
                    } else {
                        // Attacchi rimanenti: riprende la corsa
                        changeState(BossState.FURY);
                    }
                }
                break;
            }

            // ----------------------------------------------------------------
            // EXHAUSTED – FASE A: si muove verso il centro a velocita' NORMALE.
            // Timer assoluto: 4 s (2 s se enrage).
            // FIX MOONWALK: currentDirection aggiornata in base alla traiettoria.
            // Se il timer scade prima di arrivare → FURY direttamente.
            // ----------------------------------------------------------------
            case EXHAUSTED: {
                int maxExhaust = (hp <= MAX_HP / 2) ? 2000 : 4000;

                if (elapsed > maxExhaust) {
                    changeState(BossState.FURY);
                    break;
                }

                double dx     = 6.0 - this.x;
                double dy     = 5.0 - this.y;
                double length = Math.sqrt(dx * dx + dy * dy);

                if (length > 0.1) {
                    // FIX MOONWALKING: la direzione riflette l'asse di movimento prevalente
                    if (Math.abs(dx) >= Math.abs(dy)) {
                        this.currentDirection = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
                    } else {
                        this.currentDirection = (dy > 0) ? Direction.DOWN : Direction.UP;
                    }
                    // Velocita' di corsa normale — nessun moltiplicatore 0.3x
                    this.x += (dx / length) * runSpeed;
                    this.y += (dy / length) * runSpeed;
                } else {
                    // Arrivato al centro → Fase B (riposo fermo)
                    this.x = 6.0;
                    this.y = 5.0;
                    changeState(BossState.IDLE_EXHAUSTED);
                }
                break;
            }

            // ----------------------------------------------------------------
            // IDLE_EXHAUSTED – FASE B: fermo al centro esatto.
            // Attende il timer prima di tornare a FURY.
            // 3 s normali, 1.5 s se enrage (HP <= 50%).
            // ----------------------------------------------------------------
            case IDLE_EXHAUSTED: {
                long restTime = (hp <= MAX_HP / 2) ? 1500L : 3000L;
                if (elapsed > restTime) {
                    changeState(BossState.FURY);
                }
                // Nessun movimento — rimane fermo al centro
                break;
            }

            // ----------------------------------------------------------------
            // DYING – cadavere a terra; nessuna azione.
            // Il despawn e' gestito da Model.updateEnemies() via isReadyToDespawn().
            // ----------------------------------------------------------------
            case DYING:
                break;
        }
    }

    // ----------------------------------------------------------------
    //  DESPAWN: rimosso definitivamente da Model dopo 2 s in DYING
    // ----------------------------------------------------------------

    /**
     * Ritorna true quando il Boss e' in DYING da > 2 secondi e puo' essere
     * rimosso definitivamente dalla lista enemies.
     */
    public boolean isReadyToDespawn() {
        if (currentState != BossState.DYING) return false;
        return (System.currentTimeMillis() - stateStartTime) > 2000L;
    }
}