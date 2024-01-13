package Rooms;

import Game.*;
import com.kierki.client.Consts;
import com.kierki.client.Player;

import java.util.*;

public class GameRoom {
    private static final int NUMBER_OF_CARDS_PER_PLAYER = 13;
    private static final int MAX_PLAYERS = 4;
    private final String roomName;
    private final Set<Player> players;
    private Deck deck;
    private boolean gameStarted;
    private int amountOfPlayers;
    private final List<PlayedCardInfo> playedCards;
    private int round;
    private int playerStarting;
    private int playerMove;
    private int battle;
    private Suit leadSuit; // The suit of the first card played in this round
    private Suit trumpSuit; // The trump suit, if any

    public GameRoom(String roomName) {
        this.roomName = roomName;
        this.players = new HashSet<>();
        this.gameStarted = false;
        this.amountOfPlayers = 0;
        this.deck = new Deck();
        this.playedCards = new ArrayList<>();
        this.round = 1;
        this.playerStarting = 1;
        this.battle = 1;
        this.playerMove = playerStarting;
    }

    public boolean allCardsSet() {
        return this.playedCards.size() == 4;
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

    public void dealCardsToPlayers() {
        deck.shuffle();
        for (Player player : players) {
            for (int i = 0; i < NUMBER_OF_CARDS_PER_PLAYER; i++) {
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
                playedCards.add(new PlayedCardInfo(card, player.getName()));
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
        return null;
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

    public Player playBattle() {
        PlayedCardInfo winningCardInfo = null;
        this.battle += 1;
        for (PlayedCardInfo cardInfo : playedCards) {
            if (isWinningCard(cardInfo, winningCardInfo)) {
                winningCardInfo = cardInfo;
                trumpSuit = winningCardInfo.getCard().getSuit();
                leadSuit = winningCardInfo.getCard().getSuit();
            }
        }
        if (winningCardInfo != null) {
            Player winner = findPlayerByName(winningCardInfo.getPlayer());
            if (winner != null) {
                this.playerMove = winner.getPlayerIDinRoom();
                System.out.println(winner.getName() + " " + winner.getPlayerIDinRoom());
            }
            return winner;
        }

        return null;
    }

    private boolean isWinningCard(PlayedCardInfo candidate, PlayedCardInfo currentWinner) {
        if (currentWinner == null) {
            return true;
        }

        Card candidateCard = candidate.getCard();
        Card winningCard = currentWinner.getCard();

        if (trumpSuit != null) {
            if (candidateCard.getSuit() == leadSuit) {
                return candidateCard.getRank().compareTo(winningCard.getRank()) > 0;
            }
        }

        return false;
    }

    private Player findPlayerByName(String playerName) {
        for (Player player : players) {
            if (player.getName().equals(playerName)) {
                return player;
            }
        }
        return null;
    }

    public int countPoints(int roundNumber) {
        int points = 0;
        if (roundNumber < 1 || roundNumber > 7){
            return 0;
        }
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
        multiplyer = countCardsOfRankOrSuit(Rank.KING, Suit.HEARTS);
        sub = -150;
        points = sub * multiplyer;
        return points;
    }

    private int round4() {
        int sub;
        int points;
        int multiplyer;
        multiplyer = countCardsOfRankOrSuit(Rank.KING, null);
        multiplyer += countCardsOfRankOrSuit(Rank.JACK, null);
        sub = -30;
        points = sub * multiplyer;
        return points;
    }

    private int round3() {
        int multiplyer;
        int points;
        int sub;
        multiplyer = countCardsOfRankOrSuit(Rank.QUEEN, null);
        sub = -60;
        points = sub * multiplyer;
        return points;
    }

    private int round2() {
        int points;
        int multiplyer;
        int sub;
        multiplyer = countCardsOfRankOrSuit(null, Suit.HEARTS);
        sub = -20;
        points = sub * multiplyer;
        return points;
    }

    public int countCardsOfRankOrSuit(Rank desiredRank, Suit desiredSuit) {
        int count = 0;
        for (PlayedCardInfo cardInfo : playedCards) {
            Card card = cardInfo.getCard(); // Assuming getCard() returns a Card object
            if (card.getRank() == desiredRank || card.getSuit() == desiredSuit) {
                count++;
            }
        }
        return count;
    }

    public int getPlayerStarting() {
        return playerStarting;
    }

    public void setPlayerStarting(int playerStarting) {
        this.playerStarting = playerStarting;
    }

    public int getPlayerMove() {
        return playerMove;
    }

    public void setPlayerMove(int playerMove) {
        this.playerMove = playerMove;
    }

    public Player getPlayerByIdInGame(int id) {
        for (Player player : players) {
            if (player.getPlayerIDinRoom() == id) {
                return player;
            }
        }
        return null;
    }

    public void nextPlayerMove() {
        Player currentPlayer = getPlayerByIdInGame(playerMove);
        if (currentPlayer != null) {
            currentPlayer.setTurn(false);
        }
        playerMove++;
        if (playerMove > 4) {
            playerMove = 1;
        }
        Player nextPlayer = getPlayerByIdInGame(playerMove);
        if (nextPlayer != null) {
            nextPlayer.setTurn(true);
        }
    }

    public void refillDeck() {
        this.deck = new Deck();
    }

    public Player getTheWinner() {
        Player winner = null;
        int highestScore = Integer.MIN_VALUE;

        for (Player player : players) {
            int playerScore = player.getScore();
            if (playerScore >= highestScore) {
                highestScore = playerScore;
                winner = player;
            }
        }

        return winner;
    }
}