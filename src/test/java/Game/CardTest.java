package Game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void testToStringAceOfClubs() {
        Card card = new Card(Card.Suit.CLUBS, Card.Rank.ACE);
        assertEquals("ACE of CLUBS", card.toString());
    }

    @Test
    void testToStringAceOfSpades() {
        Card card = new Card(Card.Suit.SPADES, Card.Rank.ACE);
        assertEquals("ACE of SPADES", card.toString());
    }

    @Test
    void testToStringAceOfHearts() {
        Card card = new Card(Card.Suit.HEARTS, Card.Rank.ACE);
        assertEquals("ACE of HEARTS", card.toString());
    }

    @Test
    void testToStringAceOfDiamonds() {
        Card card = new Card(Card.Suit.DIAMONDS, Card.Rank.ACE);
        assertEquals("ACE of DIAMONDS", card.toString());
    }
}