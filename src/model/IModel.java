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
    public void PlaceBomb();
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
}