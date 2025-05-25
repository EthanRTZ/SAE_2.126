package ConnectX;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import model.Connect4StageModel;
import model.Connect4Board;
import boardifier.model.Model;

class Connect4StageModelTest {
    
    @Test
    void testInitialState() {
        Model model = new Model();
        Connect4StageModel stageModel = new Connect4StageModel("test", model);
        stageModel.setDimensions(6, 7, 4);
        Connect4Board board = new Connect4Board(0, 0, stageModel, 6, 7, 4);
        stageModel.setBoard(board);
        
        assertNotNull(stageModel.getBoard());
        assertFalse(stageModel.isGameOver());
        assertEquals(-1, stageModel.getWinner());
    }

    @Test
    void testBoardDimensions() {
        Model model = new Model();
        Connect4StageModel stageModel = new Connect4StageModel("test", model);
        stageModel.setDimensions(6, 7, 4);
        Connect4Board board = new Connect4Board(0, 0, stageModel, 6, 7, 4);
        stageModel.setBoard(board);
        
        assertEquals(6, stageModel.getNbRows());
        assertEquals(7, stageModel.getNbCols());
        assertEquals(4, stageModel.getNbToAlign());
    }

    @Test
    void testGameNotOverAtStart() {
        Model model = new Model();
        Connect4StageModel stageModel = new Connect4StageModel("test", model);
        assertFalse(stageModel.isGameOver());
        assertEquals(-1, stageModel.getWinner());
    }

    @Test
    void testBoardState() {
        Model model = new Model();
        Connect4StageModel stageModel = new Connect4StageModel("test", model);
        stageModel.setDimensions(6, 7, 4);
        Connect4Board board = new Connect4Board(0, 0, stageModel, 6, 7, 4);
        stageModel.setBoard(board);
        
        // Vérifie que le plateau est vide au début
        for (int row = 0; row < board.getNbRows(); row++) {
            for (int col = 0; col < board.getNbCols(); col++) {
                assertEquals(-1, board.getGrid()[row][col]);
            }
        }
        
        // Vérifie qu'aucune colonne n'est pleine au début
        for (int col = 0; col < board.getNbCols(); col++) {
            assertFalse(board.isColumnFull(col));
        }
    }
} 