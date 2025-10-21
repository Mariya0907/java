package com.electricengineering.models;

import java.time.LocalDateTime;

public class OhmResult {
    private int id;
    private int userId;
    private double voltage;
    private double current;
    private double resistance;
    private LocalDateTime createdAt;

    public OhmResult(int id, int userId, double voltage, double current, double resistance, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.voltage = voltage;
        this.current = current;
        this.resistance = resistance;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getVoltage() { return voltage; }
    public void setVoltage(double voltage) { this.voltage = voltage; }

    public double getCurrent() { return current; }
    public void setCurrent(double current) { this.current = current; }

    public double getResistance() { return resistance; }
    public void setResistance(double resistance) { this.resistance = resistance; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}