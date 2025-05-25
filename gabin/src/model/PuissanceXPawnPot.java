package model;

import boardifier.model.ContainerElement;
import boardifier.model.GameElement;
import boardifier.model.GameStageModel;

/**
 * Connect4PawnPot représente un pot contenant des pions pour un joueur.
 * Ce conteneur stocke les pions disponibles pour un joueur du jeu Connect4.
 */
public class PuissanceXPawnPot extends ContainerElement {
    private int remainingPawns;

    public PuissanceXPawnPot(int x, int y, GameStageModel gameStageModel, int nbPawns) {
        super("PuissanceXpawnpot", x, y, 1, nbPawns, gameStageModel);
        this.remainingPawns = nbPawns;
    }

    /**
     * Retourne le nombre de pions restants dans le pot.
     * @return nombre de pions restants
     */
    public int getRemainingPawns() {
        return remainingPawns;
    }

    @Override
    public void removeElement(GameElement element) {
        super.removeElement(element);
        remainingPawns--;
    }
}
