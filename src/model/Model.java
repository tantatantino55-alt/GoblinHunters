package model;

import utils.*;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Facade del layer Model.
 * Implementa IModel delegando la logica ai manager specializzati.
 * È l'unico punto di accesso visibile dall'esterno del package model.
 */
public class Model implements IModel {

    private static Model instance = null;

    // --- MANAGER ---
    private final MapManager       mapManager;
    private final CollisionManager collisionManager;
    private final SpawnManager     spawnManager;
    private final ScoreManager     scoreManager;
    private final LevelManager     levelManager;

    // --- ENTITÀ ---
    private Player player;
    private List<Enemy> enemies;

    // --- BOMBE, PROIETTILI, FUOCO ---
    private final List<Bomb>          activeBombs        = new ArrayList<>();
    private       List<Projectile>    projectiles        = new ArrayList<>();
    private final List<int[]>         activeFire         = new ArrayList<>();
    private final List<BlockDestruction> destructionEffects = new ArrayList<>();
    private final List<Collectible>   activeItems        = new ArrayList<>();

    // --- CRONOMETRO ---
    private int elapsedTicks = 0;

    /** ID logico della cella che funge da Exit Gate. */
    public static final int EXIT_GATE_ID = 25;

    // ==========================================================
    // COSTRUTTORE / SINGLETON
    // ==========================================================

    private Model() {
        // 1. Crea i manager (usano this come mediatore)
        this.mapManager       = new MapManager();
        this.collisionManager = new CollisionManager(this);
        this.spawnManager     = new SpawnManager(this);
        this.scoreManager     = new ScoreManager(this);
        this.levelManager     = new LevelManager(this);

        // 2. Inizializza player e nemici
        this.player  = new Player(0.0, 0.0);
        this.enemies = new ArrayList<>();

        // 3. Genera mappa iniziale
        int[][] initialMap = mapManager.generateProceduralMap(levelManager.getCurrentZone(), levelManager);
        mapManager.applyMap(initialMap);

        // 4. Spawn nemici iniziali
        for (int i = 0; i < 1; i++) {
            spawnManager.spawnEnemy(enemies, player, mapManager.getGameAreaArray());
        }
    }

    public static IModel getInstance() {
        if (instance == null) instance = new Model();
        return instance;
    }

    // ==========================================================
    // PACKAGE-PRIVATE: accesso per i Manager (Mediator)
    // ==========================================================

    Player getPlayer()                  { return player; }
    List<Enemy> getEnemies()            { return enemies; }
    List<Bomb> getActiveBombs()         { return activeBombs; }
    List<Projectile> getProjectiles()   { return projectiles; }
    List<int[]> getActiveFire()         { return activeFire; }
    List<Collectible> getActiveItemsList() { return activeItems; }
    List<BlockDestruction> getDestructionEffects() { return destructionEffects; }
    SpawnManager getSpawnManager()      { return spawnManager; }
    LevelManager getLevelManager()      { return levelManager; }
    MapManager   getMapManager()        { return mapManager; }

    // ==========================================================
    // IModel – MAPPA
    // ==========================================================

    @Override
    public int[][] generateProceduralMap() {
        return mapManager.generateProceduralMap(levelManager.getCurrentZone(), levelManager);
    }

    @Override public int getNumRows()       { return mapManager.getGameAreaArray().length; }
    @Override public int getNumColumns()    { return mapManager.getGameAreaArray()[0].length; }
    @Override public int[][] getGameAreaArray() { return mapManager.getGameAreaArray(); }

    @Override
    public void destroyBlock(int row, int col) {
        mapManager.destroyBlock(row, col, levelManager.getCurrentZone(),
                activeItems, destructionEffects, levelManager, scoreManager);
    }

    // ==========================================================
    // IModel – PLAYER
    // ==========================================================

    @Override public double xCoordinatePlayer()    { return player.getXCoordinate(); }
    @Override public double yCoordinatePlayer()    { return player.getYCoordinate(); }
    @Override public double getPlayerDeltaX()      { return player.getDeltaX(); }
    @Override public double getPlayerDeltaY()      { return player.getDeltaY(); }
    @Override public PlayerState getPlayerState()  { return player.getState(); }
    @Override public long getPlayerStateStartTime(){ return player.getStateStartTime(); }
    @Override public boolean isPlayerInvincible()  { return player.isInvincible(); }
    @Override public int getPlayerLives()          { return player.getLives(); }
    @Override public int getPlayerBombAmmo()       { return player.getBombAmmo(); }
    @Override public int getPlayerAuraAmmo()       { return player.getAuraAmmo(); }
    @Override public boolean hasPlayerShield()     { return player.hasShield(); }
    @Override public boolean hasPlayerMaxRadius()  { return player.hasMaxRadius(); }
    @Override public boolean hasPlayerMaxSpeed()   { return player.hasMaxSpeed(); }

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
    // IModel – NEMICI
    // ==========================================================

