package com.electricengineering.controllers;

import com.electricengineering.App;
import com.electricengineering.Database;
import com.electricengineering.models.User;
import com.electricengineering.utils.AlertUtils; // ← ДОБАВЬТЕ ЭТОТ ИМПОРТ
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            AlertUtils.showErrorAlert("Error", "Пожалуйста, введите имя пользователя и пароль");
            return;
        }

        Database db = Database.getInstance();
        User user = db.authenticateUser(username, password);

        if (user != null) {
            App.setCurrentUser(user);
            App.loadScene("main.fxml", "Инженерно-электрический калькулятор — главное меню");
        } else {
            AlertUtils.showErrorAlert("Error", "Неверное имя пользователя или пароль");
        }
    }

    @FXML
    private void handleRegisterRedirect() {
        App.loadScene("register.fxml", "Электротехнический калькулятор - Регистрация");
    }
}