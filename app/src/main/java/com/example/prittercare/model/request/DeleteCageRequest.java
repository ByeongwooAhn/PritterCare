package com.example.prittercare.model.request;

public class DeleteCageRequest {
    private String cage_serial_number;

    public DeleteCageRequest(String cageSerialNumber) {
        this.cage_serial_number = cageSerialNumber;
    }

    public String getCage_serial_number() {
        return cage_serial_number;
    }
}
