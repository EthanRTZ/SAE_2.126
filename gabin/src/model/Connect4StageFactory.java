package model;

import boardifier.model.GameStageModel;
import boardifier.model.StageElementsFactory;
import boardifier.model.TextElement;
import boardifier.model.ContainerElement;
import boardifier.model.Model;
import java.util.ArrayList;
import java.util.List;

public class Connect4StageFactory extends StageElementsFactory {
    private Connect4StageModel stageModel;

    public Connect4StageFactory(GameStageModel gameStageModel) {
        super(gameStageModel);
        stageModel = (Connect4StageModel) gameStageModel;
    }

    @Override
    public void setup() {
        // Créer le texte qui affiche le nom du joueur
        TextElement text = new TextElement(stageModel.getCurrentPlayerName(), stageModel);
        text.setLocation(0, 0);
        stageModel.setPlayerName(text);

        // Créer le plateau
        Connect4Board board = new Connect4Board(0, 1, stageModel);
        stageModel.setBoard(board);

        // Calculer le nombre total de pions nécessaires
        Model model = stageModel.getModel();
        int nbCols = model.getGameParameter("nbCols");
        int nbRows = model.getGameParameter("nbRows");
        int totalPawns = nbCols * nbRows;

        // Créer les pions pour chaque joueur
        List<Pawn> pawns = new ArrayList<>();
        for (int i = 0; i < totalPawns / 2; i++) {
            pawns.add(new Pawn(i, Pawn.PAWN_BLACK, stageModel));
            pawns.add(new Pawn(i, Pawn.PAWN_RED, stageModel));
        }
        stageModel.setPawns(pawns);
    }
} 