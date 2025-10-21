package com.electricengineering.controllers;

import com.electricengineering.App;
import com.electricengineering.Database;
import com.electricengineering.models.User;
import com.electricengineering.utils.AlertUtils; // ← ДОБАВЬТЕ ЭТОТ ИМПОРТ
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class OhmController {
    @FXML private TextField voltageField;
    @FXML private TextField currentField;
    @FXML private TextField resistanceField;
    @FXML private ComboBox<String> voltageUnit;
    @FXML private ComboBox<String> currentUnit;
    @FXML private ComboBox<String> resistanceUnit;

    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = App.getCurrentUser();

        // Initialize unit selectors
        voltageUnit.getItems().addAll("V", "mV");
        currentUnit.getItems().addAll("A", "mA");
        resistanceUnit.getItems().addAll("Ω", "kΩ", "MΩ");

        voltageUnit.setValue("V");
        currentUnit.setValue("A");
        resistanceUnit.setValue("Ω");

        // Add listeners to auto-calculate when two fields are filled
        voltageField.textProperty().addListener((obs, oldVal, newVal) -> calculateIfReady());
        currentField.textProperty().addListener((obs, oldVal, newVal) -> calculateIfReady());
        resistanceField.textProperty().addListener((obs, oldVal, newVal) -> calculateIfReady());

        // Также добавляем слушатели для ComboBox
        voltageUnit.valueProperty().addListener((obs, oldVal, newVal) -> calculateIfReady());
        currentUnit.valueProperty().addListener((obs, oldVal, newVal) -> calculateIfReady());
        resistanceUnit.valueProperty().addListener((obs, oldVal, newVal) -> calculateIfReady());
    }

    private void calculateIfReady() {
        int filledFields = 0;
        if (!voltageField.getText().isEmpty()) filledFields++;
        if (!currentField.getText().isEmpty()) filledFields++;
        if (!resistanceField.getText().isEmpty()) filledFields++;

        if (filledFields == 2) {
            calculate();
        }
    }

    @FXML
    private void calculate() {
        try {
            double voltage = 0, current = 0, resistance = 0;
            int filledFields = 0;

            if (!voltageField.getText().isEmpty()) {
                voltage = Double.parseDouble(voltageField.getText());
                filledFields++;
            }

            if (!currentField.getText().isEmpty()) {
                current = Double.parseDouble(currentField.getText());
                filledFields++;
            }

            if (!resistanceField.getText().isEmpty()) {
                resistance = Double.parseDouble(resistanceField.getText());
                filledFields++;
            }

            if (filledFields != 2) {
                AlertUtils.showErrorAlert("Error", "Please enter exactly two values");
                return;
            }

            // Convert to base units (Volts, Amps, Ohms)
            double baseVoltage = convertToBaseVoltage(voltage, voltageUnit.getValue());
            double baseCurrent = convertToBaseCurrent(current, currentUnit.getValue());
            double baseResistance = convertToBaseResistance(resistance, resistanceUnit.getValue());

            double result;
            if (voltageField.getText().isEmpty()) {
                // Calculate voltage: V = I * R
                result = baseCurrent * baseResistance;
                voltageField.setText(formatResult(convertFromBaseVoltage(result, voltageUnit.getValue())));
            } else if (currentField.getText().isEmpty()) {
                // Calculate current: I = V / R
                if (baseResistance == 0) {
                    AlertUtils.showErrorAlert("Error", "Resistance cannot be zero");
                    return;
                }
                result = baseVoltage / baseResistance;
                currentField.setText(formatResult(convertFromBaseCurrent(result, currentUnit.getValue())));
            } else {
                // Calculate resistance: R = V / I
                if (baseCurrent == 0) {
                    AlertUtils.showErrorAlert("Error", "Current cannot be zero");
                    return;
                }
                result = baseVoltage / baseCurrent;
                resistanceField.setText(formatResult(convertFromBaseResistance(result, resistanceUnit.getValue())));
            }
        } catch (NumberFormatException e) {
            AlertUtils.showErrorAlert("Error", "Please enter valid numbers");
        }
    }

    private String formatResult(double value) {
        if (value == 0) return "0";
        if (Math.abs(value) < 0.001) {
            return String.format("%.6f", value);
        } else if (Math.abs(value) < 1) {
            return String.format("%.4f", value);
        } else if (Math.abs(value) < 1000) {
            return String.format("%.2f", value);
        } else {
            return String.format("%.2f", value);
        }
    }

    @FXML
    private void save() {
        try {
            double voltage = voltageField.getText().isEmpty() ? 0 : Double.parseDouble(voltageField.getText());
            double current = currentField.getText().isEmpty() ? 0 : Double.parseDouble(currentField.getText());
            double resistance = resistanceField.getText().isEmpty() ? 0 : Double.parseDouble(resistanceField.getText());

            // Convert to base units for storage
            voltage = convertToBaseVoltage(voltage, voltageUnit.getValue());
            current = convertToBaseCurrent(current, currentUnit.getValue());
            resistance = convertToBaseResistance(resistance, resistanceUnit.getValue());

            Database db = Database.getInstance();
            db.saveOhmResult(currentUser.getId(), voltage, current, resistance);

            AlertUtils.showInfoAlert("Success", "Calculation saved successfully");
        } catch (NumberFormatException e) {
            AlertUtils.showErrorAlert("Error", "Please enter valid numbers before saving");
        }
    }

    @FXML
    private void clear() {
        voltageField.clear();
        currentField.clear();
        resistanceField.clear();
        voltageUnit.setValue("V");
        currentUnit.setValue("A");
        resistanceUnit.setValue("Ω");
    }

    @FXML
    private void back() {
        App.loadScene("main.fxml", "Electric Engineering Calculator - Main Menu");
    }

    private double convertToBaseVoltage(double value, String unit) {
        return unit.equals("mV") ? value / 1000 : value;
    }

    private double convertFromBaseVoltage(double value, String unit) {
        return unit.equals("mV") ? value * 1000 : value;
    }

    private double convertToBaseCurrent(double value, String unit) {
        return unit.equals("mA") ? value / 1000 : value;
    }

    private double convertFromBaseCurrent(double value, String unit) {
        return unit.equals("mA") ? value * 1000 : value;
    }

    private double convertToBaseResistance(double value, String unit) {
        if (unit.equals("kΩ")) return value * 1000;
        if (unit.equals("MΩ")) return value * 1000000;
        return value;
    }

    private double convertFromBaseResistance(double value, String unit) {
        if (unit.equals("kΩ")) return value / 1000;
        if (unit.equals("MΩ")) return value / 1000000;
        return value;
    }
}