package Game;

public class Card {

    private final Suit suit;
    private final Rank rank;
    private final String imagePath;
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
        imagePath = determineImagePath(suit, rank);
    }

    private String determineImagePath(Suit suit, Rank rank) {
        String rankName = rank.name().toLowerCase();
        String suitName = suit.name().toLowerCase();
        return "C:/TI-java/kierki/src/main/Images/Cards/" + suitName + "/" + rankName + ".png";
    }

    public String getImagePath() {
        return imagePath;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }

    public enum Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES
    }

    public enum Rank {
        ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING
    }
}
