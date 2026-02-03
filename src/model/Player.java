package model;

public class Player extends Entity {

    private int xCoordinate;
    private int yCoordinate;
    private int deltaX = 0;
    private int deltaY = 0;

    private String currentAction = "PLAYER_FRONT_IDLE"; // Chiave identificativa
    private int frameIndex = 0;      // Indice del fotogramma attuale
    private int totalFrames = 1;     // Numero totale di fotogrammi per l'azione attuale
    private int animationTick = 0;   // Contatore per la frequenza di aggiornamento
    private int animationSpeed = 3; // Velocità dell'animazione (tick per frame)

    public Player(int startX, int startY) {
        this.xCoordinate = startX;
        this.yCoordinate = startY;
    }

    public void updateAnimation() {
        animationTick++;
        if (animationTick >= animationSpeed) {
            animationTick = 0;

            frameIndex = (frameIndex + 1) % totalFrames;
        }
    }

    public void setAction(String newAction, int framesCount) {
        // Cambiamo azione solo se è effettivamente diversa per evitare reset inutili
        if (!this.currentAction.equals(newAction)) {
            this.currentAction = newAction;
            this.totalFrames = (framesCount > 0) ? framesCount : 1;
            this.frameIndex = 0;    // Reset dell'animazione
            this.animationTick = 0; // Reset del contatore tempo
        }
    }


    public int getXCoordinate() { return xCoordinate; }
    public int getYCoordinate() { return yCoordinate; }
    public String getCurrentAction() { return currentAction; }
    public int getFrameIndex() { return frameIndex; }

    public void setXCoordinate(int x) { this.xCoordinate = x; }
    public void setYCoordinate(int y) { this.yCoordinate = y; }
    public void setDelta(int dx, int dy) { this.deltaX = dx; this.deltaY = dy; }
    public int getDeltaX() { return deltaX; }
    public int getDeltaY() { return deltaY; }

    public void setAnimationSpeed(int speed) {
        this.animationSpeed = speed;
    }
}
