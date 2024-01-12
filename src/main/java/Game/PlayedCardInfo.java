package Game;

import Game.Card;
import com.kierki.client.Player;

public class PlayedCardInfo {
    private final Card card;
    private final Player player;

    public PlayedCardInfo(Card card, Player player) {
        this.card = card;
        this.player = player;
    }

    public Card getCard() {
        return card;
    }

    public Player getPlayer() {
        return player;
    }
}
