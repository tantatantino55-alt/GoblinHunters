package goblinhunters.model;

import goblinhunters.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Model facade. Implements IModel by delegating to specialised managers. */
public class Model implements IModel {

    private static Model instance = null;

    // managers
    private final MapManager       mapManager;
    private final CollisionManager collisionManager;
    private final SpawnManager     spawnManager;
    private final ScoreManager     scoreManager;
    private final LevelManager     levelManager;
    private final BombManager      bombManager;

    // entities
    private Player player;
    private final List<Enemy> enemies = new CopyOnWriteArrayList<>();

    // active game objects — CopyOnWriteArrayList ensures the EDT can read a snapshot
    // during paintComponent without racing against game-thread mutations
    private final List<Bomb>             activeBombs        = new CopyOnWriteArrayList<>();
    private final List<Projectile>       projectiles        = new CopyOnWriteArrayList<>();
    private final List<int[]>            activeFire         = new CopyOnWriteArrayList<>();
    private final List<BlockDestruction> destructionEffects = new CopyOnWriteArrayList<>();
    private final List<Collectible>      activeItems        = new CopyOnWriteArrayList<>();

    private int elapsedTicks = 0;

    public static final int EXIT_GATE_ID = 25;

    private int     playerDyingTimer = 0;
    private boolean gameOverPending  = false;

    // ==========================================================
    // constructor / singleton
    // ==========================================================

    private Model() {
        this.mapManager       = new MapManager();
        this.collisionManager = new CollisionManager(this);
        this.spawnManager     = new SpawnManager(this);
        this.scoreManager     = new ScoreManager(this);
        this.levelManager     = new LevelManager(this);
        this.bombManager      = new BombManager(this, collisionManager, scoreManager);

        this.player = new Player(0.0, 0.0);

        int[][] initialMap = mapManager.generateProceduralMap(levelManager.getCurrentZone(), levelManager);
        mapManager.applyMap(initialMap);

        int initialCount = levelManager.getInitialEnemyCount();
        for (int i = 0; i < initialCount; i++) {
            spawnManager.spawnEnemy(enemies, player, mapManager.getGameAreaArray(), levelManager);
        }
    }

    public static IModel getInstance() {
        if (instance == null) instance = new Model();
        return instance;
    }

    public static void resetInstance() {
        instance = new Model();
    }

    // ==========================================================
    // package-private access for managers (mediator pattern)
    // ==========================================================

    Player getPlayer()                               { return player; }
    List<Enemy> getEnemies()                         { return enemies; }
    List<Bomb> getActiveBombs()                      { return activeBombs; }
    List<Projectile> getProjectiles()                { return projectiles; }
    List<int[]> getActiveFire()                      { return activeFire; }
    List<Collectible> getActiveItemsList()           { return activeItems; }
    List<BlockDestruction> getDestructionEffects()   { return destructionEffects; }
    SpawnManager getSpawnManager()                   { return spawnManager; }
    LevelManager getLevelManager()                   { return levelManager; }
    MapManager   getMapManager()                     { return mapManager; }

    // ==========================================================
    // IModel – map
    // ==========================================================

    @Override
    public int[][] generateProceduralMap() {
        return mapManager.generateProceduralMap(levelManager.getCurrentZone(), levelManager);
    }

    @Override public int getNumRows()            { return mapManager.getGameAreaArray().length; }
    @Override public int getNumColumns()         { return mapManager.getGameAreaArray()[0].length; }
    @Override public int[][] getGameAreaArray()  { return mapManager.getGameAreaArray(); }

    @Override
    public void destroyBlock(int row, int col) {
        mapManager.destroyBlock(row, col, levelManager.getCurrentZone(),
                activeItems, destructionEffects, levelManager, scoreManager);
    }

    // ==========================================================
    // IModel – player
    // ==========================================================

