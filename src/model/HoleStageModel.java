package model;

import boardifier.model.*;

/**
 * HoleStageModel defines the model for the single stage in "The Hole". Indeed,
 * there are no levels in this game: a party starts and when it's done, the game is also done.
 *
 * HoleStageModel must define all that is needed to manage a party : state variables and game elements.
 * In the present case, there are only 2 state variables that represent the number of pawns to play by each player.
 * It is used to detect the end of the party.
 * For game elements, it depends on what is chosen as a final UI design. For that demo, there are 12 elements used
 * to represent the state : the main board, 2 pots, 8 pawns, and a text for current player.
 *
 * WARNING ! HoleStageModel DOES NOT create itself the game elements because it would prevent the possibility to mock
 * game element classes for unit testing purposes. This is why HoleStageModel just defines the game elements and the methods
 * to set this elements.
 * The instanciation of the elements is done by the HoleStageFactory, which uses the provided setters.
 *
 * HoleStageModel must also contain methods to check/modify the game state when given events occur. This is the role of
 * setupCallbacks() method that defines a callback function that must be called when a pawn is put in a container.
 * This is done by calling onPutInContainer() method, with the callback function as a parameter. After that call, boardifier
 * will be able to call the callback function automatically when a pawn is put in a container.
 * NB1: callback functions MUST BE defined with a lambda expression (i.e. an arrow function).
 * NB2:  there are other methods to defines callbacks for other events (see onXXX methods in GameStageModel)
 * In "The Hole", everytime a pawn is put in the main board, we have to check if the party is ended and in this case, who is the winner.
 * This is the role of computePartyResult(), which is called by the callback function if there is no more pawn to play.
 *
 */
public class HoleStageModel extends GameStageModel {

    // define stage state variables
    private int blackPawnsToPlay;
    private int redPawnsToPlay;
    private int winner;

    // define stage game elements
    private HoleBoard board;
    private HolePawnPot blackPot;
    private HolePawnPot redPot;
    private Pawn[] blackPawns;
    private Pawn[] redPawns;
    private TextElement playerName;
    // Uncomment next line if the example with a main container is used. see end of HoleStageFactory and HoleStageView
    //private ContainerElement mainContainer;

    public HoleStageModel(String name, Model model) {
        super(name, model);
        blackPawnsToPlay = 4;
        redPawnsToPlay = 4;
        winner = -1;
        setupCallbacks();
    }

    //Uncomment this 2 methods if example with a main container is used
    /*
    public ContainerElement getMainContainer() {
        return mainContainer;
    }

    public void setMainContainer(ContainerElement mainContainer) {
        this.mainContainer = mainContainer;
        addContainer(mainContainer);
    }
     */

    public HoleBoard getBoard() {
        return board;
    }
    public void setBoard(HoleBoard board) {
        this.board = board;
        addContainer(board);
    }

    public HolePawnPot getBlackPot() {
        return blackPot;
    }
    public void setBlackPot(HolePawnPot blackPot) {
        this.blackPot = blackPot;
        addContainer(blackPot);
    }

    public HolePawnPot getRedPot() {
        return redPot;
    }
    public void setRedPot(HolePawnPot redPot) {
        this.redPot = redPot;
        addContainer(redPot);
    }

    public Pawn[] getBlackPawns() {
        return blackPawns;
    }
    public void setBlackPawns(Pawn[] blackPawns) {
        this.blackPawns = blackPawns;
        for(int i=0;i<blackPawns.length;i++) {
            addElement(blackPawns[i]);
        }
    }

    public Pawn[] getRedPawns() {
        return redPawns;
    }
    public void setRedPawns(Pawn[] redPawns) {
        this.redPawns = redPawns;
        for(int i=0;i<redPawns.length;i++) {
            addElement(redPawns[i]);
        }
    }

