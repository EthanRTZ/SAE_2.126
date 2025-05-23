package view;

import boardifier.model.ContainerElement;
import boardifier.view.TableLook;

/**
 * Yellow pot inherits from TableLook, using the constructor for
 * flexible cell sizes and a visible border. It implies that if there is no element in a cell
 * it has a zero size and thus, is not displayed. This is why during the game,
 * the yellow pot will reduce in size because of pawn are removed from the pot to be placed
 * on the main board. At then end, it will totally disappear.
 *
 * Note that this class is not necessary and the Connect4StageView could create directly an instance of TableLook.
 * So, this subclass is just in case of we would like to change the look of the yellow pot in the future.
 */
public class YellowPawnPotLook extends TableLook {

    public YellowPawnPotLook(ContainerElement containerElement) {
        super(containerElement, -1, 1);
    }
}
