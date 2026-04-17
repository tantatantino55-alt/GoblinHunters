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
        if (isDead) return false;

        long now = System.currentTimeMillis();

        // TASK 4: Bypass I-Frames per danni simultanei (Combo)
        if (now - lastHitTime < 50) {
            hp -= damage;
            System.out.println("Boss colpito da COMBO simultanea! HP: " + hp + "/" + MAX_HP);
        } 
        else if (now - lastHitTime > I_FRAME_DURATION) {
            hp -= damage;
            System.out.println("Boss colpito! HP: " + hp + "/" + MAX_HP);
            lastHitTime = now;
        } 
        else {
            // I-Frames attivi (fase intermedia 50ms - 1000ms)
            return false;
        }

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
            // FURY – TASK 1: Stalking intelligente
            // Cerca di pareggiare X se si trova nei bordi orizzontali
            // o Y se si trova nei bordi verticali.
            // ----------------------------------------------------------------
            case FURY: {
                // 1. Allineamento (solo dopo 1500 ms dall'ingresso in FURY)
                if (elapsed > 1500) {
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

                // 2. Pedinamento sui bordi
                boolean onTop    = Math.abs(this.y - 2.1) < 0.1;
                boolean onBottom = Math.abs(this.y - 7.9) < 0.1;
                boolean onLeft   = Math.abs(this.x - 3.1) < 0.1;
                boolean onRight  = Math.abs(this.x - 8.9) < 0.1;

                if (onTop || onBottom) {
                    // Pareggia la X
                    if (Math.abs(this.x - pX) > runSpeed) {
                        if (pX > this.x) { this.x += runSpeed; this.currentDirection = Direction.RIGHT; }
                        else             { this.x -= runSpeed; this.currentDirection = Direction.LEFT;  }
                    } else {
                        this.x = pX;
                        // Orientiamoci verso il centro per far capire che stiamo "guardando" ma non siamo ancora in attacco (opzionale)
                        this.currentDirection = onTop ? Direction.DOWN : Direction.UP;
                    }
                } else if (onLeft || onRight) {
                    // Pareggia la Y
                    if (Math.abs(this.y - pY) > runSpeed) {
                        if (pY > this.y) { this.y += runSpeed; this.currentDirection = Direction.DOWN; }
                        else             { this.y -= runSpeed; this.currentDirection = Direction.UP;   }
                    } else {
                        this.y = pY;
                        this.currentDirection = onLeft ? Direction.RIGHT : Direction.LEFT;
                    }
                } else {
                    // Fallback se staccato dai bordi per qualche motivo
                    this.y -= runSpeed; this.currentDirection = Direction.UP;
                }
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
            // EXHAUSTED – FASE A: TASK 2 Movimento a "L" verso il centro.
            // Prima asse X, poi asse Y.
            // ----------------------------------------------------------------
            case EXHAUSTED: {
                int maxExhaust = (hp <= MAX_HP / 2) ? 2000 : 4000;

                if (elapsed > maxExhaust) {
                    changeState(BossState.FURY);
                    break;
                }

                if (Math.abs(this.x - 6.0) > runSpeed) {
                    // Priorità 1: allinea le ascisse (X)
                    if (this.x < 6.0) { this.x += runSpeed; this.currentDirection = Direction.RIGHT; }
                    else              { this.x -= runSpeed; this.currentDirection = Direction.LEFT;  }
                } 
                else if (Math.abs(this.y - 5.0) > runSpeed) {
                    // Priorità 2: allinea le ordinate (Y) dopo aver fissato la X
                    this.x = 6.0; // evita micro-shaking
                    if (this.y < 5.0) { this.y += runSpeed; this.currentDirection = Direction.DOWN; }
                    else              { this.y -= runSpeed; this.currentDirection = Direction.UP;   }
                } 
                else {
                    // Arrivato al centro → Fase B (riposo fermo)
                    this.x = 6.0;
                    this.y = 5.0;
                    changeState(BossState.IDLE_EXHAUSTED);
                    
                    // TASK 3: Chiama clearCracks() alla transizione di riposo
                    model.getMapManager().clearCracks();
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