    @Override public int getEnemyCount() { return enemies.size(); }

    @Override public double getEnemyX(int i)          { return isValidIndex(i) ? enemies.get(i).getX() : 0; }
    @Override public double getEnemyY(int i)          { return isValidIndex(i) ? enemies.get(i).getY() : 0; }
    @Override public Direction getEnemyDirection(int i){ return isValidIndex(i) ? enemies.get(i).getDirection() : Direction.DOWN; }
    @Override public EnemyType getEnemyType(int i)    { return isValidIndex(i) ? enemies.get(i).getType() : EnemyType.COMMON; }
    @Override public Direction getEnemyTelegraph(int i){ return isValidIndex(i) ? enemies.get(i).getTelegraphDirection() : null; }
    @Override public String getEnemyState(int i)      { return isValidIndex(i) ? enemies.get(i).getEnemyState() : "RUN"; }
    @Override public boolean isEnemyInvincible(int i) { return isValidIndex(i) && enemies.get(i).isInvincible(); }

    @Override
    public boolean isEnemyAttacking(int i) {
        return isValidIndex(i) && enemies.get(i) instanceof ShooterGoblin
                && ((ShooterGoblin) enemies.get(i)).isActuallyAttacking();
    }

    @Override
    public boolean isEnemyWaiting(int i) {
        return isValidIndex(i) && enemies.get(i) instanceof ShooterGoblin
                && ((ShooterGoblin) enemies.get(i)).isWaiting();
    }

    @Override
    public long getEnemyStateStartTime(int i) {
        return isValidIndex(i) ? enemies.get(i).getStateStartTime() : 0;
    }

    private boolean isValidIndex(int i) { return i >= 0 && i < enemies.size(); }

    // ==========================================================
    // IModel – COLLISIONI (delegano a CollisionManager)
    // ==========================================================

    @Override
    public boolean isWalkable(double nextX, double nextY) {
        return collisionManager.isWalkable(nextX, nextY,
                mapManager.getGameAreaArray(), activeBombs, player, enemies);
    }

    @Override
    public boolean isAreaOccupiedByOtherEnemy(double nextX, double nextY, Enemy self) {
        return collisionManager.isAreaOccupiedByOtherEnemy(nextX, nextY, self, enemies);
    }

    // ==========================================================
    // IModel – BOMBE
    // ==========================================================

    @Override
    public void placeBomb() {
        if (player.getBombAmmo() <= 0) { System.out.println("Bombe esaurite!"); return; }

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
        System.out.println("Bomba piazzata in [" + row + "," + col + "] (Rimaste: " + player.getBombAmmo() + ")");
    }

    @Override public int getBombCount()                     { return activeBombs.size(); }
    @Override public int getBombRow(int i)                  { return isValidBombIndex(i) ? activeBombs.get(i).getRow() : 0; }
    @Override public int getBombCol(int i)                  { return isValidBombIndex(i) ? activeBombs.get(i).getCol() : 0; }
    @Override public int getBombElapsedTime(int i) {
        return isValidBombIndex(i) ? (int)(System.currentTimeMillis() - activeBombs.get(i).getCreationTime()) : 0;
    }
    private boolean isValidBombIndex(int i)  { return i >= 0 && i < activeBombs.size(); }

    private void updateBombs() {
        Iterator<Bomb> it = activeBombs.iterator();
        while (it.hasNext()) {
            Bomb b = it.next();
            b.updateDetonationTimer();
            if (b.isExploded()) { handleExplosion(b); it.remove(); }
        }
    }

    private void handleExplosion(Bomb b) {
        int r = b.getRow(), c = b.getCol(), rad = b.getRadius();
        activeFire.add(new int[]{r, c, 0, Config.FIRE_DURATION_TICKS});
        checkExplosionDamage(r, c);

        Bomb chain = collisionManager.getBombAt(r, c, activeBombs);
        if (chain != null && !chain.isExploded()) chain.detonate();

        expandFire(r, c, -1, 0, rad, 4, 8);
        expandFire(r, c,  1, 0, rad, 5, 1);
        expandFire(r, c,  0,-1, rad, 2, 6);
        expandFire(r, c,  0, 1, rad, 3, 7);
    }

