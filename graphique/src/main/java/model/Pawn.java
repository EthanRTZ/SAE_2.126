package model;

import boardifier.model.ElementTypes;
import boardifier.model.GameElement;
import boardifier.model.GameStageModel;

public class Pawn extends GameElement {
    public static final int PAWN_YELLOW = 0;  // Anciennement PAWN_BLACK
    public static final int PAWN_RED = 1;

    private int number;
    private int color;

    public Pawn(int number, int color, GameStageModel gameStageModel) {
        super(gameStageModel);
        // Enregistrer le type d'élément pour ce jeu
        ElementTypes.register("pawn", 50);
        type = ElementTypes.getType("pawn");
        this.number = number;
        this.color = color;
    }

    public int getNumber() {
        return number;
    }

    public int getColor() {
        return color;
    }
} 