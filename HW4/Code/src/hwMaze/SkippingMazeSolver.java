package hwMaze;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Superclass of solvers that only branch at choice points.
 */
public abstract class SkippingMazeSolver extends MazeSolver
{
    public class SolutionFound extends Exception
    {
        public Position pos;
        public Direction from;

        public SolutionFound(Position pos, Direction from)
        {
            this.pos = pos;
            this.from = from;
        }
    }

    public SkippingMazeSolver(Maze maze)
    {
        super(maze);
    }

    /** Returns the first choice point reached from the given position. */
    public Choice firstChoice(Position pos) throws SolutionFound
    {
        LinkedList<Direction> moves;

        moves = maze.getMoves(pos);
        if (moves.size() == 1) return follow(pos, moves.getFirst());
        else return new Choice(pos, null, moves);
    }

    /**
     * Follows a path until a choice point. Returns the choice encountered. /*
     * If a deadend is encountered, returns a Choice object whose 'at' field is
     * the location of the dead end and whose 'choices' list is empty.
     * 
     * @param at
     *            The position to start from.
     * @param dir
     *            The direction to proceed in.
     */
    public Choice follow(Position at, Direction dir) throws SolutionFound
    {
        LinkedList<Direction> choices;
        Direction go_to = dir, came_from = dir.reverse();

        at = at.move(go_to);
        do
        {
            if (at.equals(maze.getEnd())) throw new SolutionFound(at, go_to.reverse());
            if (at.equals(maze.getStart())) throw new SolutionFound(at, go_to.reverse());
            choices = maze.getMoves(at);
            choices.remove(came_from);

            if (choices.size() == 1)
            {
                go_to = choices.getFirst();
                at = at.move(go_to);
                came_from = go_to.reverse();
            }
        } while (choices.size() == 1);

        // return new Choice(at,choices);
        Choice ret = new Choice(at, came_from, choices);
        return ret;
    }

    /**
     * Follows a path until a choice point. Marks cells along the way with the
     * specified color.
     * 
     * @param at
     *            The position to start from.
     * @param dir
     *            The direction to proceed in.
     * @param color
     *            The color to mark with.
     */
    public Choice followMark(Position at, Direction dir, int color) throws SolutionFound
    {
        LinkedList<Direction> choices;
        Direction go_to = dir, came_from = dir.reverse();

        maze.setColor(at, color);
        at = at.move(go_to);
        do
        {
            maze.setColor(at, color);
            if (at.equals(maze.getEnd())) throw new SolutionFound(at, go_to.reverse());
            if (at.equals(maze.getStart())) throw new SolutionFound(at, go_to.reverse());
            choices = maze.getMoves(at);
            choices.remove(came_from);

            if (choices.size() == 1)
            {
                go_to = choices.getFirst();
                at = at.move(go_to);
                came_from = go_to.reverse();
            }
        } while (choices.size() == 1);

        return new Choice(at, came_from, choices);
    }

    /**
     * Marks a path
     * 
     * @param path
     * @param color
     * @throws SolutionFound
     */
    public void markPath(List<Direction> path, int color)
    {
        try
        {
            Choice choice = firstChoice(maze.getStart());

            Position at = choice.at;
            Iterator<Direction> iter = path.iterator();
            while (iter.hasNext())
            {
                choice = followMark(at, iter.next(), color);
                at = choice.at;
            }
        }
        catch (SolutionFound e)
        {
        }
    }

    public List<Direction> pathToFullPath(List<Direction> path)
    {
        Iterator<Direction> pathIter = path.iterator();
        LinkedList<Direction> fullPath = new LinkedList<Direction>();

        // Get full solution path.
        Position curr = maze.getStart();
        Direction go_to = null, came_from = null;
        while (!curr.equals(maze.getEnd()))
        {
            LinkedList<Direction> moves = maze.getMoves(curr);
            moves.remove(came_from);
            if (moves.size() == 1) go_to = moves.getFirst();
            else if (moves.size() > 1) go_to = pathIter.next();
            else if (moves.size() == 0)
            {
                System.out.println("Error in solution--leads to deadend.");
                throw (new Error());
                // System.exit(-1);
            }
            fullPath.add(go_to);
            curr = curr.move(go_to);
            came_from = go_to.reverse();
        }

        return fullPath;
    }
}