    @Override public double xCoordinatePlayer()      { return player.getXCoordinate(); }
    @Override public double yCoordinatePlayer()      { return player.getYCoordinate(); }
    @Override public double getPlayerDeltaX()        { return player.getDeltaX(); }
    @Override public double getPlayerDeltaY()        { return player.getDeltaY(); }
    @Override public PlayerState getPlayerState()    { return player.getState(); }
    @Override public long getPlayerStateStartTime()  { return player.getStateStartTime(); }
    @Override public boolean isPlayerInvincible()    { return player.isInvincible(); }
    @Override public int getPlayerLives()            { return player.getLives(); }
    @Override public int getPlayerBombAmmo()         { return player.getBombAmmo(); }
    @Override public int getPlayerAuraAmmo()         { return player.getAuraAmmo(); }
    @Override public boolean hasPlayerShield()       { return player.hasShield(); }
    @Override public boolean hasPlayerMaxRadius()    { return player.hasMaxRadius(); }
    @Override public boolean hasPlayerMaxSpeed()     { return player.hasMaxSpeed(); }

    @Override
    public void setPlayerDelta(double dx, double dy) {
        double speed = player.getSpeed();
        if (dx > 0) dx = speed; else if (dx < 0) dx = -speed;
        if (dy > 0) dy = speed; else if (dy < 0) dy = -speed;
        player.setDelta(dx, dy);
    }

    @Override
    public int getElapsedTimeInSeconds() {
        return elapsedTicks / Config.FPS;
    }

    // ==========================================================
    // IModel – enemies
    // ==========================================================

    @Override public int getEnemyCount() { return enemies.size(); }

    @Override public double    getEnemyX(int i)          { return isValidIndex(i) ? enemies.get(i).getX() : 0; }
    @Override public double    getEnemyY(int i)          { return isValidIndex(i) ? enemies.get(i).getY() : 0; }
    @Override public Direction getEnemyDirection(int i)  { return isValidIndex(i) ? enemies.get(i).getDirection() : Direction.DOWN; }
    @Override public EnemyType getEnemyType(int i)       { return isValidIndex(i) ? enemies.get(i).getType() : EnemyType.COMMON; }
    @Override public String getEnemyState(int i) {
        if (player.getState() == PlayerState.DYING) return "IDLE";
        return isValidIndex(i) ? enemies.get(i).getEnemyState() : "RUN";
    }
    @Override public boolean isEnemyInvincible(int i)    { return isValidIndex(i) && enemies.get(i).isInvincible(); }

    @Override
    public long getEnemyStateStartTime(int i) {
        return isValidIndex(i) ? enemies.get(i).getStateStartTime() : 0;
    }

    private boolean isValidIndex(int i) { return i >= 0 && i < enemies.size(); }

    // ==========================================================
    // IModel – collision
    // ==========================================================

    @Override
    public boolean isWalkable(double nextX, double nextY) {
        return collisionManager.isWalkable(nextX, nextY,
                mapManager.getGameAreaArray(), activeBombs, player, enemies);
    }

    @Override
    public boolean isAreaOccupiedByOtherEnemy(double nextX, double nextY, double selfX, double selfY) {
        return collisionManager.isAreaOccupiedByOtherEnemy(nextX, nextY, selfX, selfY, enemies);
    }

    // ==========================================================
    // IModel – bombs
    // ==========================================================

    @Override
    public void placeBomb() {
        if (player.getBombAmmo() <= 0) return;

        double centerX = player.getXCoordinate() + (Config.ENTITY_LOGICAL_HITBOX_WIDTH / 2.0);
        double centerY = player.getYCoordinate() + 0.35;
        int col = (int) Math.floor(centerX);
        int row = (int) Math.floor(centerY);

        if (row < 0 || row >= Config.GRID_HEIGHT || col < 0 || col >= Config.GRID_WIDTH) return;
        int[][] map = mapManager.getGameAreaArray();
        if (map[row][col] != Config.CELL_EMPTY) {
            if (map[row - 1][col] == Config.CELL_EMPTY) row--;
            else return;
        }

        for (Bomb b : activeBombs) if (b.getRow() == row && b.getCol() == col) return;

        player.addBombAmmo(-1);
        activeBombs.add(new Bomb(row, col, Config.BOMB_DETONATION_TICKS, player.getBombRadius()));
    }

