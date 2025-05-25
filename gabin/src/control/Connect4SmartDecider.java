package control;

import boardifier.control.ActionFactory;
import boardifier.control.Controller;
import boardifier.control.Decider;
import boardifier.model.GameElement;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import java.util.Calendar;
import java.util.Random;
import model.Connect4Board;
import model.Connect4PawnPot;
import model.Connect4StageModel;
import model.Pawn;

public class Connect4SmartDecider extends Decider {
    private static final Random random = new Random(Calendar.getInstance().getTimeInMillis());

    public Connect4SmartDecider(Model model, Controller control) {
        super(model, control);
    }

    @Override
    public ActionList decide() {
        // Get game elements
        Connect4StageModel stage = (Connect4StageModel)model.getGameStage();
        Connect4Board board = stage.getBoard();
        Connect4PawnPot pot = null;
        GameElement pawn = null;
        int rowDest = 0;
        int colDest = 0;
        
        // Choose the right pawn pot based on the player
        if (model.getIdPlayer() == 0) {
            pot = stage.getYellowPot();
        } else {
            pot = stage.getRedPot();
        }

        // STEP 1: Check if we can win
        boolean moveFound = false;
        for (int col = 0; col < board.getNbCols(); col++) {
            // Check if the column is not full
            if (!board.isColumnFull(col)) {
                // Find the first empty row in this column
                int row = board.getFirstEmptyRow(col);
                
                // Simulate the move
                int[][] grid = board.getGrid();
                int playerColor;
                if (model.getIdPlayer() == 0) {
                    playerColor = Pawn.PAWN_BLACK;
                } else {
                    playerColor = Pawn.PAWN_RED;
                }
                grid[row][col] = playerColor;
                
                // Check if this move wins
                if (board.checkWin(row, col, playerColor)) {
                    rowDest = row;
                    colDest = col;
                    moveFound = true;
                    break;
                }
                
                // Undo simulated move
                grid[row][col] = -1;
            }
        }

        // STEP 2: If we can't win, check if opponent can win
        if (!moveFound) {
            int opponentColor;
            if (model.getIdPlayer() == 0) {
                opponentColor = Pawn.PAWN_RED;
            } else {
                opponentColor = Pawn.PAWN_BLACK;
            }
            
            for (int col = 0; col < board.getNbCols(); col++) {
                if (!board.isColumnFull(col)) {
                    int row = board.getFirstEmptyRow(col);
                    
                    // Simulate opponent's move
                    int[][] grid = board.getGrid();
                    grid[row][col] = opponentColor;
                    
                    // Check if opponent can win
                    if (board.checkWin(row, col, opponentColor)) {
                        rowDest = row;
                        colDest = col;
                        moveFound = true;
                        break;
                    }
                    
                    // Undo simulated move
                    grid[row][col] = -1;
                }
            }
        }

        // STEP 3: If no strategic move is found, play randomly
        if (!moveFound) {
            int col;
            do {
                col = random.nextInt(board.getNbCols());
            } while (board.isColumnFull(col));
            rowDest = board.getFirstEmptyRow(col);
            colDest = col;
        }

        // Find an available pawn in the pot
        for (int i = 0; i < pot.getNbRows(); i++) {
            if (!pot.isEmptyAt(0, i)) {
                pawn = pot.getElement(0, i);
                break;
            }
        }
        
        if (pawn == null) return null;

        // Create and return action to place the pawn
        ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "connect4board", rowDest, colDest);
        actions.setDoEndOfTurn(true);
        return actions;
    }
} 
