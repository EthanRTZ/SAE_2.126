import boardifier.control.ActionPlayer;
import boardifier.control.Decider;
import boardifier.control.Logger;
import boardifier.model.GameElement;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import control.Connect4Controller;
import control.Connect4Decider;
import control.Connect4SmartDecider;
import java.util.Scanner;
import model.Connect4Board;
import model.Connect4PawnPot;
import model.Connect4StageModel;
import model.Pawn;

public class Connect4Console {
    private static Scanner scanner;
    private static boolean useFileInput = false;

    private static void initScanner() {
        scanner = new Scanner(System.in);
        useFileInput = false;
        System.out.println("Using console input");
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
                    System.out.println("Please enter a valid integer.");
                    System.out.print(prompt);
                }
                scanner.next();
            }
            value = scanner.nextInt();
            if (!useFileInput && (value < min || value > max)) {
                System.out.println("Value must be between " + min + " and " + max + ".");
            }
        } while (value < min || value > max);
        return value;
    }

    private static void displayPawnPots(Connect4StageModel stageModel) {
        System.out.println("\nPawn pots:");
        
        // Display red pawns
        Connect4PawnPot redPot = stageModel.getRedPot();
        System.out.print("Player 1 (Red): ");
        System.out.print(boardifier.view.ConsoleColor.RED);
        for (int i = 0; i < redPot.getRemainingPawns(); i++) {
            System.out.print(" ● ");
        }
        System.out.println(boardifier.view.ConsoleColor.RESET);
        
        // Display yellow pawns
        Connect4PawnPot yellowPot = stageModel.getYellowPot();
        System.out.print("Player 2 (Yellow): ");
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
        // Display column numbers
        System.out.print("   ");
        for (int j = 0; j < nbCols; j++) {
            System.out.print("  " + (j + 1) + " ");
        }
        System.out.println();

        // Top line
        System.out.print("  ╔");
        for (int j = 0; j < nbCols - 1; j++) {
            System.out.print("═══╦");
        }
        System.out.println("═══╗");

        // Display board rows
        for (int i = 0; i < nbRows; i++) {
            System.out.printf("%2d║", nbRows - i); // Row number on the left
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
            // Middle or bottom line
            if (i < nbRows - 1) {
                System.out.print("  ╠");
                for (int j = 0; j < nbCols - 1; j++) {
                    System.out.print("═══╬");
                }
                System.out.println("═══╣");
            }
        }
        // Bottom line
        System.out.print("  ╚");
        for (int j = 0; j < nbCols - 1; j++) {
            System.out.print("═══╩");
        }
        System.out.println("═══╝");
    }

    public static void main(String[] args) {
        Logger.setLevel(Logger.LOGGER_TRACE);
        Logger.setVerbosity(Logger.VERBOSE_HIGH);
        
        // Initialize scanner
        initScanner();
        
        Model model = new Model();
        
        // Ask for game mode
        int gameMode = readInt("Choose game mode (0: Player vs Player, 1: Player vs Computer, 2: Computer vs Computer) : ", 0, 2);
        
        // Ask for computer level
        int computerLevel = 0;
        if (gameMode > 0) {
            computerLevel = readInt("Choose computer level (0: Easy, 1: Medium) : ", 0, 1);
        }
        
        // Add players according to chosen mode
        switch (gameMode) {
            case 0: // Player vs Player
                model.addHumanPlayer("Player 1");
                model.addHumanPlayer("Player 2");
                break;
            case 1: // Player vs Computer
                model.addHumanPlayer("Player 1");
                model.addComputerPlayer("Computer");
                break;
            case 2: // Computer vs Computer
                model.addComputerPlayer("Computer 1");
                model.addComputerPlayer("Computer 2");
                break;
        }
        
        // Ask for game parameters
        int nbCols = readInt("Number of columns (5-10) : ", 5, 10);
        int nbRows = readInt("Number of rows (5-10) : ", 5, 10);
        int minSize = Math.min(nbCols, nbRows);
        int nbAlign = readInt("Number of pawns to align (3-" + minSize + ") : ", 3, minSize);

        // If we were using the file for the parameters, we would close the scanner and create a new one for the strokes.
        if (useFileInput) {
            scanner.close();
            useFileInput = false;
        }
        
        // Initialize game scene
        Connect4StageModel stageModel = new Connect4StageModel("main", model);
        // Set game dimensions
        stageModel.setDimensions(nbRows, nbCols, nbAlign);
        
        // Initialize all game elements (including pawn pots)
        stageModel.getDefaultElementFactory().setup();
        
        model.startGame(stageModel);
        
        // Create controller
        Connect4Controller controller = new Connect4Controller(model, null);
        
        // Main game loop
        boolean gameOver = false;
        Connect4Board board = stageModel.getBoard();
        
        while (!gameOver) {
            // Display board
            displayBoard(board, stageModel);
            
            // Display current player
            String currentPlayer = model.getCurrentPlayer().getName();
            System.out.println("It's " + currentPlayer + "'s turn");
            
            int col;
            // Check if it's a computer player
            if (currentPlayer.toLowerCase().contains("computer")) {
                // Simulate thinking delay for computer
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                // Create appropriate decider based on level
                Decider decider;
                switch (computerLevel) {
                    case 0:
                        decider = new Connect4Decider(model, controller);
                        break;
                    case 1:
                        decider = new Connect4SmartDecider(model, controller);
                        break;
                    default:
                        decider = new Connect4Decider(model, controller);
                }
                
                // Get decider's decision
                ActionList actions = decider.decide();
                if (actions != null && !actions.getActions().isEmpty() && !actions.getActions().get(0).isEmpty()) {
                    // Execute action
                    ActionPlayer play = new ActionPlayer(model, controller, actions);
                    play.start();
                    continue;
                } else {
                    // Fallback to random column if decider fails
                    do {
                        col = (int)(Math.random() * nbCols);
                    } while (board.isColumnFull(col));
                    System.out.println(currentPlayer + " plays in column " + (col + 1));
                }
            }
            else {
                // Human player's turn
                col = readInt("Enter column number (1-" + nbCols + ") : ", 1, nbCols) - 1;
            }
            
            // Check if column is full
            if (board.isColumnFull(col)) {
                System.out.println("This column is full! Choose another column.");
                continue;
            }
            
            // Find first empty row
            int row = board.getFirstEmptyRow(col);
            
            // Place the pawn
            int color = model.getIdPlayer() == 0 ? Pawn.PAWN_RED : Pawn.PAWN_BLACK;
            board.getGrid()[row][col] = color;
            
            // Remove a pawn from corresponding pot
            Connect4PawnPot pot = (color == Pawn.PAWN_BLACK) ? stageModel.getYellowPot() : stageModel.getRedPot();
            if (pot.getRemainingPawns() > 0) {
                // Find first available pawn in pot
                for (int i = 0; i < pot.getNbCols(); i++) {
                    GameElement pawn = pot.getElement(0, i);
                    if (pawn != null) {
                        pot.removeElement(pawn);
                        break;
                    }
                }
            }
            
            // Check for win
            if (board.checkWin(row, col, color)) {
                displayBoard(board, stageModel);
                System.out.println(currentPlayer + " wins!");
                gameOver = true;
            } else if (board.isBoardFull()) {
                displayBoard(board, stageModel);
                System.out.println("It's a draw!");
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
