package model;

import boardifier.model.GameStageModel;
import boardifier.model.StageElementsFactory;
import boardifier.model.TextElement;
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

        // Créer le plateau avec les dimensions par défaut
        Connect4Board board = new Connect4Board(0, 1, stageModel, 6, 7, 4);
        stageModel.setBoard(board);

        // Calculer le nombre total de pions nécessaires
        int nbCols = board.getNbCols();
        int nbRows = board.getNbRows();
        int totalPawns = nbCols * nbRows;
        
        // Calculer la position des pots en fonction de la taille du plateau
        // Le plateau commence à x=0, donc on place les pots après le plateau
        // On ajoute 2 pour avoir un espace entre le plateau et les pots
        int boardEndX = nbCols;
        int yellowPotX = -4; // Position à gauche du plateau
        int redPotX = boardEndX + 3; // Position à droite du plateau

        // Créer les pots de pions
        Connect4PawnPot yellowPot = new Connect4PawnPot(yellowPotX, 1, stageModel);
        Connect4PawnPot redPot = new Connect4PawnPot(redPotX, 1, stageModel);
        
        // Assigner les pots au modèle
        stageModel.setYellowPot(yellowPot);
        stageModel.setRedPot(redPot);

        // Créer les pions pour chaque joueur et les ajouter dans leur pot
        List<Pawn> pawns = new ArrayList<>();
        
        // Nombre de pions par joueur (totalPawns / 2)
        int pawnsPerPlayer = totalPawns / 2;
        
        for (int i = 0; i < pawnsPerPlayer; i++) {
            // Créer les pions
            Pawn yellowPawn = new Pawn(i + 1, Pawn.PAWN_BLACK, stageModel);
            Pawn redPawn = new Pawn(i + 1, Pawn.PAWN_RED, stageModel);
            
            // Ajouter les pions à la liste
            pawns.add(yellowPawn);
            pawns.add(redPawn);
            
            // Ajouter les pions aux pots
            yellowPot.addElement(yellowPawn, 0, i);  // Correction: inverser les coordonnées
            redPot.addElement(redPawn, 0, i);  // Correction: inverser les coordonnées
        }
        
        stageModel.setPawns(pawns);
    }
}
