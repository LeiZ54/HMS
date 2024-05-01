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

public class RoomDetail implements Initializable {
    @FXML
    private Button backBtn;
    @FXML
    private Label availableRoom;
    @FXML
    private Label price;
    @FXML
    private ImageView cycleImage;
    @FXML
    private Label type;
    @FXML
    private AnchorPane inforInput;
    @FXML
    private ComboBox roomBox;
    @FXML
    private TextField nameText;
    @FXML
    private TextField emailText;
    @FXML
    private Button inforbackBtn;
    @FXML
    private Label inforDate;

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

    public void initialize(URL location, ResourceBundle resources) {
        // 初始化代码，执行依赖于UI组件的操作

        backBtn.getStyleClass().add("button-hover");
        backBtn.getStyleClass().add("button-hover:hover");
        inforbackBtn.getStyleClass().add("button-hover:hover");
        inforbackBtn.getStyleClass().add("button-hover");


    }

    public void backBtnClicked() {
        Stage stage = (Stage) GlobalVariable.navigation.getScene().getWindow();
        Scene scene = stage.getScene();
        AnchorPane root = (AnchorPane) scene.getRoot();
        AnchorPane parent = (AnchorPane) root.getChildren().get(0);
        root.getChildren().remove(parent.getChildren().get(2));
        IconController.getInstance().changeScene(FxmlAll.roomScene, false);
    }

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

    private void startSlideshow() {
        nextImage();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> nextImage()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void nextImage() {
        imageIndex = (imageIndex + 1) % imagePaths.length;
        cycleImage.setImage(new Image(imagePaths[imageIndex]));
    }

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

    public void inforBackClicked() {
        inforInput.setVisible(false);
        inforDate.setText("");
        nameText.setText("");
        emailText.setText("");
    }

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
