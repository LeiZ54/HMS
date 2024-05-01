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
///////////////////////////////////////////////////////////////////////////
/// Class: OrderDetail
/// Description: Manages the detailed view of an order, including actions such as modify, cancel, or update the status of an order.
///////////////////////////////////////////////////////////////////////////
public class OrderDetail implements Initializable {
    public String orderNumberIn;
    @FXML
    private Button modify, backBtn, cancel, statusBtn;
    @FXML
    private TextField customerNameText, customerEmailText;
    @FXML
    private Label customerName, customerEmail, status, roomNum, checkInDate, checkOutDate, orderNumber, createdAt, username;

    ///////////////////////////////////////////////////////////////////////////
    /// Method: initialize
    /// Description: Sets initial styles and configuration for UI elements in the order detail view.
    /// Input: URL - the location used to resolve relative paths for the root object, ResourceBundle - the resources used to localize the root object.
    /// Output: Applies initial CSS styles to the back button.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void initialize(URL location, ResourceBundle resources) {
        backBtn.getStyleClass().add("button-hover");
        backBtn.getStyleClass().add("button-hover:hover");
    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: initData
    /// Description: Initializes the order detail view with specific data.
    /// Input: String - order number to fetch details for.
    /// Output: Calls showDetail() to populate UI with data.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void initData(String data) {
        orderNumberIn = data;
        showDetail();
    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: showDetail
    /// Description: Fetches and displays details for a specific order.
    /// Input: None
    /// Output: Populates the order detail fields with information retrieved from the server.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
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
    ///////////////////////////////////////////////////////////////////////////
    /// Method: backBtnClicked
    /// Description: Handles the action to navigate back to the previous scene.
    /// Input: None
    /// Output: Removes the current detail view and returns to the order list scene.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void backBtnClicked() {
        Stage stage = (Stage) GlobalVariable.navigation.getScene().getWindow();
        Scene scene = stage.getScene();
        AnchorPane root = (AnchorPane) scene.getRoot();
        root.getChildren().remove(root.getChildren().get(2));
        IconController.getInstance().changeScene(FxmlAll.orderScene, false);

    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: modifyBtnClicked
    /// Description: Allows modification of the order details or confirms the changes.
    /// Input: None
    /// Output: Toggles between modify and confirm modes for order detail changes.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
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
    ///////////////////////////////////////////////////////////////////////////
    /// Method: cancelBtnClicked
    /// Description: Cancels the order and updates its status on the server and UI.
    /// Input: None
    /// Output: Changes the order status to "CANCELED" and updates the UI accordingly.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public void cancelBtnClicked() {
        GlobalVariable.post("", GlobalVariable.url + "order/cancel?orderNumber=" + orderNumberIn);

        status.setText("CANCELED");
        cancel.setVisible(false);
        modify.setVisible(false);
        statusBtn.setVisible(false);
    }
    ///////////////////////////////////////////////////////////////////////////
    /// Method: statusBtnClicked
    /// Description: Updates the status of the order (e.g., check-in, check-out) based on its current status.
    /// Input: None
    /// Output: Changes the order status and updates the UI and server status accordingly.
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
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

