package ConnectX;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import model.PuissanceXStageModel;
import model.PuissanceXBoard;
import boardifier.model.Model;

class ConnectXStageModelTest {
    
    @Test
    void testInitialState() {
        Model model = new Model();
        PuissanceXStageModel stageModel = new PuissanceXStageModel("test", model);
        stageModel.setDimensions(6, 7, 4);
        PuissanceXBoard board = new PuissanceXBoard(0, 0, stageModel, 6, 7, 4);
        stageModel.setBoard(board);
        
        assertNotNull(stageModel.getBoard());
        assertFalse(stageModel.isGameOver());
        assertEquals(-1, stageModel.getWinner());
    }

    @Test
    void testBoardDimensions() {
        Model model = new Model();
        PuissanceXStageModel stageModel = new PuissanceXStageModel("test", model);
        stageModel.setDimensions(6, 7, 4);
        PuissanceXBoard board = new PuissanceXBoard(0, 0, stageModel, 6, 7, 4);
        stageModel.setBoard(board);
        
        assertEquals(6, stageModel.getNbRows());
        assertEquals(7, stageModel.getNbCols());
        assertEquals(4, stageModel.getNbToAlign());
    }

    @Test
    void testGameNotOverAtStart() {
        Model model = new Model();
        PuissanceXStageModel stageModel = new PuissanceXStageModel("test", model);
        assertFalse(stageModel.isGameOver());
        assertEquals(-1, stageModel.getWinner());
    }

    @Test
    void testBoardState() {
        Model model = new Model();
        PuissanceXStageModel stageModel = new PuissanceXStageModel("test", model);
        stageModel.setDimensions(6, 7, 4);
        PuissanceXBoard board = new PuissanceXBoard(0, 0, stageModel, 6, 7, 4);
        stageModel.setBoard(board);
        
        // Check that the board is empty at the start
        for (int row = 0; row < board.getNbRows(); row++) {
            for (int col = 0; col < board.getNbCols(); col++) {
                assertEquals(-1, board.getGrid()[row][col]);
            }
        }
        
        // Check that no column is full at the start
        for (int col = 0; col < board.getNbCols(); col++) {
            assertFalse(board.isColumnFull(col));
        }
    }
} 