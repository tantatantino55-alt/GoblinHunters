package model;

import utils.*;

import java.awt.geom.Rectangle2D;
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

    private final List<Collectible> activeItems = new ArrayList<>();
    // --- VARIABILI PROGRESSIONE LIVELLI (GATE) ---
    public static final int GATE_ID = 9;   // Il valore logico per il Gate di fine livello
    private int currentZone = 0;           // 0 = Village, 1 = Forest, 2 = Cave
    private int difficultyCycle = 1;       // Moltiplicatore di difficoltà post-boss
    private boolean gateActive = false;    // Diventa true quando i nemici muoiono
    private boolean levelCompletedFlag = false; // Avvisa il Controller del cambio mappa
    private String currentTheme = "VILLAGE"; // <-- AGGIUNTO: Tema iniziale


    // --- VARIABILI PORTALE ---
    private int portalRow = -1;
    private int portalCol = -1;
    private boolean portalRevealed = false;
    private long lastPortalSpawnTime = 0;

    // --- MAPPA DEL TESORO (Loot pre-calcolato) ---
    // Usa una stringa "riga,colonna" come chiave per trovare l'oggetto
    private final java.util.Map<String, utils.ItemType> hiddenLoot = new java.util.HashMap<>();

    private static final int[][] testMap = {
            {0, 0, 0, 2, 0, 2, 2, 0, 2, 2, 0, 0, 0}, // Riga 0: Angolo player sicuro
            {0, 1, 0, 1, 2, 1, 2, 1, 2, 1, 2, 1, 0}, // Riga 1: Pilastri e casse
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2}, // Riga 2: Corridoio libero
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

        // 1. CHIAMIAMO IL NUOVO METODO PER AVERE LA PRIMA MAPPA
        int[][] initialMap = generateProceduralMap();

        // 2. LA COPIAMO NELLA GRIGLIA UFFICIALE DEL GIOCO
        for (int r = 0; r < Config.GRID_HEIGHT; r++) {
            System.arraycopy(initialMap[r], 0, gameAreaArray[r], 0, Config.GRID_WIDTH);
        }

        // 3. INIZIALIZZIAMO PLAYER E NEMICI
        this.player = new Player(0.0, 0.0);
        this.enemies = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            spawnEnemy();
        }
        this.lastSpawnTime = System.currentTimeMillis();
    }

    @Override
    // --- METODO PER GENERARE MAPPE PROCEDURALI (Utile per il cambio livello) ---
    public int[][] generateProceduralMap() {
        int[][] nextMap = new int[Config.GRID_HEIGHT][Config.GRID_WIDTH];
        List<int[]> emptyCells = new ArrayList<>();
        List<int[]> cratePositions = new ArrayList<>();

        // Pulizia del loot e del portale precedente (Fondamentale per i livelli > 1)
        hiddenLoot.clear();

        // --- PASSO 1 & 2: PILASTRI FISSI E IL "BUNKER" INIZIALE ---
        for (int r = 0; r < Config.GRID_HEIGHT; r++) {
            for (int c = 0; c < Config.GRID_WIDTH; c++) {

                // Pilastri di pietra nelle righe e colonne dispari
                if (r % 2 != 0 && c % 2 != 0) {
                    nextMap[r][c] = Config.CELL_INDESTRUCTIBLE_BLOCK;
                } else {
                    // La "Safe Zone" a L in alto a sinistra: [0,0], [0,1], [1,0]
                    boolean isSafeZone = (r == 0 && c == 0) || (r == 0 && c == 1) || (r == 1 && c == 0);

                    // I muri del Bunker: chiudono la Safe Zone
                    boolean isBunkerWall = (r == 0 && c == 2) || (r == 2 && c == 0);

                    if (isSafeZone) {
                        nextMap[r][c] = Config.CELL_EMPTY; // Dentro il bunker si sta sicuri
                    } else if (isBunkerWall) {
                        nextMap[r][c] = Config.CELL_DESTRUCTIBLE_BLOCK; // Muri forzati del bunker
                        cratePositions.add(new int[]{r, c}); // Li aggiungiamo alla lista del loot
                    } else {
                        nextMap[r][c] = Config.CELL_EMPTY;
                        emptyCells.add(new int[]{r, c}); // Spazi liberi per le casse casuali
                    }
                }
            }
        }

        // --- PASSO 3: GENERAZIONE DELLE RESTANTI 43 CASSE CASUALI (45 tot - 2 bunker) ---
        int NUM_RANDOM_CRATES = 43;
        java.util.Collections.shuffle(emptyCells, randomGenerator);

        for (int i = 0; i < NUM_RANDOM_CRATES && i < emptyCells.size(); i++) {
            int[] pos = emptyCells.get(i);
            nextMap[pos[0]][pos[1]] = Config.CELL_DESTRUCTIBLE_BLOCK;
            cratePositions.add(pos);
        }

        // --- PASSO 4: IL MAZZO DEL BOTTINO (Loot intelligente) ---
        java.util.Collections.shuffle(cratePositions, randomGenerator);

        if (cratePositions.size() > 0) {
            // 1. Il Portale (nascondilo nella 1° cassa estratta)
            int[] pCoords = cratePositions.get(0);
            portalRow = pCoords[0];
            portalCol = pCoords[1];
            System.out.println("DEBUG: Portale nascosto in [" + portalRow + ", " + portalCol + "]");

            // 2. Le Bombe
            for (int i = 1; i <= 10 && i < cratePositions.size(); i++) {
                int[] cCoords = cratePositions.get(i);
                hiddenLoot.put(cCoords[0] + "," + cCoords[1], ItemType.AMMO_BOMB);
            }

            // 3. L'Aura
            for (int i = 11; i <= 20 && i < cratePositions.size(); i++) {
                int[] cCoords = cratePositions.get(i);
                hiddenLoot.put(cCoords[0] + "," + cCoords[1], ItemType.AMMO_AURA);
            }
        }

        return nextMap; // Restituisce la matrice generata pronta all'uso!
    }

    // In src/model/Model.java

    // Sostituisci il vecchio isWalkable con questo basato su GRIGLIA RIGIDA
    // In src/model/Model.java

    // Sostituisci il vecchio isWalkable
    // In src/model/Model.java


