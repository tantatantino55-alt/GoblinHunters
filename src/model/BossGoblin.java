package model;

import utils.Config;
import utils.Direction;
import utils.EnemyType;
import java.util.Random;

public class BossGoblin extends Enemy {

    public enum BossState { FURY, TELEGRAPH, EXHAUSTED, IDLE_EXHAUSTED, DYING }

    private BossState currentState = BossState.FURY;

    private static final long I_FRAME_DURATION  = 1000L;
    public  static final int  MAX_HP            = 25;

    private int attackCounter = 0;
    private static final int ATTACKS_BEFORE_REST = 3;

    private final double runSpeed;
    private final Random rand = new Random();

    // Flag "Modalita' Guardia": true quando il Boss e' fermo in attesa in FURY
    private boolean guarding = false;

    // Limiti del ring interno
    private static final double MIN_X = 3.1;
    private static final double MAX_X = 8.9;
    private static final double MIN_Y = 2.1;
    private static final double MAX_Y = 7.9;

    public BossGoblin(double startX, double startY) {
        super(startX, startY, Config.GOBLIN_COMMON_SPEED * 1.5, EnemyType.BOSS);
        this.hp       = MAX_HP;
        this.runSpeed = this.speed;
        this.currentDirection = Direction.values()[rand.nextInt(4)];
    }

    @Override public EnemyType getType()  { return EnemyType.BOSS; }
    public int getHP()    { return hp; }
    public int getMaxHP() { return MAX_HP; }

    @Override
    public boolean isInvincible() {
        return (System.currentTimeMillis() - lastHitTime) < I_FRAME_DURATION;
    }

    @Override
    public String getEnemyState() {
        // Se il Boss e' in FURY ma fermo in posizione di guardia, segnala uno stato
        // distinto alla View cosi' che possa usare l'animazione IDLE anziche' RUN.
        if (currentState == BossState.FURY && guarding) return "FURY_GUARD";
        return currentState.name();
    }

    private void changeState(BossState newState) {
        this.currentState   = newState;
        this.stateStartTime = System.currentTimeMillis();
        this.guarding       = false; // reset flag guardia ad ogni cambio stato
    }

