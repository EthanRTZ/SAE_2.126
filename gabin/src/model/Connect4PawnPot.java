package model;

import boardifier.model.ContainerElement;
import boardifier.model.GameStageModel;

/**
 * Connect4PawnPot représente un pot contenant des pions pour un joueur.
 * Ce conteneur stocke les pions disponibles pour un joueur du jeu Connect4.
 */
public class Connect4PawnPot extends ContainerElement {

    public Connect4PawnPot(int x, int y, GameStageModel gameStageModel) {
        super("connect4pawnpot", x, y, 21, 1, gameStageModel);
    }

    /**
     * Retourne le nombre de pions restants dans le pot.
     * @return nombre de pions restants
     */
    public int getRemainingPawns() {
        int count = 0;
        for (int i = 0; i < getNbRows(); i++) {
            if (!isEmptyAt(i, 0)) count++;
        }
        return count;
    }
}
