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
        // Prova a muoverti. Se restituisce false (ha sbattuto), cambia direzione.
        if (!moveInDirection()) {
            changeDirection();
        }
    }
}