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
        
        // Ajouter un gestionnaire pour la fermeture de la fenêtre
        primaryStage.setOnCloseRequest(event -> {
            // Réinitialiser complètement le ConsoleGameTracker
            ConsoleGameTracker.resetInstance();
            
            // Fermer la fenêtre de jeu si elle est ouverte
            if (gameStage != null) {
                gameStage.close();
            }
            
            // Réinitialiser les variables statiques et les ressources
            resetStaticResources();
        });
    }

    private void createConfigScene(Stage stage) {
        VBox configBox = new VBox(20);
        configBox.setAlignment(Pos.CENTER);
        configBox.setPadding(new Insets(20));
        configBox.setStyle("-fx-background-color: #333333;");

        String labelStyle = "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;";

        Label modeLabel = new Label("Mode de jeu");
        modeLabel.setStyle(labelStyle);
        ComboBox<String> modeCombo = new ComboBox<>();
        modeCombo.getItems().addAll("Joueur vs Joueur", "Joueur vs Ordinateur", "Ordinateur vs Ordinateur");
        modeCombo.setValue("Joueur vs Ordinateur");
        modeCombo.setStyle("-fx-font-size: 14px;");
        modeCombo.setPrefWidth(300);

        // Niveau pour le joueur 1 (ou bot 1)
        Label levelLabel1 = new Label("Niveau Joueur 1");
        levelLabel1.setStyle(labelStyle);
        ComboBox<String> levelCombo1 = new ComboBox<>();
        levelCombo1.getItems().addAll("Humain", "Bot Facile", "Bot Moyen");
        levelCombo1.setValue("Humain");
        levelCombo1.setStyle("-fx-font-size: 14px;");
        levelCombo1.setPrefWidth(300);
        levelCombo1.setDisable(true); // Désactivé par défaut

        // Niveau pour le joueur 2 (ou bot 2)
        Label levelLabel2 = new Label("Niveau Joueur 2");
        levelLabel2.setStyle(labelStyle);
        ComboBox<String> levelCombo2 = new ComboBox<>();
        levelCombo2.getItems().addAll("Humain", "Bot Facile", "Bot Moyen");
        levelCombo2.setValue("Bot Moyen");
        levelCombo2.setStyle("-fx-font-size: 14px;");
        levelCombo2.setPrefWidth(300);

        // Gérer l'activation/désactivation des combos de niveau selon le mode
        modeCombo.setOnAction(e -> {
            switch (modeCombo.getValue()) {
                case "Joueur vs Joueur":
                    levelCombo1.setValue("Humain");
                    levelCombo2.setValue("Humain");
                    levelCombo1.setDisable(true);
                    levelCombo2.setDisable(true);
                    break;
                case "Joueur vs Ordinateur":
                    levelCombo1.setValue("Humain");
                    levelCombo2.setValue("Bot Moyen");
                    levelCombo1.setDisable(true);
                    levelCombo2.setDisable(false);
                    break;
                case "Ordinateur vs Ordinateur":
                    levelCombo1.setValue("Bot Moyen");
                    levelCombo2.setValue("Bot Moyen");
                    levelCombo1.setDisable(false);
                    levelCombo2.setDisable(false);
                    break;
            }
        });

        Label colsLabel = new Label("Nombre de colonnes");
        colsLabel.setStyle(labelStyle);
        Spinner<Integer> colsSpinner = new Spinner<>(4, 12, 7);
        colsSpinner.setStyle("-fx-font-size: 14px;");
        colsSpinner.setPrefWidth(300);

        Label rowsLabel = new Label("Nombre de lignes");
        rowsLabel.setStyle(labelStyle);
        Spinner<Integer> rowsSpinner = new Spinner<>(4, 12, 6);
        rowsSpinner.setStyle("-fx-font-size: 14px;");
        rowsSpinner.setPrefWidth(300);

        Label alignLabel = new Label("Nombre de pions à aligner");
        alignLabel.setStyle(labelStyle);
        Spinner<Integer> alignSpinner = new Spinner<>(3, 8, 4);
        alignSpinner.setStyle("-fx-font-size: 14px;");
        alignSpinner.setPrefWidth(300);

        Button startButton = new Button("Commencer");
        startButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20 10 20; -fx-background-color: #444444; -fx-text-fill: white; -fx-border-color: #666666;");
        startButton.setOnAction(e -> {
            nbCols = colsSpinner.getValue();
            nbRows = rowsSpinner.getValue();
            nbAlign = alignSpinner.getValue();
            
            // Initialiser le jeu avec les niveaux choisis
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
        
        // Permettre le redimensionnement et le plein écran
        stage.setResizable(true);
        stage.setMinWidth(500);
        stage.setMinHeight(500);
        
        stage.show();
    }

    private void initializeGame(String gameMode, String level1, String level2) {
        // Réinitialiser complètement le jeu
        resetGame();
        resetStaticResources();
        
        model = new Model();
        gameStage = new Stage();
        RootPane rootPane = new RootPane();
        view = new View(model, gameStage, rootPane);
        rootPane.init(view.getGameStageView());
        
        // Ajouter les joueurs selon le mode choisi et leurs niveaux
        switch (gameMode) {
            case "Joueur vs Joueur":
                model.addHumanPlayer("Joueur 1");
                model.addHumanPlayer("Joueur 2");
                break;
            case "Joueur vs Ordinateur":
                model.addHumanPlayer("Joueur 1");
                if (level2.equals("Bot Facile")) {
                    computerLevel = 0; // SmartDecider = facile
                    model.addComputerPlayer("Ordinateur (Facile)");
                } else {
                    computerLevel = 1; // Decider = moyen
                    model.addComputerPlayer("Ordinateur (Moyen)");
                }
                break;
            case "Ordinateur vs Ordinateur":
                // Premier bot
                if (level1.equals("Bot Facile")) {
                    computerLevel = 0; // SmartDecider = facile
                    model.addComputerPlayer("Ordinateur 1 (Facile)");
                } else {
                    computerLevel = 1; // Decider = moyen
                    model.addComputerPlayer("Ordinateur 1 (Moyen)");
                }
                // Deuxième bot
                if (level2.equals("Bot Facile")) {
                    computerLevel = 0; // SmartDecider = facile
                    model.addComputerPlayer("Ordinateur 2 (Facile)");
                } else {
                    computerLevel = 1; // Decider = moyen
                    model.addComputerPlayer("Ordinateur 2 (Moyen)");
                }
                break;
        }
        
        // Créer le stage model et le controller
        stageModel = new PuissanceXStageModel("main", model);
        controller = new PuissanceXController(model, view);
        model.setGameStage(stageModel);

        // Créer le plateau
        board = new PuissanceXBoard(80, 80, stageModel, nbRows, nbCols, nbAlign);
        stageModel.setBoard(board);
    }

    private void createGameScene() {
        // Vérifier que le plateau est initialisé
        if (board == null) {
            System.out.println("Le plateau n'est pas initialisé dans createGameScene, on attend...");
            try {
                Thread.sleep(1000);
                board = stageModel.getBoard();
                if (board == null) {
                    System.out.println("Le plateau est toujours null après l'attente");
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
        
        redPotLabel = new Label("Pions restants : " + redPot.getRemainingPawns());
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
        
        yellowPotLabel = new Label("Pions restants : " + yellowPot.getRemainingPawns());
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
        
        // Ajouter un bouton pour basculer le plein écran
        Button fullscreenButton = new Button("Plein écran (F11)");
        fullscreenButton.setStyle("-fx-font-size: 14px; -fx-padding: 8 16 8 16; -fx-background-color: #555555; -fx-text-fill: white; -fx-border-color: #777777;");
        fullscreenButton.setOnAction(e -> {
            gameStage.setFullScreen(!gameStage.isFullScreen());
            fullscreenButton.setText(gameStage.isFullScreen() ? "Quitter plein écran (F11)" : "Plein écran (F11)");
        });
        
        bottomBox.getChildren().add(fullscreenButton);
        root.setBottom(bottomBox);
        
        Scene gameScene = new Scene(root, 1000, 800);
        gameScene.setFill(Color.web("#333333"));
        gameStage.setTitle("Puissance X - Partie en cours");
        gameStage.setScene(gameScene);
        
        // Permettre le redimensionnement et le plein écran
        gameStage.setResizable(true);
        gameStage.setMinWidth(800);
        gameStage.setMinHeight(600);
        
        // Ajouter un raccourci clavier pour basculer le plein écran (F11)
        gameScene.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.F11) {
                gameStage.setFullScreen(!gameStage.isFullScreen());
            }
        });
        
        gameStage.show();

        // Si c'est un tour d'ordinateur, jouer automatiquement après que la scène soit initialisée
        if (model.getCurrentPlayer().getName().toLowerCase().contains("ordinateur")) {
            Platform.runLater(() -> {
                try {
                    Thread.sleep(1000); // Attendre que tout soit bien initialisé
                    if (board != null) {
                        playComputerTurn();
                    } else {
                        System.out.println("Le plateau est toujours null, on ne peut pas jouer");
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
                    if (!gameOver && !model.getCurrentPlayer().getName().toLowerCase().contains("ordinateur")) {
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
                    if (!gameOver && !model.getCurrentPlayer().getName().toLowerCase().contains("ordinateur")) {
                        handlePlayerMove(col);
                    }
                });
                
                gameBoard.add(cell, j, i);
            }
        }
        
        // Mettre à jour les labels des pots
        redPotLabel.setText("Pions restants : " + redPot.getRemainingPawns());
        yellowPotLabel.setText("Pions restants : " + yellowPot.getRemainingPawns());
    }

    private void handlePlayerMove(int col) {
        if (board.isColumnFull(col)) {
            statusLabel.setText("Cette colonne est pleine ! Choisissez une autre colonne.");
            return;
        }

        int row = board.getFirstEmptyRow(col);
        int color = model.getIdPlayer() == 0 ? Pawn.PAWN_RED : Pawn.PAWN_YELLOW;
        
        // Vérifier s'il reste des pions dans le pot
        PuissanceXPawnPot currentPot = color == Pawn.PAWN_RED ? redPot : yellowPot;
        if (currentPot.getRemainingPawns() <= 0) {
            statusLabel.setText("Plus de pions disponibles !");
            return;
        }
        
        board.getGrid()[row][col] = color;
        currentPot.removeElement(null); // Retirer un pion du pot
        
        updateBoard(row, col, color);
        checkGameStatus(row, col, color);
    }

    private void playComputerTurn() {
        System.out.println("=== DEBUT playComputerTurn ===");
        
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
        System.out.println("IA créée: " + decider.getClass().getSimpleName() + " (Niveau: " + (computerLevel == 0 ? "Facile" : "Moyen") + ")");
        
        // Vérifier l'état du plateau après création de l'IA
        System.out.println("État du plateau après création de l'IA:");
        System.out.println("- board: " + (board != null ? "non null" : "null"));
        System.out.println("- stageModel: " + (stageModel != null ? "non null" : "null"));
        if (stageModel != null) {
            System.out.println("- stageModel.getBoard(): " + (stageModel.getBoard() != null ? "non null" : "null"));
        }
        
        ActionList actions = decider.decide();
        System.out.println("Actions retournées par l'IA: " + (actions != null ? "non null" : "null"));
        
        if (actions == null) {
            System.out.println("L'IA n'a pas pu jouer, on réessaie dans 1 seconde");
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
        
        System.out.println("Dernière position jouée: row=" + lastRow + ", col=" + lastCol + ", color=" + color);
        
        updateBoard(lastRow, lastCol, color);
        checkGameStatus(lastRow, lastCol, color);
        System.out.println("=== FIN playComputerTurn ===");
    }

    private void checkGameStatus(int row, int col, int color) {
        System.out.println("=== DEBUT checkGameStatus ===");
        System.out.println("Paramètres: row=" + row + ", col=" + col + ", color=" + color);
        
        VBox bottomBox = (VBox) ((BorderPane) gameBoard.getParent().getParent()).getBottom();
        
        // Mettre à jour le tracker console
        ConsoleGameTracker tracker = ConsoleGameTracker.getInstance(nbRows, nbCols, nbAlign);
        tracker.updateGrid(board.getGrid());
        
        // Vérifier la victoire à la fois avec le board normal et le tracker console
        boolean winBoard = row != -1 && board.checkWin(row, col, color);
        boolean winTracker = row != -1 && tracker.checkWin(row, col, color);
        
        if (winBoard || winTracker) {
            System.out.println("Victoire détectée!");
            if (winBoard) System.out.println("- Détectée par le plateau");
            if (winTracker) System.out.println("- Détectée par le tracker console");
            
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
                tracker.reset();
                bottomBox.getChildren().clear();
                statusLabel.setTextFill(Color.WHITE);
                
                // Réinitialiser le modèle pour commencer avec le premier joueur
                if (model != null) {
                    model.setIdPlayer(0);
                    String firstPlayer = model.getCurrentPlayer().getName();
                    statusLabel.setText("Au tour de " + firstPlayer);
                    statusLabel.setTextFill(Color.RED);
                    
                    // Si le premier joueur est un ordinateur, lancer son tour
                    if (firstPlayer.toLowerCase().contains("ordinateur")) {
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
            System.out.println("Match nul détecté!");
            gameOver = true;
            statusLabel.setText("Match nul !");
            statusLabel.setTextFill(Color.LIGHTGRAY);
            
            // Proposer une nouvelle partie
            Button newGameButton = new Button("Nouvelle Partie");
            newGameButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20 10 20; -fx-background-color: #444444; -fx-text-fill: white; -fx-border-color: #666666;");
            newGameButton.setOnAction(e -> {
                resetGame();
                tracker.reset();
                bottomBox.getChildren().clear();
                statusLabel.setTextFill(Color.WHITE);
                
                // Réinitialiser le modèle pour commencer avec le premier joueur
                if (model != null) {
                    model.setIdPlayer(0);
                    String firstPlayer = model.getCurrentPlayer().getName();
                    statusLabel.setText("Au tour de " + firstPlayer);
                    statusLabel.setTextFill(Color.RED);
                    
                    // Si le premier joueur est un ordinateur, lancer son tour
                    if (firstPlayer.toLowerCase().contains("ordinateur")) {
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
            System.out.println("Jeu continue, passage au joueur suivant");
            model.setNextPlayer();
            String nextPlayer = model.getCurrentPlayer().getName();
            System.out.println("Prochain joueur: " + nextPlayer);
            statusLabel.setText("Au tour de " + nextPlayer);
            statusLabel.setTextFill(model.getIdPlayer() == 0 ? Color.RED : Color.YELLOW);
            
            if (nextPlayer.toLowerCase().contains("ordinateur")) {
                System.out.println("Prochain joueur est un ordinateur, on lance playComputerTurn dans 500ms");
                // Ajouter un délai avant de jouer le prochain coup
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
        System.out.println("=== FIN checkGameStatus ===");
    }

    private void resetGame() {
        gameOver = false;
        
        // NE PAS réinitialiser les scores ici - ils doivent persister entre les parties
        // scoreJoueur1 = 0;
        // scoreJoueur2 = 0;
        
        // Réinitialiser le tracker console
        ConsoleGameTracker tracker = ConsoleGameTracker.getInstance(nbRows, nbCols, nbAlign);
        if (tracker != null) {
            tracker.reset();
        }
        
        // Réinitialiser les variables du jeu
        if (board != null) {
            board.clear();
        }
        
        // Réinitialiser les pots si ils existent
        if (stageModel != null) {
            redPot = new PuissanceXPawnPot(80, 80, stageModel, (nbRows * nbCols + 1) / 2);
            yellowPot = new PuissanceXPawnPot(80, 80, stageModel, nbRows * nbCols / 2);
            stageModel.setRedPot(redPot);
            stageModel.setYellowPot(yellowPot);
        }
        
        // Mettre à jour l'affichage du score
        if (scoreLabel != null) {
            scoreLabel.setText("Score: Joueur 1 (Rouge) : " + scoreJoueur1 + " - Joueur 2 (Jaune) : " + scoreJoueur2);
        }
        
        // Réinitialiser l'interface si elle existe
        if (gameBoard != null) {
            BorderPane root = (BorderPane) gameBoard.getParent().getParent();
            
            // Recréer le conteneur gauche (pot rouge)
            VBox leftBox = new VBox(20);
            leftBox.setAlignment(Pos.CENTER);
            leftBox.setPadding(new Insets(0, 20, 0, 0));
            leftBox.setStyle("-fx-background-color: #333333;");
            leftBox.setMinWidth(150);
            
            Label redLabel = new Label("Joueur 1 (Rouge)");
            redLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: red;");
            
            redPotLabel = new Label("Pions restants : " + (redPot != null ? redPot.getRemainingPawns() : 0));
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
            
            yellowPotLabel = new Label("Pions restants : " + (yellowPot != null ? yellowPot.getRemainingPawns() : 0));
            yellowPotLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: yellow;");
            
            VBox yellowPotInfo = new VBox(10);
            yellowPotInfo.setAlignment(Pos.CENTER);
            yellowPotInfo.setStyle("-fx-background-color: #444444; -fx-border-color: #666666; -fx-border-width: 2; -fx-padding: 20;");
            yellowPotInfo.setMinHeight(100);
            yellowPotInfo.getChildren().add(yellowPotLabel);
            
            rightBox.getChildren().addAll(yellowLabel, yellowPotInfo);
            root.setRight(rightBox);
            
            // Mettre à jour le plateau
            updateBoard();
        }
        
        // Réinitialiser le statut
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