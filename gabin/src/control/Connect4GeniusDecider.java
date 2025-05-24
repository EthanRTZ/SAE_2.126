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

public class Connect4GeniusDecider extends Decider {
    private static final Random random = new Random(Calendar.getInstance().getTimeInMillis());

    public Connect4GeniusDecider(Model model, Controller control) {
        super(model, control);
    }

    @Override
    public ActionList decide() {
        // Récupérer les éléments du jeu
        Connect4StageModel stage = (Connect4StageModel)model.getGameStage();
        Connect4Board board = stage.getBoard();
        Connect4PawnPot pot = null;
        GameElement pawn = null;
        int rowDest = 0;
        int colDest = 0;
        
        // Choisir le bon pot de pions selon le joueur
        if (model.getIdPlayer() == 0) {
            pot = stage.getYellowPot();
        } else {
            pot = stage.getRedPot();
        }

        // TODO: Implémenter la stratégie avancée
        // 1. Évaluation de la position
        // 2. Détection des alignements partiels
        // 3. Prévention des pièges
        // 4. Stratégie de centre
        // 5. MinMax avec profondeur limitée

        // Pour l'instant, on utilise une stratégie aléatoire
        int col;
        do {
            col = random.nextInt(board.getNbCols());
        } while (board.isColumnFull(col));
        rowDest = board.getFirstEmptyRow(col);
        colDest = col;

        // Trouver un pion disponible dans le pot
        for (int i = 0; i < pot.getNbRows(); i++) {
            if (!pot.isEmptyAt(0, i)) {
                pawn = pot.getElement(0, i);
                break;
            }
        }
        
        if (pawn == null) return null;

        // Créer et retourner l'action pour placer le pion
        ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "connect4board", rowDest, colDest);
        actions.setDoEndOfTurn(true);
        return actions;
    }
} 