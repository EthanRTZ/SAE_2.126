package ConnectX;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import model.PuissanceXBoard;
import model.Pawn;
import model.PuissanceXStageModel;
import boardifier.model.Model;

class ConnectXBoardTest {
    
    @Test
    void testInitialBoardIsEmpty() {
        Model model = new Model();
        PuissanceXStageModel stageModel = new PuissanceXStageModel("test", model);
        PuissanceXBoard board = new PuissanceXBoard(0, 0, stageModel, 6, 7, 4);
        
        // Check that all cells are empty (-1)
        for (int row = 0; row < board.getNbRows(); row++) {
            for (int col = 0; col < board.getNbCols(); col++) {
                assertEquals(-1, board.getGrid()[row][col]);
            }
        }
    }

    @Test
    void testAddPawn() {
        Model model = new Model();
        PuissanceXStageModel stageModel = new PuissanceXStageModel("test", model);
        PuissanceXBoard board = new PuissanceXBoard(0, 0, stageModel, 6, 7, 4);
        Pawn pawn = new Pawn(1, Pawn.PAWN_BLACK, stageModel);
        
        // Check that an empty column can receive a pawn
        assertFalse(board.isColumnFull(0));
        int row = board.getFirstEmptyRow(0);
        assertTrue(row >= 0);
        
        // Place the pawn in the grid
        board.getGrid()[row][0] = Pawn.PAWN_BLACK;
        assertEquals(Pawn.PAWN_BLACK, board.getGrid()[row][0]);
    }

    @Test
    void testColumnIsFull() {
        Model model = new Model();
        PuissanceXStageModel stageModel = new PuissanceXStageModel("test", model);
        PuissanceXBoard board = new PuissanceXBoard(0, 0, stageModel, 6, 7, 4);
        
        // Fill the first column
        for (int row = board.getNbRows() - 1; row >= 0; row--) {
            board.getGrid()[row][0] = Pawn.PAWN_BLACK;
        }
        
        assertTrue(board.isColumnFull(0));
        assertEquals(-1, board.getFirstEmptyRow(0));
    }

    @Test
    void testCheckWin() {
        Model model = new Model();
        PuissanceXStageModel stageModel = new PuissanceXStageModel("test", model);
        PuissanceXBoard board = new PuissanceXBoard(0, 0, stageModel, 6, 7, 4);
        
        // Test vertical win
        for (int row = 5; row >= 2; row--) {
            board.getGrid()[row][0] = Pawn.PAWN_BLACK;
        }
        assertTrue(board.checkWin(2, 0, Pawn.PAWN_BLACK));
        
        // Test horizontal win
        board = new PuissanceXBoard(0, 0, stageModel, 6, 7, 4);
        for (int col = 0; col < 4; col++) {
            board.getGrid()[5][col] = Pawn.PAWN_RED;
        }
        assertTrue(board.checkWin(5, 3, Pawn.PAWN_RED));
    }
} 