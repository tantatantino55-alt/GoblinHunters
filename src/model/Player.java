package model;

public class Player extends Entity {
    private  int XCoordinate;
    private  int YCoordinate;
    private int deltaX = 0;
    private int deltaY = 0;
    //da considerare l'uso di un unico delta

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
/*
    public void updateXcoordinate (int i) {
            this.XCoordinate += i;
    }

    public void updateYcoordinate (int j) {
            this.YCoordinate += j;
    }
*/
/*il player si muove, ove possibile, in tutta l'area di gioco, quindi
non si limita a passare da una cella all'altra a scatti di 48 pixel (coordinate logiche), ma 'sfrutta tutti i pixel'.
Quindi le coordinate X e Y del Player sono quelle grafiche.

In questo caso dovremmo avere dei metodi setCoordinate (non basta muoverci di un valore intero +/- 1)

*/
    public void setXCoordinate (int x) {
        this.XCoordinate = x;
    }

    public void setYCoordinate (int y) {
        this.YCoordinate = y;
    }

    public void setDelta(int dx, int dy){
        this.deltaX = dx;
        this.deltaY = dy;
    }

    public int getDeltaX(){
        return this.deltaX;
    }

    public int getDeltaY(){
        return this.deltaY;
    }

}
