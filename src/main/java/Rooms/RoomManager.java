package Rooms;

import java.util.HashMap;
import java.util.Map;

public class RoomManager {
    private static final Map<String, GameRoom> rooms = new HashMap<>();

    public static void createRoom(String roomId) {
        if (!rooms.containsKey(roomId)) {
            rooms.put(roomId, new GameRoom(roomId));
            System.out.println("Room " + roomId + " created.");
        } else {
            System.out.println("Room " + roomId + " already exists.");
        }
    }

    public boolean doesRoomExist(String roomId) {
        return rooms.containsKey(roomId);
    }

    public GameRoom getRoom(String roomId) {
        return rooms.get(roomId);
    }
}
