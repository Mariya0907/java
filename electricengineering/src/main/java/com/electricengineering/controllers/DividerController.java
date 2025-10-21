package com.electricengineering.controllers;

import com.electricengineering.App;
import com.electricengineering.Database;
import com.electricengineering.models.User;
import com.electricengineering.utils.AlertUtils; // ← ДОБАВЬТЕ ЭТОТ ИМПОРТ
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class DividerController {
    @FXML private TextField vinField;
    @FXML private TextField voutField;
    @FXML private TextField toleranceField;
    @FXML private ComboBox<String> resistorSeries;
    @FXML private TextField minResistanceField;
    @FXML private TextField maxResistanceField;
    @FXML private Pane circuitPane;

    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = App.getCurrentUser();

        // Инициализация параметров серии резисторов
        resistorSeries.getItems().addAll("E6", "E12", "E24", "E96");
        resistorSeries.setValue("E24");

        // Установите значения по умолчанию
        toleranceField.setText("1");
        minResistanceField.setText("100");
        maxResistanceField.setText("1000000");
    }

    @FXML
    private void calculate() {
        try {
            double vin = Double.parseDouble(vinField.getText());
            double voutRequired = Double.parseDouble(voutField.getText());
            double tolerance = Double.parseDouble(toleranceField.getText());
            String series = resistorSeries.getValue();
            double minResistance = Double.parseDouble(minResistanceField.getText());
            double maxResistance = Double.parseDouble(maxResistanceField.getText());

            if (vin <= 0 || voutRequired <= 0 || voutRequired >= vin) {
                AlertUtils.showErrorAlert("Error", "Vout must be between 0 and Vin");
                return;
            }

            if (tolerance <= 0 || tolerance > 100) {
                AlertUtils.showErrorAlert("Error", "Tolerance must be between 0 and 100");
                return;
            }

            if (minResistance <= 0 || maxResistance <= 0 || minResistance >= maxResistance) {
                AlertUtils.showErrorAlert("Error", "Min resistance must be less than max resistance");
                return;
            }

            // Генерируйте стандартные значения сопротивления
            List<Double> resistors = generateResistorValues(series, minResistance, maxResistance);

            // Найдите наилучшие комбинации
            List<DividerSolution> solutions = findBestDividers(vin, voutRequired, tolerance, resistors);

            if (solutions.isEmpty()) {
                AlertUtils.showInfoAlert("Result", "No suitable resistor combinations found");
                return;
            }

            // Покажите наилучшее решение
            DividerSolution bestSolution = solutions.get(0);
            drawCircuit(bestSolution);

            // Сохраните результат
            Database db = Database.getInstance();
            db.saveDividerResult(
                    currentUser.getId(), vin, voutRequired, tolerance, series,
                    minResistance, maxResistance, bestSolution.resistorCount,
                    bestSolution.configuration, bestSolution.resistors.toString(),
                    bestSolution.calculatedVout, bestSolution.errorPercentage
            );

            AlertUtils.showInfoAlert("Success",
                    String.format("Best solution found with error: %.2f%%", bestSolution.errorPercentage));

        } catch (NumberFormatException e) {
            AlertUtils.showErrorAlert("Error", "Please enter valid numbers");
        }
    }

    @FXML
    private void clear() {
        vinField.clear();
        voutField.clear();
        toleranceField.clear();
        minResistanceField.clear();
        maxResistanceField.clear();
        circuitPane.getChildren().clear();
    }

    @FXML
    private void back() {
        App.loadScene("main.fxml", "Electric Engineering Calculator - Main Menu");
    }

    private List<Double> generateResistorValues(String series, double min, double max) {
        List<Double> values = new ArrayList<>();
        double[] baseValues;

        switch (series) {
            case "E6": baseValues = new double[]{1.0, 1.5, 2.2, 3.3, 4.7, 6.8}; break;
            case "E12": baseValues = new double[]{1.0, 1.2, 1.5, 1.8, 2.2, 2.7, 3.3, 3.9, 4.7, 5.6, 6.8, 8.2}; break;
            case "E24": baseValues = new double[]{1.0, 1.1, 1.2, 1.3, 1.5, 1.6, 1.8, 2.0, 2.2, 2.4, 2.7, 3.0,
                    3.3, 3.6, 3.9, 4.3, 4.7, 5.1, 5.6, 6.2, 6.8, 7.5, 8.2, 9.1}; break;
            case "E96":
                // Simplified E96 series (actual E96 has 96 values)
                baseValues = new double[]{1.00, 1.02, 1.05, 1.07, 1.10, 1.13, 1.15, 1.18, 1.21, 1.24, 1.27, 1.30,
                        1.33, 1.37, 1.40, 1.43, 1.47, 1.50, 1.54, 1.58, 1.62, 1.65, 1.69, 1.74,
                        1.78, 1.82, 1.87, 1.91, 1.96, 2.00, 2.05, 2.10, 2.15, 2.21, 2.26, 2.32,
                        2.37, 2.43, 2.49, 2.55, 2.61, 2.67, 2.74, 2.80, 2.87, 2.94, 3.01, 3.09,
                        3.16, 3.24, 3.32, 3.40, 3.48, 3.57, 3.65, 3.74, 3.83, 3.92, 4.02, 4.12,
                        4.22, 4.32, 4.42, 4.53, 4.64, 4.75, 4.87, 4.99, 5.11, 5.23, 5.36, 5.49,
                        5.62, 5.76, 5.90, 6.04, 6.19, 6.34, 6.49, 6.65, 6.81, 6.98, 7.15, 7.32,
                        7.50, 7.68, 7.87, 8.06, 8.25, 8.45, 8.66, 8.87, 9.09, 9.31, 9.53, 9.76}; break;
            default: baseValues = new double[]{1.0, 1.5, 2.2, 3.3, 4.7, 6.8};
        }

        // Сгенерируйте значения, умножив базовые значения на степени 10
        for (double multiplier = 1; multiplier <= max; multiplier *= 10) {
            for (double base : baseValues) {
                double value = base * multiplier;
                if (value >= min && value <= max) {
                    values.add(value);
                }
            }
        }

        return values;
    }

    private List<DividerSolution> findBestDividers(double vin, double voutRequired, double tolerance, List<Double> resistors) {
        List<DividerSolution> solutions = new ArrayList<>();
        double requiredRatio = voutRequired / vin;

        // Проверьте простые делители из двух резисторов
        for (Double r1 : resistors) {
            for (Double r2 : resistors) {
                double ratio = r2 / (r1 + r2);
                double error = Math.abs(ratio - requiredRatio) / requiredRatio * 100;

                if (error <= tolerance) {
                    solutions.add(new DividerSolution(
                            2, "SERIAL", List.of(r1, r2),
                            ratio * vin, error
                    ));
                }
            }
        }

        // Проверьте комбинации из трёх резисторов (упрощённо)
        for (Double r1 : resistors) {
            for (Double r2 : resistors) {
                for (Double r3 : resistors) {
                    // Series combination: R1 + (R2 || R3)
                    double parallel = (r2 * r3) / (r2 + r3);
                    double total = r1 + parallel;
                    double ratio = parallel / total;
                    double error = Math.abs(ratio - requiredRatio) / requiredRatio * 100;

                    if (error <= tolerance) {
                        solutions.add(new DividerSolution(
                                3, "SERIAL-PARALLEL", List.of(r1, r2, r3),
                                ratio * vin, error
                        ));
                    }
                }
            }
        }

        // Сортировка по проценту ошибок
        solutions.sort((a, b) -> Double.compare(a.errorPercentage, b.errorPercentage));

        return solutions;
    }

    private void drawCircuit(DividerSolution solution) {
        circuitPane.getChildren().clear();

        // Нарисуйте схему простого делителя напряжения
        double centerX = circuitPane.getWidth() / 2;
        double centerY = circuitPane.getHeight() / 2;

        // Нарисуйте Vin label
        Text vinText = new Text(centerX - 100, centerY - 80, "Vin");
        circuitPane.getChildren().add(vinText);

        // Нарисуйте GND label
        Text gndText = new Text(centerX - 100, centerY + 100, "GND");
        circuitPane.getChildren().add(gndText);

        // Нарисуйте Vout label
        Text voutText = new Text(centerX + 100, centerY, "Vout");
        circuitPane.getChildren().add(voutText);

        // Подберите резисторы в зависимости от конфигурации
        if (solution.configuration.equals("SERIAL") && solution.resistorCount == 2) {
            drawTwoResistorCircuit(centerX, centerY, solution.resistors);
        } else {
            drawThreeResistorCircuit(centerX, centerY, solution.resistors);
        }
    }

    private void drawTwoResistorCircuit(double centerX, double centerY, List<Double> resistors) {
        // Draw first resistor
        Rectangle r1 = new Rectangle(centerX - 60, centerY - 60, 20, 60);
        circuitPane.getChildren().add(r1);
        Text r1Text = new Text(centerX - 65, centerY - 70, String.format("%.0fΩ", resistors.get(0)));
        circuitPane.getChildren().add(r1Text);

        // Draw second resistor
        Rectangle r2 = new Rectangle(centerX - 60, centerY, 20, 60);
        circuitPane.getChildren().add(r2);
        Text r2Text = new Text(centerX - 65, centerY + 80, String.format("%.0fΩ", resistors.get(1)));
        circuitPane.getChildren().add(r2Text);

        // Draw connecting lines
        Line leftLine = new Line(centerX - 100, centerY - 60, centerX - 60, centerY - 60);
        Line middleLine = new Line(centerX - 40, centerY, centerX + 100, centerY);
        Line rightLine1 = new Line(centerX - 40, centerY - 60, centerX + 100, centerY - 60);
        Line rightLine2 = new Line(centerX - 40, centerY + 60, centerX + 100, centerY + 60);

        circuitPane.getChildren().addAll(leftLine, middleLine, rightLine1, rightLine2);
    }

    private void drawThreeResistorCircuit(double centerX, double centerY, List<Double> resistors) {
        // Упрощённая схема с тремя резисторами
        Rectangle r1 = new Rectangle(centerX - 60, centerY - 60, 20, 60);
        circuitPane.getChildren().add(r1);
        Text r1Text = new Text(centerX - 65, centerY - 70, String.format("%.0fΩ", resistors.get(0)));
        circuitPane.getChildren().add(r1Text);

        Rectangle r2 = new Rectangle(centerX, centerY, 20, 60);
        circuitPane.getChildren().add(r2);
        Text r2Text = new Text(centerX - 5, centerY + 80, String.format("%.0fΩ", resistors.get(1)));
        circuitPane.getChildren().add(r2Text);

        Rectangle r3 = new Rectangle(centerX + 60, centerY, 20, 60);
        circuitPane.getChildren().add(r3);
        Text r3Text = new Text(centerX + 55, centerY + 80, String.format("%.0fΩ", resistors.get(2)));
        circuitPane.getChildren().add(r3Text);

        // Draw connecting lines
        Line leftLine = new Line(centerX - 100, centerY - 60, centerX - 60, centerY - 60);
        Line middleLine1 = new Line(centerX - 40, centerY - 60, centerX, centerY);
        Line middleLine2 = new Line(centerX + 20, centerY, centerX + 60, centerY);
        Line rightLine = new Line(centerX + 80, centerY, centerX + 120, centerY);
        Line bottomLine = new Line(centerX - 40, centerY + 60, centerX + 80, centerY + 60);

        circuitPane.getChildren().addAll(leftLine, middleLine1, middleLine2, rightLine, bottomLine);
    }

    private static class DividerSolution {
        int resistorCount;
        String configuration;
        List<Double> resistors;
        double calculatedVout;
        double errorPercentage;

        DividerSolution(int resistorCount, String configuration, List<Double> resistors,
                        double calculatedVout, double errorPercentage) {
            this.resistorCount = resistorCount;
            this.configuration = configuration;
            this.resistors = resistors;
            this.calculatedVout = calculatedVout;
            this.errorPercentage = errorPercentage;
        }
    }
}