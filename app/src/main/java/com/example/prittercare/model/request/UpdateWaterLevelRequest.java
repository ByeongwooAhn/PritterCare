package com.example.prittercare.model.request;

public class UpdateWaterLevelRequest {
    private String cage_serial_number;
    private String env_water_level;

    public UpdateWaterLevelRequest(String cageSerialNumber, String envWaterLevel) {
        this.cage_serial_number = cageSerialNumber;
        this.env_water_level = envWaterLevel;
    }

    public String getCage_serial_number() {
        return cage_serial_number;
    }

    public String getEnv_water_level() {
        return env_water_level;
    }
}
