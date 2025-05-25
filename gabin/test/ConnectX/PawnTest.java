package ConnectX;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import model.Pawn;
import model.Connect4StageModel;
import boardifier.model.Model;

class PawnTest {
    
    @Test
    void testPawnCreation() {
        Model model = new Model();
        Connect4StageModel stageModel = new Connect4StageModel("test", model);
        Pawn pawn = new Pawn(1, Pawn.PAWN_BLACK, stageModel);
        assertEquals(Pawn.PAWN_BLACK, pawn.getColor());
        assertEquals(1, pawn.getNumber());
    }

    @Test
    void testPawnEquality() {
        Model model = new Model();
        Connect4StageModel stageModel = new Connect4StageModel("test", model);
        Pawn pawn1 = new Pawn(1, Pawn.PAWN_BLACK, stageModel);
        Pawn pawn2 = new Pawn(1, Pawn.PAWN_BLACK, stageModel);
        Pawn pawn3 = new Pawn(1, Pawn.PAWN_RED, stageModel);
        
        // Test que deux pions de même couleur sont considérés différents
        assertNotEquals(pawn1, pawn2);
        // Test que deux pions de couleurs différentes sont différents
        assertNotEquals(pawn1, pawn3);
    }
} 