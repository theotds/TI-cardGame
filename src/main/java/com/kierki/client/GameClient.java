package com.kierki.client;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

public class GameClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    GameClient(int port) throws IOException {
        startConnection(port);
    }

    private static Socket ConnectToServer(int port) throws IOException {
        Socket socket = new Socket("localhost", port);
        System.out.println("Connected successfully with server.");
        return socket;
    }

    public void startConnection(int port) throws IOException {
        try {
            clientSocket = ConnectToServer(port);
            if (clientSocket != null) {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                CompletableFuture.runAsync(() -> handleClient(clientSocket));
            } else {
                System.out.println("Connection failed: clientSocket is null");
            }
        } catch (IOException e) {
            System.out.println("Error while connecting to server: " + e.getMessage());
        }
    }

    private void handleClient(Socket socket) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String message;
            while ((message = input.readLine()) != null) {
                processServerMessage(message);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void processServerMessage(String message) {
        Platform.runLater(() -> {
            System.out.println(message);
            if (message.equals("START_LIST") || message.equals("EMPTY_LIST") || (message.startsWith("ROOM") && !message.startsWith("ROOM_UPDATE"))) {
                ClientUI.getInstance().updateRoomList(message);
            } else if (message.startsWith("ROOM_UPDATE")) {
                ClientUI.getInstance().updateRoomPlayerCount(message);
            } else if (message.startsWith("CARDS:")) {
                String[] parts = message.split(":");
                if (parts.length >= 4) { // Check if the message is for the current player
                    String roomName = parts[1];
                    String playerName = parts[2]; // This should match the current player's name
                    String[] cards = parts[3].split(","); // Split the card details
                    ClientUI.setPlayerCards(roomName, playerName, cards);
                }
            } else if (message.startsWith("CHAT")) {
                ClientUI.getInstance().updateChat(message);
            } else if (message.startsWith("PLAY")) {
                ClientUI.getInstance().updatePlayedCardsFromServer(message);
            } else if (message.startsWith("SCOREBOARDADD")) {
                ClientUI.getInstance().addPlayerToScoreboard(message);
            }else if (message.startsWith("SCOREBOARD")) {
                ClientUI.getInstance().updateScores(message);
            }else if (message.startsWith("REMOVEPLAYEDCARDS")) {
                ClientUI.getInstance().removePlayedCards(message);
            }else if (message.startsWith("FINISH")) {
                ClientUI.getInstance().setFinish(message);
            }
        });
    }

    public void sendMessage(String msg) throws IOException {
        out.println(msg);
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}
