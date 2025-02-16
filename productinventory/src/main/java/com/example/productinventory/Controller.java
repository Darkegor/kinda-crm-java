package com.example.productinventory;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class Controller {

    @FXML
    private TextField productNameField;

    @FXML
    private TextField productPriceField;

    @FXML
    private TextField productQuantityField;

    private final String serverUrl = "http://localhost:8080/api/products";

    @FXML
    private void handleAddProduct() {
        String name = productNameField.getText();
        String priceText = productPriceField.getText();
        String quantityText = productQuantityField.getText();

        if (name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
            showAlert("Error", "All fields must be filled!");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            boolean success = sendProductToServer(name, price, quantity);

            if (success) {
                showAlert("Success", "Product added successfully!");
                productNameField.clear();
                productPriceField.clear();
                productQuantityField.clear();
            } else {
                showAlert("Error", "Failed to add product to the server.");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Price and Quantity must be valid numbers!");
        }
    }

    private boolean sendProductToServer(String name, double price, int quantity) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String json = String.format("{\"name\": \"%s\", \"price\": %.2f, \"quantity\": %d}", name, price, quantity);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 201;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
