package com.example.prittercare.model.data;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.UUID;

public class ReservationData implements Serializable {

    @SerializedName("cage_serial_number") // 장치 ID
    private String cageSerialNumber;

    @SerializedName("reserve_name") // 예약 이름
    private String reserveName;

    @SerializedName("reserve_date") // 예약 날짜
    private String reserveDate;

    @SerializedName("day_loop") // 일주기
    private int dayLoop;

    @SerializedName("time_loop") // 시간주기
    private int timeLoop;

    @SerializedName("reserve_type") // 예약 종류 ("water", "food", "light")
    private String reserveType;

    @SerializedName("reserve_light_level")
    private int lightLevel; // 조명 단계 (0 ~ 5)

    @SerializedName("reserve_id")
    private int reserveId; // 예약 고유 ID

    @SerializedName("reserve_hour")
    private int reserveHour;

    @SerializedName("reserve_minute")
    private int reserveMinute;

    @SerializedName("reserve_time")
    private String reserveTime;

    private String id; // 고유 ID

    public ReservationData(String cageSerialNumber, String reserveName, String reserveDate, String reserveType,
                           String reserveTime, int dayLoop, int timeLoop, int lightLevel,
                           int reserveId, int reserveHour, int reserveMinute) {
        this.cageSerialNumber = cageSerialNumber;
        this.reserveName = reserveName;
        this.reserveDate = reserveDate;
        this.reserveType = reserveType;
        this.reserveTime = reserveTime;
        this.dayLoop = dayLoop;
        this.timeLoop = timeLoop;
        this.lightLevel = lightLevel;
        this.reserveId = reserveId;
        this.reserveHour = reserveHour;
        this.reserveMinute = reserveMinute;
    }

    // 생성자
    public ReservationData(String reserveName, String reserveType, String reserveDate, String reserveTime) {
        this.id = UUID.randomUUID().toString(); // 고유 ID 생성
        this.reserveName = reserveName;
        this.reserveType = reserveType;
        this.reserveDate = reserveDate;
        this.reserveTime = reserveTime;
    }

    // Getters and setters
    public String getCageSerialNumber() {
        return cageSerialNumber;
    }

    public void setCageSerialNumber(String cageSerialNumber) {
        this.cageSerialNumber = cageSerialNumber;
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

    public int getReserveId() {
        return reserveId;
    }

    public void setReserveId(int reserveId) {
        this.reserveId = reserveId;
    }

    public int getReserveHour() {
        return reserveHour;
    }

    public void setReserveHour(int reserveHour) {
        this.reserveHour = reserveHour;
    }

    public int getReserveMinute() {
        return reserveMinute;
    }

    public void setReserveMinute(int reserveMinute) {
        this.reserveMinute = reserveMinute;
    }

    public String getReserveTime() {
        return reserveTime;
    }

    public void setReserveTime(String reserveTime) {
        this.reserveTime = reserveTime;
    }

    public String getId() {
        return id;
    }
}
