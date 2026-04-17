package controller;

import model.Model;
import utils.Config;

// Aggiungiamo Runnable per permettere a questa classe di girare su un Thread dedicato
public class ControllerForModel implements IControllerForModel, Runnable {

    //---------------------------------------------------------------
    // STATIC FIELDS
    //---------------------------------------------------------------
    private static ControllerForModel instance = null;

    //---------------------------------------------------------------
    // INSTANCE ATTRIBUTES
    //---------------------------------------------------------------
    private Thread gameThread;
    private boolean running = false;
    private volatile boolean paused = false;
    private int transitionTimer = 0;
    private final int MAX_TRANSITION_TICKS = Config.MAX_TRANSITION_TICKS; // Circa 2 second

    /** Returns true when the game logic is frozen (pause screen active). */
    public boolean isPaused() { return paused; }
    /** Freeze or unfreeze game logic. View continues to repaint for the pause overlay. */
    public void setPaused(boolean paused) { this.paused = paused; }


    private ControllerForModel() {
        // Non usiamo più il Timer di Swing!
    }

    // Implementazione di IControllerForModel.updateGame()
    // Implementazione di IControllerForModel.updateGame()
    @Override
    public void updateGame() {
        // --- 1. GESTIONE TRANSIZIONE (GIOCO IN PAUSA LOGICA) ---
        if (Model.getInstance().isTransitioning()) {
            this.transitionTimer--;

            // A metà tempo esatto (schermo tutto nero), carichiamo la nuova mappa
            if (this.transitionTimer == MAX_TRANSITION_TICKS / 2) {
                System.out.println("Controller: Generazione nuova mappa durante la transizione...");
                int[][] nextMap = Model.getInstance().generateProceduralMap();
                Model.getInstance().prepareNextLevel(nextMap);
            }

            // Quando il timer scade, sblocchiamo il gioco
            if (this.transitionTimer <= 0) {
                Model.getInstance().setTransitioning(false);
            }

            return; // IMPORTANTE: Esce dal metodo, ferma il tempo di gioco!
        }

        // --- 2. NORMALE AGGIORNAMENTO DEL GIOCO ---
        Model.getInstance().updateGameLogic();

        // --- 3. IL REGISTA INTERCETTA IL CAMBIO LIVELLO TRAMITE IL GATE ---
        // Se il model ci dice che il livello è finito, NON generiamo subito la mappa, ma facciamo partire l'animazione!
        if (Model.getInstance().isLevelCompletedFlag()) {
            System.out.println("Controller: Gate attraversato! Avvio animazione transizione...");
            onLevelCompleted();
        }
    }

    // ... Qui sotto continua il tuo normale codice di update (movimenti, collisioni, ecc.) ...

    public void onLevelCompleted() {
        if (!Model.getInstance().isTransitioning()) {
            Model.getInstance().setTransitioning(true);
            this.transitionTimer = MAX_TRANSITION_TICKS;
        }
    }

    // Implementazione di IControllerForModel.startGameLoop()
    @Override
    public void startGameLoop() {
        if (!running) {
            running = true;
            gameThread = new Thread(this); // Crea un thread separato dalla grafica
            gameThread.start();            // Avvia il metodo run()
            System.out.println("Game Loop (Thread Professionale) avviato a " + Config.FPS + " FPS.");
        }
    }

    // IL VERO CUORE DEL GIOCO: Il Game Loop perfetto
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000.0 / Config.FPS;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            // Tappo anti-lag: impedisce accelerazioni innaturali in caso di blocchi di sistema
            if (delta > 3) {
                delta = 1;
            }

            boolean updated = false;

            // 1. FASE LOGICA: Aggiorna il gioco (es. movimento, collisioni)
            // Se il gioco è in pausa, saltiamo updateGame() ma facciamo comunque il repaint.
            while (delta >= 1) {
                if (!paused) updateGame();
                delta--;
                updated = true;
            }

            // 2. FASE GRAFICA: Disegna a schermo
            if (updated) {
                ControllerForView.getInstance().requestRepaint();
            }

            // 3. FASE DI SINCRONIZZAZIONE (Il segreto della fluidità universale)
            // Invece di usare il problematico Thread.sleep(), continuiamo a controllare
            // l'orologio ad altissima precisione (nanoTime) finché non è il momento
            // esatto di far partire il frame successivo.
            // Thread.yield() evita che il processore si surriscaldi durante l'attesa.
            while (System.nanoTime() - lastTime < nsPerTick) {
                Thread.yield();
            }
        }
    }

    //---------------------------------------------------------------
    // STATIC METHODS
    //---------------------------------------------------------------
    public static IControllerForModel getInstance() {
        if (instance == null)
            instance = new ControllerForModel();
        return instance;
    }
}