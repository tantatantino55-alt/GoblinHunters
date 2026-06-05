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
    private int bossFightNumber = 0; // quante volte si è combattuto il boss (1-indexed al momento dello spawn)

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

            if (model.getPlayer().getLives() == Config.INITIAL_LIVES) {
                totalScore += 2000;
                System.out.println("PERFECT BOSS! Bonus +2000 | Totale: " + totalScore);
            }
            model.getPlayer().restoreLives();
            ScoreRepository.getInstance().saveScore(
                MenuModel.getInstance().getPlayerName(), totalScore);
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
        bossFightNumber++;
        this.bossFightStartTime = System.currentTimeMillis();
        System.out.println("[BOSS] Fight #" + bossFightNumber + " | HP: " + BossGoblin.computeHP(bossFightNumber));
    }

    int getBossFightNumber() { return bossFightNumber; }

    // ==========================================================
    // DROP
    // ==========================================================

    void generateGoblinDrop(double x, double y, List<Collectible> activeItems, Player player) {
        Random rand = new Random();

        // Probabilità del 30% di droppare un oggetto
        if (rand.nextInt(100) >= 30) {
            return;
        }

        List<ItemType> available = new ArrayList<>();

        // Aggiunge i power-up mancanti al pool (i goblin droppano SOLO power-up)
        if (!player.hasShield())     available.add(ItemType.POWER_SHIELD);
        if (!player.hasMaxRadius())  available.add(ItemType.POWER_RADIUS);
        if (!player.hasMaxSpeed())   available.add(ItemType.POWER_SPEED);

        // Se il player ha già tutto maxato, non droppa nulla
        if (available.isEmpty()) {
            return;
        }

        ItemType dropped = available.get(rand.nextInt(available.size()));

        // Arrotondiamo al centro logico del goblin per ottenere la cella corretta
        int col = (int) Math.round(x);
        int row = (int) Math.round(y);

        // Verifica che la cella sia valida e calpestabile (non muro/cornice)
        int[][] map = model.getGameAreaArray();
        if (!isCellEmpty(map, row, col)) {
            // Cerca la cella vuota più vicina tra le 4 adiacenti
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
        System.out.println("Goblin droppa: " + dropped.name() + " @ [" + row + "," + col + "]");
    }

    /**
     * Controlla se una cella della mappa è vuota e valida per il drop.
     */
    private boolean isCellEmpty(int[][] map, int row, int col) {
        if (row < 0 || row >= map.length || col < 0 || col >= map[0].length) return false;
        return map[row][col] == Config.CELL_EMPTY;
    }
}
