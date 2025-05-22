package model;

import boardifier.control.Logger;
import boardifier.model.GameStageModel;
import boardifier.model.ContainerElement;
import boardifier.model.GameElement;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

/**
 * Hole main board represent the element where pawns are put when played
 * Thus, a simple ContainerElement with dimensions x dimensions grid is needed.
 * Nevertheless, in order to "simplify" the work for the controller part,
 * this class also contains method to determine all the valid cells to put a
 * pawn with a given value.
 */
public class HoleBoard extends ContainerElement {
    private static int rows = 6; // Valeur par défaut pour Puissance 4
    private static int cols = 7; // Valeur par défaut pour Puissance 4
    private static int tokensToWin = 4; // Valeur par défaut pour Puissance 4

    public static void setDimensions(int nbRows, int nbCols) {
        if (nbRows < 4 || nbRows > 10) {
            throw new IllegalArgumentException("Le nombre de lignes doit être entre 4 et 10");
        }
        if (nbCols < 4 || nbCols > 10) {
            throw new IllegalArgumentException("Le nombre de colonnes doit être entre 4 et 10");
        }
        rows = nbRows;
        cols = nbCols;
    }

    public static void setTokensToWin(int nbTokens) {
        if (nbTokens < 3 || nbTokens > Math.min(rows, cols)) {
            throw new IllegalArgumentException("Le nombre de jetons doit être entre 3 et " + Math.min(rows, cols));
        }
        tokensToWin = nbTokens;
    }

    public static int getRows() {
        return rows;
    }

    public static int getCols() {
        return cols;
    }

    public static int getTokensToWin() {
        return tokensToWin;
    }

    public HoleBoard(int x, int y, GameStageModel gameStageModel) {
        // call the super-constructor to create a rows x cols grid, named "holeboard", and in x,y in space
        super("holeboard", x, y, rows, cols, gameStageModel);
    }

    public void setValidCells(int number) {
        Logger.debug("called",this);
        resetReachableCells(false);
        List<Point> valid = computeValidCells(number);
        if (valid != null) {
            for(Point p : valid) {
                reachableCells[p.y][p.x] = true;
            }
        }
    }

    public List<Point> computeValidCells(int number) {
        List<Point> lst = new ArrayList<>();
        
        // Pour chaque colonne
        for (int col = 0; col < cols; col++) {
            // Trouver la première ligne vide en partant du bas
            for (int row = rows - 1; row >= 0; row--) {
                if (isEmptyAt(row, col)) {
                    lst.add(new Point(col, row));
                    break;
                }
            }
        }
        return lst;
    }

    public boolean isColumnFull(int col) {
        return !isEmptyAt(0, col);
    }

    public int getFirstEmptyRow(int col) {
        for (int row = rows - 1; row >= 0; row--) {
            if (isEmptyAt(row, col)) {
                return row;
            }
        }
        return -1;
    }

    public boolean isBoardFull() {
        for (int col = 0; col < cols; col++) {
            if (!isColumnFull(col)) {
                return false;
            }
        }
        return true;
    }

    public boolean checkWin(int row, int col, int playerColor) {
        // Vérifier horizontalement
        int count = 0;
        for (int j = 0; j < cols; j++) {
            GameElement element = getElement(row, j);
            if (element != null && ((Pawn)element).getColor() == playerColor) {
                count++;
                if (count == tokensToWin) return true;
            } else {
                count = 0;
            }
        }

        // Vérifier verticalement
        count = 0;
        for (int i = 0; i < rows; i++) {
            GameElement element = getElement(i, col);
            if (element != null && ((Pawn)element).getColor() == playerColor) {
                count++;
                if (count == tokensToWin) return true;
            } else {
                count = 0;
            }
        }

        // Vérifier diagonalement (haut gauche vers bas droite)
        count = 0;
        int startRow = row - Math.min(row, col);
        int startCol = col - Math.min(row, col);
        while (startRow < rows && startCol < cols) {
            GameElement element = getElement(startRow, startCol);
            if (element != null && ((Pawn)element).getColor() == playerColor) {
                count++;
                if (count == tokensToWin) return true;
            } else {
                count = 0;
            }
            startRow++;
            startCol++;
        }

        // Vérifier diagonalement (haut droite vers bas gauche)
        count = 0;
        startRow = row - Math.min(row, cols - 1 - col);
        startCol = col + Math.min(row, cols - 1 - col);
        while (startRow < rows && startCol >= 0) {
            GameElement element = getElement(startRow, startCol);
            if (element != null && ((Pawn)element).getColor() == playerColor) {
                count++;
                if (count == tokensToWin) return true;
            } else {
                count = 0;
            }
            startRow++;
            startCol--;
        }

        return false;
    }
}
