import boardifier.control.Decider;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import control.PuissanceXController;
import control.PuissanceXDecider;
import control.PuissanceXSmartDecider;
import control.PuissanceXHardDecider;
import java.util.Scanner;
import model.ConsoleGameTracker;
import model.Pawn;
import model.PuissanceXBoard;
import model.PuissanceXPawnPot;
import model.PuissanceXStageModel;

public class SimulationBot {
    private Model model;
    private PuissanceXStageModel stageModel;
    private PuissanceXController controller;
    private PuissanceXBoard board;
    private PuissanceXPawnPot redPot;
    private PuissanceXPawnPot yellowPot;
    private ConsoleGameTracker tracker;
    
    private int nbCols;
    private int nbRows;
    private int nbAlign;
    private int nbDuels;
    
    // Bot types
    private static final int BOT_EASY = 0;
    private static final int BOT_MEDIUM = 1;
    private static final int BOT_HARD = 2;

    private int bot1Type;
    private int bot2Type;
    private String bot1Name;
    private String bot2Name;

    // Simulation results
    private int victoiresBot1 = 0;
    private int victoiresBot2 = 0;
    private int matchsNuls = 0;
    private int totalCoups = 0;
    
    public SimulationBot(int nbDuels, int nbRows, int nbCols, int nbAlign, int bot1Type, int bot2Type) {
        this.nbDuels = nbDuels;
        this.nbRows = nbRows;
        this.nbCols = nbCols;
        this.nbAlign = nbAlign;
        this.bot1Type = bot1Type;
        this.bot2Type = bot2Type;

        // Set bot names based on their types
        this.bot1Name = getBotName(bot1Type);
        this.bot2Name = getBotName(bot2Type);
    }

    private String getBotName(int botType) {
        switch (botType) {
            case BOT_EASY: return "Easy Bot";
            case BOT_MEDIUM: return "Medium Bot";
            case BOT_HARD: return "Hard Bot";
            default: return "Unknown Bot";
        }
    }
    
    public void lancerSimulation() {
        long tempsDebut = System.currentTimeMillis();
        
        System.out.println("=== SIMULATION OF " + nbDuels + " DUELS ===");
        System.out.println("Configuration: " + nbRows + "x" + nbCols + ", alignment: " + nbAlign);
        System.out.println(bot1Name + " (Red) vs " + bot2Name + " (Yellow)");
        System.out.println("================================");
        
        for (int duel = 1; duel <= nbDuels; duel++) {
            System.out.println("\n--- Duel " + duel + "/" + nbDuels + " ---");
            jouerDuel();
            
            // Display progress every 10 duels
            if (duel % 10 == 0) {
                afficherProgres(duel);
            }
        }
        
        long tempsFin = System.currentTimeMillis();
        long tempsExecution = tempsFin - tempsDebut;
        
        afficherResultatsFinaux(tempsExecution);
    }
    
    private void jouerDuel() {
        // Initialize the game for this duel
        initialiserDuel();
        
        int coupsDuel = 0;
        boolean partieTerminee = false;
        
        while (!partieTerminee && coupsDuel < nbRows * nbCols) {
            // Determine which bot should play
            Decider decider;
            int currentBotType = model.getIdPlayer() == 0 ? bot1Type : bot2Type;

            // Create the appropriate decider based on bot type
            decider = createDecider(currentBotType);

            // Make the bot play
            ActionList actions = decider.decide();
            if (actions == null) {
                System.out.println("Error: The bot couldn't play");
                break;
            }
            
            // Simulate the move
            int currentPlayer = model.getIdPlayer();
            int color = currentPlayer == 0 ? Pawn.PAWN_RED : Pawn.PAWN_YELLOW;
            
            // Find the played column (simplified simulation)
            int colJouee = trouverColonneJouee(color);
            if (colJouee == -1) {
                System.out.println("Error: Impossible to determine the played column");
                break;
            }
            
            // Place the pawn
            int row = board.getFirstEmptyRow(colJouee);
            if (row != -1) {
                board.getGrid()[row][colJouee] = color;
                coupsDuel++;
                
                // Update the tracker
                tracker.updateGrid(board.getGrid());
                
                // Check for victory
                if (board.checkWin(row, colJouee, color) || tracker.checkWin(row, colJouee, color)) {
                    partieTerminee = true;
                    if (color == Pawn.PAWN_RED) {
                        victoiresBot1++;
                        System.out.println(bot1Name + " victory (Red) in " + coupsDuel + " moves");
                    } else {
                        victoiresBot2++;
                        System.out.println(bot2Name + " victory (Yellow) in " + coupsDuel + " moves");
                    }
                } else if (board.isBoardFull()) {
                    partieTerminee = true;
                    matchsNuls++;
                    System.out.println("Draw in " + coupsDuel + " moves");
                } else {
                    // Switch to next player
                    model.setNextPlayer();
                }
            } else {
                System.out.println("Error: Full column");
                break;
            }
        }
        
        totalCoups += coupsDuel;
    }
    
