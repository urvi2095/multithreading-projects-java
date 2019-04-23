package hwMaze;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;

/** Stores the maze and provides functions for querying the maze. */
public class Maze implements Serializable {
	/** <p>The maze is a 2 dimensional array of atomic integers encoded as an AtomicIntegerArray.
	 * The upper-left, or north-western corner of the maze is
	 * given by maze.get(0).  The lower-right, or south-eastern corner of the maze is
	 * maze.get(maze.length()-1).  The maze is stored in row-major order, so the element at
	 * row n, column m is given by maze.get(n * width + m).</p>
	 * 
	 * <p>The entrance to the maze is at row: 0, column: width/2.
	 * The exit is at row: height-1, column: width/2.</p>
	 * 
	 * <p>Individual bits of each byte in the "maze" array are used to encode
	 * information about that cell of the maze.  Only the low-order 8 bits
	 * are used.  The designated bits are:</p>
	 * <pre>  CCSSEEse</pre>
	 * <p>and have the following meaning</p>
	 * <dl>  <dt><pre>CC</pre> <dd>marks two bits that are used to give a color to this cell in the maze.  Each of the four
	 *      bit combinations corresponds to a different color that will be used by the MazeDisplay object
	 *      when drawing this cell.  This can be used by your code to provide visual feedback on solving
	 *      progress or for debugging.
	 *   <dt><pre>SS</pre> <dd>marks two bits that are used to give a color to the southern side of this cell.
	 *   <dt><pre>EE</pre> <dd>marks two bits that are used to give a color to the eastern side of this cell.
	 *   <dt><pre>s</pre>  <dd>Set to 1 if there is a wall to the SOUTH.
	 *   <dt><pre>e</pre>  <dd>Set to 1 if there is a wall to the EAST.
	 * 
	 * <p>There is always a wall to the south of cells on the southern border of the maze.
	 * There is always a wall to the east of cells on the eastern border of the maze.
	 * There is an implicit wall to the north of cells on the northern border
	 *   and to the west of cells on the western border.</p> */
	public AtomicIntegerArray maze;
	public int width, height;
	
	/**
	 * A reference to the graphical display of this maze.  This will be null if graphical
	 * maze display is not currently enabled.
	 */
	public transient MazeDisplay display;

	private static final int EAST_COLOR_SHIFT = 2;
	private static final int SOUTH_COLOR_SHIFT = 4;
	private static final int CELL_COLOR_SHIFT = 6;
	private static final int CARVED_BIT = 1 << 8;
	private static final int EAST_COLOR_BITS = 3 << EAST_COLOR_SHIFT;
	private static final int SOUTH_COLOR_BITS = 3 << SOUTH_COLOR_SHIFT;
	private static final int CELL_COLOR_BITS = 3 << CELL_COLOR_SHIFT;
	private static final int SOUTH_BIT = 2;
	private static final int EAST_BIT = 1;
	
	// Position variables used internally.  Allocated once here to avoid excessive memory allocation.
	private static final long serialVersionUID = 1L;

	/** Returns the integer at the given position.  The returned value uses the encoding
	 * described in the documentation for the {@link Maze#maze maze} field. */
	public int getCell(Position pos) {
		return maze.get(pos.col + pos.row * width);
	}
	
	private void setCell(Position pos, int val) {
		maze.set(pos.col + pos.row * width, val);
	}
	
	boolean condSetCell(Position pos, int oldVal, int newVal) {
		return maze.compareAndSet(pos.col + pos.row * width, oldVal, newVal);
	}
	
	void setCarved(Position pos) {
		int newVal = getCell(pos) | CARVED_BIT;
		setCell(pos,newVal);
	}
	
	boolean uncarved(Position pos) {
		if(pos.col < 0 || pos.row < 0 || pos.col > getWidth() - 1 || pos.row > getHeight() - 1)
			return false;
		else
			return (getCell(pos) & CARVED_BIT) == 0;
	}
	
