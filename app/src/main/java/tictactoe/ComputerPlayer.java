package tictactoe;

/**
 * Computer Player of TicTacToe - Minimax with Alpha–beta pruning is used here.
 * Alpha: best score for AI player so far
 * Beta: best score for opponent so far
 */
class ComputerPlayer extends Player {
    protected static final int DEFAULT_MAX_DEPTH = 8;
    protected Mark opponentMark;
    protected int maxDepth = DEFAULT_MAX_DEPTH;

    /** Constructor without explicit depth selection (i.e. use default depth) */
    public ComputerPlayer(Mark mark) {
        super("AI", mark);
        opponentMark = mark == Mark.CIRCLE ? Mark.CROSS : Mark.CIRCLE;
    }

    /** Constructor with explicit depth selection */
    public ComputerPlayer(Mark mark, int maxDepth) {
        this(mark);
        this.maxDepth = maxDepth;
    }

    /** Constructor with explicit level selection */
    public ComputerPlayer(Mark mark, GameLevel level) {
        this(mark, level == null ? GameLevel.HARD.depth : level.getDepth());
    }

    /** Allow user to change level */
    public void setLevel(GameLevel level) {
        this.maxDepth = level.depth;
    }

    /** Returns the "best" move given a board */
    public Move getMove(Board board) {
        // for all possible moves
        int score;
        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;
        for (Move move : board.getPossibleMoves()) {
            // evaluate the move's score
            board.move(move, mark);
            score = minimax(board, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            board.undoMove(move);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    /** Minimax with Alpha–beta pruning algorithm (Recursion) */
    public int minimax(Board board, int depth, boolean isMyMove, int alpha, int beta) {
        int evalResult = evaluate(board);
        // base cases: reached end (i.e. has winner or draw) or reached max depth
        if (evalResult != 0 || depth == maxDepth || board.isDraw())
            return evalResult;
        // maximize the score of AI player
        int score;
        if (isMyMove) {
            int bestScore = Integer.MIN_VALUE;
            for (Move move : board.getPossibleMoves()) {
                board.move(move, mark);
                score = minimax(board, depth + 1, false, alpha, beta);
                board.undoMove(move);
                bestScore = Math.max(score, bestScore);
                alpha = Math.max(alpha, bestScore);
                // if a>b: remaining moves can't have better result
                if (alpha >= beta)
                    return bestScore;
            }
            return bestScore;
            // minimize the score of human player
        } else {
            int worstScore = Integer.MAX_VALUE;
            for (Move move : board.getPossibleMoves()) {
                board.move(move, opponentMark);
                score = minimax(board, depth + 1, true, alpha, beta);
                board.undoMove(move);
                worstScore = Math.min(score, worstScore);
                beta = Math.min(beta, worstScore);
                if (beta <= alpha)
                    return worstScore;
            }
            return worstScore;
        }
    }

    /**
     * Get a score of the board. (1 meaning AI won, -1 means opponent won, 0 means
     * draw / not ended)
     */
    public int evaluate(Board board) {
        Mark winner = board.hasWinner();
        // if draw or no winner, return 0
        if (winner == null)
            return 0;
        // if AI won, return 1
        if (winner == this.mark)
            return 1;
        // if player won, return -1
        return -1;
    }

}
