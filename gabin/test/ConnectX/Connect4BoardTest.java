package ConnectX;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import model.Connect4Board;
import model.Pawn;
import model.Connect4StageModel;
import boardifier.model.Model;

class Connect4BoardTest {
    
    @Test
    void testInitialBoardIsEmpty() {
        Model model = new Model();
        Connect4StageModel stageModel = new Connect4StageModel("test", model);
        Connect4Board board = new Connect4Board(0, 0, stageModel, 6, 7, 4);
        
        // Vérifie que toutes les cases sont vides (-1)
        for (int row = 0; row < board.getNbRows(); row++) {
            for (int col = 0; col < board.getNbCols(); col++) {
                assertEquals(-1, board.getGrid()[row][col]);
            }
        }
    }

    @Test
    void testAddPawn() {
        Model model = new Model();
        Connect4StageModel stageModel = new Connect4StageModel("test", model);
        Connect4Board board = new Connect4Board(0, 0, stageModel, 6, 7, 4);
        Pawn pawn = new Pawn(1, Pawn.PAWN_BLACK, stageModel);
        
        // Vérifie qu'une colonne vide peut recevoir un pion
        assertFalse(board.isColumnFull(0));
        int row = board.getFirstEmptyRow(0);
        assertTrue(row >= 0);
        
        // Place le pion dans la grille
        board.getGrid()[row][0] = Pawn.PAWN_BLACK;
        assertEquals(Pawn.PAWN_BLACK, board.getGrid()[row][0]);
    }

    @Test
    void testColumnIsFull() {
        Model model = new Model();
        Connect4StageModel stageModel = new Connect4StageModel("test", model);
        Connect4Board board = new Connect4Board(0, 0, stageModel, 6, 7, 4);
        
        // Remplit la première colonne
        for (int row = board.getNbRows() - 1; row >= 0; row--) {
            board.getGrid()[row][0] = Pawn.PAWN_BLACK;
        }
        
        assertTrue(board.isColumnFull(0));
        assertEquals(-1, board.getFirstEmptyRow(0));
    }

    @Test
    void testCheckWin() {
        Model model = new Model();
        Connect4StageModel stageModel = new Connect4StageModel("test", model);
        Connect4Board board = new Connect4Board(0, 0, stageModel, 6, 7, 4);
        
        // Test victoire verticale
        for (int row = 5; row >= 2; row--) {
            board.getGrid()[row][0] = Pawn.PAWN_BLACK;
        }
        assertTrue(board.checkWin(2, 0, Pawn.PAWN_BLACK));
        
        // Test victoire horizontale
        board = new Connect4Board(0, 0, stageModel, 6, 7, 4);
        for (int col = 0; col < 4; col++) {
            board.getGrid()[5][col] = Pawn.PAWN_RED;
        }
        assertTrue(board.checkWin(5, 3, Pawn.PAWN_RED));
    }
} 