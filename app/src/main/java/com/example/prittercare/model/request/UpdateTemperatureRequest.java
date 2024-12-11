package com.example.prittercare.model.request;

public class UpdateTemperatureRequest {
    private String cage_serial_number;
    private String env_temperature;

    public UpdateTemperatureRequest(String cageSerialNumber, String envTemperature) {
        this.cage_serial_number = cageSerialNumber;
        this.env_temperature = envTemperature;
    }

    public String getCage_serial_number() {
        return cage_serial_number;
    }

    public String getEnv_temperature() {
        return env_temperature;
    }
}
