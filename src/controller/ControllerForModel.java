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
        // Usiamo i nano-secondi per una precisione assoluta
        long lastTime = System.nanoTime();
        double amountOfTicks = Config.FPS;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            // --- IL VERO TAPPO ANTI-SCATTO ---
            // Se c'è un lag di sistema (es. Garbage Collector), il delta si accumula.
            // Lo limitiamo a 2. Questo impedisce matematicamente alle entità
            // di fare "scatti in avanti" a velocità moltiplicata!
            if (delta > 2) {
                delta = 2;
            }

            boolean shouldRender = false;

            // Aggiorna la logica ESATTAMENTE il numero di volte necessario
            while (delta >= 1) {
                updateGame();
                delta--;
                shouldRender = true;
            }

            // Disegna a schermo solo se c'è stato un aggiornamento logico
            if (shouldRender) {
                ControllerForView.getInstance().requestRepaint();
            } else {
                // Se ha finito in anticipo, fa riposare la CPU per 1 millisecondo
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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