import boardifier.control.Logger;
import boardifier.model.GameElement;
import boardifier.model.Model;
import java.util.Scanner;
import model.Connect4Board;
import model.Connect4PawnPot;
import model.Connect4StageModel;
import model.Pawn;

public class Connect4Console {
    private static int readInt(String prompt, int min, int max) {
        Scanner scanner = new Scanner(System.in);
        int value;
        do {
            System.out.print(prompt);
            while (!scanner.hasNextInt()) {
                System.out.println("Veuillez entrer un nombre entier valide.");
                System.out.print(prompt);
                scanner.next();
            }
            value = scanner.nextInt();
            if (value < min || value > max) {
                System.out.println("La valeur doit être comprise entre " + min + " et " + max + ".");
            }
        } while (value < min || value > max);
        return value;
    }

    private static void displayPawnPots(Connect4StageModel stageModel) {
        System.out.println("\nPots de pions :");
        System.out.println("Joueur 1 (Rouge) : " + boardifier.view.ConsoleColor.RED + " ● ".repeat(stageModel.getRedPot().getRemainingPawns()) + boardifier.view.ConsoleColor.RESET);
        System.out.println("Joueur 2 (Jaune) : " + boardifier.view.ConsoleColor.YELLOW + " ● ".repeat(stageModel.getYellowPot().getRemainingPawns()) + boardifier.view.ConsoleColor.RESET);
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
        
        Model model = new Model();
        
        // Demander le mode de jeu
        int gameMode = readInt("Choisissez le mode de jeu (0: Joueur vs Joueur, 1: Joueur vs Ordinateur, 2: Ordinateur vs Ordinateur) : ", 0, 2);
        
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
        
        // Initialiser la scène de jeu
        Connect4StageModel stageModel = new Connect4StageModel("main", model);
        Connect4Board board = new Connect4Board(0, 0, stageModel, nbRows, nbCols, nbAlign);
        stageModel.setBoard(board);
        
        // Initialiser tous les éléments du jeu (pots de pions inclus)
        stageModel.getDefaultElementFactory().setup();
        
        model.startGame(stageModel);
        
        // Boucle principale du jeu
        Scanner scanner = new Scanner(System.in);
        boolean gameOver = false;
        
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
                // L'ordinateur choisit une colonne aléatoire non pleine
                do {
                    col = (int)(Math.random() * nbCols);
                } while (board.isColumnFull(col));
                System.out.println(currentPlayer + " joue dans la colonne " + (col + 1));
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
            int color = model.getIdPlayer() == 0 ? Pawn.PAWN_BLACK : Pawn.PAWN_RED;
            board.getGrid()[row][col] = color;
            
            // Retirer un pion du pot correspondant
            if (color == Pawn.PAWN_BLACK) {
                Connect4PawnPot pot = stageModel.getYellowPot();
                if (pot.getRemainingPawns() > 0) {
                    GameElement pawn = pot.getLastElement(pot.getRemainingPawns() - 1, 0);
                    if (pawn != null) {
                        pot.removeElement(pawn);
                    }
                }
            } else {
                Connect4PawnPot pot = stageModel.getRedPot();
                if (pot.getRemainingPawns() > 0) {
                    GameElement pawn = pot.getLastElement(pot.getRemainingPawns() - 1, 0);
                    if (pawn != null) {
                        pot.removeElement(pawn);
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
    }
}
