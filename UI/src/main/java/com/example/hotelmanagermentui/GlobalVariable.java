package com.example.hotelmanagermentui;


import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GlobalVariable {
    public final static String url = "http://localhost:8090/";
    public static String username;
    public static String email;
    public static String realName;
    public static String phoneNumber;
    public static String token;
    public static AnchorPane navigation;
    public static int page;
    public static String role;
    public static int focus;
    public static int lastfocus;
    public static boolean inAni;

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
    public static void clear() {
        GlobalVariable.token = "";
        GlobalVariable.email = "";
        GlobalVariable.realName = "";
        GlobalVariable.username = "";
        GlobalVariable.role = "";
        GlobalVariable.phoneNumber = "";
        write();
    }

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

    public static String readData(BufferedReader br) throws IOException {
        StringBuilder response = new StringBuilder();
        String responseLine = "";
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
        return response.toString();
    }

    public static String get(String urlStr, boolean ifNeedToken) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if(ifNeedToken){
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
    public static void alertAdd(String error)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Alert");
        alert.setHeaderText(null);
        alert.setContentText(error);
        alert.showAndWait();
    }


}
