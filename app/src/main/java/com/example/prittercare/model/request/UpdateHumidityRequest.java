package com.example.prittercare.model.request;

public class UpdateHumidityRequest {
    private String cage_serial_number;
    private String env_humidity;

    public UpdateHumidityRequest(String cageSerialNumber, String envHumidity) {
        this.cage_serial_number = cageSerialNumber;
        this.env_humidity = envHumidity;
    }

    public String getCage_serial_number() {
        return cage_serial_number;
    }

    public String getEnv_humidity() {
        return env_humidity;
    }
}
