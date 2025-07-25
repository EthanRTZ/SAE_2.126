import boardifier.control.ActionPlayer;
import boardifier.control.Controller;
import boardifier.control.Decider;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import boardifier.view.RootPane;
import boardifier.view.View;
import control.PuissanceXController;
import control.PuissanceXDecider;
import control.PuissanceXSmartDecider;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.ConsoleGameTracker;
import model.Pawn;
import model.PuissanceXBoard;
import model.PuissanceXPawnPot;
import model.PuissanceXStageModel;

public class PuissanceXFX extends Application {
    private Model model;
    private PuissanceXStageModel stageModel;
    private PuissanceXController controller;
    private PuissanceXBoard board;
    private GridPane gameBoard;
    private Label statusLabel;
    private Label scoreLabel;
    private PuissanceXPawnPot redPot;
    private PuissanceXPawnPot yellowPot;
    private HBox potsBox;
    private int nbCols;
    private int nbRows;
    private int nbAlign;
    private int computerLevel;
    private boolean gameOver = false;
    private View view;
    private Stage gameStage;
    private int scoreJoueur1 = 0;
    private int scoreJoueur2 = 0;
    private Label redPotLabel;
    private Label yellowPotLabel;

    @Override
    public void start(Stage primaryStage) {
        createConfigScene(primaryStage);
        
        // Add a handler for window closing
        primaryStage.setOnCloseRequest(event -> {
            // Completely reset the ConsoleGameTracker
            ConsoleGameTracker.resetInstance();
            
            // Close the game window if it's open
            if (gameStage != null) {
                gameStage.close();
            }
            
            // Reset static variables and resources
            resetStaticResources();
        });
    }

    private void createConfigScene(Stage stage) {
        VBox configBox = new VBox(20);
        configBox.setAlignment(Pos.CENTER);
        configBox.setPadding(new Insets(20));
        configBox.setStyle("-fx-background-color: #333333;");

        String labelStyle = "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;";

        Label modeLabel = new Label("Game mode");
        modeLabel.setStyle(labelStyle);
        ComboBox<String> modeCombo = new ComboBox<>();
        modeCombo.getItems().addAll("Player vs Player", "Player vs Computer", "Computer vs Computer");
        modeCombo.setValue("Player vs Computer");
        modeCombo.setStyle("-fx-font-size: 14px;");
        modeCombo.setPrefWidth(300);

        // Level for player 1 (or bot 1)
        Label levelLabel1 = new Label("Player 1 Level");
        levelLabel1.setStyle(labelStyle);
        ComboBox<String> levelCombo1 = new ComboBox<>();
        levelCombo1.getItems().addAll("Human", "Easy Bot", "Medium Bot");
        levelCombo1.setValue("Human");
        levelCombo1.setStyle("-fx-font-size: 14px;");
        levelCombo1.setPrefWidth(300);
        levelCombo1.setDisable(true); // Disabled by default

        // Level for player 2 (or bot 2)
        Label levelLabel2 = new Label("Player 2 Level");
        levelLabel2.setStyle(labelStyle);
        ComboBox<String> levelCombo2 = new ComboBox<>();
        levelCombo2.getItems().addAll("Human", "Easy Bot", "Medium Bot");
        levelCombo2.setValue("Medium Bot");
        levelCombo2.setStyle("-fx-font-size: 14px;");
        levelCombo2.setPrefWidth(300);

        // Handle enabling/disabling level combos based on mode
        modeCombo.setOnAction(e -> {
            switch (modeCombo.getValue()) {
                case "Player vs Player":
                    levelCombo1.setValue("Human");
                    levelCombo2.setValue("Human");
                    levelCombo1.setDisable(true);
                    levelCombo2.setDisable(true);
                    break;
                case "Player vs Computer":
                    levelCombo1.setValue("Human");
                    levelCombo2.setValue("Medium Bot");
                    levelCombo1.setDisable(true);
                    levelCombo2.setDisable(false);
                    break;
                case "Computer vs Computer":
                    levelCombo1.setValue("Medium Bot");
                    levelCombo2.setValue("Medium Bot");
                    levelCombo1.setDisable(false);
                    levelCombo2.setDisable(false);
                    break;
            }
        });

        Label colsLabel = new Label("Number of columns");
        colsLabel.setStyle(labelStyle);
        Spinner<Integer> colsSpinner = new Spinner<>(4, 12, 7);
        colsSpinner.setStyle("-fx-font-size: 14px;");
        colsSpinner.setPrefWidth(300);

        Label rowsLabel = new Label("Number of rows");
        rowsLabel.setStyle(labelStyle);
        Spinner<Integer> rowsSpinner = new Spinner<>(4, 12, 6);
        rowsSpinner.setStyle("-fx-font-size: 14px;");
        rowsSpinner.setPrefWidth(300);

        Label alignLabel = new Label("Number of pieces to align");
        alignLabel.setStyle(labelStyle);
        Spinner<Integer> alignSpinner = new Spinner<>(3, 8, 4);
        alignSpinner.setStyle("-fx-font-size: 14px;");
        alignSpinner.setPrefWidth(300);

        Button startButton = new Button("Start");
        startButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20 10 20; -fx-background-color: #444444; -fx-text-fill: white; -fx-border-color: #666666;");
        startButton.setOnAction(e -> {
            nbCols = colsSpinner.getValue();
            nbRows = rowsSpinner.getValue();
            nbAlign = alignSpinner.getValue();
            
            // Initialize the game with chosen levels
            String mode = modeCombo.getValue();
            String level1 = levelCombo1.getValue();
            String level2 = levelCombo2.getValue();
            
            initializeGame(mode, level1, level2);
            createGameScene();
        });

        configBox.getChildren().addAll(
            modeLabel, modeCombo,
            levelLabel1, levelCombo1,
            levelLabel2, levelCombo2,
            colsLabel, colsSpinner,
            rowsLabel, rowsSpinner,
            alignLabel, alignSpinner,
            startButton
        );

        Scene configScene = new Scene(configBox, 600, 600);
        configScene.setFill(Color.web("#333333"));
        stage.setTitle("Puissance X");
        stage.setScene(configScene);
        
        // Allow resizing and fullscreen
        stage.setResizable(true);
        stage.setMinWidth(500);
        stage.setMinHeight(500);
        
        stage.show();
    }

