package com.example.hotelmanagermentui;


import javafx.fxml.FXML;

import javafx.fxml.Initializable;

import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import javafx.stage.Stage;
import org.json.JSONObject;


import java.net.URL;
import java.util.ResourceBundle;

public class OrderDetail implements Initializable {
    @FXML
    public AnchorPane orderDetailPane;

    public static OrderDetail instance = null;
    public String orderNumberIn;
    @FXML
    private Button modify;
    @FXML
    private TextField customerNameText;
    @FXML
    private TextField customerEmailText;
    @FXML
    private Button backBtn;
    @FXML
    private Label customerName;
    @FXML
    private Label customerEmail;
    @FXML
    private Label status;
    @FXML
    private Button cancel;
    @FXML
    private Label roomNum;

    @FXML
    private Label checkInDate;
    @FXML
    private Label checkOutDate;

    @FXML
    private Label orderNumber;
    @FXML
    private Label createdAt;
    @FXML
    private Label username;
    @FXML
    private Button statusBtn;

    public static OrderDetail getInstance() {
        if (instance == null) {
            instance = new OrderDetail();
        }
        return instance;
    }

    public void initialize(URL location, ResourceBundle resources) {
        // 初始化代码，执行依赖于UI组件的操作

        backBtn.getStyleClass().add("button-hover");
        backBtn.getStyleClass().add("button-hover:hover");
    }

    public void initData(String data) {
        orderNumberIn = data;
        showDetail();
    }

    public void showDetail() {

        String response = GlobalVariable.get(GlobalVariable.url + "order/show?orderNumber=" + orderNumberIn, true);
        JSONObject json = new JSONObject(response);

        roomNum.setText(json.getString("roomNumber"));
        customerName.setText(json.getString("customerName"));
        customerEmail.setText(json.getString("customerEmail"));
        checkInDate.setText(json.getString("checkInDate"));
        checkOutDate.setText(json.getString("checkOutDate"));
        status.setText(json.getString("status"));
        orderNumber.setText(json.getString("orderNumber"));
        createdAt.setText(json.getString("createdAt"));
        username.setText(json.getString("username"));
        if (!GlobalVariable.role.equals("CUSTOMER")) {
            if (status.getText().equals("CREATED"))
                statusBtn.setText("Check in");
            else if (status.getText().equals("CHECKED"))
                statusBtn.setText("Check out");
            statusBtn.setVisible(true);
        }
        if (status.getText().equals("CANCELED") || status.getText().equals("FINISHED")) {
            cancel.setVisible(false);
            statusBtn.setVisible(false);
            modify.setVisible(false);
        }


    }

    //    roomNum CustomerName customerEmail checkInDate  checkOutDate Status orderNum createdAt
    public void backBtnClicked() {
        Stage stage = (Stage) GlobalVariable.navigation.getScene().getWindow();
        Scene scene = stage.getScene();
        AnchorPane root = (AnchorPane) scene.getRoot();
        root.getChildren().remove(root.getChildren().get(2));
        IconController.getInstance().changeScene(FxmlAll.orderScene, false);

    }

    public void modifyBtnClicked() {
        if (modify.getText().equals("Modify")) {

            customerNameText.setVisible(true);
            customerEmailText.setVisible(true);
            cancel.setVisible(false);
            statusBtn.setVisible(false);
            modify.setText("Confirm");
        } else if (modify.getText().equals("Confirm")) {
            String jsonInputString = new JSONObject().put("orderNumber", orderNumber.getText())
                    .put("customerName", customerNameText.getText())
                    .put("customerEmail", customerEmailText.getText()).toString();
            GlobalVariable.post(jsonInputString, GlobalVariable.url + "order/update");


            customerName.setText(customerNameText.getText());
            customerEmail.setText(customerEmailText.getText());
            customerNameText.setVisible(false);
            cancel.setVisible(true);
            customerEmailText.setVisible(false);
            if (!GlobalVariable.role.equals("CUSTOMER")) {
                statusBtn.setVisible(true);
            }
            modify.setText("Modify");
        }
    }

    public void cancelBtnClicked() {
        GlobalVariable.post("", GlobalVariable.url + "order/cancel?orderNumber=" + orderNumberIn);

        status.setText("CANCELED");
        cancel.setVisible(false);
        modify.setVisible(false);
        statusBtn.setVisible(false);
    }

    public void statusBtnClicked() {
        if (status.getText().equals("CREATED")) {
            GlobalVariable.post("", GlobalVariable.url + "order/checkin?orderNumber=" + orderNumberIn);
            status.setText("CHECKED");
            statusBtn.setText("Check out");
        } else if (status.getText().equals("CHECKED")) {
            status.setText("FINISHED");
            GlobalVariable.post("", GlobalVariable.url + "order/checkout?orderNumber=" + orderNumberIn);
            cancel.setVisible(false);
            statusBtn.setVisible(false);
            modify.setVisible(false);
        }

    }

}

