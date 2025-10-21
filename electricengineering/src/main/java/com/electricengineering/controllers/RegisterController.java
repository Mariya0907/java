package com.electricengineering.controllers;

import com.electricengineering.App;
import com.electricengineering.Database;
import com.electricengineering.utils.AlertUtils; // ← ДОБАВЬТЕ ЭТОТ ИМПОРТ
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            AlertUtils.showErrorAlert("Error", "Пожалуйста, заполните все поля");
            return;
        }

        if (!password.equals(confirmPassword)) {
            AlertUtils.showErrorAlert("Error", "Пароли не совпадают");
            return;
        }

        if (password.length() < 6) {
            AlertUtils.showErrorAlert("Error", "Пароль должен состоять как минимум из 6 символов");
            return;
        }

        Database db = Database.getInstance();
        if (db.userExists(username)) {
            AlertUtils.showErrorAlert("Error", "Имя пользователя уже существует");
            return;
        }

        if (db.registerUser(username, password, "USER")) {
            AlertUtils.showInfoAlert("Success", "Регистрация прошла успешно. Пожалуйста, войдите в систему.");
            App.loadScene("login.fxml", "Electric Engineering Calculator - Login");
        } else {
            AlertUtils.showErrorAlert("Error", "Не удалось выполнить регистрацию");
        }
    }

    @FXML
    private void handleLoginRedirect() {
        App.loadScene("login.fxml", "Electric Engineering Calculator - Login");
    }
}