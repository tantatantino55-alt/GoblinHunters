package model;

import utils.Config;
import utils.EnemyType;

public class CommonGoblin extends Enemy {

    public CommonGoblin(double startX, double startY) {
        // NESSUN NUMERO MAGICO: Passiamo la costante di Configurazione
        super(startX, startY, Config.GOBLIN_COMMON_SPEED, EnemyType.COMMON);
    }


// In CommonGoblin.java o ChasingGoblin.java
    

    @Override
    public void updateBehavior() {
        // Calcoliamo quanto siamo lontani dal centro della cella
        double diffX = Math.abs(x - Math.round(x));
        double diffY = Math.abs(y - Math.round(y));

        // Possono cambiare direzione solo se sono molto vicini al centro (NODO)
        if (diffX < speed && diffY < speed) {
            // Se decidono di girare (es. probabilità 10%)
            if (random.nextInt(100) < 10) {
                // Allineamento perfetto prima della rotazione
                this.x = Math.round(x);
                this.y = Math.round(y);
                changeDirection();
            }
        }

        moveInDirection();
    }
}