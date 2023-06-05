package tictactoe;

/**
 * Base class of a Player
 */
class Player {
    protected String name;
    protected Mark mark;

    public Player(String name, Mark mark) {
        this.name = name;
        this.mark = mark;
    }

    /** Return the name of the player */
    public String getName() {
        return name;
    }

    /** Return the mark of the player */
    public Mark getMark() {
        return mark;
    }
}