    public TextElement getPlayerName() {
        return playerName;
    }
    public void setPlayerName(TextElement playerName) {
        this.playerName = playerName;
        addElement(playerName);
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    private void setupCallbacks() {
        onPutInContainer( (element, gridDest, rowDest, colDest) -> {
            // just check when pawns are put in 3x3 board
            if (gridDest != board) return;
            Pawn p = (Pawn) element;
            if (p.getColor() == 0) {
                blackPawnsToPlay--;
            }
            else {
                redPawnsToPlay--;
            }
            if ((blackPawnsToPlay == 0) && (redPawnsToPlay == 0)) {
                computePartyResult();
            }
        });
    }

    private void computePartyResult() {
        int idWinner = -1;
        int nbBlack = 0;
        int nbRed = 0;
        int countBlack = 0;
        int countRed = 0;
        Pawn p = null;
        int row, col;
        int tokensToWin = HoleBoard.getTokensToWin();

        // Vérifier les alignements horizontaux
        for (int i = 0; i < HoleBoard.getRows(); i++) {
            for (int j = 0; j <= HoleBoard.getCols() - tokensToWin; j++) {
                int blackCount = 0;
                int redCount = 0;
                for (int k = 0; k < tokensToWin; k++) {
                    p = (Pawn) board.getElement(i, j + k);
                    if (p != null) {
                        if (p.getColor() == Pawn.PAWN_BLACK) {
                            blackCount++;
                        } else {
                            redCount++;
                        }
                    }
                }
                if (blackCount == tokensToWin) {
                    idWinner = 0;
                    break;
                }
                if (redCount == tokensToWin) {
                    idWinner = 1;
                    break;
                }
            }
            if (idWinner != -1) break;
        }

        // Vérifier les alignements verticaux
        if (idWinner == -1) {
            for (int j = 0; j < HoleBoard.getCols(); j++) {
                for (int i = 0; i <= HoleBoard.getRows() - tokensToWin; i++) {
                    int blackCount = 0;
                    int redCount = 0;
                    for (int k = 0; k < tokensToWin; k++) {
                        p = (Pawn) board.getElement(i + k, j);
                        if (p != null) {
                            if (p.getColor() == Pawn.PAWN_BLACK) {
                                blackCount++;
                            } else {
                                redCount++;
                            }
                        }
                    }
                    if (blackCount == tokensToWin) {
                        idWinner = 0;
                        break;
                    }
                    if (redCount == tokensToWin) {
                        idWinner = 1;
                        break;
                    }
                }
                if (idWinner != -1) break;
            }
        }

        // Vérifier les alignements diagonaux (haut gauche vers bas droite)
        if (idWinner == -1) {
            for (int i = 0; i <= HoleBoard.getRows() - tokensToWin; i++) {
                for (int j = 0; j <= HoleBoard.getCols() - tokensToWin; j++) {
                    int blackCount = 0;
                    int redCount = 0;
                    for (int k = 0; k < tokensToWin; k++) {
                        p = (Pawn) board.getElement(i + k, j + k);
                        if (p != null) {
                            if (p.getColor() == Pawn.PAWN_BLACK) {
                                blackCount++;
                            } else {
                                redCount++;
                            }
                        }
                    }
                    if (blackCount == tokensToWin) {
                        idWinner = 0;
                        break;
                    }
                    if (redCount == tokensToWin) {
                        idWinner = 1;
                        break;
                    }
                }
                if (idWinner != -1) break;
            }
        }

        // Vérifier les alignements diagonaux (haut droite vers bas gauche)
        if (idWinner == -1) {
            for (int i = 0; i <= HoleBoard.getRows() - tokensToWin; i++) {
                for (int j = tokensToWin - 1; j < HoleBoard.getCols(); j++) {
                    int blackCount = 0;
                    int redCount = 0;
                    for (int k = 0; k < tokensToWin; k++) {
                        p = (Pawn) board.getElement(i + k, j - k);
                        if (p != null) {
                            if (p.getColor() == Pawn.PAWN_BLACK) {
                                blackCount++;
                            } else {
                                redCount++;
                            }
                        }
                    }
                    if (blackCount == tokensToWin) {
                        idWinner = 0;
                        break;
                    }
                    if (redCount == tokensToWin) {
                        idWinner = 1;
                        break;
                    }
                }
                if (idWinner != -1) break;
            }
        }

        // Si aucun alignement n'est trouvé, compter les jetons restants
        if (idWinner == -1) {
            for (int i = 0; i < HoleBoard.getRows(); i++) {
                for (int j = 0; j < HoleBoard.getCols(); j++) {
                    p = (Pawn) board.getElement(i, j);
                    if (p != null) {
                        if (p.getColor() == Pawn.PAWN_BLACK) {
                            nbBlack++;
                            countBlack += p.getNumber();
                        } else {
                            nbRed++;
                            countRed += p.getNumber();
                        }
                    }
                }
            }

            // Décider du gagnant en fonction du nombre de jetons
            if (nbBlack < nbRed) {
                idWinner = 0;
            }
            else if (nbBlack > nbRed) {
                idWinner = 1;
            }
            else {
                if (countBlack < countRed) {
                    idWinner = 0;
                }
                else if (countBlack > countRed) {
                    idWinner = 1;
                }
            }
        }

        System.out.println("nb black: "+nbBlack+", nb red: "+nbRed+", count black: "+countBlack+", count red: "+countRed+", winner is player "+idWinner);
        // set the winner
        winner = idWinner;
        // stop de the game
        model.stopStage();
    }

    @Override
    public StageElementsFactory getDefaultElementFactory() {
        return new HoleStageFactory(this);
    }
}
