package com.example.prittercare.model.data;

import com.google.gson.annotations.SerializedName;

public class CageData {
    @SerializedName("cage_serial_number")
    private String cageSerialNumber;

    @SerializedName("cage_name")
    private String cageName;

    @SerializedName("animal_type")
    private String animalType;

    // 환경 데이터 및 기타 필드는 서버에서 응답하지 않으면 null로 유지
    @SerializedName("username")
    private String userName;

    @SerializedName("env_temperature")
    private String envTemperature;

    @SerializedName("env_humidity")
    private String envHumidity;

    @SerializedName("env_lighting")
    private String envLighting;

    @SerializedName("env_water_level")
    private String envWaterLevel;

    // Getter and Setter
    public String getCageSerialNumber() {
        return cageSerialNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEnvTemperature() {
        return envTemperature;
    }

    public void setEnvTemperature(String envTemperature) {
        this.envTemperature = envTemperature;
    }

    public String getEnvHumidity() {
        return envHumidity;
    }

    public void setEnvHumidity(String envHumidity) {
        this.envHumidity = envHumidity;
    }

    public String getEnvLighting() {
        return envLighting;
    }

    public void setEnvLighting(String envLighting) {
        this.envLighting = envLighting;
    }

    public String getEnvWaterLevel() {
        return envWaterLevel;
    }

    public void setEnvWaterLevel(String envWaterLevel) {
        this.envWaterLevel = envWaterLevel;
    }

    public void setCageSerialNumber(String cage_serial_number) {
        this.cageSerialNumber = cage_serial_number;
    }

    public String getCageName() {
        return cageName;
    }

    public void setCageName(String cage_name) {
        this.cageName = cage_name;
    }

    public String getAnimalType() {
        return animalType;
    }

    public void setAnimalType(String animal_type) {
        this.animalType = animal_type;
    }

    // toString 메서드
    @Override
    public String toString() {
        return "CageData{" +
                "cageSerialNumber='" + cageSerialNumber + '\'' +
                ", cageName='" + cageName +
                ", animalType='" + animalType + '\'' +
                ", userName='" + userName + '\'' +
                ", envTemperature='" + envTemperature + '\'' +
                ", envHumidity='" + envHumidity + '\'' +
                ", envLighting='" + envLighting + '\'' +
                ", envWaterLevel='" + envWaterLevel + '\'' +
                '}';
    }
}

