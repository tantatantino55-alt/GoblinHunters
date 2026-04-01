package model;

import utils.EnemyType;

public class BossGoblin extends CommonGoblin {

    public BossGoblin(double x, double y) {
        super(x, y);
        // Per ora eredita tutto (velocità e movimento casuale) dal CommonGoblin.
        // Se nel tuo Enemy.java hai un modo per settare gli HP (es. this.hp = 10;),
        // puoi farlo qui. Altrimenti per ora morirà con un colpo come gli altri.
    }

    @Override
    public EnemyType getType() {
        return EnemyType.BOSS;
    }
}
