package com.kierki.client;

public class Consts {
    //UI
    public static int DEFAULT_POSITION = 0;
    public static int CARD_TRANSLATE_Y = -20;
    public static int LAYOUT_SPACING = 15;
    public static int PADDING = 10;
    public static int BASIC_SCENE_WIDTH = 600;
    public static int BASIC_SCENE_HEIGHT = 400;
    public static int CHAT_SIZE = 200;
    public static int GAMESCREEN_WIDTH = 1200;
    public static int GAMESCREEN_HEIGHT = 600;
    public static int GAMEAREA_WIDTH = GAMESCREEN_WIDTH - CHAT_SIZE;
    public static int CARD_WIDTH = ((GAMEAREA_WIDTH) / 15);


    //GAME
    public static int SERVER_PORT = 12345;
    public static int ROUNDS = 1;
    public static int MAX_PLAYERS = 4;
    public static int THREAD_NUMBER = 8;

    // PATHS
    public static final String RULES_PATH = "C:/TI-java/kierki/src/main/java/rules.txt";
    public static final String CARDS_PATH = "C:/TI-java/kierki/src/main/Images/Cards/";
    public static final String FILE_PATH = "C:/TI-java/kierki/src/main/java/users.txt";

}
