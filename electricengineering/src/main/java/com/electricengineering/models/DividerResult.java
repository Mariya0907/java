package com.electricengineering.models;

import java.time.LocalDateTime;

public class DividerResult {
    private int id;
    private int userId;
    private double vin;
    private double voutRequired;
    private double tolerance;
    private String resistorSeries;
    private double minResistance;
    private double maxResistance;
    private int resistorCount;
    private String configuration;
    private String resistors;
    private double calculatedVout;
    private double errorPercentage;
    private LocalDateTime createdAt;

    public DividerResult(int id, int userId, double vin, double voutRequired, double tolerance,
                         String resistorSeries, double minResistance, double maxResistance,
                         int resistorCount, String configuration, String resistors,
                         double calculatedVout, double errorPercentage, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.vin = vin;
        this.voutRequired = voutRequired;
        this.tolerance = tolerance;
        this.resistorSeries = resistorSeries;
        this.minResistance = minResistance;
        this.maxResistance = maxResistance;
        this.resistorCount = resistorCount;
        this.configuration = configuration;
        this.resistors = resistors;
        this.calculatedVout = calculatedVout;
        this.errorPercentage = errorPercentage;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getVin() { return vin; }
    public void setVin(double vin) { this.vin = vin; }

    public double getVoutRequired() { return voutRequired; }
    public void setVoutRequired(double voutRequired) { this.voutRequired = voutRequired; }

    public double getTolerance() { return tolerance; }
    public void setTolerance(double tolerance) { this.tolerance = tolerance; }

    public String getResistorSeries() { return resistorSeries; }
    public void setResistorSeries(String resistorSeries) { this.resistorSeries = resistorSeries; }

    public double getMinResistance() { return minResistance; }
    public void setMinResistance(double minResistance) { this.minResistance = minResistance; }

    public double getMaxResistance() { return maxResistance; }
    public void setMaxResistance(double maxResistance) { this.maxResistance = maxResistance; }

    public int getResistorCount() { return resistorCount; }
    public void setResistorCount(int resistorCount) { this.resistorCount = resistorCount; }

    public String getConfiguration() { return configuration; }
    public void setConfiguration(String configuration) { this.configuration = configuration; }

    public String getResistors() { return resistors; }
    public void setResistors(String resistors) { this.resistors = resistors; }

    public double getCalculatedVout() { return calculatedVout; }
    public void setCalculatedVout(double calculatedVout) { this.calculatedVout = calculatedVout; }

    public double getErrorPercentage() { return errorPercentage; }
    public void setErrorPercentage(double errorPercentage) { this.errorPercentage = errorPercentage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}