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

    // Lista che tiene traccia dei blocchi appena distrutti per la View
    private final List<BlockDestruction> destructionEffects = new ArrayList<>();

    private final List<int[]> activeFire = new ArrayList<>(); // [row, col, type, timestamp]

   private static final int[][] testMap = {
            {0, 0, 0, 2, 2, 2, 2, 0, 2, 2, 2, 2, 2}, // Riga 0: Start Safe (0,0)
            {0, 1, 2, 1, 2, 1, 0, 1, 2, 1, 2, 1, 2}, // Riga 1: Pilastri fissi
            {0, 2, 0, 2, 0, 0, 0, 2, 2, 2, 0, 2, 0}, // Riga 2: Misto
            {2, 1, 2, 1, 2, 1, 0, 1, 2, 1, 2, 1, 0}, // Riga 3: Pilastri
            {2, 2, 0, 2, 2, 2, 0, 0, 0, 2, 2, 0, 2}, // Riga 4: Spazi aperti (Arena)
            {0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0}, // Riga 5: Linea centrale
            {2, 2, 2, 2, 2, 0, 0, 0, 2, 2, 2, 2, 2}, // Riga 6: Muro di casse
            {0, 1, 2, 1, 2, 1, 2, 1, 0, 1, 2, 1, 0}, // Riga 7: Pilastri
            {2, 2, 0, 2, 0, 2, 2, 2, 0, 0, 0, 2, 0}, // Riga 8: Labirinto
            {0, 1, 2, 1, 2, 1, 0, 1, 2, 1, 2, 1, 2}, // Riga 9: Pilastri
            {2, 2, 0, 0, 0, 2, 0, 2, 0, 2, 2, 2, 2}  // Riga 10: Fondo
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
    public boolean isWalkable(double nextX, double nextY) {
        double hbW = Config.ENTITY_LOGICAL_HITBOX_WIDTH;
        double hbH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;

        // --- 1. CALCOLO HITBOX CORRETTO ---
        // Centriamo la hitbox orizzontalmente
        double left = nextX + (1.0 - hbW) / 2.0;
        double right = left + hbW - 0.01;

        // Calibrazione Verticale: "Piedi" piantati a terra, altezza a salire
        // (Questo corregge anche il problema dell'invasione verso l'alto)
        double yOffset = 0.4; // Margine per dare profondità 2.5D
        double bottom = nextY + 1.0 - yOffset;
        double top = bottom - hbH;

        int startCol = (int) Math.floor(left);
        int endCol = (int) Math.floor(right);
        int startRow = (int) Math.floor(top);
        int endRow = (int) Math.floor(bottom);

        // Controllo Limiti Mappa
        if (startCol < 0 || endCol >= Config.GRID_WIDTH || startRow < 0 || endRow >= Config.GRID_HEIGHT) {
            return false;
        }

        // --- 2. CONTROLLO COLLISIONI ---
        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {

                // A. MURI E CASSE (Sempre Solidi)
                if (gameAreaArray[r][c] == Config.CELL_INDESTRUCTIBLE_BLOCK ||
                        gameAreaArray[r][c] == Config.CELL_DESTRUCTIBLE_BLOCK) {
                    return false;
                }

                // B. BOMBE (Solide, ma con eccezione)
                if (isBombAt(r, c)) {
                    // Se c'è una bomba, è un muro...
                    // ...TRANNE SE il player ci è attualmente "dentro" (appena piazzata)
                    if (!isPlayerCurrentlyInside(r, c)) {
                        return false;
                    }
                    // Se siamo dentro, isWalkable ritorna TRUE per questa cella,
                    // permettendoci di camminare per uscire dalla bomba.
                }
            }
        }
        return true;
    }

    // --- HELPER 1: C'è una bomba qui? ---
    private boolean isBombAt(int r, int c) {
        for (Bomb b : activeBombs) {
            if (b.getRow() == r && b.getCol() == c) return true;
        }
        return false;
    }

    // --- HELPER 2: Sono dentro la bomba? (SENZA MARGINI) ---
    private boolean isPlayerCurrentlyInside(int r, int c) {
        // Coordinate attuali del Player (prima del movimento)
        double pX = player.getXCoordinate();
        double pY = player.getYCoordinate();
        double hbW = Config.ENTITY_LOGICAL_HITBOX_WIDTH;
        double hbH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;

        // Calcoliamo i bordi esatti della hitbox attuale
        double pLeft = pX + (1.0 - hbW) / 2.0;
        double pRight = pLeft + hbW;

        // Usiamo lo stesso calcolo verticale di isWalkable per coerenza
        double yOffset = 0.4;
        double pBottom = pY + 1.0 - yOffset;
        double pTop = pBottom - hbH;

        // Coordinate della Cella Bomba (Intera cella 1x1)
        double cellLeft = c;
        double cellRight = c + 1.0;
        double cellTop = r;
        double cellBottom = r + 1.0;

        // CONTROLLO INTERSEZIONE SEMPLICE (Nessun margine "sicuro")
        // Se le due aree si sovrappongono anche minimamente, ritorna true.
        boolean overlapX = pRight > cellLeft && pLeft < cellRight;
        boolean overlapY = pBottom > cellTop && pTop < cellBottom;

        return overlapX && overlapY;
    }