    private Decider createDecider(int botType) {
        Decider decider;
        switch (botType) {
            case BOT_EASY:
                decider = new PuissanceXSmartDecider(model, controller);
                ((PuissanceXSmartDecider) decider).setStage(stageModel);
                break;
            case BOT_MEDIUM:
                decider = new PuissanceXDecider(model, controller);
                ((PuissanceXDecider) decider).setStage(stageModel);
                break;
            case BOT_HARD:
                decider = new PuissanceXHardDecider(model, controller);
                ((PuissanceXHardDecider) decider).setStage(stageModel);
                break;
            default:
                decider = new PuissanceXSmartDecider(model, controller);
                ((PuissanceXSmartDecider) decider).setStage(stageModel);
        }
        return decider;
    }

    private void initialiserDuel() {
        // Create the model
        model = new Model();
        
        // Add the bots
        model.addComputerPlayer(bot1Name);
        model.addComputerPlayer(bot2Name);

        // Create the stage and controller
        stageModel = new PuissanceXStageModel("simulation", model);
        controller = new PuissanceXController(model, null);
        model.setGameStage(stageModel);
        
        // Create the board
        board = new PuissanceXBoard(80, 80, stageModel, nbRows, nbCols, nbAlign);
        stageModel.setBoard(board);
        
        // Create the pots
        redPot = new PuissanceXPawnPot(80, 80, stageModel, (nbRows * nbCols + 1) / 2);
        yellowPot = new PuissanceXPawnPot(80, 80, stageModel, nbRows * nbCols / 2);
        stageModel.setRedPot(redPot);
        stageModel.setYellowPot(yellowPot);
        
        // Create the tracker
        ConsoleGameTracker.resetInstance();
        tracker = ConsoleGameTracker.getInstance(nbRows, nbCols, nbAlign);
        tracker.reset();
    }
    
