package view;

import boardifier.model.ContainerElement;
import boardifier.view.TableLook;

/**
 * Red pot inherits from TableLook, using the constructor for
 * flexible cell sizes and a visible border. It implies that if there is no element in a cell
 * it has a zero size and thus, is not displayed. This is why during the game,
 * the red pot will reduce in size because of pawn are removed from the pot to be placed
 * on the main board. At then end, it will totally disappear.
 */
public class RedPawnPotLook extends TableLook {

    public RedPawnPotLook(int x, int y, ContainerElement containerElement) {
        super(containerElement, -1, 1);
    }
}