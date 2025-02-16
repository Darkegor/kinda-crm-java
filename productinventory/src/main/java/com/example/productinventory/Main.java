package com.example.productinventory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/com/example/productinventory/fxml/Login.fxml"));
        Parent loginRoot = loginLoader.load();

        Scene loginScene = new Scene(loginRoot, 300, 200);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Login");

        LoginController loginController = loginLoader.getController();

        loginController.setOnLoginSuccess(() -> {
            try {
                FXMLLoader productLoader = new FXMLLoader(getClass().getResource("/com/example/productinventory/fxml/productView.fxml"));
                Parent productRoot = productLoader.load();

                ProductView productController = productLoader.getController();
                productController.loadProducts();

                Scene productScene = new Scene(productRoot, 600, 400);
                primaryStage.setScene(productScene);
                primaryStage.setTitle("Product Inventory");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
