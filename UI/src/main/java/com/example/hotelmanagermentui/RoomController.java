package com.example.hotelmanagermentui;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
///////////////////////////////////////////////////////////////////////////
/// Class: RoomController
/// Description: Manages room-related interactions within the application, such as displaying available rooms and handling date selections.
///////////////////////////////////////////////////////////////////////////
public class RoomController implements Initializable {
    @FXML
    private ScrollPane roomPane;
    @FXML
    private VBox container;
    @FXML
    private AnchorPane room, datePane, selectedPane;
    @FXML
    private DatePicker formDate, toDate;
    @FXML
    private Button backBtn;

    private String typeText, checkInDate, checkOutDate;
    private int price;
    private String[] imageurl;
    private List<String> roomNumber =  new LinkedList<>();
    ///////////////////////////////////////////////////////////////////////////
    /// Method: initialize
    /// Description: Initializes the room view by setting up date selection logic and loading room data.
    /// Input: URL - the location used to resolve relative paths for the root object, ResourceBundle - the resources used to localize the root object.
    /// Output: Configures the UI and populates it with initial room data.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void initialize(URL location, ResourceBundle resources) {
        dateInit();
        backBtn.getStyleClass().add("button-hover");
        backBtn.getStyleClass().add("button-hover:hover");
        init();
        String response = GlobalVariable.get(GlobalVariable.url + "room/typeList", false);
        JSONArray jsonArray = new JSONArray(response);
        JSONObject[] data = new JSONObject[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            data[i] = jsonArray.getJSONObject(i);
        }
        addChildren( data);
        container.setPrefWidth(530);
        container.setPrefHeight(684);
    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: dateInit
    /// Description: Sets up the date pickers for selecting check-in and check-out dates.
    /// Input: None
    /// Output: Configures day cells in the date pickers to disable past and non-selectable dates.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void dateInit() {
        formDate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) < 0);

            }
        });
        formDate.setEditable(false);
        toDate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) <= 0);
            }
        });
        toDate.setEditable(false);
        formDate.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
                if (formDate.getValue() != null) {
                    toDate.setDayCellFactory(picker -> new DateCell() {
                        public void updateItem(LocalDate date, boolean empty) {
                            super.updateItem(date, empty);
                            setDisable(empty || date.compareTo(formDate.getValue()) <= 0);
                        }
                    });
                }
            }
        });
        toDate.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
                if (toDate.getValue() != null) {
                    formDate.setDayCellFactory(picker -> new DateCell() {
                        public void updateItem(LocalDate date, boolean empty) {
                            super.updateItem(date, empty);
                            setDisable(empty || date.compareTo(toDate.getValue()) >= 0 || date.compareTo(LocalDate.now()) < 0);
                        }
                    });
                }
            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: init
    /// Description: Performs initial setup for the room pane and container, such as setting the scroll policy.
    /// Input: None
    /// Output: Configures scroll behavior and spacing for the room container.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void init() {
        roomPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        container.setSpacing(10);

    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: addChildren
    /// Description: Dynamically adds room entries to the container based on provided JSON data.
    /// Input: JSONObject[] - array of JSON objects containing room data.
    /// Output: Adds visual components for each room to the container.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void addChildren(JSONObject[] data) {
        for (int i = 0; i < data.length; i++) {
            AnchorPane copy = new AnchorPane();
            int finalI = i;
            copy.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                try {

                    Stage stage = (Stage) GlobalVariable.navigation.getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(FxmlAll.roomDetail));
                    AnchorPane roomDetailPane = loader.load();
                    RoomDetail controller = loader.getController();
                    RoomDetail.instance = controller;
                    Scene scene = stage.getScene();
                    AnchorPane root = (AnchorPane) scene.getRoot();
                    AnchorPane parent = (AnchorPane) root.getChildren().get(0);
                    parent.getChildren().add(roomDetailPane);
                    selectedPane = roomDetailPane;
                    selectedPane.setVisible(false);
                    datePane.setVisible(true);
                    typeText = data[finalI].getString("type");
                    price = data[finalI].getInt("price");
                    String imagesStr = data[finalI].getString("images");
                    imagesStr = imagesStr.substring(1, imagesStr.length() - 1);
                    imageurl = imagesStr.split(", ");

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            copy.setPrefSize(room.getPrefWidth(), room.getPrefHeight());
            copy.setStyle(room.getStyle());
            for (Node child : room.getChildren()) {
                Node childCopy = copyNode(child);
                copy.getChildren().add(childCopy);
            }
            copy.setVisible(true);
            Label type = (Label) copy.getChildren().get(1);
            Label price = (Label) copy.getChildren().get(0);
            ImageView image = (ImageView) copy.getChildren().get(2);

            type.setText(type.getText() + data[i].getString("type"));
            int priceInt = data[i].getInt("price");
            price.setText(price.getText() + priceInt);
            String imagesStr = data[i].getString("images");
            imagesStr = imagesStr.substring(1, imagesStr.length() - 1);
            String[] imageUrls = imagesStr.split(", ");
            image.setImage(new Image(imageUrls[0]));


            container.getChildren().add(copy);
        }
        container.setPrefHeight(room.getPrefHeight() * container.getChildren().size()
                + 10 * container.getChildren().size());
        if (container.getPrefHeight() < 512) {
            container.setPrefHeight(513);
            container.setPrefWidth(540);
        }

    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: copyNode
    /// Description: Creates a copy of a given Node, preserving its properties and style.
    /// Input: Node - the node to be copied.
    /// Output: Returns a new node that is a copy of the input node.
    /// Returns: Node
    ///////////////////////////////////////////////////////////////////////////
    private Node copyNode(Node node) {
        if (node instanceof Button) {
            Button original = (Button) node;
            Button copy = new Button(original.getText());
            copy.setStyle(original.getStyle());
            copy.setPrefSize(original.getPrefWidth(), original.getPrefHeight());
            copy.setLayoutX(original.getLayoutX());
            copy.setLayoutY(original.getLayoutY());
            return copy;
        } else if (node instanceof Label) {
            Label original = (Label) node;
            Label copy = new Label(original.getText());
            copy.setStyle(original.getStyle());
            copy.setPrefSize(original.getPrefWidth(), original.getPrefHeight());
            copy.setLayoutX(original.getLayoutX());
            copy.setLayoutY(original.getLayoutY());
            copy.getStyleClass().add("label-center");
            return copy;
        } else if (node instanceof ImageView) {
            ImageView original = (ImageView) node;
            ImageView copy = new ImageView();
            copy.setImage(original.getImage());
            copy.setStyle(original.getStyle());
            copy.setLayoutX(original.getLayoutX());
            copy.setLayoutY(original.getLayoutY());
            copy.setFitWidth(original.getFitWidth());
            copy.setFitHeight(original.getFitHeight());
            copy.setPreserveRatio(original.isPreserveRatio());
            return copy;
        }
        return null;
    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: dateConfirmed
    /// Description: Confirms the selected dates and loads available rooms based on those dates.
    /// Input: None
    /// Output: Retrieves available rooms for the selected dates and updates the UI accordingly.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void dateConfirmed() {

        if (formDate.getValue() == null || toDate.getValue() == null) {
            GlobalVariable.alertAdd("Please select a date!");
            return;
        }
        datePane.setVisible(false);
        selectedPane.setVisible(true);
        for (GlobalVariable.page = 0; ; GlobalVariable.page++) {
            String response = GlobalVariable.get(GlobalVariable.url + "room/list?roomNumber=&&type=" + typeText + "&&available=&&checkInDate="+formDate.getValue()+"&&checkOutDate="+toDate.getValue()+"&&page=" + GlobalVariable.page, false);
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() == 0)
                break;
            for (int i = 0; i < jsonArray.length(); i++) {
                roomNumber.add(jsonArray.getJSONObject(i).getString("roomNumber"));
            }
            checkInDate = formDate.getValue().toString();
            checkOutDate = toDate.getValue().toString();
        }

        RoomDetail.getInstance().receiveData(typeText, imageurl, roomNumber, price, checkInDate, checkOutDate);
    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: backBtnClicked
    /// Description: Handles the action for the back button, typically used to navigate away from the current pane.
    /// Input: None
    /// Output: Makes the date pane invisible and possibly performs other navigation actions.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void  backBtnClicked()
    {
        datePane.setVisible(false);
    }
}


