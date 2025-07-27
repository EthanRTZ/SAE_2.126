package control;

import boardifier.control.Controller;
import boardifier.control.Decider;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import java.util.ArrayList;
import java.util.List;
import model.Pawn;
import model.PuissanceXBoard;
import model.PuissanceXPawnPot;
import model.PuissanceXStageModel;

public class PuissanceXHardDecider extends Decider {
    private static final int WIN_SCORE = 1000000;
    private static final int NEAR_WIN_SCORE = 100000;
    private static final int BLOCK_SCORE = 50000;
    private static final int FOUR_IN_ROW_SCORE = 10000;
    private static final int THREE_IN_ROW_SCORE = 1000;
    private static final int TWO_IN_ROW_SCORE = 100;
    private static final int CENTER_COLUMN_BONUS = 50;
    private static final int DEPTH = 6;  // Profondeur de recherche plus importante
    private PuissanceXStageModel stage;

    public PuissanceXHardDecider(Model model, Controller control) {
        super(model, control);
    }

    public void setStage(PuissanceXStageModel stage) {
        this.stage = stage;
    }

    @Override
    public ActionList decide() {
        PuissanceXBoard board = stage.getBoard();
        if (board == null) {
            System.out.println("The board is not yet initialized, waiting...");
            try {
                Thread.sleep(1000);
                board = stage.getBoard();
                if (board == null) {
                    System.out.println("The board is still null after waiting");
                    return null;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        int currentPlayer = model.getIdPlayer();
        int color = currentPlayer == 0 ? Pawn.PAWN_RED : Pawn.PAWN_YELLOW;

        // Chercher les colonnes jouables et les ordonner intelligemment
        List<Integer> orderedCols = getOrderedColumns(board);

        // Vérifier d'abord si on peut gagner immédiatement
        for (int col : orderedCols) {
            if (!board.isColumnFull(col)) {
                int row = board.getFirstEmptyRow(col);
                board.getGrid()[row][col] = color;
                if (board.checkWin(row, col, color)) {
                    board.getGrid()[row][col] = -1;
                    return playMove(col, color);
                }
                board.getGrid()[row][col] = -1;
            }
        }

        // Vérifier si l'adversaire peut gagner au prochain tour
        int opponentColor = color == Pawn.PAWN_RED ? Pawn.PAWN_YELLOW : Pawn.PAWN_RED;
        for (int col : orderedCols) {
            if (!board.isColumnFull(col)) {
                int row = board.getFirstEmptyRow(col);
                board.getGrid()[row][col] = opponentColor;
                if (board.checkWin(row, col, opponentColor)) {
                    board.getGrid()[row][col] = -1;
                    return playMove(col, color);
                }
                board.getGrid()[row][col] = -1;
            }
        }

        // Utiliser l'algorithme minimax avec élagage alpha-beta
        int bestScore = Integer.MIN_VALUE;
        int bestCol = -1;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (int col : orderedCols) {
            if (!board.isColumnFull(col)) {
                int row = board.getFirstEmptyRow(col);
                board.getGrid()[row][col] = color;
                int score = alphaBeta(board, DEPTH, alpha, beta, false, color, opponentColor);
                board.getGrid()[row][col] = -1;

                if (score > bestScore) {
                    bestScore = score;
                    bestCol = col;
                }
                alpha = Math.max(alpha, bestScore);
            }
        }

        // Si aucun coup valide n'est trouvé, jouer dans la colonne centrale ou aléatoirement
        if (bestCol == -1) {
            int centerCol = board.getNbCols() / 2;
            if (!board.isColumnFull(centerCol)) {
                bestCol = centerCol;
            } else {
                for (int col : orderedCols) {
                    if (!board.isColumnFull(col)) {
                        bestCol = col;
                        break;
                    }
                }
            }
        }

        return playMove(bestCol, color);
    }

    // Méthode pour ordonner les colonnes selon leur intérêt stratégique
    private List<Integer> getOrderedColumns(PuissanceXBoard board) {
        List<Integer> columns = new ArrayList<>();

        // Privilégier la colonne centrale et celles proches du centre
        int centerCol = board.getNbCols() / 2;
        columns.add(centerCol);

        int offset = 1;
        while (centerCol - offset >= 0 || centerCol + offset < board.getNbCols()) {
            if (centerCol - offset >= 0) {
                columns.add(centerCol - offset);
            }
            if (centerCol + offset < board.getNbCols()) {
                columns.add(centerCol + offset);
            }
            offset++;
        }

        return columns;
    }

    private ActionList playMove(int col, int color) {
        PuissanceXBoard board = stage.getBoard();
        int row = board.getFirstEmptyRow(col);
        board.getGrid()[row][col] = color;

        PuissanceXPawnPot pot = color == Pawn.PAWN_RED ? stage.getRedPot() : stage.getYellowPot();
        pot.removeElement(null);

        return new ActionList();
    }

    private int alphaBeta(PuissanceXBoard board, int depth, int alpha, int beta, boolean isMaximizing, int playerColor, int opponentColor) {
        // Vérifier s'il y a une victoire, une défaite ou si la profondeur maximale est atteinte
        int winner = checkForWinner(board, playerColor, opponentColor);
        if (winner == playerColor) {
            return WIN_SCORE + depth;  // Plus la victoire est rapide, mieux c'est
        } else if (winner == opponentColor) {
            return -WIN_SCORE - depth;
        } else if (isBoardFull(board) || depth == 0) {
            return evaluateBoard(board, playerColor, opponentColor);
        }

        List<Integer> orderedCols = getOrderedColumns(board);

        if (isMaximizing) {
            int maxScore = Integer.MIN_VALUE;
            for (int col : orderedCols) {
                if (!board.isColumnFull(col)) {
                    int row = board.getFirstEmptyRow(col);
                    board.getGrid()[row][col] = playerColor;
                    int score = alphaBeta(board, depth - 1, alpha, beta, false, playerColor, opponentColor);
                    board.getGrid()[row][col] = -1;

                    maxScore = Math.max(maxScore, score);
                    alpha = Math.max(alpha, maxScore);

                    if (beta <= alpha) {
                        break;  // Élagage beta
                    }
                }
            }
            return maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (int col : orderedCols) {
                if (!board.isColumnFull(col)) {
                    int row = board.getFirstEmptyRow(col);
                    board.getGrid()[row][col] = opponentColor;
                    int score = alphaBeta(board, depth - 1, alpha, beta, true, playerColor, opponentColor);
                    board.getGrid()[row][col] = -1;

                    minScore = Math.min(minScore, score);
                    beta = Math.min(beta, minScore);

                    if (beta <= alpha) {
                        break;  // Élagage alpha
                    }
                }
            }
            return minScore;
        }
    }

    private int checkForWinner(PuissanceXBoard board, int playerColor, int opponentColor) {
        // Chercher une victoire pour chaque joueur
        for (int col = 0; col < board.getNbCols(); col++) {
            for (int row = 0; row < board.getNbRows(); row++) {
                if (board.getGrid()[row][col] == playerColor && board.checkWin(row, col, playerColor)) {
                    return playerColor;
                }
                if (board.getGrid()[row][col] == opponentColor && board.checkWin(row, col, opponentColor)) {
                    return opponentColor;
                }
            }
        }
        return -1; // Pas de vainqueur
    }

    private boolean isBoardFull(PuissanceXBoard board) {
        for (int col = 0; col < board.getNbCols(); col++) {
            if (!board.isColumnFull(col)) {
                return false;
            }
        }
        return true;
    }

    private int evaluateBoard(PuissanceXBoard board, int playerColor, int opponentColor) {
        int score = 0;
        int[][] grid = board.getGrid();
        int nbAlign = board.getNbAlign();

        // Évaluer les séquences horizontales, verticales et diagonales
        score += evaluateLines(board, grid, playerColor, opponentColor, nbAlign);

        // Bonus pour les pions au centre
        int centerCol = board.getNbCols() / 2;
        for (int row = 0; row < board.getNbRows(); row++) {
            if (grid[row][centerCol] == playerColor) {
                score += CENTER_COLUMN_BONUS;
            } else if (grid[row][centerCol] == opponentColor) {
                score -= CENTER_COLUMN_BONUS;
            }
        }

        // Bonus pour les positions qui permettent de connecter des pions
        score += evaluateConnectingMoves(board, grid, playerColor, opponentColor);

        return score;
    }

    private int evaluateLines(PuissanceXBoard board, int[][] grid, int playerColor, int opponentColor, int nbAlign) {
        int score = 0;

        // Évaluer les lignes horizontales
        for (int row = 0; row < board.getNbRows(); row++) {
            for (int col = 0; col < board.getNbCols() - nbAlign + 1; col++) {
                score += evaluateWindow(grid, row, col, 0, 1, nbAlign, playerColor, opponentColor);
            }
        }

        // Évaluer les lignes verticales
        for (int col = 0; col < board.getNbCols(); col++) {
            for (int row = 0; row < board.getNbRows() - nbAlign + 1; row++) {
                score += evaluateWindow(grid, row, col, 1, 0, nbAlign, playerColor, opponentColor);
            }
        }

        // Évaluer les diagonales ascendantes
        for (int row = nbAlign - 1; row < board.getNbRows(); row++) {
            for (int col = 0; col < board.getNbCols() - nbAlign + 1; col++) {
                score += evaluateWindow(grid, row, col, -1, 1, nbAlign, playerColor, opponentColor);
            }
        }

        // Évaluer les diagonales descendantes
        for (int row = 0; row < board.getNbRows() - nbAlign + 1; row++) {
            for (int col = 0; col < board.getNbCols() - nbAlign + 1; col++) {
                score += evaluateWindow(grid, row, col, 1, 1, nbAlign, playerColor, opponentColor);
            }
        }

        return score;
    }

    private int evaluateWindow(int[][] grid, int startRow, int startCol, int rowInc, int colInc, int windowLength, int playerColor, int opponentColor) {
        int score = 0;
        int playerCount = 0;
        int opponentCount = 0;
        int emptyCount = 0;

        // Analyser la fenêtre (sous-séquence) de pions
        for (int i = 0; i < windowLength; i++) {
            int row = startRow + i * rowInc;
            int col = startCol + i * colInc;

            if (grid[row][col] == playerColor) {
                playerCount++;
            } else if (grid[row][col] == opponentColor) {
                opponentCount++;
            } else {
                emptyCount++;
            }
        }

        // Évaluer la fenêtre
        if (playerCount > 0 && opponentCount == 0) {
            // Le joueur a des pions et l'adversaire aucun
            if (playerCount == windowLength - 1 && emptyCount == 1) {
                // Une séquence presque gagnante
                score += NEAR_WIN_SCORE;
            } else if (playerCount == windowLength - 2 && emptyCount == 2) {
                // Trois pions alignés avec deux espaces vides
                score += THREE_IN_ROW_SCORE;
            } else if (playerCount == windowLength - 3 && emptyCount == 3) {
                // Deux pions alignés avec trois espaces vides
                score += TWO_IN_ROW_SCORE;
            }
        } else if (opponentCount > 0 && playerCount == 0) {
            // L'adversaire a des pions et le joueur aucun
            if (opponentCount == windowLength - 1 && emptyCount == 1) {
                // Bloquer une séquence presque gagnante de l'adversaire
                score -= BLOCK_SCORE;
            } else if (opponentCount == windowLength - 2 && emptyCount == 2) {
                // Bloquer trois pions alignés de l'adversaire
                score -= THREE_IN_ROW_SCORE;
            } else if (opponentCount == windowLength - 3 && emptyCount == 3) {
                // Bloquer deux pions alignés de l'adversaire
                score -= TWO_IN_ROW_SCORE;
            }
        }

        return score;
    }

    private int evaluateConnectingMoves(PuissanceXBoard board, int[][] grid, int playerColor, int opponentColor) {
        int score = 0;

        // Trouver les positions qui permettent de créer plusieurs menaces en même temps
        for (int col = 0; col < board.getNbCols(); col++) {
            if (!board.isColumnFull(col)) {
                int row = board.getFirstEmptyRow(col);

                // Simuler un coup
                grid[row][col] = playerColor;

                // Compter les alignements possibles après ce coup
                int threats = countThreats(board, grid, row, col, playerColor);
                if (threats >= 2) {
                    // Position avec menace double ou plus
                    score += threats * 500;
                }

                // Annuler le coup simulé
                grid[row][col] = -1;
            }
        }

        return score;
    }

    private int countThreats(PuissanceXBoard board, int[][] grid, int row, int col, int playerColor) {
        int threats = 0;
        int nbAlign = board.getNbAlign() - 1; // On cherche nbAlign-1 pions alignés

        // Vérifier les menaces dans toutes les directions
        int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}}; // Horizontal, vertical, 2 diagonales

        for (int[] dir : directions) {
            int rowDir = dir[0];
            int colDir = dir[1];

            // Compter les pions dans la direction positive
            int count = 0;
            for (int i = 1; i < nbAlign; i++) {
                int r = row + i * rowDir;
                int c = col + i * colDir;
                if (r >= 0 && r < board.getNbRows() && c >= 0 && c < board.getNbCols() && grid[r][c] == playerColor) {
                    count++;
                } else {
                    break;
                }
            }

            // Compter les pions dans la direction négative
            for (int i = 1; i < nbAlign; i++) {
                int r = row - i * rowDir;
                int c = col - i * colDir;
                if (r >= 0 && r < board.getNbRows() && c >= 0 && c < board.getNbCols() && grid[r][c] == playerColor) {
                    count++;
                } else {
                    break;
                }
            }

            // Si on a nbAlign-1 pions alignés, c'est une menace
            if (count >= nbAlign - 1) {
                threats++;
            }
        }

        return threats;
    }
}
