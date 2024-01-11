package Rooms;

import Game.Deck;
import com.kierki.client.Player;

import java.util.HashSet;
import java.util.Set;

public class GameRoom {
    private static final int NUMBER_OF_CARDS_PER_PLAYER = 13;
    private static final int MAX_PLAYERS = 4;
    private final String roomName;
    private final Set<Player> players;
    private final Deck deck;
    private boolean gameStarted;
    private int amountOfPlayers;

    public GameRoom(String roomName) {
        this.roomName = roomName;
        this.players = new HashSet<>();
        this.gameStarted = false;
        this.amountOfPlayers = 0;
        this.deck = new Deck();
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public int getAmountOfPlayers() {
        return amountOfPlayers;
    }

    public void addPlayer(Player player) {
        players.add(player);
        this.amountOfPlayers += 1;
    }

    public Deck getDeck() {
        return deck;
    }

    public void startGame() {
        deck.RestartDeck();
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public Set<Player> getPlayers() {
        return players;
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

    public void displayPlayersName() {
        for (Player player : players) {
            System.out.print(player.getName() + " ");
        }
    }

    // Method to deal cards to players when the room is full
    public void dealCardsToPlayers() {
        deck.shuffle(); // Shuffle the deck before dealing
        for (Player player : players) {
            for (int i = 0; i < NUMBER_OF_CARDS_PER_PLAYER; i++) { // Assuming a fixed number of cards per player
                player.getHand().add(deck.dealCard());
            }
        }
    }
}