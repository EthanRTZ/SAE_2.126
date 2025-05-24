package model;

import boardifier.model.GameStageModel;
import boardifier.model.StageElementsFactory;
import boardifier.model.TextElement;
import java.util.ArrayList;
import java.util.List;

public class Connect4StageFactory extends StageElementsFactory {
    private Connect4StageModel stageModel;
    private int userRows;
    private int userCols;
    private int userNbAlign;

    public Connect4StageFactory(GameStageModel gameStageModel) {
        super(gameStageModel);
        stageModel = (Connect4StageModel) gameStageModel;
    }

    public void setDimensions(int rows, int cols, int nbAlign) {
        this.userRows = rows;
        this.userCols = cols;
        this.userNbAlign = nbAlign;
    }

    @Override
    public void setup() {
        // Créer le texte qui affiche le nom du joueur
        TextElement text = new TextElement(stageModel.getCurrentPlayerName(), stageModel);
        text.setLocation(0, 0);
        stageModel.setPlayerName(text);

        // Créer le plateau avec les dimensions spécifiées par l'utilisateur
        Connect4Board board = new Connect4Board(0, 1, stageModel, userRows, userCols, userNbAlign);
        stageModel.setBoard(board);

        // Calculer le nombre total de pions nécessaires
        int nbCols = board.getNbCols();
        int nbRows = board.getNbRows();
        int totalPawns = nbCols * nbRows;
        
        // Calculer la position des pots en fonction de la taille du plateau
        int boardEndX = nbCols;
        int yellowPotX = -4; // Position à gauche du plateau
        int redPotX = boardEndX + 3; // Position à droite du plateau

        // Chaque joueur reçoit un nombre de pions égal au nombre de cases dans la grille
        int pawnsPerPlayer = totalPawns;

        // Créer les pots de pions avec la capacité maximale
        Connect4PawnPot yellowPot = new Connect4PawnPot(yellowPotX, 1, stageModel, pawnsPerPlayer);
        Connect4PawnPot redPot = new Connect4PawnPot(redPotX, 1, stageModel, pawnsPerPlayer);
        
        // Assigner les pots au modèle
        stageModel.setYellowPot(yellowPot);
        stageModel.setRedPot(redPot);

        // Créer les pions pour chaque joueur et les ajouter dans leur pot
        List<Pawn> pawns = new ArrayList<>();
        
        // Créer et ajouter les pions pour chaque joueur
        for (int i = 0; i < pawnsPerPlayer; i++) {
            // Créer les pions
            Pawn yellowPawn = new Pawn(i + 1, Pawn.PAWN_BLACK, stageModel);
            Pawn redPawn = new Pawn(i + 1, Pawn.PAWN_RED, stageModel);
            
            // Ajouter les pions à la liste générale
            pawns.add(yellowPawn);
            pawns.add(redPawn);
            
            // Ajouter les pions aux pots (un pion par colonne)
            yellowPot.addElement(yellowPawn, 0, i);
            redPot.addElement(redPawn, 0, i);
        }
        
        stageModel.setPawns(pawns);
    }
}
