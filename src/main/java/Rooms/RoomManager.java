package Rooms;

import com.kierki.client.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RoomManager {
    private static Map<String, GameRoom> rooms = new ConcurrentHashMap<>();

    public static void createRoom(String roomId) {
        if (!rooms.containsKey(roomId)) {
            rooms.put(roomId, new GameRoom());
            System.out.println("Room " + roomId + " created.");
        } else {
            System.out.println("Room " + roomId + " already exists.");
        }
    }

    public static Map<String, GameRoom> getActiveRooms() {
        return rooms;
    }

    public Map<String, GameRoom> getRooms() {
        return rooms;
    }

    public void joinRoom(String roomId, Player player) {
        if (rooms.containsKey(roomId)) {
            rooms.get(roomId).addPlayer(player);
            System.out.println("Użytkownik " + player.getName() + " dołączył do pokoju " + roomId);
        } else {
            System.out.println("Pokój o tym ID nie istnieje.");
        }
    }

    public void removeRoom(String roomId) {
        if (rooms.containsKey(roomId)) {
            rooms.remove(roomId);
            System.out.println("Pokój " + roomId + " został usunięty.");
        } else {
            System.out.println("Pokój o tym ID nie istnieje.");
        }
    }

    public Set<String> getRoomDetails() {
        return rooms.entrySet().stream()
                .map(entry -> entry.getKey() + " (" + entry.getValue().getPlayers().size() + " players)")
                .collect(Collectors.toSet());
    }

    public static void displayAllRooms() {
        if (rooms.isEmpty()) {
            System.out.println("No rooms available.");
        } else {
            System.out.println("Current rooms:");
            for (String roomId : rooms.keySet()) {
                System.out.println("Room ID: " + roomId);
                // You can add more detailed information about each room if needed
            }
        }
    }

    public boolean doesRoomExist(String roomId) {
        return rooms.containsKey(roomId);
    }

    public GameRoom getRoom(String roomId) {
        return rooms.get(roomId);
    }
}
