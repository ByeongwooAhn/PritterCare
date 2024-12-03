package com.example.prittercare.model.data;

public class CageData {
    private String cage_serial_number;
    private String cage_name;
    private String animal_type;

    // Getter and Setter
    public String getCageSerialNumber() {
        return cage_serial_number;
    }

    public void setCageSerialNumber(String cage_serial_number) {
        this.cage_serial_number = cage_serial_number;
    }

    public String getCageName() {
        return cage_name;
    }

    public void setCageName(String cage_name) {
        this.cage_name = cage_name;
    }

    public String getAnimalType() {
        return animal_type;
    }

    public void setAnimalType(String animal_type) {
        this.animal_type = animal_type;
    }
}

