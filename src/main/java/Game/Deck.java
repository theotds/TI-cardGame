package Game;

import java.util.Collections;
import java.util.Stack;

public class Deck {
    private final Stack<Card> cards;

    public Deck() {
        this.cards = new Stack<>();
        RestartDeck();
    }

    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    public Card dealCard() {
        return cards.isEmpty() ? null : this.cards.pop();
    }

    public void RestartDeck() {
        this.cards.clear();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                this.cards.push(new Card(suit, rank));
            }
        }
    }

    // Add any other deck-related methods here (e.g., size of deck, etc.)
}