// In src/model/Model.java

    private void updatePlayerMovement() {
        double currentX = player.getXCoordinate();
        double currentY = player.getYCoordinate();
        double deltaX = player.getDeltaX();
        double deltaY = player.getDeltaY();

        updatePlayerAction(deltaX, deltaY);

        if (deltaX == 0 && deltaY == 0) return;

        // -------------------------------------------------
        // MOVIMENTO ASSE X (Sliding Verticale - Su/Giù)
        // -------------------------------------------------
        double nextX = currentX + deltaX;

        // 1. Provo il movimento diretto
        if (isWalkable(nextX, currentY) && !isOccupiedByEnemies(nextX, currentY)) {
            player.setXCoordinate(Math.max(Config.MIN_LOGICAL_X, Math.min(Config.MAX_LOGICAL_X, nextX)));
        }
        // 2. Se bloccato, cerco un "vicino" libero in alto o in basso
        else if (deltaX != 0) {
            // Coordinate intere candidate (Sopra e Sotto)
            double targetY_Up = Math.floor(currentY);
            double targetY_Down = targetY_Up + 1.0;

            // Verifico quali sono libere
            boolean canGoUp = isWalkable(nextX, targetY_Up) && !isOccupiedByEnemies(nextX, targetY_Up);
            boolean canGoDown = isWalkable(nextX, targetY_Down) && !isOccupiedByEnemies(nextX, targetY_Down);

            // Distanza attuale dai target
            double distUp = Math.abs(currentY - targetY_Up);
            double distDown = Math.abs(currentY - targetY_Down);

            // LOGICA DI SCELTA: Vai verso il buco libero. Se entrambi liberi, vai al più vicino.
            if (canGoUp && (!canGoDown || distUp < distDown)) {
                player.setYCoordinate(currentY - Config.CORNER_ALIGN_SPEED);
            }
            else if (canGoDown && (!canGoUp || distDown < distUp)) {
                player.setYCoordinate(currentY + Config.CORNER_ALIGN_SPEED);
            }
        }

        currentY = player.getYCoordinate(); // Aggiorno Y post-sliding

        // -------------------------------------------------
        // MOVIMENTO ASSE Y (Sliding Orizzontale - Destra/Sinistra)
        // -------------------------------------------------
        double nextY = currentY + deltaY;

        // 1. Provo il movimento diretto
        if (isWalkable(currentX, nextY) && !isOccupiedByEnemies(currentX, nextY)) {
            player.setYCoordinate(Math.max(Config.MIN_LOGICAL_Y, Math.min(Config.MAX_LOGICAL_Y, nextY)));
        }
        // 2. Se bloccato, cerco un "vicino" libero a sinistra o destra
        else if (deltaY != 0) {
            // Coordinate intere candidate (Sinistra e Destra)
            double targetX_Left = Math.floor(currentX);
            double targetX_Right = targetX_Left + 1.0;

            // Verifico quali sono libere alla nuova altezza (nextY)
            // NOTA: Controlliamo se mettendoci ESATTAMENTE su targetX (0.0, 1.0...) passiamo
            boolean canGoLeft = isWalkable(targetX_Left, nextY) && !isOccupiedByEnemies(targetX_Left, nextY);
            boolean canGoRight = isWalkable(targetX_Right, nextY) && !isOccupiedByEnemies(targetX_Right, nextY);

            // Distanza dai target
            double distLeft = Math.abs(currentX - targetX_Left);
            double distRight = Math.abs(currentX - targetX_Right);

            // LOGICA DI SCELTA
            if (canGoLeft && (!canGoRight || distLeft < distRight)) {
                player.setXCoordinate(currentX - Config.CORNER_ALIGN_SPEED);
            }
            else if (canGoRight && (!canGoLeft || distRight < distLeft)) {
                player.setXCoordinate(currentX + Config.CORNER_ALIGN_SPEED);
            }
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
    /**
     * Controlla se la posizione futura (nextX, nextY) è occupata fisicamente da un nemico.
     * Usa un margine per rendere la collisione fisica leggermente più piccola della hitbox di danno.
     */
    private boolean isOccupiedByEnemies(double nextX, double nextY) {
        double pHW = Config.ENTITY_LOGICAL_HITBOX_WIDTH;
        double pHH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;

        // Il margine serve a bloccare il player un attimo DOPO che le hitbox di danno si sono toccate.
        // In questo modo il contatto (e la perdita di vita) avviene sicuramente.
        double margin = 0.15;

        for (Enemy e : enemies) {
            double eX = e.getX();
            double eY = e.getY();
            double eW = Config.GOBLIN_HITBOX_WIDTH;
            double eH = Config.GOBLIN_HITBOX_HEIGHT;

            // Verifica sovrapposizione AABB con margine
            boolean overlapX = (nextX + margin) < (eX + eW - margin) &&
                    (nextX + pHW - margin) > (eX + margin);

            boolean overlapY = (nextY + margin) < (eY + eH - margin) &&
                    (nextY + pHH - margin) > (eY + margin);

            if (overlapX && overlapY) {
                return true; // Spazio occupato da un nemico
            }
        }
        return false; // Spazio libero
    }


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

    private void handleExplosion(Bomb b) {
        int r = b.getRow();
        int c = b.getCol();
        int rad = b.getRadius();
        int now = (int) System.currentTimeMillis();

        // 1. Centro (Tipo 0)
        activeFire.add(new int[]{r, c, 0, now});
        processExplosionStep(r, c);

        // 2. Espansione nelle 4 direzioni con i tipi specifici
        expandFireDirection(r, c, -1, 0, rad, 4, 8); // Su: Central Up (4), End Up (8)
        expandFireDirection(r, c, 1, 0, rad, 5, 1);  // Giù: Central Down (5), End Down (1)
        expandFireDirection(r, c, 0, -1, rad, 2, 6); // Sinistra: Central Left (2), End Left (6)
        expandFireDirection(r, c, 0, 1, rad, 3, 7);  // Destra: Central Right (3), End Right (7)
    }
    /*
     * Gestisce l'effetto dell'esplosione su una singola cella.
     * Ritorna TRUE se l'esplosione può continuare attraverso questa cella.
     * Ritorna FALSE se l'esplosione viene fermata (da un muro o cassa).
     */
    private boolean processExplosionStep(int r, int c) {
        // Controllo confini mappa
        if (r < 0 || r >= Config.GRID_HEIGHT || c < 0 || c >= Config.GRID_WIDTH) return false;

        int type = gameAreaArray[r][c];

        // CASO 1: Muro Indistruttibile
        // Ferma il fuoco. Nessun danno ai nemici (si assume siano protetti dal muro o non possano esserci dentro).
        if (type == Config.CELL_INDESTRUCTIBLE_BLOCK) return false;

        // CASO 2: Cella Accessibile (Vuota o Cassa Distruttibile)
        // Il fuoco entra in questa cella -> Controlliamo se uccide qualcuno
        checkExplosionDamage(r, c);

        // CASO 3: Muro Distruttibile (Cassa)
        if (type == Config.CELL_DESTRUCTIBLE_BLOCK) {
            gameAreaArray[r][c] = Config.CELL_EMPTY; // Distrugge la cassa
            destructionEffects.add(new BlockDestruction(r, c));
            return false; // Il fuoco SI FERMA qui (ha colpito l'ostacolo), non va oltre
        }

        // CASO 4: Cella Vuota
        // Il fuoco continua a espandersi
        return true;
    }

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
    // --- DA INSERIRE IN Model.java ---

    /**
     * Verifica se la posizione futura (nextX, nextY) è occupata da un altro nemico.
     * Impedisce la sovrapposizione delle sprite.
     */
    public boolean isAreaOccupiedByOtherEnemy(double nextX, double nextY, Enemy self) {
        // Usiamo la hitbox definita specificamente per i Goblin (0.8 solitamente)
        double w = Config.GOBLIN_HITBOX_WIDTH;
        double h = Config.GOBLIN_HITBOX_HEIGHT;

        // Margine di tolleranza per evitare "tremolii" quando sono vicini
        double epsilon = 0.05;

        for (Enemy other : enemies) {
            // Non controlliamo la collisione con noi stessi
            if (other == self) continue;

            double otherX = other.getX();
            double otherY = other.getY();

            // Calcolo collisione AABB (Axis-Aligned Bounding Box)
            // Aggiungiamo epsilon per rendere la collisione più "solida"
            boolean collisionX = (nextX + epsilon) < (otherX + w - epsilon) && (nextX + w - epsilon) > (otherX + epsilon);
            boolean collisionY = (nextY + epsilon) < (otherY + h - epsilon) && (nextY + h - epsilon) > (otherY + epsilon);

            if (collisionX && collisionY) {
                return true; // C'è collisione con un altro nemico
            }
        }
        return false; // Via libera
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
        // LOGICA DI PULIZIA: Il Model decide che dopo 500ms l'evento "distruzione" non esiste più.

        long now = System.currentTimeMillis();
        activeFire.removeIf(f -> (now - f[3]) > 500); // Il fuoco dura 500ms
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
    public List<int[]> getActiveFireData() {
        return new ArrayList<>(activeFire);
    }
    private void expandFireDirection(int startR, int startC, int dr, int dc, int rad, int centralType, int endType) {
        for (int i = 1; i <= rad; i++) {
            int r = startR + dr * i;
            int c = startC + dc * i;

            if (r < 0 || r >= Config.GRID_HEIGHT || c < 0 || c >= Config.GRID_WIDTH) break;

            int cellType = gameAreaArray[r][c];
            if (cellType == Config.CELL_INDESTRUCTIBLE_BLOCK) break;

            // Determina se è la fine del raggio
            boolean isEnd = (i == rad);
            if (!isEnd) {
                int nr = r + dr, nc = c + dc;
                if (nr < 0 || nr >= Config.GRID_HEIGHT || nc < 0 || nc >= Config.GRID_WIDTH || gameAreaArray[nr][nc] != Config.CELL_EMPTY)
                    isEnd = true;
            }
            if (cellType == Config.CELL_DESTRUCTIBLE_BLOCK) isEnd = true;

            activeFire.add(new int[]{r, c, isEnd ? endType : centralType, (int) System.currentTimeMillis()});
            processExplosionStep(r, c);

            if (cellType == Config.CELL_DESTRUCTIBLE_BLOCK) break;
        }
    }

    public static IModel getInstance() {
        if (instance == null) instance = new Model();
        return instance;
    }
}

