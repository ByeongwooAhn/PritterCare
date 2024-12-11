package com.example.prittercare.model.request;

public class UpdateCageNameRequest {
    private String cage_serial_number;
    private String cage_name;

    public UpdateCageNameRequest(String cageSerialNumber, String cageName) {
        this.cage_serial_number = cageSerialNumber;
        this.cage_name = cageName;
    }

    public String getCage_serial_number() {
        return cage_serial_number;
    }

    public String getCage_name() {
        return cage_name;
    }
}
