package com.example.hotelmanagermentui;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
///////////////////////////////////////////////////////////////////////////
/// Class: OrderController
/// Description: Manages the order-related interactions within the application, including displaying and updating order details.
///////////////////////////////////////////////////////////////////////////
public class OrderController implements Initializable {
    @FXML
    private VBox container;
    @FXML
    private AnchorPane order;
    @FXML
    private ScrollPane orderPane;
    @FXML
    private Label roomNumber, checkInDate, checkOutDate, status;
    @FXML
    private TextField searchText;

    ///////////////////////////////////////////////////////////////////////////
    /// Method: initialize
    /// Description: Initializes the order management UI, setting up dynamic loading of order information.
    /// Input: URL - the location used to resolve relative paths for the root object, ResourceBundle - the resources used to localize the root object.
    /// Output: Configures initial settings for scroll pane and labels.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void initialize(URL location, ResourceBundle resources) {
        init();
        GlobalVariable.page = 0;
        newInfo();

    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: init
    /// Description: Configures the initial settings for the order pane, including scroll behavior and label styles.
    /// Input: None
    /// Output: Sets up the UI components' initial properties.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void init() {
        orderPane.setMinSize(542, 628);
        orderPane.setMaxSize(542, 628);
        roomNumber.getStyleClass().add("label-center");
        checkInDate.getStyleClass().add("label-center");
        checkOutDate.getStyleClass().add("label-center");
        status.getStyleClass().add("label-center");
        orderPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        orderPane.vvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.doubleValue() == 1.0) {
                    newInfo();
                }
            }
        });
        container.setSpacing(10);

    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: addChildren
    /// Description: Dynamically adds order entries to the scrollable container based on provided JSON data.
    /// Input: JSONArray of JSONObject representing order data.
    /// Output: Adds visual components to the UI container to represent each order.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void addChildren(JSONObject[] data) {
        int number = data.length;
        for (int i = 0; i < number; i++) {
            AnchorPane copy = new AnchorPane();
            int finalI = i;
            copy.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                try {

                    Stage stage = (Stage) GlobalVariable.navigation.getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(FxmlAll.orderDetail));
                    AnchorPane orderDetailPane = loader.load();
                    OrderDetail controller = loader.getController();
                    controller.initData(data[finalI].getString("orderNumber"));

                    Scene scene = stage.getScene();
                    AnchorPane root = (AnchorPane) scene.getRoot();
                    root.getChildren().add(orderDetailPane);


                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            copy.setPrefSize(order.getPrefWidth(), order.getPrefHeight());
            copy.setStyle(order.getStyle());
            for (Node child : order.getChildren()) {
                Node childCopy = copyNode(child);
                copy.getChildren().add(childCopy);
            }
            copy.setVisible(true);
            Label createdAt = (Label) copy.getChildren().get(1);
            Label roomNumber = (Label) copy.getChildren().get(0);
            Label status = (Label) copy.getChildren().get(2);
            Label name = (Label) copy.getChildren().get(3);
            Label orderNumber = (Label) copy.getChildren().get(4);
            createdAt.setText(createdAt.getText() + data[i].getString("createdAt"));
            roomNumber.setText(roomNumber.getText() + data[i].getString("roomNumber"));
            status.setText(status.getText() + data[i].getString("status"));
            name.setText(name.getText() + data[i].getString("customerName"));
            orderNumber.setText(orderNumber.getText() + data[i].getString("orderNumber"));
            container.getChildren().add(copy);
        }


    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: copyNode
    /// Description: Creates a copy of a JavaFX Node, preserving its style and properties.
    /// Input: Node - the node to be copied.
    /// Output: None
    /// Returns: Node - a new node which is a copy of the input node.
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
        }
        return null;
    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: searchBtnClicked
    /// Description: Handles the search operation, refreshing the container with new order information based on the search text.
    /// Input: None
    /// Output: Clears and repopulates the container based on search criteria.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void searchBtnClicked() {
        container.getChildren().removeAll(container.getChildren());

        GlobalVariable.page = 0;
        newInfo();
    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: newInfo
    /// Description: Loads new order information from the server and updates the UI accordingly.
    /// Input: None
    /// Output: Requests new data from the server and updates the UI to display new orders.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void newInfo(){
        String response = null;
        JSONArray jsonArray = new JSONArray();
        JSONObject[] data = new JSONObject[0];
        String[] variable={
                "orderNumber=",
                "roomNumber=",
                "username=",
                "customerName=",
                "customerEmail=",
                "status=",
                "checkInDate=",
                "checkOutDate="
        };
        String url;
        if(searchText.getText().isEmpty()){
            response = GlobalVariable.get(GlobalVariable.url + "order/list?orderNumber=&&roomNumber=&&username=&&customerName=&&customerEmail=&&status=&&checkInDate=&&checkOutDate=&&page=" + GlobalVariable.page, true);
            jsonArray = new JSONArray(response);
            data = new JSONObject[jsonArray.length()];
        }else{
            for(int i = 0; i < 8; ++i){
                url = GlobalVariable.url+"order/list?";
                for(int j = 0; j < 8; ++j) {
                    if(i != j)
                        url = url+variable[j]+"&&";
                    else {
                        url = url + variable[j];
                        url = url + searchText.getText()+"&&";
                    }
                }
                response = GlobalVariable.get(url+"page=" + GlobalVariable.page, true);
                try {
                    jsonArray = new JSONArray(response);
                    if(jsonArray.length() != 0){
                        data = new JSONObject[jsonArray.length()];
                        break;
                    }
                }catch (Exception e){
                    jsonArray = new JSONArray();
                    data = new JSONObject[0];
                }
            }
        }
        ++GlobalVariable.page;
        for (int i = 0; i < jsonArray.length(); i++) {
            data[i] = jsonArray.getJSONObject(i);
        }
        addChildren(data);
    }

}
