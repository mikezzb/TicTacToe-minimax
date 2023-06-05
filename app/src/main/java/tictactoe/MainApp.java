package tictactoe;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;
import java.util.function.Consumer;

/**
 * TicTacToe Cell Mark
 */
enum Mark {
    EMPTY,
    CIRCLE,
    CROSS
}

/**
 * State of the TicTacToe Game
 */
enum GameState {
    ONGOING,
    WIN,
    DRAW
}

/**
 * Mode of the TicTacToe Game
 */
enum GameMode {
    NOT_STARTED, // Game Not Started
    PVP, // Player vs Player
    PVC_HUMAN_FIRST, // Player vs Computer (Human start first)
    PVC_COMPUTER_FIRST, // Player vs Computer (Computer start first)
}

/**
 * Represent a move on board.
 * Row and column are used to identify a position on board.
 * Row and column CANNOT be changed after initialization. (final)
 */
class Move {
    public static final int ROWS = 3;
    public static final int COLUMNS = 3;
    public final int row;
    public final int column;
    public int score; // score of the move (optional)

    public Move(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public Move(int row, int column, int score) {
        this(row, column);
        setScore(score);
    }

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Convert move's 2d position to 1d position
     */
    public int toIndex() {
        return row * ROWS + column;
    }

    @Override
    public String toString() {
        return String.format("(%d,%d) with score %d", row, column, score);
    }
}

/**
 * Represents the difficulty level of the Computer Player. Each GameLevel
 * corresponding to a minimax depth.
 */
class GameLevel {
    public static final String[] LEVELS = { "Easy", "Medium", "Hard" };

    public static final GameLevel EASY = new GameLevel(3);
    public static final GameLevel MEDIUM = new GameLevel(5);
    public static final GameLevel HARD = new GameLevel(8);

    public final int depth;

    public GameLevel(int depth) {
        this.depth = depth;
    }

    /**
     * Static factory method to return a GameLevel instance from a level string.
     * To encapsulate object creation from a string level.
     */
    public static GameLevel createGameLevel(String level) {
        switch (level) {
            case "Easy":
                return EASY;
            case "Medium":
                return MEDIUM;
            default:
                return HARD;
        }
    }

    public int getDepth() {
        return depth;
    }
}

/**
 * GUI of a TicTacToe Cell.
 * It stores it's positon as Move and it's mark
 */
class TicTacToeCell extends Button {
    private static final int SIZE = 100;
    private Mark mark;
    private Move move;

    public TicTacToeCell(int row, int column) {
        super();
        move = new Move(row, column);
        setPrefSize(SIZE, SIZE);
        setMark(Mark.EMPTY);
        setFocusTraversable(false);
        setFont(new Font(42));
    }

    /** Show mark on cell */
    public void setMark(Mark mark) {
        this.mark = mark;
        setDisable(mark != Mark.EMPTY); // disable the button if already placed mark
        switch (mark) {
            case EMPTY:
                setText("");
                break;
            case CROSS:
                setText("X");
                break;
            case CIRCLE:
                setText("O");
                break;
        }
    }

    /** Show an border indicator of the cell */
    public void showScoreIndicator(int score) {
        Color indicatorColor;
        // if user lost
        if (score < 0) {
            indicatorColor = Color.RED;
            // if user win
        } else if (score > 0) {
            indicatorColor = Color.GREEN;
            // if ongoing / draw
        } else {
            indicatorColor = Color.ORANGE;
        }
        BorderStroke borderStroke = new BorderStroke(indicatorColor, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                BorderWidths.DEFAULT);
        Border border = new Border(borderStroke);
        setBorder(border);
    }

    public void hideScoreIndicator() {
        setBorder(null);
    }

    public Move getMove() {
        return move;
    }

    /** Return true if NO mark is placed on the button */
    public boolean isEmpty() {
        return mark == Mark.EMPTY;
    }

    /**
     * Set callback to notify the GUI controller to control the Game Model.
     * Consumer: generic Java I/F that takes a single arg and returns void
     */
    public void setOnActionCallback(Consumer<TicTacToeCell> cb) {
        setOnAction(ev -> cb.accept(this));
    }
}

/**
 * The GUI of the TicTacToe Game.
 * It has mainly 2 scenes:
 * 1. StartScene that renders the Game gameMode selection
 * 2. GameScene that renders the TicTacToe game.
 * It updates the models & views, and handle events from views.
 */
public class MainApp extends Application {
    // UI items
    private Stage primaryStage;
    private Scene startScene;
    private Scene gameScene;
    private Label banner;
    private TicTacToeCell buttons[];
    private HBox gameEndButtonBox;
    // Game items
    private TicTacToeGame game;
    private GameMode gameMode;
    private GameState gameState;
    private GameLevel gameLevel;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        switchScene(GameMode.NOT_STARTED);
        primaryStage.show();
    }

