module com.example.projektzeiterfassung {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.projektzeiterfassung to javafx.fxml;
    exports com.example.projektzeiterfassung;
}