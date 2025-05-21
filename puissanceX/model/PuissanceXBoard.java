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
        for (int ligne = getHeight() - 1; ligne >= 0; ligne--) {
            if (getElement(ligne, colonne) == null) {
                return ligne; // première colonne vide trouvé
            }
        }
        return -1; // colonne pleine
    }

    /**
     * Vérifie s'il y a une victoire horizontale
     * @param playerNumber Le numéro du joueur à vérifier
     * @return true si une victoire horizontale est trouvée
     */
    private boolean verifierVictoireHorizontale(int playerNumber) {
        // Parcourt chaque ligne du plateau
        for (int row = 0; row < getHeight(); row++) { // - A modifier pour verticale
            int count = 0; // Compteur de pions consécutifs du joueur
            // Parcourt chaque colonne de la ligne courante
            for (int col = 0; col < getWidth(); col++) { // - A modifier pour verticale
                Object elem = getElement(row, col);
                // Vérifie si la case contient un pion du joueur
                if (elem != null && elem instanceof model.PuissanceXPion) {
                    if (((model.PuissanceXPion) elem).getPlayer() == playerNumber) {
                        count++;
                        // Si le compteur atteint le nombre requis, victoire
                        if (count >= nombrePionsAAligner) return true;
                    } else {
                        count = 0; // Réinitialise le compteur si ce n'est pas un pion du joueur
                    }
                } else {
                    count = 0; // Réinitialise si la case est vide
                }
            }
        }
        return false;
    }

    /**
     * Vérifie s'il y a une victoire verticale
     * @param playerNumber Le numéro du joueur à vérifier
     * @return true si une victoire verticale est trouvée
     */
    private boolean verifierVictoireVerticale(int playerNumber) {
            // Parcourt chaque ligne du plateau
            for (int row = 0; row < getWidth(); row++) {
                int count = 0; // Compteur de pions consécutifs du joueur
                // Parcourt chaque colonne de la ligne courante
                for (int col = 0; col < getHeight(); col++) {
                    Object elem = getElement(row, col);
                    // Vérifie si la case contient un pion du joueur
                    if (elem != null && elem instanceof model.PuissanceXPion) {
                        if (((model.PuissanceXPion) elem).getPlayer() == playerNumber) {
                            count++;
                            // Si le compteur atteint le nombre requis, victoire
                            if (count >= nombrePionsAAligner) return true;
                        } else {
                            count = 0; // Réinitialise le compteur si ce n'est pas un pion du joueur
                        }
                    } else {
                        count = 0; // Réinitialise si la case est vide
                    }
                }
            }
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
        for (int col = 0; col < getWidth(); col++) {
            if (!estColonnePleine(col)) {
                int ligne = trouverPositionLaPlusBasse(col);
                if (ligne != -1) {
                    lst.add(new Point(col, ligne));
                }
            }
        }
        return lst;
    }

    /**
     * Vérifie s'il y a une victoire (X pions alignés)
     */
    public boolean checkVictory(int playerNumber) {
        return verifierVictoireHorizontale(playerNumber)
            || verifierVictoireVerticale(playerNumber)
            || verifierVictoireDiagonale1(playerNumber)
            || verifierVictoireDiagonale2(playerNumber);
    }

    public int getNombrePionsAAligner() {
        return nombrePionsAAligner;
    }
}
