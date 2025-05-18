package view;

import boardifier.control.Logger;
import boardifier.model.GameStageModel;
import boardifier.view.ClassicBoardLook;
import boardifier.view.GameStageView;
import boardifier.view.TextLook;
import model.PuissanceXStageModel;

/**
 * PuissanceXStageView has to create all the looks for all game elements created by the PuissanceXStageFactory.
 * The desired UI is the following:
 * 
 * Player: [Current Player Name]
 * 
 *   1   2   3   4   5   6   7
 * ┌───┬───┬───┬───┬───┬───┬───┐
 * │   │   │   │   │   │   │   │
 * ├───┼───┼───┼───┼───┼───┼───┤
 * │   │   │   │   │   │   │   │
 * ├───┼───┼───┼───┼───┼───┼───┤
 * │   │   │   │   │   │   │   │
 * ├───┼───┼───┼───┼───┼───┼───┤
 * │   │   │   │   │   │   │   │
 * ├───┼───┼───┼───┼───┼───┼───┤
 * │   │   │   │   │   │   │   │
 * └───┴───┴───┴───┴───┴───┴───┘
 *
 * The UI constraints are :
 *   - the main board has borders and coordinates, and cells of size 1x3
 *   - tokens are displayed with their color
 *
 *   The main board can be instanciated directly as a ClassicBoardLook.
 */

public class PuissanceXStageView extends GameStageView {
    public PuissanceXStageView(String name, GameStageModel gameStageModel) {
        super(name, gameStageModel);
    }

    @Override
    public void createLooks() {
        PuissanceXStageModel model = (PuissanceXStageModel)gameStageModel;

        // create a TextLook for the text element (player name)
        addLook(new TextLook(model.getPlayerName()));
        // create a ClassicBoardLook for the main board (cell size 1x3, with coordinates)
        addLook(new ClassicBoardLook(1, 3, model.getBoard(), 1, 1, true));
        // create looks for all tokens (pions)
        // Suppose you have a method to get all pions from the model, otherwise adapt as needed
        if (model.getBoard() != null) {
            for (GameElement element : model.getBoard().getElements()) {
                addLook(new PionLook(element));
            }
        }
        Logger.debug("finished creating game stage looks", this);
    }
} 