    private void expandFire(int sr, int sc, int dr, int dc, int rad, int midType, int tipType) {
        int[][] map = mapManager.getGameAreaArray();
        for (int i = 1; i <= rad; i++) {
            int cr = sr + dr * i, cc = sc + dc * i;
            if (cr < 0 || cr >= Config.GRID_HEIGHT || cc < 0 || cc >= Config.GRID_WIDTH) break;
            int cell = map[cr][cc];
            if (cell == Config.CELL_INDESTRUCTIBLE_BLOCK || cell == Config.CELL_ORNAMENT || cell == Config.CELL_SKELETON_START) break;
            if (cell == Config.CELL_DESTRUCTIBLE_BLOCK) { destroyBlock(cr, cc); break; }
            boolean tip = (i == rad);
            activeFire.add(new int[]{cr, cc, tip ? tipType : midType, Config.FIRE_DURATION_TICKS});
            checkExplosionDamage(cr, cc);
        }
    }

    // ==========================================================
    // IModel – PROIETTILI
    // ==========================================================

    @Override public void addProjectile(Projectile p) { projectiles.add(p); }
    @Override public int getProjectileCount()         { return projectiles.size(); }
    @Override public double getProjectileX(int i)     { return isValidProjIndex(i) ? projectiles.get(i).getX() : 0; }
    @Override public double getProjectileY(int i)     { return isValidProjIndex(i) ? projectiles.get(i).getY() : 0; }
    @Override public boolean isProjectileEnemy(int i) { return isValidProjIndex(i) && projectiles.get(i).isEnemyProjectile(); }
    @Override public int getProjectileDirection(int i){ return isValidProjIndex(i) ? projectiles.get(i).getDirection().ordinal() : 0; }
    private boolean isValidProjIndex(int i)  { return i >= 0 && i < projectiles.size(); }

    // ==========================================================
    // IModel – FUOCO
    // ==========================================================

    @Override public int getFireCount()      { return activeFire.size(); }
    @Override public int getFireRow(int i)   { return isValidFireIndex(i) ? activeFire.get(i)[0] : 0; }
    @Override public int getFireCol(int i)   { return isValidFireIndex(i) ? activeFire.get(i)[1] : 0; }
    @Override public int getFireType(int i)  { return isValidFireIndex(i) ? activeFire.get(i)[2] : 0; }
    private boolean isValidFireIndex(int i)  { return i >= 0 && i < activeFire.size(); }

    // ==========================================================
    // IModel – DISTRUZIONI
    // ==========================================================

    @Override public int getDestructionCount()         { return destructionEffects.size(); }
    @Override public int getDestructionRow(int i)      { return isValidDestIndex(i) ? destructionEffects.get(i).getRow() : 0; }
    @Override public int getDestructionCol(int i)      { return isValidDestIndex(i) ? destructionEffects.get(i).getCol() : 0; }
    @Override public int getDestructionElapsedTime(int i){
        return isValidDestIndex(i) ? (int)(System.currentTimeMillis() - destructionEffects.get(i).getCreationTime()) : 0;
    }
    private boolean isValidDestIndex(int i)  { return i >= 0 && i < destructionEffects.size(); }

    // ==========================================================
    // IModel – COLLECTIBLE
    // ==========================================================

    @Override public int getCollectibleCount()          { return activeItems.size(); }
    @Override public double getCollectibleX(int i)      { return isValidItemIndex(i) ? activeItems.get(i).getX() : 0; }
    @Override public double getCollectibleY(int i)      { return isValidItemIndex(i) ? activeItems.get(i).getY() : 0; }
    @Override public ItemType getCollectibleType(int i) { return isValidItemIndex(i) ? activeItems.get(i).getType() : ItemType.AMMO_BOMB; }
    @Override public long getCollectibleSpawnTime(int i){ return isValidItemIndex(i) ? activeItems.get(i).getSpawnTime() : 0; }
    private boolean isValidItemIndex(int i)  { return i >= 0 && i < activeItems.size(); }

