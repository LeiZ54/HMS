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
//
// Class: IconController
//
// Description: Manages the icon interactions within the application's navigation, including switching scenes based on icon selection.
//
public class IconController implements Initializable {
    private Image user = new Image(getClass().getResourceAsStream("/UI/User.png"));
    private Image userSelected = new Image(getClass().getResourceAsStream("/UI/UserSelected.png"));

    private Image room = new Image(getClass().getResourceAsStream("/UI/Room.png"));
    private Image roomSelected = new Image(getClass().getResourceAsStream("/UI/RoomSelected.png"));

    private Image order = new Image(getClass().getResourceAsStream("/UI/Order.png"));
    private Image orderSelected = new Image(getClass().getResourceAsStream("/UI/OrderSelected.png"));

    @FXML
    private ImageView userIcon, roomIcon, orderIcon;
    public static IconController instance;
    ///////////////////////////////////////////////////////////////////////////
    /// Method: getInstance
    /// Description: Provides a singleton instance of IconController.
    /// Input: None
    /// Output: None
    /// Returns: IconController - the singleton instance.
    ///////////////////////////////////////////////////////////////////////////
    public static IconController getInstance() {
        if (instance == null) {
            instance = new IconController();
        }
        return instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    /// Method: initialize
    /// Description: Initializes the controller, setting up initial icon states and configuration.
    /// Input: URL - the location used to resolve relative paths for the root object, ResourceBundle - the resources used to localize the root object.
    /// Output: Initializes icons based on user authentication state.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void initialize(URL location, ResourceBundle resources) {
        init();
        GlobalVariable.inAni = false;
    }


    ///////////////////////////////////////////////////////////////////////////
    /// Method: init
    /// Description: Initializes focus settings for navigation icons.
    /// Input: None
    /// Output: Resets focus to the default state.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void init() {
        GlobalVariable.focus = 0;
        GlobalVariable.lastfocus = 0;
    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: changeUserIcon
    /// Description: Changes the displayed user icon based on selection, performing user checks and potentially redirecting the user.
    /// Input: None
    /// Output: Changes the user icon to a selected state, possibly triggering a scene change.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
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
    ///////////////////////////////////////////////////////////////////////////
    /// Method: changeRoomIcon
    /// Description: Changes the displayed room icon based on selection and updates the displayed scene.
    /// Input: None
    /// Output: Changes the room icon to a selected state and updates the scene.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
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
    ///////////////////////////////////////////////////////////////////////////
    /// Method: changeOrderIcon
    /// Description: Changes the displayed order icon based on user selection and updates the displayed scene, handling login status.
    /// Input: None
    /// Output: Changes the order icon to a selected state, possibly triggering a scene change.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
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
    ///////////////////////////////////////////////////////////////////////////
    /// Method: clear
    /// Description: Resets icons to their unselected states based on the current focus.
    /// Input: None
    /// Output: Resets navigation icons to their default images.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void clear() {
        if (GlobalVariable.focus == 0)
            roomIcon.setImage(room);
        else if (GlobalVariable.focus == 1)
            orderIcon.setImage(order);
        else if (GlobalVariable.focus == 2)
            userIcon.setImage(user);
    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: changeScene
    /// Description: Changes the currently displayed scene based on the given FXML file, with optional animation.
    /// Input: String - the FXML file for the new scene, boolean - if true, animate the transition.
    /// Output: Changes the scene displayed in the main application window.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
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
    ///////////////////////////////////////////////////////////////////////////
    /// Method: animationFormAToB
    /// Description: Animates the transition between two scenes represented by AnchorPane A and B.
    /// Input: AnchorPane A - the current scene, AnchorPane B - the new scene, AnchorPane root - the root container.
    /// Output: Performs animation and updates the displayed scene.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
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

