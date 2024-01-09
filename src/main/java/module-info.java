module com.kierki.kierki {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.kierki.client to javafx.fxml;
    exports com.kierki.client;
    exports Game;
    opens Game to javafx.fxml;
    exports Rooms;
    opens Rooms to javafx.fxml;
}