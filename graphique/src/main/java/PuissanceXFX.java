import boardifier.control.ActionPlayer;
import boardifier.control.Controller;
import boardifier.control.Decider;
import boardifier.model.Model;
import boardifier.model.StageElementsFactory;
import boardifier.view.RootPane;
import boardifier.view.View;
import control.PuissanceXController;
import control.PuissanceXDecider;
import control.PuissanceXSmartDecider;
import javafx.application.Application;
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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.PuissanceXBoard;
import model.PuissanceXStageModel;
import model.Pawn;
import model.PuissanceXPawnPot;
import boardifier.model.action.ActionList;
import view.RedPawnPotLook;
import view.YellowPawnPotLook;

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

    @Override
    public void start(Stage primaryStage) {
        createConfigScene(primaryStage);
    }

    private void createConfigScene(Stage stage) {
        VBox configBox = new VBox(20);
        configBox.setPadding(new Insets(40));
        configBox.setAlignment(Pos.CENTER);
        configBox.setStyle("-fx-background-color: #333333;");

        // Style pour les labels
        String labelStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;";
        
        // Mode de jeu
        Label modeLabel = new Label("Mode de jeu:");
        modeLabel.setStyle(labelStyle);
        ComboBox<String> modeCombo = new ComboBox<>();
        modeCombo.setStyle("-fx-font-size: 14px;");
        modeCombo.setPrefWidth(300);
        modeCombo.getItems().addAll("Joueur vs Joueur", "Joueur vs Ordinateur", "Ordinateur vs Ordinateur");
        modeCombo.setValue("Joueur vs Joueur");

        // Niveau de l'ordinateur
        Label levelLabel = new Label("Niveau de l'ordinateur:");
        levelLabel.setStyle(labelStyle);
        ComboBox<String> levelCombo = new ComboBox<>();
        levelCombo.setStyle("-fx-font-size: 14px;");
        levelCombo.setPrefWidth(300);
        levelCombo.getItems().addAll("Facile", "Moyen");
        levelCombo.setValue("Facile");

        // Dimensions du plateau
        Label colsLabel = new Label("Nombre de colonnes (5-10):");
        colsLabel.setStyle(labelStyle);
        Spinner<Integer> colsSpinner = new Spinner<>(5, 10, 7);
        colsSpinner.setStyle("-fx-font-size: 14px;");
        colsSpinner.setPrefWidth(300);
        
        Label rowsLabel = new Label("Nombre de lignes (5-10):");
        rowsLabel.setStyle(labelStyle);
        Spinner<Integer> rowsSpinner = new Spinner<>(5, 10, 6);
        rowsSpinner.setStyle("-fx-font-size: 14px;");
        rowsSpinner.setPrefWidth(300);

        // Nombre d'alignements
        Label alignLabel = new Label("Nombre de pions à aligner:");
        alignLabel.setStyle(labelStyle);
        Spinner<Integer> alignSpinner = new Spinner<>(3, 6, 4);
        alignSpinner.setStyle("-fx-font-size: 14px;");
        alignSpinner.setPrefWidth(300);

        Button startButton = new Button("Commencer");
        startButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20 10 20; -fx-background-color: #444444; -fx-text-fill: white; -fx-border-color: #666666;");
        startButton.setOnAction(e -> {
            nbCols = colsSpinner.getValue();
            nbRows = rowsSpinner.getValue();
            nbAlign = alignSpinner.getValue();
            computerLevel = levelCombo.getValue().equals("Facile") ? 0 : 1;
            
            initializeGame(modeCombo.getValue());
            createGameScene();
        });

        configBox.getChildren().addAll(
            modeLabel, modeCombo,
            levelLabel, levelCombo,
            colsLabel, colsSpinner,
            rowsLabel, rowsSpinner,
            alignLabel, alignSpinner,
            startButton
        );

        Scene configScene = new Scene(configBox, 600, 400);
        configScene.setFill(Color.web("#333333"));
        stage.setTitle("Puissance X");
        stage.setScene(configScene);
        stage.show();
    }

    private void initializeGame(String gameMode) {
        model = new Model();
        gameStage = new Stage();
        RootPane rootPane = new RootPane();
        view = new View(model, gameStage, rootPane);
        rootPane.init(view.getGameStageView());
        
        // Ajouter les joueurs selon le mode choisi
        switch (gameMode) {
            case "Joueur vs Joueur":
                model.addHumanPlayer("Joueur 1");
                model.addHumanPlayer("Joueur 2");
                break;
            case "Joueur vs Ordinateur":
                model.addHumanPlayer("Joueur 1");
                model.addComputerPlayer("Ordinateur");
                break;
            case "Ordinateur vs Ordinateur":
                model.addComputerPlayer("Ordinateur 1");
                model.addComputerPlayer("Ordinateur 2");
                break;
        }

        try {
            stageModel = new PuissanceXStageModel("stage1", model);
            stageModel.setDimensions(nbRows, nbCols, nbAlign);
            stageModel.createElements(stageModel.getDefaultElementFactory());
            model.startGame(stageModel);
            
            controller = new PuissanceXController(model, view);
            board = stageModel.getBoard();
            
            model.setIdPlayer(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createGameScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #333333;");

        // Style pour les labels
        String labelStyle = "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;";
        
        // Panneau du haut (scores et statut)
        VBox topBox = new VBox(15);
        topBox.setAlignment(Pos.CENTER);
        topBox.setStyle("-fx-background-color: #333333;");
        
        scoreLabel = new Label("Score: Joueur 1 (Rouge) : " + scoreJoueur1 + " - Joueur 2 (Jaune) : " + scoreJoueur2);
        scoreLabel.setStyle(labelStyle);
        
        statusLabel = new Label("Au tour de " + model.getCurrentPlayer().getName());
        statusLabel.setStyle(labelStyle);
        
        topBox.getChildren().addAll(scoreLabel, statusLabel);
        root.setTop(topBox);
        
        // Panneau central (plateau de jeu)
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setStyle("-fx-background-color: #333333;");
        
        // Créer le plateau de jeu
        gameBoard = new GridPane();
        gameBoard.setHgap(10);
        gameBoard.setVgap(10);
        gameBoard.setAlignment(Pos.CENTER);
        gameBoard.setStyle("-fx-background-color: #333333;");
        updateBoard();
        
        centerBox.getChildren().add(gameBoard);
        root.setCenter(centerBox);
        
        // Panneau de gauche (pot rouge)
        VBox leftBox = new VBox(20);
        leftBox.setAlignment(Pos.CENTER);
        leftBox.setPadding(new Insets(0, 20, 0, 0));
        leftBox.setStyle("-fx-background-color: #333333;");
        leftBox.setMinWidth(150);
        
        Label redLabel = new Label("Joueur 1 (Rouge)");
        redLabel.setStyle(labelStyle + "; -fx-text-fill: red;");
        
        redPot = new PuissanceXPawnPot(80, 80, stageModel, (nbRows * nbCols + 1) / 2);
        stageModel.setRedPot(redPot);
        
        Label redPotLabel = new Label("Pions restants : " + redPot.getRemainingPawns());
        redPotLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: red;");
        
        VBox redPotInfo = new VBox(10);
        redPotInfo.setAlignment(Pos.CENTER);
        redPotInfo.setStyle("-fx-background-color: #444444; -fx-border-color: #666666; -fx-border-width: 2; -fx-padding: 20;");
        redPotInfo.setMinHeight(100);
        redPotInfo.getChildren().add(redPotLabel);
        
        leftBox.getChildren().addAll(redLabel, redPotInfo);
        root.setLeft(leftBox);
        
        // Panneau de droite (pot jaune)
        VBox rightBox = new VBox(20);
        rightBox.setAlignment(Pos.CENTER);
        rightBox.setPadding(new Insets(0, 0, 0, 20));
        rightBox.setStyle("-fx-background-color: #333333;");
        rightBox.setMinWidth(150);
        
        Label yellowLabel = new Label("Joueur 2 (Jaune)");
        yellowLabel.setStyle(labelStyle + "; -fx-text-fill: yellow;");
        
        yellowPot = new PuissanceXPawnPot(80, 80, stageModel, nbRows * nbCols / 2);
        stageModel.setYellowPot(yellowPot);
        
        Label yellowPotLabel = new Label("Pions restants : " + yellowPot.getRemainingPawns());
        yellowPotLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: yellow;");
        
        VBox yellowPotInfo = new VBox(10);
        yellowPotInfo.setAlignment(Pos.CENTER);
        yellowPotInfo.setStyle("-fx-background-color: #444444; -fx-border-color: #666666; -fx-border-width: 2; -fx-padding: 20;");
        yellowPotInfo.setMinHeight(100);
        yellowPotInfo.getChildren().add(yellowPotLabel);
        
        rightBox.getChildren().addAll(yellowLabel, yellowPotInfo);
        root.setRight(rightBox);
        
        // Panneau du bas (bouton nouvelle partie quand nécessaire)
        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20, 0, 0, 0));
        bottomBox.setStyle("-fx-background-color: #333333;");
        root.setBottom(bottomBox);
        
        Scene gameScene = new Scene(root, 1000, 800);
        gameScene.setFill(Color.web("#333333"));
        gameStage.setTitle("Puissance X - Partie en cours");
        gameStage.setScene(gameScene);
        gameStage.show();

        // Si c'est un tour d'ordinateur, jouer automatiquement
        if (model.getCurrentPlayer().getName().toLowerCase().contains("ordinateur")) {
            playComputerTurn();
        }
    }

    private void updateBoard() {
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
                } else if (value == Pawn.PAWN_BLACK) {
                    circle.setFill(Color.YELLOW);
                    circle.setStroke(Color.GOLDENROD);
                }
                
                cell.getChildren().add(circle);
                
                final int col = j;
                cell.setOnMouseClicked(e -> {
                    if (!gameOver && !model.getCurrentPlayer().getName().toLowerCase().contains("ordinateur")) {
                        handlePlayerMove(col);
                    }
                });
                
                gameBoard.add(cell, j, i);
            }
        }
    }

    private void handlePlayerMove(int col) {
        if (board.isColumnFull(col)) {
            statusLabel.setText("Cette colonne est pleine ! Choisissez une autre colonne.");
            return;
        }

        int row = board.getFirstEmptyRow(col);
        int color = model.getIdPlayer() == 0 ? Pawn.PAWN_RED : Pawn.PAWN_BLACK;
        
        // Vérifier s'il reste des pions dans le pot
        PuissanceXPawnPot currentPot = color == Pawn.PAWN_RED ? redPot : yellowPot;
        if (currentPot.getRemainingPawns() <= 0) {
            statusLabel.setText("Plus de pions disponibles !");
            return;
        }
        
        board.getGrid()[row][col] = color;
        currentPot.removeElement(null); // Retirer un pion du pot
        
        updateBoard();
        checkGameStatus(row, col, color);
    }

    private void playComputerTurn() {
        Decider decider = computerLevel == 0 ? 
            new PuissanceXDecider(model, controller) : 
            new PuissanceXSmartDecider(model, controller);

        ActionList actions = decider.decide();
        if (actions != null && !actions.getActions().isEmpty()) {
            ActionPlayer play = new ActionPlayer(model, controller, actions);
            play.start();
            updateBoard();
            checkGameStatus(-1, -1, -1);
        }
    }

    private void checkGameStatus(int row, int col, int color) {
        VBox bottomBox = (VBox) ((BorderPane) gameBoard.getParent().getParent()).getBottom();
        
        if (row != -1 && board.checkWin(row, col, color)) {
            gameOver = true;
            String gagnant = model.getCurrentPlayer().getName();
            if (color == Pawn.PAWN_RED) {
                scoreJoueur1++;
                statusLabel.setTextFill(Color.RED);
            } else {
                scoreJoueur2++;
                statusLabel.setTextFill(Color.YELLOW);
            }
            scoreLabel.setText("Score: Joueur 1 (Rouge) : " + scoreJoueur1 + " - Joueur 2 (Jaune) : " + scoreJoueur2);
            statusLabel.setText(gagnant + " a gagné !");
            
            // Proposer une nouvelle partie
            Button newGameButton = new Button("Nouvelle Partie");
            newGameButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20 10 20; -fx-background-color: #444444; -fx-text-fill: white; -fx-border-color: #666666;");
            newGameButton.setOnAction(e -> {
                resetGame();
                bottomBox.getChildren().clear();
                statusLabel.setTextFill(Color.WHITE);
            });
            
            bottomBox.getChildren().add(newGameButton);
            
        } else if (board.isBoardFull()) {
            gameOver = true;
            statusLabel.setText("Match nul !");
            statusLabel.setTextFill(Color.LIGHTGRAY);
            
            // Proposer une nouvelle partie
            Button newGameButton = new Button("Nouvelle Partie");
            newGameButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20 10 20; -fx-background-color: #444444; -fx-text-fill: white; -fx-border-color: #666666;");
            newGameButton.setOnAction(e -> {
                resetGame();
                bottomBox.getChildren().clear();
                statusLabel.setTextFill(Color.WHITE);
            });
            
            bottomBox.getChildren().add(newGameButton);
            
        } else {
            model.setNextPlayer();
            statusLabel.setText("Au tour de " + model.getCurrentPlayer().getName());
            statusLabel.setTextFill(model.getIdPlayer() == 0 ? Color.RED : Color.YELLOW);
            
            if (model.getCurrentPlayer().getName().toLowerCase().contains("ordinateur")) {
                playComputerTurn();
            }
        }
    }

    private void resetGame() {
        gameOver = false;
        board.clear();
        
        // Réinitialiser les pots
        redPot = new PuissanceXPawnPot(80, 80, stageModel, (nbRows * nbCols + 1) / 2);
        yellowPot = new PuissanceXPawnPot(80, 80, stageModel, nbRows * nbCols / 2);
        stageModel.setRedPot(redPot);
        stageModel.setYellowPot(yellowPot);
        
        // Récupérer les conteneurs des pots
        BorderPane root = (BorderPane) gameBoard.getParent().getParent();
        
        // Recréer le conteneur gauche (pot rouge)
        VBox leftBox = new VBox(20);
        leftBox.setAlignment(Pos.CENTER);
        leftBox.setPadding(new Insets(0, 20, 0, 0));
        leftBox.setStyle("-fx-background-color: #333333;");
        leftBox.setMinWidth(150);
        
        Label redLabel = new Label("Joueur 1 (Rouge)");
        redLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: red;");
        
        Label redPotLabel = new Label("Pions restants : " + redPot.getRemainingPawns());
        redPotLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: red;");
        
        VBox redPotInfo = new VBox(10);
        redPotInfo.setAlignment(Pos.CENTER);
        redPotInfo.setStyle("-fx-background-color: #444444; -fx-border-color: #666666; -fx-border-width: 2; -fx-padding: 20;");
        redPotInfo.setMinHeight(100);
        redPotInfo.getChildren().add(redPotLabel);
        
        leftBox.getChildren().addAll(redLabel, redPotInfo);
        root.setLeft(leftBox);
        
        // Recréer le conteneur droit (pot jaune)
        VBox rightBox = new VBox(20);
        rightBox.setAlignment(Pos.CENTER);
        rightBox.setPadding(new Insets(0, 0, 0, 20));
        rightBox.setStyle("-fx-background-color: #333333;");
        rightBox.setMinWidth(150);
        
        Label yellowLabel = new Label("Joueur 2 (Jaune)");
        yellowLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: yellow;");
        
        Label yellowPotLabel = new Label("Pions restants : " + yellowPot.getRemainingPawns());
        yellowPotLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: yellow;");
        
        VBox yellowPotInfo = new VBox(10);
        yellowPotInfo.setAlignment(Pos.CENTER);
        yellowPotInfo.setStyle("-fx-background-color: #444444; -fx-border-color: #666666; -fx-border-width: 2; -fx-padding: 20;");
        yellowPotInfo.setMinHeight(100);
        yellowPotInfo.getChildren().add(yellowPotLabel);
        
        rightBox.getChildren().addAll(yellowLabel, yellowPotInfo);
        root.setRight(rightBox);
        
        model.setIdPlayer(0);
        updateBoard();
        statusLabel.setText("Au tour de " + model.getCurrentPlayer().getName());
        statusLabel.setTextFill(Color.RED);
    }

    public static void main(String[] args) {
        launch(args);
    }
} 