package control;

import boardifier.control.ActionFactory;
import boardifier.control.Controller;
import boardifier.control.Decider;
import boardifier.model.GameElement;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import model.Connect4Board;
import model.Connect4StageModel;
import model.Connect4PawnPot;
import model.Pawn;

import java.util.Calendar;
import java.util.Random;

public class Connect4SmartDecider extends Decider {
    private static final Random random = new Random(Calendar.getInstance().getTimeInMillis());

    public Connect4SmartDecider(Model model, Controller control) {
        super(model, control);
    }

    @Override
    public ActionList decide() {
        // Récupérer les éléments du jeu
        Connect4StageModel stage = (Connect4StageModel)model.getGameStage();
        Connect4Board board = stage.getBoard();
        Connect4PawnPot pot = null;
        GameElement pawn = null;
        int rowDest = 0;
        int colDest = 0;
        
        // Choisir le bon pot de pions selon le joueur
        if (model.getIdPlayer() == 0) {
            pot = stage.getYellowPot();
        } else {
            pot = stage.getRedPot();
        }

        // ÉTAPE 1: Vérifier si on peut gagner
        boolean coupTrouve = false;
        for (int col = 0; col < board.getNbCols(); col++) {
            // Vérifier si la colonne n'est pas pleine
            if (!board.isColumnFull(col)) {
                // Trouver la première ligne vide dans cette colonne
                int row = board.getFirstEmptyRow(col);
                
                // Simuler le coup
                int[][] grid = board.getGrid();
                int couleurJoueur;
                if (model.getIdPlayer() == 0) {
                    couleurJoueur = Pawn.PAWN_BLACK;
                } else {
                    couleurJoueur = Pawn.PAWN_RED;
                }
                grid[row][col] = couleurJoueur;
                
                // Vérifier si ce coup fait gagner
                if (board.checkWin(row, col, couleurJoueur)) {
                    rowDest = row;
                    colDest = col;
                    coupTrouve = true;
                    break;
                }
                
                // Annuler le coup simulé
                grid[row][col] = -1;
            }
        }

        // ÉTAPE 2: Si on ne peut pas gagner, vérifier si l'adversaire peut gagner
        if (!coupTrouve) {
            int couleurAdversaire;
            if (model.getIdPlayer() == 0) {
                couleurAdversaire = Pawn.PAWN_RED;
            } else {
                couleurAdversaire = Pawn.PAWN_BLACK;
            }
            
            for (int col = 0; col < board.getNbCols(); col++) {
                if (!board.isColumnFull(col)) {
                    int row = board.getFirstEmptyRow(col);
                    
                    // Simuler le coup de l'adversaire
                    int[][] grid = board.getGrid();
                    grid[row][col] = couleurAdversaire;
                    
                    // Vérifier si l'adversaire peut gagner
                    if (board.checkWin(row, col, couleurAdversaire)) {
                        rowDest = row;
                        colDest = col;
                        coupTrouve = true;
                        break;
                    }
                    
                    // Annuler le coup simulé
                    grid[row][col] = -1;
                }
            }
        }

        // ÉTAPE 3: Si aucun coup stratégique n'est trouvé, jouer aléatoirement
        if (!coupTrouve) {
            int col;
            do {
                col = random.nextInt(board.getNbCols());
            } while (board.isColumnFull(col));
            rowDest = board.getFirstEmptyRow(col);
            colDest = col;
        }

        // Trouver un pion disponible dans le pot
        for (int i = 0; i < pot.getNbRows(); i++) {
            if (!pot.isEmptyAt(0, i)) {
                pawn = pot.getElement(0, i);
                break;
            }
        }
        
        if (pawn == null) return null;

        // Créer et retourner l'action pour placer le pion
        ActionList actions = ActionFactory.generatePutInContainer(model, pawn, "connect4board", rowDest, colDest);
        actions.setDoEndOfTurn(true);
        return actions;
    }
} 
