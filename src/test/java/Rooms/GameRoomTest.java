package Rooms;

import com.kierki.client.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameRoomTest {

    @Test
    void getAmountOfPlayers_0Players() {
        GameRoom room = new GameRoom("roomName");
        assertEquals(0, room.getAmountOfPlayers());
    }

    @Test
    void getAmountOfPlayers_1Player() {
        GameRoom room = new GameRoom("roomName");
        room.addPlayer(new Player("player1"));
        assertEquals(1, room.getAmountOfPlayers());
    }

    @Test
    void getAmountOfPlayers_2Player() {
        GameRoom room = new GameRoom("roomName");
        room.addPlayer(new Player("player1"));
        room.addPlayer(new Player("player2"));
        assertEquals(2, room.getAmountOfPlayers());
    }

    @Test
    void isFullTrue() {
        GameRoom room = new GameRoom("roomName");
        room.addPlayer(new Player("player1"));
        room.addPlayer(new Player("player2"));
        room.addPlayer(new Player("player3"));
        room.addPlayer(new Player("player4"));
        assertTrue(room.isFull());
    }

    @Test
    void isFullFalse() {
        GameRoom room = new GameRoom("roomName");
        room.addPlayer(new Player("player1"));
        room.addPlayer(new Player("player2"));
        room.addPlayer(new Player("player3"));
        assertFalse(room.isFull());
    }

    @Test
    void canJoinWhenFullRoom() {
        GameRoom room = new GameRoom("roomName");
        room.addPlayer(new Player("player1"));
        room.addPlayer(new Player("player2"));
        room.addPlayer(new Player("player3"));
        room.addPlayer(new Player("player4"));
        assertFalse(room.canJoin());
    }

    @Test
    void canJoinWhenOnePlaceLeft() {
        GameRoom room = new GameRoom("roomName");
        room.addPlayer(new Player("player1"));
        room.addPlayer(new Player("player2"));
        room.addPlayer(new Player("player3"));
        assertTrue(room.canJoin());
    }
}