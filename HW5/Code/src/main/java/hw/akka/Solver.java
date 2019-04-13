package hw.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.ArrayList;

import static hw.akka.Messages.Msg;

/**
 * This is the main actor and the only actor that is created directly under the
 * {@code ActorSystem} This actor creates 4 child actors
 * {@code Searcher}
 *
 * @author Akash Nagesh and M. Kokar
 */


public class Solver extends AbstractActor {

	private static final int SEARCHER_ACTORS = 4;
	private String filename;
	private int start_end_city;
	private int path_length_threshold;
	private ArrayList<ActorRef> searcherActors = new ArrayList<>();

	public Solver(String filename, String start_end_city, String path_length_threshold) {
		this.filename = filename;
		this.start_end_city = Integer.valueOf(start_end_city);
		this.path_length_threshold = Integer.valueOf(path_length_threshold);
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(SearchSolution.class, searchSolution -> {
					if (searchSolution.getTourCost() == 0.0 ) {

						//When Tour cost was greater than Threshold
						searchSolution.setMessage("The Tour Cost was greater than the Threshold given.");

						//Tell all agents about the threshold value exceeded
						for (ActorRef searcher : searcherActors) {
							searcher.tell(searchSolution, self());
						}
					}
					else {

						//Getting the first sender and setting it as the Winner
						searchSolution.setWinnerActor(getSender());
						String winner = getSender().path().name();

						for (ActorRef searcher : searcherActors) {
							if (searcher.path().name().equals(winner)) {

								//Notify the Winner
								searcher.tell(Msg.WINNER, self());

								//When the searcher is done, stop this actor and with it the application
								getContext().stop(self());
							}
							else {
								//Tell all other agents, except the Winner, about the solution found
								searcher.tell(searchSolution, self());
							}
						}
					}
				})
				.build();
	}

	@Override
	public void preStart() {

		//Prop for Searcher Agent Actor
		Props searcherProp = Props.create(Searcher.class, filename, start_end_city, path_length_threshold);

		// Create the Searcher Actors
		for(int i=1; i<= SEARCHER_ACTORS; i++)
			searcherActors.add(getContext().actorOf(searcherProp, "SearcherAgentActor"+i));

		// Tell all searcher agent actors to perform the search
		for(ActorRef searcher : searcherActors)
			searcher.tell(Msg.SEARCH, self());

	}

}
