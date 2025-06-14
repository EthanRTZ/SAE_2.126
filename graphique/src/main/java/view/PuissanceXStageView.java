package view;

import boardifier.control.Logger;
import boardifier.model.GameStageModel;
import boardifier.view.ClassicBoardLook;
import boardifier.view.GameStageView;
import boardifier.view.TextLook;
import model.Pawn;
import model.PuissanceXStageModel;
import javafx.scene.paint.Color;

public class PuissanceXStageView extends GameStageView {
    public PuissanceXStageView(String name, GameStageModel gameStageModel) {
        super(name, gameStageModel);
    }

    @Override
    public void createLooks() {
        PuissanceXStageModel model = (PuissanceXStageModel) gameStageModel;

        // Create the player name display with a font size of 20 and black color
        addLook(new TextLook(20, "0x000000", model.getPlayerName()));

        // Create the board display with alternating colors and a border
        addLook(new ClassicBoardLook(
            40, // cell size
            model.getBoard(), // container element
            0, // depth
            Color.valueOf("0xFFFFFF"), // even cell color (white)
            Color.valueOf("0xD3D3D3"), // odd cell color (light gray)
            1, // cell border width
            Color.valueOf("0x000000"), // cell border color (black)
            2, // frame width
            Color.valueOf("0x000000"), // frame color (black)
            true // display coordinates
        ));
        
        // Add views for pawn pots
        addLook(new YellowPawnPotLook(40, 40, model.getYellowPot()));
        addLook(new RedPawnPotLook(40, 40, model.getRedPot()));

        // Create display for existing pawns
        for (Pawn pawn : model.getPawns()) {
            addLook(new PawnLook(pawn));
        }

        Logger.debug("finished creating game stage looks", this);
    }
}
