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
     * Vérifie si une colonne est pleine
     * @param colonne Le numéro de la colonne à vérifier
     * @return true si la colonne est pleine, false sinon
     */
    public boolean estColonnePleine(int colonne) {
        // TODO: Implémenter la vérification
        // - Vérifier si la case la plus haute de la colonne est occupée
        return false;
    }

    /**
     * Trouve la position la plus basse disponible dans une colonne
     * @param colonne Le numéro de la colonne
     * @return La position (ligne) la plus basse disponible, ou -1 si la colonne est pleine
     */
    public int trouverPositionLaPlusBasse(int colonne) {
        // TODO: Implémenter la recherche
        // - Parcourir la colonne de bas en haut
        // - Retourner la première position vide trouvée
        return -1;
    }

    /**
     * Vérifie s'il y a une victoire horizontale
     * @param playerNumber Le numéro du joueur à vérifier
     * @return true si une victoire horizontale est trouvée
     */
    private boolean verifierVictoireHorizontale(int playerNumber) {
        // TODO: Implémenter la vérification horizontale
        // - Parcourir chaque ligne
        // - Vérifier les alignements de nombrePionsAAligner pions
        return false;
    }

    /**
     * Vérifie s'il y a une victoire verticale
     * @param playerNumber Le numéro du joueur à vérifier
     * @return true si une victoire verticale est trouvée
     */
    private boolean verifierVictoireVerticale(int playerNumber) {
        // TODO: Implémenter la vérification verticale
        // - Parcourir chaque colonne
        // - Vérifier les alignements de nombrePionsAAligner pions
        return false;
    }

    /**
     * Vérifie s'il y a une victoire diagonale (haut-gauche vers bas-droite)
     * @param playerNumber Le numéro du joueur à vérifier
     * @return true si une victoire diagonale est trouvée
     */
    private boolean verifierVictoireDiagonale1(int playerNumber) {
        // TODO: Implémenter la vérification diagonale
        // - Parcourir les diagonales possibles
        // - Vérifier les alignements de nombrePionsAAligner pions
        return false;
    }

    /**
     * Vérifie s'il y a une victoire diagonale (haut-droite vers bas-gauche)
     * @param playerNumber Le numéro du joueur à vérifier
     * @return true si une victoire diagonale est trouvée
     */
    private boolean verifierVictoireDiagonale2(int playerNumber) {
        // TODO: Implémenter la vérification diagonale
        // - Parcourir les diagonales possibles
        // - Vérifier les alignements de nombrePionsAAligner pions
        return false;
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