package com.example.prittercare.model;

import com.example.prittercare.model.data.CageData;
import com.example.prittercare.model.data.ReservationData;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponse {
    @SerializedName("cages")
    private List<CageData> cages;

    @SerializedName("reservations")
    private List<ReservationData> reservations;

    public ApiResponse(List<CageData> cages, List<ReservationData> reservations) {
        this.cages = cages;
        this.reservations = reservations;
    }

    public List<CageData> getCages() {
        return cages;
    }

    public List<ReservationData> getReservations() {
        return reservations;
    }
}


