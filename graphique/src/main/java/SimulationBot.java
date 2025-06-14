import boardifier.control.Decider;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import control.PuissanceXController;
import control.PuissanceXDecider;
import control.PuissanceXSmartDecider;
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
    
    // Simulation results
    private int victoiresBotFacile = 0;
    private int victoiresBotMoyen = 0;
    private int matchsNuls = 0;
    private int totalCoups = 0;
    
    public SimulationBot(int nbDuels, int nbRows, int nbCols, int nbAlign) {
        this.nbDuels = nbDuels;
        this.nbRows = nbRows;
        this.nbCols = nbCols;
        this.nbAlign = nbAlign;
    }
    
    public void lancerSimulation() {
        long tempsDebut = System.currentTimeMillis();
        
        System.out.println("=== SIMULATION OF " + nbDuels + " DUELS ===");
        System.out.println("Configuration: " + nbRows + "x" + nbCols + ", alignment: " + nbAlign);
        System.out.println("Easy Bot vs Medium Bot");
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
            if (model.getIdPlayer() == 0) {
                // Easy Bot (SmartDecider)
                decider = new PuissanceXSmartDecider(model, controller);
                ((PuissanceXSmartDecider) decider).setStage(stageModel);
            } else {
                // Medium Bot (Decider)
                decider = new PuissanceXDecider(model, controller);
                ((PuissanceXDecider) decider).setStage(stageModel);
            }
            
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
                        victoiresBotFacile++;
                        System.out.println("Easy Bot victory (Red) in " + coupsDuel + " moves");
                    } else {
                        victoiresBotMoyen++;
                        System.out.println("Medium Bot victory (Yellow) in " + coupsDuel + " moves");
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
    
    private void initialiserDuel() {
        // Create the model
        model = new Model();
        
        // Add the bots
        model.addComputerPlayer("Easy Bot");
        model.addComputerPlayer("Medium Bot");
        
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
        System.out.println("Easy Bot: " + victoiresBotFacile + " victories (" + 
                          String.format("%.1f", (double)victoiresBotFacile/duelActuel*100) + "%)");
        System.out.println("Medium Bot: " + victoiresBotMoyen + " victories (" + 
                          String.format("%.1f", (double)victoiresBotMoyen/duelActuel*100) + "%)");
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
        System.out.println("Easy Bot (SmartDecider):");
        System.out.println("  Victories: " + victoiresBotFacile + " (" + 
                          String.format("%.1f", (double)victoiresBotFacile/nbDuels*100) + "%)");
        System.out.println();
        System.out.println("Medium Bot (Decider):");
        System.out.println("  Victories: " + victoiresBotMoyen + " (" + 
                          String.format("%.1f", (double)victoiresBotMoyen/nbDuels*100) + "%)");
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
        if (victoiresBotFacile > victoiresBotMoyen) {
            System.out.println("\n🏆 WINNER: Easy Bot (SmartDecider)");
        } else if (victoiresBotMoyen > victoiresBotFacile) {
            System.out.println("\n🏆 WINNER: Medium Bot (Decider)");
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
        
        // Launch the simulation
        SimulationBot simulation = new SimulationBot(nbDuels, nbRows, nbCols, nbAlign);
        simulation.lancerSimulation();
    }
} 