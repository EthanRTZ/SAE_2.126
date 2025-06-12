package view;

import boardifier.model.GameElement;
import boardifier.view.ElementLook;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.Pawn;

public class PawnLook extends ElementLook {
    private Circle circle;

    public PawnLook(GameElement element) {
        super(element);
        render();
    }

    @Override
    protected void render() {
        clearGroup();
        clearShapes();
        
        circle = new Circle(25); // rayon de 25 pixels
        Pawn pawn = (Pawn) element;
        if (pawn.getColor() == Pawn.PAWN_BLACK) {
            circle.setFill(Color.YELLOW);
        } else {
            circle.setFill(Color.RED);
        }
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);
        
        addShape(circle);
    }

    @Override
    public void onSelectionChange() {
        Pawn pawn = (Pawn) element;
        if (pawn.isSelected()) {
            circle.setStroke(Color.WHITE);
            circle.setStrokeWidth(3);
        } else {
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(2);
        }
    }
} 