    private void initializeGame(String gameMode, String level1, String level2) {
        // Completely reset the game
        resetGame();
        resetStaticResources();
        
        model = new Model();
        gameStage = new Stage();
        RootPane rootPane = new RootPane();
        view = new View(model, gameStage, rootPane);
        rootPane.init(view.getGameStageView());
        
        // Add players based on chosen mode and their levels
        switch (gameMode) {
            case "Player vs Player":
                model.addHumanPlayer("Player 1");
                model.addHumanPlayer("Player 2");
                break;
            case "Player vs Computer":
                model.addHumanPlayer("Player 1");
                if (level2.equals("Easy Bot")) {
                    computerLevel = 0; // SmartDecider = easy
                    model.addComputerPlayer("Computer (Easy)");
                } else {
                    computerLevel = 1; // Decider = medium
                    model.addComputerPlayer("Computer (Medium)");
                }
                break;
            case "Computer vs Computer":
                // First bot
                if (level1.equals("Easy Bot")) {
                    computerLevel = 0; // SmartDecider = easy
                    model.addComputerPlayer("Computer 1 (Easy)");
                } else {
                    computerLevel = 1; // Decider = medium
                    model.addComputerPlayer("Computer 1 (Medium)");
                }
                // Second bot
                if (level2.equals("Easy Bot")) {
                    computerLevel = 0; // SmartDecider = easy
                    model.addComputerPlayer("Computer 2 (Easy)");
                } else {
                    computerLevel = 1; // Decider = medium
                    model.addComputerPlayer("Computer 2 (Medium)");
                }
                break;
        }
        
        // Create the stage model and controller
        stageModel = new PuissanceXStageModel("main", model);
        controller = new PuissanceXController(model, view);
        model.setGameStage(stageModel);

        // Create the board
        board = new PuissanceXBoard(80, 80, stageModel, nbRows, nbCols, nbAlign);
        stageModel.setBoard(board);
    }

