package com.example.prittercare.view.main.reservation;

import java.io.Serializable;

public class Alarm implements Serializable {
    private String time;
    private String date;
    private boolean isEnabled;
    private String name;  // 알람 이름 추가

    public Alarm(String time, String date, boolean isEnabled, String name) {
        this.time = time;
        this.date = date;
        this.isEnabled = isEnabled;
        this.name = name;
    }

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
}
