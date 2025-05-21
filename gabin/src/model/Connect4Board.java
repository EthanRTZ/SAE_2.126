package model;

import boardifier.model.GameStageModel;
import boardifier.model.ContainerElement;
import boardifier.model.Model;

public class Connect4Board extends ContainerElement {
    private int nbCols;
    private int nbRows;
    private int nbAlign;
    private int[][] grid;

    public Connect4Board(int x, int y, GameStageModel gameStageModel) {
        super("connect4board", x, y, gameStageModel.getModel().getGameParameter("nbRows"), gameStageModel.getModel().getGameParameter("nbCols"), gameStageModel);
        nbCols = gameStageModel.getModel().getGameParameter("nbCols");
        nbRows = gameStageModel.getModel().getGameParameter("nbRows");
        nbAlign = gameStageModel.getModel().getGameParameter("nbAlign");
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
        for (int j = 0; j < nbCols; j++) {
            if (grid[row][j] == playerColor) {
                count++;
                if (count == nbAlign) return true;
            } else {
                count = 0;
            }
        }

        // Vérifier verticalement
        count = 0;
        for (int i = 0; i < nbRows; i++) {
            if (grid[i][col] == playerColor) {
                count++;
                if (count == nbAlign) return true;
            } else {
                count = 0;
            }
        }

        // Vérifier diagonalement (haut gauche vers bas droite)
        count = 0;
        int startRow = row - Math.min(row, col);
        int startCol = col - Math.min(row, col);
        while (startRow < nbRows && startCol < nbCols) {
            if (grid[startRow][startCol] == playerColor) {
                count++;
                if (count == nbAlign) return true;
            } else {
                count = 0;
            }
            startRow++;
            startCol++;
        }

        // Vérifier diagonalement (haut droite vers bas gauche)
        count = 0;
        startRow = row - Math.min(row, nbCols - 1 - col);
        startCol = col + Math.min(row, nbCols - 1 - col);
        while (startRow < nbRows && startCol >= 0) {
            if (grid[startRow][startCol] == playerColor) {
                count++;
                if (count == nbAlign) return true;
            } else {
                count = 0;
            }
            startRow++;
            startCol--;
        }

        return false;
    }
} 