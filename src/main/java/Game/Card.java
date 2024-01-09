package Game;

public class Card {

    public enum Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES
    }

    public enum Rank {
        ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING
    }

    private final Suit suit;
    private final Rank rank;
    private String imagePath;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
        imagePath = determineImagePath(suit, rank);
    }

    private String determineImagePath(Suit suit, Rank rank) {
        // Convert the rank and suit to a path string. The rank is converted to lowercase,
        // except for numbers 10 and up which use the first two characters.
        // The file names are assumed to be lowercase.
        String rankName = rank.name().toLowerCase();
        String suitName = suit.name().toLowerCase();
        // Construct the path based on the given directory structure
        // For example: "Images/Cards/hearts/ace.png" for Ace of Hearts
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
}
