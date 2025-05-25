package ConnectX;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import model.Connect4PawnPot;
import model.Pawn;
import model.Connect4StageModel;
import boardifier.model.Model;
import boardifier.model.GameElement;

class Connect4PawnPotTest {
    
    @Test
    void testInitialPawnPot() {
        Model model = new Model();
        Connect4StageModel stageModel = new Connect4StageModel("test", model);
        Connect4PawnPot pot = new Connect4PawnPot(0, 0, stageModel, 21);
        
        assertEquals(21, pot.getRemainingPawns());
    }
    
    @Test
    void testRemovePawn() {
        Model model = new Model();
        Connect4StageModel stageModel = new Connect4StageModel("test", model);
        Connect4PawnPot pot = new Connect4PawnPot(0, 0, stageModel, 21);
        Pawn pawn = new Pawn(1, Pawn.PAWN_BLACK, stageModel);
        
        // Ajoute d'abord le pion au pot
        pot.addElement(pawn, 0, 0);
        
        // Retire le pion et vérifie que le nombre de pions restants diminue
        pot.removeElement(pawn);
        assertEquals(20, pot.getRemainingPawns());
    }
    
    @Test
    void testMultipleRemovePawns() {
        Model model = new Model();
        Connect4StageModel stageModel = new Connect4StageModel("test", model);
        Connect4PawnPot pot = new Connect4PawnPot(0, 0, stageModel, 21);
        
        // Crée et ajoute plusieurs pions
        for(int i = 0; i < 5; i++) {
            Pawn pawn = new Pawn(i, Pawn.PAWN_BLACK, stageModel);
            pot.addElement(pawn, 0, i);
        }
        
        // Retire quelques pions
        for(int i = 0; i < 3; i++) {
            pot.removeElement(pot.getElement(0, i));
        }
        
        assertEquals(18, pot.getRemainingPawns());
    }
    
    @Test
    void testPotDimensions() {
        Model model = new Model();
        Connect4StageModel stageModel = new Connect4StageModel("test", model);
        Connect4PawnPot pot = new Connect4PawnPot(0, 0, stageModel, 21);
        
        // Vérifie que le pot a les bonnes dimensions (1 ligne, 21 colonnes)
        assertEquals(1, pot.getNbRows());
        assertEquals(21, pot.getNbCols());
    }
} 