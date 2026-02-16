package model;

import utils.Config;
import utils.EnemyType;

public class CommonGoblin extends Enemy {

    public CommonGoblin(double startX, double startY) {
        super(startX, startY, Config.GOBLIN_COMMON_SPEED, EnemyType.COMMON);
    }

    @Override
    public void updateBehavior() {
        // RIMOSSA LA LOGICA DEL CENTRO CELLA!
        // Ora il Goblin Comune cammina ciecamente dritto.
        // Quando colpirà un muro o un compagno, la fisica di base (Enemy.java)
        // chiamerà in automatico handleWallCollision() per farlo svoltare in una via libera.
        moveInDirection();
    }
}