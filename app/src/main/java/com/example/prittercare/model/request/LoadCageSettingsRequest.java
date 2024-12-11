package com.example.prittercare.model.request;

public class LoadCageSettingsRequest {
    private String cageSerialNumber;

    public LoadCageSettingsRequest(String cage_serial_number) {
        this.cageSerialNumber = cage_serial_number;
    }

    public String getCageSerialNumber() {
        return cageSerialNumber;
    }
}