    @Override public int getBombCount()          { return activeBombs.size(); }
    @Override public int getBombRow(int i)       { return isValidBombIndex(i) ? activeBombs.get(i).getRow() : 0; }
    @Override public int getBombCol(int i)       { return isValidBombIndex(i) ? activeBombs.get(i).getCol() : 0; }
    @Override public int getBombElapsedTime(int i) {
        return isValidBombIndex(i) ? (int)(System.currentTimeMillis() - activeBombs.get(i).getCreationTime()) : 0;
    }
    private boolean isValidBombIndex(int i) { return i >= 0 && i < activeBombs.size(); }

    // ==========================================================
    // IModel – projectiles
    // ==========================================================

    void addProjectile(Projectile p)                     { projectiles.add(p); }
    @Override public int getProjectileCount()            { return projectiles.size(); }
    @Override public double getProjectileX(int i)        { return isValidProjIndex(i) ? projectiles.get(i).getX() : 0; }
    @Override public double getProjectileY(int i)        { return isValidProjIndex(i) ? projectiles.get(i).getY() : 0; }
    @Override public boolean isProjectileEnemy(int i)    { return isValidProjIndex(i) && projectiles.get(i).isEnemyProjectile(); }
    @Override public int getProjectileDirection(int i)   { return isValidProjIndex(i) ? projectiles.get(i).getDirection().ordinal() : 0; }
    private boolean isValidProjIndex(int i)              { return i >= 0 && i < projectiles.size(); }

    // ==========================================================
    // IModel – fire
    // ==========================================================

    @Override public int getFireCount()     { return activeFire.size(); }
    @Override public int getFireRow(int i)  { return isValidFireIndex(i) ? activeFire.get(i)[0] : 0; }
    @Override public int getFireCol(int i)  { return isValidFireIndex(i) ? activeFire.get(i)[1] : 0; }
    @Override public int getFireType(int i) { return isValidFireIndex(i) ? activeFire.get(i)[2] : 0; }
    private boolean isValidFireIndex(int i) { return i >= 0 && i < activeFire.size(); }

    // ==========================================================
    // IModel – destruction effects
    // ==========================================================

    @Override public int getDestructionCount()           { return destructionEffects.size(); }
    @Override public int getDestructionRow(int i)        { return isValidDestIndex(i) ? destructionEffects.get(i).getRow() : 0; }
    @Override public int getDestructionCol(int i)        { return isValidDestIndex(i) ? destructionEffects.get(i).getCol() : 0; }
    @Override public int getDestructionElapsedTime(int i){
        return isValidDestIndex(i) ? (int)(System.currentTimeMillis() - destructionEffects.get(i).getCreationTime()) : 0;
    }
    private boolean isValidDestIndex(int i) { return i >= 0 && i < destructionEffects.size(); }

    // ==========================================================
    // IModel – collectibles
    // ==========================================================

    @Override public int getCollectibleCount()           { return activeItems.size(); }
    @Override public double getCollectibleX(int i)       { return isValidItemIndex(i) ? activeItems.get(i).getX() : 0; }
    @Override public double getCollectibleY(int i)       { return isValidItemIndex(i) ? activeItems.get(i).getY() : 0; }
    @Override public ItemType getCollectibleType(int i)  { return isValidItemIndex(i) ? activeItems.get(i).getType() : ItemType.AMMO_BOMB; }
    private boolean isValidItemIndex(int i)              { return i >= 0 && i < activeItems.size(); }

    // boss floor cracks
    @Override public int getCrackCount()    { return mapManager.getCrackCount(); }
    @Override public int getCrackRow(int i) { return (i >= 0 && i < mapManager.getCrackCount()) ? mapManager.getCrackRow(i) : 0; }
    @Override public int getCrackCol(int i) { return (i >= 0 && i < mapManager.getCrackCount()) ? mapManager.getCrackCol(i) : 0; }

    // boss HUD — searches the enemy list for the BossGoblin instance
    @Override
    public int getBossHP() {
        for (Enemy e : enemies)
            if (e instanceof BossGoblin boss) return boss.getHP();
        return 0;
    }

    @Override
    public int getBossMaxHP() {
        for (Enemy e : enemies)
            if (e instanceof BossGoblin boss) return boss.getMaxHP();
        return BossGoblin.MAX_HP_CAP;
    }

