package goblinhunter.model;

public class Player extends Entity {
    private  int XCoordinate;
    private  int YCoordinate;
    public Player(int startX , int startY){
        this.XCoordinate = startX;
        this.YCoordinate = startY;
    }
    public int getXCoordinate(){
        return this.XCoordinate;
    }
    public int getYCoordinate(){
        return this.YCoordinate;
    }
    public void move(int x,int y){
        this.XCoordinate += x;
        this.YCoordinate += y;
    }


}
