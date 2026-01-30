package model;

/**
 * Classe Model del Player.
 * Gestisce esclusivamente la logica dei dati (posizione, movimento e stato dell'animazione).
 * Non ha dipendenze da classi grafiche o manager esterni per rispettare l'MVC.
 */
public class Player extends Entity {

    // Coordinate e Movimento
    private int xCoordinate;
    private int yCoordinate;
    private int deltaX = 0;
    private int deltaY = 0;

    // Stato dell'Animazione (Pura logica numerica)
    private String currentAction = "PLAYER_FRONT_IDLE"; // Chiave identificativa
    private int frameIndex = 0;      // Indice del fotogramma attuale
    private int totalFrames = 1;     // Numero totale di fotogrammi per l'azione attuale
    private int animationTick = 0;   // Contatore per la frequenza di aggiornamento
    private int animationSpeed = 10; // Velocità dell'animazione (tick per frame)

    public Player(int startX, int startY) {
        this.xCoordinate = startX;
        this.yCoordinate = startY;
    }

    /**
     * Aggiorna lo stato dell'animazione.
     * Viene chiamata ad ogni tick del Game Loop dal ControllerForModel.
     * Utilizza l'operatore modulo (%) per un loop dei frame estremamente efficiente.
     */
    public void updateAnimation() {
        animationTick++;
        if (animationTick >= animationSpeed) {
            animationTick = 0;
            // Calcolo ciclico: se frameIndex + 1 == totalFrames, torna a 0.
            frameIndex = (frameIndex + 1) % totalFrames;
        }
    }

    /**
     * Imposta una nuova azione per il Player.
     * @param newAction La stringa che identifica l'animazione (es. "PLAYER_WALK").
     * @param framesCount Il numero di fotogrammi di cui è composta l'azione.
     */
    public void setAction(String newAction, int framesCount) {
        // Cambiamo azione solo se è effettivamente diversa per evitare reset inutili
        if (!this.currentAction.equals(newAction)) {
            this.currentAction = newAction;
            this.totalFrames = (framesCount > 0) ? framesCount : 1;
            this.frameIndex = 0;    // Reset dell'animazione
            this.animationTick = 0; // Reset del contatore tempo
        }
    }

    // --- GETTER (Utilizzati dalla View per il disegno) ---

    public int getXCoordinate() { return xCoordinate; }
    public int getYCoordinate() { return yCoordinate; }
    public String getCurrentAction() { return currentAction; }
    public int getFrameIndex() { return frameIndex; }

    // --- SETTER E LOGICA DI MOVIMENTO (Utilizzati dal Controller) ---

    public void setXCoordinate(int x) { this.xCoordinate = x; }
    public void setYCoordinate(int y) { this.yCoordinate = y; }
    public void setDelta(int dx, int dy) { this.deltaX = dx; this.deltaY = dy; }
    public int getDeltaX() { return deltaX; }
    public int getDeltaY() { return deltaY; }

    /**
     * Permette di regolare la velocità dell'animazione dinamicamente.
     */
    public void setAnimationSpeed(int speed) {
        this.animationSpeed = speed;
    }
}
