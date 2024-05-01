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
//
// Class: LoginController
//
// Description:
// Manages the login interface for the hotel management system.    ///
// This class handles user interactions with the login form,       ///
// including field validation, styling updates on interactions,    ///
// and submission of login credentials.
//
public class LoginController {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button register;
    @FXML
    private Label errorMessage;
    /////////////////////////////////////////////////////////////////////////////
    /// Method: usernameClicked
    /// Description: Applies styling to the username field when clicked.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void usernameClicked() {
        username.setStyle("-fx-background-color: #0D1117; -fx-border-color: #1F6FEB;-fx-text-fill: white");
        password.setStyle("-fx-background-color: #0D1117; -fx-border-color: #30363D; -fx-text-fill: white");
    }

    /////////////////////////////////////////////////////////////////////////////
    /// Method: passwordClicked
    /// Description: Applies styling to the password field when clicked.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void passwordClicked() {
        password.setStyle("-fx-background-color: #0D1117; -fx-border-color: #1F6FEB;-fx-text-fill: white");
        username.setStyle("-fx-background-color: #0D1117; -fx-border-color: #30363D; -fx-text-fill: white");
    }

    /////////////////////////////////////////////////////////////////////////////
    /// Method: registerClicked
    /// Description: Triggers the transition to the registration scene.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void registerClicked() {

        IconController.getInstance().changeScene(FxmlAll.registerScene, false);
    }

    /////////////////////////////////////////////////////////////////////////////
    /// Method: registerEntered
    /// Description: Applies an underline style to the register button when hovered.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void registerEntered() {
        register.setUnderline(true);
    }

    /////////////////////////////////////////////////////////////////////////////
    /// Method: registerExited
    /// Description: Removes the underline style from the register button when no longer hovered.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void registerExited() {
        register.setUnderline(false);
    }

    /////////////////////////////////////////////////////////////////////////////
    /// Method: signInClicked
    /// Description: Handles the login process, including validation of credentials and navigation based on the response.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
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
        String token = json.isNull("token") ? "" : json.getString("token");
        String error = json.isNull("error") ? "" : json.getString("error");
        if (token.isEmpty() && error.isEmpty()) {
            GlobalVariable.alertAdd("Network Error!");
        } else {
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
