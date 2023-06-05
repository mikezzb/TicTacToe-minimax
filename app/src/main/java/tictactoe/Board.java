package tictactoe;

import java.util.ArrayList;
import java.util.List;

/**
 * Board of the game
 */
public class Board {
    private Mark board[][];

    /* Constructor: create and initialize the 2D array */
    public Board() {
        // initialize board sizes
        board = new Mark[3][3];
        // initialize grids
        initGrids();
    }

    /* Display the board on the screen */
    public void display() {
        System.out.println("---------");
        // print all entries in board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                // determine the printed symbol of this entry (0: empty, 1: circle, 2: cross)
                char symbol;
                switch (board[i][j]) {
                    case EMPTY:
                        symbol = Character.forDigit((2 - i) * 3 + j + 1, 10); // convert grid index to char
                        break;
                    case CIRCLE:
                        symbol = 'O';
                        break;
                    case CROSS:
                        symbol = 'X';
                        break;
                    default:
                        symbol = '!'; // error
                        break;
                }
                // print the symbol in format
                System.out.printf("|%c|", symbol);
            }
            // add new line
            System.out.print('\n');
        }
        System.out.println("---------");
    }

    /*
     * Return the winner's mark if there is a winner in the game, otherwise return
     * null
     */
    public Mark hasWinner() {
        // check for diagonal \
        Mark currentMark = board[0][0]; // set entry #7 as current mark for diag \ checking
        // if has mark on #7, then check diag
        if (currentMark != Mark.EMPTY) {
            boolean win = currentMark == board[1][1] && currentMark == board[2][2]; // check if #7 == #5 == #3
            if (win)
                return currentMark;
        }
        // check for diagonal /
        currentMark = board[0][2]; // set entry #9 as current mark for diag / checking
        // if has mark on #9, then check diag
        if (currentMark != Mark.EMPTY) {
            boolean win = currentMark == board[1][1] && currentMark == board[2][0]; // check if #9 == #5 == #1
            if (win)
                return currentMark;
        }
        // check for horizontal and vertical
        for (int i = 0; i < 3; i++) {
            // check a horizontal row
            currentMark = board[i][0];
            if (currentMark != Mark.EMPTY) {
                // check if same row has same marking
                boolean win = currentMark == board[i][1] && currentMark == board[i][2];
                if (win)
                    return currentMark;
            }
            // check a vertical column
            currentMark = board[0][i];
            if (currentMark != Mark.EMPTY) {
                // check if same column has same marking
                boolean win = currentMark == board[1][i] && currentMark == board[2][i];
                if (win)
                    return currentMark;
            }
        }
        return null;
    }

    /* Return true if the board is full, otherwise return false */
    public boolean isDraw() {
        // check for any empty space in board, if no then it's draw
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                // if has any empty space (0) then it's not draw
                if (board[i][j] == Mark.EMPTY)
                    return false;
            }
        }
        return true;
    }

    /*
     * Place the mark on the given location, return true if the location was empty
     * before placing, otherwise return false
     */
    public boolean move(int row, int column, Mark mark) {
        // if the location is not empty (0) then return false
        if (!canMove(row, column))
            return false;
        // place mark on the given location
        board[row][column] = mark;
        return true;
    }

    /** Function overload of move with a Move object to represent row and column */
    public boolean move(Move move, Mark mark) {
        return move(move.row, move.column, mark);
    }

    public void undoMove(Move move) {
        board[move.row][move.column] = Mark.EMPTY;
    }

    public boolean canMove(int row, int column) {
        return board[row][column] == Mark.EMPTY;
    }

    public GameState getState() {
        if (hasWinner() != null)
            return GameState.WIN;
        if (isDraw())
            return GameState.DRAW;
        return GameState.ONGOING;
    }

    public void initGrids() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                // initialize the grid to 0 (empty)
                board[i][j] = Mark.EMPTY;
            }
        }
    }

    public List<Move> getPossibleMoves() {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < Move.ROWS; i++) {
            for (int j = 0; j < Move.COLUMNS; j++) {
                if (canMove(i, j)) {
                    moves.add(new Move(i, j));
                }
            }
        }
        return moves;
    };
}
