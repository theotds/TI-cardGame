package com.kierki.client;

import javafx.application.Platform;

import java.io.*;
import java.net.*;
import java.util.concurrent.CompletableFuture;

public class GameClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    GameClient(int port) throws IOException{
        startConnection(port);
    }
    public void startConnection(int port) throws IOException {
        try {
            clientSocket = ConnectToServer(port);
            CompletableFuture.runAsync(() -> handleClient(clientSocket));
        } catch (IOException e) {
            System.out.println("Error while connecting to server: " + e.getMessage());
        }
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

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
            ClientUI.getInstance().updateRoomList(message);
        });
    }

    private static Socket ConnectToServer(int port) throws IOException {
        Socket socket = new Socket("localhost", port);
        System.out.println("Connected successfully with server.");
        return socket;
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
