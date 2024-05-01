//------------------------------------------------ ---------------------------
//  Hotel management System
//  Author: Boyuan Ge, Lei Zhu, Yuheng Xia, Zhiyao Song
//  Date:05/01/2024
//  Description:
//  This project is the front-end of the Hotel Management System
package com.example.hotelmanagermentui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
//
// Class: HotelManage
//
// Description: This class represents the main application for the Hotel Management System's front-end.
//              It initializes and displays the primary user interface.
//
public class HotelManage extends Application {


    ///////////////////////////////////////////////////////////////////////////
    /// Method: start
    /// Description: Initializes and displays the main stage of the application.
    /// Input: Stage - the primary stage for this application.
    /// Output: Displays the UI elements on the stage.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void start(Stage stage) throws IOException {
        init(stage);
        stage.show();

    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: init
    /// Description: Sets up the initial scene for the application including title, icons, layout, and styles.
    /// Input: Stage - the primary stage to be initialized.
    /// Output: Sets the initial scene of the stage with all UI components loaded and styled.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void init(Stage stage) throws IOException {
        stage.setTitle("HotelManage");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/UI/icon.png")));
        AnchorPane root = new AnchorPane();
        AnchorPane initScene = FXMLLoader.load(getClass().getResource("RoomScene.fxml"));
        GlobalVariable.navigation= FXMLLoader.load(getClass().getResource("BottomNavigation.fxml"));

        GlobalVariable.navigation.setLayoutY(800);
        root.getChildren().add(initScene);
        root.getChildren().add(GlobalVariable.navigation);

        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setResizable(false);
    }

    ///////////////////////////////////////////////////////////////////////////
    /// Method: main
    /// Description: The entry point for the JavaFX application.
    /// Input: args - command line arguments.
    /// Output: Launches the application.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public static void main(String[] args) {
        launch();
    }
}