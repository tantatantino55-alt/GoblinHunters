package goblinhunter.model;

public class Player extends Entity {
    private  int XCoordinate;
    private  int YCoordinate;
    private int deltaX = 0;
    private int deltaY = 0;

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

    public void updateXcoordinate (int i) {
            this.XCoordinate += i;
    }

    public void updateYcoordinate (int j) {
            this.YCoordinate += j;
    }

    public void updateDelta(int dx, int dy){
        this.deltaX = dx;
        this.deltaY = dy;
    }


}