    // boss portal (zone 2)
    @Override public boolean isBossPortalActive() { return levelManager.isBossPortalActive(); }
    @Override public int getBossPortalRow()       { return levelManager.getBossPortalRow(); }
    @Override public int getBossPortalCol()       { return levelManager.getBossPortalCol(); }

    // ==========================================================
    // IModel – levels / gate / portal
    // ==========================================================

    @Override public int getCurrentZone()             { return levelManager.getCurrentZone(); }
    @Override public int getDifficultyCycle()         { return levelManager.getDifficultyCycle(); }
    @Override public boolean isExitGateActive()       { return levelManager.isExitGateActive(); }
    @Override public boolean isPreparationPhase()     { return levelManager.isPreparationPhase(); }
    @Override public boolean isLevelCompletedFlag()   { return levelManager.isLevelCompletedFlag(); }
    @Override public boolean isGameOverPending()      { return gameOverPending; }
    @Override public void    clearGameOverPending()   { gameOverPending = false; }
    @Override public String getCurrentTheme()         { return levelManager.getCurrentTheme(); }
    @Override public int getPortalRow()               { return levelManager.getPortalRow(); }
    @Override public int getPortalCol()               { return levelManager.getPortalCol(); }
    @Override public boolean isPortalRevealed()       { return levelManager.isPortalRevealed(); }
    @Override public int getExitGateRow()             { return levelManager.getExitGateRow(); }
    @Override public int getExitGateCol()             { return levelManager.getExitGateCol(); }
    @Override public long getExitGateActivationTime() { return levelManager.getExitGateActivationTime(); }
    @Override public boolean isTransitioning()        { return levelManager.isTransitioning(); }
    @Override public void setTransitioning(boolean t) { levelManager.setTransitioning(t); }

    @Override public int getScore() { return scoreManager.getScore(); }

    @Override
    public void prepareNextLevel() {
        levelManager.prepareNextLevel();
        scoreManager.resetZoneScore();

        player.resetPowerUps();
        player.resetPerfectLevel();
        player.clearBossFightSnapshot();

        int[][] newMap = mapManager.generateProceduralMap(levelManager.getCurrentZone(), levelManager);
        mapManager.applyMap(newMap);

        activeBombs.clear();
        projectiles.clear();
        activeFire.clear();
        activeItems.clear();
        enemies.clear();
        mapManager.clearCracks();

        player.setXCoordinate(0.0);
        player.setYCoordinate(0.0);
        player.setDelta(0, 0);
        player.setState(PlayerState.IDLE_FRONT);

        if (levelManager.getCurrentZone() != 2) {
            int initialCount = levelManager.getInitialEnemyCount();
            for (int i = 0; i < initialCount; i++) {
                spawnManager.spawnEnemy(enemies, player, mapManager.getGameAreaArray(), levelManager);
            }
        }
    }

    // ==========================================================
    // IModel – player actions
    // ==========================================================

    @Override
    public void playerShoot() {
        if (!player.canCast() || player.isCasting()) return;
        if (player.getAuraAmmo() <= 0) return;
        player.addAuraAmmo(-1);
        player.startCast();
        player.setDelta(0, 0);
        switch (player.getDirection()) {
            case UP    -> player.setState(PlayerState.CAST_BACK);
            case DOWN  -> player.setState(PlayerState.CAST_FRONT);
            case LEFT  -> player.setState(PlayerState.CAST_LEFT);
            case RIGHT -> player.setState(PlayerState.CAST_RIGHT);
        }
    }

