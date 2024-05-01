package com.example.hotelmanagermentui.User;


import com.example.hotelmanagermentui.FxmlAll;
import com.example.hotelmanagermentui.GlobalVariable;
import com.example.hotelmanagermentui.IconController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;
//
// Class: UserController
//
// Description:
// This class manages user interactions within the hotel management system. It handles user information display,
// updates, role management, and navigation through different parts of the user interface.
//
public class UserController implements Initializable {
    //   main scene
    //------------------------------------------------------------
    @FXML
    private Label username, realName, email, role, phone;
    @FXML
    private Button information, password;
    //    information scene
    //------------------------------------------------------------
    @FXML
    private AnchorPane inforPane;
    @FXML
    private Label inforUsername, inforRealName, inforEmail, inforPhone, inforPhoneError, inforEmailError, inforRealNameError;
    @FXML
    private TextField phoneText, emailText, realNameText;
    @FXML
    private Button inforBack, inforChangeBtn;
    //    password change scene
    //------------------------------------------------------------
    @FXML
    private AnchorPane passwordPane, oldPasswordPane, newPasswordPane;
    @FXML
    private Button passwordBack1, passwordBack2, logOutBtn;
    @FXML
    private TextField oldPasswordText, newPasswordText, passwordAgainText;
    @FXML
    private Label oldPasswordError, newPasswordError;
    //     search bar
    //------------------------------------------------------------
    @FXML
    private ComboBox roleChoice;
    @FXML
    private AnchorPane search;
    @FXML
    private TextField searchText;
    @FXML
    private Button searchBackBtn, searchModifyBtn, searchSearchBtn, deleteBtn;
    @FXML
    private Label searchUsername, searchRealName, searchEmail, searchRole, searchPhone;
    @FXML
    private ImageView searchBtn;
    /////////////////////////////////////////////////////////////////////////////
    /// Method: initialize
    /// Description: Sets initial values and configuration for the user interface elements when the controller is created.
    /// Input: URL location, ResourceBundle resources
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化代码，执行依赖于UI组件的操作
        setText();

    }
    /////////////////////////////////////////////////////////////////////////////
    /// Method: getUserInformation
    /// Description: Retrieves the current user's information from the server and updates the UI accordingly.
    /// Input: boolean aniNeeded - If true, animations will be used during the scene changes.
    /// Output: None
    /// Returns: boolean - Returns true if information retrieval is successful, false otherwise.
    /////////////////////////////////////////////////////////////////////////////
    public static boolean getUserInformation(boolean aniNeeded) {
        String response = GlobalVariable.get(GlobalVariable.url + "user/show?username=",true);
        JSONObject json = new JSONObject(response);

        String error = json.isNull("error") ? "" : json.getString("error");
        if (error.equals("Invalid token!")) {
            GlobalVariable.clear();
            IconController.getInstance().changeScene(FxmlAll.loginScene, aniNeeded);
            return false;
        } else if (!error.isEmpty()) {
            IconController.getInstance().changeScene(FxmlAll.userScene, aniNeeded);
            GlobalVariable.alertAdd("Network Error!");
            return false;
        } else {
            GlobalVariable.username = json.getString("username");
            GlobalVariable.realName = json.getString("realName");
            GlobalVariable.email = json.getString("email");
            GlobalVariable.role = json.getString("role");
            GlobalVariable.phoneNumber = json.getString("phoneNumber");
            GlobalVariable.write();
            IconController.getInstance().changeScene(FxmlAll.userScene, aniNeeded);
            return true;
        }


    }
    /////////////////////////////////////////////////////////////////////////////
    /// Method: setText
    /// Description: Sets the text of UI labels to the user's current information.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void setText() {
        username.setText(GlobalVariable.username);
        realName.setText(GlobalVariable.realName);
        email.setText(GlobalVariable.email);
        role.setText(GlobalVariable.role);
        phone.setText(GlobalVariable.phoneNumber);
        roleChoice.getItems().addAll("CUSTOMER", "STAFF", "ADMIN");

        roleChoice.setStyle("-fx-font-size: 16px;");

        inforBack.getStyleClass().add("button-hover");
        passwordBack1.getStyleClass().add("button-hover");
        passwordBack2.getStyleClass().add("button-hover");
        searchBackBtn.getStyleClass().add("button-hover");

        information.getStyleClass().add("button-hover-bgChange");
        password.getStyleClass().add("button-hover-bgChange");
        logOutBtn.getStyleClass().add("button-hover-bgChange");
        if (GlobalVariable.role.equals("CUSTOMER")) {
            searchBtn.setVisible(false);
        }

    }
    /////////////////////////////////////////////////////////////////////////////
    /// Method: changeInformation
    /// Description: Displays the user information editing pane and populates it with current user data.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void changeInformation() {
        inforPane.setVisible(true);
        inforUsername.setText(GlobalVariable.username);
        inforRealName.setText(GlobalVariable.realName);
        inforEmail.setText(GlobalVariable.email);
        inforPhone.setText(GlobalVariable.phoneNumber);
    }

    /////////////////////////////////////////////////////////////////////////////
    /// Method: logOut
    /// Description: Logs out the current user and clears all session data.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void logOut() {
        GlobalVariable.token = "";
        GlobalVariable.username = "";
        GlobalVariable.email = "";
        GlobalVariable.realName = "";
        GlobalVariable.phoneNumber = "";
        GlobalVariable.write();
        IconController.getInstance().changeScene(FxmlAll.loginScene, false);
    }
    /////////////////////////////////////////////////////////////////////////////
    /// Method: changePassword
    /// Description: Displays the pane for changing the user's password.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void changePassword() {
        passwordPane.setVisible(true);
    }



    /////////////////////////////////////////////////////////////////////////////
    /// Method: passwordBackClicked
    /// Description: Handles the back button click on the password change screen, resetting all fields and errors.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void passwordBackClicked() {
        passwordPane.setVisible(false);
        oldPasswordPane.setVisible(true);
        newPasswordPane.setVisible(false);
        oldPasswordText.setText("");
        newPasswordText.setText("");
        passwordAgainText.setText("");
        oldPasswordError.setText("");
        newPasswordError.setText("");

    }
    /////////////////////////////////////////////////////////////////////////////
    /// Method: oldPasswordConfirmClicked
    /// Description: Validates the old password with the server and, if correct, proceeds to the new password entry screen.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void oldPasswordConfirmClicked() {
        String jsonInputString = new JSONObject()
                .put("username", GlobalVariable.username)
                .put("password", oldPasswordText.getText())
                .toString();
        String response = GlobalVariable.post(jsonInputString, GlobalVariable.url + "auth/login");
        JSONObject json = new JSONObject(response);
        String token = json.isNull("token") ? "" : json.getString("token");
        String error = json.isNull("error") ? "" : json.getString("error");
        if (token.isEmpty() && error.isEmpty()) {
            GlobalVariable.alertAdd("Network Error!");
        } else {
            if (!token.isEmpty()) {
                oldPasswordPane.setVisible(false);
                newPasswordPane.setVisible(true);

            } else {
                oldPasswordError.setText("password is incorrect");
                oldPasswordText.setText("");
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////
    /// Method: newPasswordConfirmClicked
    /// Description: Confirms the new password, ensures it matches the repeated password entry, and updates the password via the server.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void newPasswordConfirmClicked() {
        if (newPasswordText.getText().equals(passwordAgainText.getText())) {
            //调用修改密码接口
            String jsonInputString = new JSONObject().put("newPassword", newPasswordText.getText())
                    .put("oldPassword", oldPasswordText.getText()).toString();
            String response = GlobalVariable.post(jsonInputString, GlobalVariable.url + "user/updatePassword");
            JSONObject json = new JSONObject(response);
            String newPassword = json.isNull("newPassword")?"":json.getString("newPassword");
            if(!newPassword.isEmpty()) {
                newPasswordError.setText(newPassword);
            }
            else{
                passwordBackClicked();
            }


        } else {
            newPasswordText.setText("");
            passwordAgainText.setText("");
            newPasswordError.setText("Passwords do not match");
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    /// Method: inforBackClicked
    /// Description: Handles the back button in the information update panel, resets all fields to the original values.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void inforBackClicked() {
        email.setText(GlobalVariable.email);
        phone.setText(GlobalVariable.phoneNumber);
        realName.setText(GlobalVariable.realName);
        inforPaneInit();
    }

    /////////////////////////////////////////////////////////////////////////////
    /// Method: inforChangeBtnClicked
    /// Description: Handles the toggle between modifying and confirming changes to user information.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void inforChangeBtnClicked() {
        if (inforChangeBtn.getText().equals("Modify")) {
            phoneText.setVisible(true);
            emailText.setVisible(true);
            realNameText.setVisible(true);
            phoneText.setText(inforPhone.getText());
            emailText.setText(inforEmail.getText());
            realNameText.setText(inforRealName.getText());

            inforEmail.setVisible(false);
            inforRealName.setVisible(false);
            inforChangeBtn.setText("Confirm");
        } else {
            String jsonInputString = new JSONObject().put("realName", realNameText.getText())
                    .put("phoneNumber", phoneText.getText())
                    .put("email", emailText.getText()).toString();
            String response = GlobalVariable.post(jsonInputString, GlobalVariable.url + "user/update");
            JSONObject json = new JSONObject(response);
            String realName = json.isNull("realName") ? "" : json.getString("realName");
            String email = json.isNull("email") ? "" : json.getString("email");
            String phoneNumber = json.isNull("phoneNumber") ? "" : json.getString("phoneNumber");
            if (!email.isEmpty() || !realName.isEmpty() || !phoneNumber.isEmpty()) {
                inforPhoneError.setText(phoneNumber);
                inforEmailError.setText(email);
                inforRealNameError.setText(realName);
            } else {
                GlobalVariable.phoneNumber = phoneText.getText();
                GlobalVariable.email = emailText.getText();
                GlobalVariable.realName = realNameText.getText();
                inforEmail.setVisible(true);
                inforRealName.setVisible(true);
                phoneText.setVisible(false);
                emailText.setVisible(false);
                realNameText.setVisible(false);
                inforPhone.setText(phoneText.getText());
                inforEmail.setText(emailText.getText());
                inforRealName.setText(realNameText.getText());
                inforChangeBtn.setText("Modify");
                inforPhoneError.setText("");
                inforEmailError.setText("");
                inforRealNameError.setText("");
            }
        }

    }
    /////////////////////////////////////////////////////////////////////////////
    /// Method: inforPaneInit
    /// Description: Resets the information pane to its initial state, hiding text fields and showing labels.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void inforPaneInit() {

        inforPane.setVisible(false);
        inforUsername.setVisible(true);
        inforRealName.setVisible(true);
        inforEmail.setVisible(true);
        phoneText.setVisible(false);
        emailText.setVisible(false);
        realNameText.setVisible(false);
    }

    /////////////////////////////////////////////////////////////////////////////
    /// Method: searchPaneShow
    /// Description: Displays the search pane and initializes its fields.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void searchPaneShow() {
        search.setVisible(true);

        searchText.setText("");
        searchUsername.setText("");
        searchRealName.setText("");
        searchEmail.setText("");
        searchPhone.setText("");
        searchRole.setText("");
        roleChoice.setVisible(false);
        searchModifyBtn.setText("Modify");
        searchSearchBtn.setVisible(true);
        searchModifyBtn.setVisible(false);
        deleteBtn.setVisible(false);
    }
    /////////////////////////////////////////////////////////////////////////////
    /// Method: searchBackBtnClicked
    /// Description: Hides the search pane and resets search fields to default.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void searchBackBtnClicked() {
        search.setVisible(false);
    }
    /////////////////////////////////////////////////////////////////////////////
    /// Method: searchSearchBtnClicked
    /// Description: Performs a search based on the entered username and updates the UI with user information or error messages.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void searchSearchBtnClicked() {
        String response = GlobalVariable.get(GlobalVariable.url + "user/search?username=" + searchText.getText(),true);
        JSONObject json = new JSONObject(response);
        String error = json.isNull("error") ? "" : json.getString("error");
        if (error.equals("Unknown Error")) {
            GlobalVariable.alertAdd("Network Error!");
        } else if (!error.isEmpty()) {
            GlobalVariable.alertAdd("User Not Found!");
            searchUsername.setText("");
            searchRealName.setText("");
            searchEmail.setText("");
            searchRole.setText("");
            searchModifyBtn.setVisible(false);
            deleteBtn.setVisible(false);

        } else {
            searchUsername.setText(json.getString("username"));
            searchRealName.setText(json.getString("realName"));
            searchEmail.setText(json.getString("email"));
            searchRole.setText(json.getString("role"));
            roleChoice.setValue(json.getString("role"));
            searchPhone.setText(json.getString("phoneNumber"));
            if (GlobalVariable.role.equals("ADMIN"))
                searchModifyBtn.setVisible(true);
            deleteBtn.setVisible(true);

        }
    }
    /////////////////////////////////////////////////////////////////////////////
    /// Method: searchModifyBtnClicked
    /// Description: Toggles between modifying and confirming changes to the user's role.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void searchModifyBtnClicked() {
        if (searchModifyBtn.getText().equals("Modify")) {
            deleteBtn.setVisible(false);
            roleChoice.setVisible(true);
            searchSearchBtn.setVisible(false);
            searchModifyBtn.setText("Confirm");
        } else {
            roleChoice.setVisible(false);
            if (GlobalVariable.role.equals("ADMIN"))
                deleteBtn.setVisible(true);
            searchSearchBtn.setVisible(true);
            searchRole.setText(roleChoice.getValue().toString());
            //
            String jsonInputString = new JSONObject().put("username", searchUsername.getText())
                    .put("role", roleChoice.getValue().toString()).toString();
            String response = GlobalVariable.post(jsonInputString, GlobalVariable.url + "user/roleUpdate");
            JSONObject json = new JSONObject(response);
            String msg = json.isNull("message") ? "" : json.getString("message");
            if (msg == null) {
                GlobalVariable.alertAdd("Network Error!");
                searchRole.setText(GlobalVariable.role);
            }
            searchModifyBtn.setText("Modify");
        }
    }
    /////////////////////////////////////////////////////////////////////////////
    /// Method: DeleteBtnClicked
    /// Description: Prompts for confirmation before deleting a user and handles the deletion process.
    /// Input: None
    /// Output: None
    /// Returns: void
    /////////////////////////////////////////////////////////////////////////////
    public void DeleteBtnClicked() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation dialog");
        alert.setHeaderText("Delete or not?");
        alert.setContentText("Make your choice");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String jsonInputString = new JSONObject().toString();
                String res = GlobalVariable.post(jsonInputString, GlobalVariable.url + "user/delete?username=" + searchUsername.getText());
                JSONObject json = new JSONObject(res);
                String error = json.isNull("error")?"":json.getString("error");
                String message = json.isNull("message")?"":json.getString("message");
                if(!error.isEmpty()){
                    GlobalVariable.alertAdd("User Not Found!");
                }
                else if(!message.isEmpty()){
                    GlobalVariable.alertAdd("User deleted successfully!");
                    searchPaneShow();
                }else{
                    GlobalVariable.alertAdd("Network Error!");
                }
            }
        });
    }


}
