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
import control.PuissanceXHardDecider;
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

    private boolean isDarkTheme = true; // Thème sombre par défaut
    private Scene configScene;
    private Scene gameScene;
    private BorderPane gameRoot;
    private VBox configBox;

    private String player1Name = "Player 1 (Red)";
    private String player2Name = "Player 2 (Yellow)";

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
        configBox = new VBox(20);
        configBox.setAlignment(Pos.CENTER);
        configBox.setPadding(new Insets(30));
        // Style appliqué plus bas via applyTheme()

        String labelStyle = "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #f5f5f5; -fx-effect: dropshadow(gaussian, #00000055, 2, 0.2, 0, 1);";

        Label modeLabel = new Label("Mode de jeu");
        modeLabel.setStyle(labelStyle);
        ComboBox<String> modeCombo = new ComboBox<>();
        modeCombo.getItems().addAll("Player vs Player", "Player vs Computer", "Computer vs Computer");
        modeCombo.setValue("Player vs Computer");
        modeCombo.setStyle("-fx-font-size: 16px; -fx-background-radius: 10px; -fx-border-radius: 10px;");
        modeCombo.setPrefWidth(320);

        Label levelLabel1 = new Label("Niveau Joueur 1");
        levelLabel1.setStyle(labelStyle);
        ComboBox<String> levelCombo1 = new ComboBox<>();
        levelCombo1.setStyle("-fx-font-size: 16px; -fx-background-radius: 10px; -fx-border-radius: 10px;");
        levelCombo1.setPrefWidth(320);

        Label levelLabel2 = new Label("Niveau Joueur 2");
        levelLabel2.setStyle(labelStyle);
        ComboBox<String> levelCombo2 = new ComboBox<>();
        levelCombo2.setStyle("-fx-font-size: 16px; -fx-background-radius: 10px; -fx-border-radius: 10px;");
        levelCombo2.setPrefWidth(320);

        levelCombo1.getItems().setAll("Human");
        levelCombo1.setValue("Human");
        levelCombo1.setDisable(true);

        levelCombo2.getItems().setAll("Easy Bot", "Medium Bot", "Hard Bot");
        levelCombo2.setValue("Medium Bot");
        levelCombo2.setDisable(false);

        modeCombo.setOnAction(e -> {
            switch (modeCombo.getValue()) {
                case "Player vs Player":
                    levelCombo1.getItems().setAll("Human");
                    levelCombo2.getItems().setAll("Human");
                    levelCombo1.setValue("Human");
                    levelCombo2.setValue("Human");
                    levelCombo1.setDisable(true);
                    levelCombo2.setDisable(true);
                    break;
                case "Player vs Computer":
                    levelCombo1.getItems().setAll("Human");
                    levelCombo2.getItems().setAll("Easy Bot", "Medium Bot", "Hard Bot");
                    levelCombo1.setValue("Human");
                    levelCombo2.setValue("Medium Bot");
                    levelCombo1.setDisable(true);
                    levelCombo2.setDisable(false);
                    break;
                case "Computer vs Computer":
                    levelCombo1.getItems().setAll("Easy Bot", "Medium Bot", "Hard Bot");
                    levelCombo2.getItems().setAll("Easy Bot", "Medium Bot", "Hard Bot");
                    levelCombo1.setValue("Medium Bot");
                    levelCombo2.setValue("Medium Bot");
                    levelCombo1.setDisable(false);
                    levelCombo2.setDisable(false);
                    break;
            }
        });

        Label colsLabel = new Label("Nombre de colonnes");
        colsLabel.setStyle(labelStyle);
        Spinner<Integer> colsSpinner = new Spinner<>(4, 12, 7);
        colsSpinner.setStyle("-fx-font-size: 16px; -fx-background-radius: 10px; -fx-border-radius: 10px;");
        colsSpinner.setPrefWidth(320);

        Label rowsLabel = new Label("Nombre de lignes");
        rowsLabel.setStyle(labelStyle);
        Spinner<Integer> rowsSpinner = new Spinner<>(4, 12, 6);
        rowsSpinner.setStyle("-fx-font-size: 16px; -fx-background-radius: 10px; -fx-border-radius: 10px;");
        rowsSpinner.setPrefWidth(320);

        Label alignLabel = new Label("Pions à aligner");
        alignLabel.setStyle(labelStyle);
        Spinner<Integer> alignSpinner = new Spinner<>(3, 8, 4);
        alignSpinner.setStyle("-fx-font-size: 16px; -fx-background-radius: 10px; -fx-border-radius: 10px;");
        alignSpinner.setPrefWidth(320);

        Button startButton = new Button("Démarrer la partie");
        startButton.setStyle("-fx-font-size: 18px; -fx-padding: 12 32 12 32; -fx-background-color: linear-gradient(from 0% 0% to 100% 0%, #ff512f 0%, #dd2476 100%);"
            + "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-effect: dropshadow(gaussian, #00000055, 4, 0.2, 0, 2);");
        startButton.setOnMouseEntered(ev -> startButton.setStyle("-fx-font-size: 18px; -fx-padding: 12 32 12 32; -fx-background-color: linear-gradient(from 0% 0% to 100% 0%, #dd2476 0%, #ff512f 100%);"
            + "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-effect: dropshadow(gaussian, #00000099, 8, 0.3, 0, 4);"));
        startButton.setOnMouseExited(ev -> startButton.setStyle("-fx-font-size: 18px; -fx-padding: 12 32 12 32; -fx-background-color: linear-gradient(from 0% 0% to 100% 0%, #ff512f 0%, #dd2476 100%);"
            + "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-effect: dropshadow(gaussian, #00000055, 4, 0.2, 0, 2);"));
        startButton.setOnAction(e -> {
            nbCols = colsSpinner.getValue();
            nbRows = rowsSpinner.getValue();
            nbAlign = alignSpinner.getValue();
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

        Button themeButton = new Button("Thème clair");
        themeButton.setStyle("-fx-font-size: 14px; -fx-padding: 8 20 8 20; -fx-background-radius: 12px; -fx-background-color: #444; -fx-text-fill: #fff;");
        themeButton.setOnAction(e -> {
            isDarkTheme = !isDarkTheme;
            applyTheme();
            themeButton.setText(isDarkTheme ? "Thème clair" : "Thème sombre");
        });

        configBox.getChildren().add(themeButton);

        configScene = new Scene(configBox, 600, 650);
        applyTheme();
        stage.setTitle("Puissance X");
        stage.setScene(configScene);
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
                player1Name = "Player 1 (Red)";
                player2Name = "Player 2 (Yellow)";
                break;
            case "Player vs Computer":
                model.addHumanPlayer("Player 1");
                player1Name = "Player 1 (Red)";

                // Ajout du niveau "Hard Bot"
                if (level2.equals("Easy Bot")) {
                    computerLevel = 0; // SmartDecider = easy
                    model.addComputerPlayer("Computer (Easy)");
                    player2Name = "Computer (Easy)";
                } else if (level2.equals("Hard Bot")) {
                    computerLevel = 2; // HardDecider = hard
                    model.addComputerPlayer("Computer (Hard)");
                    player2Name = "Computer (Hard)";
                } else {
                    computerLevel = 1; // Decider = medium
                    model.addComputerPlayer("Computer (Medium)");
                    player2Name = "Computer (Medium)";
                }
                break;
            case "Computer vs Computer":
                // First bot
                if (level1.equals("Easy Bot")) {
                    computerLevel = 0; // SmartDecider = easy
                    model.addComputerPlayer("Computer 1 (Easy)");
                    player1Name = "Computer 1 (Easy)";
                } else if (level1.equals("Hard Bot")) {
                    computerLevel = 2; // HardDecider = hard
                    model.addComputerPlayer("Computer 1 (Hard)");
                    player1Name = "Computer 1 (Hard)";
                } else {
                    computerLevel = 1; // Decider = medium
                    model.addComputerPlayer("Computer 1 (Medium)");
                    player1Name = "Computer 1 (Medium)";
                }
                // Second bot
                if (level2.equals("Easy Bot")) {
                    computerLevel = 0; // SmartDecider = easy
                    model.addComputerPlayer("Computer 2 (Easy)");
                    player2Name = "Computer 2 (Easy)";
                } else if (level2.equals("Hard Bot")) {
                    computerLevel = 2; // HardDecider = hard
                    model.addComputerPlayer("Computer 2 (Hard)");
                    player2Name = "Computer 2 (Hard)";
                } else {
                    computerLevel = 1; // Decider = medium
                    model.addComputerPlayer("Computer 2 (Medium)");
                    player2Name = "Computer 2 (Medium)";
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
        
        gameRoot = new BorderPane();
        gameRoot.setPadding(new Insets(40));
        // Style appliqué plus bas via applyTheme()

        String labelStyle = "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #f5f5f5; -fx-effect: dropshadow(gaussian, #00000055, 2, 0.2, 0, 1);";

        VBox topBox = new VBox(15);
        topBox.setAlignment(Pos.CENTER);
        topBox.setStyle("-fx-background-color: transparent;");

        scoreLabel = new Label("Score: " + player1Name + " : " + scoreJoueur1 + " - " + player2Name + " : " + scoreJoueur2);
        scoreLabel.setStyle(labelStyle);

        statusLabel = new Label("Next player: " + model.getCurrentPlayer().getName());
        statusLabel.setStyle(labelStyle);

        topBox.getChildren().addAll(scoreLabel, statusLabel);
        gameRoot.setTop(topBox);

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setStyle("-fx-background-color: transparent;");

        gameBoard = new GridPane();
        gameBoard.setHgap(10);
        gameBoard.setVgap(10);
        gameBoard.setAlignment(Pos.CENTER);
        gameBoard.setStyle("-fx-background-color: transparent;");

        updateBoard();

        centerBox.getChildren().add(gameBoard);
        gameRoot.setCenter(centerBox);

        VBox leftBox = new VBox(20);
        leftBox.setAlignment(Pos.CENTER);
        leftBox.setPadding(new Insets(0, 20, 0, 0));
        leftBox.setStyle("-fx-background-color: transparent;");
        leftBox.setMinWidth(150);

        Label redLabel = new Label(player1Name);
        redLabel.setStyle(labelStyle + "; -fx-text-fill: #ff4b2b;");

        redPot = new PuissanceXPawnPot(80, 80, stageModel, (nbRows * nbCols + 1) / 2);
        stageModel.setRedPot(redPot);

        redPotLabel = new Label("Pieces remaining: " + redPot.getRemainingPawns());
        redPotLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ff4b2b;");

        VBox redPotInfo = new VBox(10);
        redPotInfo.setAlignment(Pos.CENTER);
        // Style dynamique appliqué dans applyTheme()
        redPotInfo.setMinHeight(100);
        redPotInfo.setStyle("-fx-background-color: #2d2d2d; -fx-border-color: #ff4b2b; -fx-border-width: 2; -fx-padding: 20; -fx-background-radius: 15px; -fx-border-radius: 15px;");
        redPotInfo.getChildren().add(redPotLabel);

        leftBox.getChildren().addAll(redLabel, redPotInfo);
        gameRoot.setLeft(leftBox);

        VBox rightBox = new VBox(20);
        rightBox.setAlignment(Pos.CENTER);
        rightBox.setPadding(new Insets(0, 0, 0, 20));
        rightBox.setStyle("-fx-background-color: transparent;");
        rightBox.setMinWidth(150);

        Label yellowLabel = new Label(player2Name);
        yellowLabel.setStyle(labelStyle + "; -fx-text-fill: #ffe259;");

        yellowPot = new PuissanceXPawnPot(80, 80, stageModel, nbRows * nbCols / 2);
        stageModel.setYellowPot(yellowPot);

        yellowPotLabel = new Label("Pieces remaining: " + yellowPot.getRemainingPawns());
        yellowPotLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffe259;");

        VBox yellowPotInfo = new VBox(10);
        yellowPotInfo.setAlignment(Pos.CENTER);
        // Style dynamique appliqué dans applyTheme()
        yellowPotInfo.setMinHeight(100);
        yellowPotInfo.setStyle("-fx-background-color: #2d2d2d; -fx-border-color: #ffe259; -fx-border-width: 2; -fx-padding: 20; -fx-background-radius: 15px; -fx-border-radius: 15px;");
        yellowPotInfo.getChildren().add(yellowPotLabel);

        rightBox.getChildren().addAll(yellowLabel, yellowPotInfo);
        gameRoot.setRight(rightBox);

        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20, 0, 0, 0));
        bottomBox.setStyle("-fx-background-color: transparent;");

        Button fullscreenButton = new Button("Plein écran (F11)");
        fullscreenButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 24 10 24; -fx-background-color: linear-gradient(from 0% 0% to 100% 0%, #36d1c4 0%, #1e90ff 100%);"
            + "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 16px; -fx-border-radius: 16px; -fx-effect: dropshadow(gaussian, #00000055, 4, 0.2, 0, 2);");
        fullscreenButton.setOnMouseEntered(ev -> fullscreenButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 24 10 24; -fx-background-color: linear-gradient(from 0% 0% to 100% 0%, #1e90ff 0%, #36d1c4 100%);"
            + "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 16px; -fx-border-radius: 16px; -fx-effect: dropshadow(gaussian, #00000099, 8, 0.3, 0, 4);"));
        fullscreenButton.setOnMouseExited(ev -> fullscreenButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 24 10 24; -fx-background-color: linear-gradient(from 0% 0% to 100% 0%, #36d1c4 0%, #1e90ff 100%);"
            + "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 16px; -fx-border-radius: 16px; -fx-effect: dropshadow(gaussian, #00000055, 4, 0.2, 0, 2);"));
        fullscreenButton.setOnAction(e -> {
            gameStage.setFullScreen(!gameStage.isFullScreen());
            fullscreenButton.setText(gameStage.isFullScreen() ? "Quitter le plein écran (F11)" : "Plein écran (F11)");
        });

        Button themeButton = new Button(isDarkTheme ? "Thème clair" : "Thème sombre");
        themeButton.setStyle("-fx-font-size: 14px; -fx-padding: 8 20 8 20; -fx-background-radius: 12px; -fx-background-color: #444; -fx-text-fill: #fff;");
        themeButton.setOnAction(e -> {
            isDarkTheme = !isDarkTheme;
            applyTheme();
            themeButton.setText(isDarkTheme ? "Thème clair" : "Thème sombre");
        });

        bottomBox.getChildren().addAll(fullscreenButton, themeButton);
        gameRoot.setBottom(bottomBox);

        gameScene = new Scene(gameRoot, 1000, 800);
        applyTheme();
        gameStage.setTitle("Puissance X - Partie en cours");
        gameStage.setScene(gameScene);
        gameStage.setResizable(true);
        gameStage.setMinWidth(800);
        gameStage.setMinHeight(600);

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

    // Ajout des références pour le style dynamique
    private VBox redPotInfo;
    private VBox yellowPotInfo;

    // Applique le thème sombre ou clair à toutes les scènes et composants principaux
    private void applyTheme() {
        // Correction : syntaxe JavaFX pour linear-gradient
        String darkBg = "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #232526 0%, #414345 100%);";
        String darkBox = "-fx-background-color: #232526;";
        String darkBorder = "-fx-border-radius: 20px; -fx-background-radius: 20px; -fx-effect: dropshadow(gaussian, #00000088, 30, 0.3, 0, 8);";
        String lightBg = "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #f5f5f5 0%, #e0e0e0 100%);";
        String lightBox = "-fx-background-color: #f5f5f5;";
        String lightBorder = "-fx-border-radius: 20px; -fx-background-radius: 20px; -fx-effect: dropshadow(gaussian, #bbbbbb88, 30, 0.3, 0, 8);";

        if (configBox != null) {
            configBox.setStyle((isDarkTheme ? darkBg : lightBg) + (isDarkTheme ? darkBorder : lightBorder));
            for (javafx.scene.Node node : configBox.getChildren()) {
                if (node instanceof Label) {
                    ((Label) node).setStyle(isDarkTheme
                        ? "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #f5f5f5;"
                        : "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #232526;");
                }
            }
        }
        if (configScene != null) {
            configScene.setFill(isDarkTheme ? Color.web("#232526") : Color.web("#f5f5f5"));
        }
        if (gameRoot != null) {
            gameRoot.setStyle((isDarkTheme ? darkBg : lightBg) + (isDarkTheme ? darkBorder : lightBorder));
        }
        if (gameScene != null) {
            gameScene.setFill(isDarkTheme ? Color.web("#232526") : Color.web("#f5f5f5"));
        }

        // Garder le fond noir pour les rectangles d'informations des pions restants
        // mais adapter le reste au thème
        if (redPotInfo != null) {
            redPotInfo.setStyle("-fx-background-color: #2d2d2d; -fx-border-color: #ff4b2b; -fx-border-width: 2; -fx-padding: 20; -fx-background-radius: 15px; -fx-border-radius: 15px;");
        }
        if (yellowPotInfo != null) {
            yellowPotInfo.setStyle("-fx-background-color: #2d2d2d; -fx-border-color: #ffe259; -fx-border-width: 2; -fx-padding: 20; -fx-background-radius: 15px; -fx-border-radius: 15px;");
        }

        // Adapter d'autres éléments selon le thème
        if (gameRoot != null) {
            for (javafx.scene.Node node : gameRoot.getChildren()) {
                if (node instanceof VBox && (node == gameRoot.getTop() || node == gameRoot.getCenter() || node == gameRoot.getBottom())) {
                    ((VBox) node).setStyle("-fx-background-color: transparent;");

                    // Adapter les textes au thème choisi
                    for (javafx.scene.Node child : ((VBox) node).getChildren()) {
                        if (child instanceof Label && child != redPotLabel && child != yellowPotLabel) {
                            ((Label) child).setStyle(isDarkTheme
                                ? "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #f5f5f5;"
                                : "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #232526;");
                        }
                    }
                }
            }
        }
    }

    private void updateBoard() {
        gameBoard.getChildren().clear();
        int rows = board.getNbRows();
        int cols = board.getNbCols();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(70, 70);
                cell.setStyle("-fx-background-color: #232526; -fx-border-color: #888888; -fx-border-width: 2; -fx-background-radius: 50px; -fx-border-radius: 50px;");

                Circle circle = new Circle(30);
                circle.setFill(Color.web("#f5f5f5"));
                circle.setStroke(Color.web("#bbbbbb"));
                circle.setStrokeWidth(2);

                // Cercle intérieur plus clair
                Circle innerCircle = new Circle(18);
                innerCircle.setStrokeWidth(0);

                int value = board.getGrid()[i][j];
                if (value == Pawn.PAWN_RED) {
                    circle.setFill(Color.web("#ff4b2b"));
                    circle.setStroke(Color.web("#b22222"));
                    innerCircle.setFill(Color.web("#ffb3a7")); // rouge clair
                } else if (value == Pawn.PAWN_YELLOW) {
                    circle.setFill(Color.web("#ffe259"));
                    circle.setStroke(Color.web("#bdb76b"));
                    innerCircle.setFill(Color.web("#fff9b0")); // jaune clair
                } else {
                    innerCircle.setFill(Color.web("#ffffff")); // blanc pour case vide
                }

                cell.getChildren().addAll(circle, innerCircle);

                final int col = j;
                cell.setOnMouseEntered(e -> cell.setStyle("-fx-background-color: #444444; -fx-border-color: #36d1c4; -fx-border-width: 3; -fx-background-radius: 50px; -fx-border-radius: 50px;"));
                cell.setOnMouseExited(e -> cell.setStyle("-fx-background-color: #232526; -fx-border-color: #888888; -fx-border-width: 2; -fx-background-radius: 50px; -fx-border-radius: 50px;"));
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
        int rows = board.getNbRows();
        int cols = board.getNbCols();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(70, 70);
                cell.setStyle("-fx-background-color: #232526; -fx-border-color: #888888; -fx-border-width: 2; -fx-background-radius: 50px; -fx-border-radius: 50px;");

                Circle circle = new Circle(30);
                circle.setFill(Color.web("#f5f5f5"));
                circle.setStroke(Color.web("#bbbbbb"));
                circle.setStrokeWidth(2);

                // Cercle intérieur plus clair
                Circle innerCircle = new Circle(18);
                innerCircle.setStrokeWidth(0);

                int value = board.getGrid()[i][j];
                if (value == Pawn.PAWN_RED) {
                    circle.setFill(Color.web("#ff4b2b"));
                    circle.setStroke(Color.web("#b22222"));
                    innerCircle.setFill(Color.web("#ffb3a7")); // rouge clair
                } else if (value == Pawn.PAWN_YELLOW) {
                    circle.setFill(Color.web("#ffe259"));
                    circle.setStroke(Color.web("#bdb76b"));
                    innerCircle.setFill(Color.web("#fff9b0")); // jaune clair
                } else {
                    innerCircle.setFill(Color.web("#ffffff")); // blanc pour case vide
                }

                cell.getChildren().addAll(circle, innerCircle);

                final int col = j;
                cell.setOnMouseEntered(e -> cell.setStyle("-fx-background-color: #444444; -fx-border-color: #36d1c4; -fx-border-width: 3; -fx-background-radius: 50px; -fx-border-radius: 50px;"));
                cell.setOnMouseExited(e -> cell.setStyle("-fx-background-color: #232526; -fx-border-color: #888888; -fx-border-width: 2; -fx-background-radius: 50px; -fx-border-radius: 50px;"));
                cell.setOnMouseClicked(e -> {
                    if (!gameOver && !model.getCurrentPlayer().getName().toLowerCase().contains("computer")) {
                        handlePlayerMove(col);
                    }
                });

                gameBoard.add(cell, j, i);
            }
        }
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
            System.out.println("IA created: PuissanceXSmartDecider (Level: Easy)");
        } else if (computerLevel == 2) {
            // Niveau difficile = HardDecider
            decider = new PuissanceXHardDecider(model, controller);
            ((PuissanceXHardDecider) decider).setStage(stageModel);
            System.out.println("IA created: PuissanceXHardDecider (Level: Hard)");
        } else {
            // Niveau moyen = Decider
            decider = new PuissanceXDecider(model, controller);
            ((PuissanceXDecider) decider).setStage(stageModel);
            System.out.println("IA created: PuissanceXDecider (Level: Medium)");
        }

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

        // Toujours reset l'instance du tracker pour garantir la bonne taille
        int rows = board.getNbRows();
        int cols = board.getNbCols();
        int align = board.getNbAlign();
        ConsoleGameTracker.resetInstance();
        ConsoleGameTracker tracker = ConsoleGameTracker.getInstance(rows, cols, align);

        // Sécurisation : ne pas appeler updateGrid si la taille ne correspond pas
        int[][] grid = board.getGrid();
        if (grid.length == rows && grid[0].length == cols) {
            tracker.updateGrid(grid);
        } else {
            System.err.println("Erreur : la taille du plateau ne correspond pas à celle du tracker !");
            return;
        }

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
            scoreLabel.setText("Score: " + player1Name + " : " + scoreJoueur1 + " - " + player2Name + " : " + scoreJoueur2);
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
        
        // Toujours reset l'instance du tracker pour garantir la bonne taille
        int rows = (board != null) ? board.getNbRows() : nbRows;
        int cols = (board != null) ? board.getNbCols() : nbCols;
        int align = (board != null) ? board.getNbAlign() : nbAlign;
        ConsoleGameTracker.resetInstance();
        ConsoleGameTracker tracker = ConsoleGameTracker.getInstance(rows, cols, align);
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
            scoreLabel.setText("Score: " + player1Name + " : " + scoreJoueur1 + " - " + player2Name + " : " + scoreJoueur2);
        }
        
        // Reset interface if it exists
        if (gameBoard != null) {
            BorderPane root = (BorderPane) gameBoard.getParent().getParent();

            // Recreate left container (red pot)
            VBox leftBox = new VBox(20);
            leftBox.setAlignment(Pos.CENTER);
            leftBox.setPadding(new Insets(0, 20, 0, 0));
            leftBox.setStyle("-fx-background-color: transparent;");
            leftBox.setMinWidth(150);

            Label redLabel = new Label(player1Name);
            redLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: red;");

            redPotLabel = new Label("Pieces remaining: " + (redPot != null ? redPot.getRemainingPawns() : 0));
            redPotLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: red;");

            VBox redPotInfo = new VBox(10);
            redPotInfo.setAlignment(Pos.CENTER);
            redPotInfo.setMinHeight(100);
            // Garder le fond noir pour les rectangles d'informations
            redPotInfo.setStyle("-fx-background-color: #2d2d2d; -fx-border-color: #ff4b2b; -fx-border-width: 2; -fx-padding: 20; -fx-background-radius: 15px; -fx-border-radius: 15px;");
            redPotInfo.getChildren().add(redPotLabel);

            leftBox.getChildren().addAll(redLabel, redPotInfo);
            root.setLeft(leftBox);

            // Recreate right container (yellow pot)
            VBox rightBox = new VBox(20);
            rightBox.setAlignment(Pos.CENTER);
            rightBox.setPadding(new Insets(0, 0, 0, 20));
            rightBox.setStyle("-fx-background-color: transparent;");
            rightBox.setMinWidth(150);

            Label yellowLabel = new Label(player2Name);
            yellowLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: yellow;");

            yellowPotLabel = new Label("Pieces remaining: " + (yellowPot != null ? yellowPot.getRemainingPawns() : 0));
            yellowPotLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: yellow;");

            VBox yellowPotInfo = new VBox(10);
            yellowPotInfo.setAlignment(Pos.CENTER);
            yellowPotInfo.setMinHeight(100);
            // Garder le fond noir pour les rectangles d'informations
            yellowPotInfo.setStyle("-fx-background-color: #2d2d2d; -fx-border-color: #ffe259; -fx-border-width: 2; -fx-padding: 20; -fx-background-radius: 15px; -fx-border-radius: 15px;");
            yellowPotInfo.getChildren().add(yellowPotLabel);

            rightBox.getChildren().addAll(yellowLabel, yellowPotInfo);
            root.setRight(rightBox);

            // Stocker les VBox pour le thème
            this.redPotInfo = redPotInfo;
            this.yellowPotInfo = yellowPotInfo;

            // Appliquer le thème pour mettre à jour le style des rectangles
            applyTheme();

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

