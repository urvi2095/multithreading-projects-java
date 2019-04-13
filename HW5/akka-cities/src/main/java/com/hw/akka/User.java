package com.hw.akka;

import akka.actor.*;

/**
 * Main class for your estimation actor system.
 *
 * @author Akash Nagesh and M. Kokar
 *
 */
public class User {

	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("EstimationSystem");

		/*
		 * Create the Solver Actor and send it the StartProcessing
		 * message. Once you get back the response, use it to print the result.
		 * Remember, there is only one actor directly under the ActorSystem.
		 * Also, do not forget to shutdown the actorsystem
		 */

		//Creating Solver Actor
		ActorRef a = system.actorOf(Props.create(Solver.class), "StartProcessing");

		//Creating Terminator for Shuting down the ActorSystem
		system.actorOf(Props.create(Terminator.class, a), "Terminator");

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
