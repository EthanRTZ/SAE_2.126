package model;

import boardifier.model.ContainerElement;
import boardifier.model.GameElement;
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

    @Override
    public void addElement(GameElement element, int row, int col) {
        grid[row][col] = ((Pawn) element).getColor();
        // Appeler super après la mise à jour de la grille
        super.addElement(element, row, col);
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
        // Vérifier à gauche du pion placé
        for (int j = col; j >= 0 && grid[row][j] == playerColor; j--) {
            count++;
        }
        // Vérifier à droite du pion placé (sans recompter le pion placé)
        for (int j = col + 1; j < nbCols && grid[row][j] == playerColor; j++) {
            count++;
        }
        if (count >= nbAlign) {
            return true;
        }

        // Vérifier verticalement
        count = 0;
        // Vérifier en haut du pion placé
        for (int i = row; i >= 0 && grid[i][col] == playerColor; i--) {
            count++;
        }
        // Vérifier en bas du pion placé (sans recompter le pion placé)
        for (int i = row + 1; i < nbRows && grid[i][col] == playerColor; i++) {
            count++;
        }
        if (count >= nbAlign) {
            return true;
        }

        // Vérifier diagonalement (haut gauche vers bas droite)
        count = 0;
        // Vérifier en haut à gauche du pion placé
        for (int i = row, j = col; i >= 0 && j >= 0 && grid[i][j] == playerColor; i--, j--) {
            count++;
        }
        // Vérifier en bas à droite du pion placé (sans recompter le pion placé)
        for (int i = row + 1, j = col + 1; i < nbRows && j < nbCols && grid[i][j] == playerColor; i++, j++) {
            count++;
        }
        if (count >= nbAlign) {
            return true;
        }

        // Vérifier diagonalement (haut droite vers bas gauche)
        count = 0;
        // Vérifier en haut à droite du pion placé
        for (int i = row, j = col; i >= 0 && j < nbCols && grid[i][j] == playerColor; i--, j++) {
            count++;
        }
        // Vérifier en bas à gauche du pion placé (sans recompter le pion placé)
        for (int i = row + 1, j = col - 1; i < nbRows && j >= 0 && grid[i][j] == playerColor; i++, j--) {
            count++;
        }
        if (count >= nbAlign) {
            return true;
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