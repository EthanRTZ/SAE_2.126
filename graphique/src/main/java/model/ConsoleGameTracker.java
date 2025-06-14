package model;

public class ConsoleGameTracker {
    private int[][] grid;
    private int nbRows;
    private int nbCols;
    private int nbAlign;
    private static ConsoleGameTracker instance;

    private ConsoleGameTracker(int rows, int cols, int align) {
        this.nbRows = rows;
        this.nbCols = cols;
        this.nbAlign = align;
        this.grid = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = -1;
            }
        }
    }

    public static ConsoleGameTracker getInstance(int rows, int cols, int align) {
        if (instance == null) {
            instance = new ConsoleGameTracker(rows, cols, align);
        }
        return instance;
    }

    public void reset() {
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                grid[i][j] = -1;
            }
        }
    }

    // Completely reset the instance, to be used when closing the game
    public static void resetInstance() {
        instance = null;
    }

    public void updateGrid(int[][] newGrid) {
        for (int i = 0; i < nbRows; i++) {
            System.arraycopy(newGrid[i], 0, grid[i], 0, nbCols);
        }
        printGrid();
    }

    private void printGrid() {
        System.out.println("\nCurrent board state (Console Tracker):");
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                if (grid[i][j] == -1) {
                    System.out.print(". ");
                } else if (grid[i][j] == Pawn.PAWN_RED) {
                    System.out.print("R ");
                } else {
                    System.out.print("Y ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public boolean checkWin(int row, int col, int playerColor) {
        // Display the coordinates of the last played move
        System.out.println("Victory check for move: row=" + row + ", column=" + col + ", color=" + (playerColor == Pawn.PAWN_YELLOW ? "YELLOW" : "RED"));

        // Horizontal verification
        int count = 1;
        // Left
        for (int j = col - 1; j >= 0 && grid[row][j] == playerColor; j--) {
            count++;
            System.out.println("Horizontal left: " + count);
        }
        // Right
        for (int j = col + 1; j < nbCols && grid[row][j] == playerColor; j++) {
            count++;
            System.out.println("Horizontal right: " + count);
        }
        if (count >= nbAlign) {
            System.out.println("Horizontal victory detected for player " + (playerColor == Pawn.PAWN_YELLOW ? "YELLOW" : "RED"));
            return true;
        }

        // Vertical verification
        count = 1;
        // Top
        for (int i = row - 1; i >= 0 && grid[i][col] == playerColor; i--) {
            count++;
            System.out.println("Vertical top: " + count);
        }
        // Bottom
        for (int i = row + 1; i < nbRows && grid[i][col] == playerColor; i++) {
            count++;
            System.out.println("Vertical bottom: " + count);
        }
        if (count >= nbAlign) {
            System.out.println("Vertical victory detected for player " + (playerColor == Pawn.PAWN_YELLOW ? "YELLOW" : "RED"));
            return true;
        }

        // Diagonal verification (top-left to bottom-right)
        count = 1;
        // Top-left
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0 && grid[i][j] == playerColor; i--, j--) {
            count++;
            System.out.println("Diagonal \\ top-left: " + count);
        }
        // Bottom-right
        for (int i = row + 1, j = col + 1; i < nbRows && j < nbCols && grid[i][j] == playerColor; i++, j++) {
            count++;
            System.out.println("Diagonal \\ bottom-right: " + count);
        }
        if (count >= nbAlign) {
            System.out.println("Diagonal \\ victory detected for player " + (playerColor == Pawn.PAWN_YELLOW ? "YELLOW" : "RED"));
            return true;
        }

        // Diagonal verification (top-right to bottom-left)
        count = 1;
        // Top-right
        for (int i = row - 1, j = col + 1; i >= 0 && j < nbCols && grid[i][j] == playerColor; i--, j++) {
            count++;
            System.out.println("Diagonal / top-right: " + count);
        }
        // Bottom-left
        for (int i = row + 1, j = col - 1; i < nbRows && j >= 0 && grid[i][j] == playerColor; i++, j--) {
            count++;
            System.out.println("Diagonal / bottom-left: " + count);
        }
        if (count >= nbAlign) {
            System.out.println("Diagonal / victory detected for player " + (playerColor == Pawn.PAWN_YELLOW ? "YELLOW" : "RED"));
            return true;
        }

        // Additional verification for all possible diagonals
        for (int startRow = 0; startRow < nbRows; startRow++) {
            for (int startCol = 0; startCol < nbCols; startCol++) {
                if (grid[startRow][startCol] == playerColor) {
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
                            System.out.println("Diagonal \\ victory detected (additional verification) for player " + 
                                             (playerColor == Pawn.PAWN_YELLOW ? "YELLOW" : "RED"));
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
                            System.out.println("Diagonal / victory detected (additional verification) for player " + 
                                             (playerColor == Pawn.PAWN_YELLOW ? "YELLOW" : "RED"));
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
} 