    @Override
    public void staffAttack() {
        Direction dir = player.getDirection();
        double startX = player.getXCoordinate(), startY = player.getYCoordinate();
        double OFFSET = 0.7;

        double tX = startX, tY = startY;
        switch (dir) {
            case RIGHT -> tX += OFFSET;
            case LEFT  -> tX -= OFFSET;
            case DOWN  -> tY += OFFSET;
            case UP    -> tY -= OFFSET;
        }

        int[][] map = mapManager.getGameAreaArray();
        int gX = (int) Math.round(tX), gY = (int) Math.round(tY);

        if (gY >= 0 && gY < map.length && gX >= 0 && gX < map[0].length) {
            if (map[gY][gX] == Config.CELL_DESTRUCTIBLE_BLOCK) destroyBlock(gY, gX);
        }

        Enemy killed = null;
        for (Enemy e : enemies) {
            if (e.getType() == EnemyType.BOSS) continue;
            if (tX < e.getX() + 0.6 && tX + 0.8 > e.getX() &&
                tY < e.getY() + 0.6 && tY + 0.8 > e.getY()) {
                killed = e;
                break;
            }
        }
        if (killed != null) {
            enemies.remove(killed);
            scoreManager.handleEnemyDeath(killed, levelManager.getCurrentZone(), activeItems);
        }

        switch (dir) {
            case UP    -> player.setState(PlayerState.ATTACK_BACK);
            case DOWN  -> player.setState(PlayerState.ATTACK_FRONT);
            case LEFT  -> player.setState(PlayerState.ATTACK_LEFT);
            case RIGHT -> player.setState(PlayerState.ATTACK_RIGHT);
        }
    }

    @Override
    public void resetPlayerStateAfterAction() {
        PlayerState idle = switch (player.getDirection()) {
            case UP    -> PlayerState.IDLE_BACK;
            case DOWN  -> PlayerState.IDLE_FRONT;
            case LEFT  -> PlayerState.IDLE_LEFT;
            case RIGHT -> PlayerState.IDLE_RIGHT;
        };
        player.setState(idle);
    }

    // ==========================================================
    // IModel – game loop
    // ==========================================================

    @Override
    public void updateGameLogic() {
        elapsedTicks++;

        if (player.getState() == PlayerState.DYING) {
            if (playerDyingTimer > 0) {
                playerDyingTimer--;
                if (playerDyingTimer <= 0) {
                    ScoreRepository.getInstance().saveScore(
                        MenuModel.getInstance().getPlayerName(), scoreManager.getScore());
                    this.gameOverPending = true;
                }
            }
            bombManager.updateBombs();
            updateProjectiles();
            mapManager.updateCracks();
            return;
        }

        if (levelManager.getCurrentZone() == 2 && levelManager.isPreparationPhase()) {
            if (levelManager.tickBossPreparation()) {
                triggerGlobalExplosion();
            }
        }

        if (player.isCasting()) {
            player.decrementCastTimer();
            if (player.getCastTimer() <= 0) {
                player.finishCast();
                spawnAuraProjectile();
                updateIdleState();
            }
        } else {
            updatePlayerMovement();
            checkItemPickup();
        }

        // zero-ammo with no active effects on the board → force a respawn to prevent soft-lock
        boolean hasActiveEffects = !activeBombs.isEmpty() || !projectiles.isEmpty()
                || !activeFire.isEmpty() || !destructionEffects.isEmpty();
        boolean hasAmmoCrystals = activeItems.stream().anyMatch(
                item -> item.getType() == ItemType.AMMO_BOMB
                     || item.getType() == ItemType.AMMO_AURA);

        if (player.isOutOfAmmo() && !player.isInvincible()
                && !hasActiveEffects && !hasAmmoCrystals) {
            handlePlayerHit();
        }

        updateEnemies();
        bombManager.updateBombs();
        updateProjectiles();

        mapManager.updateCracks();

        checkCollisions();
        spawnManager.manageSpawning(enemies, levelManager.getPortalCol(), levelManager.getPortalRow(),
                levelManager.isPortalRevealed(), levelManager);

        if (levelManager.getCurrentZone() == 2) {
            spawnManager.manageBossSpawning(enemies, levelManager.isBossPortalActive());
            checkBossPortalDeactivation();
        }

        bombManager.tickFire();

        long now = System.currentTimeMillis();
        destructionEffects.removeIf(bd -> (now - bd.getCreationTime()) > 500);

        if (!levelManager.isLevelCompletedFlag()) {
            levelManager.checkExitGateCollision(enemies, player, mapManager.getGameAreaArray());
        }
    }

    // ==========================================================
    // private logic
    // ==========================================================

    private void triggerGlobalExplosion() {
        mapManager.destroyAllCrates(activeItems, destructionEffects);
        scoreManager.startBossFight(); // increments bossFightNumber before creating the boss
        enemies.add(new BossGoblin(6.0, 5.0, scoreManager.getBossFightNumber()));

        player.snapshotBossFightAmmo();

        levelManager.activateBossPortal();
        spawnManager.resetBossPortalTimer();
    }