    private void createGameScene() {
        // Check if the board is initialized
        if (board == null) {
            System.out.println("The board is not initialized in createGameScene, waiting...");
            try {
                Thread.sleep(1000);
                board = stageModel.getBoard();
                if (board == null) {
                    System.out.println("The board is still null after waiting");
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #333333;");

        // Style for labels
        String labelStyle = "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;";
        
        // Top panel (scores and status)
        VBox topBox = new VBox(15);
        topBox.setAlignment(Pos.CENTER);
        topBox.setStyle("-fx-background-color: #333333;");
        
        scoreLabel = new Label("Score: Player 1 (Red) : " + scoreJoueur1 + " - Player 2 (Yellow) : " + scoreJoueur2);
        scoreLabel.setStyle(labelStyle);
        
        statusLabel = new Label("Next player: " + model.getCurrentPlayer().getName());
        statusLabel.setStyle(labelStyle);
        
        topBox.getChildren().addAll(scoreLabel, statusLabel);
        root.setTop(topBox);
        
        // Center panel (game board)
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setStyle("-fx-background-color: #333333;");
        
        // Create the game board
        gameBoard = new GridPane();
        gameBoard.setHgap(10);
        gameBoard.setVgap(10);
        gameBoard.setAlignment(Pos.CENTER);
        gameBoard.setStyle("-fx-background-color: #333333;");
        
        updateBoard();
        
        centerBox.getChildren().add(gameBoard);
        root.setCenter(centerBox);
        
        // Left panel (red pot)
        VBox leftBox = new VBox(20);
        leftBox.setAlignment(Pos.CENTER);
        leftBox.setPadding(new Insets(0, 20, 0, 0));
        leftBox.setStyle("-fx-background-color: #333333;");
        leftBox.setMinWidth(150);
        
        Label redLabel = new Label("Player 1 (Red)");
        redLabel.setStyle(labelStyle + "; -fx-text-fill: red;");
        
        redPot = new PuissanceXPawnPot(80, 80, stageModel, (nbRows * nbCols + 1) / 2);
        stageModel.setRedPot(redPot);
        
        redPotLabel = new Label("Pieces remaining: " + redPot.getRemainingPawns());
        redPotLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: red;");
        
        VBox redPotInfo = new VBox(10);
        redPotInfo.setAlignment(Pos.CENTER);
        redPotInfo.setStyle("-fx-background-color: #444444; -fx-border-color: #666666; -fx-border-width: 2; -fx-padding: 20;");
        redPotInfo.setMinHeight(100);
        redPotInfo.getChildren().add(redPotLabel);
        
        leftBox.getChildren().addAll(redLabel, redPotInfo);
        root.setLeft(leftBox);
        
        // Right panel (yellow pot)
        VBox rightBox = new VBox(20);
        rightBox.setAlignment(Pos.CENTER);
        rightBox.setPadding(new Insets(0, 0, 0, 20));
        rightBox.setStyle("-fx-background-color: #333333;");
        rightBox.setMinWidth(150);
        
        Label yellowLabel = new Label("Player 2 (Yellow)");
        yellowLabel.setStyle(labelStyle + "; -fx-text-fill: yellow;");
        
        yellowPot = new PuissanceXPawnPot(80, 80, stageModel, nbRows * nbCols / 2);
        stageModel.setYellowPot(yellowPot);
        
        yellowPotLabel = new Label("Pieces remaining: " + yellowPot.getRemainingPawns());
        yellowPotLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: yellow;");
        
        VBox yellowPotInfo = new VBox(10);
        yellowPotInfo.setAlignment(Pos.CENTER);
        yellowPotInfo.setStyle("-fx-background-color: #444444; -fx-border-color: #666666; -fx-border-width: 2; -fx-padding: 20;");
        yellowPotInfo.setMinHeight(100);
        yellowPotInfo.getChildren().add(yellowPotLabel);
        
        rightBox.getChildren().addAll(yellowLabel, yellowPotInfo);
        root.setRight(rightBox);
        
        // Bottom panel (new game button when needed)
        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20, 0, 0, 0));
        bottomBox.setStyle("-fx-background-color: #333333;");
        
        // Add a button to toggle fullscreen
        Button fullscreenButton = new Button("Fullscreen (F11)");
        fullscreenButton.setStyle("-fx-font-size: 14px; -fx-padding: 8 16 8 16; -fx-background-color: #555555; -fx-text-fill: white; -fx-border-color: #777777;");
        fullscreenButton.setOnAction(e -> {
            gameStage.setFullScreen(!gameStage.isFullScreen());
            fullscreenButton.setText(gameStage.isFullScreen() ? "Exit fullscreen (F11)" : "Fullscreen (F11)");
        });
        
        bottomBox.getChildren().add(fullscreenButton);
        root.setBottom(bottomBox);
        
        Scene gameScene = new Scene(root, 1000, 800);
        gameScene.setFill(Color.web("#333333"));
        gameStage.setTitle("Puissance X - Game in progress");
        gameStage.setScene(gameScene);
        
        // Allow resizing and fullscreen
        gameStage.setResizable(true);
        gameStage.setMinWidth(800);
        gameStage.setMinHeight(600);
        
        // Add keyboard shortcut to toggle fullscreen (F11)
        gameScene.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.F11) {
                gameStage.setFullScreen(!gameStage.isFullScreen());
            }
        });
        
        gameStage.show();

        // If it's a computer turn, play automatically after the scene is initialized
        if (model.getCurrentPlayer().getName().toLowerCase().contains("computer")) {
            Platform.runLater(() -> {
                try {
                    Thread.sleep(1000); // Wait for everything to be properly initialized
                    if (board != null) {
                        playComputerTurn();
                    } else {
                        System.out.println("The board is still null, cannot play");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void updateBoard() {
        gameBoard.getChildren().clear();
        
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                // Create a container for the cell
                StackPane cell = new StackPane();
                cell.setPrefSize(70, 70);
                cell.setStyle("-fx-background-color: #333333; -fx-border-color: #666666; -fx-border-width: 2;");
                
                // Create the circle for the pawn
                Circle circle = new Circle(30);
                circle.setFill(Color.WHITE);
                circle.setStroke(Color.GRAY);
                circle.setStrokeWidth(2);
                
                int value = board.getGrid()[i][j];
                if (value == Pawn.PAWN_RED) {
                    circle.setFill(Color.RED);
                    circle.setStroke(Color.DARKRED);
                } else if (value == Pawn.PAWN_YELLOW) {
                    circle.setFill(Color.YELLOW);
                    circle.setStroke(Color.GOLDENROD);
                }
                
                cell.getChildren().add(circle);
                
                final int col = j;
                cell.setOnMouseClicked(e -> {
                    if (!gameOver && !model.getCurrentPlayer().getName().toLowerCase().contains("computer")) {
                        handlePlayerMove(col);
                    }
                });
                
                gameBoard.add(cell, j, i);
            }
        }
    }

    private void updateBoard(int lastRow, int lastCol, int color) {
        gameBoard.getChildren().clear();
        
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                // Créer un conteneur pour la cellule
                StackPane cell = new StackPane();
                cell.setPrefSize(70, 70);
                cell.setStyle("-fx-background-color: #333333; -fx-border-color: #666666; -fx-border-width: 2;");
                
                // Créer le cercle pour le pion
                Circle circle = new Circle(30);
                circle.setFill(Color.WHITE);
                circle.setStroke(Color.GRAY);
                circle.setStrokeWidth(2);
                
                int value = board.getGrid()[i][j];
                if (value == Pawn.PAWN_RED) {
                    circle.setFill(Color.RED);
                    circle.setStroke(Color.DARKRED);
                } else if (value == Pawn.PAWN_YELLOW) {
                    circle.setFill(Color.YELLOW);
                    circle.setStroke(Color.GOLDENROD);
                }
                
                cell.getChildren().add(circle);
                
                final int col = j;
                cell.setOnMouseClicked(e -> {
                    if (!gameOver && !model.getCurrentPlayer().getName().toLowerCase().contains("computer")) {
                        handlePlayerMove(col);
                    }
                });
                
                gameBoard.add(cell, j, i);
            }
        }
        
        // Mettre à jour les labels des pots
        redPotLabel.setText("Pieces remaining: " + redPot.getRemainingPawns());
        yellowPotLabel.setText("Pieces remaining: " + yellowPot.getRemainingPawns());
    }

    private void handlePlayerMove(int col) {
        if (board.isColumnFull(col)) {
            statusLabel.setText("This column is full! Choose another column.");
            return;
        }

        int row = board.getFirstEmptyRow(col);
        int color = model.getIdPlayer() == 0 ? Pawn.PAWN_RED : Pawn.PAWN_YELLOW;
        
        // Vérifier s'il reste des pions dans le pot
        PuissanceXPawnPot currentPot = color == Pawn.PAWN_RED ? redPot : yellowPot;
        if (currentPot.getRemainingPawns() <= 0) {
            statusLabel.setText("No more pieces available!");
            return;
        }
        
        board.getGrid()[row][col] = color;
        currentPot.removeElement(null); // Retirer un pion du pot
        
        updateBoard(row, col, color);
        checkGameStatus(row, col, color);
    }

    private void playComputerTurn() {
        System.out.println("=== START playComputerTurn ===");
        
        // Créer le decider approprié selon le niveau
        Decider decider;
        if (computerLevel == 0) {
            // Niveau facile = SmartDecider
            decider = new PuissanceXSmartDecider(model, controller);
            ((PuissanceXSmartDecider) decider).setStage(stageModel);
        } else {
            // Niveau moyen = Decider
            decider = new PuissanceXDecider(model, controller);
            ((PuissanceXDecider) decider).setStage(stageModel);
        }
        System.out.println("IA created: " + decider.getClass().getSimpleName() + " (Level: " + (computerLevel == 0 ? "Easy" : "Medium") + ")");
        
        // Vérifier l'état du plateau après création de l'IA
        System.out.println("État du plateau après création de l'IA:");
        System.out.println("- board: " + (board != null ? "non null" : "null"));
        System.out.println("- stageModel: " + (stageModel != null ? "non null" : "null"));
        if (stageModel != null) {
            System.out.println("- stageModel.getBoard(): " + (stageModel.getBoard() != null ? "non null" : "null"));
        }
        
        ActionList actions = decider.decide();
        System.out.println("Actions returned by the IA: " + (actions != null ? "non null" : "null"));
        
        if (actions == null) {
            System.out.println("The IA couldn't play, retrying in 1 second");
            Platform.runLater(() -> {
                try {
                    Thread.sleep(1000);
                    playComputerTurn();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            return;
        }
        
        // Utiliser directement stageModel au lieu de récupérer un nouveau stage
        System.out.println("État du plateau après récupération du stage:");
        System.out.println("- stageModel: " + (stageModel != null ? "non null" : "null"));
        if (stageModel != null) {
            System.out.println("- stageModel.getBoard(): " + (stageModel.getBoard() != null ? "non null" : "null"));
        }
        
        int currentPlayer = model.getIdPlayer();
        int color = currentPlayer == 0 ? Pawn.PAWN_RED : Pawn.PAWN_YELLOW;
        
        // Trouver la dernière position jouée en cherchant la position la plus haute dans chaque colonne
        int lastRow = -1;
        int lastCol = -1;
        
        // Parcourir chaque colonne
        for (int col = 0; col < board.getNbCols(); col++) {
            // Chercher le pion le plus haut de la couleur actuelle
            for (int row = 0; row < board.getNbRows(); row++) {
                if (board.getGrid()[row][col] == color) {
                    // Si c'est le premier pion trouvé ou s'il est plus haut que le précédent
                    if (lastRow == -1 || row < lastRow) {
                        lastRow = row;
                        lastCol = col;
                    }
                    break; // Passer à la colonne suivante une fois qu'on a trouvé un pion
                }
            }
        }
        
        System.out.println("Last played position: row=" + lastRow + ", col=" + lastCol + ", color=" + color);
        
        updateBoard(lastRow, lastCol, color);
        checkGameStatus(lastRow, lastCol, color);
        System.out.println("=== END playComputerTurn ===");
    }

    private void checkGameStatus(int row, int col, int color) {
        System.out.println("=== START checkGameStatus ===");
        System.out.println("Parameters: row=" + row + ", col=" + col + ", color=" + color);
        
        VBox bottomBox = (VBox) ((BorderPane) gameBoard.getParent().getParent()).getBottom();
        
        // Update the console tracker
        ConsoleGameTracker tracker = ConsoleGameTracker.getInstance(nbRows, nbCols, nbAlign);
        tracker.updateGrid(board.getGrid());
        
        // Check for victory using both the normal board and console tracker
        boolean winBoard = row != -1 && board.checkWin(row, col, color);
        boolean winTracker = row != -1 && tracker.checkWin(row, col, color);
        
        if (winBoard || winTracker) {
            System.out.println("Victory detected!");
            if (winBoard) System.out.println("- Detected by the board");
            if (winTracker) System.out.println("- Detected by the console tracker");
            
            gameOver = true;
            String gagnant = model.getCurrentPlayer().getName();
            if (color == Pawn.PAWN_RED) {
                scoreJoueur1++;
                statusLabel.setTextFill(Color.RED);
            } else {
                scoreJoueur2++;
                statusLabel.setTextFill(Color.YELLOW);
            }
            scoreLabel.setText("Score: Player 1 (Red) : " + scoreJoueur1 + " - Player 2 (Yellow) : " + scoreJoueur2);
            statusLabel.setText(gagnant + " has won!");
            
            // Propose a new game
            Button newGameButton = new Button("New Game");
            newGameButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20 10 20; -fx-background-color: #444444; -fx-text-fill: white; -fx-border-color: #666666;");
            newGameButton.setOnAction(e -> {
                resetGame();
                tracker.reset();
                bottomBox.getChildren().clear();
                statusLabel.setTextFill(Color.WHITE);
                
                // Reset the model to start with the first player
                if (model != null) {
                    model.setIdPlayer(0);
                    String firstPlayer = model.getCurrentPlayer().getName();
                    statusLabel.setText("Next player: " + firstPlayer);
                    statusLabel.setTextFill(Color.RED);
                    
                    // If the first player is a computer, start their turn
                    if (firstPlayer.toLowerCase().contains("computer")) {
                        Platform.runLater(() -> {
                            try {
                                Thread.sleep(1000);
                                playComputerTurn();
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        });
                    }
                }
            });
            
            bottomBox.getChildren().add(newGameButton);
        } 
        else if (board.isBoardFull()) {
            System.out.println("Draw detected!");
            gameOver = true;
            statusLabel.setText("Draw!");
            statusLabel.setTextFill(Color.LIGHTGRAY);
            
            // Propose a new game
            Button newGameButton = new Button("New Game");
            newGameButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20 10 20; -fx-background-color: #444444; -fx-text-fill: white; -fx-border-color: #666666;");
            newGameButton.setOnAction(e -> {
                resetGame();
                tracker.reset();
                bottomBox.getChildren().clear();
                statusLabel.setTextFill(Color.WHITE);
                
                // Reset the model to start with the first player
                if (model != null) {
                    model.setIdPlayer(0);
                    String firstPlayer = model.getCurrentPlayer().getName();
                    statusLabel.setText("Next player: " + firstPlayer);
                    statusLabel.setTextFill(Color.RED);
                    
                    // If the first player is a computer, start their turn
                    if (firstPlayer.toLowerCase().contains("computer")) {
                        Platform.runLater(() -> {
                            try {
                                Thread.sleep(1000);
                                playComputerTurn();
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        });
                    }
                }
            });
            
            bottomBox.getChildren().add(newGameButton);
        } 
        else {
            System.out.println("Game continues, switching to next player");
            model.setNextPlayer();
            String nextPlayer = model.getCurrentPlayer().getName();
            System.out.println("Next player: " + nextPlayer);
            statusLabel.setText("Next player: " + nextPlayer);
            statusLabel.setTextFill(model.getIdPlayer() == 0 ? Color.RED : Color.YELLOW);
            
            if (nextPlayer.toLowerCase().contains("computer")) {
                System.out.println("Next player is a computer, launching playComputerTurn in 500ms");
                // Add a delay before playing the next move
                new Thread(() -> {
                    try {
                        Thread.sleep(500);
                        javafx.application.Platform.runLater(() -> playComputerTurn());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
        System.out.println("=== END checkGameStatus ===");
    }

    private void resetGame() {
        gameOver = false;
        
        // DO NOT reset scores here - they must persist between games
        // scoreJoueur1 = 0;
        // scoreJoueur2 = 0;
        
        // Reset the console tracker
        ConsoleGameTracker tracker = ConsoleGameTracker.getInstance(nbRows, nbCols, nbAlign);
        if (tracker != null) {
            tracker.reset();
        }
        
        // Reset game variables
        if (board != null) {
            board.clear();
        }
        
        // Reset pots if they exist
        if (stageModel != null) {
            redPot = new PuissanceXPawnPot(80, 80, stageModel, (nbRows * nbCols + 1) / 2);
            yellowPot = new PuissanceXPawnPot(80, 80, stageModel, nbRows * nbCols / 2);
            stageModel.setRedPot(redPot);
            stageModel.setYellowPot(yellowPot);
        }
        
        // Update score display
        if (scoreLabel != null) {
            scoreLabel.setText("Score: Player 1 (Red) : " + scoreJoueur1 + " - Player 2 (Yellow) : " + scoreJoueur2);
        }
        
        // Reset interface if it exists
        if (gameBoard != null) {
            BorderPane root = (BorderPane) gameBoard.getParent().getParent();
            
            // Recreate left container (red pot)
            VBox leftBox = new VBox(20);
            leftBox.setAlignment(Pos.CENTER);
            leftBox.setPadding(new Insets(0, 20, 0, 0));
            leftBox.setStyle("-fx-background-color: #333333;");
            leftBox.setMinWidth(150);
            
            Label redLabel = new Label("Player 1 (Red)");
            redLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: red;");
            
            redPotLabel = new Label("Pieces remaining: " + (redPot != null ? redPot.getRemainingPawns() : 0));
            redPotLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: red;");
            
            VBox redPotInfo = new VBox(10);
            redPotInfo.setAlignment(Pos.CENTER);
            redPotInfo.setStyle("-fx-background-color: #444444; -fx-border-color: #666666; -fx-border-width: 2; -fx-padding: 20;");
            redPotInfo.setMinHeight(100);
            redPotInfo.getChildren().add(redPotLabel);
            
            leftBox.getChildren().addAll(redLabel, redPotInfo);
            root.setLeft(leftBox);
            
            // Recreate right container (yellow pot)
            VBox rightBox = new VBox(20);
            rightBox.setAlignment(Pos.CENTER);
            rightBox.setPadding(new Insets(0, 0, 0, 20));
            rightBox.setStyle("-fx-background-color: #333333;");
            rightBox.setMinWidth(150);
            
            Label yellowLabel = new Label("Player 2 (Yellow)");
            yellowLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: yellow;");
            
            yellowPotLabel = new Label("Pieces remaining: " + (yellowPot != null ? yellowPot.getRemainingPawns() : 0));
            yellowPotLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: yellow;");
            
            VBox yellowPotInfo = new VBox(10);
            yellowPotInfo.setAlignment(Pos.CENTER);
            yellowPotInfo.setStyle("-fx-background-color: #444444; -fx-border-color: #666666; -fx-border-width: 2; -fx-padding: 20;");
            yellowPotInfo.setMinHeight(100);
            yellowPotInfo.getChildren().add(yellowPotLabel);
            
            rightBox.getChildren().addAll(yellowLabel, yellowPotInfo);
            root.setRight(rightBox);
            
            // Update the board
            updateBoard();
        }
        
        // Reset status
        if (statusLabel != null) {
            statusLabel.setTextFill(Color.WHITE);
        }
    }

    // Méthode pour réinitialiser les ressources statiques
    private void resetStaticResources() {
        // Réinitialiser le modèle et les composants du jeu
        model = null;
        stageModel = null;
        controller = null;
        board = null;
        gameBoard = null;
        redPot = null;
        yellowPot = null;
        gameOver = false;
        view = null;
        gameStage = null;
        
        // Réinitialiser les scores
        scoreJoueur1 = 0;
        scoreJoueur2 = 0;
    }

    public static void main(String[] args) {
        launch(args);
    }
} 