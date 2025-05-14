package model;

import boardifier.model.GameElement;
import boardifier.model.GameStageModel;

public class Pion extends GameElement {
    /**
     * TODO pour adapter en PuissanceX:
     * - Simplifier la classe pour n'avoir que deux types de pions (joueur 1 et 2)
     * - Ajouter des attributs pour la couleur des pions
     * - Possiblement ajouter des méthodes pour l'animation de chute
     */
    private final int joueur; // 1 ou 2
    private final String couleur; // par exemple "ROUGE" ou "JAUNE"

    public Pion(int joueur, String couleur, GameStageModel gameStageModel) {
        super(gameStageModel);
        this.joueur = joueur;
        this.couleur = couleur;
    }

    public int getJoueur() {
        return joueur;
    }

    public String getCouleur() {
        return couleur;
    }
} 