package com.electricengineering.controllers;

import com.electricengineering.App;
import com.electricengineering.models.User;
import javafx.fxml.FXML;

public class MainController {
    @FXML
    private void handleOhmCalculator() {
        App.loadScene("ohm.fxml", "Electric Engineering Calculator - Ohm's Law");
    }

    @FXML
    private void handleDividerCalculator() {
        App.loadScene("divider.fxml", "Electric Engineering Calculator - Voltage Divider");
    }

    @FXML
    private void handleHistory() {
        App.loadScene("history.fxml", "Electric Engineering Calculator - History");
    }

    @FXML
    private void handleLogout() {
        App.setCurrentUser(null);
        App.loadScene("login.fxml", "Electric Engineering Calculator - Login");
    }
}