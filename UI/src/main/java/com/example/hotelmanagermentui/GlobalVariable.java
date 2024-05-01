package com.example.hotelmanagermentui;


import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

//
// Class: GlobalVariable
//
// Description:
// Holds global variables and utility methods used across the hotel management system.
//
public class GlobalVariable {
    public final static String url = "http://localhost:8090/";
    // User information variables
    public static String username;
    public static String email;
    public static String realName;
    public static String phoneNumber;
    public static String token;
    public static String role;
    // Navigation variables
    public static AnchorPane navigation;
    public static int page;
    public static int focus;
    public static int lastfocus;
    public static boolean inAni;

    ///////////////////////////////////////////////////////////////////////////
    /// Method: write
    /// Description: Serializes user information into a JSON object and writes it to a local file "UI/data.txt".
    /// Input: None
    /// Output: Writes data to "UI/data.txt"
    /// Returns: void
    ///////////////////////////////////////////////////////////////////////////
    public static void write() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", GlobalVariable.token);
        jsonObject.put("username", GlobalVariable.username);
        jsonObject.put("email", GlobalVariable.email);
        jsonObject.put("realName", GlobalVariable.realName);
        jsonObject.put("role", GlobalVariable.role);
        jsonObject.put("phoneNumber", GlobalVariable.phoneNumber);
        try (FileWriter file = new FileWriter("UI/data.txt")) {
            file.write(jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
/// Method: clear
/// Description: Resets all user information variables to empty strings and writes the cleared data to "UI/data.txt".
/// Input: None
/// Output: Updates "UI/data.txt" with cleared values.
/// Returns: void
///////////////////////////////////////////////////////////////////////////
    public static void clear() {
        GlobalVariable.token = "";
        GlobalVariable.email = "";
        GlobalVariable.realName = "";
        GlobalVariable.username = "";
        GlobalVariable.role = "";
        GlobalVariable.phoneNumber = "";
        write();
    }

    ///////////////////////////////////////////////////////////////////////////
/// Method: read
/// Description: Reads user information from "UI/data.txt" and updates global variables based on the content.
/// Input: None
/// Output: Updates global variables with data read from "UI/data.txt".
/// Returns: void
///////////////////////////////////////////////////////////////////////////
    public static void read() {
        try {
            File file = new File("UI/data.txt");
            Scanner scanner = new Scanner(file);
            StringBuilder jsonText = new StringBuilder();
            while (scanner.hasNextLine()) {
                jsonText.append(scanner.nextLine());
            }
            scanner.close();

            JSONObject jsonObject = new JSONObject(jsonText.toString());
            GlobalVariable.token = jsonObject.getString("token");
            GlobalVariable.username = jsonObject.getString("username");
            GlobalVariable.email = jsonObject.getString("email");
            GlobalVariable.realName = jsonObject.getString("realName");
            GlobalVariable.role = jsonObject.getString("role");
            GlobalVariable.phoneNumber = jsonObject.getString("phoneNumber");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
/// Method: post
/// Description: Sends a POST request to a specified URL with JSON input and returns the server response.
/// Input: JSON string to be sent and URL string where the request is made.
/// Output: Server response as a string.
/// Returns: String - Response from server.
///////////////////////////////////////////////////////////////////////////
    public static String post(String jsonInputString, String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + GlobalVariable.token);
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            // Check the response
            if (conn.getResponseCode() == 200) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    String res = readData(br);
                    conn.disconnect();
                    return res;
                }

            } else {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                    String res = readData(br);
                    conn.disconnect();
                    return res;
                }
            }
        } catch (Exception e) {
            return "{}";
        }
    }

    ///////////////////////////////////////////////////////////////////////////
/// Method: readData
/// Description: Reads data line by line from a BufferedReader and concatenates it into a single string.
/// Input: BufferedReader - a BufferedReader from which data is read.
/// Output: None
/// Returns: String - the concatenated data from the BufferedReader.
///////////////////////////////////////////////////////////////////////////
    public static String readData(BufferedReader br) throws IOException {
        StringBuilder response = new StringBuilder();
        String responseLine = "";
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
        return response.toString();
    }

    ///////////////////////////////////////////////////////////////////////////
/// Method: get
/// Description: Sends a GET request to a specified URL and returns the server response, optionally including a token in the request header.
/// Input: URL string where the request is made and a boolean indicating whether the token should be included.
/// Output: Server response as a string.
/// Returns: String - Response from server, or an error message if an exception occurs.
///////////////////////////////////////////////////////////////////////////
    public static String get(String urlStr, boolean ifNeedToken) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if (ifNeedToken) {
                conn.setRequestProperty("Authorization", "Bearer " + GlobalVariable.token);
            }
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = readData(br);
                br.close();
                return response;
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String response = readData(br);
                br.close();
                return response;
            }
        } catch (Exception e) {
            return "{error:\"Unknown Error\"}";
        }
    }

    ///////////////////////////////////////////////////////////////////////////
/// Method: alertAdd
/// Description: Displays an information alert with a specified message.
/// Input: String - message to be displayed in the alert.
/// Output: Displays an alert dialog.
/// Returns: void
///////////////////////////////////////////////////////////////////////////
    public static void alertAdd(String error) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Alert");
        alert.setHeaderText(null);
        alert.setContentText(error);
        alert.showAndWait();
    }


}
