package Rooms;

import com.kierki.client.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameRoomTest {

    private GameRoom gameRoom;

    @BeforeEach
    public void setUp() {
        gameRoom = new GameRoom("TestRoom");
    }

    @Test
    void getAmountOfPlayers_0Players() {
        assertEquals(0, gameRoom.getAmountOfPlayers());
    }

    @Test
    void getAmountOfPlayers_1Player() {
        gameRoom.addPlayer(new Player("player1"));
        assertEquals(1, gameRoom.getAmountOfPlayers());
    }

    @Test
    void getAmountOfPlayers_2Player() {
        gameRoom.addPlayer(new Player("player1"));
        gameRoom.addPlayer(new Player("player2"));
        assertEquals(2, gameRoom.getAmountOfPlayers());
    }

    @Test
    void isFullTrue() {
        gameRoom.addPlayer(new Player("player1"));
        gameRoom.addPlayer(new Player("player2"));
        gameRoom.addPlayer(new Player("player3"));
        gameRoom.addPlayer(new Player("player4"));
        assertTrue(gameRoom.isFull());
    }

    @Test
    void isFullFalse() {
        gameRoom.addPlayer(new Player("player1"));
        gameRoom.addPlayer(new Player("player2"));
        gameRoom.addPlayer(new Player("player3"));
        assertFalse(gameRoom.isFull());
    }

    @Test
    void canJoinWhenFullRoom() {
        gameRoom.addPlayer(new Player("player1"));
        gameRoom.addPlayer(new Player("player2"));
        gameRoom.addPlayer(new Player("player3"));
        gameRoom.addPlayer(new Player("player4"));
        assertFalse(gameRoom.canJoin());
    }

    @Test
    void canJoinWhenOnePlaceLeft() {
        gameRoom.addPlayer(new Player("player1"));
        gameRoom.addPlayer(new Player("player2"));
        gameRoom.addPlayer(new Player("player3"));
        assertTrue(gameRoom.canJoin());
    }

    @Test
    void getName() {
        assertEquals("TestRoom", gameRoom.getName());
    }

    @Test
    void findPlayerTrue() {
        Player player = new Player("player1");
        Player player2 = new Player("player2");
        Player player3 = new Player("player3");
        Player player4 = new Player("player4");
        gameRoom.addPlayer(player);
        gameRoom.addPlayer(player2);
        gameRoom.addPlayer(player3);
        gameRoom.addPlayer(player4);
        assertEquals(player, gameRoom.findPlayer("player1"));
    }

    @Test
    void findPlayerFalse() {
        Player player = new Player("player1");
        Player player2 = new Player("player2");
        gameRoom.addPlayer(player);
        gameRoom.addPlayer(player2);
        assertNull(gameRoom.findPlayer("player3"));
    }
}