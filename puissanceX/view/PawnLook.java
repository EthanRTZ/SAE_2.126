package view;

import boardifier.model.GameElement;
import boardifier.view.ConsoleColor;
import boardifier.view.ElementLook;
import model.Pion;

/**
 * The look of the Pion is fixed, with a single character representing the player
 * and a colored background (red or yellow).
 */
public class PionLook extends ElementLook {

    public PionLook(GameElement element) {
        super(element, 1, 1);
    }

    protected void render() {
        Pion pion = (Pion)element;
        if ("ROUGE".equals(pion.getCouleur())) {
            shape[0][0] = ConsoleColor.WHITE + ConsoleColor.RED_BACKGROUND + "O" + ConsoleColor.RESET;
        }
        else {
            shape[0][0] = ConsoleColor.BLACK + ConsoleColor.YELLOW_BACKGROUND + "O" + ConsoleColor.RESET;
        }
    }
}
