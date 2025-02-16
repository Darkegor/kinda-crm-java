package com.example.productinventory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseHelper {

    private static final String URL = "jdbc:mysql://localhost:3306/product_inventory";
    private static final String USER = "root";
    private static final String PASSWORD = "Negr1234";

    public static void addProduct(String name, double price, int quantity) {
        String query = "INSERT INTO products (name, price, quantity) VALUES (?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, price);
            preparedStatement.setInt(3, quantity);

            preparedStatement.executeUpdate();
            System.out.println("Product added successfully to the database.");
        } catch (SQLException e) {
            System.err.println("Error adding product to the database: " + e.getMessage());
        }
    }

    private static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
            throw e;
        }
    }

    public static void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                System.err.println("Error closing resource: " + e.getMessage());
            }
        }
    }
}
