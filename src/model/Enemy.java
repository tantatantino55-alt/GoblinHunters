package model;

import utils.Direction;
import utils.EnemyType;

import java.util.Random;

public abstract class Enemy extends Entity {
    // x, y, speed sono ora ereditati da Entity

    protected Direction currentDirection;
    protected EnemyType type;
    protected Random random;

    protected boolean isChasing = false;
    protected Direction telegraphDirection = null;
    protected boolean recentlyBounced = false;

    // --- VARIABILI PER SALUTE E MORTE ---
    protected int hp = 1;
    protected boolean isDead = false;
    protected long lastHitTime = 0;
    protected long stateStartTime = 0;

    public Enemy(double startX, double startY, double speed, EnemyType type) {
        super(startX, startY); // delega x, y a Entity
        this.speed = speed;
        this.random = new Random();
        this.currentDirection = Direction.getRandom();
        this.type = type;
        this.stateStartTime = System.currentTimeMillis();
    }

    public abstract void updateBehavior();

    // --- METODI VITA E MORTE ---

    public boolean isDead() {
        return isDead;
    }

    public boolean isInvincible() {
        return false;
    }

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


    // --- LOGICA DI MOVIMENTO ---

    protected void moveInDirection() {
        if (isDead) return;

        Model model = (Model) Model.getInstance();
        double alignSpeed = speed;

        double deltaX = 0;
        double deltaY = 0;

        switch (currentDirection) {
            case UP    -> deltaY = -speed;
            case DOWN  -> deltaY = speed;
            case LEFT  -> deltaX = -speed;
            case RIGHT -> deltaX = speed;
        }

        // MOVIMENTO ORIZZONTALE
        if (deltaX != 0) {
            double nextX = x + deltaX;
            boolean hitWall  = !model.isWalkable(nextX, y);
            boolean hitEnemy = model.isAreaOccupiedByOtherEnemy(nextX, y, this);

            if (!hitWall && !hitEnemy) {
                this.x = nextX;
                double idealY = Math.round(y);
                double diffY = y - idealY;
                if (Math.abs(diffY) > 0.001) {
                    double step = Math.min(alignSpeed, Math.abs(diffY));
                    if (diffY > 0) this.y -= step;
                    else           this.y += step;
                }
            } else if (hitEnemy) {
                handleEnemyCollision();
            } else {
                handleWallCollision();
            }
        }
        // MOVIMENTO VERTICALE
        else if (deltaY != 0) {
            double nextY = y + deltaY;
            boolean hitWall  = !model.isWalkable(x, nextY);
            boolean hitEnemy = model.isAreaOccupiedByOtherEnemy(x, nextY, this);

            if (!hitWall && !hitEnemy) {
                this.y = nextY;
                double idealX = Math.round(x);
                double diffX = x - idealX;
                if (Math.abs(diffX) > 0.001) {
                    double step = Math.min(alignSpeed, Math.abs(diffX));
                    if (diffX > 0) this.x -= step;
                    else           this.x += step;
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
            java.util.List<Direction> laterali = new java.util.ArrayList<>(valid);
            laterali.remove(opp);

            if (!laterali.isEmpty() && random.nextBoolean()) {
                currentDirection = laterali.get(random.nextInt(laterali.size()));
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

    protected void resetMemory() { }

    protected java.util.List<Direction> getValidDirections() {
        java.util.List<Direction> valid = new java.util.ArrayList<>();
        double step = 0.5;
        Model model = (Model) Model.getInstance();

        if (model.isWalkable(x, y - step) && !model.isAreaOccupiedByOtherEnemy(x, y - step, this)) valid.add(Direction.UP);
        if (model.isWalkable(x, y + step) && !model.isAreaOccupiedByOtherEnemy(x, y + step, this)) valid.add(Direction.DOWN);
        if (model.isWalkable(x - step, y) && !model.isAreaOccupiedByOtherEnemy(x - step, y, this)) valid.add(Direction.LEFT);
        if (model.isWalkable(x + step, y) && !model.isAreaOccupiedByOtherEnemy(x + step, y, this)) valid.add(Direction.RIGHT);

        return valid;
    }

    protected void changeDirection() {
        java.util.List<Direction> valid = getValidDirections();
        Direction opposite = switch (currentDirection) {
            case UP    -> Direction.DOWN;
            case DOWN  -> Direction.UP;
            case LEFT  -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
        };

        if (valid.size() > 1) valid.remove(opposite);
        if (!valid.isEmpty()) {
            currentDirection = valid.get(random.nextInt(valid.size()));
        }
    }

    public Direction getTelegraphDirection() {
        return null;
    }

    // getX() e getY() sono ereditati da Entity
    public Direction getDirection() { return currentDirection; }
    public EnemyType getType()      { return type; }
}