    private void checkBossPortalDeactivation() {
        if (!levelManager.isBossPortalActive()) return;

        boolean bossAlive = enemies.stream()
                .anyMatch(e -> e.getType() == EnemyType.BOSS && !e.isDead());
        if (bossAlive) return;

        long livingGoblins = enemies.stream()
                .filter(e -> !e.isDead() && e.getType() != EnemyType.BOSS)
                .count();

        if (livingGoblins == 0) {
            levelManager.deactivateBossPortal();
        }
    }

    private void updatePlayerMovement() {
        PlayerState state = player.getState();
        if (state.name().startsWith("ATTACK")) return;

        double currentX = player.getXCoordinate(), currentY = player.getYCoordinate();
        double deltaX = player.getDeltaX(), deltaY = player.getDeltaY();

        updatePlayerAction(deltaX, deltaY);
        if (deltaX == 0 && deltaY == 0) return;

        double alignSpeed = Config.ENTITY_LOGICAL_SPEED * 1.5;
        double CORNER_TOLERANCE = 0.60;
        int[][] map = mapManager.getGameAreaArray();

        if (deltaX != 0) {
            double rawNextX = currentX + deltaX;
            double clampedNextX = rawNextX;
            int r = (int) Math.round(currentY), c = (int) Math.round(currentX);

            if (deltaX < 0 && collisionManager.isCellBlocked(r, c-1, map, activeBombs, player)) clampedNextX = Math.max(clampedNextX, c);
            if (deltaX > 0 && collisionManager.isCellBlocked(r, c+1, map, activeBombs, player)) clampedNextX = Math.min(clampedNextX, c);

            boolean blocked = !isWalkable(rawNextX, currentY) || collisionManager.isOccupiedByEnemies(rawNextX, currentY, enemies) || clampedNextX != rawNextX;

            if (!blocked) {
                player.setXCoordinate(Math.max(Config.MIN_LOGICAL_X, Math.min(Config.MAX_LOGICAL_X, clampedNextX)));
                double idealY = Math.round(currentY), diffY = currentY - idealY;
                if (Math.abs(diffY) > 0.01 && isWalkable(clampedNextX, idealY) && !collisionManager.isOccupiedByEnemies(clampedNextX, idealY, enemies)) {
                    double step = Math.min(alignSpeed, Math.abs(diffY));
                    player.setYCoordinate(diffY > 0 ? currentY - step : currentY + step);
                }
            } else {
                int targetCol = deltaX < 0 ? c - 1 : c + 1;
                double tYUp = Math.floor(currentY), tYDown = tYUp + 1.0;
                int rUp = (int) Math.round(tYUp), rDown = (int) Math.round(tYDown);
                boolean canUp   = !collisionManager.isCellBlocked(rUp,   targetCol, map, activeBombs, player) && isWalkable(rawNextX, tYUp)   && !collisionManager.isOccupiedByEnemies(rawNextX, tYUp,   enemies);
                boolean canDown = !collisionManager.isCellBlocked(rDown, targetCol, map, activeBombs, player) && isWalkable(rawNextX, tYDown)  && !collisionManager.isOccupiedByEnemies(rawNextX, tYDown, enemies);
                double dUp = Math.abs(currentY - tYUp), dDown = Math.abs(currentY - tYDown);
                if (canUp  && dUp   < CORNER_TOLERANCE && (!canDown || dUp  < dDown)) { if (dUp   > 0.01) player.setYCoordinate(currentY - Math.min(alignSpeed, dUp));   }
                else if (canDown && dDown < CORNER_TOLERANCE && (!canUp || dDown < dUp))  { if (dDown > 0.01) player.setYCoordinate(currentY + Math.min(alignSpeed, dDown)); }
            }
        }

        currentX = player.getXCoordinate(); currentY = player.getYCoordinate();
        if (deltaY != 0) {
            double rawNextY = currentY + deltaY, clampedNextY = rawNextY;
            int r2 = (int) Math.round(currentY), c2 = (int) Math.round(currentX);

            if (deltaY < 0 && collisionManager.isCellBlocked(r2-1, c2, map, activeBombs, player)) clampedNextY = Math.max(clampedNextY, r2);
            if (deltaY > 0 && collisionManager.isCellBlocked(r2+1, c2, map, activeBombs, player)) clampedNextY = Math.min(clampedNextY, r2);

            boolean blocked = !isWalkable(currentX, rawNextY) || collisionManager.isOccupiedByEnemies(currentX, rawNextY, enemies) || clampedNextY != rawNextY;

            if (!blocked) {
                player.setYCoordinate(Math.max(Config.MIN_LOGICAL_Y, Math.min(Config.MAX_LOGICAL_Y, clampedNextY)));
                double idealX = Math.round(currentX), diffX = currentX - idealX;
                if (Math.abs(diffX) > 0.01 && isWalkable(idealX, clampedNextY) && !collisionManager.isOccupiedByEnemies(idealX, clampedNextY, enemies)) {
                    double step = Math.min(alignSpeed, Math.abs(diffX));
                    player.setXCoordinate(diffX > 0 ? currentX - step : currentX + step);
                }
            } else {
                int targetRow = deltaY < 0 ? r2 - 1 : r2 + 1;
                double tXLeft = Math.floor(currentX), tXRight = tXLeft + 1.0;
                int cLeft = (int) Math.round(tXLeft), cRight = (int) Math.round(tXRight);
                boolean canLeft  = !collisionManager.isCellBlocked(targetRow, cLeft,  map, activeBombs, player) && isWalkable(tXLeft,  rawNextY) && !collisionManager.isOccupiedByEnemies(tXLeft,  rawNextY, enemies);
                boolean canRight = !collisionManager.isCellBlocked(targetRow, cRight, map, activeBombs, player) && isWalkable(tXRight, rawNextY) && !collisionManager.isOccupiedByEnemies(tXRight, rawNextY, enemies);
                double dLeft = Math.abs(currentX - tXLeft), dRight = Math.abs(currentX - tXRight);
                if (canLeft  && dLeft  < CORNER_TOLERANCE && (!canRight || dLeft  < dRight)) { if (dLeft  > 0.01) player.setXCoordinate(currentX - Math.min(alignSpeed, dLeft));  }
                else if (canRight && dRight < CORNER_TOLERANCE && (!canLeft || dRight < dLeft))  { if (dRight > 0.01) player.setXCoordinate(currentX + Math.min(alignSpeed, dRight)); }
            }
        }
    }

