package com.electricengineering.controllers;

import com.electricengineering.App;
import com.electricengineering.Database;
import com.electricengineering.models.OhmResult;
import com.electricengineering.models.DividerResult;
import com.electricengineering.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;

import java.time.format.DateTimeFormatter;

public class HistoryController {
    @FXML private TableView<OhmResult> ohmTable;
    @FXML private TableView<DividerResult> dividerTable;

    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = App.getCurrentUser();
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());

        loadOhmHistory(isAdmin);
        loadDividerHistory(isAdmin);
    }

    private void loadOhmHistory(boolean isAdmin) {
        Database db = Database.getInstance();
        ObservableList<OhmResult> ohmResults = FXCollections.observableArrayList(db.getOhmResults(currentUser.getId(), isAdmin));

        // Составьте таблицу по закону Ома
        TableColumn<OhmResult, Double> voltageCol = new TableColumn<>("Voltage (V)");
        voltageCol.setCellValueFactory(new PropertyValueFactory<>("voltage"));

        TableColumn<OhmResult, Double> currentCol = new TableColumn<>("Current (A)");
        currentCol.setCellValueFactory(new PropertyValueFactory<>("current"));

        TableColumn<OhmResult, Double> resistanceCol = new TableColumn<>("Resistance (Ω)");
        resistanceCol.setCellValueFactory(new PropertyValueFactory<>("resistance"));

        TableColumn<OhmResult, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return new SimpleStringProperty(cellData.getValue().getCreatedAt().format(formatter));
        });

        ohmTable.getColumns().setAll(voltageCol, currentCol, resistanceCol, dateCol);
        ohmTable.setItems(ohmResults);
    }

    private void loadDividerHistory(boolean isAdmin) {
        Database db = Database.getInstance();
        ObservableList<DividerResult> dividerResults = FXCollections.observableArrayList(db.getDividerResults(currentUser.getId(), isAdmin));

        // Set up voltage divider table columns
        TableColumn<DividerResult, Double> vinCol = new TableColumn<>("Vin");
        vinCol.setCellValueFactory(new PropertyValueFactory<>("vin"));

        TableColumn<DividerResult, Double> voutCol = new TableColumn<>("Vout Required");
        voutCol.setCellValueFactory(new PropertyValueFactory<>("voutRequired"));

        TableColumn<DividerResult, Double> calculatedVoutCol = new TableColumn<>("Calculated Vout");
        calculatedVoutCol.setCellValueFactory(new PropertyValueFactory<>("calculatedVout"));

        TableColumn<DividerResult, Double> errorCol = new TableColumn<>("Error %");
        errorCol.setCellValueFactory(new PropertyValueFactory<>("errorPercentage"));

        TableColumn<DividerResult, Integer> countCol = new TableColumn<>("Resistors");
        countCol.setCellValueFactory(new PropertyValueFactory<>("resistorCount"));

        TableColumn<DividerResult, String> configCol = new TableColumn<>("Configuration");
        configCol.setCellValueFactory(new PropertyValueFactory<>("configuration"));

        TableColumn<DividerResult, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return new SimpleStringProperty(cellData.getValue().getCreatedAt().format(formatter));
        });

        dividerTable.getColumns().setAll(vinCol, voutCol, calculatedVoutCol, errorCol, countCol, configCol, dateCol);
        dividerTable.setItems(dividerResults);
    }

    @FXML
    private void back() {
        App.loadScene("main.fxml", "Инженерно-электрический калькулятор — главное меню");
    }
}