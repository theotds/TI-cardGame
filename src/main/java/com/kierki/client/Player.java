package com.kierki.client;

public class Player {
    private String name;
    private int score;
    private String gameState; // This could represent different states like "Playing", "Waiting", etc.

    public Player(String name) {
        this.name = name;
        this.score = 0; // Initialize score to 0
        this.gameState = "Waiting"; // Default state
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    // Method to update the player's score
    public void updateScore(int points) {
        this.score += points;
    }

    // Additional methods related to player actions can be added here
}
