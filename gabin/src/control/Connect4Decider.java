package control;

import boardifier.control.ActionFactory;
import boardifier.control.Controller;
import boardifier.control.Decider;
import boardifier.model.GameElement;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import model.Connect4Board;
import model.Connect4StageModel;
import model.Connect4PawnPot;
import model.Pawn;

import java.util.Calendar;
import java.util.Random;

public class Connect4Decider extends Decider {
    private static final Random random = new Random(Calendar.getInstance().getTimeInMillis());
    private static final int WIN_SCORE = 10000;
    private static final int BLOCK_SCORE = 8000;
    private static final int THREE_IN_ROW_SCORE = 1000;
    private static final int TWO_IN_ROW_SCORE = 700;
    private static final int CENTER_COLUMN_BONUS = 100;
    private static final int THREAT_SCORE = 2000;
    private static final int DEPTH = 3;

    public Connect4Decider(Model model, Controller control) {
        super(model, control);
    }

    private int evaluateColumn(Connect4Board board, int col, int playerId) {
        if (board.isColumnFull(col)) return Integer.MIN_VALUE;
        
        int row = board.getFirstEmptyRow(col);
        int score = 0;
        
        int centerCol = board.getNbCols() / 2;
        int distanceFromCenter = Math.abs(col - centerCol);
        score += (board.getNbCols() / 2 - distanceFromCenter) * CENTER_COLUMN_BONUS;
        
        int[][] grid = board.getGrid();
        int playerColor = (playerId == 0) ? Pawn.PAWN_BLACK : Pawn.PAWN_RED;
        grid[row][col] = playerColor;
        
        if (board.checkWin(row, col, playerColor)) {
            score += WIN_SCORE;
        }
        
        int opponentColor = (playerId == 0) ? Pawn.PAWN_RED : Pawn.PAWN_BLACK;
        grid[row][col] = opponentColor;
        if (board.checkWin(row, col, opponentColor)) {
            score += BLOCK_SCORE;
        }

        score += evaluateSequences(board, row, col, playerColor);
        score += evaluateThreats(board, row, col, playerColor);

        score -= evaluateOpponentOpportunities(board, row, col, playerColor);

        grid[row][col] = -1;

        return score;
    }

    private int evaluateSequences(Connect4Board board, int row, int col, int playerColor) {
        int score = 0;
        int[][] directions = {{1,0}, {0,1}, {1,1}, {1,-1}};
        int[][] grid = board.getGrid();

        for (int[] dir : directions) {
            int count = 1;
            int empty = 0;

            for (int direction = -1; direction <= 1; direction += 2) {
                for (int i = 1; i <= 3; i++) {
                    int newRow = row + direction * i * dir[0];
                    int newCol = col + direction * i * dir[1];

                    if (newRow >= 0 && newRow < board.getNbRows() &&
                        newCol >= 0 && newCol < board.getNbCols()) {
                        if (grid[newRow][newCol] == playerColor) {
                            count++;
                        } else if (grid[newRow][newCol] == -1) {
                            empty++;
                        }
                    }
                }
            }

            if (count == 3 && empty >= 1) score += THREE_IN_ROW_SCORE;
            if (count == 2 && empty >= 2) score += TWO_IN_ROW_SCORE;
        }

        return score;
    }

    private int evaluateOpponentOpportunities(Connect4Board board, int row, int col, int playerColor) {
        int score = 0;
        int opponentColor = (playerColor == Pawn.PAWN_BLACK) ? Pawn.PAWN_RED : Pawn.PAWN_BLACK;
        int[][] grid = board.getGrid();

        grid[row][col] = opponentColor;

        score += evaluateSequences(board, row, col, opponentColor);

        for (int c = 0; c < board.getNbCols(); c++) {
            if (!board.isColumnFull(c)) {
                int r = board.getFirstEmptyRow(c);
                grid[r][c] = opponentColor;
                if (board.checkWin(r, c, opponentColor)) {
                    score += BLOCK_SCORE + 2000;
                }

                grid[r][c] = -1;
            }
        }

        grid[row][col] = -1;
        return score;
    }

