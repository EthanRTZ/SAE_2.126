package view;

import boardifier.control.Logger;
import boardifier.model.GameStageModel;
import boardifier.view.ClassicBoardLook;
import boardifier.view.GameStageView;
import boardifier.view.TextLook;
import model.Connect4StageModel;
import model.Pawn;

public class Connect4StageView extends GameStageView {
    public Connect4StageView(String name, GameStageModel gameStageModel) {
        super(name, gameStageModel);
    }

    @Override
    public void createLooks() {
        Connect4StageModel model = (Connect4StageModel) gameStageModel;

        // Créer l'affichage du nom du joueur
        addLook(new TextLook(model.getPlayerName()));

        // Créer l'affichage du plateau avec des bordures et colonnes numérotées de 1 à 7, sans numéros de lignes
        addLook(new ClassicBoardLook(2, 4, model.getBoard(), 0, 1, false));
        
        // Ajouter les vues pour les pots de pions
        addLook(new YellowPawnPotLook(model.getYellowPot()));
        addLook(new RedPawnPotLook(2, 4, model.getRedPot()));

        // Créer l'affichage des pions déjà existants
        for (Pawn pawn : model.getPawns()) {
            addLook(new PawnLook(pawn));
        }

        Logger.debug("finished creating game stage looks", this);
    }
}
