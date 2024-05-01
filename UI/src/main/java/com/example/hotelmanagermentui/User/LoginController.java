package com.example.hotelmanagermentui.User;


import com.example.hotelmanagermentui.FxmlAll;
import com.example.hotelmanagermentui.GlobalVariable;
import com.example.hotelmanagermentui.IconController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.json.JSONObject;

public class LoginController {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button register;
    @FXML
    private Label errorMessage;


    public void usernameClicked() {
        username.setStyle("-fx-background-color: #0D1117; -fx-border-color: #1F6FEB;-fx-text-fill: white");
        password.setStyle("-fx-background-color: #0D1117; -fx-border-color: #30363D; -fx-text-fill: white");
    }

    public void passwordClicked() {
        password.setStyle("-fx-background-color: #0D1117; -fx-border-color: #1F6FEB;-fx-text-fill: white");
        username.setStyle("-fx-background-color: #0D1117; -fx-border-color: #30363D; -fx-text-fill: white");
    }

    public void registerClicked() {

        IconController.getInstance().changeScene(FxmlAll.registerScene,false);
    }

    public void registerEntered() {
        register.setUnderline(true);
    }

    public void registerExited() {
        register.setUnderline(false);
    }

    public void signInClicked() {
        errorMessage.setText("");
        String usernameText = username.getText();
        String passwordText = password.getText();
        GlobalVariable.username = usernameText;

        String jsonInputString = new JSONObject()
                .put("username", usernameText)
                .put("password", passwordText)
                .toString();
        String response = GlobalVariable.post(jsonInputString, GlobalVariable.url + "auth/login");
        JSONObject json = new JSONObject(response);
        String token = json.isNull("token")?"":json.getString("token");
        String error = json.isNull("error")?"":json.getString("error");
        if (token.isEmpty() && error.isEmpty()) {
            GlobalVariable.alertAdd("Network Error!");
        }
        else {
            if (!token.isEmpty()) {
                GlobalVariable.token = token;
                GlobalVariable.write();
                UserController.getUserInformation(false);
            } else {
                errorMessage.setText("Username or password is incorrect");
                password.setText("");
            }
        }
    }
}
