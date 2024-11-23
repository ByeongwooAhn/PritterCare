package com.example.prittercare.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CageData implements Parcelable {
    private String cageSerialNumber;
    private String cageName;
    private String aniamlTyple;
    private String temperature;
    private String humidity;
    private String lighting;
    private String waterLevel;

    public CageData(String cageSerialNumber, String cageName, String aniamlTyple, String temperature, String humidity, String lighting, String waterLevel) {
        this.cageSerialNumber = cageSerialNumber;
        this.cageName = cageName;
        this.aniamlTyple = aniamlTyple;
        this.temperature = temperature;
        this.humidity = humidity;
        this.lighting = lighting;
        this.waterLevel = waterLevel;
    }

    // Parcelable 구현
    protected CageData(Parcel in) {
        cageSerialNumber = in.readString();
        cageName = in.readString();
        aniamlTyple = in.readString();
        temperature = in.readString();
        humidity = in.readString();
        lighting = in.readString();
        waterLevel = in.readString();
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
        dest.writeString(cageName);
        dest.writeString(aniamlTyple);
        dest.writeString(temperature);
        dest.writeString(humidity);
        dest.writeString(lighting);
        dest.writeString(waterLevel);
    }

    // Getter & Setter
    public String getCageSerialNumber() {
        return cageSerialNumber;
    }

    public String getCageName() {
        return cageName;
    }

    public String getAnimalType() {
        return aniamlTyple;
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

    public void setCageName(String cageName) {
        this.cageName = cageName;
    }

    public void setAniamlTyple(String aniamlTyple) {
        this.aniamlTyple = aniamlTyple;
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
