package model;

public class BoardDimensions {
    private static int dimensions = 3; // Valeur par défaut

    public static void setDimensions(int dim) {
        if (dim < 3) {
            throw new IllegalArgumentException("Les dimensions doivent être au moins 3x3");
        }
        dimensions = dim;
    }

    public static int getDimensions() {
        return dimensions;
    }
} 