package com.example.hotelmanagermentui;

import com.example.hotelmanagermentui.User.UserController;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class IconController implements Initializable {


    private Image user = new Image(getClass().getResourceAsStream("/UI/User.png"));
    private Image userSelected = new Image(getClass().getResourceAsStream("/UI/UserSelected.png"));

    private Image room = new Image(getClass().getResourceAsStream("/UI/Room.png"));
    private Image roomSelected = new Image(getClass().getResourceAsStream("/UI/RoomSelected.png"));

    private Image order = new Image(getClass().getResourceAsStream("/UI/Order.png"));
    private Image orderSelected = new Image(getClass().getResourceAsStream("/UI/OrderSelected.png"));


    public static IconController instance;

    public static IconController getInstance() {
        if (instance == null) {
            instance = new IconController();
        }
        return instance;
    }


    //---------------------------------------------------
    @FXML
    private ImageView userIcon;

    @FXML
    private ImageView roomIcon;

    @FXML
    private ImageView orderIcon;

    public void initialize(URL location, ResourceBundle resources) {
        // 初始化代码，执行依赖于UI组件的操作
        init();
        GlobalVariable.inAni = false;
    }


    //-------------------------------------
    public void init() {
        GlobalVariable.focus = 0;
        GlobalVariable.lastfocus = 0;
    }

    public void changeUserIcon() {
        if (userIcon.getImage().equals(userSelected) || GlobalVariable.inAni) {
            return;
        }
        GlobalVariable.inAni = true;
        GlobalVariable.lastfocus = GlobalVariable.focus;
        clear();
        GlobalVariable.focus = 2;

        GlobalVariable.read();
        if (GlobalVariable.token.isEmpty()) {
            changeScene(FxmlAll.loginScene, true);
        } else {
            UserController.getUserInformation(true);
        }

        userIcon.setImage(userSelected);
    }

    public void changeRoomIcon() {
        if (roomIcon.getImage().equals(roomSelected) || GlobalVariable.inAni) {
            return;
        }
        GlobalVariable.inAni = true;
        GlobalVariable.lastfocus = GlobalVariable.focus;
        clear();
        GlobalVariable.focus = 0;
        changeScene(FxmlAll.roomScene, true);
        roomIcon.setImage(roomSelected);
    }

    public void changeOrderIcon() {
        if (orderIcon.getImage().equals(orderSelected) || GlobalVariable.inAni) {
            return;
        }
        GlobalVariable.read();
        if(GlobalVariable.token.isEmpty()){
            changeUserIcon();
            GlobalVariable.alertAdd("Please Login!");
        }else{
            if(UserController.getUserInformation(false)){
                GlobalVariable.inAni = true;
                GlobalVariable.lastfocus = GlobalVariable.focus;
                clear();
                GlobalVariable.focus = 1;
                changeScene(FxmlAll.orderScene, true);
                orderIcon.setImage(orderSelected);
            }
            else{
                GlobalVariable.alertAdd("Please Login!");
            }
        }


    }

    public void clear() {
        if (GlobalVariable.focus == 0)
            roomIcon.setImage(room);
        else if (GlobalVariable.focus == 1)
            orderIcon.setImage(order);
        else if (GlobalVariable.focus == 2)
            userIcon.setImage(user);
    }

    public void changeScene(String fxml, boolean ani) {
        try {
            Stage stage = (Stage) GlobalVariable.navigation.getScene().getWindow();
            AnchorPane targetRoot = (AnchorPane) FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = stage.getScene();
            AnchorPane root = (AnchorPane) scene.getRoot();
            AnchorPane startRoot = (AnchorPane) root.getChildren().get(0);
            if (!ani) {
                root.getChildren().add(targetRoot);
                root.getChildren().remove(startRoot);
                GlobalVariable.navigation.toFront();
            } else
                animationFormAToB(startRoot, targetRoot, root);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void animationFormAToB(AnchorPane A, AnchorPane B, AnchorPane root) throws IOException {
        Double distance = 582.0;
        if (GlobalVariable.focus < GlobalVariable.lastfocus)
            distance = -distance;
        root.getChildren().add(B);
        GlobalVariable.navigation.toFront();
        B.setLayoutX(distance);
        TranslateTransition AMove = new TranslateTransition(Duration.seconds(0.3), A);
        AMove.setToX(-2 * distance);
        TranslateTransition BMove = new TranslateTransition(Duration.seconds(0.3), B);
        BMove.setToX(-distance);

        AMove.play();
        BMove.play();
        AMove.setOnFinished(event -> {
            root.getChildren().remove(A);
            GlobalVariable.inAni = false;
        });
    }


}

