package Game;

public class Card {

    private final Suit suit;
    private final Rank rank;
    private final String imagePath;
    private boolean selected;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
        imagePath = determineImagePath(suit, rank);
        selected = false;
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

    public void select() {
        this.selected = true;
    }

    public void unSelect() {
        this.selected = false;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public enum Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES
    }

    public enum Rank {
        ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING
    }
}
