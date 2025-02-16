module com.example.productinventory {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires java.sql;

    opens com.example.productinventory to com.fasterxml.jackson.databind, javafx.fxml;
    exports com.example.productinventory;
}