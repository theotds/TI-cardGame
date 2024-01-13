package Game;

import static com.kierki.client.Consts.CARDS_PATH;

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
        return CARDS_PATH + suitName + "/" + rankName + ".png";
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
