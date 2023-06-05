package tictactoe;

import java.util.List;

/**
 * Evaluate a game board and returns all possible moves with their scores
 */
public class GameEvaluator extends ComputerPlayer {
    public GameEvaluator() {
        super(Mark.EMPTY);
    }

    /** Returns all possible moves with their scores */
    public List<Move> evaluateMoves(Board board, Mark mark) {
        setMark(mark);
        // for all possible moves
        int score;
        List<Move> possibleMoves = board.getPossibleMoves();
        for (Move move : possibleMoves) {
            // evaluate the move's score
            board.move(move, mark);
            score = minimax(board, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            board.undoMove(move);
            // update score of the move
            move.setScore(score);
        }
        return possibleMoves;
    }

    private void setMark(Mark mark) {
        this.mark = mark;
        opponentMark = mark == Mark.CIRCLE ? Mark.CROSS : Mark.CIRCLE;
    }

}