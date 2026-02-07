package model;

import utils.Config;
import utils.Direction;
import utils.EnemyType;

public class ShooterGoblin extends ChasingGoblin {

    private int ammo;
    private int reloadTimer;
    private int telegraphTimer;
    private Direction telegraphDirection; // La mira "bloccata"

    private enum State { RELOADING, PATROL_OR_CHASE, AIMING }
    private State state;

    public ShooterGoblin(double startX, double startY) {
        super(startX, startY, Config.GOBLIN_COMMON_SPEED, EnemyType.SHOOTER);
        this.ammo = Config.SHOOTER_MAX_AMMO;
        this.state = State.PATROL_OR_CHASE;
        this.telegraphDirection = null;
    }

    @Override
    public void updateBehavior() {
        double px = Model.getInstance().xCoordinatePlayer();
        double py = Model.getInstance().yCoordinatePlayer();

        // 1. Controllo Bombe (Priorità ereditata ma critica per interrompere la mira)
        Direction safeDir = getSafeDirectionFromBombs();
        if (safeDir != null) {
            this.telegraphDirection = null; // Interrompe la mira se deve scappare!
            this.state = State.PATROL_OR_CHASE;
            this.currentDirection = safeDir;
            moveInDirection();
            return;
        }

        // 2. Macchina a Stati
        switch (state) {
            case RELOADING:
                handleReloadState(px, py);
                break;

            case AIMING:
                handleAimingState();
                break;

            case PATROL_OR_CHASE:
                handleNormalState(px, py);
                break;
        }
    }

    private void handleNormalState(double px, double py) {
        // Se ho colpi e vedo il player -> MIRA
        if (ammo > 0 && hasLineOfSight(px, py)) {
            startAiming(px, py);
        }
        // Altrimenti uso il cervello da Chaser (ereditato)
        else {
            this.speed = Config.GOBLIN_COMMON_SPEED; // Velocità normale
            // Chiama il metodo del padre:
            // Se "sente" l'odore insegue, altrimenti random.
            super.updateBehavior();
        }
    }

    private void handleAimingState() {
        this.speed = Config.SHOOTER_SPEED_AIMING; // Si ferma per mirare
        telegraphTimer--;

        if (telegraphTimer <= 0) {
            shoot();
            // Dopo lo sparo, se ha ancora colpi torna a cercare, se no ricarica
            if (ammo > 0) state = State.PATROL_OR_CHASE;
            else state = State.RELOADING;
        }
    }

    private void handleReloadState(double px, double py) {
        reloadTimer--;

        // Durante la ricarica è aggressivo ma veloce (comportamento da Chaser potenziato)
        this.speed = Config.SHOOTER_SPEED_CHASE;

        // Uso la logica di inseguimento del padre
        super.updateBehavior();

        if (reloadTimer <= 0) {
            ammo = Config.SHOOTER_MAX_AMMO;
            state = State.PATROL_OR_CHASE;
            System.out.println("Shooter: Ricarica completata!");
        }
    }

    private void startAiming(double px, double py) {
        state = State.AIMING;
        telegraphTimer = Config.SHOOTER_TELEGRAPH_TIME;

        // Calcola direzione mira (Telegraph)
        double dx = px - this.x;
        double dy = py - this.y;
        if (Math.abs(dx) > Math.abs(dy))
            telegraphDirection = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
        else
            telegraphDirection = (dy > 0) ? Direction.DOWN : Direction.UP;
    }

    private void shoot() {
        ammo--;
        System.out.println("Shooter: BANG! Direzione: " + telegraphDirection);
        // Qui dovrai aggiungere al Model il proiettile:
        // Model.getInstance().addProjectile(new Projectile(this.x, this.y, telegraphDirection));

        telegraphDirection = null; // Rimuovi il mirino rosso
    }

    // Verifica Line of Sight (Vista retta)
    private boolean hasLineOfSight(double px, double py) {
        int cx = (int) this.x;
        int cy = (int) this.y;
        int tx = (int) px;
        int ty = (int) py;

        if (cx != tx && cy != ty) return false; // Non allineati

        // Verifica ostacoli sulla linea
        return checkPathClear(cx, cy, tx, ty);
    }

    private boolean checkPathClear(int x1, int y1, int x2, int y2) {
        int[][] map = Model.getInstance().getGameAreaArray();

        if (x1 == x2) { // Verticale
            for (int r = Math.min(y1, y2) + 1; r < Math.max(y1, y2); r++)
                if (!Model.getInstance().isWalkable(x1, r)) return false;
        } else { // Orizzontale
            for (int c = Math.min(x1, x2) + 1; c < Math.max(x1, x2); c++)
                if (!Model.getInstance().isWalkable(c, y1)) return false;
        }
        return true;
    }

    @Override
    public Direction getTelegraphDirection() {
        return this.telegraphDirection;
    }
}