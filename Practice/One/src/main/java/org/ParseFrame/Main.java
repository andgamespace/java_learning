package org.ParseFrame;


public class Main {
    public static void main(String[] args) {
        int highScore = calculateHighScorePosition(1500);
        displayHighScorePosition("Tim", highScore);
        highScore = calculateHighScorePosition(900);
        displayHighScorePosition("Bob", highScore);
        highScore = calculateHighScorePosition(400);
        displayHighScorePosition("Percy", highScore);
        highScore = calculateHighScorePosition(50);
        displayHighScorePosition("Gilbert", highScore);
        highScore = calculateHighScorePosition(1000);
        displayHighScorePosition("John", highScore);
        highScore = calculateHighScorePosition(500);
        displayHighScorePosition("Steve", highScore);
        highScore = calculateHighScorePosition(100);
        displayHighScorePosition("Mark", highScore);
        highScore = calculateHighScorePosition(25);
        displayHighScorePosition("Tom", highScore);
        highScore = calculateHighScorePosition(0);
        displayHighScorePosition("Jerry", highScore);
        highScore = calculateHighScorePosition(250);
        displayHighScorePosition("Harry", highScore);

    }
    public static void displayHighScorePosition(String playerName, int highScore) {
        System.out.println(playerName + " managed to get into position " + highScore + " on the high score table.");
    }
    public static int calculateHighScorePosition(int playerScore) {
        if (playerScore >= 1000) {
            return 1;
        } else if (playerScore >= 500) {
            return 2;
        } else if (playerScore >= 100 ) {
            return 3;
        }
            return 4;
    }

}