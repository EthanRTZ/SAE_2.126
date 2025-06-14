package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import boardifier.model.Model;
import boardifier.model.GameStageModel;
import model.PuissanceXBoard;
import model.PuissanceXStageModel;

public class PuissanceXBoardTest {
    
    private Model model;
    private PuissanceXStageModel stageModel;
    private PuissanceXBoard board;
    private static final int NB_ROWS = 6;
    private static final int NB_COLS = 7;
    private static final int NB_ALIGN = 4;
    private static final int BOARD_X = 0;
    private static final int BOARD_Y = 0;
    
    @BeforeEach
    void setUp() {
        model = new Model();
        stageModel = new PuissanceXStageModel("test", model);
        board = new PuissanceXBoard(BOARD_X, BOARD_Y, stageModel, NB_ROWS, NB_COLS, NB_ALIGN);
    }
    
    @Test
    void testInitialisation() {
        assertEquals(NB_COLS, board.getNbCols());
        assertEquals(NB_ROWS, board.getNbRows());
        assertEquals(NB_ALIGN, board.getNbAlign());
        
        // Check that all cells are empty (-1)
        int[][] grid = board.getGrid();
        for (int i = 0; i < NB_ROWS; i++) {
            for (int j = 0; j < NB_COLS; j++) {
                assertEquals(-1, grid[i][j]);
            }
        }
    }
    
    @Test
    void testColonnePleine() {
        int col = 0;
        // Fill a column
        for (int i = NB_ROWS - 1; i >= 0; i--) {
            board.getGrid()[i][col] = 1;
        }
        
        assertTrue(board.isColumnFull(col));
        assertEquals(-1, board.getFirstEmptyRow(col));
    }
    
    @Test
    void testVictoireHorizontale() {
        int row = NB_ROWS - 1;
        // Place 4 pawns aligned horizontally
        for (int col = 0; col < NB_ALIGN; col++) {
            board.getGrid()[row][col] = 1;
        }
        
        assertTrue(board.checkWin(row, NB_ALIGN - 1, 1));
    }
    
    @Test
    void testVictoireVerticale() {
        int col = 0;
        // Place 4 pawns aligned vertically
        for (int row = NB_ROWS - 1; row >= NB_ROWS - NB_ALIGN; row--) {
            board.getGrid()[row][col] = 1;
        }
        
        assertTrue(board.checkWin(NB_ROWS - NB_ALIGN, col, 1));
    }
    
    @Test
    void testVictoireDiagonale() {
        // Create a diagonal configuration
        for (int i = 0; i < NB_ALIGN; i++) {
            board.getGrid()[NB_ROWS - 1 - i][i] = 1;
        }
        
        assertTrue(board.checkWin(NB_ROWS - NB_ALIGN, NB_ALIGN - 1, 1));
    }
    
    @Test
    void testPasDeVictoire() {
        // Place pawns without creating an alignment
        for (int col = 0; col < 3; col++) {
            board.getGrid()[NB_ROWS - 1][col] = 1;
        }
        
        assertFalse(board.checkWin(NB_ROWS - 1, 2, 1));
    }
    
    @Test
    void testPlateauPlein() {
        // Fill the entire board
        for (int i = 0; i < NB_ROWS; i++) {
            for (int j = 0; j < NB_COLS; j++) {
                board.getGrid()[i][j] = (i + j) % 2;
            }
        }
        
        assertTrue(board.isBoardFull());
    }
    
    @Test
    void testClear() {
        // Fill some cells
        board.getGrid()[0][0] = 1;
        board.getGrid()[1][1] = 2;
        
        // Clear the board
        board.clear();
        
        // Check that all cells are empty
        int[][] grid = board.getGrid();
        for (int i = 0; i < NB_ROWS; i++) {
            for (int j = 0; j < NB_COLS; j++) {
                assertEquals(-1, grid[i][j]);
            }
        }
    }
}