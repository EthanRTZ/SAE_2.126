package model;

import boardifier.model.ContainerElement;
import boardifier.model.GameStageModel;

public class PuissanceXBoard extends ContainerElement {
    private int nbCols;
    private int nbRows;
    private int nbAlign;
    private int[][] grid;

    public PuissanceXBoard(int x, int y, GameStageModel gameStageModel, int rows, int cols, int align) {
        super("PuissanceXboard", x, y, rows, cols, gameStageModel);
        nbCols = cols;
        nbRows = rows;
        nbAlign = align;
        grid = new int[nbRows][nbCols];
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                grid[i][j] = -1;
            }
        }
    }

    public int getNbCols() {
        return nbCols;
    }

    public int getNbRows() {
        return nbRows;
    }

    public int getNbAlign() {
        return nbAlign;
    }

    public int[][] getGrid() {
        return grid;
    }

    // Vérifie si une colonne est pleine
    public boolean isColumnFull(int col) {
        return grid[0][col] != -1;
    }

    // Trouve la première ligne vide dans une colonne
    public int getFirstEmptyRow(int col) {
        for (int i = nbRows - 1; i >= 0; i--) {
            if (grid[i][col] == -1) {
                return i;
            }
        }
        return -1;
    }

    // Vérifie si le plateau est plein
    public boolean isBoardFull() {
        for (int j = 0; j < nbCols; j++) {
            if (!isColumnFull(j)) {
                return false;
            }
        }
        return true;
    }

    // Vérifie si un joueur a gagné
    public boolean checkWin(int row, int col, int playerColor) {
        // Vérifier horizontalement
        int count = 0;
        for (int j = Math.max(0, col - 3); j <= Math.min(nbCols - 1, col + 3); j++) {
            if (grid[row][j] == playerColor) {
                count++;
                if (count >= nbAlign) return true;
            } else {
                count = 0;
            }
        }

        // Vérifier verticalement
        count = 0;
        for (int i = Math.max(0, row - 3); i <= Math.min(nbRows - 1, row + 3); i++) {
            if (grid[i][col] == playerColor) {
                count++;
                if (count >= nbAlign) return true;
            } else {
                count = 0;
            }
        }

        // Vérifier diagonalement (haut gauche vers bas droite)
        count = 0;
        for (int i = -3; i <= 3; i++) {
            int r = row + i;
            int c = col + i;
            if (r >= 0 && r < nbRows && c >= 0 && c < nbCols) {
                if (grid[r][c] == playerColor) {
                    count++;
                    if (count >= nbAlign) return true;
                } else {
                    count = 0;
                }
            }
        }

        // Vérifier diagonalement (haut droite vers bas gauche)
        count = 0;
        for (int i = -3; i <= 3; i++) {
            int r = row + i;
            int c = col - i;
            if (r >= 0 && r < nbRows && c >= 0 && c < nbCols) {
                if (grid[r][c] == playerColor) {
                    count++;
                    if (count >= nbAlign) return true;
                } else {
                    count = 0;
                }
            }
        }

        return false;
    }

    // Réinitialise le plateau
    public void clear() {
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                grid[i][j] = -1;
            }
        }
    }
} 