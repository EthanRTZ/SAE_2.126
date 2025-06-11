package view;

import boardifier.model.GameElement;
import boardifier.view.ElementLook;
import model.Pawn;

public class PawnLook extends ElementLook {
    public PawnLook(GameElement element) {
        super(element, 3, 1);
        updateShape();
    }

    @Override
    public void render() {
        updateShape();
    }

    private void updateShape() {
        // Définir l'apparence du pion en fonction de sa couleur
        Pawn pawn = (Pawn) element;
        String pawnSymbol = " ● ";
        if (pawn.getColor() == Pawn.PAWN_BLACK) {
            shape[0][0] = boardifier.view.ConsoleColor.YELLOW + pawnSymbol + boardifier.view.ConsoleColor.RESET;
        } else {
            shape[0][0] = boardifier.view.ConsoleColor.RED + pawnSymbol + boardifier.view.ConsoleColor.RESET;
        }
    }
} 
