package control;

import boardifier.control.ActionFactory;
import boardifier.control.Controller;
import boardifier.control.Decider;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import model.Connect4Board;
import model.Connect4StageModel;
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
        Connect4StageModel stage = (Connect4StageModel) model.getGameStage();
        Connect4Board board = stage.getBoard();

        // Choisir une colonne aléatoire qui n'est pas pleine
        int col;
        do {
            col = random.nextInt(7);
        } while (board.isColumnFull(col));

        // Trouver la première ligne vide dans la colonne
        int row = board.getFirstEmptyRow(col);

        // Trouver un pion disponible pour le joueur actuel
        Pawn pawn = null;
        int color = model.getIdPlayer() == 0 ? Pawn.PAWN_BLACK : Pawn.PAWN_RED;
        for (Pawn p : stage.getPawns()) {
            if (p.getColor() == color && !board.contains(p)) {
                pawn = p;
                break;
            }
        }
        
        if (pawn == null) return null;

        // Placer le pion
        return ActionFactory.generatePutInContainer(model, pawn, "connect4board", row, col);
    }
} 