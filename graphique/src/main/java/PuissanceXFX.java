import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.PuissanceXBoard;
import model.PuissanceXStageModel;
import model.Pawn;
import boardifier.model.Model;
import control.PuissanceXController;
import control.PuissanceXDecider;
import control.PuissanceXSmartDecider;
import boardifier.control.Decider;
import boardifier.control.ActionPlayer;
import boardifier.model.action.ActionList;

public class PuissanceXFX extends Application {
    private Model model;
    private PuissanceXStageModel stageModel;
    private PuissanceXController controller;
    private PuissanceXBoard board;
    private GridPane gameBoard;
    private Label statusLabel;
    private int nbCols;
    private int nbRows;
    private int nbAlign;
    private int computerLevel;
    private boolean gameOver = false;

    @Override
    public void start(Stage primaryStage) {
        // Créer la scène de configuration
        VBox configBox = createConfigScene();
        Scene configScene = new Scene(configBox, 400, 300);
        
        primaryStage.setTitle("Puissance X");
        primaryStage.setScene(configScene);
        primaryStage.show();
    }

    private VBox createConfigScene() {
        VBox configBox = new VBox(10);
        configBox.setPadding(new Insets(20));
        configBox.setAlignment(Pos.CENTER);

        // Mode de jeu
        Label modeLabel = new Label("Mode de jeu:");
        ComboBox<String> modeCombo = new ComboBox<>();
        modeCombo.getItems().addAll("Joueur vs Joueur", "Joueur vs Ordinateur", "Ordinateur vs Ordinateur");
        modeCombo.setValue("Joueur vs Joueur");

        // Niveau de l'ordinateur
        Label levelLabel = new Label("Niveau de l'ordinateur:");
        ComboBox<String> levelCombo = new ComboBox<>();
        levelCombo.getItems().addAll("Facile", "Moyen");
        levelCombo.setValue("Facile");

        // Dimensions du plateau
        Label colsLabel = new Label("Nombre de colonnes (5-10):");
        Spinner<Integer> colsSpinner = new Spinner<>(5, 10, 7);
        Label rowsLabel = new Label("Nombre de lignes (5-10):");
        Spinner<Integer> rowsSpinner = new Spinner<>(5, 10, 6);

        // Nombre d'alignements
        Label alignLabel = new Label("Nombre de pions à aligner:");
        Spinner<Integer> alignSpinner = new Spinner<>(3, 6, 4);

        Button startButton = new Button("Commencer");
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

        return configBox;
    }

    private void initializeGame(String gameMode) {
        model = new Model();
        
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

        stageModel = new PuissanceXStageModel("main", model);
        stageModel.setDimensions(nbRows, nbCols, nbAlign);
        stageModel.getDefaultElementFactory().setup();
        model.startGame(stageModel);
        
        controller = new PuissanceXController(model, null);
        board = stageModel.getBoard();
    }

    private void createGameScene() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        // Créer le plateau de jeu
        gameBoard = new GridPane();
        gameBoard.setHgap(5);
        gameBoard.setVgap(5);
        updateBoard();

        // Label de statut
        statusLabel = new Label("Au tour de " + model.getCurrentPlayer().getName());
        
        root.getChildren().addAll(statusLabel, gameBoard);
        
        Scene gameScene = new Scene(root);
        Stage gameStage = new Stage();
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
                Button cell = new Button();
                cell.setPrefSize(50, 50);
                cell.setStyle("-fx-background-color: white; -fx-border-color: black;");
                
                int value = board.getGrid()[i][j];
                if (value == Pawn.PAWN_RED) {
                    cell.setStyle("-fx-background-color: red; -fx-border-color: black;");
                } else if (value == Pawn.PAWN_BLACK) {
                    cell.setStyle("-fx-background-color: yellow; -fx-border-color: black;");
                }
                
                final int col = j;
                cell.setOnAction(e -> {
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
        board.getGrid()[row][col] = color;

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
        if (row != -1 && board.checkWin(row, col, color)) {
            gameOver = true;
            statusLabel.setText(model.getCurrentPlayer().getName() + " a gagné !");
        } else if (board.isBoardFull()) {
            gameOver = true;
            statusLabel.setText("Match nul !");
        } else {
            model.setNextPlayer();
            statusLabel.setText("Au tour de " + model.getCurrentPlayer().getName());
            
            if (model.getCurrentPlayer().getName().toLowerCase().contains("ordinateur")) {
                playComputerTurn();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
} 