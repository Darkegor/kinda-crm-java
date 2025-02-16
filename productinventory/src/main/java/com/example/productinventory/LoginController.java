package com.example.productinventory;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField registerUsernameField;

    @FXML
    private PasswordField registerPasswordField;

    private Runnable onLoginSuccess;

    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Username and password cannot be empty.");
            return;
        }

        try {
            URL url = new URL("http://localhost:8080/api/auth/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonInput = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonInput.getBytes());
                os.flush();
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                if (onLoginSuccess != null) {
                    onLoginSuccess.run();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Login Error", "An error occurred while logging in.");
        }
    }

    @FXML
    private void handleRegisterButtonAction() {
        String newUsername = registerUsernameField.getText();
        String newPassword = registerPasswordField.getText();

        if (newUsername.isEmpty() || newPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Username and password cannot be empty.");
            return;
        }

        try {
            URL url = new URL("http://localhost:8080/api/auth/register");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonInput = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", newUsername, newPassword);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonInput.getBytes());
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Registration completed. You can now log in.");
                registerUsernameField.clear();
                registerPasswordField.clear();
            } else if (responseCode == HttpURLConnection.HTTP_CONFLICT) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username is already taken.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Error", "Something went wrong during registration.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Registration Error", "An error occurred while registering.");
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
