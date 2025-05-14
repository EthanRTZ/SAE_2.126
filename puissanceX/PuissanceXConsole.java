import boardifier.control.Logger;
import boardifier.model.GameException;
import boardifier.view.View;
import boardifier.control.StageFactory;
import boardifier.model.Model;
import control.PuissanceXController;

public class PuissanceXConsole {
    /**
     * TODO pour adapter en PuissanceX:
     * - Permettre de définir la taille du plateau (NxM)
     * - Permettre de définir le nombre de pions à aligner (X)
     * - Adapter la logique de victoire (X pions alignés)
     * - Ajouter la logique de gravité (les pions tombent)
     */
    public static void main(String[] args) {
        // Configuration du logger
        Logger.setLevel(Logger.LOGGER_TRACE);
        Logger.setVerbosity(Logger.VERBOSE_HIGH);
        
        // Mode de jeu (0: Humain vs Humain, 1: Humain vs IA, 2: IA vs IA)
        int mode = 0;
        if (args.length == 1) {
            try {
                mode = Integer.parseInt(args[0]);
                if ((mode <0) || (mode>2)) mode = 0;
            }
            catch(NumberFormatException e) {
                mode = 0;
            }
        }

        // Création du modèle et ajout des joueurs
        Model model = new Model();
        if (mode == 0) {
            model.addHumanPlayer("player1");
            model.addHumanPlayer("player2");
        }
        else if (mode == 1) {
            model.addHumanPlayer("player");
            model.addComputerPlayer("computer");
        }
        else if (mode == 2) {
            model.addComputerPlayer("computer1");
            model.addComputerPlayer("computer2");
        }

        // TODO: Modifier pour utiliser les classes PuissanceX
        StageFactory.registerModelAndView("puissanceX", "model.PuissanceXStageModel", "view.PuissanceXStageView");
        View gameView = new View(model);
        PuissanceXController control = new PuissanceXController(model, gameView);
        control.setFirstStageName("puissanceX");
        
        try {
            control.startGame();
            control.stageLoop();
        }
        catch(GameException e) {
            System.out.println("Cannot start the game. Abort");
        }
    }
} 