    private void updatePlayerAction(double dx, double dy) {
        if      (dx > 0) { player.setDirection(Direction.RIGHT); player.setState(PlayerState.RUN_RIGHT); }
        else if (dx < 0) { player.setDirection(Direction.LEFT);  player.setState(PlayerState.RUN_LEFT);  }
        else if (dy > 0) { player.setDirection(Direction.DOWN);  player.setState(PlayerState.RUN_FRONT); }
        else if (dy < 0) { player.setDirection(Direction.UP);    player.setState(PlayerState.RUN_BACK);  }
        else             { updateIdleState(); }
    }

    private void updateIdleState() {
        PlayerState cur = player.getState();
        switch (cur) {
            case RUN_RIGHT, IDLE_RIGHT, CAST_RIGHT, ATTACK_RIGHT -> player.setState(PlayerState.IDLE_RIGHT);
            case RUN_LEFT,  IDLE_LEFT,  CAST_LEFT,  ATTACK_LEFT  -> player.setState(PlayerState.IDLE_LEFT);
            case RUN_BACK,  IDLE_BACK,  CAST_BACK,  ATTACK_BACK  -> player.setState(PlayerState.IDLE_BACK);
            default -> player.setState(PlayerState.IDLE_FRONT);
        }
    }

    private void checkCollisions() {
        double pX = player.getXCoordinate(), pY = player.getYCoordinate();
        double pHW = Config.ENTITY_LOGICAL_HITBOX_WIDTH, pHH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;

        for (Enemy e : enemies) {
            if (e.isDead()) continue;
            boolean cx = pX < e.getX() + pHW && pX + pHW > e.getX();
            boolean cy = pY < e.getY() + pHH && pY + pHH > e.getY();
            if (cx && cy) { handlePlayerHit(); break; }
        }

        // boss floor cracks deal damage via overlap, not just on creation
        if (!player.isInvincible()) {
            int pCol = (int) Math.floor(pX + pHW / 2.0);
            int pRow = (int) Math.floor(pY + 0.6); // foot of the player sprite
            if (mapManager.hasCrackAt(pRow, pCol)) {
                handlePlayerHit();
            }
        }
    }

