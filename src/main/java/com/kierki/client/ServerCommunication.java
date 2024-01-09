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
            this.socket = new Socket(host, port);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;
        } catch (IOException e) {
            System.out.println("Could not connect to server: " + e.getMessage());
            return false;
        }
    }

    public void handleMessage(String message, PrintWriter out) {
        if (isCreateRoomRequest(message)) {
            String roomId = extractRoomIdFromMessage(message);
            RoomManager.createRoom(roomId); // Directly calling the static method
            RoomManager.displayAllRooms();
        }else if (message.equals("GET_ROOM_LIST")) {
            sendRoomList(this.out);
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

    // Method to send the list of rooms to the client
    public void sendRoomList(PrintWriter out) {
        Map<String, GameRoom> rooms = RoomManager.getActiveRooms();
        // Construct a message containing the list of rooms
        StringBuilder roomListMsg = new StringBuilder("ROOM_LIST ");
        for (String roomId : rooms.keySet()) {
            roomListMsg.append(roomId).append(", ");
        }
        // Remove the last comma and space
        if (roomListMsg.length() > 0) {
            roomListMsg.setLength(roomListMsg.length() - 2);
        }
        // Send the message to the client
        out.println(roomListMsg.toString());
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