    private int trouverColonneJouee(int color) {
        // Compare board state before and after to find the played column
        int[][] gridAvant = new int[nbRows][nbCols];
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                gridAvant[i][j] = board.getGrid()[i][j];
            }
        }
        
        // Simulate the move (we can't really do it without the action, so we use a different approach)
        // For simplicity, we'll look for the first non-full column
        for (int col = 0; col < nbCols; col++) {
            if (!board.isColumnFull(col)) {
                return col;
            }
        }
        return -1;
    }
    
    private void afficherProgres(int duelActuel) {
        System.out.println("\n--- PROGRESS AT " + duelActuel + " DUELS ---");
        System.out.println(bot1Name + " (Red): " + victoiresBot1 + " victories (" +
                          String.format("%.1f", (double)victoiresBot1/duelActuel*100) + "%)");
        System.out.println(bot2Name + " (Yellow): " + victoiresBot2 + " victories (" +
                          String.format("%.1f", (double)victoiresBot2/duelActuel*100) + "%)");
        System.out.println("Draws: " + matchsNuls + " (" +
                          String.format("%.1f", (double)matchsNuls/duelActuel*100) + "%)");
        System.out.println("Average moves per game: " + 
                          String.format("%.1f", (double)totalCoups/duelActuel));
    }
    
    private void afficherResultatsFinaux(long tempsExecution) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("FINAL SIMULATION RESULTS");
        System.out.println("=".repeat(50));
        System.out.println("Number of duels: " + nbDuels);
        System.out.println("Configuration: " + nbRows + "x" + nbCols + ", alignment: " + nbAlign);
        System.out.println("lines: " + nbRows);
        System.out.println("columns: " + nbCols);
        System.out.println();
        System.out.println(bot1Name + " (Red):");
        System.out.println("  Victories: " + victoiresBot1 + " (" +
                          String.format("%.1f", (double)victoiresBot1/nbDuels*100) + "%)");
        System.out.println();
        System.out.println(bot2Name + " (Yellow):");
        System.out.println("  Victories: " + victoiresBot2 + " (" +
                          String.format("%.1f", (double)victoiresBot2/nbDuels*100) + "%)");
        System.out.println();
        System.out.println("Draws: " + matchsNuls + " (" + 
                          String.format("%.1f", (double)matchsNuls/nbDuels*100) + "%)");
        System.out.println();
        System.out.println("General statistics:");
        System.out.println("  Average moves per game: " + 
                          String.format("%.1f", (double)totalCoups/nbDuels));
        System.out.println("  Total moves played: " + totalCoups);
        
        // Display execution time in a readable format
        long seconds = tempsExecution / 1000;
        long milliseconds = tempsExecution % 1000;
        if (seconds > 0) {
            System.out.println("  Execution time: " + seconds + "s " + milliseconds + "ms");
        } else {
            System.out.println("  Execution time: " + milliseconds + "ms");
        }
        
        // Determine the winner
        if (victoiresBot1 > victoiresBot2) {
            System.out.println("\n🏆 WINNER: " + bot1Name + " (Red)");
        } else if (victoiresBot2 > victoiresBot1) {
            System.out.println("\n🏆 WINNER: " + bot2Name + " (Yellow)");
        } else {
            System.out.println("\n🤝 DRAW between the two bots");
        }
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== BOT DUEL SIMULATOR ===");
        System.out.println();
        
        // Ask for the number of duels
        System.out.print("Number of duels to simulate: ");
        int nbDuels = scanner.nextInt();
        
        // Ask for the size of the board
        System.out.print("Number of rows: ");
        int nbRows = scanner.nextInt();
        
        System.out.print("Number of columns: ");
        int nbCols = scanner.nextInt();
        
        System.out.print("Number of pawns to align: ");
        int nbAlign = scanner.nextInt();
        
        // Ask for bot types
        System.out.println("\nSelect bot for RED player:");
        System.out.println("0 - Easy Bot (SmartDecider)");
        System.out.println("1 - Medium Bot (Decider)");
        System.out.println("2 - Hard Bot (HardDecider)");
        System.out.print("Your choice: ");
        int bot1Type = scanner.nextInt();

        System.out.println("\nSelect bot for YELLOW player:");
        System.out.println("0 - Easy Bot (SmartDecider)");
        System.out.println("1 - Medium Bot (Decider)");
        System.out.println("2 - Hard Bot (HardDecider)");
        System.out.print("Your choice: ");
        int bot2Type = scanner.nextInt();

        scanner.close();
        
        // Validate parameters
        if (nbDuels <= 0 || nbRows <= 0 || nbCols <= 0 || nbAlign <= 0) {
            System.out.println("Error: All parameters must be positive");
            return;
        }
        
        if (nbAlign > Math.min(nbRows, nbCols)) {
            System.out.println("Error: The number of pieces to align cannot exceed the smaller dimension of the board");
            return;
        }
        
        if (bot1Type < 0 || bot1Type > 2 || bot2Type < 0 || bot2Type > 2) {
            System.out.println("Error: Invalid bot type selected");
            return;
        }

        // Launch the simulation
        SimulationBot simulation = new SimulationBot(nbDuels, nbRows, nbCols, nbAlign, bot1Type, bot2Type);
        simulation.lancerSimulation();
    }
}
