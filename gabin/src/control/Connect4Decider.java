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

    public Connect4Decider(Model model, Controller control) {
        super(model, control);
    }

    @Override
    public ActionList decide() {
        // do a cast get a variable of the real type to get access to the attributes of Connect4StageModel
        Connect4StageModel stage = (Connect4StageModel)model.getGameStage();
        Connect4Board board = stage.getBoard(); // get the board
        Connect4PawnPot pot = null; // the pot where to take a pawn
        GameElement pawn = null; // the pawn that is moved
        int rowDest = 0; // the dest. row in board
        int colDest = 0; // the dest. col in board
        
        // Sélectionner le pot correspondant au joueur actuel
        if (model.getIdPlayer() == Pawn.PAWN_BLACK) {
            pot = stage.getYellowPot();
        } else {
            pot = stage.getRedPot();
        }

        // Choisir une colonne aléatoire qui n'est pas pleine
        int col;
        do {
            col = random.nextInt(board.getNbCols());
        } while (board.isColumnFull(col));

        // Trouver la première ligne vide dans la colonne
        int row = board.getFirstEmptyRow(col);

        // Trouver un pion disponible dans le pot
        for (int i = 0; i < pot.getNbCols(); i++) {
            if (!pot.isEmptyAt(i, 0)) {
                pawn = pot.getElement(i, 0);
                rowDest = row;
                colDest = col;
                break;
            }
        }
        
        if (pawn == null) return null;

        // Placer le pion
        ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "connect4board", rowDest, colDest);
        actions.setDoEndOfTurn(true); // after playing this action list, it will be the end of turn for current player.
        return actions;
    }
}
