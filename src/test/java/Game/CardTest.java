package Game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void testToString() {
        Card card = new Card(Card.Suit.CLUBS, Card.Rank.ACE);
        assertEquals("ACE of CLUBS", card.toString());
    }

    @Test
    void testToString2() {
        Card card = new Card(Card.Suit.SPADES, Card.Rank.ACE);
        assertEquals("ACE of SPADES", card.toString());
    }

    @Test
    void testToString3() {
        Card card = new Card(Card.Suit.HEARTS, Card.Rank.ACE);
        assertEquals("ACE of HEARTS", card.toString());
    }

    @Test
    void testToString4() {
        Card card = new Card(Card.Suit.DIAMONDS, Card.Rank.ACE);
        assertEquals("ACE of DIAMONDS", card.toString());
    }
}