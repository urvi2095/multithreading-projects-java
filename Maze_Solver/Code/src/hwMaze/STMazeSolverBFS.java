package hwMaze;

import java.util.LinkedList;
import java.util.List;

/**
 * A single-threaded breadth-first solver.
 */
public class STMazeSolverBFS extends SkippingMazeSolver
{
    public class SolutionNode
    {
        public SolutionNode parent;
        public Choice choice;

        public SolutionNode(SolutionNode parent, Choice choice)
        {
            this.parent = parent;
            this.choice = choice;
        }
    }

    public STMazeSolverBFS(Maze maze)
    {
        super(maze);
    }

    Direction exploring = null;

    /**
     * Expands a node in the search tree, returning the list of child nodes.
     * 
     * @throws SolutionFound
     */
    public List<SolutionNode> expand(SolutionNode node) throws SolutionFound
    {
        LinkedList<SolutionNode> result = new LinkedList<SolutionNode>();
        if (maze.display != null) maze.setColor(node.choice.at, 0);
        for (Direction dir : node.choice.choices)
        {
            exploring = dir;
            Choice newChoice = follow(node.choice.at, dir);
            if (maze.display != null) maze.setColor(newChoice.at, 2);
            result.add(new SolutionNode(node, newChoice));
        }
        return result;
    }

    /**
     * Performs a breadth-first search of the maze. The algorithm builds a tree
     * rooted at the start position. Parent pointers are used to point the way
     * back to the entrance. The algorithm stores the list of leaves in the
     * variables "frontier". During each iteration, these leaves are each
     * expanded and the children the result become the new frontier. If a node
     * represents a dead-end, it is discarded. Execution stops when the exit is
     * discovered, as indicated by the SolutionFound exception.
     */
    public List<Direction> solve()
    {
        SolutionNode curr = null;
        LinkedList<SolutionNode> frontier = new LinkedList<SolutionNode>();

        try
        {
            frontier.push(new SolutionNode(null, firstChoice(maze.getStart())));
            while (!frontier.isEmpty())
            {
                LinkedList<SolutionNode> new_frontier = new LinkedList<SolutionNode>();
                for (SolutionNode node : frontier)
                {
                    if (!node.choice.isDeadend())
                    {
                        curr = node;
                        new_frontier.addAll(expand(node));
                    }
                    else if (maze.display != null)
                    {
                        maze.setColor(node.choice.at, 0);
                    }
                }
                frontier = new_frontier;
                if (maze.display != null)
                {
                    maze.display.updateDisplay();
                    try
                    {
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e)
                    {
                    }
                    // Could use: maze.display.waitForMouse();
                    // if we wanted to pause until a mouse button was pressed.
                }
            }
            // No solution found.
            return null;
        }
        catch (SolutionFound e)
        {
            if (curr == null)
            {
                // this only happens if there was a direct path from the start
                // to the end
                return pathToFullPath(maze.getMoves(maze.getStart()));
            }
            else
            {
                LinkedList<Direction> soln = new LinkedList<Direction>();
                // First save the direction we were going in when the exit was
                // discovered.
                soln.addFirst(exploring);
                while (curr != null)
                {
                    try
                    {
                        Choice walkBack = followMark(curr.choice.at, curr.choice.from, 1);
                        if (maze.display != null)
                        {
                            maze.display.updateDisplay();
                        }
                        soln.addFirst(walkBack.from);
                        curr = curr.parent;
                    }
                    catch (SolutionFound e2)
                    {
                        // If there is a choice point at the maze entrance, then
                        // record
                        // which direction we should choose.
                        if (maze.getMoves(maze.getStart()).size() > 1) soln.addFirst(e2.from);
                        if (maze.display != null)
                        {
                            markPath(soln, 1);
                            maze.display.updateDisplay();
                        }
                        return pathToFullPath(soln);
                    }
                }
                markPath(soln, 1);
                return pathToFullPath(soln);
            }
        }
    }
}
