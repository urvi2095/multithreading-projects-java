package hwMaze;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;

/**
 * Main entry point for the program. Provides command-line support for
 * generating random mazes and passing maze files to solvers.
 */
public class Main
{
    private Maze maze;
    private boolean solvable;

    /**
     * Method that calls the solvers. To add your solver to the list of solvers
     * that is run, uncomment it in the "solvers" array defined at the top of this
     * method.
     */
    public void solve()
    {
        // Add your solvers to this array to test them.
        MazeSolver[] solvers =
        {
        		new STMazeSolverRec(maze),
                new STMazeSolverDFS(maze),
                new STMazeSolverBFS(maze),
                new StudentMTMazeSolver(maze),  //uncomment this line when you are ready to test yours
        };

        for (MazeSolver solver : solvers)
        {
            long startTime, endTime;
            float sec;

            System.out.println();
            System.out.println(className(solver.getClass()) + ":");

            startTime = System.currentTimeMillis();
            List<Direction> soln = solver.solve();
            endTime = System.currentTimeMillis();
            sec = (endTime - startTime) / 1000F;

            if (soln == null)
            {
                if (!solvable) System.out.println("Correctly found no solution in " + sec + " seconds.");
                else System.out.println("Incorrectly returned no solution when there is one.");
            }
            else
            {
                if (maze.checkSolution(soln)) System.out.println("Correct solution found in " + sec + " seconds.");
                else System.out.println("Incorrect solution found.");
            }
        }
    }

    public static void main(String[] args)
    {
        Main m = new Main();

        //Uncomment these lines to run via command prompt with a certain file
        /*
        if (args.length != 1)
        {
            System.out.println("Arguments:");
            System.out.println("  filename");
            System.out.println("    To solve the maze stored in filename.");
            System.exit(-1);
        }
        */
 
        //These lines are to run via Eclipse without a command prompt
        String mazeLocationNotInProjectFolder = "mazes1/"; //replace this with your maze directory
        String whichMazeToUse = "6x6.mz"; //which maze file to load
        String[] replaceArgs = {mazeLocationNotInProjectFolder+whichMazeToUse};
        args = replaceArgs;
        
        //You probably shouldn't change the lines below
        File file = new File(args[0]);
        if (!file.exists()) {
            System.out.println("File " + file.getAbsolutePath() + " does not exist.");
            System.exit(-2);
        }
        try {
            m.read(args[0]);
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException while reading maze from: " + args[0]);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException while reading maze from: " + args[0]);
            e.printStackTrace();
        }
        
        // Uncomment to use maze display
        m.initDisplay();
        
        m.solve();
    }
    

    @SuppressWarnings("unchecked")
    private void read(String filename) throws IOException, ClassNotFoundException
    {
        MazeInputStream in =
                new MazeInputStream(new BufferedInputStream(new FileInputStream(filename)));
        maze = (Maze) in.readObject();
        solvable = in.readBoolean();
        in.close();
    }

    private String className(Class<?> cl)
    {
        StringBuffer fullname = new StringBuffer(cl.getName());
        String name = fullname.substring(fullname.lastIndexOf(".") + 1);
        return name;
    }
    
    
    private void initDisplay()
    {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int maze_width = maze.getWidth();
        int maze_height = maze.getHeight() + 2;
        int cell_width = (dim.width / maze_width);
        int cell_height = (dim.height / maze_height);
        int cell_size = Math.min(cell_width, cell_height);

        if (cell_size >= 2)
        {
            JFrame frame = new JFrame("Maze Solver");
            MazeDisplay display = new MazeDisplay(maze);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            maze.display = display;
            frame.setSize(maze_width * cell_size, maze_height * cell_size);
            frame.setVisible(true);
            Insets insets = frame.getInsets();
            frame.setSize(maze_width * cell_size + insets.left + insets.right + 3,
                    maze_height * cell_size + insets.top + insets.bottom + 2);
            System.out.println(frame.getSize());
            frame.getContentPane().add(display);
        }
        else
        {
            System.out.println("Maze too large to display on-screen.");
        }
    }
}
