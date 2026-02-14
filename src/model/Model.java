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

    // Variabile per il cronometro
    private int elapsedTicks = 0;


    //Attributi per gli enemies
    private List<Enemy> enemies;
    private long lastSpawnTime = 0;
    private final Random randomGenerator = new Random();

    //Attributi bomb
    private final List<Bomb> activeBombs = new ArrayList<>();

    private List<Projectile> projectiles = new ArrayList<>();

    // Lista che tiene traccia dei blocchi appena distrutti per la View
    private final List<BlockDestruction> destructionEffects = new ArrayList<>();

    private final List<int[]> activeFire = new ArrayList<>(); // [row, col, type, timestamp]
    // Lista per il fuoco: int[]{row, col, type, timestamp}


    private static final int[][] testMap = {
            {0, 0, 2, 2, 0, 2, 2, 0, 2, 2, 0, 0, 0}, // Riga 0: Angolo player sicuro
            {0, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 0}, // Riga 1: Pilastri e casse
            {2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2}, // Riga 2: Corridoio libero
            {2, 1, 0, 1, 2, 1, 2, 1, 2, 1, 0, 1, 2}, // Riga 3
            {0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0}, // Riga 4: Arena centrale
            {2, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 2}, // Riga 5: Pilastri centrali
            {0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0}, // Riga 6: Arena centrale
            {2, 1, 0, 1, 2, 1, 2, 1, 2, 1, 0, 1, 2}, // Riga 7
            {2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2}, // Riga 8
            {0, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 0}, // Riga 9
            {0, 0, 0, 2, 2, 0, 2, 2, 0, 2, 2, 0, 0}  // Riga 10
    };
   // 0 = Vuoto
   // 1 = Muro Indistruttibile (Test Sliding)
   // 0 = Vuoto
   // 1 = Muro Indistruttibile (Test Sliding)
// 0 = Vuoto
   // 1 = Muro Indistruttibile (Test Sliding)

    private Model() {
        this.gameAreaArray = new int[Config.GRID_HEIGHT][Config.GRID_WIDTH];
        for (int i = 0; i < Config.GRID_HEIGHT; i++)
            System.arraycopy(testMap[i], 0, gameAreaArray[i], 0, Config.GRID_WIDTH);

        // Il player nasce a (0.0, 0.0) logico
        this.player = new Player(0.0, 0.0);

        this.enemies = new ArrayList<>();
    }
    // In src/model/Model.java

    // Sostituisci il vecchio isWalkable con questo basato su GRIGLIA RIGIDA
    // In src/model/Model.java

    // Sostituisci il vecchio isWalkable
    // In src/model/Model.java




