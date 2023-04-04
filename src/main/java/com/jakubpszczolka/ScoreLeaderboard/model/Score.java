package com.jakubpszczolka.ScoreLeaderboard.model;

public class Score {
    private final String username;
    private int scoreValue;

    public Score(String username, int scoreValue) {
        this.username = username;
        this.scoreValue = scoreValue;
    }

    public void addToScore(int scoreValue) {
        this.scoreValue += scoreValue;
    }

    public String getUsername() {
        return username;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(int scoreValue) {
        this.scoreValue = scoreValue;
    }

    @Override
    public String toString() {
        return "Score{" +
                "username='" + username + '\'' +
                ", scoreValue=" + scoreValue +
                '}';
    }
}