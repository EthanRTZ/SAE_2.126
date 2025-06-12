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
            // Enregistrer le stage avant de le créer
            StageFactory.registerModelAndView("main", "model.PuissanceXStageModel", "view.PuissanceXStageView");
            
            model = new Model();
            controller = new PuissanceXController(model, null);
            
            // Ajouter des joueurs
            model.addHumanPlayer("Player 1");
            model.addHumanPlayer("Player 2");
            
            // Créer et configurer le stage
            stageModel = new PuissanceXStageModel("main", model);
            stageModel.setDimensions(6, 7, 4); // 6 lignes, 7 colonnes, 4 à aligner
            stageModel.createElements(stageModel.getDefaultElementFactory());
            model.startGame(stageModel);
            
            // Récupérer les éléments créés
            board = stageModel.getBoard();
            yellowPot = stageModel.getYellowPot();
            redPot = stageModel.getRedPot();
        } catch (Exception e) {
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
        // Remplir la grille en alternant les pions des deux joueurs
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
        assertEquals(-1, stageModel.getWinner());
    }
    
    @Test
    void testVictoireJoueur1() {
        // Aligner 4 pions rouges horizontalement
        int row = board.getNbRows() - 1;
        for (int col = 0; col < 4; col++) {
            Pawn pawn = (Pawn) redPot.getElement(0, col);
            board.addElement(pawn, row, col);
            if (col < 3) model.setNextPlayer(); // Passer au joueur suivant sauf pour le dernier pion
        }
        assertEquals(0, stageModel.getWinner());
    }
    
    @Test
    void testVictoireJoueur2() {
        // Aligner 4 pions jaunes verticalement
        int col = 0;
        model.setNextPlayer(); // Commencer avec le joueur 2
        for (int row = 0; row < 4; row++) {
            Pawn pawn = (Pawn) yellowPot.getElement(0, row);
            board.addElement(pawn, row, col);
            if (row < 3) model.setNextPlayer(); // Passer au joueur suivant sauf pour le dernier pion
        }
        assertEquals(1, stageModel.getWinner());
    }
}
