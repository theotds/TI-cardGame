package Game;

public class PlayedCardInfo {
    private final Card card;
    private final String playerName;

    public PlayedCardInfo(Card card, String playerName) {
        this.card = card;
        this.playerName = playerName;
    }

    public Card getCard() {
        return card;
    }

    public String getPlayer() {
        return playerName;
    }
}
