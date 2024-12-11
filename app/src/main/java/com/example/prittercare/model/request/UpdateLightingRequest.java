package com.example.prittercare.model.request;

public class UpdateLightingRequest {
    private String cage_serial_number;
    private String env_lighting;

    public UpdateLightingRequest(String cageSerialNumber, String envLighting) {
        this.cage_serial_number = cageSerialNumber;
        this.env_lighting = envLighting;
    }

    public String getCage_serial_number() {
        return cage_serial_number;
    }

    public String getEnv_lighting() {
        return env_lighting;
    }
}
