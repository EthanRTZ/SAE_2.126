package control;

import boardifier.control.Controller;
import boardifier.control.Decider;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import java.util.Calendar;
import java.util.Random;
import model.Pawn;
import model.PuissanceXBoard;
import model.PuissanceXPawnPot;
import model.PuissanceXStageModel;

public class PuissanceXSmartDecider extends Decider {
    private static final Random random = new Random(Calendar.getInstance().getTimeInMillis());
    private PuissanceXStageModel stage;

    public PuissanceXSmartDecider(Model model, Controller control) {
        super(model, control);
    }

    public void setStage(PuissanceXStageModel stage) {
        this.stage = stage;
    }

    @Override
    public ActionList decide() {
        // Obtenir le plateau et le joueur actuel
        PuissanceXBoard board = stage.getBoard();
        
        // Vérifier si le plateau est initialisé
        if (board == null) {
            System.out.println("Le plateau n'est pas encore initialisé, on attend...");
            try {
                Thread.sleep(1000); // Attendre 1 seconde
                board = stage.getBoard();
                if (board == null) {
                    System.out.println("Le plateau est toujours null après l'attente");
                    return null;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
        
        int currentPlayer = model.getIdPlayer();
        int color = currentPlayer == 0 ? Pawn.PAWN_RED : Pawn.PAWN_YELLOW;
        int opponentColor = color == Pawn.PAWN_RED ? Pawn.PAWN_YELLOW : Pawn.PAWN_RED;
        
        // 1. Vérifier si l'IA peut gagner immédiatement
        for (int col = 0; col < board.getNbCols(); col++) {
            if (!board.isColumnFull(col)) {
                int row = board.getFirstEmptyRow(col);
                board.getGrid()[row][col] = color;
                if (board.checkWin(row, col, color)) {
                    // Retirer un pion du pot
                    PuissanceXPawnPot pot = color == Pawn.PAWN_RED ? stage.getRedPot() : stage.getYellowPot();
                    pot.removeElement(null);
                    return new ActionList();
                }
                board.getGrid()[row][col] = -1;
            }
        }
        
        // 2. Vérifier si l'adversaire peut gagner au prochain coup et le bloquer
        for (int col = 0; col < board.getNbCols(); col++) {
            if (!board.isColumnFull(col)) {
                int row = board.getFirstEmptyRow(col);
                board.getGrid()[row][col] = opponentColor;
                if (board.checkWin(row, col, opponentColor)) {
                    board.getGrid()[row][col] = color;
                    // Retirer un pion du pot
                    PuissanceXPawnPot pot = color == Pawn.PAWN_RED ? stage.getRedPot() : stage.getYellowPot();
                    pot.removeElement(null);
                    return new ActionList();
                }
                board.getGrid()[row][col] = -1;
            }
        }
        
        // 3. Si aucun coup stratégique n'est trouvé, jouer aléatoirement
        int col;
        do {
            col = (int) (Math.random() * board.getNbCols());
        } while (board.isColumnFull(col));
        
        int row = board.getFirstEmptyRow(col);
        board.getGrid()[row][col] = color;
        
        // Retirer un pion du pot
        PuissanceXPawnPot pot = color == Pawn.PAWN_RED ? stage.getRedPot() : stage.getYellowPot();
        pot.removeElement(null);
        
        return new ActionList();
    }
} 
