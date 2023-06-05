package tictactoe;

import java.util.List;

/**
 * TicTacToe game.
 * Instead of actively ask for user input from Player class, this game wait for
 * a move initiated by the caller.
 * Suitable for GUI since the event-based callback controls the game.
 */
class TicTacToeGame {

    protected Board board; // represents the game board

    protected Player[] players; // Polymorphism of Player, each entry can be either ComputerPlayer or
                                // HumanPlayer

    protected int currentPlayerIdx;

    protected GameEvaluator evaluator; // evalautor as AI-assistance for Human

    /** Constructor: create the player objects and the board object */
    public TicTacToeGame(GameMode gameMode, GameLevel gameLevel) {
        /* Initialize players */
        initPlayers(gameMode, gameLevel);
        /* Initialize board */
        board = new Board();
        /* Initialize evaluator */
        evaluator = new GameEvaluator();
    }

    /** Init players based on game mode and game level */
    public void initPlayers(GameMode gameMode, GameLevel gameLevel) {
        players = new Player[2];
        // Player vs Player
        if (gameMode == GameMode.PVP) {
            // initialize HumanPlayer Player 1 with mark O
            players[0] = new Player("Player 1", Mark.CIRCLE);
            // initialize HumanPlayer Player 2 with mark X
            players[1] = new Player("Player 2", Mark.CROSS);
            // HumanPlayer vs ComputerPlayer (Human First)
        } else if (gameMode == GameMode.PVC_HUMAN_FIRST) {
            // initialize HumanPlayer Player 1 with mark O
            players[0] = new Player("Player", Mark.CIRCLE);
            // initialize ComputerPlayer 1 with mark X
            players[1] = new ComputerPlayer(Mark.CROSS, gameLevel);
            // HumanPlayer vs ComputerPlayer (Computer First)
        } else {
            // initialize ComputerPlayer 1 with mark X
            players[0] = new ComputerPlayer(Mark.CIRCLE, gameLevel);
            // initialize HumanPlayer Player 1 with mark O
            players[1] = new Player("Player", Mark.CROSS);
        }
        /* Init current player */
        currentPlayerIdx = 0;
    }

    public Player getCurrentPlayer() {
        return players[currentPlayerIdx];
    }

    public Mark getCurrentPlayerMark() {
        return getCurrentPlayer().getMark();
    }

    /**
     * Place current player's mark to a move position.
     * Allow Presenter to update the Model upon View events.
     * 
     * @param move
     * @return Status of the Game (i.e. Win/Draw/Ongoing)
     */
    public GameState move(Move move) {
        board.move(move, getCurrentPlayerMark());
        GameState gameState = board.getState();
        if (gameState == GameState.ONGOING)
            currentPlayerIdx = (currentPlayerIdx + 1) % 2;
        return gameState;
    }

    /** Update computer's difficulty level */
    public void changeComputerLevel(GameLevel gameLevel) {
        for (Player player : players) {
            if (player instanceof ComputerPlayer) {
                ((ComputerPlayer) player).setLevel(gameLevel);
            }
        }
    }

    /** Restart the game */
    public void restart(GameMode gameMode, GameLevel gameLevel) {
        initPlayers(gameMode, gameLevel);
        board.initGrids();
    }

    /** Return whether current player is computer */
    public Boolean isComputer() {
        return getCurrentPlayer() instanceof ComputerPlayer;
    }

    /** Get computer's move on the current board */
    public Move getComputerMove() {
        if (!isComputer())
            return null;
        return ((ComputerPlayer) getCurrentPlayer()).getMove(board);
    }

    public List<Move> evaluateMoves() {
        return evaluator.evaluateMoves(board, getCurrentPlayerMark());
    }
}
