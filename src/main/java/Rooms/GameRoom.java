package Rooms;

import Game.Card;
import Game.Deck;
import Game.PlayedCardInfo;
import com.kierki.client.Player;

import java.util.*;

public class GameRoom {
    private static final int NUMBER_OF_CARDS_PER_PLAYER = 13;
    private static final int MAX_PLAYERS = 4;
    private final String roomName;
    private final Set<Player> players;
    private final Deck deck;
    private boolean gameStarted;
    private int amountOfPlayers;
    private final List<PlayedCardInfo> playedCards;

    public GameRoom(String roomName) {
        this.roomName = roomName;
        this.players = new HashSet<>();
        this.gameStarted = false;
        this.amountOfPlayers = 0;
        this.deck = new Deck();
        this.playedCards = new ArrayList<>();
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

    public void addPlayedCard(Card card, String playerName) {
        playedCards.add(new PlayedCardInfo(card, playerName));
    }

    public void setPlayedCards() {
        for (Player player : players) {
            if (player.getPlayedCard() != null) {
                Card card = player.getPlayedCard();
                playedCards.add(new PlayedCardInfo(card, player.getName())); // Add the card and player to the playedCards map
                player.getHand().remove(card);
            }
        }
    }

    public Player findPlayer(String playerName) {
        for (Player player : players) {
            if (player.getName().equals(playerName)) {
                return player;
            }
        }
        return null; // Return null if no player is found
    }

    public void resetPlayedCards() {
        playedCards.clear();
    }

    public List<PlayedCardInfo> getPlayedCards() {
        return playedCards;
    }
}