    void handlePlayerHit() {
        if (player.isInvincible() || player.getState() == PlayerState.DYING) return;
        boolean lifeLost = player.takeDamage();
        if (lifeLost) {
            if (player.getLives() <= 0) {
                player.setState(PlayerState.DYING);
                player.setDelta(0, 0);
                playerDyingTimer = 80; // ~1.3 s pause before the Game Over screen
            } else {
                player.setXCoordinate(0.0);
                player.setYCoordinate(0.0);
                player.setDelta(0, 0);
                player.setState(PlayerState.IDLE_FRONT);

                boolean bossAlive = enemies.stream()
                        .anyMatch(e -> e.getType() == EnemyType.BOSS && !e.isDead());
                if (levelManager.getCurrentZone() == 2 && bossAlive && !player.isOutOfAmmo()) {
                    player.restoreBossFightAmmo();
                } else if (player.isOutOfAmmo()) {
                    player.restoreDefaultAmmo();
                }
            }
        }
    }

    private void updateEnemies() {
        enemies.removeIf(e -> e instanceof BossGoblin && ((BossGoblin) e).isReadyToDespawn());
        for (Enemy e : enemies) {
            e.updateBehavior();
        }
    }

    private void updateProjectiles() {
        for (Projectile p : projectiles) {
            p.update();
            if (!p.isActive()) continue;

            if (p.isEnemyProjectile()) {
                if (Math.abs(p.getX() - player.getXCoordinate()) < 0.5 &&
                    Math.abs(p.getY() - player.getYCoordinate()) < 0.5) {
                    handlePlayerHit();
                    p.setActive(false);
                }
            } else {
                Enemy hit = null;
                for (Enemy e : enemies) {
                    if (e.isDead()) continue;
                    if (Math.abs(p.getX() - e.getX()) < 0.6 && Math.abs(p.getY() - e.getY()) < 0.6) {
                        if (e.getType() == EnemyType.BOSS) {
                            boolean fatal = e.takeDamage(1);
                            if (fatal) scoreManager.handleEnemyDeath(e, levelManager.getCurrentZone(), activeItems);
                        } else {
                            hit = e;
                            scoreManager.handleEnemyDeath(e, levelManager.getCurrentZone(), activeItems);
                        }
                        p.setActive(false);
                        break;
                    }
                }
                if (hit != null) enemies.remove(hit);
            }
        }
        projectiles.removeIf(p -> !p.isActive());
    }

    private void spawnAuraProjectile() {
        double sX = player.getXCoordinate(), sY = player.getYCoordinate();
        Direction dir = player.getDirection();
        double pX = sX, pY = sY;
        switch (dir) {
            case RIGHT -> pX += 0.7;
            case LEFT  -> pX -= 0.7;
            case DOWN  -> pY += 0.7;
            case UP    -> pY -= 0.7;
        }
        projectiles.add(new AuraProjectile(pX, pY, dir));
    }

    private void checkItemPickup() {
        double pX = player.getXCoordinate(), pY = player.getYCoordinate();
        List<Collectible> picked = new ArrayList<>();
        for (Collectible item : activeItems) {
            if (Math.abs(pX - item.getX()) < 0.6 && Math.abs(pY - item.getY()) < 0.6) {
                applyItemEffect(item.getType());
                picked.add(item);
            }
        }
        activeItems.removeAll(picked);
    }

    private void applyItemEffect(ItemType type) {
        switch (type) {
            case AMMO_BOMB    -> player.addBombAmmo(3);
            case AMMO_AURA    -> player.addAuraAmmo(2);
            case POWER_SHIELD -> player.setShield(true);
            case POWER_RADIUS -> player.setMaxRadius(true);
            case POWER_SPEED  -> player.setMaxSpeed(true);
        }
    }
}
