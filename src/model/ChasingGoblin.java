package model;

import utils.Config;
import utils.Direction;
import utils.EnemyType;

public class ChasingGoblin extends Enemy {

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

        // 1. Self Preservation (Bombe)
        Direction safeDir = getSafeDirectionFromBombs();
        if (safeDir != null) {
            this.currentDirection = safeDir;
            // Se non riesce a scappare, prova a muoversi a caso pur di non stare fermo
            if (!moveInDirection()) changeDirection();
            return;
        }

        // 2. Logica Olfatto
        double dist = calculateSmellDistance(px, py);
        if (dist <= Config.SMELL_THRESHOLD_DISTANCE) {
            moveSmartTowards(px, py); // Usiamo il nuovo metodo intelligente
        } else {
            // Pattuglia: se sbatte cambia direzione
            if (!moveInDirection()) changeDirection();
        }
    }

    // --- NUOVO MOVIMENTO FLUIDO ---
    private void moveSmartTowards(double tx, double ty) {
        double dx = tx - x;
        double dy = ty - y;

        // Tolleranza per l'allineamento (centro cella è .0 o .5 a seconda della logica, qui assumiamo celle intere)
        // Se il goblin è a x=1.1 e deve andare SU, deve prima correggere X verso 1.0 o 1.5

        Direction primary = null;
        Direction secondary = null;

        // Determina asse prioritario
        if (Math.abs(dx) > Math.abs(dy)) {
            primary = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
            secondary = (dy > 0) ? Direction.DOWN : Direction.UP;
        } else {
            primary = (dy > 0) ? Direction.DOWN : Direction.UP;
            secondary = (dx > 0) ? Direction.RIGHT : Direction.LEFT;
        }

        // Tenta PRIMARIO
        this.currentDirection = primary;
        if (canMove(primary)) {
            // Se posso muovermi, fallo. Ma prima... ALLINEAMENTO!
            alignToGrid(primary);
            moveInDirection();
            return;
        }

        // Se primario bloccato, tenta SECONDARIO
        this.currentDirection = secondary;
        if (canMove(secondary)) {
            alignToGrid(secondary);
            moveInDirection();
            return;
        }

        // Se tutto bloccato, stai fermo o prova random (per sbloccarsi dai mucchi)
        changeDirection();
        moveInDirection();
    }

    // Corregge la posizione per entrare nei corridoi
    private void alignToGrid(Direction dir) {
        double alignSpeed = speed;
        // Se vado SU/GIÙ, devo allineare la X
        if (dir == Direction.UP || dir == Direction.DOWN) {
            double cellX = Math.round(x); // Trova la colonna più vicina
            if (Math.abs(x - cellX) > 0.05) { // Se sono disallineato
                if (x < cellX) x += alignSpeed;
                else x -= alignSpeed;
            }
        }
        // Se vado DX/SX, devo allineare la Y
        else if (dir == Direction.LEFT || dir == Direction.RIGHT) {
            double cellY = Math.round(y); // Trova la riga più vicina
            if (Math.abs(y - cellY) > 0.05) {
                if (y < cellY) y += alignSpeed;
                else y -= alignSpeed;
            }
        }
    }

    // Helper per verificare se la strada è libera (Muri + Nemici)
    private boolean canMove(Direction dir) {
        double nx = x; double ny = y;
        switch(dir) {
            case UP: ny -= speed; break;
            case DOWN: ny += speed; break;
            case LEFT: nx -= speed; break;
            case RIGHT: nx += speed; break;
        }
        Model m = (Model) Model.getInstance();
        return m.isWalkable(nx, ny) && m.isSpotFreeFromEnemies(nx, ny, this);
    }

    // ... tieni i metodi calculateSmellDistance e countObstacles come prima ...
    // ... tieni getSafeDirectionFromBombs come prima ...

    protected double calculateSmellDistance(double px, double py) {
        // ... (copia dal codice precedente) ...
        return Math.abs(this.x - px) + Math.abs(this.y - py); // Versione semplice per test
    }

    protected Direction getSafeDirectionFromBombs() {
        // ... (copia dal codice precedente) ...
        return null;
    }
}