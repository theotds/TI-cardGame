package Server;

import Rooms.GameRoom;
import com.kierki.client.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    // list for all rooms
    private static final HashMap<String, GameRoom> gameRooms = new HashMap<>();
    // clientWriters is list of writers for all connected clients
    private static final List<PrintWriter> clientWriters = new CopyOnWriteArrayList<>();
    private static final int PORT = 12345;
    private static final ExecutorService clientHandlingPool = Executors.newFixedThreadPool(8);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Game Server is running in port " + PORT);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    // Check for null socket (unlikely but safe to handle)
                    if (clientSocket != null) {
                        // Create a CompletableFuture to handle the client asynchronously
                        clientHandlingPool.execute(() -> handleClient(clientSocket));
                    } else {
                        System.err.println("Accepted client socket is null");
                    }
                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                    // Handle exception as needed (logging, retrying, etc.)
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void handleClient(Socket clientSocket) {
        // Check if the client is connected
        if (clientSocket.isConnected()) {
            System.out.println("Client connected: " + clientSocket.getPort());
        }
        // output is the writter to specific client that is connected
        PrintWriter output = null;
        try {

            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            clientWriters.add(output);

            String message;
            while ((message = input.readLine()) != null) {
                if (isJoinRoomMessage(message)) {
                    String roomName = joinRoom(message);
                    broadcastRoomStatus(roomName);
                } else if(isChatMessage(message)){
                    processChatMessage(message);
                } else processClientMessage(clientSocket, message);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (output != null) {
                clientWriters.remove(output);
            }
        }
    }

    private static void processChatMessage(String message) {
        System.out.println(message);
        if (message.startsWith("CHAT:")) {
            String[] parts = message.split(":", 3);
            if (parts.length == 3) {
                String roomName = parts[1].split("-")[1];
                String chatMessage = parts[2];

                GameRoom room = gameRooms.get(roomName);
                if (room != null) {
                    // Broadcast the message to all players in the room
                    for (PrintWriter writer : clientWriters) {
                        writer.println("CHAT:" + roomName + ":" + chatMessage);
                    }
                } else {
                    System.out.println("Room not found: " + roomName);
                }
            }
        }
    }
    private static boolean isChatMessage(String message) {
        return message.startsWith("CHAT:");
    }

    private static String joinRoom(String message) {
        String roomName = extractRoomName(message);
        GameRoom room = gameRooms.getOrDefault(roomName, new GameRoom(roomName));
        if (room.canJoin()) {
            Player player = new Player(extractPlayerName(message));
            System.out.println(player.getName() + " joined game " + room.getName());
            room.addPlayer(player);
            gameRooms.putIfAbsent(roomName, room);
        }
        return roomName;
    }

    private static void broadcastRoomStatus(String roomName) {
        GameRoom room = gameRooms.get(roomName);
        if (room != null) {
            String statusMessage = "ROOM_UPDATE:" + roomName + ":" + room.getAmountOfPlayers();
            for (PrintWriter writer : clientWriters) {
                writer.println(statusMessage);
            }
        }
    }

    private static String extractRoomName(String message) {
        String[] parts = message.split(":");
        return parts.length > 1 ? parts[1] : "";
    }

    private static String extractPlayerName(String message) {
        String[] parts = message.split(":");
        return parts.length > 2 ? parts[2] : "";
    }

    private static boolean isJoinRoomMessage(String message) {
        return message.startsWith("JOIN_ROOM:");
    }

    private static void processClientMessage(Socket clientSocket, String message) {
        String[] parts = message.split(" ");
        if (parts.length == 2) {

            String action = parts[0];
            String details = parts[1];

            if (action.equals("CREATE_ROOM")) {
                addRoom(details, clientSocket);
                broadcastRoomStatus(details);
            }
        }
        if (message.equals("GET_ROOM_LIST")) {
            sendRoomsList();
            broadcastAllRoomStatus();
        }
    }

    private static void broadcastAllRoomStatus() {
        for (Map.Entry<String, GameRoom> entry : gameRooms.entrySet()) {
            String roomName = entry.getKey();
            GameRoom room = entry.getValue();
            String statusMessage = "ROOM_UPDATE:" + roomName + ":" + room.getPlayers().size();
            for (PrintWriter writer : clientWriters) {
                writer.println(statusMessage);
            }
        }
    }

    private static void addRoom(String roomName, Socket clientSocket) {
        GameRoom newRoom = new GameRoom(roomName);
        gameRooms.put(roomName, newRoom);
        showRooms();
        sendRoomsList();
    }

    private static void sendRoomsList() {
        synchronized (gameRooms) {
            // Check if the list of rooms is empty
            if (gameRooms.isEmpty()) {
                for (PrintWriter clientWriter : clientWriters) {
                    clientWriter.println("EMPTY_LIST");
                }
            } else {
                // Iterate over each client and send them the list of rooms
                for (PrintWriter clientWriter : clientWriters) {
                    clientWriter.println("START_LIST");
                    for (Map.Entry<String, GameRoom> entry : gameRooms.entrySet()) {
                        String roomName = entry.getKey();
                        // Assuming GameRoom has methods to get details
                        GameRoom room = entry.getValue();
                        clientWriter.println("ROOM " + roomName);
                    }
                }
            }
        }
    }

    public static void showRooms() {
        if (gameRooms.isEmpty()) {
            System.out.println("There are no game rooms available.");
        } else {
            System.out.println("List of Game Rooms:");
            for (String roomName : gameRooms.keySet()) {
                // You can also retrieve the GameRoom object and display more details if needed.
                // GameRoom room = appointmentEntries.get(roomName);
                System.out.println(roomName);
            }
        }
    }
}
