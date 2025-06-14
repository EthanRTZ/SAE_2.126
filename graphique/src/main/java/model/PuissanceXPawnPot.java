package model;

import boardifier.model.ContainerElement;
import boardifier.model.GameElement;
import boardifier.model.GameStageModel;

/**
 * PuissanceXPawnPot represents a pot containing pawns for a player.
 * This container stores the available pawns for a PuissanceX game player.
 */
public class PuissanceXPawnPot extends ContainerElement {
    private int remainingPawns;

    public PuissanceXPawnPot(int x, int y, GameStageModel gameStageModel, int nbPawns) {
        super("PuissanceXpawnpot", x, y, 1, nbPawns, gameStageModel);
        this.remainingPawns = nbPawns;
    }

    /**
     * Returns the number of remaining pawns in the pot.
     * @return number of remaining pawns
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
