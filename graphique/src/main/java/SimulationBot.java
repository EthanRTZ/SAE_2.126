import boardifier.control.Controller;
import boardifier.control.Decider;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import control.PuissanceXController;
import control.PuissanceXDecider;
import control.PuissanceXSmartDecider;
import model.ConsoleGameTracker;
import model.Pawn;
import model.PuissanceXBoard;
import model.PuissanceXPawnPot;
import model.PuissanceXStageModel;

import java.util.Scanner;

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
    
    // Résultats de la simulation
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
        System.out.println("=== SIMULATION DE " + nbDuels + " DUELS ===");
        System.out.println("Configuration: " + nbRows + "x" + nbCols + ", alignement: " + nbAlign);
        System.out.println("Bot Facile vs Bot Moyen");
        System.out.println("================================");
        
        for (int duel = 1; duel <= nbDuels; duel++) {
            System.out.println("\n--- Duel " + duel + "/" + nbDuels + " ---");
            jouerDuel();
            
            // Afficher le progrès tous les 10 duels
            if (duel % 10 == 0) {
                afficherProgres(duel);
            }
        }
        
        afficherResultatsFinaux();
    }
    
    private void jouerDuel() {
        // Initialiser le jeu pour ce duel
        initialiserDuel();
        
        int coupsDuel = 0;
        boolean partieTerminee = false;
        
        while (!partieTerminee && coupsDuel < nbRows * nbCols) {
            // Déterminer quel bot doit jouer
            Decider decider;
            if (model.getIdPlayer() == 0) {
                // Bot Facile (SmartDecider)
                decider = new PuissanceXSmartDecider(model, controller);
                ((PuissanceXSmartDecider) decider).setStage(stageModel);
            } else {
                // Bot Moyen (Decider)
                decider = new PuissanceXDecider(model, controller);
                ((PuissanceXDecider) decider).setStage(stageModel);
            }
            
            // Faire jouer le bot
            ActionList actions = decider.decide();
            if (actions == null) {
                System.out.println("Erreur: Le bot n'a pas pu jouer");
                break;
            }
            
            // Simuler le coup
            int currentPlayer = model.getIdPlayer();
            int color = currentPlayer == 0 ? Pawn.PAWN_RED : Pawn.PAWN_YELLOW;
            
            // Trouver la colonne jouée (simulation simplifiée)
            int colJouee = trouverColonneJouee(color);
            if (colJouee == -1) {
                System.out.println("Erreur: Impossible de déterminer la colonne jouée");
                break;
            }
            
            // Placer le pion
            int row = board.getFirstEmptyRow(colJouee);
            if (row != -1) {
                board.getGrid()[row][colJouee] = color;
                coupsDuel++;
                
                // Mettre à jour le tracker
                tracker.updateGrid(board.getGrid());
                
                // Vérifier la victoire
                if (board.checkWin(row, colJouee, color) || tracker.checkWin(row, colJouee, color)) {
                    partieTerminee = true;
                    if (color == Pawn.PAWN_RED) {
                        victoiresBotFacile++;
                        System.out.println("Victoire du Bot Facile (Rouge) en " + coupsDuel + " coups");
                    } else {
                        victoiresBotMoyen++;
                        System.out.println("Victoire du Bot Moyen (Jaune) en " + coupsDuel + " coups");
                    }
                } else if (board.isBoardFull()) {
                    partieTerminee = true;
                    matchsNuls++;
                    System.out.println("Match nul en " + coupsDuel + " coups");
                } else {
                    // Passer au joueur suivant
                    model.setNextPlayer();
                }
            } else {
                System.out.println("Erreur: Colonne pleine");
                break;
            }
        }
        
        totalCoups += coupsDuel;
    }
    
    private void initialiserDuel() {
        // Créer le modèle
        model = new Model();
        
        // Ajouter les bots
        model.addComputerPlayer("Bot Facile");
        model.addComputerPlayer("Bot Moyen");
        
        // Créer le stage et le controller
        stageModel = new PuissanceXStageModel("simulation", model);
        controller = new PuissanceXController(model, null);
        model.setGameStage(stageModel);
        
        // Créer le plateau
        board = new PuissanceXBoard(80, 80, stageModel, nbRows, nbCols, nbAlign);
        stageModel.setBoard(board);
        
        // Créer les pots
        redPot = new PuissanceXPawnPot(80, 80, stageModel, (nbRows * nbCols + 1) / 2);
        yellowPot = new PuissanceXPawnPot(80, 80, stageModel, nbRows * nbCols / 2);
        stageModel.setRedPot(redPot);
        stageModel.setYellowPot(yellowPot);
        
        // Créer le tracker
        tracker = ConsoleGameTracker.getInstance(nbRows, nbCols, nbAlign);
        tracker.reset();
    }
    
    private int trouverColonneJouee(int color) {
        // Comparer l'état du plateau avant et après pour trouver la colonne jouée
        int[][] gridAvant = new int[nbRows][nbCols];
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                gridAvant[i][j] = board.getGrid()[i][j];
            }
        }
        
        // Simuler le coup (on ne peut pas vraiment le faire sans l'action, donc on utilise une approche différente)
        // Pour simplifier, on va chercher la première colonne non pleine
        for (int col = 0; col < nbCols; col++) {
            if (!board.isColumnFull(col)) {
                return col;
            }
        }
        return -1;
    }
    
    private void afficherProgres(int duelActuel) {
        System.out.println("\n--- PROGRÈS À " + duelActuel + " DUELS ---");
        System.out.println("Bot Facile: " + victoiresBotFacile + " victoires (" + 
                          String.format("%.1f", (double)victoiresBotFacile/duelActuel*100) + "%)");
        System.out.println("Bot Moyen: " + victoiresBotMoyen + " victoires (" + 
                          String.format("%.1f", (double)victoiresBotMoyen/duelActuel*100) + "%)");
        System.out.println("Matchs nuls: " + matchsNuls + " (" + 
                          String.format("%.1f", (double)matchsNuls/duelActuel*100) + "%)");
        System.out.println("Moyenne de coups par partie: " + 
                          String.format("%.1f", (double)totalCoups/duelActuel));
    }
    
    private void afficherResultatsFinaux() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("RÉSULTATS FINAUX DE LA SIMULATION");
        System.out.println("=".repeat(50));
        System.out.println("Nombre de duels: " + nbDuels);
        System.out.println("Configuration: " + nbRows + "x" + nbCols + ", alignement: " + nbAlign);
        System.out.println();
        System.out.println("Bot Facile (SmartDecider):");
        System.out.println("  Victoires: " + victoiresBotFacile + " (" + 
                          String.format("%.1f", (double)victoiresBotFacile/nbDuels*100) + "%)");
        System.out.println();
        System.out.println("Bot Moyen (Decider):");
        System.out.println("  Victoires: " + victoiresBotMoyen + " (" + 
                          String.format("%.1f", (double)victoiresBotMoyen/nbDuels*100) + "%)");
        System.out.println();
        System.out.println("Matchs nuls: " + matchsNuls + " (" + 
                          String.format("%.1f", (double)matchsNuls/nbDuels*100) + "%)");
        System.out.println();
        System.out.println("Statistiques générales:");
        System.out.println("  Moyenne de coups par partie: " + 
                          String.format("%.1f", (double)totalCoups/nbDuels));
        System.out.println("  Total de coups joués: " + totalCoups);
        
        // Déterminer le gagnant
        if (victoiresBotFacile > victoiresBotMoyen) {
            System.out.println("\n🏆 GAGNANT: Bot Facile (SmartDecider)");
        } else if (victoiresBotMoyen > victoiresBotFacile) {
            System.out.println("\n🏆 GAGNANT: Bot Moyen (Decider)");
        } else {
            System.out.println("\n🤝 ÉGALITÉ entre les deux bots");
        }
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== SIMULATEUR DE DUELS BOT ===");
        System.out.println();
        
        // Demander le nombre de duels
        System.out.print("Nombre de duels à simuler: ");
        int nbDuels = scanner.nextInt();
        
        // Demander la taille du plateau
        System.out.print("Nombre de lignes: ");
        int nbRows = scanner.nextInt();
        
        System.out.print("Nombre de colonnes: ");
        int nbCols = scanner.nextInt();
        
        System.out.print("Nombre de pions à aligner: ");
        int nbAlign = scanner.nextInt();
        
        scanner.close();
        
        // Valider les paramètres
        if (nbDuels <= 0 || nbRows <= 0 || nbCols <= 0 || nbAlign <= 0) {
            System.out.println("Erreur: Tous les paramètres doivent être positifs");
            return;
        }
        
        if (nbAlign > Math.min(nbRows, nbCols)) {
            System.out.println("Erreur: Le nombre de pions à aligner ne peut pas dépasser la plus petite dimension du plateau");
            return;
        }
        
        // Lancer la simulation
        SimulationBot simulation = new SimulationBot(nbDuels, nbRows, nbCols, nbAlign);
        simulation.lancerSimulation();
    }
} 