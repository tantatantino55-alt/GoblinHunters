package goblinhunters.model;

import goblinhunters.utils.Config;
import goblinhunters.utils.Direction;
import goblinhunters.utils.EnemyType;

public class ChasingGoblin extends Enemy {

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

        int currentGridX = (int) Math.round(x);
        int currentGridY = (int) Math.round(y);

        double diffX = Math.abs(x - currentGridX);
        double diffY = Math.abs(y - currentGridY);

        // re-evaluate direction only when snapped to a cell centre to avoid jitter
        if (diffX < speed && diffY < speed) {
            if (currentGridX != lastDecisionX || currentGridY != lastDecisionY) {
                this.x = currentGridX;
                this.y = currentGridY;
                this.lastDecisionX = currentGridX;
                this.lastDecisionY = currentGridY;

                if (recentlyBounced) {
                    recentlyBounced = false;
                } else {
                    decideSmartDirection(px, py);
                }
            }
        }
        moveInDirection();
    }

    private void decideSmartDirection(double tx, double ty) {
        double dx = tx - this.x;
        double dy = ty - this.y;
        Direction primary, secondary;

        // prefer the axis with the larger gap so the goblin converges faster
        if (Math.abs(dx) > Math.abs(dy)) {
            primary   = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
            secondary = (dy > 0) ? Direction.DOWN  : Direction.UP;
        } else {
            primary   = (dy > 0) ? Direction.DOWN  : Direction.UP;
            secondary = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
        }

        java.util.List<Direction> valid = getValidDirections();
        Direction opposite = getOppositeDirection();

        if (valid.size() > 1) valid.remove(opposite); // avoid U-turning unless dead-end

        if (valid.contains(primary)) {
            this.currentDirection = primary;
        } else if (valid.contains(secondary)) {
            this.currentDirection = secondary;
        } else if (!valid.isEmpty()) {
            this.currentDirection = valid.get(0);
        } else {
            this.currentDirection = opposite;
        }
    }

    @Override
    protected void resetMemory() {
        this.lastDecisionX = -1;
        this.lastDecisionY = -1;
    }
}
