package goblinhunters.controller;

import goblinhunters.model.MenuModel;
import goblinhunters.model.Model;
import goblinhunters.model.ScoreRepository;
import goblinhunters.utils.*;
import goblinhunters.view.AudioManager;
import goblinhunters.view.MenuDrawer;
import goblinhunters.view.ResourceLoader;
import goblinhunters.view.View;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ControllerForView implements IControllerForView {

    private static ControllerForView instance = null;
    private ControllerForView() {}

    @Override public void openGameGUI() {
        View.getInstance().openGameGUI();
        AudioManager.getInstance().loadAndPlay("/goblin_theme.wav");
    }
    @Override
    public GameState getGameState() {
        return ControllerForModel.getInstance().getGameState();
    }

    @Override
    public void menuHandleClick(int characterIndex) {
        MenuModel.getInstance().selectCharacter(characterIndex);
    }

    @Override
    public void menuConfirmSelection() {
        if (!MenuModel.getInstance().isNameValid()) {
            MenuDrawer.getInstance().setShowNameError(true);
            return;
        }
        MenuDrawer.getInstance().setShowNameError(false);
        MenuModel.getInstance().setTypingName(false);
        MenuModel.getInstance().confirmSelection();
        if (MenuModel.getInstance().isCharacterConfirmed()) {
            startGameWithSelectedCharacter();
        }
    }

    /**
     * Transitions MENU → PLAYING:
     * 1. Reads the confirmed character type.
     * 2. Reloads player animations with the correct sprite sheet.
     * 3. Sets game state to PLAYING.
     */
    private void startGameWithSelectedCharacter() {
        CharacterType selected = MenuModel.getInstance().getConfirmedCharacterType();
        if (selected == null) return;

        ResourceLoader.reloadPlayerAnimations(selected.getSheetPath());
        ControllerForModel.getInstance().setGameState(GameState.PLAYING);
    }

    @Override
    public void resetGame() {
        ControllerForModel.getInstance().resetGame();
        MenuModel.getInstance().reset();
    }

    @Override public void   menuAppendNameChar(char c)  { MenuModel.getInstance().appendNameChar(c); }
    @Override public void   menuDeleteNameChar()         { MenuModel.getInstance().deleteNameChar(); }
    @Override public void   menuSetTypingName(boolean v) { MenuModel.getInstance().setTypingName(v); }
    @Override public String getMenuPlayerName()          { return MenuModel.getInstance().getPlayerName(); }
    @Override public int    getMenuSelectedIndex()       { return MenuModel.getInstance().getSelectedIndex(); }
    @Override public boolean isMenuTypingName()          { return MenuModel.getInstance().isTypingName(); }

    @Override public List<ScoreEntry> getTopScores() {
        return ScoreRepository.getInstance().getTopScores().stream()
                .map(r -> new ScoreEntry(r.name, r.score))
                .collect(Collectors.toList());
    }
    @Override public double getXCoordinatePlayer() { return Model.getInstance().xCoordinatePlayer(); }
    @Override public double getYCoordinatePlayer() { return Model.getInstance().yCoordinatePlayer(); }
    @Override public double getDeltaX() { return Model.getInstance().getPlayerDeltaX(); }
    @Override public double getDeltaY() { return Model.getInstance().getPlayerDeltaY(); }
    @Override public void setPlayerMovement(double dx, double dy) { Model.getInstance().setPlayerDelta(dx, dy); }
    @Override public int[][] getGameAreaArray() { return Model.getInstance().getGameAreaArray(); }
    @Override public void placeBomb() { Model.getInstance().placeBomb(); }
    @Override public void requestRepaint() { View.getInstance().requestRepaint(); }

    @Override public int getEnemyCount() { return Model.getInstance().getEnemyCount(); }
    @Override public double getEnemyX(int index) { return Model.getInstance().getEnemyX(index); }
    @Override public double getEnemyY(int index) { return Model.getInstance().getEnemyY(index); }
    @Override public Direction getEnemyDirection(int index) { return Model.getInstance().getEnemyDirection(index); }
    @Override public EnemyType getEnemyType(int index) { return Model.getInstance().getEnemyType(index); }

    @Override public PlayerState getPlayerState() { return Model.getInstance().getPlayerState(); }
    @Override public long getPlayerStateStartTime() { return Model.getInstance().getPlayerStateStartTime(); }
    @Override public boolean isPlayerInvincible() { return Model.getInstance().isPlayerInvincible(); }
    @Override public int getPlayerLives() { return Model.getInstance().getPlayerLives(); }
    @Override public int getElapsedTimeInSeconds() { return Model.getInstance().getElapsedTimeInSeconds(); }
    @Override public int getScore() { return Model.getInstance().getScore(); }

    @Override public int getBombCount() { return Model.getInstance().getBombCount(); }
    @Override public int getBombRow(int index) { return Model.getInstance().getBombRow(index); }
    @Override public int getBombCol(int index) { return Model.getInstance().getBombCol(index); }
    @Override public int getBombElapsedTime(int index) { return Model.getInstance().getBombElapsedTime(index); }

    @Override public int getProjectileCount() { return Model.getInstance().getProjectileCount(); }
    @Override public double getProjectileX(int index) { return Model.getInstance().getProjectileX(index); }
    @Override public double getProjectileY(int index) { return Model.getInstance().getProjectileY(index); }
    @Override public boolean isProjectileEnemy(int index) { return Model.getInstance().isProjectileEnemy(index); }
    @Override public int getProjectileDirection(int index) { return Model.getInstance().getProjectileDirection(index); }

    @Override public int getDestructionCount() { return Model.getInstance().getDestructionCount(); }
    @Override public int getDestructionRow(int index) { return Model.getInstance().getDestructionRow(index); }
    @Override public int getDestructionCol(int index) { return Model.getInstance().getDestructionCol(index); }
    @Override public int getDestructionElapsedTime(int index) { return Model.getInstance().getDestructionElapsedTime(index); }

    @Override public int getFireCount() { return Model.getInstance().getFireCount(); }
    @Override public int getFireRow(int index) { return Model.getInstance().getFireRow(index); }
    @Override public int getFireCol(int index) { return Model.getInstance().getFireCol(index); }
    @Override public int getFireType(int index) { return Model.getInstance().getFireType(index); }

    @Override
    public void playerShoot() {
        Model.getInstance().playerShoot();
    }

    @Override
    public void resetPlayerStateAfterAction() {
        Model.getInstance().resetPlayerStateAfterAction();
    }

    @Override
    public void staffAttack() {
        Model.getInstance().staffAttack();
    }

    @Override
    public boolean isStaffUsable() {
        // staff is only usable during the boss-preparation phase (zone 2, crates still present)
        return Model.getInstance().getCurrentZone() == 2
                && Model.getInstance().isPreparationPhase();
    }

    @Override public int getPlayerBombAmmo() { return Model.getInstance().getPlayerBombAmmo(); }
    @Override public int getPlayerAuraAmmo() { return Model.getInstance().getPlayerAuraAmmo(); }
    @Override public boolean hasPlayerShield() { return Model.getInstance().hasPlayerShield(); }
    @Override public boolean hasPlayerMaxRadius() { return Model.getInstance().hasPlayerMaxRadius(); }
    @Override public boolean hasPlayerMaxSpeed() { return Model.getInstance().hasPlayerMaxSpeed(); }

    @Override
    public int getPortalRow() {
        return Model.getInstance().getPortalRow();
    }

    @Override
    public int getPortalCol() {
        return Model.getInstance().getPortalCol();
    }

    @Override
    public boolean isPortalRevealed() {
        return Model.getInstance().isPortalRevealed();
    }

    @Override
    public String getCurrentTheme() {
        return Model.getInstance().getCurrentTheme();
    }

    @Override
    public boolean isTransitioning() {
        return Model.getInstance().isTransitioning();
    }

    @Override public int getCollectibleCount() { return Model.getInstance().getCollectibleCount(); }
    @Override public double getCollectibleX(int index) { return Model.getInstance().getCollectibleX(index); }
    @Override public double getCollectibleY(int index) { return Model.getInstance().getCollectibleY(index); }
    @Override public ItemType getCollectibleType(int index) { return Model.getInstance().getCollectibleType(index); }

    @Override
    public boolean isGateActive() {
        return Model.getInstance().isExitGateActive();
    }

    @Override
    public long getGateActivationTime() {
        return Model.getInstance().getExitGateActivationTime();
    }

    @Override
    public int getExitGateCol() {
        return Model.getInstance().getExitGateCol();
    }

    @Override
    public int getExitGateRow() {
        return Model.getInstance().getExitGateRow();
    }

    @Override public String getEnemyState(int index) { return Model.getInstance().getEnemyState(index); }
    @Override public boolean isEnemyInvincible(int index) { return Model.getInstance().isEnemyInvincible(index); }
    @Override public long getEnemyStateStartTime(int index) { return Model.getInstance().getEnemyStateStartTime(index); }

    @Override public int getCrackCount()        { return Model.getInstance().getCrackCount(); }
    @Override public int getCrackRow(int index) { return Model.getInstance().getCrackRow(index); }
    @Override public int getCrackCol(int index) { return Model.getInstance().getCrackCol(index); }

    @Override public int getBossHP()    { return Model.getInstance().getBossHP(); }
    @Override public int getBossMaxHP() { return Model.getInstance().getBossMaxHP(); }

    @Override public boolean isBossPortalActive() { return Model.getInstance().isBossPortalActive(); }
    @Override public int getBossPortalRow()       { return Model.getInstance().getBossPortalRow(); }
    @Override public int getBossPortalCol()       { return Model.getInstance().getBossPortalCol(); }

    @Override
    public boolean isPaused() {
        return ControllerForModel.getInstance().isPaused();
    }

    @Override
    public void setPaused(boolean paused) {
        ControllerForModel.getInstance().setPaused(paused);
    }

    @Override
    public PauseController getPauseController() {
        return PauseController.getInstance();
    }

    /**
     * Callback registered by GamePanel once at construction time.
     * The controller calls this to forward rebinds to the Swing InputMap.
     */
    private BiConsumer<Integer, String> keyBindingApplier = null;

    @Override
    public void setKeyBindingApplier(BiConsumer<Integer, String> applier) {
        this.keyBindingApplier = applier;
    }

    /**
     * Called by {@link PauseController} when the user confirms a new key.
     * Delegates to the callback registered by GamePanel.
     */
    @Override
    public void applyKeyBinding(int actionIndex, String newKeyName) {
        if (keyBindingApplier != null) {
            keyBindingApplier.accept(actionIndex, newKeyName);
        }
    }

    public static IControllerForView getInstance() {
        if (instance == null) instance = new ControllerForView();
        return instance;
    }
}
