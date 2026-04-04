package model;

import utils.Config;
import utils.ItemType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gestisce il punteggio di gioco, i drop dei goblin e il calcolo
 * dei bonus (tempo boss, perfect level).
 */
class ScoreManager {

    private final Model model;
    private int totalScore = 0;
    private int currentZoneScore = 0;
    private long bossFightStartTime = 0;

    ScoreManager(Model model) {
        this.model = model;
    }

    // ==========================================================
    // PUNTEGGIO
    // ==========================================================

    void addScore(int points, boolean isEnemyKill, int currentZone) {
        if (isEnemyKill && currentZone != 2 && currentZoneScore >= Config.SCORE_ZONE_CAP) {
            System.out.println("Cap raggiunto in questa zona! Sbrigati a trovare il portale!");
            return;
        }
        if (isEnemyKill && currentZone != 2) {
            currentZoneScore += points;
        }
        totalScore += points;
        System.out.println("SCORE: +" + points + " | Punteggio Totale: " + totalScore);
    }

    int getScore() {
        return totalScore;
    }

    void resetZoneScore() {
        currentZoneScore = 0;
    }

    // ==========================================================
    // MORTE NEMICI
    // ==========================================================

    void handleEnemyDeath(Enemy e, int currentZone, List<Collectible> activeItems) {
        if (e instanceof BossGoblin) {
            long timeTakenMs = System.currentTimeMillis() - bossFightStartTime;
            int secondsTaken = (int) (timeTakenMs / 1000);

            int timeBonus = Config.MAX_BOSS_TIME_BONUS - (secondsTaken * Config.BOSS_BONUS_DECAY_PER_SEC);
            if (timeBonus < 0) timeBonus = 0;

            int finalScore = Config.SCORE_BOSS_BASE + timeBonus;
            totalScore += finalScore;

            System.out.println("BOSS SCONFITTO IN " + secondsTaken + " SECONDI!");
            System.out.println("Time Bonus: " + timeBonus + " | Punti Totali Boss: " + finalScore);
        } else {
            int points = 0;
            if (e instanceof ShooterGoblin)       points = Config.SCORE_SHOOTER_GOBLIN;
            else if (e instanceof ChasingGoblin)  points = Config.SCORE_CHASING_GOBLIN;
            else                                   points = Config.SCORE_COMMON_GOBLIN;
            addScore(points, true, currentZone);
        }

        generateGoblinDrop(e.getX(), e.getY(), activeItems, model.getPlayer());
    }

    void startBossFight() {
        this.bossFightStartTime = System.currentTimeMillis();
    }

    // ==========================================================
    // DROP
    // ==========================================================

    void generateGoblinDrop(double x, double y, List<Collectible> activeItems, Player player) {
        List<ItemType> available = new ArrayList<>();
        if (!player.hasShield())     available.add(ItemType.POWER_SHIELD);
        if (!player.hasMaxRadius())  available.add(ItemType.POWER_RADIUS);
        if (!player.hasMaxSpeed())   available.add(ItemType.POWER_SPEED);

        if (available.isEmpty()) {
            System.out.println("Nessun drop: il player ha già tutto maxato!");
            return;
        }

        Random rand = new Random();
        ItemType dropped = available.get(rand.nextInt(available.size()));
        int col = (int) Math.floor(x);
        int row = (int) Math.floor(y);
        activeItems.add(new Collectible(col, row, dropped));
        System.out.println("Goblin droppa: " + dropped.name());
    }
}
