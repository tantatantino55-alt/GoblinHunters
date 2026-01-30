package controller;

import model.Model;
import utils.Config;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerForModel implements IControllerForModel {

    //---------------------------------------------------------------
    // STATIC FIELDS
    //---------------------------------------------------------------
    private static ControllerForModel instance = null;

    //---------------------------------------------------------------
    // INSTANCE ATTRIBUTES
    //---------------------------------------------------------------
    private final Timer gameTimer;

    private ControllerForModel() {
        // Inizializza il Timer con il ritardo calcolato in Config (es. 16ms per 60 FPS)
        this.gameTimer = new Timer(Config.GAME_LOOP_DELAY_MS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1. Aggiornamento Logica (Model Update)
                updateGame();
                // 2. Richiesta di Disegno (View Repaint)
                ControllerForView.getInstance().requestRepaint();
            }
        });
        // Il timer non si avvia finché non è chiamato startGameLoop()
    }


    // Implementazione di IControllerForModel.updateGame()
    @Override
    public void updateGame() {
        Model.getInstance().updatePlayerMovement();
        // Qui avverrà la chiamata al Model per avanzare la simulazione
        // Esempio:
        // IModel.getInstance().updateEntities();
        // IModel.getInstance().checkCollisions();
        // IModel.getInstance().spawnEnemies();

        // Per ora, possiamo solo chiamare il Model per avanzare il tempo, se necessario.
    }

    // Implementazione di IControllerForModel.startGameLoop()
    @Override
    public void startGameLoop() {
        if (!gameTimer.isRunning()) {
            gameTimer.start();
            System.out.println("Game Loop avviato a " + Config.FPS + " FPS.");
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
