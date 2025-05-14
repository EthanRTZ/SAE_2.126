package control;

import boardifier.control.Controller;
import boardifier.model.Model;
import boardifier.model.GameElement;
import boardifier.view.View;
import model.PuissanceXBoard;
import model.Pion;

public class PuissanceXController extends Controller {
    /**
     * TODO pour adapter en PuissanceX:
     * - Adapter la logique de jeu pour le PuissanceX
     * - Gérer la gravité des pions
     * - Implémenter la vérification de victoire avec X pions
     * - Gérer les tours de jeu
     * - Permettre de paramétrer la taille du plateau et le nombre de pions à aligner
     */
    private final int largeurPlateau;
    private final int hauteurPlateau;
    private final int nombrePionsAAligner;

    public PuissanceXController(Model model, View view, int largeur, int hauteur, int nombrePionsAAligner) {
        super(model, view);
        this.largeurPlateau = largeur;
        this.hauteurPlateau = hauteur;
        this.nombrePionsAAligner = nombrePionsAAligner;
    }

    @Override
    public void stageLoop() {
        // TODO: Implémenter la boucle principale du jeu
    }

    /**
     * TODO: À implémenter
     * Gère le placement d'un pion dans une colonne
     */
    public void placerPion(int colonne) {
        // TODO:
        // - Vérifier si la colonne est valide
        // - Trouver la position la plus basse disponible
        // - Placer le pion
        // - Vérifier la victoire avec X pions
        // - Passer au joueur suivant
    }

    /**
     * TODO: À implémenter
     * Vérifie si le coup est valide
     */
    public boolean coupValide(int colonne) {
        // TODO:
        // - Vérifier si la colonne existe
        // - Vérifier si la colonne n'est pas pleine
        return false;
    }

    /**
     * TODO: À implémenter
     * Gère la fin de partie
     */
    public void finDePartie(boolean victoire, int joueur) {
        // TODO:
        // - Afficher le message de fin approprié
        // - Proposer une nouvelle partie
    }

    // Getters pour les paramètres du jeu
    public int getLargeurPlateau() {
        return largeurPlateau;
    }

    public int getHauteurPlateau() {
        return hauteurPlateau;
    }

    public int getNombrePionsAAligner() {
        return nombrePionsAAligner;
    }
} 