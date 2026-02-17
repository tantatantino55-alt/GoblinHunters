package model;

import utils.Config;
import utils.Direction;
import utils.EnemyType;

public class ShooterGoblin extends ChasingGoblin {

    private int ammo;
    private int reloadTimer;
    private int telegraphTimer;
    private int attackAnimTimer; // Nuovo timer dedicato solo ai 2 frame dell'animazione di attacco
    private Direction telegraphDirection;

    // Stati più precisi per l'attacco
    private enum State { RELOADING, PATROL_OR_CHASE, TELEGRAPHING, ATTACKING }
    private State state;

    // --- VARIABILI PER L'OFFSET DEL PROIETTILE (Modificale per testare) ---
    // Questi valori si sommano alla coordinata X/Y (logica) del Goblin.
    // 1.0 = 1 Cella. 0.5 = Mezza Cella.
    private final double OFFSET_X_RIGHT = 0.6;
    private final double OFFSET_X_LEFT = -0.6;
    private final double OFFSET_Y_DOWN = 0.6;
    private final double OFFSET_Y_UP = -0.6;

    private java.util.List<Projectile> activeProjectiles;

    public ShooterGoblin(double startX, double startY) {
        super(startX, startY, Config.GOBLIN_COMMON_SPEED, EnemyType.SHOOTER);
        this.ammo = Config.SHOOTER_MAX_AMMO;
        this.state = State.PATROL_OR_CHASE;
        this.telegraphDirection = null;
        this.reloadTimer = 0;

        this.activeProjectiles = new java.util.ArrayList<>();
    }

    @Override
    public void updateBehavior() {
        double px = Model.getInstance().xCoordinatePlayer();
        double py = Model.getInstance().yCoordinatePlayer();

        // NESSUNA FUGA DALLA BOMBA: Ora la vedono come un muro!

        switch (state) {
            case RELOADING:
                handleReloadState(px, py);
                break;
            case TELEGRAPHING:
                handleTelegraphState();
                break;
            case ATTACKING:
                handleAttackingState();
                break;
            case PATROL_OR_CHASE:
                handleNormalState(px, py);
                break;
        }
    }
    private void handleNormalState(double px, double py) {
        if (ammo > 0 && hasLineOfSight(px, py)) {
            startTelegraphing(px, py);
        } else {
            this.speed = Config.GOBLIN_COMMON_SPEED;
            super.updateBehavior();
        }
    }

    // FASE 1: Fermo, ti fissa per 0.5 secondi (Usa l'animazione di Idle o Corsa sul posto)
    private void startTelegraphing(double px, double py) {
        state = State.TELEGRAPHING;
        telegraphTimer = Config.SHOOTER_TELEGRAPH_TIME; // Es. 30 tick = 0.5s

        double dx = px - this.x;
        double dy = py - this.y;

        if (Math.abs(dx) > Math.abs(dy))
            telegraphDirection = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
        else
            telegraphDirection = (dy > 0) ? Direction.DOWN : Direction.UP;

        this.currentDirection = telegraphDirection;
        this.speed = 0.0;

        // --- AGGIUNGI QUESTE DUE RIGHE ---
        // Forza il goblin al centro perfetto del binario prima di sparare.
        // Impedisce che quando riparte raschi contro gli spigoli dei muri!
        this.x = Math.round(this.x);
        this.y = Math.round(this.y);
    }

    private void handleTelegraphState() {
        telegraphTimer--;
        if (telegraphTimer <= 0) {
            startAttacking();
        }
    }

    // FASE 2: Esegue i 2 frame di attacco
    private void startAttacking() {
        state = State.ATTACKING;
        // Supponendo 80ms a frame e 60 FPS di logica, 2 frame durano circa 10 tick.
        // Modifica questo valore se vuoi l'animazione più lenta o più veloce
        attackAnimTimer = 10;
    }

    private void handleAttackingState() {
        attackAnimTimer--;
        if (attackAnimTimer <= 0) {
            shoot();

            if (ammo > 0) {
                state = State.PATROL_OR_CHASE;
            } else {
                state = State.RELOADING;
                reloadTimer = Config.SHOOTER_RELOAD_TIME;
            }
        }
    }

    private void shoot() {
        ammo--;

        double projX = this.x;
        double projY = this.y;

        switch (telegraphDirection) {
            case RIGHT -> projX += OFFSET_X_RIGHT;
            case LEFT -> projX += OFFSET_X_LEFT;
            case DOWN -> projY += OFFSET_Y_DOWN;
            case UP -> projY += OFFSET_Y_UP;
        }

        Projectile p = new BoneProjectile(projX, projY, telegraphDirection);
        Model.getInstance().addProjectile(p);

        // Salviamo il proiettile nella memoria del goblin per seguirne il volo!
        activeProjectiles.add(p);

        telegraphDirection = null;
    }

    private void handleReloadState(double px, double py) {
        // 1. Rimuove automaticamente dalla memoria le ossa che si sono schiantate
        activeProjectiles.removeIf(p -> !p.isActive());

        // 2. FASE DI ATTESA: Finché c'è almeno un osso in volo, il goblin sta FERMO a guardare
        if (!activeProjectiles.isEmpty()) {
            this.speed = 0.0;
            return; // Interrompe qui e non fa nient'altro
        }

        // 3. FASE DI COOLDOWN: Ossa distrutte. Inizia a ricaricare, ma intanto ti insegue!
        reloadTimer--;

        // Rimettiamo la sua velocità normale da inseguitore
        this.speed = Config.GOBLIN_COMMON_SPEED;

        // Chiamiamo la logica di movimento dell'Inseguitore base (ChasingGoblin)
        super.updateBehavior();

        // 4. FINE COOLDOWN: Il timer è scaduto, il caricatore è di nuovo pieno
        if (reloadTimer <= 0) {
            ammo = Config.SHOOTER_MAX_AMMO;
            state = State.PATROL_OR_CHASE;
            System.out.println("Shooter: Ricarica completata! Torno a mirare.");
        }
    }

    // Aggiungi questo in fondo alla classe, serve alla grafica:
    public boolean isWaiting() {
        return state == State.RELOADING && !activeProjectiles.isEmpty();
    }

    // --- METODI DI VISTA E CONTROLLO ---
    private boolean hasLineOfSight(double px, double py) {
        int cx = (int) (this.x + 0.5);
        int cy = (int) (this.y + 0.5);
        int tx = (int) (px + 0.5);
        int ty = (int) (py + 0.5);

        if (cx != tx && cy != ty) return false;
        return checkPathClear(cx, cy, tx, ty);
    }

    private boolean checkPathClear(int x1, int y1, int x2, int y2) {
        int[][] map = Model.getInstance().getGameAreaArray();
        if (x1 == x2) {
            for (int r = Math.min(y1, y2); r <= Math.max(y1, y2); r++) {
                if (map[r][x1] != Config.CELL_EMPTY) return false;
            }
        } else {
            for (int c = Math.min(x1, x2); c <= Math.max(x1, x2); c++) {
                if (map[y1][c] != Config.CELL_EMPTY) return false;
            }
        }
        return true;
    }

    @Override
    public Direction getTelegraphDirection() {
        return this.telegraphDirection;
    }

    // NUOVO METODO PER LA VIEW: Dice alla View se deve disegnare i 2 frame di attacco!
    public boolean isActuallyAttacking() {
        return state == State.ATTACKING;
    }
}