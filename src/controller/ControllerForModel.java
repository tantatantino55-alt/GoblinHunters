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

    private ControllerForModel() {
        // Non usiamo più il Timer di Swing!
    }

    // Implementazione di IControllerForModel.updateGame()
    @Override
    public void updateGame() {
        Model.getInstance().updateGameLogic();
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
            while (delta >= 1) {
                updateGame();
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