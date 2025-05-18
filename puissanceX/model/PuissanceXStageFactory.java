package model;

import boardifier.model.ContainerElement;
import boardifier.model.GameStageModel;
import boardifier.model.StageElementsFactory;
import boardifier.model.TextElement;

/**
 * PuissanceXStageFactory must create the game elements that are defined in PuissanceXStageModel
 * WARNING: it just creates the game element and NOT their look, which is done in PuissanceXStageView.
 *
 * If there must be a precise position in the display for the look of a game element, then this element must be created
 * with that position in the virtual space and MUST NOT be placed in a container element. Indeed, for such
 * elements, the position in their virtual space will match the position on the display.
 *
 * Otherwise, game elements must be put in a container and it will be the look of the container that will manage
 * the position of element looks on the display.
 */
public class PuissanceXStageFactory extends StageElementsFactory {
    private PuissanceXStageModel stageModel;

    public PuissanceXStageFactory(GameStageModel gameStageModel) {
        super(gameStageModel);
        stageModel = (PuissanceXStageModel) gameStageModel;
    }

    @Override
    public void setup() {
        // TODO: Create the text that displays the player name
        // - Create a TextElement for the current player's name
        // - Position it at the top of the screen
        // - Add it to the stage model

        // TODO: Create the game board
        // - Create a PuissanceXBoard with the specified dimensions
        // - Position it in the center of the screen
        // - Add it to the stage model

        // TODO: Create the player tokens
        // - Create tokens for both players
        // - Set their colors (e.g., red and yellow)
        // - Store them for later use

        // TODO: Initialize the game state
        // - Set up the initial player
        // - Initialize the board state
        // - Set up any game-specific variables
    }
} 