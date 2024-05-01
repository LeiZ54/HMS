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

public class RegisterController {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private TextField email;
    @FXML
    private PasswordField rePassword;
    @FXML
    private TextField realname;
    @FXML
    private Button back;
    @FXML
    private Label usernameError;
    @FXML
    private Label passwordError;
    @FXML
    private Label rePasswordError;
    @FXML
    private Label emailError;
    @FXML
    private Label realNameError;
    @FXML
    private TextField phoneNumber;
    @FXML
    private Label phoneNumberError;

    public void usernameClicked() {
        changeOutlier(username);
    }

    public void passwordClicked() {
        changeOutlier(password);
    }

    public void emailClicked() {
        changeOutlier(email);
    }

    public void rePasswordClicked() {
        changeOutlier(rePassword);
    }

    public void realnameClicked() {
        changeOutlier(realname);
    }

    public void phoneNumberClicked() {
        changeOutlier(phoneNumber);
    }

    public void changeOutlier(TextField textField) {
        username.setStyle("-fx-background-color: #0D1117; -fx-border-color: #30363D;-fx-text-fill: white");
        password.setStyle("-fx-background-color: #0D1117; -fx-border-color: #30363D; -fx-text-fill: white");
        email.setStyle("-fx-background-color: #0D1117; -fx-border-color: #30363D; -fx-text-fill: white");
        rePassword.setStyle("-fx-background-color: #0D1117; -fx-border-color: #30363D; -fx-text-fill: white");
        realname.setStyle("-fx-background-color: #0D1117; -fx-border-color: #30363D; -fx-text-fill: white");
        phoneNumber.setStyle("-fx-background-color: #0D1117; -fx-border-color: #30363D; -fx-text-fill: white");
        textField.setStyle("-fx-background-color: #0D1117; -fx-border-color: #1F6FEB;-fx-text-fill: white");
    }

    public void backClicked() {
        IconController.getInstance().changeScene(FxmlAll.loginScene,false);
    }

    public void backEntered() {
        back.setUnderline(true);
    }

    public void backExited() {
        back.setUnderline(false);
    }

    public void registerClicked() {

        if(!password.getText().equals(rePassword.getText())) {
            rePasswordError.setText("The password do not match");
            usernameError.setText("");
            passwordError.setText("");
            emailError.setText("");
            realNameError.setText("");
            phoneNumberError.setText("");
            return;
        }
        rePasswordError.setText("");

        String jsonInputString = new JSONObject().put("username", username.getText())
                .put("password", password.getText())
                .put("email", email.getText())
                .put("realName", realname.getText())
                .put("phoneNumber", phoneNumber.getText()).toString();
        String response = GlobalVariable.post(jsonInputString, GlobalVariable.url + "auth/register");
        JSONObject json = new JSONObject(response);
        String token = json.isNull("token")?"":json.getString("token");
        String username = json.isNull("username")?"":json.getString("username");
        String realName = json.isNull("realName")?"":json.getString("realName");
        String email = json.isNull("email")?"":json.getString("email");
        String password = json.isNull("password")?"":json.getString("password");
        String phoneNumber = json.isNull("phoneNumber")?"":json.getString("phoneNumber");
        if(username.isEmpty()){
            username = json.isNull("error")?"":json.getString("error");
        }

        if(!token.isEmpty()){
            GlobalVariable.token = token;
            GlobalVariable.username = username;
            GlobalVariable.realName = realName;
            GlobalVariable.email = email;
            GlobalVariable.phoneNumber = phoneNumber;
            GlobalVariable.write();
            UserController.getUserInformation(false);
        }
        else if(!username.isEmpty() || !password.isEmpty() || !email.isEmpty() || !realName.isEmpty() || !phoneNumber.isEmpty()){
            usernameError.setText(username);
            passwordError.setText(password);
            emailError.setText(email);
            realNameError.setText(realName);
            phoneNumberError.setText(phoneNumber);
        }
        else{
            GlobalVariable.alertAdd("Network Error!");
        }
    }

}
