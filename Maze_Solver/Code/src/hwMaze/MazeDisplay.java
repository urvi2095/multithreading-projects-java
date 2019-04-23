package hwMaze;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;

import javax.swing.JPanel;

/**
 * Provides a graphical display of the maze and functions for updating the
 * display.
 */
public class MazeDisplay extends JPanel
{
    Maze maze;
    private Object lock = new Object();
    private int numFound, numDrawn;
    Hashtable<Integer, Position> cellTypes = new Hashtable<Integer, Position>(100);

    private class MListener implements MouseListener
    {
        public void mouseClicked(MouseEvent e)
        {
            synchronized (lock)
            {
                lock.notifyAll();
            }
        }

        public void mousePressed(MouseEvent e)
        {
        }

        public void mouseReleased(MouseEvent e)
        {
        }

        public void mouseEntered(MouseEvent e)
        {
        }

        public void mouseExited(MouseEvent e)
        {
        }
    }

    public MazeDisplay(Maze maze)
    {
        super();
        setIgnoreRepaint(true);
        addMouseListener(new MListener());
        requestFocusInWindow();
        this.maze = maze;
    }

    public void waitForMouse()
    {
        try
        {
            synchronized (lock)
            {
                lock.wait();
            }
        }
        catch (InterruptedException e)
        {
            System.out.println("Interrupted while waiting for keypress.");
        }
    }

    private Color lookupColor(int color)
    {
        switch (color)
        {
            case 0:
                return Color.LIGHT_GRAY;
            case 1:
                return Color.red;
            case 2:
                return Color.green;
            case 3:
                return Color.cyan;
        }
        return Color.LIGHT_GRAY;
    }

    private void drawMazeCell(Position pos, int x, int y, int width, int height, Graphics g)
    {
        Position cell = cellTypes.get(maze.getCell(pos));
        if (cell != null)
        {
            g.copyArea(cell.col, cell.row, width, height, x - cell.col, y - cell.row);
            numFound++;
        }
        else
        {
            g.setColor(lookupColor(maze.getColor(pos)));
            g.fillRect(x, y, width - 1, height - 1);
            if (maze.canMove(pos, Direction.EAST))
            {
                g.setColor(lookupColor(maze.getColor(pos, Direction.EAST)));
                g.drawLine(x + width - 1, y, x + width - 1, y + height - 2);
            }
            if (maze.canMove(pos, Direction.SOUTH))
            {
                g.setColor(lookupColor(maze.getColor(pos, Direction.SOUTH)));
                g.drawLine(x, y + height - 1, x + width - 2, y + height - 1);
            }
            cellTypes.put(maze.getCell(pos), new Position(x, y));
        }
        numDrawn++;
    }

    private void drawMaze(Graphics g)
    {
        /*
         * Find out how large we can make the cells. Reserve one pixel for the
         * wall around the border. Reserve one row above and below for the
         * entrance and exit.
         */
        cellTypes.clear();
        numFound = 0;
        numDrawn = 0;
        int cellWidth = (getWidth() - 2) / maze.getWidth();
        int cellHeight = (getHeight() - 2) / (maze.getHeight() + 2);
        cellWidth = Math.min(cellWidth, cellHeight);
        cellHeight = cellWidth;

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        Position curr = new Position(0, 0);
        for (int draw_y = cellHeight + 1; curr.row < maze.getHeight(); curr = curr.move(Direction.SOUTH), draw_y += cellHeight)
        {
            curr = new Position(0, curr.row);
            for (int draw_x = 2; curr.col < maze.getWidth(); curr = curr.move(Direction.EAST), draw_x += cellWidth)
            {
                drawMazeCell(curr, draw_x, draw_y, cellWidth, cellHeight, g);
            }
        }

        // Draw entrance and exit
        curr = new Position(maze.getWidth() / 2, 0);
        g.setColor(lookupColor(maze.getColor(curr)));
        g.fillRect(maze.getWidth() / 2 * cellWidth + 1, 0, cellWidth - 1, cellHeight + 1);
        curr = new Position(maze.getWidth() / 2, maze.getHeight() - 1);
        g.setColor(lookupColor(maze.getColor(curr)));
        g.fillRect(maze.getWidth() / 2 * cellWidth + 1, (maze.getHeight() + 1) * cellHeight, cellWidth - 1, cellHeight + 1);

        // System.out.println("found = " + numFound + "\tdrawn = " + numDrawn);
    }

    public void paint(Graphics g)
    {
        drawMaze(g);
    }

    /**
     * Requests a display update and immediately returns. The actual update will
     * happen asynchronously in another thread.
     */
    public void updateDisplay()
    {
        repaint();
    }
}
