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
    private int round;
    private int playerStarting;
    private int battle;

    public GameRoom(String roomName) {
        this.roomName = roomName;
        this.players = new HashSet<>();
        this.gameStarted = false;
        this.amountOfPlayers = 0;
        this.deck = new Deck();
        this.playedCards = new ArrayList<>();
        this.round = 0;
        this.playerStarting = 1;
        this.battle = 1;
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

    public int getRound() {
        return round;
    }

    public void nextRound() {
        this.round += 1;
    }

    public void startRound(int roundNumber) {
        switch (roundNumber) {
            case 1:
                //  rozdanie 1. -- bez lew; -20 pkt. za każdą wziętą lewę (wygrana w pojedynku)
                break;
            case 2:
                //  rozdanie 2. -- bez kierów; -20 pkt. za każdego wziętego kiera
                break;
            case 3:
                //  rozdanie 3. -- bez dam; -60 pkt. za każdą wziętą damę
                break;
            case 4:
                //  rozdanie 4. -- bez panów; -30 pkt. za każdego wziętego króla lub waleta
                break;
            case 5:
                //  rozdanie 5. -- bez króla kier, -150 za jego wzięcie
                break;
            case 6:
                //  rozdanie 6. -- bez siódmej i ostatniej lewy, po -75 pkt. za każdą z nich
                break;
            case 7:
                //  rozdanie 7. (tzw. rozbójnik) -- wszystkie ograniczenia z rozdań 1-6.
                break;
        }
        // Additional setup for the round
    }

    public int countPoints(int roundNumber) {
        int points = 0;
        int multiplyer;
        int sub;
        switch (roundNumber) {
            case 7:
            case 1:
                //  rozdanie 1. -- bez lew; -20 pkt. za każdą wziętą lewę (wygrana w pojedynku)
                points = -20;
                if (roundNumber != 7) break;
            case 2:
                //  rozdanie 2. -- bez kierów; -20 pkt. za każdego wziętego kiera
                points = round2();
                if (roundNumber != 7) break;
            case 3:
                //  rozdanie 3. -- bez dam; -60 pkt. za każdą wziętą damę
                points = round3();
                if (roundNumber != 7) break;
            case 4:
                //  rozdanie 4. -- bez panów; -30 pkt. za każdego wziętego króla lub waleta
                points = round4();
                if (roundNumber != 7) break;
            case 5:
                //  rozdanie 5. -- bez króla kier, -150 za jego wzięcie
                points = round5();
                if (roundNumber != 7) break;
            case 6:
                //  rozdanie 6. -- bez siódmej i ostatniej lewy, po -75 pkt. za każdą z nich
                points = round6(points);
                if (roundNumber != 7) break;
        }
        return points;
    }

    private int round6(int points) {
        if (this.battle == 7 || this.battle == 13) {
            points = -75;
        }
        return points;
    }

    private int round5() {
        int multiplyer;
        int points;
        int sub;
        multiplyer = countCardsOfRankOrSuit(Card.Rank.KING, Card.Suit.HEARTS);
        sub = -150;
        points = sub * multiplyer;
        return points;
    }

    private int round4() {
        int sub;
        int points;
        int multiplyer;
        multiplyer = countCardsOfRankOrSuit(Card.Rank.KING, null);
        multiplyer += countCardsOfRankOrSuit(Card.Rank.JACK, null);
        sub = -30;
        points = sub * multiplyer;
        return points;
    }

    private int round3() {
        int multiplyer;
        int points;
        int sub;
        multiplyer = countCardsOfRankOrSuit(Card.Rank.QUEEN, null);
        sub = -60;
        points = sub * multiplyer;
        return points;
    }

    private int round2() {
        int points;
        int multiplyer;
        int sub;
        multiplyer = countCardsOfRankOrSuit(null, Card.Suit.HEARTS);
        sub = -20;
        points = sub * multiplyer;
        return points;
    }

    public int countCardsOfRankOrSuit(Card.Rank desiredRank, Card.Suit desiredSuit) {
        int count = 0;
        for (PlayedCardInfo cardInfo : playedCards) {
            Card card = cardInfo.getCard(); // Assuming getCard() returns a Card object
            if (card.getRank() == desiredRank || card.getSuit() == desiredSuit) {
                count++;
            }
        }
        return count;
    }

}