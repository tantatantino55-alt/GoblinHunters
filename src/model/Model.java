package model;

import utils.Config;
import utils.Direction;
import utils.EnemyType;
import utils.PlayerState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class Model implements IModel {
    private static Model instance = null;
    private final int[][] gameAreaArray;
    private Player player;


    //Attributi per gli enemies
    private List<Enemy> enemies;
    private long lastSpawnTime = 0;
    private final Random randomGenerator = new Random();

    //Attributi bomb
    private final List<Bomb> activeBombs = new ArrayList<>();

    private List<Projectile> projectiles = new ArrayList<>();

    private static final int[][] testMap = {
            {0,1,0,0,0,0,0,0,0,0,0,0,0},
            {0,1,0,0,0,0,0,2,0,0,0,0,0},
            {0,1,1,1,1,0,0,0,0,0,0,0,0},
            {0,0,0,0,1,0,0,0,0,0,0,0,0},
            {1,1,1,0,1,0,0,0,0,0,2,0,0},
            {0,0,2,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,2,0,0,0,0,0,0,0},
            {0,0,2,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,2,0,0,0},
            {0,0,0,0,0,2,2,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0}
    };

    private Model() {
        this.gameAreaArray = new int[Config.GRID_HEIGHT][Config.GRID_WIDTH];
        for (int i = 0; i < Config.GRID_HEIGHT; i++)
            System.arraycopy(testMap[i], 0, gameAreaArray[i], 0, Config.GRID_WIDTH);

        // Il player nasce a (0.0, 0.0) logico
        this.player = new Player(0.0, 0.0);

        this.enemies = new ArrayList<>();
    }

    // In Model.java
    public boolean isWalkable(double nextX, double nextY) {
        double hbW = Config.ENTITY_LOGICAL_HITBOX_WIDTH;
        double hbH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;

        // Calcolo bordi
        double left = nextX + (1.0 - hbW) / 2.0;
        double right = left + hbW - 0.01;
        double top = nextY + (1.0 - hbH); // <--- SE QUESTO È SBAGLIATO, ESCE IN ALTO
        double bottom = nextY + 1.0 - 0.01;

        int startCol = (int) Math.floor(left);
        int endCol = (int) Math.floor(right);
        int startRow = (int) Math.floor(top);
        int endRow = (int) Math.floor(bottom);

        // SE NON HAI QUESTO IF, IL GOBLIN ENTRA NELL'ARIA NERA IN ALTO (startRow < 0)
        if (startCol < 0 || endCol >= Config.GRID_WIDTH || startRow < 0 || endRow >= Config.GRID_HEIGHT) {
            return false;
        }
        // Controllo collisione con i blocchi (muri)
        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {
                if (gameAreaArray[r][c] == Config.CELL_INDESTRUCTIBLE_BLOCK ||
                        gameAreaArray[r][c] == Config.CELL_DESTRUCTIBLE_BLOCK) {
                    return false;
                }
            }
        }
        return true;
    }

    private void updatePlayerMovement() {
        double currentX = player.getXCoordinate();
        double currentY = player.getYCoordinate();
        double deltaX = player.getDeltaX();
        double deltaY = player.getDeltaY();

        updatePlayerAction(deltaX, deltaY);

        if (deltaX == 0 && deltaY == 0) return;

        // Movimento asse X con limiti logici
        double nextX = currentX + deltaX;
        if (isWalkable(nextX, currentY)) {
            player.setXCoordinate(Math.max(Config.MIN_LOGICAL_X, Math.min(Config.MAX_LOGICAL_X, nextX)));
        }

        // Movimento asse Y con limiti logici
        double nextY = currentY + deltaY;
        if (isWalkable(player.getXCoordinate(), nextY)) {
            player.setYCoordinate(Math.max(Config.MIN_LOGICAL_Y, Math.min(Config.MAX_LOGICAL_Y, nextY)));
        }
    }

    @Override
    public void setPlayerDelta(double dx, double dy) {
        // Riceve già valori logici (es. 0.05), li salva direttamente
        this.player.setDelta(dx, dy);
    }



    private void updatePlayerAction(double dx, double dy) {
        if (dx > 0) {
            player.setState(PlayerState.RUN_RIGHT);
        }
        else if (dx < 0) {
            player.setState(PlayerState.RUN_LEFT);
        }
        else if (dy > 0) {
            player.setState(PlayerState.RUN_FRONT);
        }
        else if (dy < 0) {
            player.setState(PlayerState.RUN_BACK);
        }
        else {
            // IL GIOCATORE È FERMO (dx == 0 e dy == 0)
            // Dobbiamo capire in che direzione guardava prima di fermarsi
            updateIdleState();
        }
    }
    private void updateIdleState() {
        PlayerState current = player.getState();

        switch (current) {
            case RUN_RIGHT:
            case IDLE_RIGHT: // Se era già fermo a destra, resta fermo
                player.setState(PlayerState.IDLE_RIGHT);
                break;

            case RUN_LEFT:
            case IDLE_LEFT:
                player.setState(PlayerState.IDLE_LEFT);
                break;

            case RUN_BACK:
            case IDLE_BACK:
                player.setState(PlayerState.IDLE_BACK);
                break;

            case RUN_FRONT:
            case IDLE_FRONT:
            default:
                player.setState(PlayerState.IDLE_FRONT);
                break;
        }
    }
    /*private void updatePlayerAction(double dx, double dy) {
        if (dx > 0) player.setAction("PLAYER_RIGHT_RUNNING", 12);
        else if (dx < 0) player.setAction("PLAYER_LEFT_RUNNING", 12);
        else if (dy > 0) player.setAction("PLAYER_FRONT_RUNNING", 12);
        else if (dy < 0) player.setAction("PLAYER_BACK_RUNNING", 12);
        else {
            String last = player.getCurrentAction();
            if (last.contains("RIGHT")) player.setAction("PLAYER_RIGHT_IDLE", 16);
            else if (last.contains("LEFT")) player.setAction("PLAYER_LEFT_IDLE", 16);
            else if (last.contains("BACK")) player.setAction("PLAYER_BACK_IDLE", 16);
            else player.setAction("PLAYER_FRONT_IDLE", 16);
        }
    }*/

    @Override
    public double xCoordinatePlayer() { return player.getXCoordinate(); }

    @Override
    public double yCoordinatePlayer() { return player.getYCoordinate(); }

    @Override
    public double getPlayerDeltaX() { return player.getDeltaX(); }

    @Override
    public double getPlayerDeltaY() { return player.getDeltaY(); }

    @Override
    public int getNumRows() { return gameAreaArray.length; }

    @Override
    public int getNumColumns() { return gameAreaArray[0].length; }

    @Override
    public int[][] getGameAreaArray() { return gameAreaArray; }

    //Metodi di gestione bombe
    /**
     * Gestisce il ciclo di vita di tutte le bombe attive.
     */
    private void updateBombs() {
        Iterator<Bomb> it = activeBombs.iterator();
        while (it.hasNext()) {
            Bomb b = it.next();
            b.updateDetonationTimer(); // Aggiorna il countdown della singola bomba

            if (b.isExploded()) {
                handleExplosion(b); // Applica gli effetti dell'esplosione sulla mappa
                it.remove();        // Rimuove la bomba per liberare slot al player
            }
        }
    }

    /**
     * Calcola l'area d'impatto dell'esplosione e distrugge i blocchi.
     */
    private void handleExplosion(Bomb b) {
        int r = b.getRow();
        int c = b.getCol();
        int rad = b.getRadius();

        // L'esplosione colpisce il centro e si espande nelle 4 direzioni (croce)
        destroyTile(r, c);
        for (int i = 1; i <= rad; i++) if (!destroyTile(r + i, c)) break; // Giù
        for (int i = 1; i <= rad; i++) if (!destroyTile(r - i, c)) break; // Su
        for (int i = 1; i <= rad; i++) if (!destroyTile(r, c + i)) break; // Destra
        for (int i = 1; i <= rad; i++) if (!destroyTile(r, c - i)) break; // Sinistra
    }

    /**
     * Tenta di distruggere una cella. Ritorna true se l'onda d'urto può proseguire.
     */
    private boolean destroyTile(int r, int c) {
        // Controllo confini della griglia
        if (r < 0 || r >= Config.GRID_HEIGHT || c < 0 || c >= Config.GRID_WIDTH) return false;

        int type = gameAreaArray[r][c]; //

        // Un blocco indistruttibile ferma l'esplosione immediatamente
        if (type == Config.CELL_INDESTRUCTIBLE_BLOCK) return false;

        // Un blocco distruttibile viene rimosso, ma ferma l'onda d'urto
        if (type == Config.CELL_DESTRUCTIBLE_BLOCK) {
            gameAreaArray[r][c] = Config.CELL_EMPTY;
            return false;
        }

        // Se la cella è vuota, l'esplosione prosegue verso la successiva
        return true;
    }

    @Override
    public void PlaceBomb() {
        // 1. Controllo disponibilità bombe
        if (activeBombs.size() < player.getMaxBombs()) {

            // Recuperiamo le dimensioni della hitbox logica da Config
            double hbH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;

            // --- IL CALCOLO CORRETTO ---
            // Il centro X rimane lo stesso (x + 0.5) perché la hitbox è centrata orizzontalmente.
            // Il centro Y deve essere traslato verso il basso, dove si trova la hitbox (i piedi).
            // Invece di +0.5, usiamo il punto medio dell'altezza della hitbox rispetto al fondo della cella.
            double centerX = player.getXCoordinate() + 0.5;
            double centerY = player.getYCoordinate() + (1.0 - (hbH / 2.0));

            int row = (int) Math.floor(centerY);
            int col = (int) Math.floor(centerX);

            // 2. Controllo per non sovrapporre bombe nella stessa cella
            for (Bomb b : activeBombs) {
                if (b.getRow() == row && b.getCol() == col) return;
            }

            // 3. Aggiunta della bomba
            activeBombs.add(new Bomb(row, col, Config.BOMB_DETONATION_TICKS, player.getBombRadius()));
            System.out.println("Bomba piazzata correttamente nella cella: [" + row + "," + col + "]");
        }
    }

    @Override
    public int[][] getActiveBombsData() {
        // Creiamo una matrice: tante righe quante sono le bombe, 2 colonne per [row, col]
        int[][] data = new int[activeBombs.size()][2];

        for (int i = 0; i < activeBombs.size(); i++) {
            Bomb b = activeBombs.get(i);
            data[i][0] = b.getRow(); // Coordinata Y (riga)
            data[i][1] = b.getCol(); // Coordinata X (colonna)
        }
        return data;
    }

    @Override
    public PlayerState getPlayerState() {
        return this.player.getState();
    }


    private void manageSpawning() {
        // Controllo cap massimo (da Config)
        if (enemies.size() >= Config.MAX_ENEMIES_ON_MAP) return;

        long currentTime = System.currentTimeMillis();
        // Controllo intervallo tempo (da Config)
        if (currentTime - lastSpawnTime > Config.SPAWN_INTERVAL_MS) {
            spawnEnemy();
            lastSpawnTime = currentTime;
        }
    }
    // In Model.java, aggiorna il metodo spawnEnemy
    private void spawnEnemy() {
        int r, c;
        int attempts = 0;
        boolean validPosition = false;

        while (!validPosition && attempts < 20) {
            r = randomGenerator.nextInt(Config.GRID_HEIGHT);
            c = randomGenerator.nextInt(Config.GRID_WIDTH);

            if (isValidSpawnPoint(c, r)) {
                validPosition = true;

                // Logica per alternare i tipi in base al numero di nemici già presenti
                int typeIndex = enemies.size() % 3;

                switch (typeIndex) {
                    case 0:
                        enemies.add(new CommonGoblin(c, r));
                        System.out.println("Model: Generato CommonGoblin in (" + c + ", " + r + ")");
                        break;
                    case 1:
                        enemies.add(new ChasingGoblin(c, r));
                        System.out.println("Model: Generato ChasingGoblin in (" + c + ", " + r + ")");
                        break;
                    case 2:
                        enemies.add(new ShooterGoblin(c, r));
                        System.out.println("Model: Generato ShooterGoblin in (" + c + ", " + r + ")");
                        break;
                }
            }
            attempts++;
        }
    }
    private boolean isValidSpawnPoint(int col, int row) {
        // 1. La cella deve essere vuota
        if (gameAreaArray[row][col] != Config.CELL_EMPTY) return false;

        // 2. Distanza di sicurezza dal player (da Config)
        // Evita "Spawn Kill" ingiusti
        double distPlayerX = Math.abs(col - player.getXCoordinate());
        double distPlayerY = Math.abs(row - player.getYCoordinate());

        if (distPlayerX < Config.SPAWN_SAFE_DISTANCE && distPlayerY < Config.SPAWN_SAFE_DISTANCE) {
            return false;
        }

        // 3. (Opzionale) Controlla non ci sia già un altro nemico esattamente lì
        for(Enemy e : enemies) {
            // Controllo approssimativo sulla cella intera
            if ((int)e.getX() == col && (int)e.getY() == row) return false;
        }

        return true;
    }
    // Metodo interno per gestire le collisioni (Player vs Nemici)
    private void checkCollisions() {
        // Recuperiamo le dimensioni delle hitbox da Config
        double pX = player.getXCoordinate();
        double pY = player.getYCoordinate();
        double pHW = Config.ENTITY_LOGICAL_HITBOX_WIDTH;
        double pHH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;

        for (Enemy e : enemies) {
            double eX = e.getX();
            double eY = e.getY();
            double eHW = Config.ENTITY_LOGICAL_HITBOX_WIDTH;
            double eHH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;

            // Logica di intersezione AABB (Axis-Aligned Bounding Box)
            // Due rettangoli si toccano se si sovrappongono sia in orizzontale che verticale
            boolean collisionX = pX < eX + eHW && pX + pHW > eX;
            boolean collisionY = pY < eY + eHH && pY + pHH > eY;

            if (collisionX && collisionY) {
                handlePlayerHit();
                break; // Basta essere colpiti da uno solo per volta
            }
        }
    }

    private void handlePlayerHit() {
        // PER ORA: Stampiamo solo su console (lo implementeremo meglio con le vite dopo)
        System.out.println("!!! COLLISIONE! IL GIOCATORE È STATO COLPITO !!!");

        // TODO Futuro:
        // lives--;
        // if (lives <= 0) gameOver();
        // else resetPositions();
    }

    @Override
    public List<Enemy> getEnemies() {
        return new ArrayList<>(enemies); // Restituisce una copia difensiva (Best Practice)+
    }
    private void updateEnemies() {
        for (Enemy enemy : enemies) {
            enemy.updateBehavior();
        }
    }
    @Override
    public void updateGameLogic() {
        // 1. Prima muovi tutti
        updatePlayerMovement(); // Può restare private!
        updateEnemies();        // private

        // 2. Poi gestisci le bombe (quando le implementerai)
        updateBombs();          // private

        updateProjectiles(); // <--- AGGIUNGI QUESTO

        // 3. Infine controlli chi è morto o cosa è esploso
        checkCollisions();      // private

        // 4. Gestisci lo spawn
        manageSpawning();       // private
    }

    @Override
    public int getEnemyCount() {
        return enemies.size();
    }

    @Override
    public double getEnemyX(int index) {
        // Controllo di sicurezza: se l'indice è valido restituisco X, altrimenti 0
        if (isValidIndex(index)) {
            return enemies.get(index).getX();
        }
        return 0.0;
    }

    @Override
    public double getEnemyY(int index) {
        if (isValidIndex(index)) {
            return enemies.get(index).getY();
        }
        return 0.0;
    }

    @Override
    public Direction getEnemyDirection(int index) {
        if (isValidIndex(index)) {
            return enemies.get(index).getDirection();
        }
        return Direction.DOWN; // Default sicuro
    }

    @Override
    public EnemyType getEnemyType(int index) {
        if (isValidIndex(index)) {
            return enemies.get(index).getType();
        }
        return EnemyType.COMMON; // Default sicuro
    }

    @Override
    public long getPlayerStateStartTime() {
        return player.getStateStartTime();
    }

    // 2. Aggiungi il metodo per aggiungere proiettili (chiamato da ShooterGoblin)
    public void addProjectile(Projectile p) {
        projectiles.add(p);
    }

    // 3. Aggiungi il metodo di aggiornamento
    private void updateProjectiles() {
        Iterator<Projectile> it = projectiles.iterator();
        while (it.hasNext()) {
            Projectile p = it.next();
            p.update(); // Muove il proiettile

            if (!p.isActive()) {
                it.remove(); // Rimuove se ha colpito un muro
                continue;
            }

            // Collisione Proiettile Nemico -> Player
            if (p.isEnemyProjectile()) {
                // Hitbox semplice 0.5 (metà cella)
                if (Math.abs(p.getX() - player.getXCoordinate()) < 0.5 &&
                        Math.abs(p.getY() - player.getYCoordinate()) < 0.5) {

                    handlePlayerHit();
                    p.setActive(false);
                }
            }
        }
    }
    // ... in fondo alla classe Model ...

    @Override
    public Direction getEnemyTelegraph(int index) {
        if (isValidIndex(index)) {
            // Chiediamo al nemico: "Stai mirando?"
            // Nota: getTelegraphDirection() è definito in Enemy (base) e ritorna null,
            // ma ShooterGoblin fa override e ritorna la direzione vera.
            return enemies.get(index).getTelegraphDirection();
        }
        return null;
    }

    @Override
    public List<double[]> getProjectilesData() {
        List<double[]> data = new ArrayList<>();

        for (Projectile p : projectiles) {
            // Convertiamo l'oggetto Projectile in un array di 4 numeri
            double[] info = new double[4];

            info[0] = p.getX(); // Coordinata X
            info[1] = p.getY(); // Coordinata Y

            // Tipo: 0.0 = Proiettile Nemico (Osso), 1.0 = Proiettile Player (Aura)
            info[2] = p.isEnemyProjectile() ? 0.0 : 1.0;

            // Direzione: La convertiamo in numero per semplicità (opzionale, se vuoi ruotare lo sprite)
            // 0=UP, 1=DOWN, 2=LEFT, 3=RIGHT
            info[3] = (double) p.getDirection().ordinal();

            data.add(info);
        }
        return data;
    }


    // Helper privato per evitare crash se la View chiede un indice che non esiste più
    private boolean isValidIndex(int index) {
        return index >= 0 && index < enemies.size();
    }

    public static IModel getInstance() {
        if (instance == null) instance = new Model();
        return instance;
    }
}

