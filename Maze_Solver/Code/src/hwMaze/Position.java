package hwMaze;

/**
 * Stores a position in the maze.
 */
public class Position
{
    /** The column for this position */
    public final int col;
    /** The row for this position */
    public final int row;

    public Position(int col, int row)
    {
        this.col = col;
        this.row = row;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other instanceof Position)
        {
            Position o = (Position) other;
            return (col == o.col) && (row == o.row);
        }
        else
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return col + "," + row;
    }

    /**
     * Generates a new position that is the result of moving from this position
     * in direction "dir."
     */
    public Position move(Direction dir)
    {
        switch (dir)
        {
            case NORTH:
                return new Position(col, row - 1);
            case SOUTH:
                return new Position(col, row + 1);
            case EAST:
                return new Position(col + 1, row);
            case WEST:
                return new Position(col - 1, row);
        }

        return null; // unreachable
    }

    public int hashCode()
    {
        return (col << 16) + row;
    }
}