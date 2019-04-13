package com.hw.akka;

import java.io.File;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * This is the main actor and the only actor that is created directly under the
 * {@code ActorSystem} This actor creates 4 child actors
 * {@code Searcher}
 * 
 * @author Akash Nagesh and M. Kokar
 *
 */

import static com.hw.akka.Searcher.Msg;

public class Solver extends AbstractActor {

	public Solver() {

	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.matchEquals(Msg.DONE, m -> {
					// when the searcher is done, stop this actor and with it the application
					getContext().stop(self());
				})
				.build();
	}

	@Override
	public void preStart() {
		// Create the Searcher Actors
		final ActorRef searcher = getContext().actorOf(Props.create(Searcher.class), "Searcher");
		// Tell it to perform the search
		searcher.tell(Msg.SEARCH, self());
	}

}
