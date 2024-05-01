module com.example.hotelmanagermentui {
    requires javafx.controls;
    requires javafx.fxml;
    requires json;
    requires java.sql;

    opens com.example.hotelmanagermentui to javafx.fxml;
    exports com.example.hotelmanagermentui;
    exports com.example.hotelmanagermentui.User;
    opens com.example.hotelmanagermentui.User to javafx.fxml;
}