// In src/model/Model.java

    // In src/model/Model.java

    // --- A. LOGICA DI MOVIMENTO RIGIDO (BOMBERMAN STYLE) ---
    private void updatePlayerMovement() {
        double x = player.getXCoordinate();
        double y = player.getYCoordinate();
        double dx = player.getDeltaX();
        double dy = player.getDeltaY();
        double speed = player.getSpeed();

        // 1. Gestione Animazione: Se non premo nulla, sono fermo.
        if (dx == 0 && dy == 0) {
            player.setMoving(false);
            return;
        }
        player.setMoving(true);

        // 2. Gestione Direzione Animazione (importante per non rimanere in FRONT_IDLE)
        if (dy < 0) player.setDirection(Direction.UP);
        else if (dy > 0) player.setDirection(Direction.DOWN);
        else if (dx < 0) player.setDirection(Direction.LEFT);
        else if (dx > 0) player.setDirection(Direction.RIGHT);

        // 3. Logica Movimento Rigido (Bomberman)
        // Se mi muovo in orizzontale, devo essere allineato verticalmente
        if (dx != 0) {
            double distY = y - Math.round(y); // Quanto sono lontano dal centro riga?

            if (Math.abs(distY) < 0.1) {
                // SONO ALLINEATO: Mi muovo su X
                player.setYCoordinate(Math.round(y)); // Snap perfetto al centro
                double nextX = x + dx;

                if (isWalkable(nextX, y)) {
                    // Controllo collisione nemici (Morte)
                    if (isAreaOccupiedByOtherEnemy(nextX, y, null)) {
                        player.setState(PlayerState.DEAD);
                    } else {
                        player.setXCoordinate(nextX);
                    }
                }
            } else {
                // NON ALLINEATO: Correggo Y invece di muovere X (Auto-Align)
                double fixSpeed = (distY > 0) ? -speed : speed;
                player.setYCoordinate(y + fixSpeed);
            }
        }
        // Se mi muovo in verticale, devo essere allineato orizzontalmente
        else if (dy != 0) {
            double distX = x - Math.round(x); // Quanto sono lontano dal centro colonna?

            if (Math.abs(distX) < 0.1) {
                // SONO ALLINEATO: Mi muovo su Y
                player.setXCoordinate(Math.round(x)); // Snap perfetto al centro
                double nextY = y + dy;

                if (isWalkable(x, nextY)) {
                    if (isAreaOccupiedByOtherEnemy(x, nextY, null)) {
                        player.setState(PlayerState.DEAD);
                    } else {
                        player.setYCoordinate(nextY);
                    }
                }
            } else {
                // NON ALLINEATO: Correggo X invece di muovere Y
                double fixSpeed = (distX > 0) ? -speed : speed;
                player.setXCoordinate(x + fixSpeed);
            }
        }
    }

    @Override
    public boolean isWalkable(double x, double y) {
        int c = (int) Math.round(x);
        int r = (int) Math.round(y);
        if (c < 0 || c >= Config.GRID_WIDTH || r < 0 || r >= Config.GRID_HEIGHT) return false;
        return gameAreaArray[r][c] == Config.CELL_EMPTY;
    }

    // Metodo helper per i nemici (da usare anche in updatePlayerMovement)
    private boolean isOccupiedByEnemies(double x, double y) {
        // Controllo semplice: nessun nemico nella cella target arrotondata
        int tx = (int) Math.round(x);
        int ty = (int) Math.round(y);
        for (Enemy e : enemies) {
            if ((int)Math.round(e.getX()) == tx && (int)Math.round(e.getY()) == ty) return true;
        }
        return false;
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
    /**
     * Controlla se la posizione futura (nextX, nextY) è occupata fisicamente da un nemico.
     * Usa un margine per rendere la collisione fisica leggermente più piccola della hitbox di danno.
     */


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

// In src/model/Model.java

    // Sostituisci il metodo handleExplosion
    private void handleExplosion(Bomb b) {
        int r = b.getRow();
        int c = b.getCol();
        int rad = b.getRadius();

        // 0 = CENTRO. Durata = FIRE_DURATION_TICKS
        activeFire.add(new int[]{r, c, 0, Config.FIRE_DURATION_TICKS});

        checkExplosionDamage(r, c);


        // --- AGGIUNTA: Reazione a catena nel centro ---
        // (Utile se un'esplosione colpisce una bomba appena piazzata lì)
        Bomb other = getBombAt(r, c);
        if (other != null && !other.isExploded()) {
            other.detonate();
        }
        // Espansione nelle 4 direzioni
        // I numeri (4,8,5,1...) sono i TIPI di pezzo (Logica posizionale)
        // 4=MedioVert, 8=FineSu | 5=MedioVert, 1=FineGiu | ...
        expandFireDirection(r, c, -1, 0, rad, 4, 8); // SU
        expandFireDirection(r, c, 1, 0, rad, 5, 1);  // GIÙ
        expandFireDirection(r, c, 0, -1, rad, 2, 6); // SINISTRA
        expandFireDirection(r, c, 0, 1, rad, 3, 7);  // DESTRA
    }

    // Sostituisci il metodo expandFireDirection

    // In src/model/Model.java
    private void expandFireDirection(int startR, int startC, int dr, int dc, int rad, int centralType, int endType) {
        for (int i = 1; i <= rad; i++) {
            int currentR = startR + dr * i;
            int currentC = startC + dc * i;

            if (currentR < 0 || currentR >= Config.GRID_HEIGHT || currentC < 0 || currentC >= Config.GRID_WIDTH) {
                break;
            }

            int cellType = gameAreaArray[currentR][currentC];

            if (cellType == Config.CELL_INDESTRUCTIBLE_BLOCK) {
                break;
            }

            if (cellType == Config.CELL_DESTRUCTIBLE_BLOCK) {
                gameAreaArray[currentR][currentC] = Config.CELL_EMPTY;
                destructionEffects.add(new BlockDestruction(currentR, currentC));

                // RIMOSSO: checkExplosionDamage(currentR, currentC);
                // La cassa assorbe l'esplosione. Il fuoco si ferma qui e NON danneggia
                // chi è "dietro" o chi tocca leggermente questa cella.
                break;
            }

            // Se la cella è vuota, il fuoco prosegue e danneggia
            boolean isTip = (i == rad);
            // ... (resto della logica per calcolare isTip) ...

            activeFire.add(new int[]{currentR, currentC, isTip ? endType : centralType, Config.FIRE_DURATION_TICKS});

            // Controlla i danni SOLO nelle celle dove il fuoco è effettivamente passato (celle vuote)
            checkExplosionDamage(currentR, currentC);
        }
    }
    /*
     * Gestisce l'effetto dell'esplosione su una singola cella.
     * Ritorna TRUE se l'esplosione può continuare attraverso questa cella.
     * Ritorna FALSE se l'esplosione viene fermata (da un muro o cassa).
     */

    /**
     * Tenta di distruggere una cella. Ritorna true se l'onda d'urto può proseguire.
     */

    // In src/model/Model.java

    @Override
    public void placeBomb() {
        //if (activeBombs.size() >= player.getMaxBombs()) return;

        // --- CALCOLO PERFETTO DEL CENTRO ---
        // X: Centro orizzontale (Invariato)
        double centerX = player.getXCoordinate() + (Config.ENTITY_LOGICAL_HITBOX_WIDTH / 2.0);

        // Y: Qui c'era l'errore. Dobbiamo puntare al centro della hitbox di collisione.
        // In isWalkable la hitbox va da (Y+0.1) a (Y+0.6). Il centro è Y + 0.35.
        // Usiamo questo valore fisso per essere sicuri di restare nella cella camminabile.
        double centerY = player.getYCoordinate() + 0.35;

        // Math.floor per ottenere l'indice della griglia
        int col = (int) Math.floor(centerX);
        int row = (int) Math.floor(centerY);

        // --- CONTROLLI ---
        if (row < 0 || row >= Config.GRID_HEIGHT || col < 0 || col >= Config.GRID_WIDTH) return;

        // CHECK DI SICUREZZA (Smart Fallback)
        // Se per qualche motivo di arrotondamento siamo finiti dentro un muro...
        if (gameAreaArray[row][col] != Config.CELL_EMPTY) {
            // ...proviamo a piazzare nella cella superiore (dove c'è la testa),
            // che è sicuramente libera se ci stiamo camminando.
            if (gameAreaArray[row - 1][col] == Config.CELL_EMPTY) {
                row = row - 1;
            } else {
                return; // Se neanche quella è libera, allora siamo davvero bloccati.
            }
        }

        // Controllo sovrapposizione bombe
        for (Bomb b : activeBombs) {
            if (b.getRow() == row && b.getCol() == col) return;
        }

        activeBombs.add(new Bomb(row, col, Config.BOMB_DETONATION_TICKS, player.getBombRadius()));
        System.out.println("Bomba piazzata in [" + row + "," + col + "]");
    }
    /**
     * Controlla se ci sono nemici nella cella (row, col) colpita dall'esplosione
     * e li elimina se presenti.
     */
    private void checkExplosionDamage(int row, int col) {
        // Usiamo un iteratore per poter rimuovere elementi dalla lista mentre cicliamo in sicurezza
        Iterator<Enemy> it = enemies.iterator();

        // Area dell'esplosione (la cella intera 1x1)
        double expX = col;
        double expY = row;
        double expSize = 1.0;

        while (it.hasNext()) {
            Enemy e = it.next();

            // Coordinate e Hitbox del nemico
            double eX = e.getX();
            double eY = e.getY();
            double eW = Config.GOBLIN_HITBOX_WIDTH;
            double eH = Config.GOBLIN_HITBOX_HEIGHT;

            // Intersezione tra Rettangoli (AABB)
            // Se il rettangolo del nemico tocca il rettangolo dell'esplosione...
            boolean hitX = eX < expX + expSize && eX + eW > expX;
            boolean hitY = eY < expY + expSize && eY + eH > expY;

            if (hitX && hitY) {
                it.remove(); // RIMUOVI IL NEMICO DALLA LISTA
                System.out.println("Goblin eliminato dall'esplosione in [" + row + "," + col + "]!");
            }
        }

        // --- AGGIUNTA: DANNO AL PLAYER ---
        if (!player.isInvincible()) {
            double pX = player.getXCoordinate();
            double pY = player.getYCoordinate();
            double pW = Config.ENTITY_LOGICAL_HITBOX_WIDTH;
            double pH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;

            if (pX < expX + expSize && pX + pW > expX && pY < expY + expSize && pY + pH > expY) {
                handlePlayerHit();
            }
        }
    }



    // 3. (OPZIONALE) Getter per l'interfaccia
    @Override
    public boolean isPlayerInvincible() {
        return player.isInvincible(); // Chiama il metodo che controlla il timestamp
    }

    @Override
    public int getPlayerLives() {
        return player.getLives();
    }

    @Override
    public int getElapsedTimeInSeconds() {
        // Divide i tick per gli FPS per ottenere i secondi reali
        return elapsedTicks / Config.FPS;
    }


// In src/model/Model.java

    @Override
    public int[][] getActiveBombsData() {
        // Matrice Nx3: [Row, Col, ElapsedTime]
        int[][] data = new int[activeBombs.size()][3];
        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < activeBombs.size(); i++) {
            Bomb b = activeBombs.get(i);
            data[i][0] = b.getRow();
            data[i][1] = b.getCol();

            // Calcoliamo quanto tempo è passato (in ms) da quando esiste
            // Cast a int è sicuro perché una bomba dura pochi secondi
            data[i][2] = (int) (currentTime - b.getCreationTime());
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

    // In src/model/Model.java

    private void spawnEnemy() {
        int r, c;
        int attempts = 0;
        boolean spawned = false;

        // Aumentiamo i tentativi a 100 per essere sicuri di trovare spazio
        while (!spawned && attempts < 100) {
            c = randomGenerator.nextInt(Config.GRID_WIDTH);
            r = randomGenerator.nextInt(Config.GRID_HEIGHT);

            if (isValidSpawnPoint(c, r)) {
                // Scegliamo il tipo in base a quanti nemici ci sono già
                int typeIndex = enemies.size() % 3;
                Enemy newEnemy;

                switch (typeIndex) {
                    case 1 -> newEnemy = new ChasingGoblin(c, r);
                    case 2 -> newEnemy = new ShooterGoblin(c, r);
                    default -> newEnemy = new CommonGoblin(c, r);
                }

                enemies.add(newEnemy);
                spawned = true;
                lastSpawnTime = System.currentTimeMillis(); // Reset del timer solo se nasce
                System.out.println("Model: Generato " + newEnemy.getType() + " in (" + c + ", " + r + ")");
            }
            attempts++;
        }
    }

    private boolean isValidSpawnPoint(int col, int row) {
        // 1. Limiti severi per evitare la zona nera (Margine di 1 cella)
        if (col < 1 || col >= Config.GRID_WIDTH - 1 || row < 1 || row >= Config.GRID_HEIGHT - 1) return false;

        // 2. La cella deve essere vuota
        if (gameAreaArray[row][col] != Config.CELL_EMPTY) return false;

        // 3. Distanza minima dal player (usa la costante di Config)
        double dist = Math.sqrt(Math.pow(col - player.getXCoordinate(), 2) + Math.pow(row - player.getYCoordinate(), 2));
        if (dist < Config.MIN_SPAWN_DISTANCE) return false;

        // 4. CONTROLLO SOVRAPPOSIZIONE: Non spawnare se c'è già un nemico vicino (raggio 0.8)
        for (Enemy e : enemies) {
            if (Math.abs(e.getX() - col) < 0.8 && Math.abs(e.getY() - row) < 0.8) return false;
        }

        return true;
    }
    // --- DA INSERIRE IN Model.java ---


    @Override
    public List<int[]> getFireData() {
        return new ArrayList<>(activeFire);
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

    // 2. Aggiorniamo handlePlayerHit per gestire Respawn e Reset
    private void handlePlayerHit() {
        if (player.isInvincible()) return;

        // Applica il danno (riduce vita e imposta timer invincibilità)
        player.takeDamage();

        // RESET POSIZIONE (Respawn)
        player.setXCoordinate(0.0);
        player.setYCoordinate(0.0);
        player.setDelta(0, 0); // Ferma eventuali movimenti residui
        player.setState(PlayerState.IDLE_FRONT); // Reset animazione

        System.out.println("RESPAWN: Player riportato all'inizio.");
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
        elapsedTicks++; // Aumenta di 1 ogni frame (60 volte al secondo)

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
        // LOGICA DI PULIZIA: Il Model decide che dopo 500ms l'evento "distruzione" non esiste più.
// --- GESTIONE FUOCO (PURE LOGIC) ---
        // Decrementiamo la "vita" del fuoco.
        Iterator<int[]> it = activeFire.iterator();
        while (it.hasNext()) {
            int[] f = it.next();
            f[3]--; // Decrementa i tick (30 -> 29 -> ... -> 0)

            if (f[3] <= 0) {
                it.remove(); // Tempo scaduto, rimuovi logicamente
            }
        }

        // Pulizia effetti grafici di distruzione (basati su tempo reale o tick, a tua scelta)
        long now = System.currentTimeMillis();
        destructionEffects.removeIf(bd -> (now - bd.getCreationTime()) > 500);
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
    @Override
    public List<int[]> getDestructionsData() {
        List<int[]> data = new ArrayList<>();
        long currentTime = System.currentTimeMillis();

        for (BlockDestruction bd : destructionEffects) {
            int[] info = new int[3];
            info[0] = bd.getRow();
            info[1] = bd.getCol();
            info[2] = (int) (currentTime - bd.getCreationTime()); // Tempo trascorso in ms
            data.add(info);
        }
        return data;
    }

        @Override
        public boolean isAreaOccupiedByOtherEnemy(double nextX, double nextY, Enemy self) {
            // 1. Calcoliamo la casella "obiettivo" arrotondando le coordinate
            int targetCol = (int) Math.round(nextX);
            int targetRow = (int) Math.round(nextY);

            for (Enemy other : enemies) {
                // Ignoriamo noi stessi
                if (other == self) continue;

                // 2. Se anche l'altro nemico si trova (arrotondato) nella stessa casella...
                if ((int) Math.round(other.getX()) == targetCol &&
                        (int) Math.round(other.getY()) == targetRow) {

                    return true; // ...allora la cella è occupata!
                }
            }
            return false; // Cella libera
        }


    private Bomb getBombAt(int r, int c) {
        for (Bomb b : activeBombs) {
            if (b.getRow() == r && b.getCol() == c) {
                return b;
            }
        }
        return null;
    }







    public static IModel getInstance() {
        if (instance == null) instance = new Model();
        return instance;
    }
}

