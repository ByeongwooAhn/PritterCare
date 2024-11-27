package com.example.prittercare.model.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ReservationData implements Serializable {
    @SerializedName("cage_serial_number")
    private String cageSerialNumber;

    @SerializedName("reserve_name")
    private String reserveName;

    @SerializedName("reserve_date")
    private String reserveDate;

    @SerializedName("reserve_time")
    private String reserveTime;

    @SerializedName("activate")
    private boolean activate;

    @SerializedName("day_loop")
    private int dayLoop; // 일주기

    @SerializedName("time_loop")
    private int timeLoop; // 시간주기

    @SerializedName("reserve_type")
    private String reserveType; // 예약 종류

    public ReservationData(String cageSerialNumber, String reserveTime, String reserveDate, boolean activate, String reserveName, String reserveType, int dayLoop, int timeLoop) {
        this.cageSerialNumber = cageSerialNumber;
        this.reserveName = reserveName;
        this.reserveDate = reserveDate;
        this.reserveTime = reserveTime;
        this.activate = activate;
        this.dayLoop = dayLoop;
        this.timeLoop = timeLoop;
        this.reserveType = reserveType;
    }

    public ReservationData(String reserveTime, String reserveDate, boolean activate, String reserveName, String reserveType) {
    }

    // Getters and setters
    public String getReserveTime() {
        return reserveTime;
    }

    public void setReserveTime(String reserveTime) {
        this.reserveTime = reserveTime;
    }

    public String getReserveDate() {
        return reserveDate;
    }

    public void setReserveDate(String reserveDate) {
        this.reserveDate = reserveDate;
    }

    public boolean isActivate() {
        return activate;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }

    public String getReserveName() {
        return reserveName;
    }

    public void setReserveName(String reserveName) {
        this.reserveName = reserveName;
    }

    public String getReserveType() {
        return reserveType;
    }

    public void setReserveType(String reserveType) {
        this.reserveType = reserveType;
    }

    public int getDayLoop() {
        return dayLoop;
    }

    public void setDayLoop(int dayLoop) {
        this.dayLoop = dayLoop;
    }

    public int getTimeLoop() {
        return timeLoop;
    }

    public void setTimeLoop(int timeLoop) {
        this.timeLoop = timeLoop;
    }
}
