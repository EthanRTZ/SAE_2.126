package model;

import boardifier.model.GameStageModel;
import boardifier.model.Model;
import boardifier.model.TextElement;
import boardifier.model.StageElementsFactory;
import java.util.ArrayList;
import java.util.List;

public class Connect4StageModel extends GameStageModel {
    private Connect4Board board;
    private TextElement playerName;
    private boolean gameOver;
    private List<Pawn> pawns;
    private int winner;

    public Connect4StageModel(String name, Model model) {
        super(name, model);
        gameOver = false;
        pawns = new ArrayList<>();
        winner = -1;
        setupCallbacks();
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