    // --- CREPE DEL BOSS ---
    @Override public int getCrackCount()     { return mapManager.getCrackCount(); }
    @Override public int getCrackRow(int i)  { return (i >= 0 && i < mapManager.getCrackCount()) ? mapManager.getCrackRow(i) : 0; }
    @Override public int getCrackCol(int i)  { return (i >= 0 && i < mapManager.getCrackCount()) ? mapManager.getCrackCol(i) : 0; }

    // ==========================================================
    // IModel – LIVELLI / GATE / PORTALE
    // ==========================================================

    @Override public int getCurrentZone()              { return levelManager.getCurrentZone(); }
    @Override public int getDifficultyCycle()          { return levelManager.getDifficultyCycle(); }
    @Override public boolean isExitGateActive()        { return levelManager.isExitGateActive(); }
    @Override public boolean isGateActive()            { return levelManager.isExitGateActive(); }
    @Override public boolean isLevelCompletedFlag()    { return levelManager.isLevelCompletedFlag(); }
    @Override public String getCurrentTheme()          { return levelManager.getCurrentTheme(); }
    @Override public int getPortalRow()                { return levelManager.getPortalRow(); }
    @Override public int getPortalCol()                { return levelManager.getPortalCol(); }
    @Override public boolean isPortalRevealed()        { return levelManager.isPortalRevealed(); }
    @Override public long getPortalRevealTime()        { return levelManager.getPortalRevealTime(); }
    @Override public int getExitGateRow()              { return levelManager.getExitGateRow(); }
    @Override public int getExitGateCol()              { return levelManager.getExitGateCol(); }
    @Override public long getExitGateActivationTime()  { return levelManager.getExitGateActivationTime(); }
    @Override public long getGateExitActivationTime()  { return levelManager.getExitGateActivationTime(); }
    @Override public boolean isTransitioning()         { return levelManager.isTransitioning(); }
    @Override public void setTransitioning(boolean t)  { levelManager.setTransitioning(t); }

    @Override public int getScore() { return scoreManager.getScore(); }

    @Override
    public void prepareNextLevel(int[][] ignoredMap) {
        levelManager.prepareNextLevel();
        scoreManager.resetZoneScore();

        player.resetPowerUps();
        player.resetPerfectLevel();

        // Genera mappa DOPO aver avanzato la zona
        int[][] newMap = mapManager.generateProceduralMap(levelManager.getCurrentZone(), levelManager);
        mapManager.applyMap(newMap);

        // Pulisce entità e overlay temporanei
        activeBombs.clear();
        projectiles.clear();
        activeFire.clear();
        activeItems.clear();
        enemies.clear();
        mapManager.clearCracks();  // Rimuove le crepe del Boss al cambio livello

        // Riposiziona player
        player.setXCoordinate(0.0);
        player.setYCoordinate(0.0);
        player.setDelta(0, 0);
        player.setState(PlayerState.IDLE_FRONT);

        // Spawn iniziale (non nella mappa boss)
        if (levelManager.getCurrentZone() != 2) {
            spawnManager.spawnEnemy(enemies, player, mapManager.getGameAreaArray());
        }
    }

    // ==========================================================
    // IModel – AZIONI PLAYER
    // ==========================================================

