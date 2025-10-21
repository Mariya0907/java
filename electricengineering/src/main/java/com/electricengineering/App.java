package com.electricengineering;

import com.electricengineering.models.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App {
    private static User currentUser;
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void loadScene(String fxmlFile, String title) {
        try {
            if (primaryStage == null) {
                System.err.println("Error: primaryStage is null. Cannot load scene: " + fxmlFile);
                return;
            }

            Parent root = FXMLLoader.load(App.class.getResource("/views/" + fxmlFile));
            Scene scene = new Scene(root);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Error loading FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }
}