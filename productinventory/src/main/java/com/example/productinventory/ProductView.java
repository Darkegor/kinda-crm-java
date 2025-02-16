package com.example.productinventory;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

public class ProductView {

    @FXML
    private TextField productNameField;

    @FXML
    private TextField productPriceField;

    @FXML
    private TextField productQuantityField;

    @FXML
    private ListView<String> productListView;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String serverUrl = "http://localhost:8080/api/products";

    private Long selectedProductId = null;

    @FXML
    public void initialize() {
        loadProducts();

        productListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadProductDetails(newSelection);
            }
        });
    }

    @FXML
    public void addProduct() {
        String name = productNameField.getText();
        String priceText = productPriceField.getText();
        String quantityText = productQuantityField.getText();

        if (name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
            showError("All fields must be filled!");
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceText);
            int quantity = Integer.parseInt(quantityText);

            String json = String.format("{\"name\": \"%s\", \"price\": %s, \"quantity\": %d}", name, price, quantity);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 201) {
                            Platform.runLater(() -> {
                                clearFields();
                                loadProducts();
                            });
                        } else {
                            showError("Failed to add product: " + response.body());
                        }
                    })
                    .exceptionally(e -> {
                        e.printStackTrace();
                        showError("An error occurred while adding the product.");
                        return null;
                    });
        } catch (NumberFormatException e) {
            showError("Price and quantity must be valid numbers!");
        }
    }

    @FXML
    public void editProduct() {
        if (selectedProductId == null) {
            showError("Please select a product to edit.");
            return;
        }

        String name = productNameField.getText();
        String priceText = productPriceField.getText();
        String quantityText = productQuantityField.getText();

        if (name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
            showError("All fields must be filled!");
            return;
        }

        try {
            BigDecimal price = new BigDecimal(priceText);
            int quantity = Integer.parseInt(quantityText);

            String json = String.format("{\"id\": %d, \"name\": \"%s\", \"price\": %s, \"quantity\": %d}",
                    selectedProductId, name, price, quantity);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + "/" + selectedProductId))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            Platform.runLater(() -> {
                                clearFields();
                                loadProducts();
                            });
                        } else {
                            showError("Failed to edit product: " + response.body());
                        }
                    })
                    .exceptionally(e -> {
                        e.printStackTrace();
                        showError("An error occurred while editing the product.");
                        return null;
                    });
        } catch (NumberFormatException e) {
            showError("Price and quantity must be valid numbers!");
        }
    }

    public void loadProducts() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl))
                .header("Accept", "application/json")
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseProducts)
                .thenAccept(products -> Platform.runLater(() -> {
                    productListView.getItems().clear();
                    productListView.getItems().addAll(products);
                }))
                .exceptionally(e -> {
                    e.printStackTrace();
                    showError("An error occurred while loading products.");
                    return null;
                });
    }

    private void loadProductDetails(String productString) {
        try {
            String[] parts = productString.split(" - ");
            String name = parts[0];
            String price = parts[1].substring(1);
            String quantity = parts[2].split(" ")[0];

            selectedProductId = Long.parseLong(parts[0].split(": ")[1]); // Извлечение ID

            productNameField.setText(name);
            productPriceField.setText(price);
            productQuantityField.setText(quantity);
        } catch (Exception e) {
            showError("Failed to parse product details.");
            selectedProductId = null;
        }
    }

    private List<String> parseProducts(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Product> products = mapper.readValue(json, new TypeReference<List<Product>>() {});

            return products.stream()
                    .map(product -> String.format("ID: %d - %s - $%s - %d pcs",
                            product.getId(),
                            product.getName(),
                            product.getPrice().toPlainString(),
                            product.getQuantity()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of("Failed to load products");
        }
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(message);
            alert.show();
        });
    }

    private void clearFields() {
        productNameField.clear();
        productPriceField.clear();
        productQuantityField.clear();
        selectedProductId = null;
    }
}
