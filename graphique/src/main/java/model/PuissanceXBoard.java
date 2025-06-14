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

    // Check if a column is full
    public boolean isColumnFull(int col) {
        return grid[0][col] != -1;
    }

    // Find the first empty row in a column
    public int getFirstEmptyRow(int col) {
        for (int i = nbRows - 1; i >= 0; i--) {
            if (grid[i][col] == -1) {
                return i;
            }
        }
        return -1;
    }

    // Check if the board is full
    public boolean isBoardFull() {
        for (int j = 0; j < nbCols; j++) {
            if (!isColumnFull(j)) {
                return false;
            }
        }
        return true;
    }

    // Check if a player has won
    public boolean checkWin(int row, int col, int playerColor) {
        System.out.println("PuissanceXBoard - Victory check for: row=" + row + ", column=" + col + ", color=" + (playerColor == Pawn.PAWN_YELLOW ? "YELLOW" : "RED"));

        // Check horizontally
        int count = 0;
        // Check to the left of the placed pawn
        for (int j = col; j >= 0 && grid[row][j] == playerColor; j--) {
            count++;
        }
        // Check to the right of the placed pawn (without recounting the placed pawn)
        for (int j = col + 1; j < nbCols && grid[row][j] == playerColor; j++) {
            count++;
        }
        if (count >= nbAlign) {
            System.out.println("PuissanceXBoard - Horizontal victory detected!");
            return true;
        }

        // Check vertically
        count = 0;
        // Check above the placed pawn
        for (int i = row; i >= 0 && grid[i][col] == playerColor; i--) {
            count++;
        }
        // Check below the placed pawn (without recounting the placed pawn)
        for (int i = row + 1; i < nbRows && grid[i][col] == playerColor; i++) {
            count++;
        }
        if (count >= nbAlign) {
            System.out.println("PuissanceXBoard - Vertical victory detected!");
            return true;
        }

        // Check diagonally (top left to bottom right)
        count = 0;
        // Check to the top left of the placed pawn
        for (int i = row, j = col; i >= 0 && j >= 0 && grid[i][j] == playerColor; i--, j--) {
            count++;
        }
        // Check to the bottom right of the placed pawn (without recounting the placed pawn)
        for (int i = row + 1, j = col + 1; i < nbRows && j < nbCols && grid[i][j] == playerColor; i++, j++) {
            count++;
        }
        if (count >= nbAlign) {
            System.out.println("PuissanceXBoard - Diagonal \\ victory detected!");
            return true;
        }

        // Check diagonally (top right to bottom left)
        count = 0;
        // Check to the top right of the placed pawn
        for (int i = row, j = col; i >= 0 && j < nbCols && grid[i][j] == playerColor; i--, j++) {
            count++;
        }
        // Check to the bottom left of the placed pawn (without recounting the placed pawn)
        for (int i = row + 1, j = col - 1; i < nbRows && j >= 0 && grid[i][j] == playerColor; i++, j--) {
            count++;
        }
        if (count >= nbAlign) {
            System.out.println("PuissanceXBoard - Diagonal / victory detected!");
            return true;
        }

        // Additional verification in all directions
        for (int startRow = 0; startRow < nbRows; startRow++) {
            for (int startCol = 0; startCol < nbCols; startCol++) {
                if (grid[startRow][startCol] == playerColor) {
                    // Check horizontally
                    if (startCol <= nbCols - nbAlign) {
                        count = 1;
                        for (int k = 1; k < nbAlign; k++) {
                            if (grid[startRow][startCol + k] == playerColor) {
                                count++;
                            } else {
                                break;
                            }
                        }
                        if (count >= nbAlign) {
                            System.out.println("PuissanceXBoard - Horizontal victory detected (additional verification)!");
                            return true;
                        }
                    }
                    
                    // Check vertically
                    if (startRow <= nbRows - nbAlign) {
                        count = 1;
                        for (int k = 1; k < nbAlign; k++) {
                            if (grid[startRow + k][startCol] == playerColor) {
                                count++;
                            } else {
                                break;
                            }
                        }
                        if (count >= nbAlign) {
                            System.out.println("PuissanceXBoard - Vertical victory detected (additional verification)!");
                            return true;
                        }
                    }
                    
                    // Check descending diagonal
                    if (startRow <= nbRows - nbAlign && startCol <= nbCols - nbAlign) {
                        count = 1;
                        for (int k = 1; k < nbAlign; k++) {
                            if (grid[startRow + k][startCol + k] == playerColor) {
                                count++;
                            } else {
                                break;
                            }
                        }
                        if (count >= nbAlign) {
                            System.out.println("PuissanceXBoard - Diagonal \\ victory detected (additional verification)!");
                            return true;
                        }
                    }
                    
                    // Check ascending diagonal
                    if (startRow >= nbAlign - 1 && startCol <= nbCols - nbAlign) {
                        count = 1;
                        for (int k = 1; k < nbAlign; k++) {
                            if (grid[startRow - k][startCol + k] == playerColor) {
                                count++;
                            } else {
                                break;
                            }
                        }
                        if (count >= nbAlign) {
                            System.out.println("PuissanceXBoard - Diagonal / victory detected (additional verification)!");
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    // Reset the board
    public void clear() {
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                grid[i][j] = -1;
            }
        }
    }
} 