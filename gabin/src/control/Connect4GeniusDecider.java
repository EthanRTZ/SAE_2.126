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

public class Connect4GeniusDecider extends Decider {
    private static final Random random = new Random(Calendar.getInstance().getTimeInMillis());

    public Connect4GeniusDecider(Model model, Controller control) {
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

        // TODO: Implement advanced strategy
        // 1. Position evaluation
        // 2. Partial alignment detection
        // 3. Trap prevention
        // 4. Center strategy
        // 5. MinMax with limited depth

        // For now, using a random strategy
        int col;
        do {
            col = random.nextInt(board.getNbCols());
        } while (board.isColumnFull(col));
        rowDest = board.getFirstEmptyRow(col);
        colDest = col;

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