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
    private static final int WIN_SCORE = 1000;
    private static final int BLOCK_SCORE = 800;
    private static final int THREE_IN_ROW_SCORE = 100;
    private static final int TWO_IN_ROW_SCORE = 10;

    public Connect4Decider(Model model, Controller control) {
        super(model, control);
    }

    private int evaluateColumn(Connect4Board board, int col, int playerId) {
        if (board.isColumnFull(col)) return Integer.MIN_VALUE;
        
        int row = board.getFirstEmptyRow(col);
        int score = 0;
        
        // Simuler le coup en modifiant directement la grille
        int[][] grid = board.getGrid();
        int playerColor = (playerId == 0) ? Pawn.PAWN_BLACK : Pawn.PAWN_RED;
        grid[row][col] = playerColor;
        
        // Vérifier si c'est un coup gagnant
        if (board.checkWin(row, col, playerColor)) {
            score += WIN_SCORE;
        }
        
        // Vérifier si ça bloque un coup gagnant de l'adversaire
        int opponentColor = (playerId == 0) ? Pawn.PAWN_RED : Pawn.PAWN_BLACK;
        grid[row][col] = opponentColor;
        if (board.checkWin(row, col, opponentColor)) {
            score += BLOCK_SCORE;
        }
        
        // Évaluer les séquences de 3 et 2 pions
        score += evaluateSequences(board, row, col, playerColor);
        
        // Pénaliser les colonnes qui donnent des opportunités à l'adversaire
        score -= evaluateOpponentOpportunities(board, row, col, playerColor);
        
        // Restaurer la grille
        grid[row][col] = -1;
        
        return score;
    }
    
    private int evaluateSequences(Connect4Board board, int row, int col, int playerColor) {
        int score = 0;
        int[][] directions = {{1,0}, {0,1}, {1,1}, {1,-1}}; // horizontal, vertical, diagonales
        int[][] grid = board.getGrid();
        
        for (int[] dir : directions) {
            int count = 1; // compte le pion qu'on vient de placer
            int empty = 0;
            
            // Vérifier dans les deux directions
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
        
        // Simuler le coup de l'adversaire
        grid[row][col] = opponentColor;
        
        // Vérifier si ça crée des opportunités pour l'adversaire
        score += evaluateSequences(board, row, col, opponentColor);
        
        // Vérifier si ça permet à l'adversaire de gagner au prochain coup
        for (int c = 0; c < board.getNbCols(); c++) {
            if (!board.isColumnFull(c)) {
                int r = board.getFirstEmptyRow(c);
                grid[r][c] = opponentColor;
                if (board.checkWin(r, c, opponentColor)) {
                    score += BLOCK_SCORE;
                }
                grid[r][c] = -1;
            }
        }
        
        // Restaurer la grille
        grid[row][col] = -1;
        return score;
    }

    @Override
    public ActionList decide() {
        Connect4StageModel stage = (Connect4StageModel)model.getGameStage();
        Connect4Board board = stage.getBoard();
        Connect4PawnPot pot = null;
        GameElement pawn = null;
        int rowDest = 0;
        int colDest = 0;
        
        if (model.getIdPlayer() == 0) {
            pot = stage.getYellowPot();
        } else {
            pot = stage.getRedPot();
        }

        // Évaluer chaque colonne possible
        int bestScore = Integer.MIN_VALUE;
        int bestCol = -1;
        
        for (int col = 0; col < board.getNbCols(); col++) {
            if (!board.isColumnFull(col)) {
                int score = evaluateColumn(board, col, model.getIdPlayer());
                if (score > bestScore) {
                    bestScore = score;
                    bestCol = col;
                }
            }
        }
        
        // Si aucune colonne n'est valide, retourner null
        if (bestCol == -1) return null;
        
        // Trouver la première ligne vide dans la meilleure colonne
        int row = board.getFirstEmptyRow(bestCol);
        
        // Trouver un pion disponible dans le pot
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
}
