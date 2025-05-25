package model;

import boardifier.model.GameStageModel;
import boardifier.model.StageElementsFactory;
import boardifier.model.TextElement;
import java.util.ArrayList;
import java.util.List;

public class PuissanceXStageFactory extends StageElementsFactory {
    private PuissanceXStageModel stageModel;

    public PuissanceXStageFactory(GameStageModel gameStageModel) {
        super(gameStageModel);
        stageModel = (PuissanceXStageModel) gameStageModel;
    }

    @Override
    public void setup() {
        // Créer le texte qui affiche le nom du joueur
        TextElement text = new TextElement(stageModel.getCurrentPlayerName(), stageModel);
        text.setLocation(0, 0);
        stageModel.setPlayerName(text);

        // Récupérer les dimensions choisies par l'utilisateur depuis le modèle
        PuissanceXBoard board = new PuissanceXBoard(0, 1, stageModel,
                                             stageModel.getNbRows(), 
                                             stageModel.getNbCols(), 
                                             stageModel.getNbToAlign());
        stageModel.setBoard(board);

        // Calculer le nombre total de pions nécessaires
        int nbCols = stageModel.getNbCols();
        int nbRows = stageModel.getNbRows();
        int totalPawns = nbCols * nbRows;
        
        // Calculer la position des pots en fonction de la taille du plateau
        int boardEndX = nbCols;
        int yellowPotX = -4; // Position à gauche du plateau
        int redPotX = boardEndX + 3; // Position à droite du plateau

        // Calculer le nombre de pions pour chaque joueur
        int pawnsPerPlayerYellow = totalPawns / 2;
        int pawnsPerPlayerRed = pawnsPerPlayerYellow;
        if (totalPawns % 2 != 0) {
            pawnsPerPlayerRed++; // Rouge a 1 pion de plus si impair
        }

        // Créer les pots de pions avec la capacité maximale
        PuissanceXPawnPot yellowPot = new PuissanceXPawnPot(yellowPotX, 1, stageModel, pawnsPerPlayerYellow);
        PuissanceXPawnPot redPot = new PuissanceXPawnPot(redPotX, 1, stageModel, pawnsPerPlayerRed);
        
        // Assigner les pots au modèle
        stageModel.setYellowPot(yellowPot);
        stageModel.setRedPot(redPot);

        // Créer les pions pour chaque joueur et les ajouter dans leur pot
        List<Pawn> pawns = new ArrayList<>();
        
        // Créer et ajouter les pions jaunes
        for (int i = 0; i < pawnsPerPlayerYellow; i++) {
            Pawn yellowPawn = new Pawn(i + 1, Pawn.PAWN_BLACK, stageModel);
            pawns.add(yellowPawn);
            yellowPot.addElement(yellowPawn, 0, i);
        }

        // Créer et ajouter les pions rouges (peut avoir un pion de plus)
        for (int i = 0; i < pawnsPerPlayerRed; i++) {
            Pawn redPawn = new Pawn(i + 1, Pawn.PAWN_RED, stageModel);
            pawns.add(redPawn);
            redPot.addElement(redPawn, 0, i);
        }
        
        stageModel.setPawns(pawns);
    }
}
