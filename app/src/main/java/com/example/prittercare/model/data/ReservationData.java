package com.example.prittercare.model.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ReservationData implements Serializable {

    @SerializedName("cage_serial_number") // 장치 ID
    private String cageSerialNumber;

    @SerializedName("reserve_name") // 예약 이름
    private String reserveName;

    @SerializedName("reserve_date") // 예약 날짜
    private String reserveDate;

    @SerializedName("reserve_time") // 예약 시간
    private String reserveTime;

    @SerializedName("day_loop") // 일주기
    private int dayLoop;

    @SerializedName("time_loop") // 시간주기
    private int timeLoop;

    @SerializedName("reserve_type") // 예약 종류 ("water", "food", "light")
    private String reserveType;

    @SerializedName("reserve_light_level")
    private int lightLevel; // 조명 단계 (0 ~ 5)

    private int id; // 예약 고유 ID
    private String userId; // 사용자 ID 추가
    private int hour;
    private int minute;

    public ReservationData(String cageSerialNumber, String reserveTime, String reserveDate, String reserveName, String reserveType, int dayLoop, int timeLoop) {
        this.cageSerialNumber = cageSerialNumber;
        this.reserveName = reserveName;
        this.reserveDate = reserveDate;
        this.reserveTime = reserveTime;
        this.dayLoop = dayLoop;
        this.timeLoop = timeLoop;
        this.reserveType = reserveType;
    }

    public ReservationData(String reserveTime, String reserveDate, String reserveName, String reserveType) {
    }

    // Getters and setters
    public String getCageSerialNumber() {
        return cageSerialNumber;
    }

    public void setCageSerialNumber(String cageSerialNumber) {
        this.cageSerialNumber = cageSerialNumber;
    }

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

    public int getLightLevel() {
        return lightLevel;
    }

    public void setLightLevel(int lightLevel) {
        this.lightLevel = lightLevel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
