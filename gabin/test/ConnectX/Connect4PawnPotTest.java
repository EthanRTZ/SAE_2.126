package ConnectX;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import model.PuissanceXPawnPot;
import model.Pawn;
import model.PuissanceXStageModel;
import boardifier.model.Model;
import boardifier.model.GameElement;

class PuissanceXPawnPotTest {
    
    @Test
    void testInitialPawnPot() {
        Model model = new Model();
        PuissanceXStageModel stageModel = new PuissanceXStageModel("test", model);
        PuissanceXPawnPot pot = new PuissanceXPawnPot(0, 0, stageModel, 21);
        
        assertEquals(21, pot.getRemainingPawns());
    }
    
    @Test
    void testRemovePawn() {
        Model model = new Model();
        PuissanceXStageModel stageModel = new PuissanceXStageModel("test", model);
        PuissanceXPawnPot pot = new PuissanceXPawnPot(0, 0, stageModel, 21);
        Pawn pawn = new Pawn(1, Pawn.PAWN_BLACK, stageModel);
        
        // First add the pawn to the pot
        pot.addElement(pawn, 0, 0);
        
        // Remove the pawn and check that the number of remaining pawns decreases
        pot.removeElement(pawn);
        assertEquals(20, pot.getRemainingPawns());
    }
    
    @Test
    void testMultipleRemovePawns() {
        Model model = new Model();
        PuissanceXStageModel stageModel = new PuissanceXStageModel("test", model);
        PuissanceXPawnPot pot = new PuissanceXPawnPot(0, 0, stageModel, 21);
        
        // Create and add multiple pawns
        for(int i = 0; i < 5; i++) {
            Pawn pawn = new Pawn(i, Pawn.PAWN_BLACK, stageModel);
            pot.addElement(pawn, 0, i);
        }
        
        // Remove some pawns
        for(int i = 0; i < 3; i++) {
            pot.removeElement(pot.getElement(0, i));
        }
        
        assertEquals(18, pot.getRemainingPawns());
    }
    
    @Test
    void testPotDimensions() {
        Model model = new Model();
        PuissanceXStageModel stageModel = new PuissanceXStageModel("test", model);
        PuissanceXPawnPot pot = new PuissanceXPawnPot(0, 0, stageModel, 21);
        
        // Check that the pot has the correct dimensions (1 row, 21 columns)
        assertEquals(1, pot.getNbRows());
        assertEquals(21, pot.getNbCols());
    }
} 