package Server;

import Rooms.GameRoom;
import com.kierki.client.ServerCommunication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

class ClientHandler implements Runnable {
    private Socket clientSocket;
    Map<String, GameRoom> activeRooms;
    ServerCommunication serverCommunication;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            serverCommunication = new ServerCommunication();
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                // Assuming ServerCommunication is a class with static methods
                serverCommunication.handleMessage(clientMessage);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
