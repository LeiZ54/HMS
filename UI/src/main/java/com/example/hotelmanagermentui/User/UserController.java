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

public class UserController implements Initializable {
    //   main scene
    @FXML
    private Label username;
    @FXML
    private Label realName;
    @FXML
    private Label email;
    @FXML
    private Label role;
    @FXML
    private Button information;
    @FXML
    private Button password;
    @FXML
    private Label phone;
    //    information scene
//    ------------------------------------------------------------
    @FXML
    private AnchorPane inforPane;
    @FXML
    private Label inforUsername;
    @FXML
    private Label inforRealName;
    @FXML
    private Label inforEmail;
    @FXML
    private Label inforPhone;

    @FXML
    private TextField phoneText;
    @FXML
    private TextField emailText;
    @FXML
    private TextField realNameText;
    @FXML
    private Button inforBack;
    @FXML
    private Button inforChangeBtn;
    @FXML
    private Label inforPhoneError;
    @FXML
    private Label inforEmailError;
    @FXML
    private Label inforRealNameError;


    //    password change scene
//    ----------------------------------------------
    @FXML
    private AnchorPane passwordPane;
    @FXML
    private AnchorPane oldPasswordPane;
    @FXML
    private AnchorPane newPasswordPane;
    @FXML
    private Button passwordBack1;
    @FXML
    private Button passwordBack2;
    @FXML
    private TextField oldPasswordText;
    @FXML
    private TextField newPasswordText;
    @FXML
    private TextField passwordAgainText;
    @FXML
    private Button logOutBtn;
    @FXML
    private Label oldPasswordError;
    @FXML
    private Label newPasswordError;

    //    搜索栏
//    ————————————————————————————————————————————————————————
    @FXML
    private ComboBox roleChoice;
    @FXML
    private AnchorPane search;
    @FXML
    private TextField searchText;

    @FXML
    private Button searchBackBtn;
    @FXML
    private Label searchUsername;
    @FXML
    private Label searchRealName;
    @FXML
    private Label searchEmail;
    @FXML
    private Label searchRole;
    @FXML
    private Label searchPhone;
    @FXML
    private ImageView searchBtn;
    @FXML
    private Button searchModifyBtn;
    @FXML
    private Button searchSearchBtn;
    @FXML
    private Button deleteBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化代码，执行依赖于UI组件的操作
        setText();

    }

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

    public void changeInformation() {
        inforPane.setVisible(true);
        inforUsername.setText(GlobalVariable.username);
        inforRealName.setText(GlobalVariable.realName);
        inforEmail.setText(GlobalVariable.email);
        inforPhone.setText(GlobalVariable.phoneNumber);
    }


    public void logOut() {
//        String res = GlobalVariable.get(GlobalVariable.url + "addData");
//        System.out.println(res);
        GlobalVariable.token = "";
        GlobalVariable.username = "";
        GlobalVariable.email = "";
        GlobalVariable.realName = "";
        GlobalVariable.phoneNumber = "";
        GlobalVariable.write();
        IconController.getInstance().changeScene(FxmlAll.loginScene, false);
    }

    public void changePassword() {
        passwordPane.setVisible(true);
    }


//    change password
    //-----------------------------------------------------------

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
    //  Change information
//    --------------------------------------------------------------


    public void inforBackClicked() {
        email.setText(GlobalVariable.email);
        phone.setText(GlobalVariable.phoneNumber);
        realName.setText(GlobalVariable.realName);
        inforPaneInit();
    }


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
//            调用修改信息接口
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

    public void inforPaneInit() {

        inforPane.setVisible(false);
        inforUsername.setVisible(true);
        inforRealName.setVisible(true);
        inforEmail.setVisible(true);
        phoneText.setVisible(false);
        emailText.setVisible(false);
        realNameText.setVisible(false);
    }

    //    搜索栏
//    --------------------------------------------------------------------------
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

    public void searchBackBtnClicked() {
        search.setVisible(false);
    }

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
            if (searchText.getText().equals(GlobalVariable.username)) {
                GlobalVariable.alertAdd("Can not find yourself!");
                return;
            }
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

    public void searchModifyBtnClicked() {
        if (searchModifyBtn.getText().equals("Modify")) {
            deleteBtn.setVisible(false);
            roleChoice.setVisible(true);
            searchSearchBtn.setVisible(false);
            searchModifyBtn.setText("Confirm");
        } else {
//            写入数据
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