    /** Create the Main Scene */
    private Scene createStartScene() {
        GridPane gridPane = createGridPane();

        /* Create all components */
        // create a label for the title
        Label titleLabel = new Label("Welcome to TicTacToe Game");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // create the difficulty selection box
        Label captionLabel = new Label("Please select AI difficulty: ");
        ChoiceBox<String> levelChoiceBox = new ChoiceBox<>();
        levelChoiceBox.getItems().addAll(GameLevel.LEVELS);
        levelChoiceBox.setValue(GameLevel.LEVELS[2]);
        levelChoiceBox.setMinHeight(30);
        HBox captionBox = new HBox(20, captionLabel, levelChoiceBox);

        // create the gameMode selection buttons
        Button pvpButton = new Button("Player vs Player");
        Button pvcButton = new Button("Player vs AI (Player first)");
        Button pvcAltButton = new Button("Player vs AI (AI first)");
        VBox buttonBox = new VBox(20, pvpButton, pvcButton, pvcAltButton);

        /* Arrange all components in the grid pane (object, col num, row num) */
        pvpButton.setMinWidth(200);
        pvcButton.setMinWidth(200);
        pvcAltButton.setMinWidth(200);

        gridPane.add(titleLabel, 0, 0, 2, 1);
        gridPane.add(captionBox, 0, 1, 2, 1);
        gridPane.add(buttonBox, 0, 2, 2, 1);
        captionBox.setAlignment(Pos.CENTER);
        buttonBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(gridPane);

        /* Bind actions */
        pvpButton.setOnAction(event -> switchScene(GameMode.PVP));
        pvcButton.setOnAction(event -> switchScene(GameMode.PVC_HUMAN_FIRST));
        pvcAltButton.setOnAction(event -> switchScene(GameMode.PVC_COMPUTER_FIRST));
        levelChoiceBox.setOnAction(event -> {
            int selectedIndex = levelChoiceBox.getSelectionModel().getSelectedIndex();
            gameLevel = GameLevel.createGameLevel(GameLevel.LEVELS[selectedIndex]);
            if (game != null) {
                game.changeComputerLevel(gameLevel);
            }
        });

        return scene;
    }

    /** Create the game board scene */
    private Scene createGameScene() {
        GridPane boardPane = createGridPane();

        /* Create all components */
        // create the grid of cells
        GridPane cellsGrid = new GridPane();
        buttons = new TicTacToeCell[9];
        cellsGrid.setAlignment(Pos.CENTER);
        GridPane.setHalignment(cellsGrid, HPos.CENTER);
        cellsGrid.setHgap(10);
        cellsGrid.setVgap(10);

        for (int i = 0; i < Move.ROWS; i++) {
            for (int j = 0; j < Move.COLUMNS; j++) {
                TicTacToeCell button = new TicTacToeCell(i, j);
                button.setOnActionCallback(this::onMove); // pass method reference as callback
                GridPane.setConstraints(button, j, i);
                cellsGrid.getChildren().add(button);
                buttons[i * 3 + j] = button;
            }
        }

        // create banner and buttons row
        banner = new Label(getBannerText());
        banner.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        Button restartButton = new Button("Restart");
        Button backButton = new Button("Back to Main");
        Button evalButton = new Button("Evaluate Moves");
        gameEndButtonBox = new HBox(20, restartButton, backButton, evalButton);

        /* Arrange all components in the grid pane (object, col num, row num) */
        restartButton.setFocusTraversable(false);
        backButton.setFocusTraversable(false);
        evalButton.setFocusTraversable(false);
        boardPane.add(banner, 0, 0, 2, 1);
        boardPane.add(cellsGrid, 0, 1);
        boardPane.add(gameEndButtonBox, 0, 2, 2, 1);
        gameEndButtonBox.setAlignment(Pos.CENTER);
        GridPane.setHalignment(banner, HPos.CENTER);
        Scene scene = new Scene(boardPane);

        /* Bind actions */
        restartButton.setOnAction(event -> restart());
        backButton.setOnAction(event -> {
            resetButtons();
            switchScene(GameMode.NOT_STARTED);
        });
        evalButton.setOnAction(event -> evaluateMoves());
        return scene;
    }

