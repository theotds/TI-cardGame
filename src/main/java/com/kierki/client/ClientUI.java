package com.kierki.client;

import Game.Card;
import Rooms.GameRoom;
import Rooms.RoomManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO - SERVER-CLIENT, GAME UI, JOIN ROOM, PLAYER, JOIN AFTER CREATE

public class ClientUI extends Application {
    private static final int MAX_PLAYERS = 4;
    private static ClientUI instance;
    private final RoomManager roomManager = new RoomManager();
    private final AuthenticationModule authentication = new AuthenticationModule();
    private Stage window;
    private GameRoom playingRoom;
    private ListView<String> roomList;
    private Player player;
    private GameClient client;

    public static ClientUI getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;
        window = primaryStage;
        window.setTitle("Gra Kierki - Klient");

        Scene loginScene = buildLoginScene();
        window.setScene(loginScene);
        window.show();
    }

    private Scene buildLoginScene() {
        VBox layout = new VBox(15); // Zwiększony odstęp między elementami
        configureLayout(layout);

        // Elementy ekranu logowania
        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Nazwa użytkownika");
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Hasło");

        HBox switchBox = new HBox(15); // Zwiększony odstęp między przyciskami
        switchBox.setAlignment(Pos.CENTER);
        Button switchToLogin = createStyledButton("Logowanie", true);
        Button switchToRegister = createStyledButton("Rejestracja", false);
        switchToRegister.setOnAction(e -> window.setScene(buildRegisterScene()));
        switchBox.getChildren().addAll(switchToLogin, switchToRegister);

        Button confirmButton = createStyledButton("Zaloguj", false);
        confirmButton.setOnAction(e -> login(usernameInput.getText(), passwordInput.getText()));

        layout.getChildren().addAll(switchBox, usernameInput, passwordInput, confirmButton);

        return new Scene(layout, 600, 400); // Zwiększony rozmiar okna
    }

    private boolean login(String username, String password) {
        boolean logged = authentication.loginUser(username, password);
        if (logged) {
            try {
                this.client = new GameClient(12345);
                player = new Player(username);
                requestRoomList();

                window.setScene(buildRoomSelectionScene());
            } catch (Exception e) {
                e.printStackTrace();
                //TODO -ERROR
                // Handle connection error, such as showing an error message to the user
            }
        }
        return logged;
    }

    private void requestRoomList() throws IOException {
        if (this.client != null) {
            this.client.sendMessage("GET_ROOM_LIST");
        }
    }

    private Scene buildRegisterScene() {
        VBox layout = new VBox(15); // Zwiększony odstęp między elementami
        configureLayout(layout);

        // Elementy ekranu rejestracji
        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Nazwa użytkownika");
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Hasło");

        HBox switchBox = new HBox(15); // Zwiększony odstęp między przyciskami
        switchBox.setAlignment(Pos.CENTER);
        Button switchToLogin = createStyledButton("Logowanie", false);
        switchToLogin.setOnAction(e -> window.setScene(buildLoginScene()));
        Button switchToRegister = createStyledButton("Rejestracja", true);
        switchBox.getChildren().addAll(switchToLogin, switchToRegister);

        Button confirmButton = createStyledButton("Zarejestruj", false);
        confirmButton.setOnAction(e -> register(usernameInput, passwordInput));

        layout.getChildren().addAll(switchBox, usernameInput, passwordInput, confirmButton);

        return new Scene(layout, 600, 400); // Zwiększony rozmiar okna
    }

    private void register(TextField usernameInput, PasswordField passwordInput) {
        authentication.registerUser(usernameInput.getText(), passwordInput.getText());
    }

    private Button createStyledButton(String text, boolean isDisabled) {
        Button button = new Button(text);
        button.setDisable(isDisabled);
        button.setStyle("-fx-background-color: #ADD8E6; -fx-text-fill: black; -fx-font-size: 14px;");
        button.setMinWidth(120); // Ustawienie minimalnej szerokości
        button.setMinHeight(40); // Ustawienie minimalnej wysokości
        return button;
    }

    private void configureLayout(VBox layout) {
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FFFFFF; -fx-font-size: 14px;");
    }

    // TODO - status gry
    private Scene buildRoomSelectionScene() {
        VBox layout = new VBox(15);
        configureLayout(layout);

        Label titleLabel = new Label(player.getName() + "Wybierz pokój do gry");
        titleLabel.setStyle("-fx-font-size: 20px;");

        roomList = new ListView<>(); // Initialize the room list

        Button joinRoomButton = createStyledButton("Dołącz do pokoju", false);
        joinRoomButton.setOnAction(e -> joinSelectedRoom());

        Button createRoomButton = createStyledButton("Stwórz nowy pokój", false);
        createRoomButton.setOnAction(e -> showCreateRoomDialog());

        Button showRulesButton = createStyledButton("Zasady gry", false);
        showRulesButton.setOnAction(e -> showGameRules());

        layout.getChildren().addAll(titleLabel, roomList, joinRoomButton, createRoomButton, showRulesButton);

        return new Scene(layout, 600, 400);
    }

    private void showGameRules() {
        String rules = readGameRules();

        Alert rulesAlert = new Alert(Alert.AlertType.INFORMATION);
        rulesAlert.setTitle("Zasady gry");
        rulesAlert.setHeaderText("Zasady gry");

        // Use a TextArea inside a ScrollPane to display long text
        TextArea textArea = new TextArea(rules);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefHeight(600);

        rulesAlert.getDialogPane().setContent(textArea);
        rulesAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        rulesAlert.showAndWait();
    }

    private String readGameRules() {
        String rulesPath = "C:/TI-java/kierki/src/main/java/rules.txt";
        try (FileInputStream fileStream = new FileInputStream(rulesPath); BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            e.printStackTrace(); // This will print more detailed error information
            return "Error: Unable to load game rules.";
        }
    }

    public void updateRoomPlayerCount(String message) {
        String[] parts = message.split(":");
        if (parts.length == 3) {
            String roomName = parts[1];
            int playerCount = Integer.parseInt(parts[2]);

            Platform.runLater(() -> {
                // Assuming 'roomList' is a ListView or similar UI component
                for (int i = 0; i < roomList.getItems().size(); i++) {
                    String item = roomList.getItems().get(i);

                    // Assuming each item in the list is in the format "RoomName: X players"
                    if (item.startsWith(roomName)) {
                        // Update the item with the new player count
                        roomList.getItems().set(i, roomName + "\t\t" + playerCount + "/" + MAX_PLAYERS);
                        roomManager.getRoom(roomName).setPlayerCount(playerCount);
                        break;  // Exit the loop once the item is found and updated
                    }
                }
            });
        }
    }

    public void updateRoomList(String message) {
        // Run the update on the JavaFX Application Thread
        Platform.runLater(() -> {
            // Check if the list is started, empty, or contains room details
            if (message.equals("START_LIST")) {
                // Clear the current room list in the UI
                clearRoomList();
            } else if (message.equals("EMPTY_LIST")) {
                // Handle the case where there are no rooms available
                // For example, show a message or clear the list
                showNoRoomsAvailable();
            } else {
                // Parse the message for room details and update the UI
                String[] parts = message.split(" ");
                if (parts.length == 2 && parts[0].equals("ROOM")) {
                    String roomDetails = parts[1];
                    // Add this room to the UI list
                    if (!roomManager.doesRoomExist(roomDetails)) {
                        RoomManager.createRoom(roomDetails);
                    }
                    addRoomToList(roomDetails);
                }
            }
        });
    }

    private void clearRoomList() {
        roomList.getItems().clear();
    }

    private void showNoRoomsAvailable() {
        roomList.getItems().clear();
    }

    private void addRoomToList(String roomDetails) {
        roomList.getItems().add(roomDetails);
    }

    private void showCreateRoomDialog() {
        // Create the custom dialog.
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(player.getName() + "Stwórz nowy pokój");

        // Set the button types.
        ButtonType createButtonType = new ButtonType("Stwórz", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Create the room name label and field.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField roomName = new TextField();
        roomName.setPromptText("Nazwa pokoju");

        grid.add(new Label("Nazwa pokoju:"), 0, 0);
        grid.add(roomName, 1, 0);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a room name when the create button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return roomName.getText();
            }
            return null;
        });

        // Show the dialog and capture the result.
        Optional<String> result = dialog.showAndWait();

        // Call method to handle room creation
        result.ifPresent(this::createRoom);
    }

    private void createRoom(String roomName) {
        System.out.println("Nazwa nowego pokoju: " + roomName);
        try {
            client.sendMessage("CREATE_ROOM " + roomName);
            requestRoomList();
        } catch (IOException e) {
            System.out.println("error");
        }
    }

    private void joinSelectedRoom() {
        String selectedRoom = roomList.getSelectionModel().getSelectedItem();
        if (selectedRoom != null && !selectedRoom.trim().isEmpty()) {
            String roomId = selectedRoom.split("\t\t")[0]; // Assuming the room ID is the first part of the list item
            if (roomManager.doesRoomExist(roomId)) {
                GameRoom room = roomManager.getRoom(roomId);
                if (room != null && room.canJoin()) {
                    System.out.println("joining " + room.getName());
                    room.addPlayer(player); // You need a Player object here
                    System.out.println(roomManager.getRoom(roomId).getAmountOfPlayers());
                    // Proceed to game scene or lobby
                    try {
                        proceedToGame(room);

                    } catch (IOException e) {
                        System.out.println("card not found");
                    }
                } else {
                    // Room is full or game is in progress
                    showAlert("Cannot join room: " + roomId);
                }
            } else {
                showAlert("Room does not exist.");
            }
        } else {
            showAlert("No room selected.");
        }
    }

    private void proceedToGame(GameRoom room) throws IOException {
        client.sendMessage("JOIN_ROOM:" + room.getName() + ":" + player.getName());
        playingRoom = room;
        window.setScene(buildGameRoomScene(room));
    }

    //TODO - TEXT CHAT, VOICE CHAT, GAME LOGIC, ADD PLAYERS, EXIT,

    private void showAlert(String message) {
        // Show an alert dialog or update a status label with the message
        System.out.println(message); // Just as a placeholder, should be replaced with UI code
    }

    private Scene buildGameRoomScene(GameRoom room) {
        BorderPane borderPane = new BorderPane();

        window.setTitle("Pokój: " + room.getName() + " gracz: " + player.getName());
        // Game Area
        HBox gameArea = new HBox();
        gameArea.setPadding(new Insets(10));
        gameArea.setStyle("-fx-background-color: lightblue;");

        // Chat Area
        VBox chatArea = new VBox(10);
        chatArea.setPadding(new Insets(10));
        TextArea chatMessages = new TextArea();
        chatMessages.setEditable(false);
        TextField chatInput = new TextField();

        Button sendMessageButton = new Button("Send");
        sendMessageButton.setOnAction(event -> {
            // TODO: Implement send message action
            String message = chatInput.getText();
            chatMessages.appendText(message + "\n");
            chatInput.clear();
        });

        Button voiceChatButton = new Button("Voice Chat");
        voiceChatButton.setOnAction(event -> {
            // Placeholder for voice chat functionality
            System.out.println("Voice chat feature not implemented yet.");
        });

        chatArea.getChildren().addAll(chatMessages, chatInput, sendMessageButton, voiceChatButton);
        chatArea.setPrefWidth(300);

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> {
            // TODO: Add your exit logic here
            window.close(); // For example, just close the window
        });


        HBox playerHandArea = new HBox(10); // Horizontal box with spacing
        playerHandArea.setPadding(new Insets(10));
        playerHandArea.setAlignment(Pos.BOTTOM_CENTER); // Center align the cards

        // Assuming Player class has a method getHand() returning a list of Card objects
        player.getHand().add(0,new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        player.getHand().add(0,new Card(Card.Suit.HEARTS, Card.Rank.KING));
        player.getHand().add(0,new Card(Card.Suit.HEARTS, Card.Rank.QUEEN));
        player.getHand().add(0,new Card(Card.Suit.HEARTS, Card.Rank.JACK));
        player.getHand().add(0,new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        player.getHand().add(0,new Card(Card.Suit.HEARTS, Card.Rank.KING));
        player.getHand().add(0,new Card(Card.Suit.HEARTS, Card.Rank.QUEEN));
        player.getHand().add(0,new Card(Card.Suit.HEARTS, Card.Rank.JACK));

        player.getHand().add(0,new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        player.getHand().add(0,new Card(Card.Suit.HEARTS, Card.Rank.KING));
        player.getHand().add(0,new Card(Card.Suit.HEARTS, Card.Rank.QUEEN));
        player.getHand().add(0,new Card(Card.Suit.HEARTS, Card.Rank.JACK));
        player.getHand().add(0,new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        player.getHand().add(0,new Card(Card.Suit.HEARTS, Card.Rank.KING));
        player.getHand().add(0,new Card(Card.Suit.HEARTS, Card.Rank.QUEEN));
        player.getHand().add(0,new Card(Card.Suit.HEARTS, Card.Rank.JACK));
        player.getHand().add(0,new Card(Card.Suit.HEARTS, Card.Rank.TWO));

        for (Card card : player.getHand()) {
            System.out.println(card.toString());
            ImageView cardView = new ImageView(new Image(card.getImagePath()));
            cardView.setFitWidth(80);
            cardView.setPreserveRatio(true);
            playerHandArea.getChildren().add(cardView); // Add each card as an ImageView to the HBox
        }
        if(player.getHand().isEmpty()){
            System.out.println("empty");
        }
        gameArea.getChildren().add(playerHandArea);
        HBox exitButtonContainer = new HBox(exitButton);
        exitButtonContainer.setAlignment(Pos.BOTTOM_RIGHT); // Align to bottom-right
        exitButtonContainer.setPadding(new Insets(10)); // Add some padding

        // Layout Setup
        borderPane.setCenter(gameArea); // Assuming gameArea is defined
        borderPane.setRight(chatArea); // Assuming chatArea is defined
        borderPane.setBottom(exitButtonContainer);

        return new Scene(borderPane, 1820,980);
    }
}