	void clearEast(Position pos) {
		int newVal = getCell(pos) & ~EAST_BIT;
		setCell(pos,newVal);
	}
	void clearSouth(Position pos) {
		int newVal = getCell(pos) & ~SOUTH_BIT;
		setCell(pos,newVal);
	}
	void setEast(Position pos) {
		int newVal = getCell(pos) | EAST_BIT;
		setCell(pos,newVal);
	}	
	void setSouth(Position pos) {
		int newVal = getCell(pos) | SOUTH_BIT;
		setCell(pos,newVal);
	}

	/** Returns true if it is possible to move in direction @dir when at position @pos. */
	public boolean canMove(Position pos, Direction dir) {
		switch(dir) {
		case NORTH:
				if(pos.row == 0) return false;
				else return (getCell(pos.move(Direction.NORTH)) & SOUTH_BIT) == 0;
		case SOUTH:
				return (getCell(pos) & SOUTH_BIT) == 0;
		case EAST:
				return (getCell(pos) & EAST_BIT) == 0;
		case WEST:
				if(pos.col == 0) return false;
				else return (getCell(pos.move(Direction.WEST)) & EAST_BIT) == 0;
		}
		
		return false; // unreachable
	}
	
	/** Returns the list of open directions at this position.  A direction is
	 * open if it is not blocked by a wall.
	 */
	public LinkedList<Direction> getMoves(Position pos) {
		LinkedList<Direction> result = new LinkedList<Direction>();
		
		if(canMove(pos,Direction.SOUTH))
			result.add(Direction.SOUTH);
		if(canMove(pos,Direction.EAST))
			result.add(Direction.EAST);
		if(canMove(pos,Direction.WEST))
			result.add(Direction.WEST);
		if(canMove(pos,Direction.NORTH))
			result.add(Direction.NORTH);
		return result;
	}
	
	/** Returns the width of the maze.
	 */
	public int getWidth() {
		return width;
	}
	
	/** Returns the height of the maze.
	 */
	public int getHeight() {
		return height;
	}
	
	/** Sets the color of the cell at position 'pos' to the specified value, which
	 *  must be between 0 and 3 inclusive. */
	public void setColor(Position pos, int color) {
		int oldVal, newVal;
		
		color %= 4;
		color <<= CELL_COLOR_SHIFT;
		do {
			oldVal = getCell(pos);
			// clear color
			newVal = oldVal & ~CELL_COLOR_BITS;
			// set color
			newVal = newVal | color;
		} while(!condSetCell(pos,oldVal,newVal));
	}
	
	/** Gets the color of the cell at position 'pos'.  The returned value will
	 *  be between 0 and 3 inclusive. */
	public int getColor(Position pos) {
		return (getCell(pos) & CELL_COLOR_BITS) >> CELL_COLOR_SHIFT;
	}

	/** Sets the color of the edge in direction 'dir' at position 'pos' to the
	 *  specified value, which must be between 0 and 3 inclusive.  The northern
	 *  wall of top-row cells and the western wall of left-column cells cannot
	 *  be colored. */
	public void setColor(Position pos, Direction dir, int color) {
		switch(dir) {
		case NORTH:
			if(pos.row == 0)
				return;
			else
				setSouthColor(pos.move(Direction.NORTH),color);
			break;
		case SOUTH:
			setSouthColor(pos,color);
			break;
		case EAST:
			setEastColor(pos,color);
			break;
		case WEST:
			if(pos.col == 0)
				return;
			else
				setEastColor(pos.move(Direction.WEST),color);
			break;
		}
	}

	/** Gets the color of the edge in direction 'dir' at position 'pos'.  The northern
	 *  wall of top-row cells and the western wall of left-column cells cannot
	 *  be colored.  The color 0 will be returned for these cases. */
	public int getColor(Position pos, Direction dir) {
		switch(dir) {
		case NORTH:
			if(pos.row == 0)
				return 0;
			else
				return getSouthColor(pos.move(Direction.NORTH));
		case SOUTH:
			return getSouthColor(pos);
		case EAST:
			return getEastColor(pos);
		case WEST:
			if(pos.col == 0)
				return 0;
			else
				return getEastColor(pos.move(Direction.WEST));
		}
		
		return 0; // unreachable
	}

