package control;

import boardifier.control.ActionFactory;
import boardifier.control.ActionPlayer;
import boardifier.control.Controller;
import boardifier.control.StageFactory;
import boardifier.model.GameElement;
import boardifier.model.GameException;
import boardifier.model.Model;
import boardifier.model.Player;
import boardifier.model.action.ActionList;
import boardifier.view.View;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import model.Connect4Board;
import model.Connect4PawnPot;
import model.Connect4StageModel;
import model.Pawn;

public class Connect4Controller extends Controller {
    private BufferedReader consoleIn;
    private boolean firstPlayer;

    public Connect4Controller(Model model, View view) {
        super(model, view);
        firstPlayer = true;
        consoleIn = new BufferedReader(new InputStreamReader(System.in));
        
        // Initialiser la scène de jeu
        try {
            Connect4StageModel stageModel = (Connect4StageModel) StageFactory.createStageModel("main", model);
            model.startGame(stageModel);
        } catch (GameException e) {
            System.out.println("Erreur lors de l'initialisation de la scène : " + e.getMessage());
        }
    }

    public void stageLoop() {
        update();
        while (!model.isEndStage()) {
            playTurn();
            endOfTurn();
            update();
        }
        endGame();
    }

    private void playTurn() {
        Player p = model.getCurrentPlayer();
        if (p.getType() == Player.COMPUTER) {
            System.out.println("L'ORDINATEUR JOUE");
            Connect4Decider decider = new Connect4Decider(model, this);
            ActionPlayer play = new ActionPlayer(model, this, decider, null);
            play.start();
            
            // Ajouter un petit délai pour laisser le temps à l'action de s'exécuter
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            boolean ok = false;
            while (!ok) {
                System.out.print(p.getName() + " > ");
                try {
                    String line = consoleIn.readLine();
                    if (line.length() == 1) {
                        ok = analyseAndPlay(line);
                    }
                    if (!ok) {
                        System.out.println("Instruction incorrecte. Réessayez !");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void endOfTurn() {
        // Ne pas changer le joueur si l'action le fait déjà
        if (!model.isEndStage()) {
            Player p = model.getCurrentPlayer();
            Connect4StageModel stageModel = (Connect4StageModel) model.getGameStage();
            stageModel.getPlayerName().setText(p.getName());
        }
    }

    private boolean analyseAndPlay(String line) {
        Connect4StageModel gameStage = (Connect4StageModel) model.getGameStage();
        Connect4Board board = gameStage.getBoard();
        Connect4PawnPot pot;
        
        // Sélectionner le bon pot selon le joueur courant
        if (model.getIdPlayer() == 0) {
            pot = gameStage.getYellowPot();
        } else {
            pot = gameStage.getRedPot();
        }

        // Convertir le numéro de la colonne en index
        int col;
        try {
            col = Integer.parseInt(line) - 1;
        } catch (NumberFormatException e) {
            return false;
        }
        if ((col < 0) || (col >= board.getNbCols())) return false;

        // Vérifier si la colonne est pleine
        if (board.isColumnFull(col)) return false;

        // Trouver la première ligne vide dans la colonne
        int row = board.getFirstEmptyRow(col);

        // Trouver un pion disponible dans le pot
        GameElement pawn = null;
        for (int i = 0; i < pot.getNbRows(); i++) {
            if (!pot.isEmptyAt(0, i)) {
                pawn = pot.getElement(0, i);
                break;
            }
        }
        
        if (pawn == null) return false;

        // Créer l'action pour placer le pion
        ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "connect4board", row, col);
        actions.setDoEndOfTurn(true);
        
        // Exécuter l'action
        ActionPlayer play = new ActionPlayer(model, this, actions);
        play.start();
        
        return true;
    }

    public void endGame() {
        Connect4StageModel stageModel = (Connect4StageModel) model.getGameStage();
        int winner = stageModel.getWinner();
        if (winner == Pawn.PAWN_BLACK) {
            System.out.println("Joueur 1 a gagné !");
        } else if (winner == Pawn.PAWN_RED) {
            System.out.println("Joueur 2 a gagné !");
        } else {
            System.out.println("Match nul !");
        }
    }
}
