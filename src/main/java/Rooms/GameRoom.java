package Rooms;

import com.kierki.client.Player;

import java.util.HashSet;
import java.util.Set;

public class GameRoom {
    private final String roomName;
    private final Set<Player> players;
    private String gameState;
    private static final int MAX_PLAYERS = 4;
    private final boolean gameStarted;
    private int amountOfPlayers;

    public GameRoom(String roomName) {
        this.roomName = roomName;
        this.players = new HashSet<>();
        this.gameStarted = false;
        this.amountOfPlayers = 0;
    }

    public int getAmountOfPlayers() {
        return amountOfPlayers;
    }

    public void addPlayer(Player player) {
        players.add(player);
        this.amountOfPlayers += 1;
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
        return amountOfPlayers >= MAX_PLAYERS;
    }

    // Method to check if the game has already started
    public boolean hasGameStarted() {
        return gameStarted;
    }

    public boolean canJoin() {
        return (!isFull()) && (!gameStarted);
    }

    public String getName() {
        return roomName;
    }

    public void setPlayerCount(int playerCount) {
        this.amountOfPlayers = playerCount;
    }

    // Metody do zarządzania stanem gry
}
