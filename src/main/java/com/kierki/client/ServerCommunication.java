package com.kierki.client;

import Rooms.GameRoom;
import Rooms.RoomManager;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ServerCommunication {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public boolean connectToServer(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;
        } catch (IOException e) {
            System.out.println("Could not connect to server: " + e.getMessage());
            return false;
        }
    }

    public void handleMessage(String message) {
        if (isCreateRoomRequest(message)) {
            String roomId = extractRoomIdFromMessage(message);
            RoomManager.createRoom(roomId); // Directly calling the static method
            RoomManager.displayAllRooms();
        }
        // Handle other message types as needed
    }
    private boolean isCreateRoomRequest(String message) {
        return message.startsWith("CREATE_ROOM");
    }

    private String extractRoomIdFromMessage(String message) {
        // Assuming the message format is "CREATE_ROOM roomId"
        return message.substring("CREATE_ROOM".length()).trim();
    }

    // Method to send a message to the server
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    // Method to close the connection
    public void closeConnection() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Error when closing the socket: " + e.getMessage());
        }
    }
}