    @Override
    public void playerShoot() {
        if (!player.canCast() || player.isCasting()) return;
        if (player.getAuraAmmo() <= 0) { System.out.println("Colpi d'Aura esauriti!"); return; }
        player.addAuraAmmo(-1);
        player.startCast();
        player.setDelta(0, 0);
        switch (player.getDirection()) {
            case UP    -> player.setState(PlayerState.CAST_BACK);
            case DOWN  -> player.setState(PlayerState.CAST_FRONT);
            case LEFT  -> player.setState(PlayerState.CAST_LEFT);
            case RIGHT -> player.setState(PlayerState.CAST_RIGHT);
        }
        System.out.println("Player: Inizio lancio Aura... (Munizioni: " + player.getAuraAmmo() + ")");
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

        Rectangle2D.Double hitbox = new Rectangle2D.Double(tX, tY, 0.8, 0.8);
        int[][] map = mapManager.getGameAreaArray();
        int gX = (int) Math.round(tX), gY = (int) Math.round(tY);

        if (gY >= 0 && gY < map.length && gX >= 0 && gX < map[0].length) {
            if (map[gY][gX] == Config.CELL_DESTRUCTIBLE_BLOCK) destroyBlock(gY, gX);
        }

        Iterator<Enemy> eIt = enemies.iterator();
        while (eIt.hasNext()) {
            Enemy e = eIt.next();
            // Il Boss è troppo corazzato: ignorato dallo staff
            if (e.getType() == EnemyType.BOSS) continue;
            if (hitbox.intersects(new Rectangle2D.Double(e.getX(), e.getY(), 0.6, 0.6))) {
                eIt.remove();
                scoreManager.handleEnemyDeath(e, levelManager.getCurrentZone(), activeItems);
                System.out.println("Goblin eliminato con il bastone!");
                break;
            }
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
    // IModel – GAME LOOP
    // ==========================================================

    @Override
    public void updateGameLogic() {
        elapsedTicks++;

        // Timer boss
        if (levelManager.getCurrentZone() == 2 && levelManager.isPreparationPhase()) {
            if (levelManager.tickBossPreparation()) {
                triggerGlobalExplosion();
            }
        }

        // Player (casting o movimento)
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

        updateEnemies();
        updateBombs();
        updateProjectiles();

        // Aggiorna timer crepe Boss (overlay indipendente dalla mappa)
        mapManager.updateCracks();

        checkCollisions();
        spawnManager.manageSpawning(enemies, levelManager.getPortalCol(), levelManager.getPortalRow(), levelManager.isPortalRevealed());

        // Fuoco
        Iterator<int[]> it = activeFire.iterator();
        while (it.hasNext()) {
            int[] f = it.next();
            f[3]--;
            if (f[3] <= 0) it.remove();
            else checkExplosionDamage(f[0], f[1]);
        }

        long now = System.currentTimeMillis();
        destructionEffects.removeIf(bd -> (now - bd.getCreationTime()) > 500);

        if (!levelManager.isLevelCompletedFlag()) {
            levelManager.checkExitGateCollision(enemies, player, mapManager.getGameAreaArray());
        }
    }

    // ==========================================================
    // LOGICA INTERNA PRIVATA
    // ==========================================================

    private void triggerGlobalExplosion() {
        System.out.println("DEBUG: Esplosione tutte le casse...");
        mapManager.destroyAllCrates(activeItems, destructionEffects);
        enemies.add(new BossGoblin(6.0, 5.0));
        scoreManager.startBossFight();
        System.out.println("Il Boss è sceso nell'arena!");
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

        // ASSE X
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
                if (canUp  && dUp   < CORNER_TOLERANCE && (!canDown || dUp < dDown))  { if (dUp   > 0.01) player.setYCoordinate(currentY - Math.min(alignSpeed, dUp));   }
                else if (canDown && dDown < CORNER_TOLERANCE && (!canUp  || dDown < dUp)) { if (dDown > 0.01) player.setYCoordinate(currentY + Math.min(alignSpeed, dDown)); }
            }
        }

        // ASSE Y
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
                else if (canRight && dRight < CORNER_TOLERANCE && (!canLeft  || dRight < dLeft)) { if (dRight > 0.01) player.setXCoordinate(currentX + Math.min(alignSpeed, dRight)); }
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

        // --- COLLISIONE CON NEMICI VIVI ---
        for (Enemy e : enemies) {
            if (e.isDead()) continue;
            boolean cx = pX < e.getX() + pHW && pX + pHW > e.getX();
            boolean cy = pY < e.getY() + pHH && pY + pHH > e.getY();
            if (cx && cy) { handlePlayerHit(); break; }
        }

        // --- DANNO DA CREPE DEL BOSS ---
        // Le crepe sono un overlay: se il player calpesta una crepa attiva subisce danno
        if (!player.isInvincible()) {
            int pCol = (int) Math.floor(pX + pHW / 2.0);
            int pRow = (int) Math.floor(pY + 0.6);  // punto ai piedi del player
            if (mapManager.hasCrackAt(pRow, pCol)) {
                handlePlayerHit();
            }
        }
    }

    private void handlePlayerHit() {
        if (player.isInvincible()) return;
        boolean lifeLost = player.takeDamage();
        if (lifeLost) {
            player.setXCoordinate(0.0);
            player.setYCoordinate(0.0);
            player.setDelta(0, 0);
            player.setState(PlayerState.IDLE_FRONT);
            System.out.println("RESPAWN.");
        }
    }

    private void updateEnemies() {
        for (Enemy e : enemies) e.updateBehavior();
    }

