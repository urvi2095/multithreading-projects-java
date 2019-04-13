package hw.akka;

import akka.actor.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Main class for your estimation actor system.
 *
 * @author Akash Nagesh and M. Kokar
 *
 */
public class User {

	public static void main(String[] args) throws Exception {

		/*
		 * Create the Solver Actor and send it the StartProcessing
		 * message. Once you get back the response, use it to print the result.
		 * Remember, there is only one actor directly under the ActorSystem.
		 * Also, do not forget to shutdown the actorsystem
		 */

		//Constants
		String FILE_NAME;
		String START_END_CITY;
		String PATH_LENGTH_THRESHOLD;

		//Accepting filename as command line input
		InputStreamReader r=new InputStreamReader(System.in);
		BufferedReader br=new BufferedReader(r);
		System.out.println("Enter the Filename for array matrix of distance between cities:");
		FILE_NAME=br.readLine();
		System.out.println("Enter starting and ending city index:");
		START_END_CITY=br.readLine();
		System.out.println("Enter the threshold for length of the Path:");
		PATH_LENGTH_THRESHOLD=br.readLine();
		System.out.println("You entered FILENAME: "+ FILE_NAME
				+ ", START_END_CITY: "+ START_END_CITY
				+ ", PATH_LENGTH_THRESHOLD: " + PATH_LENGTH_THRESHOLD);

		//Creating Actor System for the Project
		ActorSystem system = ActorSystem.create("EstimationSystem");

		//Creating Solver Actor
		Props solverProps = Props.create(Solver.class, FILE_NAME, START_END_CITY, PATH_LENGTH_THRESHOLD);
		ActorRef solverNode = system.actorOf(solverProps, "SolverAgent");

		//Creating Terminator for Shutting down the ActorSystem
		system.terminate();
		//system.actorOf(Props.create(Terminator.class, solverNode), "Terminator");

	}
		//Shutdown
		public class Terminator extends AbstractLoggingActor {

			private final ActorRef ref;

			public Terminator(ActorRef ref) {
				this.ref = ref;
				getContext().watch(ref);
			}

			@Override
			public Receive createReceive() {
				return receiveBuilder()
						.match(Terminated.class, t -> {
							log().info("{} has terminated, shutting down system", ref.path());
							getContext().system().terminate();
						})
						.build();
			}
		}


}
