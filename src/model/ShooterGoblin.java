package model;

import utils.Config;
import utils.Direction;
import utils.EnemyType;

public class ShooterGoblin extends ChasingGoblin {

    // =========================================================
    // 1. DICHIARAZIONE VARIABILI (Devono essere qui!)
    // =========================================================
    private int ammo;                     // Colpi nel caricatore
    private int reloadTimer;              // Contatore per la ricarica
    private int telegraphTimer;           // Contatore per la mira (prima di sparare)
    private Direction telegraphDirection; // La direzione in cui sta mirando (ferma)

    // Stati della macchinetta a stati del Goblin
    private enum State { RELOADING, PATROL_OR_CHASE, AIMING }
    private State state;

    // =========================================================
    // 2. COSTRUTTORE
    // =========================================================
    public ShooterGoblin(double startX, double startY) {
        super(startX, startY, Config.GOBLIN_COMMON_SPEED, EnemyType.SHOOTER);
        this.ammo = Config.SHOOTER_MAX_AMMO;
        this.state = State.PATROL_OR_CHASE;
        this.telegraphDirection = null;
        this.reloadTimer = 0;
    }

    // =========================================================
    // 3. LOGICA COMPORTAMENTALE (Update)
    // =========================================================
    @Override
    public void updateBehavior() {
        double px = Model.getInstance().xCoordinatePlayer();
        double py = Model.getInstance().yCoordinatePlayer();

        // A. PRIORITÀ: Scappa dalle bombe (Ereditato da ChasingGoblin ma gestito qui per interrompere la mira)
        Direction safeDir = getSafeDirectionFromBombs();
        if (safeDir != null) {
            this.telegraphDirection = null; // Interrompe la mira!
            this.state = State.PATROL_OR_CHASE;
            this.currentDirection = safeDir;
            moveInDirection();
            return;
        }

        // B. MACCHINA A STATI
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

    // --- GESTIONE STATI ---

    private void handleNormalState(double px, double py) {
        // Se ho colpi e vedo il player -> INIZIA A MIRARE
        if (ammo > 0 && hasLineOfSight(px, py)) {
            startAiming(px, py);
        }
        // Altrimenti mi comporto come un normale inseguitore
        else {
            this.speed = Config.GOBLIN_COMMON_SPEED;
            super.updateBehavior();
        }
    }

    private void handleAimingState() {
        this.speed = Config.SHOOTER_SPEED_AIMING; // Si ferma (0.0)
        telegraphTimer--;

        // Finito il tempo di mira -> SPARA
        if (telegraphTimer <= 0) {
            shoot();

            // LOGICA INTERVALLI:
            if (ammo > 0) {
                // Se ha ancora colpi, torna a cercare/mirare subito
                state = State.PATROL_OR_CHASE;
            } else {
                // Se ha finito i colpi, entra in ricarica (PAUSA LUNGA)
                state = State.RELOADING;
                reloadTimer = Config.SHOOTER_RELOAD_TIME; // <--- FONDAMENTALE: Setta il timer!
            }
        }
    }

    private void handleReloadState(double px, double py) {
        reloadTimer--;

        // Mentre ricarica scappa/insegue velocemente (comportamento aggressivo)
        this.speed = Config.SHOOTER_SPEED_CHASE;
        super.updateBehavior();

        if (reloadTimer <= 0) {
            ammo = Config.SHOOTER_MAX_AMMO;
            state = State.PATROL_OR_CHASE;
            System.out.println("Shooter: Ricarica completata!");
        }
    }

    // --- METODI DI SUPPORTO ---

    private void startAiming(double px, double py) {
        state = State.AIMING;
        telegraphTimer = Config.SHOOTER_TELEGRAPH_TIME; // Pausa pre-sparo (es. 0.5 sec)

        // Calcola la direzione di mira una volta sola e la blocca
        double dx = px - this.x;
        double dy = py - this.y;
        if (Math.abs(dx) > Math.abs(dy))
            telegraphDirection = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
        else
            telegraphDirection = (dy > 0) ? Direction.DOWN : Direction.UP;
    }

    // IL METODO DI SPARO (Ora le variabili esistono!)
    private void shoot() {
        ammo--; // Variabile definita in alto
        System.out.println("Shooter: BANG! Direzione: " + telegraphDirection);

        // Crea il proiettile
        Model.getInstance().addProjectile(new BoneProjectile(this.x, this.y, telegraphDirection));

        telegraphDirection = null; // Rimuove il mirino
    }

    // --- VISTA E OVERRIDE ---

    // Verifica se la strada è libera (niente muri)
    private boolean hasLineOfSight(double px, double py) {
        int cx = (int) (this.x + 0.5); // Centro nemico
        int cy = (int) (this.y + 0.5);
        int tx = (int) (px + 0.5);     // Centro target
        int ty = (int) (py + 0.5);

        if (cx != tx && cy != ty) return false; // Non allineati sugli assi

        return checkPathClear(cx, cy, tx, ty);
    }


    private boolean checkPathClear(int x1, int y1, int x2, int y2) {
        int[][] map = Model.getInstance().getGameAreaArray();
        if (x1 == x2) { // Verticale
            for (int r = Math.min(y1, y2); r <= Math.max(y1, y2); r++) {
                if (map[r][x1] != Config.CELL_EMPTY) return false;
            }
        } else { // Orizzontale
            for (int c = Math.min(x1, x2); c <= Math.max(x1, x2); c++) {
                if (map[y1][c] != Config.CELL_EMPTY) return false;
            }
        }
        return true;
    }

    @Override
    public Direction getTelegraphDirection() {
        return this.telegraphDirection; // Serve alla View per disegnare il mirino/attacco
    }
}