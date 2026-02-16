package model;

import utils.Config;
import utils.Direction;
import utils.EnemyType;

public class ChasingGoblin extends Enemy {

    // --- LA SOLUZIONE AL LOOP: Memoria dell'ultimo incrocio ---
    protected int lastDecisionX = -1;
    protected int lastDecisionY = -1;

    public ChasingGoblin(double startX, double startY, double speed, EnemyType type) {
        super(startX, startY, speed, type);
    }

    public ChasingGoblin(double startX, double startY) {
        super(startX, startY, Config.GOBLIN_COMMON_SPEED, EnemyType.HUNTER);
    }

    @Override
    public void updateBehavior() {
        double px = Model.getInstance().xCoordinatePlayer();
        double py = Model.getInstance().yCoordinatePlayer();
        double dist = Math.abs(this.x - px) + Math.abs(this.y - py);

        int currentGridX = (int) Math.round(x);
        int currentGridY = (int) Math.round(y);

        double diffX = Math.abs(x - currentGridX);
        double diffY = Math.abs(y - currentGridY);

        if (diffX < speed && diffY < speed) {
            if (currentGridX != lastDecisionX || currentGridY != lastDecisionY) {

                this.x = currentGridX;
                this.y = currentGridY;

                this.lastDecisionX = currentGridX;
                this.lastDecisionY = currentGridY;

                // LA MAGIA: Se ha appena rimbalzato, NON calcola la rotta per il player,
                // ma gira a caso per districarsi dall'ingorgo!
                if (dist <= 6 && !recentlyBounced) {
                    decideSmartDirection(px, py);
                } else {
                    changeDirection();
                    recentlyBounced = false; // Reset dello stato: dal prossimo incrocio torna a inseguire!
                }
            }
        }

        moveInDirection();
    }

    private void decideSmartDirection(double tx, double ty) {
        double dx = tx - this.x;
        double dy = ty - this.y;
        Direction primary, secondary;

        if (Math.abs(dx) > Math.abs(dy)) {
            primary = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
            secondary = (dy > 0) ? Direction.DOWN : Direction.UP;
        } else {
            primary = (dy > 0) ? Direction.DOWN : Direction.UP;
            secondary = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
        }

        java.util.List<Direction> valid = getValidDirections();

        if (valid.contains(primary)) {
            this.currentDirection = primary;
        } else if (valid.contains(secondary)) {
            this.currentDirection = secondary;
        } else {
            changeDirection(); // Se bloccato, cerca l'uscita
        }
    }

    // --- METODI DI SUPPORTO EREDITATI E BOMB-DODGING ---

    protected int countObstacles(double x1, double y1, double x2, double y2) {
        int startCol = (int) Math.min(x1, x2);
        int endCol = (int) Math.max(x1, x2);
        int startRow = (int) Math.min(y1, y2);
        int endRow = (int) Math.max(y1, y2);

        int count = 0;
        int[][] map = Model.getInstance().getGameAreaArray();

        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {
                if (map[r][c] == Config.CELL_DESTRUCTIBLE_BLOCK) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    protected void resetMemory() {
        // Dimentica l'ultimo incrocio in cui ha preso una decisione.
        // In questo modo, quando rimbalza indietro dopo lo scontro,
        // al prossimo incrocio ricalcolerà immediatamente una nuova rotta per aggirare l'ostacolo!
        this.lastDecisionX = -1;
        this.lastDecisionY = -1;
    }

    protected Direction getSafeDirectionFromBombs() {
        IModel model = Model.getInstance();
        int bombCount = model.getBombCount();

        for (int i = 0; i < bombCount; i++) {
            int bombRow = model.getBombRow(i);
            int bombCol = model.getBombCol(i);

            // Calcola la distanza tra il goblin e la singola bomba
            double bdist = Math.abs(bombCol - this.x) + Math.abs(bombRow - this.y);

            if (bdist < Config.SAFE_DISTANCE_FROM_BOMB) {
                // Scappa nella direzione opposta alla bomba
                if (bombCol > this.x) return Direction.LEFT;
                if (bombCol < this.x) return Direction.RIGHT;
                if (bombRow > this.y) return Direction.UP;
                return Direction.DOWN;
            }
        }
        return null;
    }
}