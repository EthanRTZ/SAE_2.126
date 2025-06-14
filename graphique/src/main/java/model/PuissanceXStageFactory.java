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
        // Create the text that displays the player name
        TextElement text = new TextElement(stageModel.getCurrentPlayerName(), stageModel);
        text.setLocation(0, 0);
        stageModel.setPlayerName(text);

        // Get the dimensions chosen by the user from the model
        PuissanceXBoard board = new PuissanceXBoard(0, 1, stageModel,
                                             stageModel.getNbRows(), 
                                             stageModel.getNbCols(), 
                                             stageModel.getNbToAlign());
        stageModel.setBoard(board);

        // Calculate the total number of pawns needed
        int nbCols = stageModel.getNbCols();
        int nbRows = stageModel.getNbRows();
        int totalPawns = nbCols * nbRows;
        
        // Calculate pot positions based on board size
        int boardEndX = nbCols;
        int yellowPotX = -4; // Position to the left of the board
        int redPotX = boardEndX + 3; // Position to the right of the board

        // Calculate the number of pawns for each player
        int pawnsPerPlayerYellow = totalPawns / 2;
        int pawnsPerPlayerRed = pawnsPerPlayerYellow;
        if (totalPawns % 2 != 0) {
            pawnsPerPlayerRed++; // Red has 1 more pawn if odd
        }

        // Create pawn pots with maximum capacity
        PuissanceXPawnPot yellowPot = new PuissanceXPawnPot(yellowPotX, 1, stageModel, pawnsPerPlayerYellow);
        PuissanceXPawnPot redPot = new PuissanceXPawnPot(redPotX, 1, stageModel, pawnsPerPlayerRed);
        
        // Assign pots to the model
        stageModel.setYellowPot(yellowPot);
        stageModel.setRedPot(redPot);

        // Create pawns for each player and add them to their pot
        List<Pawn> pawns = new ArrayList<>();
        
        // Create and add yellow pawns
        for (int i = 0; i < pawnsPerPlayerYellow; i++) {
            Pawn yellowPawn = new Pawn(i + 1, Pawn.PAWN_YELLOW, stageModel);
            pawns.add(yellowPawn);
            yellowPot.addElement(yellowPawn, 0, i);
        }

        // Create and add red pawns (may have one more pawn)
        for (int i = 0; i < pawnsPerPlayerRed; i++) {
            Pawn redPawn = new Pawn(i + 1, Pawn.PAWN_RED, stageModel);
            pawns.add(redPawn);
            redPot.addElement(redPawn, 0, i);
        }
        
        stageModel.setPawns(pawns);
    }
}