    private void checkExplosionDamage(int row, int col) {
        double expL = col, expR = col + 1.0, expT = row, expB = row + 1.0;

        Iterator<Enemy> it = enemies.iterator();
        while (it.hasNext()) {
            Enemy e = it.next();
            if (e.isDead()) continue;

            double eW = Config.GOBLIN_HITBOX_WIDTH, eH = Config.GOBLIN_HITBOX_HEIGHT;
            double eL = e.getX() + (1.0 - eW) / 2.0, eR = eL + eW;
            double eB = e.getY() + 1.0 - 0.4,         eT = eB - eH;

            if (eL < expR && eR > expL && eT < expB && eB > expT) {
                boolean fatal = e.takeDamage(1);
                if (fatal) {
                    if (e.getType() == EnemyType.BOSS) {
                        System.out.println("IL BOSS È SCONFITTO!");
                    } else {
                        it.remove();
                        scoreManager.handleEnemyDeath(e, levelManager.getCurrentZone(), activeItems);
                        System.out.println("Goblin eliminato da esplosione in [" + row + "," + col + "]!");
                    }
                }
            }
        }

        if (!player.isInvincible()) {
            double pX = player.getXCoordinate(), pY = player.getYCoordinate();
            double pW = Config.ENTITY_LOGICAL_HITBOX_WIDTH, pH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;
            double pL = pX + (1.0 - pW) / 2.0, pR = pL + pW;
            double pB = pY + 1.0 - 0.4,         pT = pB - pH;
            double m = 0.1;
            if ((pL + m) < expR && (pR - m) > expL && (pT + m) < expB && (pB - m) > expT) {
                handlePlayerHit();
            }
        }
    }

    private void updateProjectiles() {
        Iterator<Projectile> it = projectiles.iterator();
        while (it.hasNext()) {
            Projectile p = it.next();
            p.update();
            if (!p.isActive()) { it.remove(); continue; }

            if (p.isEnemyProjectile()) {
                // Proiettile nemico vs Player
                if (Math.abs(p.getX() - player.getXCoordinate()) < 0.5 &&
                    Math.abs(p.getY() - player.getYCoordinate()) < 0.5) {
                    handlePlayerHit();
                    p.setActive(false);
                }
            } else {
                // Proiettile player (Aura) vs Nemici
                Iterator<Enemy> eIt = enemies.iterator();
                boolean hit = false;
                while (eIt.hasNext()) {
                    Enemy e = eIt.next();
                    if (e.isDead()) continue;  // non colpisce i cadaveri
                    if (Math.abs(p.getX() - e.getX()) < 0.6 && Math.abs(p.getY() - e.getY()) < 0.6) {
                        if (e.getType() == EnemyType.BOSS) {
                            // Il Boss non viene mai rimosso dalla lista:
                            // takeDamage() gestisce internamente gli I-Frames.
                            // Il proiettile si distrugge sempre all'impatto.
                            boolean fatal = e.takeDamage(1);
                            if (fatal) {
                                System.out.println("IL BOSS È SCONFITTO! (Aura)");
                                scoreManager.handleEnemyDeath(e, levelManager.getCurrentZone(), activeItems);
                            }
                        } else {
                            eIt.remove();
                            scoreManager.handleEnemyDeath(e, levelManager.getCurrentZone(), activeItems);
                            System.out.println("Goblin fulminato dall'Aura!");
                        }
                        hit = true;
                        break;
                    }
                }
                if (hit) p.setActive(false);
            }
        }
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
        addProjectile(new AuraProjectile(pX, pY, dir));
    }

    private void checkItemPickup() {
        Iterator<Collectible> it = activeItems.iterator();
        double pX = player.getXCoordinate(), pY = player.getYCoordinate();
        while (it.hasNext()) {
            Collectible item = it.next();
            if (Math.abs(pX - item.getX()) < 0.6 && Math.abs(pY - item.getY()) < 0.6) {
                applyItemEffect(item.getType());
                it.remove();
            }
        }
    }

    private void applyItemEffect(ItemType type) {
        switch (type) {
            case AMMO_BOMB   -> player.addBombAmmo(10);
            case AMMO_AURA   -> player.addAuraAmmo(10);
            case POWER_SHIELD -> player.setShield(true);
            case POWER_RADIUS -> player.setMaxRadius(true);
            case POWER_SPEED  -> player.setMaxSpeed(true);
        }
        System.out.println("RACCOLTO: " + type.name());
    }
}
