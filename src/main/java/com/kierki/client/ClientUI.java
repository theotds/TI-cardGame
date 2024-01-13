package com.kierki.client;

import Game.Card;
import Game.PlayedCardInfo;
import Game.Rank;
import Game.Suit;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.kierki.client.Consts.*;


public class ClientUI extends Application {
    public static ImageView lastClickedCardView = null;
    private static ClientUI instance;
    private static HBox playerHandArea;
    private static GameRoom playingRoom;
    private static Player player;
    private final RoomManager roomManager = new RoomManager();
    private final AuthenticationModule authentication = new AuthenticationModule();
    private Stage window;
    private ListView<String> roomList;
    private GameClient client;
    private TextArea chatMessages;
    private static Card selectedCard;
    private HBox playedCardsArea;
    private Map<String, Label> playerScoreLabels = new HashMap<>();
    private VBox scoreboard;

    public static ClientUI getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static void updatePlayerHandArea() {
        playerHandArea.getChildren().clear();
        for (Card card : player.getHand()) {
            System.out.println(card);
            ImageView cardView = new ImageView(new Image(card.getImagePath()));
            cardView.setFitWidth(CARD_WIDTH);
            cardView.setPreserveRatio(true);
            cardView.setOnMouseClicked(event -> {
                if (lastClickedCardView != null) {
                    lastClickedCardView.setTranslateY(DEFAULT_POSITION);
                }
                cardView.setTranslateY(CARD_TRANSLATE_Y);
                selectedCard = card;
                lastClickedCardView = cardView;
            });

            playerHandArea.getChildren().add(cardView);
        }
        if (player.getHand().isEmpty()) {
            System.out.println("empty");
        }
    }

    public static void setPlayerCards(String roomName, String playerName, String[] cards) {
        if (roomName.equals(playingRoom.getName()) && playerName.equals(player.getName())) {
            player.getHand().clear();
            for (String cardName : cards) {
                String[] parts = cardName.split(" of ");
                String rank = parts[0];
                String suit = parts[1];
                Card newCard = createCardFromName(suit, rank);
                player.getHand().add(newCard);
            }
            updatePlayerHandArea();
        }
    }

    private static Card createCardFromName(String suit, String rank) {
        Suit suitCard;
        Rank rankCard;
        try {
            suitCard = Suit.valueOf(suit);
            rankCard = Rank.valueOf(rank);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid suit or rank in card name");
        }

        return new Card(suitCard, rankCard);
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
        VBox layout = new VBox(LAYOUT_SPACING);
        configureLayout(layout);

        // Elementy ekranu logowania
        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Nazwa użytkownika");
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Hasło");

        HBox switchBox = new HBox(LAYOUT_SPACING);
        switchBox.setAlignment(Pos.CENTER);
        Button switchToLogin = createStyledButton("Logowanie", true);
        Button switchToRegister = createStyledButton("Rejestracja", false);
        switchToRegister.setOnAction(e -> window.setScene(buildRegisterScene()));
        switchBox.getChildren().addAll(switchToLogin, switchToRegister);

        Button confirmButton = createStyledButton("Zaloguj", false);
        confirmButton.setOnAction(e -> login(usernameInput.getText(), passwordInput.getText()));

        layout.getChildren().addAll(switchBox, usernameInput, passwordInput, confirmButton);

        return new Scene(layout, BASIC_SCENE_WIDTH, BASIC_SCENE_HEIGHT);
    }

