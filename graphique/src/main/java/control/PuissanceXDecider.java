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

public class PuissanceXDecider extends Decider {
    private static final Random random = new Random(Calendar.getInstance().getTimeInMillis());
    private static final int WIN_SCORE = 100000;
    private static final int BLOCK_SCORE = 50000;
    private static final int THREE_IN_ROW_SCORE = 1000;
    private static final int TWO_IN_ROW_SCORE = 100;
    private static final int CENTER_COLUMN_BONUS = 50;
    private static final int DEPTH = 4;
    private PuissanceXStageModel stage;

    public PuissanceXDecider(Model model, Controller control) {
        super(model, control);
    }

    public void setStage(PuissanceXStageModel stage) {
        this.stage = stage;
    }

    @Override
    public ActionList decide() {
        PuissanceXBoard board = stage.getBoard();
        int currentPlayer = model.getIdPlayer();
        int color = currentPlayer == 0 ? Pawn.PAWN_RED : Pawn.PAWN_YELLOW;
        
        // Vérifier d'abord les coups gagnants directs
        for (int col = 0; col < board.getNbCols(); col++) {
            if (!board.isColumnFull(col)) {
                int row = board.getFirstEmptyRow(col);
                board.getGrid()[row][col] = color;
                if (board.checkWin(row, col, color)) {
                    board.getGrid()[row][col] = -1;
                    return playMove(board, col, color);
                }
                board.getGrid()[row][col] = -1;
            }
        }
        
        // Vérifier les coups qui bloquent une victoire adverse
        int opponentColor = color == Pawn.PAWN_RED ? Pawn.PAWN_YELLOW : Pawn.PAWN_RED;
        for (int col = 0; col < board.getNbCols(); col++) {
            if (!board.isColumnFull(col)) {
                int row = board.getFirstEmptyRow(col);
                board.getGrid()[row][col] = opponentColor;
                if (board.checkWin(row, col, opponentColor)) {
                    board.getGrid()[row][col] = -1;
                    return playMove(board, col, color);
                }
                board.getGrid()[row][col] = -1;
            }
        }
        
        // Utiliser l'algorithme minimax pour trouver le meilleur coup
        int bestScore = Integer.MIN_VALUE;
        int bestCol = -1;
        
        for (int col = 0; col < board.getNbCols(); col++) {
            if (!board.isColumnFull(col)) {
                int row = board.getFirstEmptyRow(col);
                board.getGrid()[row][col] = color;
                int score = minimax(board, DEPTH, false, color);
                board.getGrid()[row][col] = -1;
                
                if (score > bestScore) {
                    bestScore = score;
                    bestCol = col;
                }
            }
        }
        
        // Si aucun coup n'est trouvé, jouer au centre ou aléatoirement
        if (bestCol == -1) {
            int centerCol = board.getNbCols() / 2;
            if (!board.isColumnFull(centerCol)) {
                bestCol = centerCol;
            } else {
                do {
                    bestCol = random.nextInt(board.getNbCols());
                } while (board.isColumnFull(bestCol));
            }
        }
        
        return playMove(board, bestCol, color);
    }

    private ActionList playMove(PuissanceXBoard board, int col, int color) {
        int row = board.getFirstEmptyRow(col);
        board.getGrid()[row][col] = color;
        
        PuissanceXPawnPot pot = color == Pawn.PAWN_RED ? stage.getRedPot() : stage.getYellowPot();
        pot.removeElement(null);
        
        return new ActionList();
    }

