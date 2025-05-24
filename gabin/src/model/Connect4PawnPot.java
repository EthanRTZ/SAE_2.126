package model;

import boardifier.model.ContainerElement;
import boardifier.model.GameElement;
import boardifier.model.GameStageModel;

/**
 * Connect4PawnPot représente un pot contenant des pions pour un joueur.
 * Ce conteneur stocke les pions disponibles pour un joueur du jeu Connect4.
 */
public class Connect4PawnPot extends ContainerElement {
    private int totalPawns;

    public Connect4PawnPot(int x, int y, GameStageModel gameStageModel, int nbPawns) {
        super("connect4pawnpot", x, y, nbPawns, 1, gameStageModel);
        this.totalPawns = nbPawns;
    }

    /**
     * Retourne le nombre de pions restants dans le pot.
     * @return nombre de pions restants
     */
    public int getRemainingPawns() {
        return totalPawns;
    }

    @Override
    public void removeElement(GameElement element) {
        super.removeElement(element);
        totalPawns--;
    }
}