// In src/model/Model.java

    // In src/model/Model.java

    // --- A. LOGICA DI MOVIMENTO RIGIDO (BOMBERMAN STYLE) ---
    // src/model/Model.java

    private void updatePlayerMovement() {
        PlayerState currentState = player.getState();
        // ------------------------------------------------------
        if (currentState.name().startsWith("ATTACK")) {
            return; // Blocca l'aggiornamento del movimento/stato
        }




        double currentX = player.getXCoordinate();
        double currentY = player.getYCoordinate();
        double deltaX = player.getDeltaX();
        double deltaY = player.getDeltaY();

        updatePlayerAction(deltaX, deltaY);
        if (deltaX == 0 && deltaY == 0) return;

        double alignSpeed = Config.ENTITY_LOGICAL_SPEED * 1.5;
        double CORNER_TOLERANCE = 0.60;

        // -------------------------------------------------
        // ASSE X (Movimento Orizzontale)
        // -------------------------------------------------
        if (deltaX != 0) {
            double rawNextX = currentX + deltaX;
            double clampedNextX = rawNextX;

            int r = (int) Math.round(currentY);
            int c = (int) Math.round(currentX);

            if (deltaX < 0 && isCellBlocked(r, c - 1)) clampedNextX = Math.max(clampedNextX, c);
            if (deltaX > 0 && isCellBlocked(r, c + 1)) clampedNextX = Math.min(clampedNextX, c);

            boolean isBlocked = !isWalkable(rawNextX, currentY) || isOccupiedByEnemies(rawNextX, currentY) || clampedNextX != rawNextX;

            if (!isBlocked) {
                player.setXCoordinate(Math.max(Config.MIN_LOGICAL_X, Math.min(Config.MAX_LOGICAL_X, clampedNextX)));

                double idealY = Math.round(currentY);
                double diffY = currentY - idealY;
                if (Math.abs(diffY) > 0.01) {
                    if (isWalkable(clampedNextX, idealY) && !isOccupiedByEnemies(clampedNextX, idealY)) {
                        double step = Math.min(alignSpeed, Math.abs(diffY));
                        if (diffY > 0) player.setYCoordinate(currentY - step);
                        else player.setYCoordinate(currentY + step);
                    }
                }
            } else {
                // SLIDING
                int targetCol = deltaX < 0 ? c - 1 : c + 1;

                double targetY_Up = Math.floor(currentY);
                double targetY_Down = targetY_Up + 1.0;
                int rUp = (int) Math.round(targetY_Up);
                int rDown = (int) Math.round(targetY_Down);

                // CONTROLLO CRUCIALE: Scivolo SOLO se la cella di destinazione OLTRE l'angolo è libera!
                boolean canGoUp = !isCellBlocked(rUp, targetCol) && isWalkable(rawNextX, targetY_Up) && !isOccupiedByEnemies(rawNextX, targetY_Up);
                boolean canGoDown = !isCellBlocked(rDown, targetCol) && isWalkable(rawNextX, targetY_Down) && !isOccupiedByEnemies(rawNextX, targetY_Down);

                double distUp = Math.abs(currentY - targetY_Up);
                double distDown = Math.abs(currentY - targetY_Down);

                if (canGoUp && distUp < CORNER_TOLERANCE && (!canGoDown || distUp < distDown)) {
                    if (distUp > 0.01) player.setYCoordinate(currentY - Math.min(alignSpeed, distUp));
                }
                else if (canGoDown && distDown < CORNER_TOLERANCE && (!canGoUp || distDown < distUp)) {
                    if (distDown > 0.01) player.setYCoordinate(currentY + Math.min(alignSpeed, distDown));
                }
            }
        }

        // -------------------------------------------------
        // ASSE Y (Movimento Verticale)
        // -------------------------------------------------
        currentX = player.getXCoordinate();
        currentY = player.getYCoordinate();

        if (deltaY != 0) {
            double rawNextY = currentY + deltaY;
            double clampedNextY = rawNextY;

            int r2 = (int) Math.round(currentY);
            int c2 = (int) Math.round(currentX);

            if (deltaY < 0 && isCellBlocked(r2 - 1, c2)) clampedNextY = Math.max(clampedNextY, r2);
            if (deltaY > 0 && isCellBlocked(r2 + 1, c2)) clampedNextY = Math.min(clampedNextY, r2);

            boolean isBlocked = !isWalkable(currentX, rawNextY) || isOccupiedByEnemies(currentX, rawNextY) || clampedNextY != rawNextY;

            if (!isBlocked) {
                player.setYCoordinate(Math.max(Config.MIN_LOGICAL_Y, Math.min(Config.MAX_LOGICAL_Y, clampedNextY)));

                double idealX = Math.round(currentX);
                double diffX = currentX - idealX;
                if (Math.abs(diffX) > 0.01) {
                    if (isWalkable(idealX, clampedNextY) && !isOccupiedByEnemies(idealX, clampedNextY)) {
                        double step = Math.min(alignSpeed, Math.abs(diffX));
                        if (diffX > 0) player.setXCoordinate(currentX - step);
                        else player.setXCoordinate(currentX + step);
                    }
                }
            } else {
                // SLIDING
                int targetRow = deltaY < 0 ? r2 - 1 : r2 + 1;

                double targetX_Left = Math.floor(currentX);
                double targetX_Right = targetX_Left + 1.0;
                int cLeft = (int) Math.round(targetX_Left);
                int cRight = (int) Math.round(targetX_Right);

                // CONTROLLO CRUCIALE: Scivolo SOLO se la cella di destinazione OLTRE l'angolo è libera!
                boolean canGoLeft = !isCellBlocked(targetRow, cLeft) && isWalkable(targetX_Left, rawNextY) && !isOccupiedByEnemies(targetX_Left, rawNextY);
                boolean canGoRight = !isCellBlocked(targetRow, cRight) && isWalkable(targetX_Right, rawNextY) && !isOccupiedByEnemies(targetX_Right, rawNextY);

                double distLeft = Math.abs(currentX - targetX_Left);
                double distRight = Math.abs(currentX - targetX_Right);

                if (canGoLeft && distLeft < CORNER_TOLERANCE && (!canGoRight || distLeft < distRight)) {
                    if (distLeft > 0.01) player.setXCoordinate(currentX - Math.min(alignSpeed, distLeft));
                }
                else if (canGoRight && distRight < CORNER_TOLERANCE && (!canGoLeft || distRight < distLeft)) {
                    if (distRight > 0.01) player.setXCoordinate(currentX + Math.min(alignSpeed, distRight));
                }
            }
        }
    }

    @Override
    public boolean isWalkable(double nextX, double nextY) {
        double hbW = Config.ENTITY_LOGICAL_HITBOX_WIDTH;
        double hbH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;

        double left = nextX + (1.0 - hbW) / 2.0;
        double right = left + hbW - 0.01;
        double yOffset = 0.4;
        double bottom = nextY + 1.0 - yOffset;
        double top = bottom - hbH;

        int startCol = (int) Math.floor(left);
        int endCol = (int) Math.floor(right);
        int startRow = (int) Math.floor(top);
        int endRow = (int) Math.floor(bottom);

        // Controllo confini mappa
        if (startCol < 0 || endCol >= Config.GRID_WIDTH || startRow < 0 || endRow >= Config.GRID_HEIGHT) return false;

        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {

                // 1. Controllo Muri (Indistruttibili e Distruttibili)
                if (gameAreaArray[r][c] == Config.CELL_INDESTRUCTIBLE_BLOCK ||
                        gameAreaArray[r][c] == Config.CELL_DESTRUCTIBLE_BLOCK) {
                    return false;
                }

                // 2. Controllo Bombe (Se c'è una bomba E nessuno ci è ancora sopra, diventa un muro)
                if (getBombAt(r, c) != null && !isPlayerCurrentlyInside(r, c) && !isAnyEnemyCurrentlyInside(r, c)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isPlayerCurrentlyInside(int r, int c) {
        double pX = player.getXCoordinate();
        double pY = player.getYCoordinate();
        double hbW = Config.ENTITY_LOGICAL_HITBOX_WIDTH;
        double hbH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;

        double pLeft = pX + (1.0 - hbW) / 2.0;
        double pRight = pLeft + hbW;
        double pBottom = pY + 1.0 - 0.4;
        double pTop = pBottom - hbH;

        return pRight > c && pLeft < (c + 1.0) && pBottom > r && pTop < (r + 1.0);
    }

    // NUOVO METODO: Permette anche ai goblin di uscire dalle bombe appena piazzate
    private boolean isAnyEnemyCurrentlyInside(int r, int c) {
        double hbW = Config.ENTITY_LOGICAL_HITBOX_WIDTH;
        double hbH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;

        for (Enemy e : enemies) {
            double eLeft = e.getX() + (1.0 - hbW) / 2.0;
            double eRight = eLeft + hbW;
            double eBottom = e.getY() + 1.0 - 0.4;
            double eTop = eBottom - hbH;

            if (eRight > c && eLeft < (c + 1.0) && eBottom > r && eTop < (r + 1.0)) {
                return true;
            }
        }
        return false;
    }


    // --- Helper per inchiodare il giocatore ai binari senza rovinare le hitbox ---
    private boolean isCellBlocked(int r, int c) {
        if (r < 0 || r >= Config.GRID_HEIGHT || c < 0 || c >= Config.GRID_WIDTH) return true;
        if (gameAreaArray[r][c] == Config.CELL_INDESTRUCTIBLE_BLOCK ||
                gameAreaArray[r][c] == Config.CELL_DESTRUCTIBLE_BLOCK) return true;
        if (getBombAt(r, c) != null && !isPlayerCurrentlyInside(r, c)) return true;
        return false;
    }

    private boolean isOccupiedByEnemies(double nextX, double nextY) {
        double margin = 0.15;
        double pHW = Config.ENTITY_LOGICAL_HITBOX_WIDTH;
        double pHH = Config.ENTITY_LOGICAL_HITBOX_HEIGHT;

        for (Enemy e : enemies) {
            double eX = e.getX();
            double eY = e.getY();

            boolean overlapX = (nextX + margin) < (eX + Config.GOBLIN_HITBOX_WIDTH - margin) &&
                    (nextX + pHW - margin) > (eX + margin);
            boolean overlapY = (nextY + margin) < (eY + Config.GOBLIN_HITBOX_HEIGHT - margin) &&
                    (nextY + pHH - margin) > (eY + margin);

            if (overlapX && overlapY) return true;
        }
        return false;
    }


    @Override
    public void setPlayerDelta(double dx, double dy) {
        // Usiamo la VERA velocità del player (che aumenta col Power-up)
        double currentSpeed = player.getSpeed();

        // Applichiamo la direzione richiesta ma con la velocità corretta
        if (dx > 0) dx = currentSpeed;
        else if (dx < 0) dx = -currentSpeed;

        if (dy > 0) dy = currentSpeed;
        else if (dy < 0) dy = -currentSpeed;

        this.player.setDelta(dx, dy);
    }


    private void updatePlayerAction(double dx, double dy) {
        if (dx > 0) {
            player.setDirection(Direction.RIGHT); // <--- AGGIUNTO
            player.setState(PlayerState.RUN_RIGHT);
        } else if (dx < 0) {
            player.setDirection(Direction.LEFT);  // <--- AGGIUNTO
            player.setState(PlayerState.RUN_LEFT);
        } else if (dy > 0) {
            player.setDirection(Direction.DOWN);  // <--- AGGIUNTO
            player.setState(PlayerState.RUN_FRONT);
        } else if (dy < 0) {
            player.setDirection(Direction.UP);    // <--- AGGIUNTO
            player.setState(PlayerState.RUN_BACK);
        } else {
            updateIdleState();
        }
    }

    private void updateIdleState() {
        PlayerState current = player.getState();

        switch (current) {
            case RUN_RIGHT:
            case IDLE_RIGHT:
            case CAST_RIGHT:   // <-- AGGIUNTO
            case ATTACK_RIGHT: // <-- AGGIUNTO
                player.setState(PlayerState.IDLE_RIGHT);
                break;

            case RUN_LEFT:
            case IDLE_LEFT:
            case CAST_LEFT:    // <-- AGGIUNTO
            case ATTACK_LEFT:  // <-- AGGIUNTO
                player.setState(PlayerState.IDLE_LEFT);
                break;

            case RUN_BACK:
            case IDLE_BACK:
            case CAST_BACK:    // <-- AGGIUNTO
            case ATTACK_BACK:  // <-- AGGIUNTO
                player.setState(PlayerState.IDLE_BACK);
                break;

            case RUN_FRONT:
            case IDLE_FRONT:
            case CAST_FRONT:   // <-- AGGIUNTO
            case ATTACK_FRONT: // <-- AGGIUNTO
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
    public double xCoordinatePlayer() {
        return player.getXCoordinate();
    }

    @Override
    public double yCoordinatePlayer() {
        return player.getYCoordinate();
    }

    @Override
    public double getPlayerDeltaX() {
        return player.getDeltaX();
    }

    @Override
    public double getPlayerDeltaY() {
        return player.getDeltaY();
    }

    @Override
    public int getNumRows() {
        return gameAreaArray.length;
    }

    @Override
    public int getNumColumns() {
        return gameAreaArray[0].length;
    }

    @Override
    public int[][] getGameAreaArray() {
        return gameAreaArray;
    }


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
                // MODIFICA: Invece di svuotare l'array manualmente, chiamiamo destroyBlock
                // che si occupa di cancellare la cassa, fare l'effetto grafico E droppare risorse!
                destroyBlock(currentR, currentC);
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

    @Override
    public void placeBomb() {
        // --- AGGIUNTA: CONTROLLO MUNIZIONI BOMBA ---
        if (player.getBombAmmo() <= 0) {
            System.out.println("Bombe esaurite!");
            return; // Blocca il piazzamento
        }

        double centerX = player.getXCoordinate() + (Config.ENTITY_LOGICAL_HITBOX_WIDTH / 2.0);
        double centerY = player.getYCoordinate() + 0.35;

        int col = (int) Math.floor(centerX);
        int row = (int) Math.floor(centerY);

        if (row < 0 || row >= Config.GRID_HEIGHT || col < 0 || col >= Config.GRID_WIDTH) return;

        if (gameAreaArray[row][col] != Config.CELL_EMPTY) {
            if (gameAreaArray[row - 1][col] == Config.CELL_EMPTY) {
                row = row - 1;
            } else {
                return;
            }
        }

        // Controllo sovrapposizione bombe
        for (Bomb b : activeBombs) {
            if (b.getRow() == row && b.getCol() == col) return;
        }

        // --- AGGIUNTA: CONSUMO MUNIZIONE ---
        player.addBombAmmo(-1); // La consuma solo se la piazza effettivamente!

        activeBombs.add(new Bomb(row, col, Config.BOMB_DETONATION_TICKS, player.getBombRadius()));
        System.out.println("Bomba piazzata in [" + row + "," + col + "] (Rimaste: " + player.getBombAmmo() + ")");
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
            boolean hitX = eX < expX + expSize && eX + eW > expX;
            boolean hitY = eY < expY + expSize && eY + eH > expY;

            if (hitX && hitY) {
                it.remove(); // RIMUOVI IL NEMICO DALLA LISTA

                // --- AGGIUNTA DROP GOBLIN ---
                generateGoblinDrop(eX, eY);

                System.out.println("Goblin eliminato dall'esplosione in [" + row + "," + col + "]!");
            }
        }

        // --- DANNO AL PLAYER ---
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
    public PlayerState getPlayerState() {
        return this.player.getState();
    }


    private void manageSpawning() {
        int currentEnemies = enemies.size();

        // Se li abbiamo uccisi tutti, il portale si spegne e potremo passarci per vincere!
        if (currentEnemies == 0) {
            return;
        }

        // Se il portale è stato scoperto E ci sono meno di 6 goblin vivi, genera mostri!
        if (portalRevealed && currentEnemies < 6) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPortalSpawnTime > 10000) { // 10 secondi (10000 ms)
                spawnEnemyAtPortal();
                lastPortalSpawnTime = currentTime;
            }
        }
    }

    private void spawnEnemyAtPortal() {
        int typeIndex = randomGenerator.nextInt(3);
        Enemy newEnemy;

        // Il goblin nasce ESATTAMENTE sulle coordinate del portale
        switch (typeIndex) {
            case 0 -> newEnemy = new ChasingGoblin(portalCol, portalRow);
            case 1 -> newEnemy = new ShooterGoblin(portalCol, portalRow);
            default -> newEnemy = new CommonGoblin(portalCol, portalRow);
        }

        enemies.add(newEnemy);
        System.out.println("Allarme! Il Portale ha sputato fuori un " + newEnemy.getType() + "!");
    }

    private void spawnEnemy() {
        int r, c;
        int attempts = 0;
        boolean spawned = false;

        // Aumentiamo i tentativi a 100 per essere sicuri di trovare spazio
        while (!spawned && attempts < 100) {
            c = randomGenerator.nextInt(Config.GRID_WIDTH);
            r = randomGenerator.nextInt(Config.GRID_HEIGHT);

            if (isValidSpawnPoint(c, r)) {

                // Generazione Casuale Equilibrata
                int typeIndex = randomGenerator.nextInt(3);
                Enemy newEnemy;

                switch (typeIndex) {
                    case 0 -> newEnemy = new ChasingGoblin(c, r);
                    case 1 -> newEnemy = new ShooterGoblin(c, r);
                    default -> newEnemy = new CommonGoblin(c, r);
                }

                enemies.add(newEnemy);
                spawned = true;
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
        if (player.isInvincible()) return;

        // lifeLost sarà TRUE solo se non avevamo lo scudo
        boolean lifeLost = player.takeDamage();

        // RESET POSIZIONE (Respawn) SOLO SE ABBIAMO PERSO LA VITA
        if (lifeLost) {
            player.setXCoordinate(0.0);
            player.setYCoordinate(0.0);
            player.setDelta(0, 0);
            player.setState(PlayerState.IDLE_FRONT);
            System.out.println("RESPAWN: Player riportato all'inizio.");
        }
    }

    private void updateEnemies() {
        for (Enemy enemy : enemies) {
            enemy.updateBehavior();
        }
    }

    @Override
    public void updateGameLogic() {
        elapsedTicks++;

        // 1. GESTIONE PLAYER (O si muove, O spara)
        if (player.isCasting()) {
            player.decrementCastTimer();
            if (player.getCastTimer() <= 0) {
                player.finishCast();
                spawnAuraProjectile();
                updateIdleState();
            }
        } else {
            // Viene chiamato UNA SOLA VOLTA!
            updatePlayerMovement();
            checkItemPickup();
        }

        // 2. AGGIORNAMENTO ENTITÀ (Chiamati UNA SOLA VOLTA!)
        updateEnemies();
        updateBombs();
        updateProjectiles();

        // 3. COLLISIONI E SPAWN
        checkCollisions();
        manageSpawning();

        // 4. PULIZIA GRAFICA (Fuoco e Casse)
        java.util.Iterator<int[]> it = activeFire.iterator();
        while (it.hasNext()) {
            int[] f = it.next();
            f[3]--;
            if (f[3] <= 0) it.remove();
        }

        long now = System.currentTimeMillis();
        destructionEffects.removeIf(bd -> (now - bd.getCreationTime()) > 500);
        // --- AGGIUNTA: CONTROLLO GATE E FINE LIVELLO ---
        if (!levelCompletedFlag) {
            checkGateCollision();
        }
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
            p.update(); // Muove il proiettile di uno step

            if (!p.isActive()) {
                it.remove(); // Rimuove se ha colpito un muro o esaurito la gittata
                continue;
            }

            // --- 1. PROIETTILE DEL GOBLIN (OSSO) -> Cerca solo il Player ---
            if (p.isEnemyProjectile()) {
                if (Math.abs(p.getX() - player.getXCoordinate()) < 0.5 &&
                        Math.abs(p.getY() - player.getYCoordinate()) < 0.5) {

                    handlePlayerHit();
                    p.setActive(false); // L'osso si rompe addosso al player
                }
            }

            // --- 2. PROIETTILE DEL PLAYER (AURA) -> Cerca solo i Nemici ---
            else {
                Iterator<Enemy> eIt = enemies.iterator();
                boolean hitEnemy = false;

                while(eIt.hasNext()) {
                    Enemy e = eIt.next();
                    // Tolleranza hitbox 0.6 per centrare il goblin
                    if (Math.abs(p.getX() - e.getX()) < 0.6 && Math.abs(p.getY() - e.getY()) < 0.6) {
                        eIt.remove(); // Elimina il Goblin dalla lista

                        // --- AGGIUNTA DROP GOBLIN ---
                        generateGoblinDrop(e.getX(), e.getY());

                        System.out.println("Goblin fulminato dall'Aura!");
                        hitEnemy = true;
                        break; // L'aura NON perfora: uccide un solo nemico e si ferma!
                    }
                }

                if (hitEnemy) {
                    p.setActive(false); // L'Aura si disintegra addosso al goblin
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




    // Helper privato per evitare crash se la View chiede un indice che non esiste più
    private boolean isValidIndex(int index) {
        return index >= 0 && index < enemies.size();
    }


    @Override
    public boolean isAreaOccupiedByOtherEnemy(double nextX, double nextY, Enemy self) {
        double size = 0.55;

        for (Enemy other : enemies) {
            if (other == self) continue;

            double nextDistX = Math.abs(nextX - other.getX());
            double nextDistY = Math.abs(nextY - other.getY());

            // I due goblin stanno per collidere visivamente?
            if (nextDistX < size && nextDistY < size) {

                double currentDistX = Math.abs(self.getX() - other.getX());
                double currentDistY = Math.abs(self.getY() - other.getY());

                // IL SEGRETO: Bloccali SOLO se stanno tentando di avvicinarsi ulteriormente!
                if (nextDistX < currentDistX || nextDistY < currentDistY) {
                    return true;
                }
                // Se stanno cercando di allontanarsi, lasciali fare per sbrogliare la sovrapposizione!
            }
        }
        return false;
    }

    @Override
    public boolean isEnemyAttacking(int index) {
        if (isValidIndex(index) && enemies.get(index) instanceof ShooterGoblin) {
            return ((ShooterGoblin) enemies.get(index)).isActuallyAttacking();
        }
        return false;
    }

    @Override
    public boolean isEnemyWaiting(int index) {
        if (isValidIndex(index) && enemies.get(index) instanceof ShooterGoblin) {
            return ((ShooterGoblin) enemies.get(index)).isWaiting();
        }
        return false;
    }


    private Bomb getBombAt(int r, int c) {
        for (Bomb b : activeBombs) {
            if (b.getRow() == r && b.getCol() == c) {
                return b;
            }
        }
        return null;
    }
    // In src/model/Model.java

    /**
     * Metodo specifico per i Goblin: permette un movimento più fluido
     * e riduce drasticamente la collisione tra nemici per evitare blocchi.
     */
// In Model.java


    // ==========================================================
    // METODI AD INDICE PER AZZERARE L'USO DEL GARBAGE COLLECTOR
    // ==========================================================

    // --- BOMBE ---
    @Override public int getBombCount() { return activeBombs.size(); }

    @Override public int getBombRow(int index) {
        return isValidBombIndex(index) ? activeBombs.get(index).getRow() : 0;
    }

    @Override public int getBombCol(int index) {
        return isValidBombIndex(index) ? activeBombs.get(index).getCol() : 0;
    }

    @Override public int getBombElapsedTime(int index) {
        if (!isValidBombIndex(index)) return 0;
        return (int) (System.currentTimeMillis() - activeBombs.get(index).getCreationTime());
    }

    private boolean isValidBombIndex(int index) { return index >= 0 && index < activeBombs.size(); }

    // --- PROIETTILI ---
    @Override public int getProjectileCount() { return projectiles.size(); }

    @Override public double getProjectileX(int index) {
        return isValidProjIndex(index) ? projectiles.get(index).getX() : 0;
    }

    @Override public double getProjectileY(int index) {
        return isValidProjIndex(index) ? projectiles.get(index).getY() : 0;
    }

    @Override public boolean isProjectileEnemy(int index) {
        return isValidProjIndex(index) && projectiles.get(index).isEnemyProjectile();
    }

    @Override public int getProjectileDirection(int index) {
        return isValidProjIndex(index) ? projectiles.get(index).getDirection().ordinal() : 0;
    }

    private boolean isValidProjIndex(int index) { return index >= 0 && index < projectiles.size(); }

    // --- DISTRUZIONI (Casse) ---
    @Override public int getDestructionCount() { return destructionEffects.size(); }

    @Override public int getDestructionRow(int index) {
        return isValidDestIndex(index) ? destructionEffects.get(index).getRow() : 0;
    }

    @Override public int getDestructionCol(int index) {
        return isValidDestIndex(index) ? destructionEffects.get(index).getCol() : 0;
    }

    @Override public int getDestructionElapsedTime(int index) {
        if (!isValidDestIndex(index)) return 0;
        return (int) (System.currentTimeMillis() - destructionEffects.get(index).getCreationTime());
    }

    private boolean isValidDestIndex(int index) { return index >= 0 && index < destructionEffects.size(); }

    // --- FUOCO ---
    @Override public int getFireCount() { return activeFire.size(); }

    @Override public int getFireRow(int index) {
        return isValidFireIndex(index) ? activeFire.get(index)[0] : 0;
    }

    @Override public int getFireCol(int index) {
        return isValidFireIndex(index) ? activeFire.get(index)[1] : 0;
    }

    @Override public int getFireType(int index) {
        return isValidFireIndex(index) ? activeFire.get(index)[2] : 0;
    }

    private boolean isValidFireIndex(int index) { return index >= 0 && index < activeFire.size(); }

    @Override
    public void playerShoot() {
        // Se è in cooldown o sta già attaccando, ignora l'input
        if (!player.canCast() || player.isCasting()) return;

        // --- AGGIUNTA: CONTROLLO MUNIZIONI AURA ---
        if (player.getAuraAmmo() <= 0) {
            System.out.println("Colpi d'Aura esauriti!");
            return; // Blocca il colpo
        }
        player.addAuraAmmo(-1); // Consuma 1 munizione

        player.startCast();
        player.setDelta(0, 0); // Frena di colpo (Rooting)

        // Cambia lo stato per mostrare l'animazione CAST
        switch (player.getDirection()) {
            case UP -> player.setState(PlayerState.CAST_BACK);
            case DOWN -> player.setState(PlayerState.CAST_FRONT);
            case LEFT -> player.setState(PlayerState.CAST_LEFT);
            case RIGHT -> player.setState(PlayerState.CAST_RIGHT);
        }
        System.out.println("Player: Inizio lancio Aura... (Munizioni rimaste: " + player.getAuraAmmo() + ")");
    }

    private void spawnAuraProjectile() {
        double startX = player.getXCoordinate();
        double startY = player.getYCoordinate();
        utils.Direction dir = player.getDirection();

        double projX = startX;
        double projY = startY;

        // --- VALORI DI OFFSET MANUALI (Modificali a piacimento!) ---
        // 0.5 = Mezza cella. Usa valori positivi/negativi per centrare l'Aura
        double OFFSET_RIGHT = 0.7;
        double OFFSET_LEFT  = -0.7;
        double OFFSET_DOWN  = 0.7;
        double OFFSET_UP    = -0.7;

        switch (dir) {
            case RIGHT -> projX += OFFSET_RIGHT;
            case LEFT  -> projX += OFFSET_LEFT;
            case DOWN  -> projY += OFFSET_DOWN;
            case UP    -> projY += OFFSET_UP;
        }

        Projectile aura = new AuraProjectile(projX, projY, dir);
        addProjectile(aura);
    }

    @Override
    public void destroyBlock(int row, int col) {
        if (gameAreaArray[row][col] == Config.CELL_DESTRUCTIBLE_BLOCK) {
            gameAreaArray[row][col] = Config.CELL_EMPTY;

            // --- PASSO 5: DROP INTELLIGENTE PRECALCOLATO ---
            String key = row + "," + col; // Creiamo la chiave "riga,colonna"
            if (hiddenLoot.containsKey(key)) {
                ItemType droppedItem = hiddenLoot.get(key); // Leggiamo cosa c'era nascosto
                activeItems.add(new Collectible(col, row, droppedItem));
                System.out.println("Cassa droppa: " + droppedItem.name());
                hiddenLoot.remove(key); // Rimuoviamo l'oggetto dalla memoria
            }

            // --- CONTROLLO PORTALE ---
            if (row == portalRow && col == portalCol) {
                portalRevealed = true;
                lastPortalSpawnTime = System.currentTimeMillis(); // Fa partire il timer
                System.out.println("ALLARME! Portale scoperto prematuramente in [" + row + ", " + col + "]!");
            }

            destructionEffects.add(new BlockDestruction(row, col));
            System.out.println("Cassa distrutta in [" + row + ", " + col + "]");
        }
    }

    @Override
    public void staffAttack() {
        double startX = player.getXCoordinate();
        double startY = player.getYCoordinate();
        utils.Direction dir = player.getDirection();

        double targetX = startX;
        double targetY = startY;
        double OFFSET = 0.7;

        switch (dir) {
            case RIGHT -> targetX += OFFSET;
            case LEFT  -> targetX -= OFFSET;
            case DOWN  -> targetY += OFFSET;
            case UP    -> targetY -= OFFSET;
        }

        Rectangle2D.Double staffHitbox = new Rectangle2D.Double(targetX, targetY, 0.8, 0.8);

        // Gestione Blocchi
        int gridX = (int) Math.round(targetX);
        int gridY = (int) Math.round(targetY);
        int[][] map = getGameAreaArray();

        if (gridY >= 0 && gridY < map.length && gridX >= 0 && gridX < map[0].length) {
            if (map[gridY][gridX] == Config.CELL_DESTRUCTIBLE_BLOCK) {
                // MODIFICA: Usiamo il metodo ufficiale anche per il bastone
                destroyBlock(gridY, gridX);
            }
        }

        // Gestione Nemici
        Iterator<Enemy> eIt = enemies.iterator();
        while (eIt.hasNext()) {
            Enemy e = eIt.next();
            Rectangle2D.Double enemyBox = new Rectangle2D.Double(e.getX(), e.getY(), 0.6, 0.6);
            if (staffHitbox.intersects(enemyBox)) {
                eIt.remove();

                // MODIFICA: Aggiunto il drop del Power-up quando ucciso col bastone
                generateGoblinDrop(e.getX(), e.getY());
                System.out.println("Goblin eliminato con il bastone!");

                break;
            }
        }

        // --- LOGICA DI VISUALIZZAZIONE (Stessa del Casting) ---
        // Impostiamo lo stato ATTACK specifico per la direzione
        switch (dir) {
            case UP    -> player.setState(PlayerState.ATTACK_BACK);
            case DOWN  -> player.setState(PlayerState.ATTACK_FRONT);
            case LEFT  -> player.setState(PlayerState.ATTACK_LEFT);
            case RIGHT -> player.setState(PlayerState.ATTACK_RIGHT);
        }

        // Fondamentale: aggiorniamo il tempo di inizio stato per la View
    }

    @Override
    public void resetPlayerStateAfterAction() {
        utils.Direction dir = player.getDirection();

        PlayerState idleState = switch (dir) {
            case UP    -> PlayerState.IDLE_BACK;
            case DOWN  -> PlayerState.IDLE_FRONT;
            case LEFT  -> PlayerState.IDLE_LEFT;
            case RIGHT -> PlayerState.IDLE_RIGHT;
        };

        player.setState(idleState);
    }


    // --- LOTTERIA DEI GOBLIN (Power-up Intelligenti) ---
    private void generateGoblinDrop(double x, double y) {
        Random rand = new Random();

        // MODIFICA TEMPORANEA PER TEST: Droppa SEMPRE al 100%
        // (In futuro rimetteremo: if (rand.nextInt(100) >= 40) return;)

        List<utils.ItemType> availableDrops = new ArrayList<>();
        if (!player.hasShield()) availableDrops.add(utils.ItemType.POWER_SHIELD);
        if (!player.hasMaxRadius()) availableDrops.add(utils.ItemType.POWER_RADIUS);
        if (!player.hasMaxSpeed()) availableDrops.add(utils.ItemType.POWER_SPEED);

        if (availableDrops.isEmpty()) {
            System.out.println("Nessun drop: il player ha già tutto maxato!");
            return;
        }

        int dropIndex = rand.nextInt(availableDrops.size());
        utils.ItemType droppedItem = availableDrops.get(dropIndex);

        int col = (int) Math.floor(x);
        int row = (int) Math.floor(y);
        activeItems.add(new Collectible(col, row, droppedItem));
        System.out.println("Goblin droppa: " + droppedItem.name());
    }

    private void checkItemPickup() {
        Iterator<Collectible> it = activeItems.iterator();
        double pX = player.getXCoordinate();
        double pY = player.getYCoordinate();

        while (it.hasNext()) {
            Collectible item = it.next();
            // Controllo tolleranza 0.5 per la raccolta
            if (Math.abs(pX - item.getX()) < 0.6 && Math.abs(pY - item.getY()) < 0.6) {
                applyItemEffect(item.getType());
                it.remove(); // Oggetto raccolto!
            }
        }
    }

    private void applyItemEffect(ItemType type) {
        switch (type) {
            case AMMO_BOMB -> player.addBombAmmo(10);
            case AMMO_AURA -> player.addAuraAmmo(10);
            case POWER_SHIELD -> player.setShield(true);
            case POWER_RADIUS -> {
                player.setMaxRadius(true);
                // Qui in futuro metterai: player.setBombRadius(Config.BOMB_RADIUS + 1);
            }
            case POWER_SPEED -> {
                player.setMaxSpeed(true);
                // Qui in futuro aumenteremo la logica della velocità
            }
        }
        System.out.println("RACCOLTO: " + type.name());
    }

    @Override
    public java.util.List<Collectible> getActiveItems() {
        return activeItems;
    }

    @Override public int getPlayerBombAmmo() { return player.getBombAmmo(); }
    @Override public int getPlayerAuraAmmo() { return player.getAuraAmmo(); }
    @Override public boolean hasPlayerShield() { return player.hasShield(); }
    @Override public boolean hasPlayerMaxRadius() { return player.hasMaxRadius(); }
    @Override public boolean hasPlayerMaxSpeed() { return player.hasMaxSpeed(); }

    @Override public int getPortalRow() { return portalRow; }
    @Override public int getPortalCol() { return portalCol; }
    @Override public boolean isPortalRevealed() { return portalRevealed; }

// ==========================================================
    // METODI PER LOGICA DEL GATE E CAMBIO LIVELLO
    // ==========================================================

    private void checkGateCollision() {
        // Il Gate si attiva (e diventa attraversabile) SOLO se non ci sono più nemici
        if (enemies.isEmpty()) {
            gateActive = true;

            // Convertiamo le coordinate in indici di matrice per il Player
            double centerX = player.getXCoordinate() + (Config.ENTITY_LOGICAL_HITBOX_WIDTH / 2.0);
            double centerY = player.getYCoordinate() + 0.35;

            int col = (int) Math.floor(centerX);
            int row = (int) Math.floor(centerY);

            // Sicurezza: controlliamo di non essere fuori dai bordi
            if (row >= 0 && row < Config.GRID_HEIGHT && col >= 0 && col < Config.GRID_WIDTH) {
                // Se il Player calpesta l'ID del Gate (9)... Livello completato!
                if (gameAreaArray[row][col] == GATE_ID) {
                    levelCompletedFlag = true;
                }
            }
        }
    }
    @Override
    public void prepareNextLevel(int[][] newMap) {
        // 1. Reset flag del Gate
        levelCompletedFlag = false;
        gateActive = false;

        // 2. Progressione Zona e Tema
        currentZone++;
        if (currentZone > 2) {
            currentZone = 0;   // Ricomincia dal Villaggio
            difficultyCycle++; // Aumenta il moltiplicatore difficoltà
            System.out.println("VITTORIA GLOBALE! Inizio ciclo di difficoltà: " + difficultyCycle);
        } else {
            System.out.println("Avanzamento al livello: " + currentZone);
        }

        // --- AGGIUNTA: AGGIORNAMENTO DEL TEMA LOGICO ---
        switch (currentZone) {
            case 1:  currentTheme = "FOREST"; break;
            case 2:  currentTheme = "CAVE"; break;
            case 0:
            default: currentTheme = "VILLAGE"; break;
        }

        // 3. Copia la nuova mappa generata...
        // ... (il resto del metodo rimane uguale) ...



        // 4. Svuota le vecchie entità dal campo per il nuovo livello
        activeBombs.clear();
        projectiles.clear();
        activeFire.clear();
        activeItems.clear();
        enemies.clear();

        // 5. Riposiziona il Player (nella safe-zone iniziale del bunker [0,0])
        player.setXCoordinate(0.0);
        player.setYCoordinate(0.0);
        player.setDelta(0, 0);
        player.setState(utils.PlayerState.IDLE_FRONT);

        // 6. Spawn iniziale dei nuovi nemici per la nuova mappa
        for (int i = 0; i < 6; i++) {
            spawnEnemy();
        }
    }

    // --- GETTER IMPLEMENTATI PER L'INTERFACCIA ---
    @Override public int getCurrentZone() { return currentZone; }
    @Override public int getDifficultyCycle() { return difficultyCycle; }
    @Override public boolean isGateActive() { return gateActive; }
    @Override public boolean isLevelCompletedFlag() { return levelCompletedFlag; }
    @Override public String getCurrentTheme() { return currentTheme; }

    public static IModel getInstance() {
        if (instance == null) instance = new Model();
        return instance;
    }
}

