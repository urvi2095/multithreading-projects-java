package hw.akka;

import akka.actor.AbstractActor;

import java.io.*;
import java.util.List;
import java.util.Scanner;

import static hw.akka.Messages.Msg;

/**
 * this actor implements the search for a path that satisfies the project requirements 
 *
 * @author M. Kokar
 *
 */
public class Searcher extends AbstractActor {

	private String filename;
	private int start_end_city;
	private int path_length_threshold;

	public Searcher(String filename, int start_end_city, int path_length_threshold) {
		this.filename = filename;
		this.start_end_city = start_end_city;
		this.path_length_threshold = path_length_threshold;
	}

	@Override
	public Receive createReceive() {
		//Code to implement
		return receiveBuilder()
				.matchEquals(Msg.SEARCH, m -> {
					//Start the Search
					System.out.println(self().path().name() + " is beginning the search now");
					SearchSolution solutionFound = search(filename);
					sender().tell(solutionFound, self());
				})
				.match(SearchSolution.class, searchSolution -> {
					//Search Complete notification to all other actors
					if(searchSolution.getMessage() != null) {
						//Notification about threshold exceeded
						System.out.println(searchSolution.getMessage());
						getContext().system().terminate();
					}
					else {
						//Notification about Winner and the Solution for all other actors
						System.out.println(getSelf().path().name() + " ->> " + searchSolution.getWinnerActor().path().name() + "is the Winner");
						getContext().system().terminate();
					}
				})
				.matchEquals(Msg.WINNER, m -> {
					//Search Complete notification to the winner actor
					System.out.println(getSelf().path().name() + " ->> I am the Winner");
				})
				.build();
	}

	/**
	 * Method to Search for the TourCost and TourPath starting at a given node.
	 *
	 */
	public SearchSolution search(String filename) throws IOException {

		SearchSolution searchSolution = new SearchSolution();

		//Converting the .txt file to a 2D Matrix Array
		double matrix[][] = getMatrix(filename);

		//Implementing TSP on the Matrix
		TSPDynamicProgramming tspDynamicProgramming = new TSPDynamicProgramming(start_end_city, matrix);
		double tourCost = tspDynamicProgramming.getTourCost();
		//System.out.println(self().path().name() + "-> Tour cost: "+ tourCost);

		//Comparing with Inputted Length Threshold
		if(tourCost <= path_length_threshold) {

			List<Integer> path = tspDynamicProgramming.getTour();
			System.out.println(getSelf().path().name() + " PathFound: "+ path);
			searchSolution.setTourCost(tourCost);
			searchSolution.setTourPath(path);
		}
		else {
			//In cases where, the tour cost is greater than the threshold mentioned.
			searchSolution.setTourCost(0.0);
		}

		return searchSolution;
	}

	/**
	 * Method to convert the text file into a 2D Matrix Array
	 *
	 */
	public double[][] getMatrix(String filename) throws IOException {
		Scanner s = new Scanner(new File(filename));
		int size = countLines(filename);
		double matrix[][] = new double[size][size];
		while (s.hasNext()) {
			for (int i = 0; i < matrix.length; i++) {
				for (int col = 0; col < matrix.length; col++) {
					matrix[i][col] = s.nextDouble();
				}
			}
		}
		s.close();
		return  matrix;
	}

	/**
	 * Method to get the number of rows in the text file
	 * This will determine the Number of Cities.
	 *
	 */
	public static int countLines(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}

}
