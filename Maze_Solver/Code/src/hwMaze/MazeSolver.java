package hwMaze;

import java.util.List;

/**
 * Superclass of all solvers. Do not change!
 */
public abstract class MazeSolver
{
    protected Maze maze;

    public MazeSolver(Maze maze)
    {
        this.maze = maze;
    }

    /**
     * Solve the maze and return the solution. A solution is a list of
     * directions that lead from the maze start to the end. If no solution
     * exists, null should be returned.
     * 
     * @return The list of directions that would lead a person from the maze
     *         start to the end.
     */
    public abstract List<Direction> solve();
}
