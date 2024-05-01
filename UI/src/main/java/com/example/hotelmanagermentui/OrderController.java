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

public class OrderController implements Initializable {
    @FXML
    private VBox container;
    @FXML
    private AnchorPane order;
    @FXML
    private ScrollPane orderPane;

    @FXML
    private Label roomNumber;
    @FXML
    private Label checkInDate;
    @FXML
    private Label checkOutDate;
    @FXML
    private Label status;
    @FXML
    private TextField searchText;

    public static OrderController instance;

    public static OrderController getInstance() {
        if (instance == null) instance = new OrderController();
        return instance;
    }


    public void initialize(URL location, ResourceBundle resources) {
        // 初始化代码，执行依赖于UI组件的操作
        init();
        GlobalVariable.page = 0;
        newInfo();

    }

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
        // 这里可以添加更多类型的检查和复制逻辑
        return null;
    }

    public void searchBtnClicked() {
        container.getChildren().removeAll(container.getChildren());

        GlobalVariable.page = 0;
        newInfo();
    }
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
