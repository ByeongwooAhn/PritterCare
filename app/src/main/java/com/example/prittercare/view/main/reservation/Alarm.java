package com.example.prittercare.view.main.reservation;

import java.io.Serializable;

public class Alarm implements Serializable {
    private String time;
    private String date;
    private boolean isEnabled;
    private String name;
    private String type; // 예약 종류
    private int dailyCycle; // 일주기
    private int hourlyCycle; // 시간주기

    public Alarm(String time, String date, boolean isEnabled, String name, String type, int dailyCycle, int hourlyCycle) {
        this.time = time;
        this.date = date;
        this.isEnabled = isEnabled;
        this.name = name;
        this.type = type;
        this.dailyCycle = dailyCycle;
        this.hourlyCycle = hourlyCycle;
    }

    public Alarm(String time, String date, boolean isEnabled, String name, String type) {
    }

    // Getters and setters
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDailyCycle() {
        return dailyCycle;
    }

    public void setDailyCycle(int dailyCycle) {
        this.dailyCycle = dailyCycle;
    }

    public int getHourlyCycle() {
        return hourlyCycle;
    }

    public void setHourlyCycle(int hourlyCycle) {
        this.hourlyCycle = hourlyCycle;
    }
}
