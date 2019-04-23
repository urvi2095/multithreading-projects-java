package hw.akka;

/**
 * Messages that are passed around the actors are usually immutable classes.
 * Think how you go about creating immutable classes:) Make them all static
 * classes inside the Messages class.
 * 
 * This class should have all the immutable messages that you need to pass
 * around actors. You are free to add more classes(Messages) that you think is
 * necessary
 * 
 * @author Akash Nagesh and M. Kokar
 *
 */
public class Messages {
	
	//Messages defined here
    public static enum Msg {
        SEARCH,
        DONE,
        STOP_SEARCH,
        WINNER;
    }

}