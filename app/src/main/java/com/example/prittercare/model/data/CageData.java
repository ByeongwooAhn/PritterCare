package com.example.prittercare.model.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CageData implements Parcelable {
    @SerializedName("cage_serial_number")
    private String cageSerialNumber;

    @SerializedName("username")
    private String userName;

    @SerializedName("cage_name")
    private String cageName;

    @SerializedName("animal_type")
    private String animalType;

    @SerializedName("env_temperature")
    private String temperature;

    @SerializedName("env_humidity")
    private String humidity;

    @SerializedName("env_lighting")
    private String lighting;

    @SerializedName("env_water_level")
    private String waterLevel;

    @SerializedName("setType")
    private String setType;

    @SerializedName("setValue")
    private String setValue;

    public CageData(String cageSerialNumber, String userName, String cageName, String animalType, String temperature, String humidity, String lighting, String waterLevel, String setType, String setValue) {
        this.cageSerialNumber = cageSerialNumber;
        this.userName = userName;
        this.cageName = cageName;
        this.animalType = animalType;
        this.temperature = temperature;
        this.humidity = humidity;
        this.lighting = lighting;
        this.waterLevel = waterLevel;
        this.setType = setType;
        this.setValue = setValue;
    }

    public CageData() {
        this.cageSerialNumber = "";
        this.userName = "";
        this.cageName = "";
        this.animalType = "";
        this.temperature = "";
        this.humidity = "";
        this.lighting = "";
        this.waterLevel = "";
        this.setType = "";
        this.setValue = "";
    }

    // Parcelable 구현
    protected CageData(Parcel in) {
        cageSerialNumber = in.readString();
        userName = in.readString();
        cageName = in.readString();
        animalType = in.readString();
        temperature = in.readString();
        humidity = in.readString();
        lighting = in.readString();
        waterLevel = in.readString();
        setType = in.readString();
        setValue = in.readString();
    }

    public static final Creator<CageData> CREATOR = new Creator<CageData>() {
        @Override
        public CageData createFromParcel(Parcel in) {
            return new CageData(in);
        }

        @Override
        public CageData[] newArray(int size) {
            return new CageData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cageSerialNumber);
        dest.writeString(userName);
        dest.writeString(cageName);
        dest.writeString(animalType);
        dest.writeString(temperature);
        dest.writeString(humidity);
        dest.writeString(lighting);
        dest.writeString(waterLevel);
        dest.writeString(setType);
        dest.writeString(setValue);
    }

    // Getter & Setter
    public String getCageSerialNumber() {
        return cageSerialNumber;
    }

    public String getUserName() {
        return userName;
    }

    public String getCageName() {
        return cageName;
    }

    public String getAnimalType() {
        return animalType;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getLighting() {
        return lighting;
    }

    public String getWaterLevel() {
        return waterLevel;
    }

    public String getSetType() {
        return setType;
    }

    public String getSetValue() {
        return setValue;
    }

    public void setCageName(String cageName) {
        this.cageName = cageName;
    }

    public void setAniamlType(String aniamlType) {
        this.animalType = aniamlType;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public void setLighting(String lighting) {
        this.lighting = lighting;
    }

    public void setWaterLevel(String waterLevel) {
        this.waterLevel = waterLevel;
    }
}
