package hwMaze;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Represents a choice point in the maze. Also used to represent a dead-end
 * (which is an empty set of choices).
 * */
public class Choice
{
    /** The position of the choice point. */
    public final Position at;
    /** The direction we came from to arrive at this choice point. */
    public final Direction from;
    /**
     * The choices available for the next move (available moves minus
     * {@link #from}).
     */
    public final Deque<Direction> choices;

    Choice(Position at, Direction from, LinkedList<Direction> choices)
    {
        this.at = at;
        this.choices = choices;
        this.from = from;
    }

    /**
     * Returns true if the choice represents a dead-end.
     * 
     * @return
     */
    boolean isDeadend()
    {
        return choices.isEmpty();
    }
}