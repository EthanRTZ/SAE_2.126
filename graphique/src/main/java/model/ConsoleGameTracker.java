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

    // Réinitialise complètement l'instance, à utiliser lors de la fermeture du jeu
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
        System.out.println("\nÉtat actuel du plateau (Console Tracker):");
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
        // Afficher les coordonnées du dernier coup joué
        System.out.println("Vérification de victoire pour le coup : ligne=" + row + ", colonne=" + col + ", couleur=" + (playerColor == Pawn.PAWN_YELLOW ? "JAUNE" : "ROUGE"));

        // Vérification horizontale
        int count = 1;
        // Gauche
        for (int j = col - 1; j >= 0 && grid[row][j] == playerColor; j--) {
            count++;
            System.out.println("Horizontal gauche : " + count);
        }
        // Droite
        for (int j = col + 1; j < nbCols && grid[row][j] == playerColor; j++) {
            count++;
            System.out.println("Horizontal droite : " + count);
        }
        if (count >= nbAlign) {
            System.out.println("Victoire horizontale détectée pour le joueur " + (playerColor == Pawn.PAWN_YELLOW ? "JAUNE" : "ROUGE"));
            return true;
        }

        // Vérification verticale
        count = 1;
        // Haut
        for (int i = row - 1; i >= 0 && grid[i][col] == playerColor; i--) {
            count++;
            System.out.println("Vertical haut : " + count);
        }
        // Bas
        for (int i = row + 1; i < nbRows && grid[i][col] == playerColor; i++) {
            count++;
            System.out.println("Vertical bas : " + count);
        }
        if (count >= nbAlign) {
            System.out.println("Victoire verticale détectée pour le joueur " + (playerColor == Pawn.PAWN_YELLOW ? "JAUNE" : "ROUGE"));
            return true;
        }

        // Vérification diagonale (haut-gauche à bas-droite)
        count = 1;
        // Haut-gauche
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0 && grid[i][j] == playerColor; i--, j--) {
            count++;
            System.out.println("Diagonale \\ haut-gauche : " + count);
        }
        // Bas-droite
        for (int i = row + 1, j = col + 1; i < nbRows && j < nbCols && grid[i][j] == playerColor; i++, j++) {
            count++;
            System.out.println("Diagonale \\ bas-droite : " + count);
        }
        if (count >= nbAlign) {
            System.out.println("Victoire diagonale \\ détectée pour le joueur " + (playerColor == Pawn.PAWN_YELLOW ? "JAUNE" : "ROUGE"));
            return true;
        }

        // Vérification diagonale (haut-droite à bas-gauche)
        count = 1;
        // Haut-droite
        for (int i = row - 1, j = col + 1; i >= 0 && j < nbCols && grid[i][j] == playerColor; i--, j++) {
            count++;
            System.out.println("Diagonale / haut-droite : " + count);
        }
        // Bas-gauche
        for (int i = row + 1, j = col - 1; i < nbRows && j >= 0 && grid[i][j] == playerColor; i++, j--) {
            count++;
            System.out.println("Diagonale / bas-gauche : " + count);
        }
        if (count >= nbAlign) {
            System.out.println("Victoire diagonale / détectée pour le joueur " + (playerColor == Pawn.PAWN_YELLOW ? "JAUNE" : "ROUGE"));
            return true;
        }

        // Vérification supplémentaire pour toutes les diagonales possibles
        for (int startRow = 0; startRow < nbRows; startRow++) {
            for (int startCol = 0; startCol < nbCols; startCol++) {
                if (grid[startRow][startCol] == playerColor) {
                    // Vérifier diagonale descendante
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
                            System.out.println("Victoire diagonale \\ détectée (vérification supplémentaire) pour le joueur " + 
                                             (playerColor == Pawn.PAWN_YELLOW ? "JAUNE" : "ROUGE"));
                            return true;
                        }
                    }
                    
                    // Vérifier diagonale montante
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
                            System.out.println("Victoire diagonale / détectée (vérification supplémentaire) pour le joueur " + 
                                             (playerColor == Pawn.PAWN_YELLOW ? "JAUNE" : "ROUGE"));
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
} 