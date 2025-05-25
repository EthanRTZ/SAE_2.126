import boardifier.control.Logger;
import boardifier.model.GameElement;
import boardifier.model.Model;
import java.util.Scanner;
import model.Connect4Board;
import model.Connect4PawnPot;
import model.Connect4StageModel;
import model.Pawn;
import control.Connect4Controller;
import control.Connect4Decider;
import control.Connect4SmartDecider;
import control.Connect4GeniusDecider;
import boardifier.model.action.ActionList;
import boardifier.control.Decider;
import boardifier.model.action.GameAction;
import boardifier.control.ActionFactory;
import boardifier.control.ActionPlayer;
import java.io.File;
import java.io.FileNotFoundException;

public class Connect4Console {
    private static Scanner scanner;
    private static boolean useFileInput = false;

    private static void initScanner() {
        scanner = new Scanner(System.in);
        useFileInput = false;
        System.out.println("Utilisation de la console");
    }

    private static int readInt(String prompt, int min, int max) {
        if (scanner == null) {
            initScanner();
        }

        int value;
        do {
            if (!useFileInput) {
                System.out.print(prompt);
            }
            while (!scanner.hasNextInt()) {
                if (!useFileInput) {
                    System.out.println("Veuillez entrer un nombre entier valide.");
                    System.out.print(prompt);
                }
                scanner.next();
            }
            value = scanner.nextInt();
            if (!useFileInput && (value < min || value > max)) {
                System.out.println("La valeur doit être comprise entre " + min + " et " + max + ".");
            }
        } while (value < min || value > max);
        return value;
    }

    private static void displayPawnPots(Connect4StageModel stageModel) {
        System.out.println("\nPots de pions :");
        
        // Afficher les pions rouges
        Connect4PawnPot redPot = stageModel.getRedPot();
        System.out.print("Joueur 1 (Rouge) : ");
        System.out.print(boardifier.view.ConsoleColor.RED);
        for (int i = 0; i < redPot.getRemainingPawns(); i++) {
            System.out.print(" ● ");
        }
        System.out.println(boardifier.view.ConsoleColor.RESET);
        
        // Afficher les pions jaunes
        Connect4PawnPot yellowPot = stageModel.getYellowPot();
        System.out.print("Joueur 2 (Jaune) : ");
        System.out.print(boardifier.view.ConsoleColor.YELLOW);
        for (int i = 0; i < yellowPot.getRemainingPawns(); i++) {
            System.out.print(" ● ");
        }
        System.out.println(boardifier.view.ConsoleColor.RESET);
        System.out.println();
    }

    private static void displayBoard(Connect4Board board, Connect4StageModel stageModel) {
        displayPawnPots(stageModel);
        int nbCols = board.getNbCols();
        int nbRows = board.getNbRows();
        // Afficher les numéros de colonnes
        System.out.print("   ");
        for (int j = 0; j < nbCols; j++) {
            System.out.print("  " + (j + 1) + " ");
        }
        System.out.println();

        // Ligne supérieure
        System.out.print("  ╔");
        for (int j = 0; j < nbCols - 1; j++) {
            System.out.print("═══╦");
        }
        System.out.println("═══╗");

        // Affichage des lignes du plateau
        for (int i = 0; i < nbRows; i++) {
            System.out.printf("%2d║", nbRows - i); // Numéro de ligne à gauche
            for (int j = 0; j < nbCols; j++) {
                int value = board.getGrid()[i][j];
                String cell = "   ";
                if (value == Pawn.PAWN_BLACK)
                    cell = boardifier.view.ConsoleColor.YELLOW + " ● " + boardifier.view.ConsoleColor.RESET;
                else if (value == Pawn.PAWN_RED)
                    cell = boardifier.view.ConsoleColor.RED + " ● " + boardifier.view.ConsoleColor.RESET;
                System.out.print(cell + "║");
            }
            System.out.println();
            // Ligne intermédiaire ou inférieure
            if (i < nbRows - 1) {
                System.out.print("  ╠");
                for (int j = 0; j < nbCols - 1; j++) {
                    System.out.print("═══╬");
                }
                System.out.println("═══╣");
            }
        }
        // Ligne inférieure
        System.out.print("  ╚");
        for (int j = 0; j < nbCols - 1; j++) {
            System.out.print("═══╩");
        }
        System.out.println("═══╝");
    }

