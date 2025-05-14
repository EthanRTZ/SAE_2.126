package model;

import boardifier.model.GameStageModel;
import boardifier.model.ContainerElement;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class PuissanceXBoard extends ContainerElement {
    /**
     * TODO pour adapter en PuissanceX:
     * - Permettre de définir la taille du plateau (NxM)
     * - Permettre de définir le nombre de pions à aligner (X)
     * - Implémenter la logique de gravité (les pions tombent en bas)
     * - Ajouter la vérification de victoire (X pions alignés)
     *   - Vérifier horizontalement
     *   - Vérifier verticalement
     *   - Vérifier diagonalement (deux directions)
     */
    private final int nombrePionsAAligner; // Le X de PuissanceX
    
    public PuissanceXBoard(int x, int y, int largeur, int hauteur, int nombrePionsAAligner, GameStageModel gameStageModel) {
        super("puissanceXboard", x, y, largeur, hauteur, gameStageModel);
        this.nombrePionsAAligner = nombrePionsAAligner;
    }

    /**
     * TODO: À modifier
     * - Ne permettre que les colonnes non pleines
     * - Implémenter la logique de gravité
     */
    public void setValidCells(int number) {
        resetReachableCells(false);
        List<Point> valid = computeValidCells(number);
        if (valid != null) {
            for(Point p : valid) {
                reachableCells[p.y][p.x] = true;
            }
        }
    }

    /**
     * TODO: À réécrire complètement
     * - Ne retourner que les colonnes non pleines
     * - Prendre en compte la gravité (le pion tombe au plus bas)
     */
    public List<Point> computeValidCells(int number) {
        List<Point> lst = new ArrayList<>();
        // TODO: Implémenter la logique pour PuissanceX
        // - Vérifier chaque colonne
        // - Si la colonne n'est pas pleine, ajouter la position la plus basse disponible
        return lst;
    }

    /**
     * TODO: À implémenter
     * Vérifie s'il y a une victoire (X pions alignés)
     */
    public boolean checkVictory(int playerNumber) {
        // TODO: Implémenter
        // - Vérifier horizontalement
        // - Vérifier verticalement
        // - Vérifier diagonalement (deux directions)
        // - Utiliser nombrePionsAAligner pour la vérification
        return false;
    }

    public int getNombrePionsAAligner() {
        return nombrePionsAAligner;
    }
} 