package model;

import boardifier.model.GameStageModel;
import boardifier.model.Model;
import boardifier.model.Player;
import boardifier.model.StageElementsFactory;
import boardifier.model.TextElement;

public class PuissanceXStageModel extends GameStageModel {
    private PuissanceXBoard board;
    private int currentPlayer;
    private int winner;
    private boolean gameEnded;
    private TextElement playerName;

    public PuissanceXStageModel(String name, Model model) {
        super(name, model);
        currentPlayer = 0;
        winner = -1;
        gameEnded = false;
        setupCallbacks();
    }

    /**
     * Initialise le plateau de jeu
     * @param largeur La largeur du plateau
     * @param hauteur La hauteur du plateau
     * @param nombrePionsAAligner Le nombre de pions à aligner pour gagner
     */
    public void initBoard(int largeur, int hauteur, int nombrePionsAAligner) {
        // TODO: Implémenter l'initialisation
        // - Créer le plateau avec les dimensions spécifiées
        // - Positionner le plateau au centre de la fenêtre
        // - Initialiser les cellules vides
    }

    /**
     * Vérifie si le jeu est terminé
     * @return true si le jeu est terminé (victoire ou match nul)
     */
    public boolean isGameEnded() {
        // TODO: Implémenter la vérification
        // - Vérifier s'il y a une victoire
        // - Vérifier s'il y a match nul (plateau plein)
        return gameEnded;
    }

    /**
     * Vérifie s'il y a match nul
     * @return true si le plateau est plein
     */
    public boolean isBoardFull() {
        // TODO: Implémenter la vérification
        // - Vérifier si toutes les colonnes sont pleines
        return false;
    }

    /**
     * Change le joueur actuel
     */
    public void switchPlayer() {
        // TODO: Implémenter le changement de joueur
        // - Alterner entre les joueurs 0 et 1
    }

    /**
     * Place un pion dans une colonne
     * @param colonne La colonne où placer le pion
     * @return true si le placement a réussi
     */
    public boolean placePawn(int colonne) {
        // TODO: Implémenter le placement
        // - Vérifier si la colonne est valide
        // - Trouver la position la plus basse disponible
        // - Placer le pion
        // - Vérifier la victoire
        // - Changer de joueur si pas de victoire
        return false;
    }

    /**
     * Récupère le joueur actuel
     * @return Le numéro du joueur actuel (0 ou 1)
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Récupère le gagnant
     * @return Le numéro du joueur gagnant (-1 si pas de gagnant)
     */
    public int getWinner() {
        return winner;
    }

    /**
     * Définit le gagnant
     * @param playerNumber Le numéro du joueur gagnant
     */
    public void setWinner(int playerNumber) {
        this.winner = playerNumber;
        this.gameEnded = true;
        model.setIdWinner(playerNumber);
        model.stopStage();
    }

    /**
     * Récupère le plateau de jeu
     * @return Le plateau de jeu
     */
    public PuissanceXBoard getBoard() {
        return board;
    }

    /**
     * Définit le plateau de jeu
     * @param board Le plateau de jeu
     */
    public void setBoard(PuissanceXBoard board) {
        this.board = board;
        addContainer(board);
    }

    /**
     * Récupère l'élément texte affichant le nom du joueur
     * @return L'élément texte
     */
    public TextElement getPlayerName() {
        return playerName;
    }

    /**
     * Définit l'élément texte affichant le nom du joueur
     * @param playerName L'élément texte
     */
    public void setPlayerName(TextElement playerName) {
        this.playerName = playerName;
        addElement(playerName);
    }

    /**
     * Configure les callbacks pour les événements du jeu
     */
    private void setupCallbacks() {
        onPutInContainer((element, gridDest, rowDest, colDest) -> {
            // Vérifier quand un pion est placé sur le plateau
            if (gridDest != board) return;
            
            // Vérifier la victoire après chaque placement
            if (board.checkVictory(currentPlayer)) {
                setWinner(currentPlayer);
            } else if (isBoardFull()) {
                // Match nul
                setWinner(-1);
            } else {
                // Changer de joueur si pas de victoire
                switchPlayer();
            }
        });
    }

    @Override
    public StageElementsFactory getDefaultElementFactory() {
        return new PuissanceXStageFactory(this);
    }
} 