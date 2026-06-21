package goblinhunters.model;

import goblinhunters.utils.Config;
import goblinhunters.utils.ItemType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Manages the game score, goblin drops, and bonus calculations (boss time bonus, perfect level). */
class ScoreManager {

    private final Model model;
    private int totalScore = 0;
    private int currentZoneScore = 0;
    private long bossFightStartTime = 0;
    private int bossFightNumber = 0; // 1-indexed at the moment the boss spawns

    ScoreManager(Model model) {
        this.model = model;
    }

    void addScore(int points, boolean isEnemyKill, int currentZone) {
        if (isEnemyKill && currentZone != 2 && currentZoneScore >= Config.SCORE_ZONE_CAP) return;
        if (isEnemyKill && currentZone != 2) {
            currentZoneScore += points;
        }
        totalScore += points;
    }

    int getScore() {
        return totalScore;
    }

    void resetZoneScore() {
        currentZoneScore = 0;
    }

    void handleEnemyDeath(Enemy e, int currentZone, List<Collectible> activeItems) {
        if (e instanceof BossGoblin) {
            long timeTakenMs = System.currentTimeMillis() - bossFightStartTime;
            int secondsTaken = (int) (timeTakenMs / 1000);

            int timeBonus = Config.MAX_BOSS_TIME_BONUS - (secondsTaken * Config.BOSS_BONUS_DECAY_PER_SEC);
            if (timeBonus < 0) timeBonus = 0;

            int finalScore = Config.SCORE_BOSS_BASE + timeBonus;
            totalScore += finalScore;

            if (model.getPlayer().getLives() == Config.INITIAL_LIVES) {
                totalScore += 2000; // perfect boss clear bonus
            }
            model.getPlayer().restoreLives();
            ScoreRepository.getInstance().saveScore(
                MenuModel.getInstance().getPlayerName(), totalScore);
        } else {
            int points = e instanceof ShooterGoblin      ? Config.SCORE_SHOOTER_GOBLIN
                       : e instanceof ChasingGoblin  ? Config.SCORE_CHASING_GOBLIN
                       :                               Config.SCORE_COMMON_GOBLIN;
            addScore(points, true, currentZone);
        }

        generateGoblinDrop(e.getX(), e.getY(), activeItems, model.getPlayer());
    }

    void startBossFight() {
        bossFightNumber++;
        this.bossFightStartTime = System.currentTimeMillis();
    }

    int getBossFightNumber() { return bossFightNumber; }

    void generateGoblinDrop(double x, double y, List<Collectible> activeItems, Player player) {
        Random rand = new Random();

        if (rand.nextInt(100) >= 30) return; // 30% drop chance

        List<ItemType> available = new ArrayList<>();

        // goblins only drop power-ups the player doesn't already have
        if (!player.hasShield())     available.add(ItemType.POWER_SHIELD);
        if (!player.hasMaxRadius())  available.add(ItemType.POWER_RADIUS);
        if (!player.hasMaxSpeed())   available.add(ItemType.POWER_SPEED);

        if (available.isEmpty()) return; // player is already fully powered up

        ItemType dropped = available.get(rand.nextInt(available.size()));

        int col = (int) Math.round(x);
        int row = (int) Math.round(y);

        int[][] map = model.getGameAreaArray();
        if (!isCellEmpty(map, row, col)) {
            // find nearest adjacent empty cell
            int[][] offsets = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
            for (int[] off : offsets) {
                int nr = row + off[0];
                int nc = col + off[1];
                if (isCellEmpty(map, nr, nc)) {
                    row = nr;
                    col = nc;
                    break;
                }
            }
        }

        activeItems.add(new Collectible(col, row, dropped));
    }

    /** Returns true if the map cell is within bounds and empty — valid for a drop. */
    private boolean isCellEmpty(int[][] map, int row, int col) {
        if (row < 0 || row >= map.length || col < 0 || col >= map[0].length) return false;
        return map[row][col] == Config.CELL_EMPTY;
    }
}
