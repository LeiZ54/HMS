package com.example.hotelmanagermentui;

import com.example.hotelmanagermentui.User.UserController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
///////////////////////////////////////////////////////////////////////////
/// Class: RoomDetail
/// Description: Manages the detailed view of room availability, including the ability to book rooms and handle room data.
///////////////////////////////////////////////////////////////////////////
public class RoomDetail implements Initializable {
    @FXML
    private Button backBtn, inforbackBtn;
    @FXML
    private Label availableRoom, price, type, inforDate;
    @FXML
    private ImageView cycleImage;
    @FXML
    private AnchorPane inforInput;
    @FXML
    private ComboBox roomBox;
    @FXML
    private TextField nameText, emailText;

    private int imageIndex = 0;
    private String[] imagePaths;
    private List<String> roomNumber;
    private String checkInDate;
    private String checkOutDate;


    public static RoomDetail instance = null;

    public static RoomDetail getInstance() {
        if (instance == null) {
            instance = new RoomDetail();
        }
        return instance;
    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: initialize
    /// Description: Initializes the room detail view by configuring UI styles and button behaviors.
    /// Input: URL - the location used to resolve relative paths for the root object, ResourceBundle - the resources used to localize the root object.
    /// Output: Configures initial CSS styles for buttons.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void initialize(URL location, ResourceBundle resources) {

        backBtn.getStyleClass().add("button-hover");
        backBtn.getStyleClass().add("button-hover:hover");
        inforbackBtn.getStyleClass().add("button-hover:hover");
        inforbackBtn.getStyleClass().add("button-hover");


    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: backBtnClicked
    /// Description: Handles the action to navigate back to the previous scene.
    /// Input: None
    /// Output: Navigates back to the room scene.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void backBtnClicked() {
        Stage stage = (Stage) GlobalVariable.navigation.getScene().getWindow();
        Scene scene = stage.getScene();
        AnchorPane root = (AnchorPane) scene.getRoot();
        AnchorPane parent = (AnchorPane) root.getChildren().get(0);
        root.getChildren().remove(parent.getChildren().get(2));
        IconController.getInstance().changeScene(FxmlAll.roomScene, false);
    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: receiveData
    /// Description: Receives data to display detailed room information, sets up a slideshow of images.
    /// Input: Various details about the room, including type, images, room numbers, and dates.
    /// Output: Updates the UI with detailed room information.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void receiveData(String typeText, String[] imageurl, List<String> roomNumber, int price, String checkInDate, String checkOutDate) {
        type.setText(typeText);
        this.price.setText(String.valueOf(price));
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        imagePaths = imageurl;
        this.roomNumber = roomNumber;
        String roomNumberText = "";
        for (int i = 0; i < roomNumber.size(); i++) {
            roomNumberText = roomNumberText + " " + roomNumber.get(i);
        }
        availableRoom.setText(roomNumberText);
        startSlideshow();

    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: startSlideshow
    /// Description: Starts an image slideshow for the room.
    /// Input: None
    /// Output: Cycles through images periodically.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    private void startSlideshow() {
        nextImage();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> nextImage()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: nextImage
    /// Description: Updates the room image to the next one in the slideshow.
    /// Input: None
    /// Output: Changes the displayed room image.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    private void nextImage() {
        imageIndex = (imageIndex + 1) % imagePaths.length;
        cycleImage.setImage(new Image(imagePaths[imageIndex]));
    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: bookBtnClicked
    /// Description: Handles the booking process for selected rooms, showing booking information input form if authenticated.
    /// Input: None
    /// Output: Displays booking form or prompts for login if not authenticated.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void bookBtnClicked() {
        GlobalVariable.read();
        if (GlobalVariable.token.isEmpty()) {
            GlobalVariable.alertAdd("Please Login!");
        } else {
            if(roomNumber.size() == 0) {
                GlobalVariable.alertAdd("No more rooms available! Please select other day!");
            }else{
                roomBox.getItems().clear();
                for (int i = 0; i < roomNumber.size(); i++) {
                    roomBox.getItems().addAll(roomNumber.get(i));
                }
                inforDate.setText(checkInDate + " to " + checkOutDate);
                inforInput.setVisible(true);
            }
        }

    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: inforBackClicked
    /// Description: Handles the action to navigate away from the booking information input form.
    /// Input: None
    /// Output: Clears and hides the booking information form.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void inforBackClicked() {
        inforInput.setVisible(false);
        inforDate.setText("");
        nameText.setText("");
        emailText.setText("");
    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: confirmBtnClicked
    /// Description: Confirms the booking information and sends it to the server.
    /// Input: None
    /// Output: Submits the booking information and handles the server response.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void confirmBtnClicked() {
        String jsonInputString = new JSONObject().put("roomNumber", roomBox.getValue())
                .put("customerName", nameText.getText())
                .put("customerEmail", emailText.getText())
                .put("checkInDate", checkInDate)
                .put("checkOutDate", checkOutDate).toString();
        System.out.println(jsonInputString);
        String response = GlobalVariable.post(jsonInputString, GlobalVariable.url + "order/create");
        System.out.println(response);
        JSONObject json = new JSONObject(response);
        String message = json.isNull("message")?"":json.getString("message");
        if(!message.isEmpty()) {
            GlobalVariable.alertAdd(message);
        }
        inforBackClicked();
        backBtnClicked();
    }
}
