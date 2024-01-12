package com.kierki.client;

import Game.Card;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player {
    private String name;
    private int score;
    private List<Card> hand;
    private Card playedCard;
    private int playerIDinRoom;

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.hand = new ArrayList<>();
    }

    public List<Card> getHand() {
        return hand;
    }

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

    // Method to update the player's score
    public void updateScore(int points) {
        this.score += points;
    }

    public void setPlayedCard(Card card) {
        playedCard = card;
    }

    public Card getPlayedCard() {
        return playedCard;
    }

    public void removeCard(String rankStr, String suitStr) {
        Iterator<Card> iterator = hand.iterator();
        while (iterator.hasNext()) {
            Card card = iterator.next();
            Card.Rank rank = Card.Rank.valueOf(rankStr); // Convert string to enum
            Card.Suit suit = Card.Suit.valueOf(suitStr); // Convert string to enum

            if (card.getRank() == rank && card.getSuit() == suit) {
                iterator.remove();
                break; // Remove only the first matching card
            }
        }
    }

    public int getPlayerIDinRoom() {
        return playerIDinRoom;
    }

    public void setPlayerIDinRoom(int playerIDinRoom) {
        this.playerIDinRoom = playerIDinRoom;
    }

    // Additional methods related to player actions can be added here
}