    private int minimax(PuissanceXBoard board, int depth, boolean isMaximizing, int playerColor) {
        int opponentColor = playerColor == Pawn.PAWN_RED ? Pawn.PAWN_YELLOW : Pawn.PAWN_RED;
        
        // Vérifier les conditions de fin de jeu
        if (depth == 0) {
            return evaluateBoard(board, playerColor);
        }
        
        if (isMaximizing) {
            int maxScore = Integer.MIN_VALUE;
            for (int col = 0; col < board.getNbCols(); col++) {
                if (!board.isColumnFull(col)) {
                    int row = board.getFirstEmptyRow(col);
                    board.getGrid()[row][col] = playerColor;
                    
                    // Vérifier la victoire immédiate
                    if (board.checkWin(row, col, playerColor)) {
                        board.getGrid()[row][col] = -1;
                        return WIN_SCORE;
                    }
                    
                    int score = minimax(board, depth - 1, false, playerColor);
                    board.getGrid()[row][col] = -1;
                    maxScore = Math.max(maxScore, score);
                }
            }
            return maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (int col = 0; col < board.getNbCols(); col++) {
                if (!board.isColumnFull(col)) {
                    int row = board.getFirstEmptyRow(col);
                    board.getGrid()[row][col] = opponentColor;
                    
                    // Vérifier la défaite immédiate
                    if (board.checkWin(row, col, opponentColor)) {
                        board.getGrid()[row][col] = -1;
                        return -WIN_SCORE;
                    }
                    
                    int score = minimax(board, depth - 1, true, playerColor);
                    board.getGrid()[row][col] = -1;
                    minScore = Math.min(minScore, score);
                }
            }
            return minScore;
        }
    }

    private int evaluateBoard(PuissanceXBoard board, int playerColor) {
        int score = 0;
        int opponentColor = playerColor == Pawn.PAWN_RED ? Pawn.PAWN_YELLOW : Pawn.PAWN_RED;
        int[][] grid = board.getGrid();
        
        // Évaluer les séquences horizontales
        for (int row = 0; row < board.getNbRows(); row++) {
            for (int col = 0; col < board.getNbCols() - 3; col++) {
                score += evaluateSequence(grid, row, col, 0, 1, playerColor, opponentColor);
            }
        }
        
        // Évaluer les séquences verticales
        for (int row = 0; row < board.getNbRows() - 3; row++) {
            for (int col = 0; col < board.getNbCols(); col++) {
                score += evaluateSequence(grid, row, col, 1, 0, playerColor, opponentColor);
            }
        }
        
        // Évaluer les séquences diagonales
        for (int row = 0; row < board.getNbRows() - 3; row++) {
            for (int col = 0; col < board.getNbCols() - 3; col++) {
                score += evaluateSequence(grid, row, col, 1, 1, playerColor, opponentColor);
                score += evaluateSequence(grid, row, col + 3, 1, -1, playerColor, opponentColor);
            }
        }
        
        // Bonus pour le centre
        int centerCol = board.getNbCols() / 2;
        for (int row = 0; row < board.getNbRows(); row++) {
            if (grid[row][centerCol] == playerColor) {
                score += CENTER_COLUMN_BONUS;
            } else if (grid[row][centerCol] == opponentColor) {
                score -= CENTER_COLUMN_BONUS;
            }
        }
        
        return score;
    }

    private int evaluateSequence(int[][] grid, int startRow, int startCol, int deltaRow, int deltaCol, int playerColor, int opponentColor) {
        int playerCount = 0;
        int opponentCount = 0;
        int emptyCount = 0;
        
        for (int i = 0; i < 4; i++) {
            int row = startRow + i * deltaRow;
            int col = startCol + i * deltaCol;
            
            if (grid[row][col] == playerColor) {
                playerCount++;
            } else if (grid[row][col] == opponentColor) {
                opponentCount++;
            } else {
                emptyCount++;
            }
        }
        
        if (playerCount == 3 && emptyCount == 1) return THREE_IN_ROW_SCORE;
        if (playerCount == 2 && emptyCount == 2) return TWO_IN_ROW_SCORE;
        if (opponentCount == 3 && emptyCount == 1) return -THREE_IN_ROW_SCORE;
        if (opponentCount == 2 && emptyCount == 2) return -TWO_IN_ROW_SCORE;
        
        return 0;
    }
}