	/** Sets the color of the eastern edge of the cell at position 'pos' to the specified
	 *  value, which must be between 0 and 3 inclusive. */
	private void setEastColor(Position pos, int color) {
		int oldVal, newVal;
		
		color %= 4;
		color <<= EAST_COLOR_SHIFT;
		do {
			oldVal = getCell(pos);
			// clear color
			newVal = oldVal & ~EAST_COLOR_BITS;
			// set color
			newVal = newVal | color;
		} while(!condSetCell(pos,oldVal,newVal));
	}

	/** Gets the color of the eastern edge of the cell at position 'pos'.  The returned
	 *  value will be between 0 and 3 inclusive. */
	private int getEastColor(Position pos) {
		return (getCell(pos) & EAST_COLOR_BITS) >> EAST_COLOR_SHIFT;
	}

	/** Sets the color of the eastern edge of the cell at position 'pos' to the specified
	 *  value, which must be between 0 and 3 inclusive. */
	private void setSouthColor(Position pos, int color) {
		int oldVal, newVal;
		
		color %= 4;
		color <<= SOUTH_COLOR_SHIFT;
		do {
			oldVal = getCell(pos);
			// clear color
			newVal = oldVal & ~SOUTH_COLOR_BITS;
			// set color
			newVal = newVal | color;
		} while(!condSetCell(pos,oldVal,newVal));
	}

	/** Gets the color of the eastern edge of the cell at position 'pos'.  The returned
	 *  value will be between 0 and 3 inclusive. */
	private int getSouthColor(Position pos) {
		return (getCell(pos) & SOUTH_COLOR_BITS) >> SOUTH_COLOR_SHIFT;
	}

	/** Returns the position corresponding to the entrance of the maze. */
	public Position getStart() {
		return new Position(getWidth()/2, 0);
	}

	/** Returns the position corresponding to the exit of the maze. */
	public Position getEnd() {
		return new Position(getWidth()/2, getHeight()-1);
	}
	
	/** Checks that a solution is correct.
	 * @return true if the solution is correct, false otherwise. */
	public final boolean checkSolution(List<Direction> soln) {
		Position at = getStart();
		Iterator<Direction> iter = soln.iterator();
		while(iter.hasNext()) {
			Direction dir = iter.next();
			if(!canMove(at,dir))
				return false;
			at = at.move(dir);
		}
		return at.equals(getEnd());
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(width);
		out.writeInt(height);
		Position pos = new Position(0,0);
		while(pos.row < height) {
			pos = new Position(0, pos.row + 1);
			while(pos.col < width) {
				int bits = 0;
				int bit;
				for(bit = 0; bit < 16 && pos.col < width; bit++, pos.move(Direction.EAST)) {
					bits >>>= 2;
					bits |= canMove(pos,Direction.EAST) ? 0 : 0x40000000;
					bits |= canMove(pos,Direction.SOUTH) ? 0 : 0x80000000;
				}
				bits >>>= ((16 - bit) * 2);
				out.writeInt(bits);
			}
		}
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		width = in.readInt();
		height = in.readInt();
		maze = new AtomicIntegerArray(width * height);
		Position pos = new Position(0,0);
		while(pos.row < height) {
			pos = new Position(0, pos.row);
			while(pos.col < width) {
				int bits = in.readInt();
				for(int bit = 0; bit < 16 && pos.col < width; bit++, pos = pos.move(Direction.EAST)) {
					if((bits & 1) == 1)
						setEast(pos);
					if((bits & 2) == 2)
						setSouth(pos);
					bits >>= 2;
				}
			}
			pos = pos.move(Direction.SOUTH);
		}
	}
}
