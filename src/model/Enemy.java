package model;

import utils.Direction;
import utils.EnemyType;

import java.util.Random;

public abstract class Enemy extends Entity {
    protected double x;
    protected double y;
    protected double speed;
    protected Direction currentDirection;
    protected EnemyType type;
    protected Random random;

    protected boolean isChasing = false;
    protected Direction telegraphDirection = null; // Se non null, sta mirando

    protected boolean recentlyBounced = false;

    public Enemy(double startX, double startY, double speed, EnemyType type) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.random = new Random();
        this.currentDirection = Direction.getRandom();
        this.type = type;
    }

    public abstract void updateBehavior();

// --- DA INSERIRE IN Enemy.java (sovrascrive il metodo precedente) ---

    // In src/model/Enemy.java
// In Enemy.java

// In src/model/Enemy.java

    protected void moveInDirection() {
        Model model = (Model) Model.getInstance();
        double alignSpeed = speed;

        double deltaX = 0;
        double deltaY = 0;

        switch (currentDirection) {
            case UP -> deltaY = -speed;
            case DOWN -> deltaY = speed;
            case LEFT -> deltaX = -speed;
            case RIGHT -> deltaX = speed;
        }

        // --- MOVIMENTO ORIZZONTALE ---
        if (deltaX != 0) {
            double nextX = x + deltaX;
            boolean hitWall = !model.isWalkable(nextX, y);
            boolean hitEnemy = model.isAreaOccupiedByOtherEnemy(nextX, y, this);

            if (!hitWall && !hitEnemy) {
                this.x = nextX;
                double idealY = Math.round(y);
                double diffY = y - idealY;
                if (Math.abs(diffY) > 0.001) {
                    double step = Math.min(alignSpeed, Math.abs(diffY));
                    if (diffY > 0) this.y -= step;
                    else this.y += step;
                }
            } else if (hitEnemy) {
                handleEnemyCollision(); // Scontro tra Goblin!
            } else {
                handleWallCollision();  // Scontro col muro!
            }
        }
        // --- MOVIMENTO VERTICALE ---
        else if (deltaY != 0) {
            double nextY = y + deltaY;
            boolean hitWall = !model.isWalkable(x, nextY);
            boolean hitEnemy = model.isAreaOccupiedByOtherEnemy(x, nextY, this);

            if (!hitWall && !hitEnemy) {
                this.y = nextY;
                double idealX = Math.round(x);
                double diffX = x - idealX;
                if (Math.abs(diffX) > 0.001) {
                    double step = Math.min(alignSpeed, Math.abs(diffX));
                    if (diffX > 0) this.x -= step;
                    else this.x += step;
                }
            } else if (hitEnemy) {
                handleEnemyCollision(); // Scontro tra Goblin!
            } else {
                handleWallCollision();  // Scontro col muro!
            }
        }
    }

    // --- NUOVI METODI PER GESTIRE LE COLLISIONI ---

    protected void handleEnemyCollision() {
        java.util.List<Direction> valid = getValidDirections();
        valid.remove(currentDirection); // Non riprovare la strada bloccata

        if (!valid.isEmpty()) {
            Direction opp = getOppositeDirection();

            // Cerchiamo le strade "Laterali" (escludendo l'inversione a U se possibile)
            java.util.List<Direction> laterali = new java.util.ArrayList<>(valid);
            laterali.remove(opp);

            // Se c'è una via laterale libera, al 50% di probabilità la prende per sgombrare il corridoio!
            if (!laterali.isEmpty() && random.nextBoolean()) {
                currentDirection = laterali.get(random.nextInt(laterali.size()));
            } else if (valid.contains(opp)) {
                currentDirection = opp; // Altrimenti fa retromarcia
            } else {
                currentDirection = valid.get(0);
            }
        }

        recentlyBounced = true; // Si segna che è in stato di "Fuga dal traffico"
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
            currentDirection = getOppositeDirection(); // Vicolo cieco
        }

        resetMemory(); // Cancella la memoria anche se sbatte su un muro
    }

    protected Direction getOppositeDirection() {
        return switch (currentDirection) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
        };
    }

    // Metodo vuoto di base, verrà sovrascritto dall'inseguitore
    protected void resetMemory() { }


    // NUOVO METODO: Il Goblin si guarda intorno e vede quali strade sono libere
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



    // AGGIORNATO: Gira a caso, ma MAI contro un muro e MAI tornando indietro se non costretto
    protected void changeDirection() {
        java.util.List<Direction> valid = getValidDirections();
        Direction opposite = switch (currentDirection) {
            case UP -> Direction.DOWN;   case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT; case RIGHT -> Direction.LEFT;
        };

        if (valid.size() > 1) valid.remove(opposite); // Evita l'effetto "cammina avanti e indietro"

        if (!valid.isEmpty()) {
            currentDirection = valid.get(random.nextInt(valid.size()));
        }
    }

    // Default: nessun telegraph. ShooterGoblin farà l'override.
    public Direction getTelegraphDirection() {
        return null;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public Direction getDirection() { return currentDirection; }
    public EnemyType getType() { return type; }
}