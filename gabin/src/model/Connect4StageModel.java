package model;

import boardifier.model.GameStageModel;
import boardifier.model.Model;
import boardifier.model.StageElementsFactory;
import boardifier.model.TextElement;
import java.util.ArrayList;
import java.util.List;

public class Connect4StageModel extends GameStageModel {
    private Connect4Board board;
    private TextElement playerName;
    private boolean gameOver;
    private List<Pawn> pawns;
    private int winner;
    // Ajout des pots pour les pions jaunes et rouges
    private Connect4PawnPot yellowPot;
    private Connect4PawnPot redPot;
    private int nbRows;
    private int nbCols;
    private int nbToAlign;

    public Connect4StageModel(String name, Model model) {
        super(name, model);
        gameOver = false;
        pawns = new ArrayList<>();
        winner = -1;
        setupCallbacks();
    }

    public void setDimensions(int nbRows, int nbCols, int nbToAlign) {
        this.nbRows = nbRows;
        this.nbCols = nbCols;
        this.nbToAlign = nbToAlign;
    }

    public int getNbRows() {
        return nbRows;
    }

    public int getNbCols() {
        return nbCols;
    }

    public int getNbToAlign() {
        return nbToAlign;
    }

    public Connect4Board getBoard() {
        return board;
    }

    public void setBoard(Connect4Board board) {
        this.board = board;
        addContainer(board);
    }

    public TextElement getPlayerName() {
        return playerName;
    }

    public void setPlayerName(TextElement playerName) {
        this.playerName = playerName;
        addElement(playerName);
    }

    public List<Pawn> getPawns() {
        return pawns;
    }

    public void setPawns(List<Pawn> pawns) {
        this.pawns = pawns;
        for (Pawn pawn : pawns) {
            addElement(pawn);
        }
    }

    // Accesseurs pour les pots de pions
    public Connect4PawnPot getYellowPot() {
        return yellowPot;
    }

    public void setYellowPot(Connect4PawnPot yellowPot) {
        this.yellowPot = yellowPot;
        addContainer(yellowPot);
    }

    public Connect4PawnPot getRedPot() {
        return redPot;
    }

    public void setRedPot(Connect4PawnPot redPot) {
        this.redPot = redPot;
        addContainer(redPot);
    }

    private void setupCallbacks() {
        onPutInContainer((element, container, row, col) -> {
            if (container == board) {
                Pawn pawn = (Pawn) element;
                int color = pawn.getColor();
                if (board.checkWin(row, col, color)) {
                    winner = color;
                    model.stopGame();
                } else if (board.isBoardFull()) {
                    winner = -1;
                    model.stopGame();
                } else {
                    model.setNextPlayer();
                }
            }
        });
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getWinner() {
        return winner;
    }

    @Override
    public StageElementsFactory getDefaultElementFactory() {
        return new Connect4StageFactory(this);
    }
} 
