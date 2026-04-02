package model;

import utils.Config;
import utils.Direction;
import utils.EnemyType;

public class BossGoblin extends Enemy {

    public enum BossState { FURY, TELEGRAPH, EXHAUSTED, DYING }

    private BossState currentState = BossState.FURY;
    private static final long I_FRAME_DURATION = 1000; // 1 secondo di invulnerabilità
    private static final int MAX_HP = 15;

    public BossGoblin(double startX, double startY) {
        // Chiama il costruttore della nuova classe Enemy: (X, Y, Velocità, Tipo)
        super(startX, startY, Config.GOBLIN_COMMON_SPEED * 1.5, EnemyType.BOSS);
        this.hp = MAX_HP;
    }

    @Override
    public EnemyType getType() {
        return EnemyType.BOSS;
    }

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
            changeState(BossState.DYING); // Entra nello stato di morte!
            return true; // Ritorna TRUE per segnalare il colpo fatale
        }
        return false;
    }

    @Override
    public void updateBehavior() {
        // Se è morto, blocchiamo istantaneamente ogni suo movimento
        if (isDead) return;

        long elapsed = System.currentTimeMillis() - stateStartTime;
        Model model = (Model) Model.getInstance();
        double pX = model.xCoordinatePlayer();
        double pY = model.yCoordinatePlayer();

        switch (currentState) {
            case FURY:
                // 1. CONTROLLO ALLINEAMENTO (Cecchino)
                if (Math.abs(this.x - pX) < 0.5 || Math.abs(this.y - pY) < 0.5) {
                    changeState(BossState.TELEGRAPH);
                    // Si gira a guardare il player per sparargli
                    if (Math.abs(this.x - pX) < 0.5) {
                        this.currentDirection = (pY > this.y) ? Direction.DOWN : Direction.UP;
                    } else {
                        this.currentDirection = (pX > this.x) ? Direction.RIGHT : Direction.LEFT;
                    }
                    break;
                }

                // 2. CORSA SUL PERIMETRO (X tra 3.1 e 8.9, Y tra 2.1 e 7.9)
                double s = this.speed;
                double nextX = this.x;
                double nextY = this.y;

                if (this.y <= 2.1 && this.x < 8.9) {
                    nextX += s; this.currentDirection = Direction.RIGHT;
                }
                else if (this.x >= 8.9 && this.y < 7.9) {
                    nextY += s; this.currentDirection = Direction.DOWN;
                }
                else if (this.y >= 7.9 && this.x > 3.1) {
                    nextX -= s; this.currentDirection = Direction.LEFT;
                }
                else if (this.x <= 3.1 && this.y > 2.1) {
                    nextY -= s; this.currentDirection = Direction.UP;
                }
                else {
                    nextY -= s; this.currentDirection = Direction.UP; // Fallback di sicurezza
                }

                this.x = nextX;
                this.y = nextY;
                break;

            case TELEGRAPH:
                if (elapsed > 1000) {
                    // TODO: Metteremo qui lo spawn dell'onda di lava!
                    System.out.println("BOSS LANCIA LAVA!");
                    changeState(BossState.EXHAUSTED);
                }
                break;

            case EXHAUSTED:
                // Barcolla lentamente verso il centro (6.0, 5.0)
                this.speed = Config.GOBLIN_COMMON_SPEED * 0.3;
                double dx = 6.0 - this.x;
                double dy = 5.0 - this.y;
                double length = Math.sqrt(dx * dx + dy * dy);

                if (length > 0.1) {
                    this.x += (dx / length) * this.speed;
                    this.y += (dy / length) * this.speed;
                }

                // Enrage: Sotto il 50% di vita, l'affaticamento dura la metà!
                int exhaustTime = (hp <= MAX_HP / 2) ? 2000 : 4000;
                if (elapsed > exhaustTime) {
                    this.speed = Config.GOBLIN_COMMON_SPEED * 1.5; // Ripristina velocità
                    changeState(BossState.FURY);
                }
                break;

            case DYING:
                break; // Non fa nulla, aspetta solo di marcire a terra
        }
    }
}