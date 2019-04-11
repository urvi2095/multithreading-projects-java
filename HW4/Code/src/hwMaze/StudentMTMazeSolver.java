package hwMaze;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This file needs to hold your solver to be tested. 
 * You can alter the class to extend any class that extends MazeSolver.
 * It must have a constructor that takes in a Maze.
 * It must have the solve() method that returns the datatype List<Direction>
 *   which will either be a reference to a list of steps to take or will
 *   be null if the maze cannot be solved.
 */
public class StudentMTMazeSolver extends SkippingMazeSolver
{
    public StudentMTMazeSolver(Maze maze)
    {
        super(maze);
    }
    public static int count=0;

    public List<Direction> solve()
    {
        // TODO: Implement your code here
        List<Direction> solutionPath = null;
        ExecutorService executor;
        LinkedList<DepthFirstSearch> tasks = new LinkedList<DepthFirstSearch>();
        List<Future<List<Direction>>> futures = new LinkedList<Future<List<Direction>>>();

        // get the runtime object associated with the current Java application
        Runtime runtime = Runtime.getRuntime();
        // get the number of processors available to the Java virtual machine
        int numberOfProcessors = runtime.availableProcessors();

        //Initialize thread pool with number of threads = number of current processors in JVM
        executor = Executors.newFixedThreadPool(numberOfProcessors);

        try{
            Choice start = firstChoice(maze.getStart());

            int size = start.choices.size();
            for(int index = 0; index < size; index++){
                Choice curr = follow(start.at, start.choices.peek());
                tasks.add(new DepthFirstSearch(curr, start.choices.pop()));
            }

            futures = executor.invokeAll(tasks);
        } catch (SolutionFound e1) {
            System.out.println("Caught Solution Found Exception");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();

        for(Future<List<Direction>> result : futures){
            try {
                if(result.get() != null){
                    solutionPath = result.get();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("The total number of nodes traversed is: "+count);
        return solutionPath;

    }

    private class DepthFirstSearch implements Callable<List<Direction>>{
        Choice current;
        Direction followDir;
        public DepthFirstSearch(Choice current, Direction followDir){
            this.current = current;
            this.followDir = followDir;

        }

        @Override
        public List<Direction> call() {

            LinkedList<Choice> choiceStack = new LinkedList<Choice>();
            Choice ch;

            try
            {
                choiceStack.push(this.current);
                count=count+1;
                while (!choiceStack.isEmpty())
                {
                    ch = choiceStack.peek();
                    if (ch.isDeadend())
                    {
                        // backtrack.
                        choiceStack.pop();
                        if (!choiceStack.isEmpty()) choiceStack.peek().choices.pop();
//                        if (maze.display != null)
//                        {
//                            maze.setColor(ch.at, 0);
//                        }
                        continue;
                    }
                    choiceStack.push(follow(ch.at, ch.choices.peek()));
                    count++;
                }
                // No solution found.
                return null;
            }
            catch (SolutionFound e)
            {
                Iterator<Choice> iter = choiceStack.iterator();
                LinkedList<Direction> solutionPath = new LinkedList<Direction>();
                while (iter.hasNext())
                {
                    ch = iter.next();
                    solutionPath.push(ch.choices.peek());
                }
                solutionPath.push(followDir);

                if (maze.display != null) maze.display.updateDisplay();
                markPath(solutionPath, 1);
                return pathToFullPath(solutionPath);
            }

        }

    }
}
