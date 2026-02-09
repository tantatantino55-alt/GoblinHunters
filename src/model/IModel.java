package model;

import utils.Direction;
import utils.EnemyType;
import utils.PlayerState;

import java.util.List;

public interface IModel {
    public int getNumColumns();
    public int getNumRows();
    public double xCoordinatePlayer();
    public double yCoordinatePlayer();
    public void setPlayerDelta(double dx, double dy);
    public double getPlayerDeltaX();
    public double getPlayerDeltaY();
    public int[][] getGameAreaArray();
    public boolean isWalkable(double nextX, double nextY);
    public void placeBomb();
    public void updateGameLogic();
    public List<Enemy> getEnemies();
    public int[][] getActiveBombsData();
    PlayerState getPlayerState();
    int getEnemyCount();                  // 1. Quanti sono?
    double getEnemyX(int index);          // 2. Coordinata X dell'i-esimo nemico
    double getEnemyY(int index);          // 3. Coordinata Y dell'i-esimo nemico
    Direction getEnemyDirection(int index); // 4. Dove guarda?
    EnemyType getEnemyType(int index);    // 5. Che cos'è? (Common, Hunter...)
    long getPlayerStateStartTime();

    void addProjectile(Projectile projectile);
    // Restituisce la direzione di mira (o null) dell'i-esimo nemico
    Direction getEnemyTelegraph(int index);

    // Restituisce una lista di "dati grezzi" dei proiettili
    // Ogni double[] contiene: { x, y, tipo, direzione }
    List<double[]> getProjectilesData();
    List<int[]> getDestructionsData();
    public boolean isAreaOccupiedByOtherEnemy(double nextX, double nextY, Enemy enemy);

    List<int[]> getActiveFireData();
}