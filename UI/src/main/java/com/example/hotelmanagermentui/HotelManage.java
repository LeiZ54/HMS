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

public class HotelManage extends Application {



    @Override
    public void start(Stage stage) throws IOException {
        init(stage);
        stage.show();

    }
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


    public static void main(String[] args) {
        launch();
    }
}