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
// Class: RegisterController
//
// Description:
// Manages the registration interface for the hotel management system. Handles user inputs,
// validations, and styling of form elements upon interaction.
//
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

    /////////////////////////////////////////////////////////////////////////////
    /// Method: usernameClicked
    /// Description: Applies styling to the username field when clicked to highlight it as the currently active field.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void usernameClicked() {
        changeOutlier(username);
    }

    /////////////////////////////////////////////////////////////////////////////
    /// Method: passwordClicked
    /// Description: Applies styling to the password field when clicked to highlight it as the currently active field.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void passwordClicked() {
        changeOutlier(password);
    }

    /////////////////////////////////////////////////////////////////////////////
    /// Method: emailClicked
    /// Description: Applies styling to the email field when clicked to highlight it as the currently active field.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void emailClicked() {
        changeOutlier(email);
    }

    /////////////////////////////////////////////////////////////////////////////
    /// Method: rePasswordClicked
    /// Description: Applies styling to the rePassword field when clicked to highlight it as the currently active field.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void rePasswordClicked() {
        changeOutlier(rePassword);
    }

    /////////////////////////////////////////////////////////////////////////////
    /// Method: realnameClicked
    /// Description: Applies styling to the realname field when clicked to highlight it as the currently active field.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void realnameClicked() {
        changeOutlier(realname);
    }

    /////////////////////////////////////////////////////////////////////////////
    /// Method: phoneNumberClicked
    /// Description: Applies styling to the phoneNumber field when clicked to highlight it as the currently active field.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void phoneNumberClicked() {
        changeOutlier(phoneNumber);
    }

    /////////////////////////////////////////////////////////////////////////////
    /// Method: changeOutlier
    /// Description: Resets the styling of all text fields to default and then applies a highlighted styling to the text field currently being interacted with.
    /// Input: TextField textField - the text field to be highlighted
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void changeOutlier(TextField textField) {
        username.setStyle("-fx-background-color: #0D1117; -fx-border-color: #30363D;-fx-text-fill: white");
        password.setStyle("-fx-background-color: #0D1117; -fx-border-color: #30363D; -fx-text-fill: white");
        email.setStyle("-fx-background-color: #0D1117; -fx-border-color: #30363D; -fx-text-fill: white");
        rePassword.setStyle("-fx-background-color: #0D1117; -fx-border-color: #30363D; -fx-text-fill: white");
        realname.setStyle("-fx-background-color: #0D1117; -fx-border-color: #30363D; -fx-text-fill: white");
        phoneNumber.setStyle("-fx-background-color: #0D1117; -fx-border-color: #30363D; -fx-text-fill: white");
        textField.setStyle("-fx-background-color: #0D1117; -fx-border-color: #1F6FEB;-fx-text-fill: white");
    }
    /////////////////////////////////////////////////////////////////////////////
    /// Method: backClicked
    /// Description: Handles the action of returning to the login scene.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void backClicked() {
        IconController.getInstance().changeScene(FxmlAll.loginScene,false);
    }


    /////////////////////////////////////////////////////////////////////////////
    /// Method: backEntered
    /// Description: Applies an underline style to the back button when hovered.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void backEntered() {
        back.setUnderline(true);
    }


    /////////////////////////////////////////////////////////////////////////////
    /// Method: backExited
    /// Description: Removes the underline style from the back button when no longer hovered.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void backExited() {
        back.setUnderline(false);
    }

    /////////////////////////////////////////////////////////////////////////////
    /// Method: registerClicked
    /// Description: Processes the registration of a new user by validating input fields and communicating with the server.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
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