    private int evaluateThreats(Connect4Board board, int row, int col, int playerColor) {
        int score = 0;
        int[][] grid = board.getGrid();
        
        for (int c = Math.max(0, col - 3); c <= Math.min(board.getNbCols() - 1, col + 3); c++) {
            if (c == col) continue;
            int r = board.getFirstEmptyRow(c);
            if (r != -1) {
                grid[r][c] = playerColor;
                if (board.checkWin(r, c, playerColor)) {
                    score += THREAT_SCORE;
                }
                grid[r][c] = -1;
            }
        }
        
        if (row > 0) {
            grid[row-1][col] = playerColor;
            if (board.checkWin(row-1, col, playerColor)) {
                score += THREAT_SCORE;
            }
            grid[row-1][col] = -1;
        }
        
        int[][] directions = {{1,1}, {1,-1}};
        for (int[] dir : directions) {
            for (int i = -3; i <= 3; i++) {
                if (i == 0) continue;
                int newRow = row + i * dir[0];
                int newCol = col + i * dir[1];
                if (newRow >= 0 && newRow < board.getNbRows() && 
                    newCol >= 0 && newCol < board.getNbCols()) {
                    int r = board.getFirstEmptyRow(newCol);
                    if (r != -1) {
                        grid[r][newCol] = playerColor;
                        if (board.checkWin(r, newCol, playerColor)) {
                            score += THREAT_SCORE;
                        }
                        grid[r][newCol] = -1;
                    }
                }
            }
        }
        
        return score;
    }

    @Override
    public ActionList decide() {
        Connect4StageModel stage = (Connect4StageModel) model.getGameStage();
        Connect4Board board = stage.getBoard();
        Connect4PawnPot pot = (model.getIdPlayer() == 0) ? stage.getYellowPot() : stage.getRedPot();

        GameElement pawn = null;
        int rowDest = 0;
        int colDest = 0;
        int bestCol = -1;

        int playerColor = (model.getIdPlayer() == 0) ? Pawn.PAWN_BLACK : Pawn.PAWN_RED;
        int opponentColor = (playerColor == Pawn.PAWN_BLACK) ? Pawn.PAWN_RED : Pawn.PAWN_BLACK;

        // 1. Vérifier si l'IA peut gagner immédiatement
        for (int col = 0; col < board.getNbCols(); col++) {
            if (!board.isColumnFull(col)) {
                int row = board.getFirstEmptyRow(col);
                board.getGrid()[row][col] = playerColor;
                if (board.checkWin(row, col, playerColor)) {
                    board.getGrid()[row][col] = -1;
                    bestCol = col;
                    break;
                }
                board.getGrid()[row][col] = -1;
            }
        }

        // 2. Sinon, vérifier si l'adversaire peut gagner au prochain coup, et le bloquer
        if (bestCol == -1) {
            for (int col = 0; col < board.getNbCols(); col++) {
                if (!board.isColumnFull(col)) {
                    int row = board.getFirstEmptyRow(col);
                    board.getGrid()[row][col] = opponentColor;
                    if (board.checkWin(row, col, opponentColor)) {
                        board.getGrid()[row][col] = -1;
                        bestCol = col;
                        break;
                    }
                    board.getGrid()[row][col] = -1;
                }
            }
        }

        // 3. Sinon, évaluer les colonnes avec l'heuristique
        if (bestCol == -1) {
            int bestScore = Integer.MIN_VALUE;
            for (int col = 0; col < board.getNbCols(); col++) {
                if (!board.isColumnFull(col)) {
                    int score = evaluateColumnWithDepth(board, col, model.getIdPlayer(), DEPTH);
                    if (score > bestScore) {
                        bestScore = score;
                        bestCol = col;
                    }
                }
            }
        }

        // Si aucun coup trouvé (très rare sauf si grille pleine), retourner null
        if (bestCol == -1) return null;

        // Trouver la première pièce disponible dans le pot du joueur
        int row = board.getFirstEmptyRow(bestCol);
        for (int i = 0; i < pot.getNbRows(); i++) {
            if (!pot.isEmptyAt(0, i)) {
                pawn = pot.getElement(0, i);
                rowDest = row;
                colDest = bestCol;
                break;
            }
        }

        if (pawn == null) return null;

        ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "connect4board", rowDest, colDest);
        actions.setDoEndOfTurn(true);
        return actions;
    }


    private int evaluateColumnWithDepth(Connect4Board board, int col, int playerId, int depth) {
        if (depth == 0) {
            return evaluateColumn(board, col, playerId);
        }
        
        int row = board.getFirstEmptyRow(col);
        if (row == -1) return Integer.MIN_VALUE;
        
        int[][] grid = board.getGrid();
        int playerColor = (playerId == 0) ? Pawn.PAWN_BLACK : Pawn.PAWN_RED;
        
        grid[row][col] = playerColor;
        
        if (board.checkWin(row, col, playerColor)) {
            grid[row][col] = -1;
            return WIN_SCORE;
        }
        
        int opponentId = (playerId + 1) % 2;
        int bestOpponentScore = Integer.MIN_VALUE;
        
        for (int c = 0; c < board.getNbCols(); c++) {
            if (!board.isColumnFull(c)) {
                int score = evaluateColumnWithDepth(board, c, opponentId, depth - 1);
                bestOpponentScore = Math.max(bestOpponentScore, score);
            }
        }
        
        grid[row][col] = -1;
        
        return evaluateColumn(board, col, playerId) - bestOpponentScore / 2;
    }
}