    @Override
    public boolean takeDamage(int damage) {
        if (isDead) return false;

        long now = System.currentTimeMillis();

        // Bypass I-Frames per danni simultanei (Combo Bombe)
        if (now - lastHitTime < 50) {
            hp -= damage;
        }
        else if (now - lastHitTime > I_FRAME_DURATION) {
            hp -= damage;
            lastHitTime = now;
        }
        else {
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

    @Override
    public void updateBehavior() {
        if (currentState == BossState.DYING || isDead) return;

        long elapsed = System.currentTimeMillis() - stateStartTime;
        Model model  = (Model) Model.getInstance();

        // VERE coordinate del player (Usate SEMPRE per la mira)
        double pX    = model.xCoordinatePlayer();
        double pY    = model.yCoordinatePlayer();

        switch (currentState) {

            case FURY: {
                boolean onTop    = Math.abs(this.y - MIN_Y) < 0.1;
                boolean onBottom = Math.abs(this.y - MAX_Y) < 0.1;
                boolean onLeft   = Math.abs(this.x - MIN_X) < 0.1;
                boolean onRight  = Math.abs(this.x - MAX_X) < 0.1;

                // 1. MIRA E ATTACCO (Usa le coordinate VERE: pX e pY)
                boolean alignedX = Math.abs(this.x - pX) < 0.5;
                boolean alignedY = Math.abs(this.y - pY) < 0.5;

                boolean canShootTopBottom = (onTop || onBottom) && alignedX;
                boolean canShootLeftRight = (onLeft || onRight) && alignedY;

                if (elapsed > 1500 && (canShootTopBottom || canShootLeftRight)) {
                    changeState(BossState.TELEGRAPH);
                    if (canShootTopBottom) {
                        this.currentDirection = (pY > this.y) ? Direction.DOWN : Direction.UP;
                    } else {
                        this.currentDirection = (pX > this.x) ? Direction.RIGHT : Direction.LEFT;
                    }
                    break;
                }

                // 2. MOVIMENTO E FUGA (Usa le coordinate BERSAGLIO)
                double targetX = pX;
                double targetY = pY;

                double margin = 0.5;
                boolean inRing = pX >= MIN_X && pX <= MAX_X && pY >= MIN_Y && pY <= MAX_Y;

                boolean onTopEdge    = pY <= MIN_Y + margin && pX >= MIN_X - margin && pX <= MAX_X + margin;
                boolean onBottomEdge = pY >= MAX_Y - margin && pX >= MIN_X - margin && pX <= MAX_X + margin;
                boolean onLeftEdge   = pX <= MIN_X + margin && pY >= MIN_Y - margin && pY <= MAX_Y + margin;
                boolean onRightEdge  = pX >= MAX_X - margin && pY >= MIN_Y - margin && pY <= MAX_Y + margin;

                boolean isGuardMode = false;
                guarding = false; // Resetta all'inizio del tick; verra' settato sotto se necessario

                // Logica Cecchino: PRIORITÀ ASSI (La tua idea: Top/Bottom dominano su Left/Right)
                if (onTopEdge) {
                    targetY = MAX_Y;
                } else if (onBottomEdge) {
                    targetY = MIN_Y;
                } else if (onLeftEdge) {
                    targetX = MAX_X;
                } else if (onRightEdge) {
                    targetX = MIN_X;
                } else if (!inRing) {
                    // Modalità Guardia per le zone morte lontane (es. Spawn)
                    targetX = 6.0;
                    targetY = 5.0;
                    isGuardMode = true;
                }

                // Clamp di sicurezza
                targetX = Math.max(MIN_X, Math.min(MAX_X, targetX));
                targetY = Math.max(MIN_Y, Math.min(MAX_Y, targetY));

                // Rientro nel ring se sta fluttuando
                if (!onTop && !onBottom && !onLeft && !onRight) {
                    switch (this.currentDirection) {
                        case UP    -> this.y -= runSpeed;
                        case DOWN  -> this.y += runSpeed;
                        case LEFT  -> this.x -= runSpeed;
                        case RIGHT -> this.x += runSpeed;
                    }
                } else {
                    // Pattugliamento intelligente
                    boolean moveX = onTop || onBottom;
                    boolean moveY = onLeft || onRight;

                    if (moveX && moveY) {
                        if (Math.abs(this.x - targetX) > runSpeed) moveY = false;
                        else moveX = false;
                    }

                    if (moveX) {
                        if (Math.abs(this.x - targetX) > runSpeed) {
                            if (this.x < targetX) { this.x += runSpeed; this.currentDirection = Direction.RIGHT; }
                            else                  { this.x -= runSpeed; this.currentDirection = Direction.LEFT; }
                        } else {
                            // EVITA RI-ASSEGNAZIONI INUTILI SE È GIÀ IN POSIZIONE
                            if (Math.abs(this.x - targetX) > 0.01) {
                                this.x = targetX;
                            }

                            if (isGuardMode) {
                                this.currentDirection = Direction.DOWN;
                                guarding = true; // Segnala alla View: usa animazione IDLE
                            } else {
                                this.currentDirection = (pY > this.y) ? Direction.DOWN : Direction.UP;
                            }
                        }
                    } else if (moveY) {
                        if (Math.abs(this.y - targetY) > runSpeed) {
                            if (this.y < targetY) { this.y += runSpeed; this.currentDirection = Direction.DOWN; }
                            else                  { this.y -= runSpeed; this.currentDirection = Direction.UP; }
                        } else {
                            // EVITA RI-ASSEGNAZIONI INUTILI SE È GIÀ IN POSIZIONE
                            if (Math.abs(this.y - targetY) > 0.01) {
                                this.y = targetY;
                            }

                            if (isGuardMode) {
                                this.currentDirection = Direction.DOWN;
                                guarding = true; // Segnala alla View: usa animazione IDLE
                            } else {
                                this.currentDirection = (pX > this.x) ? Direction.RIGHT : Direction.LEFT;
                            }
                        }
                    }
                }

                this.x = Math.max(MIN_X, Math.min(MAX_X, this.x));
                this.y = Math.max(MIN_Y, Math.min(MAX_Y, this.y));
                break;
            }

            case TELEGRAPH: {
                if (elapsed > 1000) {
                    int bossRow = (int) Math.round(this.y);
                    int bossCol = (int) Math.round(this.x);
                    model.getMapManager().spawnCrackWave(bossRow, bossCol, this.currentDirection, model.getGameAreaArray());
                    attackCounter++;

                    if (attackCounter >= ATTACKS_BEFORE_REST) {
                        attackCounter = 0;
                        changeState(BossState.EXHAUSTED);
                    } else {
                        changeState(BossState.FURY);
                    }
                }
                break;
            }

            case EXHAUSTED: {
                if (Math.abs(this.x - 6.0) > runSpeed) {
                    if (this.x < 6.0) { this.x += runSpeed; this.currentDirection = Direction.RIGHT; }
                    else              { this.x -= runSpeed; this.currentDirection = Direction.LEFT;  }
                }
                else if (Math.abs(this.y - 5.0) > runSpeed) {
                    this.x = 6.0;
                    if (this.y < 5.0) { this.y += runSpeed; this.currentDirection = Direction.DOWN; }
                    else              { this.y -= runSpeed; this.currentDirection = Direction.UP;   }
                }
                else {
                    this.x = 6.0;
                    this.y = 5.0;
                    changeState(BossState.IDLE_EXHAUSTED);
                    model.getMapManager().clearCracks();
                }
                break;
            }

            case IDLE_EXHAUSTED: {
                long restTime = (hp <= MAX_HP / 2) ? 1500L : 3000L;
                if (elapsed > restTime) {
                    this.currentDirection = Direction.values()[rand.nextInt(4)];
                    changeState(BossState.FURY);
                }
                break;
            }

            case DYING:
                break;
        }
    }

    public boolean isReadyToDespawn() {
        if (currentState != BossState.DYING) return false;
        return (System.currentTimeMillis() - stateStartTime) > 2000L;
    }
}