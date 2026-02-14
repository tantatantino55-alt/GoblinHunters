package model;

import utils.Config;
import utils.Direction;
import utils.EnemyType;

public class ChasingGoblin extends Enemy {

    public ChasingGoblin(double startX, double startY, double speed, EnemyType type) {
        super(startX, startY, speed, type);
    }

    // Costruttore per default (se istanziato direttamente)
    public ChasingGoblin(double startX, double startY) {
        super(startX, startY, Config.GOBLIN_COMMON_SPEED, EnemyType.HUNTER);
    }

    @Override
    public void updateBehavior() {
        double px = Model.getInstance().xCoordinatePlayer();
        double py = Model.getInstance().yCoordinatePlayer();

        // 1. PRIORITÀ ASSOLUTA: Scappare dalle bombe (Self Preservation)
        Direction safeDir = getSafeDirectionFromBombs();
        if (safeDir != null) {
            this.currentDirection = safeDir;
            moveInDirection();
            return;
        }

        // 2. LOGICA OLFATTO (Manhattan + Muri)
        double perceivedDistance = calculateSmellDistance(px, py);

        if (perceivedDistance <= Config.SMELL_THRESHOLD_DISTANCE) {
            // Ti ha "fiutato": Insegue
            moveTowards(px, py);
        } else {
            // Troppo lontano o nascosto: Movimento casuale
            moveInDirection();
        }
    }

    // In src/model/ChasingGoblin.java
    protected void moveTowards(double targetX, double targetY) {
        double dx = targetX - this.x;
        double dy = targetY - this.y;

        Direction primary, secondary;

        // Determiniamo la direzione principale basandoci sulla distanza maggiore
        if (Math.abs(dx) > Math.abs(dy)) {
            primary = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
            secondary = (dy > 0) ? Direction.DOWN : Direction.UP;
        } else {
            primary = (dy > 0) ? Direction.DOWN : Direction.UP;
            secondary = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
        }

        // LOGICA DI MOVIMENTO INTELLIGENTE
        if (canMove(primary)) {
            this.currentDirection = primary;
        }
        else if (canMove(secondary)) {
            // SCIVOLAMENTO: Se la via diretta è bloccata, prova a girare l'angolo
            this.currentDirection = secondary;
        }
        else {
            // Se tutto è bloccato, il goblin aspetta senza impazzire
            return;
        }

        moveInDirection();
    }

    private boolean canMove(Direction dir) {
        double nx = x;
        double ny = y;
        switch(dir) {
            case UP -> ny -= speed;
            case DOWN -> ny += speed;
            case LEFT -> nx -= speed;
            case RIGHT -> nx += speed;
        }
        // Verifica se la posizione futura è calpestabile per il sistema di gioco
        return Model.getInstance().isWalkable(nx, ny);
    }



    // --- ALGORITMI DI PERCEZIONE ---

    // Calcola la distanza percepita (Reale + Penalità Muri)
    protected double calculateSmellDistance(double px, double py) {
        double manhattanDist = Math.abs(this.x - px) + Math.abs(this.y - py);
        int obstacles = countObstacles(this.x, this.y, px, py);
        return manhattanDist + (obstacles * Config.SMELL_BLOCK_PENALTY);
    }

    // Conta i blocchi distruttibili nel rettangolo tra nemico e player
    protected int countObstacles(double x1, double y1, double x2, double y2) {
        int startCol = (int) Math.min(x1, x2);
        int endCol = (int) Math.max(x1, x2);
        int startRow = (int) Math.min(y1, y2);
        int endRow = (int) Math.max(y1, y2);

        int count = 0;
        int[][] map = Model.getInstance().getGameAreaArray();

        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {
                // Contiamo solo i blocchi che "assorbono" l'odore
                if (map[r][c] == Config.CELL_DESTRUCTIBLE_BLOCK) {
                    count++;
                }
            }
        }
        return count;
    }

    // Ritorna la direzione di fuga se c'è una bomba vicina
    protected Direction getSafeDirectionFromBombs() {
        int[][] bombs = Model.getInstance().getActiveBombsData();
        for (int[] b : bombs) {
            double dist = Math.abs(b[1] - this.x) + Math.abs(b[0] - this.y);

            if (dist < Config.SAFE_DISTANCE_FROM_BOMB) {
                // Scappa nella direzione opposta
                if (b[1] > this.x) return Direction.LEFT;  // Bomba a destra -> vai a sinistra
                if (b[1] < this.x) return Direction.RIGHT; // Bomba a sinistra -> vai a destra
                if (b[0] > this.y) return Direction.UP;    // Bomba sotto -> vai su
                return Direction.DOWN;                     // Bomba sopra -> vai giù
            }
        }
        return null; // Nessun pericolo
    }
    @Override
    protected void handleWallCollision() {
        // Gli inseguitori non cambiano direzione a caso. Si fermano e
        // aspettano che moveTowards scelga una nuova direzione nel prossimo frame.
    }
}