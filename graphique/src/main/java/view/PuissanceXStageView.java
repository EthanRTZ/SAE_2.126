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

        // Créer l'affichage du nom du joueur avec une taille de police de 20 et une couleur noire
        addLook(new TextLook(20, "0x000000", model.getPlayerName()));

        // Créer l'affichage du plateau avec des couleurs alternées et une bordure
        addLook(new ClassicBoardLook(
            40, // taille des cellules
            model.getBoard(), // élément conteneur
            0, // profondeur
            Color.valueOf("0xFFFFFF"), // couleur des cases paires (blanc)
            Color.valueOf("0xD3D3D3"), // couleur des cases impaires (gris clair)
            1, // largeur de la bordure des cellules
            Color.valueOf("0x000000"), // couleur de la bordure des cellules (noir)
            2, // largeur du cadre
            Color.valueOf("0x000000"), // couleur du cadre (noir)
            true // afficher les coordonnées
        ));
        
        // Ajouter les vues pour les pots de pions
        addLook(new YellowPawnPotLook(40, 40, model.getYellowPot()));
        addLook(new RedPawnPotLook(40, 40, model.getRedPot()));

        // Créer l'affichage des pions déjà existants
        for (Pawn pawn : model.getPawns()) {
            addLook(new PawnLook(pawn));
        }

        Logger.debug("finished creating game stage looks", this);
    }
}
