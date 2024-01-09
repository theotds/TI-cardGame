package Rooms;

import com.kierki.client.Player;

import java.util.HashSet;
import java.util.Set;

public class GameRoom {
    private final Set<Player> players;
    private String gameState;
    private static final int MAX_PLAYERS = 4;
    private final boolean gameStarted;

    public GameRoom() {
        this.players = new HashSet<>();
        this.gameStarted = false;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public boolean isFull() {
        return players.size() >= MAX_PLAYERS;
    }

    // Method to check if the game has already started
    public boolean hasGameStarted() {
        return gameStarted;
    }

    public boolean canJoin() {
        return players.size() < MAX_PLAYERS && (!gameStarted);
    }

    // Metody do zarządzania stanem gry
}
