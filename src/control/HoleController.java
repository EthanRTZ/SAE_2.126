package control;

import boardifier.control.ActionFactory;
import boardifier.control.ActionPlayer;
import boardifier.control.Controller;
import boardifier.model.GameElement;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import model.HoleBoard;
import model.HolePawnPot;
import model.HoleStageModel;
import model.Pawn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HoleController extends Controller {
    private BufferedReader consoleIn;
    private boolean firstPlayer;

    public HoleController(Model model, boardifier.view.View view) {
        super(model, view);
        firstPlayer = true;
        consoleIn = new BufferedReader(new InputStreamReader(System.in));
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
        HoleStageModel gameStage = (HoleStageModel) model.getGameStage();
        HoleBoard board = gameStage.getBoard();
        HolePawnPot pot = null;
        if (model.getIdPlayer() == 0) {
            pot = gameStage.getBlackPot();
        } else {
            pot = gameStage.getRedPot();
        }

        // Demander la colonne
        int col = -1;
        do {
            System.out.print("Entrez le numéro de la colonne (1-7) : ");
            try {
                col = Integer.parseInt(consoleIn.readLine()) - 1;
                if (col < 0 || col >= HoleBoard.getCols()) {
                    System.out.println("Colonne invalide !");
                    col = -1;
                } else if (board.isColumnFull(col)) {
                    System.out.println("Cette colonne est pleine !");
                    col = -1;
                }
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre valide !");
            } catch (IOException e) {
                System.out.println("Erreur de lecture !");
            }
        } while (col == -1);

        // Trouver la première ligne vide dans la colonne
        int row = board.getFirstEmptyRow(col);

        // Trouver un pion disponible
        GameElement pawn = null;
        for (int i = 0; i < 4; i++) {
            if (!pot.isEmptyAt(i, 0)) {
                pawn = pot.getElement(i, 0);
                break;
            }
        }

        if (pawn != null) {
            // Placer le pion
            ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "holeboard", row, col);
            actions.setDoEndOfTurn(true);
            ActionPlayer play = new ActionPlayer(model, this, actions);
            play.start();
        }
    }

    public void endGame() {
        HoleStageModel stageModel = (HoleStageModel) model.getGameStage();
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
