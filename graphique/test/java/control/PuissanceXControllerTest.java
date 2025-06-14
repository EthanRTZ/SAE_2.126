package control;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import boardifier.model.Model;
import boardifier.model.Player;
import boardifier.control.StageFactory;
import model.PuissanceXBoard;
import model.PuissanceXStageModel;
import model.Pawn;
import model.PuissanceXPawnPot;
import control.PuissanceXController;

public class PuissanceXControllerTest {
    
    private Model model;
    private PuissanceXController controller;
    private PuissanceXStageModel stageModel;
    private PuissanceXBoard board;
    private PuissanceXPawnPot yellowPot;
    private PuissanceXPawnPot redPot;
    
    @BeforeEach
    void setUp() {
        try {
            // Register the stage before creating it
            StageFactory.registerModelAndView("main", "model.PuissanceXStageModel", "view.PuissanceXStageView");
            
            model = new Model();
            controller = new PuissanceXController(model, null);
            
            // Add players
            model.addHumanPlayer("Player 1"); // Player 1 = Red
            model.addHumanPlayer("Player 2"); // Player 2 = Yellow
            
            // Create and configure the stage
            stageModel = new PuissanceXStageModel("main", model);
            stageModel.setDimensions(6, 7, 4); // 6 rows, 7 columns, 4 to align
            stageModel.createElements(stageModel.getDefaultElementFactory());
            model.startGame(stageModel);
            
            // Get the created elements
            board = stageModel.getBoard();
            yellowPot = stageModel.getYellowPot();
            redPot = stageModel.getRedPot();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during setup: " + e.getMessage());
        }
    }
    
    @Test
    void testInitialisation() {
        assertNotNull(controller);
        assertNotNull(stageModel);
        assertNotNull(board);
        assertEquals(2, model.getPlayers().size());
        assertEquals("Player 1", model.getPlayers().get(0).getName());
        assertEquals("Player 2", model.getPlayers().get(1).getName());
        assertEquals(6, board.getNbRows());
        assertEquals(7, board.getNbCols());
    }
    
    @Test
    void testMatchNul() {
        // Fill the grid by alternating pawns from both players
        for (int col = 0; col < board.getNbCols(); col++) {
            for (int row = board.getNbRows() - 1; row >= 0; row--) {
                if ((row + col) % 2 == 0) {
                    Pawn pawn = (Pawn) yellowPot.getElement(0, 0);
                    board.addElement(pawn, row, col);
                } else {
                    Pawn pawn = (Pawn) redPot.getElement(0, 0);
                    board.addElement(pawn, row, col);
                }
            }
        }
        System.out.println("Test Draw - Expected: -1 (draw), Got: " + stageModel.getWinner());
        assertEquals(-1, stageModel.getWinner());
    }
    
    @Test
    void testVictoireJoueur1() {
        // Align 4 red pawns horizontally (player 1 = red = 1)
        int row = board.getNbRows() - 1;
        
        for (int col = 0; col < 4; col++) {
            Pawn pawn = (Pawn) redPot.getElement(0, 0); // Red pawns for player 1
            board.addElement(pawn, row, col);
        }
        
        System.out.println("Test Player 1 Victory - Expected: 0 (player 1), Got: " + stageModel.getWinner());
        assertEquals(0, stageModel.getWinner()); // Player 1 (red) wins
    }
    
    @Test
    void testVictoireJoueur2() {
        // Align 4 yellow pawns vertically (player 2 = yellow = 0)
        int col = 0;
        model.setNextPlayer(); // Start with player 2
        
        for (int row = board.getNbRows() - 1; row >= board.getNbRows() - 4; row--) {
            Pawn pawn = (Pawn) yellowPot.getElement(0, 0); // Yellow pawns for player 2
            board.addElement(pawn, row, col);
            if (row > board.getNbRows() - 4) {
                model.setNextPlayer(); // Switch to next player except for the last pawn
            }
        }
        
        System.out.println("Test Player 2 Victory - Expected: 1 (player 2), Got: " + stageModel.getWinner());
        assertEquals(1, stageModel.getWinner()); // Player 2 (yellow) wins
    }
}
