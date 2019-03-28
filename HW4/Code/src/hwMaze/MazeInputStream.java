package hwMaze;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * This class is needed to deserialize legacy files.
 */
public class MazeInputStream extends ObjectInputStream {

	public MazeInputStream(InputStream in) throws IOException {
		super(in);
	}
	
	@Override
	protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
	    ObjectStreamClass desc = super.readClassDescriptor();
	    if (desc.getName().equals("cmsc433_p4.Maze")) {
	        return ObjectStreamClass.lookup(Maze.class);
	    }
	    return desc;
	};

}
