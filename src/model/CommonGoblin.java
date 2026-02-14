package model;

import utils.Config;
import utils.EnemyType;

public class CommonGoblin extends Enemy {

    public CommonGoblin(double startX, double startY) {
        // NESSUN NUMERO MAGICO: Passiamo la costante di Configurazione
        super(startX, startY, Config.GOBLIN_COMMON_SPEED, EnemyType.COMMON);
    }

    @Override
    public void updateBehavior() {
        // Controlla se il goblin è vicino al centro di un tile (punto di decisione)
        boolean atIntersection = Math.abs(x - Math.round(x)) < 0.05 && Math.abs(y - Math.round(y)) < 0.05;

        // Cambia direzione casualmente SOLO se è a un incrocio, non a metà corridoio!
        if (atIntersection) {
            if (random.nextInt(100) < 5) { // 5% di probabilità di girare a un incrocio
                changeDirection();
            }
        }

        moveInDirection();
    }
}