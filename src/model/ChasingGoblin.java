package model;

import utils.Config;
import utils.Direction;
import utils.EnemyType;

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

        // DECISIONE SOLO AL CENTRO DELLA CELLA
        if (diffX < speed && diffY < speed) {
            if (currentGridX != lastDecisionX || currentGridY != lastDecisionY) {
                this.x = currentGridX;
                this.y = currentGridY;
                this.lastDecisionX = currentGridX;
                this.lastDecisionY = currentGridY;

                // Se ho appena sbattuto contro un compagno, scelgo una via a caso per smaltire il traffico
                if (recentlyBounced) {
                    //changeDirection();
                    recentlyBounced = false;
                } else {
                    // Logica "Minvo": Punto il giocatore
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

        // Scelgo l'asse dove la distanza è maggiore
        if (Math.abs(dx) > Math.abs(dy)) {
            primary = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
            secondary = (dy > 0) ? Direction.DOWN : Direction.UP;
        } else {
            primary = (dy > 0) ? Direction.DOWN : Direction.UP;
            secondary = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
        }

        java.util.List<Direction> valid = getValidDirections();
        Direction opposite = getOppositeDirection();

        // Evita di tornare indietro a meno che non sia un vicolo cieco
        if (valid.size() > 1) {
            valid.remove(opposite);
        }

        // 1. Prova la via più diretta
        if (valid.contains(primary)) {
            this.currentDirection = primary;
        }
        // 2. Prova la via secondaria per aggirare l'ostacolo
        else if (valid.contains(secondary)) {
            this.currentDirection = secondary;
        }
        // 3. Prendi una qualsiasi via libera rimasta
        else if (!valid.isEmpty()) {
            this.currentDirection = valid.get(0);
        }
        // 4. Vicolo cieco, torna indietro
        else {
            this.currentDirection = opposite;
        }
    }

    @Override
    protected void resetMemory() {
        this.lastDecisionX = -1;
        this.lastDecisionY = -1;
    }
}