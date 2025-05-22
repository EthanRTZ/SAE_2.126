import boardifier.control.Logger;
import boardifier.model.GameException;
import boardifier.model.Model;
import control.Connect4Controller;
import java.util.Scanner;
import model.Connect4StageModel;
import model.Connect4Board;
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

    private static void displayBoard(Connect4Board board) {
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
                if (value == Pawn.PAWN_BLACK) cell = " X ";
                else if (value == Pawn.PAWN_RED) cell = " O ";
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
        // Ajouter les joueurs
        model.addHumanPlayer("Joueur 1");
        model.addHumanPlayer("Joueur 2");
        
        // Demander les paramètres du jeu
        int nbCols = readInt("Nombre de colonnes (5-10) : ", 5, 10);
        int nbRows = readInt("Nombre de lignes (5-10) : ", 5, 10);
        int minSize = Math.min(nbCols, nbRows);
        int nbAlign = readInt("Nombre de jetons à aligner (3-" + minSize + ") : ", 3, minSize);
        
        // Initialiser la scène de jeu
        Connect4StageModel stageModel = new Connect4StageModel("main", model);
        Connect4Board board = new Connect4Board(0, 0, stageModel, nbRows, nbCols, nbAlign);
        stageModel.setBoard(board);
        model.startGame(stageModel);
        
        // Boucle principale du jeu
        Scanner scanner = new Scanner(System.in);
        boolean gameOver = false;
        
        while (!gameOver) {
            // Afficher le plateau
            displayBoard(board);
            
            // Afficher le joueur actuel
            String currentPlayer = model.getCurrentPlayer().getName();
            System.out.println("C'est au tour de " + currentPlayer);
            
            // Demander la colonne
            int col = readInt("Entrez le numéro de la colonne (1-" + nbCols + ") : ", 1, nbCols) - 1;
            
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
            
            // Vérifier la victoire
            if (board.checkWin(row, col, color)) {
                displayBoard(board);
                System.out.println(currentPlayer + " a gagné !");
                gameOver = true;
            } else if (board.isBoardFull()) {
                displayBoard(board);
                System.out.println("Match nul !");
                gameOver = true;
            } else {
                model.setNextPlayer();
            }
        }
    }
} 