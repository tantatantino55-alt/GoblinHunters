package controller;

import model.Model;
import utils.Direction;
import utils.EnemyType;
import utils.PlayerState;
import view.View;

public class ControllerForView implements IControllerForView {

    private static ControllerForView instance = null;
    private ControllerForView() {}

    @Override public void openGameGUI() { View.getInstance().openGameGUI(); }
    @Override public void closeGameGUI() { View.getInstance().closeGameGUI(); }
    @Override public int getNumColumns() { return Model.getInstance().getNumColumns(); }
    @Override public int getNumRows() { return Model.getInstance().getNumRows(); }
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
    @Override public Direction getEnemyTelegraph(int index) { return Model.getInstance().getEnemyTelegraph(index); }

    @Override public PlayerState getPlayerState() { return Model.getInstance().getPlayerState(); }
    @Override public long getPlayerStateStartTime() { return Model.getInstance().getPlayerStateStartTime(); }
    @Override public boolean isPlayerInvincible() { return Model.getInstance().isPlayerInvincible(); }
    @Override public int getPlayerLives() { return Model.getInstance().getPlayerLives(); }
    @Override public int getElapsedTimeInSeconds() { return Model.getInstance().getElapsedTimeInSeconds(); }

    // --- DELEGAZIONE METODI AD INDICE ---
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
    public boolean isEnemyAttacking(int index){
        return Model.getInstance().isEnemyAttacking(index);
    }

    @Override
    public boolean isEnemyWaiting(int index){
        return Model.getInstance().isEnemyWaiting(index);
    }

    @Override
    public void playerShoot() {
        Model.getInstance().playerShoot();
    }
    // In ControllerForView.java
    @Override
    public void resetPlayerStateAfterAction() {
        // Il controller delega la logica al model
        Model.getInstance().resetPlayerStateAfterAction();
    }

    @Override
    public void staffAttack() {
        Model.getInstance().staffAttack();
    }

    @Override public int getPlayerBombAmmo() { return Model.getInstance().getPlayerBombAmmo(); }
    @Override public int getPlayerAuraAmmo() { return Model.getInstance().getPlayerAuraAmmo(); }
    @Override public boolean hasPlayerShield() { return Model.getInstance().hasPlayerShield(); }
    @Override public boolean hasPlayerMaxRadius() { return Model.getInstance().hasPlayerMaxRadius(); }
    @Override public boolean hasPlayerMaxSpeed() { return Model.getInstance().hasPlayerMaxSpeed(); }

    @Override
    public int getPortalRow() {
        return model.Model.getInstance().getPortalRow();
    }

    @Override
    public int getPortalCol() {
        return model.Model.getInstance().getPortalCol();
    }

    @Override
    public boolean isPortalRevealed() {
        return model.Model.getInstance().isPortalRevealed();
    }
    @Override
    public String getCurrentTheme() {
        return Model.getInstance().getCurrentTheme();
    }
    public boolean isTransitioning(){
        return Model.getInstance().isTransitioning();
    }
    @Override public int getCollectibleCount() {
        return Model.getInstance().getCollectibleCount();
    }
    @Override public double getCollectibleX(int index) {
        return Model.getInstance().getCollectibleX(index);
    }
    @Override public double getCollectibleY(int index) {
        return Model.getInstance().getCollectibleY(index);
    }
    @Override public utils.ItemType getCollectibleType(int index) {
        return Model.getInstance().getCollectibleType(index);
    }
    @Override public long getCollectibleSpawnTime(int index) {
        return Model.getInstance().getCollectibleSpawnTime(index);
    }

    @Override
    public long getPortalRevealTime() {
        return Model.getInstance().getPortalRevealTime();
    }

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
        return  Model.getInstance().getExitGateCol();
    }

    @Override
    public int getExitGateRow() {
        return  Model.getInstance().getExitGateRow();
    }
    @Override
    public String getEnemyState(int index){
        return Model.getInstance().getEnemyState(index);
    }
    @Override
    public boolean isEnemyInvincible(int index){
        return Model.getInstance().isEnemyInvincible(index);
    }
    @Override
    public long getEnemyStateStartTime(int index){
        return Model.getInstance().getEnemyStateStartTime(index);
    }

    // --- CREPE DEL BOSS ---
    @Override public int getCrackCount()         { return Model.getInstance().getCrackCount(); }
    @Override public int getCrackRow(int index)  { return Model.getInstance().getCrackRow(index); }
    @Override public int getCrackCol(int index)  { return Model.getInstance().getCrackCol(index); }

    // --- HUD BOSS ---
    @Override public int getBossHP()    { return Model.getInstance().getBossHP(); }
    @Override public int getBossMaxHP() { return Model.getInstance().getBossMaxHP(); }

    // --- HUD JUICY ANIMATION TRIGGER ---
    @Override
    public void triggerPickupAnimation(utils.ItemType type) {
        // Il Controller fa da ponte: il Model non conosce la View.
        // Qui deleghiamo direttamente all'HudItemAnimator (componente View puro).
        view.HudItemAnimator.getInstance().triggerPickupAnimation(type);
    }

    @Override
    public int getScore() {
        return Model.getInstance().getScore();
    }

    public static IControllerForView getInstance() {
        if (instance == null) instance = new ControllerForView();
        return instance;
    }
}