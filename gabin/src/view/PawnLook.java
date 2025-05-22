package src.view;

import boardifier.model.GameElement;
import boardifier.view.ConsoleColor;
import boardifier.view.ElementLook;
import model.Pawn;

public class PawnLook extends ElementLook {
    public PawnLook(GameElement element) {
        super(element, 1, 1);
    }

    @Override
    public void render() {
        Pawn pawn = (Pawn)element;
        if (pawn.getColor() == Pawn.PAWN_BLACK) {
            // Pion noir : fond noir, X blanc
            shape[0][0] = ConsoleColor.BLACK_BACKGROUND + ConsoleColor.WHITE + "X" + ConsoleColor.RESET;
        }
        else {
            // Pion rouge : fond rouge, O noir
            shape[0][0] = ConsoleColor.RED_BACKGROUND + ConsoleColor.BLACK + "O" + ConsoleColor.RESET;
        }
    }
} 
