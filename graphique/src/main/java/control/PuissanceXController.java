package control;

import boardifier.control.ActionFactory;
import boardifier.control.ActionPlayer;
import boardifier.control.Controller;
import boardifier.control.StageFactory;
import boardifier.model.GameElement;
import boardifier.model.GameException;
import boardifier.model.Model;
import boardifier.model.Player;
import boardifier.model.action.ActionList;
import boardifier.view.View;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import model.Pawn;
import model.PuissanceXBoard;
import model.PuissanceXPawnPot;
import model.PuissanceXStageModel;

public class PuissanceXController extends Controller {
    private BufferedReader consoleIn;
    private BufferedReader fileIn;
    private boolean useFileInput;
    private boolean firstPlayer;

    public PuissanceXController(Model model, View view) {
        super(model, view);
        firstPlayer = true;
        consoleIn = new BufferedReader(new InputStreamReader(System.in));
        useFileInput = false;

        // Try to open match_nul.txt file

        // Initialize game scene
        try {
            PuissanceXStageModel stageModel = (PuissanceXStageModel) StageFactory.createStageModel("main", model);
            model.startGame(stageModel);
        } catch (GameException e) {
            System.out.println("Error during scene initialization: " + e.getMessage());
        }
    }

    public void stageLoop() {
        update();
        while (!model.isEndStage()) {
            playTurn();
            endOfTurn();
            update();
        }
        endGame();
    }

    private void playTurn() {
        Player p = model.getCurrentPlayer();
        if (p.getType() == Player.COMPUTER) {
            System.out.println("COMPUTER IS PLAYING");
            PuissanceXDecider decider = new PuissanceXDecider(model, this);
            ActionPlayer play = new ActionPlayer(model, this, decider, null);
            play.start();
            
            // Add a small delay to let the action execute
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            boolean ok = false;
            while (!ok) {
                System.out.print(p.getName() + " > ");
                try {
                    String line;
                    if (useFileInput) {
                        line = fileIn.readLine();
                        if (line == null) {
                            System.out.println("End of match_nul.txt file reached, switching to console");
                            useFileInput = false;
                            line = consoleIn.readLine();
                        } else {
                            System.out.println("Move read from match_nul.txt: " + line);
                        }
                    } else {
                        line = consoleIn.readLine();
                    }
                    
                    if (line != null && line.length() == 1) {
                        ok = analyseAndPlay(line);
                    }
                    if (!ok) {
                        System.out.println("Invalid instruction. Try again!");
                    }
                } catch (IOException e) {
                    System.out.println("Read error: " + e.getMessage());
                    useFileInput = false;
                }
            }
        }
    }

    public void endOfTurn() {
        // Don't change player if the action already does it
        if (!model.isEndStage()) {
            Player p = model.getCurrentPlayer();
            PuissanceXStageModel stageModel = (PuissanceXStageModel) model.getGameStage();
            stageModel.getPlayerName().setText(p.getName());
        }
    }

    private boolean analyseAndPlay(String line) {
        PuissanceXStageModel gameStage = (PuissanceXStageModel) model.getGameStage();
        PuissanceXBoard board = gameStage.getBoard();
        PuissanceXPawnPot pot;
        
        // Select the right pot based on current player
        if (model.getIdPlayer() == 0) {
            pot = gameStage.getYellowPot();
        } else {
            pot = gameStage.getRedPot();
        }

        // Convert column number to index
        int col;
        try {
            col = Integer.parseInt(line) - 1;
        } catch (NumberFormatException e) {
            return false;
        }
        if ((col < 0) || (col >= board.getNbCols())) return false;

        // Check if column is full
        if (board.isColumnFull(col)) return false;

        // Find first empty row in column
        int row = board.getFirstEmptyRow(col);

        // Find an available pawn in the pot
        GameElement pawn = null;
        for (int i = 0; i < pot.getNbRows(); i++) {
            if (!pot.isEmptyAt(0, i)) {
                pawn = pot.getElement(0, i);
                break;
            }
        }
        
        if (pawn == null) return false;

        // Create action to place the pawn
        ActionList actions = ActionFactory.generatePutInContainer(this, model, pawn, "PuissanceXboard", row, col);
        actions.setDoEndOfTurn(true);
        
        // Execute the action
        ActionPlayer play = new ActionPlayer(model, this, actions);
        play.start();
        
        return true;
    }

    public void endGame() {
        PuissanceXStageModel stageModel = (PuissanceXStageModel) model.getGameStage();
        int winner = stageModel.getWinner();
        if (winner == Pawn.PAWN_BLACK) {
            System.out.println("Player 1 wins!");
        } else if (winner == Pawn.PAWN_RED) {
            System.out.println("Player 2 wins!");
        } else {
            System.out.println("It's a draw!");
        }
    }
}
