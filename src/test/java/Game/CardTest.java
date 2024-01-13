package Game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void testToStringAceOfClubs() {
        Card card = new Card(Suit.CLUBS, Rank.ACE);
        assertEquals("ACE of CLUBS", card.toString());
    }

    @Test
    void testToStringAceOfSpades() {
        Card card = new Card(Suit.SPADES, Rank.ACE);
        assertEquals("ACE of SPADES", card.toString());
    }

    @Test
    void testToStringAceOfHearts() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        assertEquals("ACE of HEARTS", card.toString());
    }

    @Test
    void testToStringAceOfDiamonds() {
        Card card = new Card(Suit.DIAMONDS, Rank.ACE);
        assertEquals("ACE of DIAMONDS", card.toString());
    }
}