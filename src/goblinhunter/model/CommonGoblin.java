package goblinhunter.model;

import goblinhunter.utils.Config;
import goblinhunter.utils.EnemyType;

public class CommonGoblin extends Enemy {

    public CommonGoblin(double startX, double startY) {
        super(startX, startY, Config.GOBLIN_COMMON_SPEED, EnemyType.COMMON);
    }

    @Override
    public void updateBehavior() {
        // walks straight ahead; Enemy.handleWallCollision() redirects on impact
        moveInDirection();
    }
}
