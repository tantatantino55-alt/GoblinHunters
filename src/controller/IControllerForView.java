package controller;

import utils.Direction;
import utils.EnemyType;
import utils.ItemType;
import utils.PlayerState;

import java.util.function.BiConsumer;

public interface IControllerForView {
    public void openGameGUI();
    public void closeGameGUI();
    public int getNumColumns();
    public int getNumRows();
    public double getXCoordinatePlayer();
    public double getYCoordinatePlayer();
    public void requestRepaint();
    public void placeBomb();
    public void setPlayerMovement(double dx, double dy);
    public double getDeltaX();
    public double getDeltaY();
    public int[][] getGameAreaArray();

    int getEnemyCount();
    double getEnemyX(int index);
    double getEnemyY(int index);
    Direction getEnemyDirection(int index);
    EnemyType getEnemyType(int index);
    Direction getEnemyTelegraph(int index);

    PlayerState getPlayerState();
    long getPlayerStateStartTime();
    public boolean isPlayerInvincible();
    public int getPlayerLives();
    public int getElapsedTimeInSeconds();
    public boolean isEnemyAttacking(int index);



    // --- NUOVI METODI A INDICE ---
    int getBombCount();
    int getBombRow(int index);
    int getBombCol(int index);
    int getBombElapsedTime(int index);

    int getProjectileCount();
    double getProjectileX(int index);
    double getProjectileY(int index);
    boolean isProjectileEnemy(int index);
    int getProjectileDirection(int index);

    int getDestructionCount();
    int getDestructionRow(int index);
    int getDestructionCol(int index);
    int getDestructionElapsedTime(int index);

    int getFireCount();
    int getFireRow(int index);
    int getFireCol(int index);
    int getFireType(int index);

    public boolean isEnemyWaiting(int index);

    void playerShoot();

    // In ControllerForView.java
    void resetPlayerStateAfterAction();

    void staffAttack();

    int getPlayerBombAmmo();
    int getPlayerAuraAmmo();
    boolean hasPlayerShield();
    boolean hasPlayerMaxRadius();
    boolean hasPlayerMaxSpeed();

    int getPortalRow();
    int getPortalCol();
    boolean isPortalRevealed();
    String getCurrentTheme();

    boolean isTransitioning();
    // Collectibles (oggetti a terra)
    int getCollectibleCount();
    double getCollectibleX(int index);
    double getCollectibleY(int index);
    ItemType getCollectibleType(int index);
    long getCollectibleSpawnTime(int index);

    long getPortalRevealTime();

    boolean isGateActive();

    long getGateActivationTime();

    int getExitGateCol();

    int getExitGateRow();

    String getEnemyState(int index);
    boolean isEnemyInvincible(int index);
    long getEnemyStateStartTime(int index);

    // --- CREPE DEL BOSS ---
    int getCrackCount();
    int getCrackRow(int index);
    int getCrackCol(int index);

    // --- HUD BOSS ---
    int getBossHP();
    int getBossMaxHP();

    // --- HUD JUICY ANIMATION TRIGGER ---
    /** Notifica la View che un item è stato appena raccolto (avvia l'animazione icona HUD). */
    void triggerPickupAnimation(utils.ItemType type);

    /** Ritorna il punteggio totale corrente. */
    int getScore();

    // --- PAUSE ---
    /** Returns true when the game is paused (logic frozen, pause overlay visible). */
    boolean isPaused();
    /** Toggles or sets the paused state. When paused, the game-loop stops updating. */
    void setPaused(boolean paused);

    // --- PAUSE CONTROLLER ---
    /**
     * Espone il {@link PauseController} alla View.
     */
    PauseController getPauseController();

    // --- KEY BINDING APPLIER ---
    /**
     * Registra il callback che GamePanel espone per applicare le modifiche
     * ai keybindings nell'InputMap/ActionMap di Swing.
     *
     * <p>Chiamato UNA VOLTA da {@code GamePanel} durante la sua costruzione.
     * Il Controller NON dipende da {@code GamePanel} — usa solo questa funzione
     * anonima per propagare le modifiche.</p>
     *
     * @param applier {@code BiConsumer<Integer, String>} dove:
     *                Integer = indice azione (0-6, vedi {@code PauseModel}),
     *                String  = nuovo keyName uppercase, es. "W", "UP", "SPACE"
     */
    void setKeyBindingApplier(BiConsumer<Integer, String> applier);

    /**
     * Propaga un rebind effettuato nel menu di pausa verso il sistema di input di Swing.
     * Chiamato da {@link PauseController} quando l'utente conferma un nuovo tasto.
     *
     * @param actionIndex indice azione (0-6)
     * @param newKeyName  nuovo tasto in formato KeyStroke uppercase (es. "W", "SPACE")
     */
    void applyKeyBinding(int actionIndex, String newKeyName);
}