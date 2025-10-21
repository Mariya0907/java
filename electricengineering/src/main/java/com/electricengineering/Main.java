package com.electricengineering;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // تعيين primaryStage في App
            App.setPrimaryStage(primaryStage);

            // تحميل واجهة المستخدم
            Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
            primaryStage.setTitle("Electric Engineering Calculator - Login");
            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}