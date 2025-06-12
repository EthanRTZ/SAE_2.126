package view;

import boardifier.model.ContainerElement;
import boardifier.view.TableLook;
import model.PuissanceXPawnPot;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Red pot inherits from TableLook, using the constructor for
 * flexible cell sizes and a visible border. It implies that if there is no element in a cell
 * it has a zero size and thus, is not displayed. This is why during the game,
 * the red pot will reduce in size because of pawn are removed from the pot to be placed
 * on the main board. At then end, it will totally disappear.
 */
public class RedPawnPotLook extends TableLook {
    private Text countText;

    public RedPawnPotLook(int cellHeight, int cellWidth, ContainerElement containerElement) {
        super(containerElement, -1, 2, Color.RED);
        countText = new Text();
        countText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        countText.setFill(Color.BLACK);
        updatePawnCount();
        addNode(countText);
    }

    @Override
    public void onLocationChange() {
        super.onLocationChange();
        updatePawnCount();
    }

    private void updatePawnCount() {
        PuissanceXPawnPot pot = (PuissanceXPawnPot) element;
        int count = pot.getRemainingPawns();
        countText.setText("Rouges: " + count + " pions");
        countText.setX(element.getX());
        countText.setY(element.getY() - 20);
    }
}
