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

        // Distanza Manhattan
        double dist = Math.abs(this.x - px) + Math.abs(this.y - py);

        // Insegue SOLO se vicino (6 celle) E vede il player
        // (Aggiungi hasClearPath se vuoi, per ora semplifichiamo sulla distanza)
        if (dist <= 6) {
            moveTowardsSmart(px, py);
        } else {
            if (random.nextInt(100) < 5) changeDirection();
            moveInDirection();
        }
    }

    // In src/model/ChasingGoblin.java - Algoritmo di movimento "Smart"
    private void moveTowardsSmart(double tx, double ty) {
        double dx = tx - this.x;
        double dy = ty - this.y;
        Direction primary, secondary;

        // Determina le direzioni preferite basate sulla distanza maggiore
        if (Math.abs(dx) > Math.abs(dy)) {
            primary = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
            secondary = (dy > 0) ? Direction.DOWN : Direction.UP;
        } else {
            primary = (dy > 0) ? Direction.DOWN : Direction.UP;
            secondary = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
        }

        // Prova la direzione migliore, se bloccata prova quella secondaria (aggiramento)
        if (canMove(primary)) {
            this.currentDirection = primary;
        } else if (canMove(secondary)) {
            this.currentDirection = secondary;
        } else {
            // Se totalmente bloccato, prova a cambiare direzione casualmente per sbloccarsi
            if (random.nextInt(100) < 10) changeDirection();
        }

        moveInDirection();
    }

    private boolean canMove(Direction d) {
        double nx = x, ny = y;
        double step = 0.5; // Controlla mezzo blocco avanti
        switch(d) {
            case UP -> ny -= step; case DOWN -> ny += step;
            case LEFT -> nx -= step; case RIGHT -> nx += step;
        }
        return Model.getInstance().isWalkable(nx, ny) &&
                !Model.getInstance().isAreaOccupiedByOtherEnemy(nx, ny, this);
    }


    // --- ALGORITMI DI PERCEZIONE ---


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