    /** Create common grid pane. Share same layout configuration for consistency */
    private GridPane createGridPane() {
        /* Creating a Grid Pane (a 2D grid for placing the controls) */
        GridPane gridPane = new GridPane();
        // Setting size for the pane
        gridPane.setMinSize(600, 400);
        // Setting the padding (the distance between the controls and the edges)
        gridPane.setPadding(new Insets(100, 100, 100, 100));
        // Setting the vertical and horizontal gaps between the columns and the rows
        gridPane.setVgap(20);
        // Setting the Grid alignment
        gridPane.setAlignment(Pos.CENTER);
        return gridPane;
    }

    /** Get the current banner text based on current game states */
    private String getBannerText() {
        return String.format("%s\'s turn", game.getCurrentPlayer().getName());
    }

    /** Handles callback when a TicTacToe button is pressed */
    private void onMove(TicTacToeCell button) {
        // skip handling if button is not pressable
        Boolean isDisable = button.isDisable();
        if (isDisable)
            return;
        // change button UI
        button.setMark(game.getCurrentPlayerMark());
        // reset evaluation on previous move
        resetEvaluation();
        // change game state
        gameState = game.move(button.getMove());
        // change banner according to game state
        switch (gameState) {
            case ONGOING:
                banner.setText(getBannerText());
                break;
            case WIN:
                banner.setText(String.format("Winner is %s!", game.getCurrentPlayer().getName()));
                break;
            case DRAW:
                banner.setText("Draw game!");
                break;
        }
        // if game is ended, handle the game end logic
        if (gameState != GameState.ONGOING) {
            onGameEnd();
            return;
        }
        // if next player is computer, execute computer's move
        boolean isComputer = game.isComputer();
        if (isComputer) {
            computerMove();
        }
    }

    /** Get and execute computer's move on the GUI and Game */
    private void computerMove() {
        disableButtons();
        Move move = game.getComputerMove();
        // find out the button that the move corresponding to
        TicTacToeCell nextButton = buttons[move.toIndex()];
        enableButtons();
        onMove(nextButton);
    }

    /** Handle game end */
    private void onGameEnd() {
        resetEvaluation();
        disableButtons();
    }

    /** Evalaute all possible moves */
    private void evaluateMoves() {
        // disable evalaution after game ended
        if (gameState != GameState.ONGOING)
            return;
        List<Move> moves = game.evaluateMoves();
        for (Move move : moves) {
            TicTacToeCell button = buttons[move.toIndex()];
            button.showScoreIndicator(move.score);
        }
    }

    private void resetEvaluation() {
        for (TicTacToeCell button : buttons) {
            button.hideScoreIndicator();
        }
    }

    private void disableButtons() {
        for (TicTacToeCell button : buttons) {
            button.setDisable(true);
        }
    }

    private void enableButtons() {
        for (TicTacToeCell button : buttons) {
            if (button.isEmpty())
                button.setDisable(false);
        }
    }

    /** Clear all marks on buttons */
    private void resetButtons() {
        for (TicTacToeCell button : buttons) {
            button.setMark(Mark.EMPTY);
        }
    }

    private void restart() {
        game.restart(gameMode, gameLevel);
        resetButtons();
        resetEvaluation();
        if (gameMode == GameMode.PVC_COMPUTER_FIRST)
            computerMove();
        banner.setText(getBannerText());
    }

    /** Switch to GUI to another scene according to game mode */
    private void switchScene(GameMode gameMode) {
        this.gameMode = gameMode;
        // if game started, init the game & game scene
        if (gameMode != GameMode.NOT_STARTED) {
            if (game == null)
                game = new TicTacToeGame(gameMode, gameLevel);
            else
                game.restart(gameMode, gameLevel);
            if (gameScene == null)
                gameScene = createGameScene();
            else {
                banner.setText(getBannerText());
                resetEvaluation();
            }
            primaryStage.setScene(gameScene);
        }
        switch (gameMode) {
            case NOT_STARTED:
                // init the start scene if not yet
                if (startScene == null)
                    startScene = createStartScene();
                else
                    banner.setText(getBannerText());
                primaryStage.setTitle("TicTacToe");
                primaryStage.setScene(startScene);
                break;
            case PVP:
                primaryStage.setTitle("TicTacToe (Player vs Player)");
                break;
            case PVC_HUMAN_FIRST:
                primaryStage.setTitle("TicTacToe (Player vs AI)");
                break;
            case PVC_COMPUTER_FIRST:
                primaryStage.setTitle("TicTacToe (Player vs AI)");
                // computer move
                computerMove();
                break;
        }
    }
}
