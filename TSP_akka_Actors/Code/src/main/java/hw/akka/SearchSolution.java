package hw.akka;

import akka.actor.ActorRef;

import java.util.List;

public class SearchSolution {

    private double tourCost;
    private List<Integer> tourPath;
    private ActorRef winnerActor;
    private String message;
    private boolean isWinner;

    public double getTourCost() {
        return tourCost;
    }

    public void setTourCost(double tourCost) {
        this.tourCost = tourCost;
    }

    public List<Integer> getTourPath() {
        return tourPath;
    }

    public void setTourPath(List<Integer> tourPath) {
        this.tourPath = tourPath;
    }

    public ActorRef getWinnerActor() {
        return winnerActor;
    }

    public void setWinnerActor(ActorRef winnerActor) {
        this.winnerActor = winnerActor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getWinner() {
        return isWinner;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }
}
