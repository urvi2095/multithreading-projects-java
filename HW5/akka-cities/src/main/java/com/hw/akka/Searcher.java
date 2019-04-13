package com.hw.akka;

import akka.actor.AbstractActor;

/**
 * this actor implements the search for a path that satisfies the project requirements 
 *
 * @author M. Kokar
 *
 */
public class Searcher extends AbstractActor {

	public Searcher() {
		// TODO 
	}

	public static enum Msg {
		SEARCH, DONE;
	}

	@Override
	public Receive createReceive() {
		//Code to implement
		return receiveBuilder()
				.matchEquals(Msg.SEARCH, m -> {
					System.out.println("Hello World!");
					sender().tell(Msg.DONE, self());
				})
				.build();
	}

}