    private boolean login(String username, String password) {
        boolean logged = authentication.loginUser(username, password);
        if (logged) {
            try {
                this.client = new GameClient(SERVER_PORT);
                player = new Player(username);
                requestRoomList();

                window.setScene(buildRoomSelectionScene());
            } catch (Exception e) {
                e.printStackTrace();
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
        VBox layout = new VBox(LAYOUT_SPACING);
        configureLayout(layout);

        // Elementy ekranu rejestracji
        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Nazwa użytkownika");
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Hasło");

        HBox switchBox = new HBox(LAYOUT_SPACING);
        switchBox.setAlignment(Pos.CENTER);
        Button switchToLogin = createStyledButton("Logowanie", false);
        switchToLogin.setOnAction(e -> window.setScene(buildLoginScene()));
        Button switchToRegister = createStyledButton("Rejestracja", true);
        switchBox.getChildren().addAll(switchToLogin, switchToRegister);

        Button confirmButton = createStyledButton("Zarejestruj", false);
        confirmButton.setOnAction(e -> register(usernameInput, passwordInput));

        layout.getChildren().addAll(switchBox, usernameInput, passwordInput, confirmButton);

        return new Scene(layout, BASIC_SCENE_WIDTH, BASIC_SCENE_HEIGHT);
    }

    private void register(TextField usernameInput, PasswordField passwordInput) {
        authentication.registerUser(usernameInput.getText(), passwordInput.getText());
    }

    private Button createStyledButton(String text, boolean isDisabled) {
        Button button = new Button(text);
        button.setDisable(isDisabled);
        button.setStyle("-fx-background-color: #ADD8E6; -fx-text-fill: black; -fx-font-size: 14px;");
        button.setMinWidth(120);
        button.setMinHeight(40);
        return button;
    }

    private void configureLayout(VBox layout) {
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FFFFFF; -fx-font-size: 14px;");
    }

    private Scene buildRoomSelectionScene() {
        VBox layout = new VBox(LAYOUT_SPACING);
        configureLayout(layout);

        Label titleLabel = new Label(player.getName() + "Wybierz pokój do gry");
        titleLabel.setStyle("-fx-font-size: 20px;");

        roomList = new ListView<>();

        Button joinRoomButton = createStyledButton("Dołącz do pokoju", false);
        joinRoomButton.setOnAction(e -> joinSelectedRoom());

        Button createRoomButton = createStyledButton("Stwórz nowy pokój", false);
        createRoomButton.setOnAction(e -> showCreateRoomDialog());

        Button showRulesButton = createStyledButton("Zasady gry", false);
        showRulesButton.setOnAction(e -> showGameRules());

        layout.getChildren().addAll(titleLabel, roomList, joinRoomButton, createRoomButton, showRulesButton);

        return new Scene(layout, BASIC_SCENE_WIDTH, BASIC_SCENE_HEIGHT);
    }

    private void showGameRules() {
        String rules = readGameRules();

        Alert rulesAlert = new Alert(Alert.AlertType.INFORMATION);
        rulesAlert.setTitle("Zasady gry");
        rulesAlert.setHeaderText("Zasady gry");

        TextArea textArea = new TextArea(rules);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefHeight(BASIC_SCENE_WIDTH);

        rulesAlert.getDialogPane().setContent(textArea);
        rulesAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        rulesAlert.showAndWait();
    }

    private String readGameRules() {
        String rulesPath = "C:/TI-java/kierki/src/main/java/rules.txt";
        try (FileInputStream fileStream = new FileInputStream(rulesPath); BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Unable to load game rules.";
        }
    }

    public void updateRoomPlayerCount(String message) {
        String[] parts = message.split(":");
        if (parts.length == 3) {
            String roomName = parts[1];
            int playerCount = Integer.parseInt(parts[2]);

            Platform.runLater(() -> {
                for (int i = 0; i < roomList.getItems().size(); i++) {
                    String item = roomList.getItems().get(i);

                    if (item.startsWith(roomName)) {
                        roomList.getItems().set(i, roomName + "\t\t" + playerCount + "/" + MAX_PLAYERS);
                        roomManager.getRoom(roomName).setPlayerCount(playerCount);
                        break;
                    }
                }
            });
        }
    }

    public void updateRoomList(String message) {
        Platform.runLater(() -> {
            if (message.equals("START_LIST")) {
                clearRoomList();
            } else if (message.equals("EMPTY_LIST")) {
                showNoRoomsAvailable();
            } else {
                String[] parts = message.split(" ");
                if (parts.length == 2 && parts[0].equals("ROOM")) {
                    String roomDetails = parts[1];
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
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(player.getName() + "Stwórz nowy pokój");

        ButtonType createButtonType = new ButtonType("Stwórz", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(PADDING);
        grid.setVgap(PADDING);
        grid.setPadding(new Insets(20, 150, PADDING, PADDING));

        TextField roomName = new TextField();
        roomName.setPromptText("Nazwa pokoju");

        grid.add(new Label("Nazwa pokoju:"), 0, 0);
        grid.add(roomName, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return roomName.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

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
            String roomId = selectedRoom.split("\t\t")[0];
            if (roomManager.doesRoomExist(roomId)) {
                GameRoom room = roomManager.getRoom(roomId);
                if (room != null && room.canJoin()) {
                    System.out.println("joining " + room.getName());
                    room.addPlayer(player);
                    System.out.println(roomManager.getRoom(roomId).getAmountOfPlayers());
                    try {
                        proceedToGame(room);

                    } catch (IOException e) {
                        System.out.println("card not found");
                    }
                } else {
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

        window.setX(0);
        window.setY(0);
        window.setScene(buildGameRoomScene(room));
    }

    private void showAlert(String message) {
        System.out.println(message);
    }

    private Scene buildGameRoomScene(GameRoom room) {
        BorderPane borderPane = new BorderPane();

        window.setTitle("Pokój: " + room.getName() + " gracz: " + player.getName());
        // Game Area
        VBox gameArea = new VBox();
        gameArea.setPadding(new Insets(PADDING));
        gameArea.setStyle("-fx-background-color: lightblue;");

        // Chat Area
        VBox chatArea = new VBox(PADDING);
        chatArea.setPadding(new Insets(PADDING));
        chatMessages = new TextArea();
        chatMessages.setEditable(false);
        TextField chatInput = new TextField();

        VBox scoreboardContainer = new VBox(PADDING);
        Label scoreboardTitle = new Label("scoreboard:");
        scoreboard = new VBox(PADDING);
        scoreboard = new VBox(PADDING);

        scoreboard.setAlignment(Pos.CENTER);
        scoreboardContainer.getChildren().addAll(scoreboardTitle, scoreboard);

        Button sendMessageButton = new Button("Send");
        sendMessageButton.setOnAction(event -> {
            String message = chatInput.getText();
            try {
                client.sendMessage("CHAT:ROOM-" + room.getName() + ":" + player.getName() + ":" + message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            chatInput.clear();
        });

        Button confirmButton = createStyledButton("Confirm", false);
        confirmButton.setOnAction(event -> {
            try {
                if (selectedCard != null) {
                    client.sendMessage("PLAY:" + room.getName() + ":" + player.getName() + ":" + selectedCard.toString());
                } else {
                    System.out.println("card not selected");
                }
            } catch (IOException e) {
                System.out.println("server Play error");
            }
        });

        playedCardsArea = new HBox(PADDING);
        playedCardsArea.setPadding(new Insets(PADDING));
        playedCardsArea.setAlignment(Pos.CENTER);


        chatArea.getChildren().addAll(chatMessages, chatInput, sendMessageButton, scoreboardContainer, confirmButton);
        chatArea.setPrefWidth(CHAT_SIZE);

        playerHandArea = new HBox(PADDING);
        playerHandArea.setPadding(new Insets(PADDING));
        playerHandArea.setAlignment(Pos.BOTTOM_CENTER);
        updatePlayerHandArea();

        gameArea.setAlignment(Pos.BOTTOM_CENTER);
        gameArea.getChildren().addAll(playedCardsArea, playerHandArea);

        // Layout Setup
        borderPane.setCenter(gameArea);
        borderPane.setRight(chatArea);

        return new Scene(borderPane, GAMESCREEN_WIDTH, GAMESCREEN_HEIGHT);
    }

    public void addPlayerToScoreboard(String message) {
        String[] parts = message.split(":");
        if (parts.length == 4) {
            String roomName = parts[1];
            String playerName = parts[2];
            String score = parts[3];
            if (roomName.equals(playingRoom.getName())) {
                Platform.runLater(() -> {
                    Label scoreLabel = new Label(playerName + ": " + score);
                    playerScoreLabels.put(playerName, scoreLabel);
                    scoreboard.getChildren().add(scoreLabel);
                });
            }
        }
    }

    public void updateScores(String message) {
        String[] parts = message.split(":");
        if (parts.length == 4) {
            String roomName = parts[1];
            String playerName = parts[2];
            String score = parts[3];
            if (roomName.equals(playingRoom.getName())) {
                Platform.runLater(() -> {
                    Label scoreLabel = playerScoreLabels.get(playerName);
                    if (scoreLabel != null) {
                        scoreLabel.setText(playerName + ": " + score);
                    }
                });
            }
        }

    }

    private void updatePlayedCards(PlayedCardInfo cardInfo) {
        ImageView cardView = new ImageView(new Image(cardInfo.getCard().getImagePath()));
        cardView.setFitWidth(CARD_WIDTH);
        cardView.setPreserveRatio(true);

        Label playerNameLabel = new Label(cardInfo.getPlayer());

        VBox cardAndPlayer = new VBox(cardView, playerNameLabel);
        cardAndPlayer.setAlignment(Pos.CENTER);

        playedCardsArea.getChildren().add(cardAndPlayer);

    }

    public void updateChat(String message) {
        String[] parts = message.split(":");
        if (parts.length == 4) {
            String roomName = parts[1];
            String playerNick = parts[2];
            String chatMessage = parts[3];
            if (roomName.equals(playingRoom.getName())) {
                Platform.runLater(() -> {
                    chatMessages.appendText(playerNick + ": " + chatMessage + "\n");
                });
            }
        }
    }

    public void updatePlayedCardsFromServer(String message) {
        String[] parts = message.split(":");
        if (parts.length == 4) {
            String roomName = parts[1];
            String playerNick = parts[2];
            String cardName = parts[3];
            if (roomName.equals(playingRoom.getName())) {
                String[] cardDetails = cardName.split(" of ");
                String rank = cardDetails[0];
                String suit = cardDetails[1];
                Card card = createCardFromName(suit, rank);
                PlayedCardInfo cardInfo = new PlayedCardInfo(card, playerNick);
                updatePlayedCards(cardInfo);
                if (playerNick.equals(player.getName())) {
                    player.removeCard(rank, suit);
                    selectedCard = null;
                    updatePlayerHandArea();
                }
            }
        }
    }

    public void removePlayedCards(String message) {
        String[] parts = message.split(":");
        if (parts.length == 2) {
            String roomName = parts[1];
            if (roomName.equals(playingRoom.getName())) {
                playingRoom.getPlayedCards().clear();
                playedCardsArea.getChildren().clear();
            }
        }
    }

    public void setFinish(String message) {
        String[] parts = message.split(":");
        if (parts.length == 4) {
            String roomName = parts[1];
            String winnerName = parts[2];
            String score = parts[3];
            if (roomName.equals(playingRoom.getName())) {
                VBox endScreenLayout = new VBox(PADDING);
                endScreenLayout.setAlignment(Pos.CENTER);

                Label winnerLabel = new Label("Winner: " + winnerName);
                Label scoreLabel = new Label("Score: " + score);
                Button exitButton = new Button("Exit");

                endScreenLayout.getChildren().addAll(winnerLabel, scoreLabel, exitButton);

                Scene endScene = new Scene(endScreenLayout, 300, 200);
                Platform.runLater(() -> {
                    window.setScene(endScene);
                });

                exitButton.setOnAction(e -> {
                    Platform.exit();
                });
            }
        }
    }
}
