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
        // Get the board and current player
        PuissanceXBoard board = stage.getBoard();
        
        // Check if the board is initialized
        if (board == null) {
            System.out.println("The board is not yet initialized, waiting...");
            try {
                Thread.sleep(1000); // Wait 1 second
                board = stage.getBoard();
                if (board == null) {
                    System.out.println("The board is still null after waiting");
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
        
        // 1. Check if the AI can win immediately
        for (int col = 0; col < board.getNbCols(); col++) {
            if (!board.isColumnFull(col)) {
                int row = board.getFirstEmptyRow(col);
                board.getGrid()[row][col] = color;
                if (board.checkWin(row, col, color)) {
                    // Remove a pawn from the pot
                    PuissanceXPawnPot pot = color == Pawn.PAWN_RED ? stage.getRedPot() : stage.getYellowPot();
                    pot.removeElement(null);
                    return new ActionList();
                }
                board.getGrid()[row][col] = -1;
            }
        }
        
        // 2. Check if the opponent can win on the next move and block them
        for (int col = 0; col < board.getNbCols(); col++) {
            if (!board.isColumnFull(col)) {
                int row = board.getFirstEmptyRow(col);
                board.getGrid()[row][col] = opponentColor;
                if (board.checkWin(row, col, opponentColor)) {
                    board.getGrid()[row][col] = color;
                    // Remove a pawn from the pot
                    PuissanceXPawnPot pot = color == Pawn.PAWN_RED ? stage.getRedPot() : stage.getYellowPot();
                    pot.removeElement(null);
                    return new ActionList();
                }
                board.getGrid()[row][col] = -1;
            }
        }
        
        // 3. If no strategic move is found, play randomly
        int col;
        do {
            col = (int) (Math.random() * board.getNbCols());
        } while (board.isColumnFull(col));
        
        int row = board.getFirstEmptyRow(col);
        board.getGrid()[row][col] = color;
        
        // Remove a pawn from the pot
        PuissanceXPawnPot pot = color == Pawn.PAWN_RED ? stage.getRedPot() : stage.getYellowPot();
        pot.removeElement(null);
        
        return new ActionList();
    }
} 
