package goblinhunter.model;

import goblinhunter.utils.Direction;
import goblinhunter.utils.EnemyType;

import java.util.Random;

public abstract class Enemy extends Entity {

    protected Direction currentDirection;
    protected EnemyType type;
    protected Random random;

    protected Direction telegraphDirection = null;
    protected boolean recentlyBounced = false;

    protected int hp = 1;
    protected boolean isDead = false;
    protected long lastHitTime = 0;
    protected long stateStartTime = 0;

    // instance methods

    public Enemy(double startX, double startY, double speed, EnemyType type) {
        super(startX, startY);
        this.speed = speed;
        this.random = new Random();
        this.currentDirection = Direction.getRandom();
        this.type = type;
        this.stateStartTime = System.currentTimeMillis();
    }

    public abstract void updateBehavior();

    public boolean isDead()       { return isDead; }
    public boolean isInvincible() { return false; }

    public boolean takeDamage(int damage) {
        if (isDead || isInvincible()) return false;

        hp -= damage;
        if (hp <= 0) {
            isDead = true;
            return true;
        }

        lastHitTime = System.currentTimeMillis();
        return false;
    }

    public String getEnemyState() { return "RUN"; }
    public long getStateStartTime() { return stateStartTime; }

    // movement

    protected void moveInDirection() {
        if (isDead) return;

        IModel model = Model.getInstance();
        double alignSpeed = speed;

        double deltaX = 0;
        double deltaY = 0;

        switch (currentDirection) {
            case UP    -> deltaY = -speed;
            case DOWN  -> deltaY =  speed;
            case LEFT  -> deltaX = -speed;
            case RIGHT -> deltaX =  speed;
        }

        if (deltaX != 0) {
            double nextX = x + deltaX;
            boolean hitWall  = !model.isWalkable(nextX, y);
            boolean hitEnemy = model.isAreaOccupiedByOtherEnemy(nextX, y, x, y);

            if (!hitWall && !hitEnemy) {
                this.x = nextX;
                double idealY = Math.round(y);
                double diffY = y - idealY;
                if (Math.abs(diffY) > 0.001) {
                    double step = Math.min(alignSpeed, Math.abs(diffY));
                    this.y += (diffY > 0) ? -step : step;
                }
            } else if (hitEnemy) {
                handleEnemyCollision();
            } else {
                handleWallCollision();
            }
        } else if (deltaY != 0) {
            double nextY = y + deltaY;
            boolean hitWall  = !model.isWalkable(x, nextY);
            boolean hitEnemy = model.isAreaOccupiedByOtherEnemy(x, nextY, x, y);

            if (!hitWall && !hitEnemy) {
                this.y = nextY;
                double idealX = Math.round(x);
                double diffX = x - idealX;
                if (Math.abs(diffX) > 0.001) {
                    double step = Math.min(alignSpeed, Math.abs(diffX));
                    this.x += (diffX > 0) ? -step : step;
                }
            } else if (hitEnemy) {
                handleEnemyCollision();
            } else {
                handleWallCollision();
            }
        }
    }

    protected void handleEnemyCollision() {
        java.util.List<Direction> valid = getValidDirections();
        valid.remove(currentDirection);

        if (!valid.isEmpty()) {
            Direction opp = getOppositeDirection();
            java.util.List<Direction> lateral = new java.util.ArrayList<>(valid);
            lateral.remove(opp);

            if (!lateral.isEmpty() && random.nextBoolean()) {
                currentDirection = lateral.get(random.nextInt(lateral.size()));
            } else if (valid.contains(opp)) {
                currentDirection = opp;
            } else {
                currentDirection = valid.get(0);
            }
        }

        recentlyBounced = true;
        resetMemory();
    }

    protected void handleWallCollision() {
        this.x = Math.round(this.x);
        this.y = Math.round(this.y);

        java.util.List<Direction> valid = getValidDirections();
        valid.remove(currentDirection);

        if (!valid.isEmpty()) {
            currentDirection = valid.get(random.nextInt(valid.size()));
        } else {
            currentDirection = getOppositeDirection();
        }

        recentlyBounced = true;
        resetMemory();
    }

    protected Direction getOppositeDirection() {
        return switch (currentDirection) {
            case UP    -> Direction.DOWN;
            case DOWN  -> Direction.UP;
            case LEFT  -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
        };
    }

    protected void resetMemory() {}

    protected java.util.List<Direction> getValidDirections() {
        java.util.List<Direction> valid = new java.util.ArrayList<>();
        double step = 0.5;
        IModel model = Model.getInstance();

        if (model.isWalkable(x, y - step) && !model.isAreaOccupiedByOtherEnemy(x, y - step, x, y))
            valid.add(Direction.UP);
        if (model.isWalkable(x, y + step) && !model.isAreaOccupiedByOtherEnemy(x, y + step, x, y))
            valid.add(Direction.DOWN);
        if (model.isWalkable(x - step, y) && !model.isAreaOccupiedByOtherEnemy(x - step, y, x, y))
            valid.add(Direction.LEFT);
        if (model.isWalkable(x + step, y) && !model.isAreaOccupiedByOtherEnemy(x + step, y, x, y))
            valid.add(Direction.RIGHT);

        return valid;
    }

    public Direction getDirection()          { return currentDirection; }
    public EnemyType getType()               { return type; }
}