    public static void main(String[] args) {
        Logger.setLevel(Logger.LOGGER_TRACE);
        Logger.setVerbosity(Logger.VERBOSE_HIGH);
        
        // Initialiser le scanner
        initScanner();
        
        Model model = new Model();
        
        // Demander le mode de jeu
        int gameMode = readInt("Choisissez le mode de jeu (0: Joueur vs Joueur, 1: Joueur vs Ordinateur, 2: Ordinateur vs Ordinateur) : ", 0, 2);
        
        // Demander le niveau de l'ordinateur
        int computerLevel = 0;
        if (gameMode > 0) {
            computerLevel = readInt("Choisissez le niveau de l'ordinateur (0: Facile, 1: Moyen, 2: Difficile) : ", 0, 2);
        }
        
        // Ajouter les joueurs selon le mode choisi
        switch (gameMode) {
            case 0: // Joueur vs Joueur
                model.addHumanPlayer("Joueur 1");
                model.addHumanPlayer("Joueur 2");
                break;
            case 1: // Joueur vs Ordinateur
                model.addHumanPlayer("Joueur 1");
                model.addComputerPlayer("Ordinateur");
                break;
            case 2: // Ordinateur vs Ordinateur
                model.addComputerPlayer("Ordinateur 1");
                model.addComputerPlayer("Ordinateur 2");
                break;
        }
        
        // Demander les paramètres du jeu
        int nbCols = readInt("Nombre de colonnes (5-10) : ", 5, 10);
        int nbRows = readInt("Nombre de lignes (5-10) : ", 5, 10);
        int minSize = Math.min(nbCols, nbRows);
        int nbAlign = readInt("Nombre de jetons à aligner (3-" + minSize + ") : ", 3, minSize);
        
        // Si on utilisait le fichier pour les paramètres, on ferme le scanner et on en crée un nouveau pour les coups
        if (useFileInput) {
            scanner.close();
            useFileInput = false;
        }
        
        // Initialiser la scène de jeu
        Connect4StageModel stageModel = new Connect4StageModel("main", model);
        // Définir les dimensions du jeu
        stageModel.setDimensions(nbRows, nbCols, nbAlign);
        
        // Initialiser tous les éléments du jeu (pots de pions inclus)
        stageModel.getDefaultElementFactory().setup();
        
        model.startGame(stageModel);
        
        // Créer le contrôleur
        Connect4Controller controller = new Connect4Controller(model, null);
        
        // Boucle principale du jeu
        boolean gameOver = false;
        Connect4Board board = stageModel.getBoard();
        
        while (!gameOver) {
            // Afficher le plateau
            displayBoard(board, stageModel);
            
            // Afficher le joueur actuel
            String currentPlayer = model.getCurrentPlayer().getName();
            System.out.println("C'est au tour de " + currentPlayer);
            
            int col;
            // Vérifier si c'est un joueur ordinateur
            if (currentPlayer.toLowerCase().contains("ordinateur")) {
                // Simuler un délai de réflexion pour l'ordinateur
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                // Créer le décideur approprié selon le niveau
                Decider decider;
                switch (computerLevel) {
                    case 0:
                        decider = new Connect4Decider(model, controller);
                        break;
                    case 1:
                        decider = new Connect4SmartDecider(model, controller);
                        break;
                    case 2:
                        decider = new Connect4GeniusDecider(model, controller);
                        break;
                    default:
                        decider = new Connect4Decider(model, controller);
                }
                
                // Obtenir la décision du décideur
                ActionList actions = decider.decide();
                if (actions != null && !actions.getActions().isEmpty() && !actions.getActions().get(0).isEmpty()) {
                    // Exécuter l'action
                    ActionPlayer play = new ActionPlayer(model, controller, actions);
                    play.start();
                    continue;
                } else {
                    // Fallback sur une colonne aléatoire si le décideur échoue
                    do {
                        col = (int)(Math.random() * nbCols);
                    } while (board.isColumnFull(col));
                    System.out.println(currentPlayer + " joue dans la colonne " + (col + 1));
                }
            }
            else {
                // Tour d'un joueur humain
                col = readInt("Entrez le numéro de la colonne (1-" + nbCols + ") : ", 1, nbCols) - 1;
            }
            
            // Vérifier si la colonne est pleine
            if (board.isColumnFull(col)) {
                System.out.println("Cette colonne est pleine ! Choisissez une autre colonne.");
                continue;
            }
            
            // Trouver la première ligne vide
            int row = board.getFirstEmptyRow(col);
            
            // Placer le pion
            int color = model.getIdPlayer() == 0 ? Pawn.PAWN_RED : Pawn.PAWN_BLACK;
            board.getGrid()[row][col] = color;
            
            // Retirer un pion du pot correspondant
            Connect4PawnPot pot = (color == Pawn.PAWN_BLACK) ? stageModel.getYellowPot() : stageModel.getRedPot();
            if (pot.getRemainingPawns() > 0) {
                // Chercher le premier pion disponible dans le pot
                for (int i = 0; i < pot.getNbCols(); i++) {
                    GameElement pawn = pot.getElement(0, i);
                    if (pawn != null) {
                        pot.removeElement(pawn);
                        break;
                    }
                }
            }
            
            // Vérifier la victoire
            if (board.checkWin(row, col, color)) {
                displayBoard(board, stageModel);
                System.out.println(currentPlayer + " a gagné !");
                gameOver = true;
            } else if (board.isBoardFull()) {
                displayBoard(board, stageModel);
                System.out.println("Match nul !");
                gameOver = true;
            } else {
                model.setNextPlayer();
            }
        }
        
        // Fermer le scanner à la fin du jeu
        if (scanner != null) {
            scanner.close();
        }
    }
}
