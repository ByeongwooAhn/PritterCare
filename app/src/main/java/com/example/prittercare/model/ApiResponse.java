package com.example.prittercare.model;

import com.example.prittercare.model.data.CageData;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Data data;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("cages")
        private List<CageData> cages;

/*        @SerializedName("reservations")
        private List<ReservationData> reservations;*/

        public List<CageData> getCages() {
            return cages;
        }

/*        public List<ReservationData> getReservations() {
            return reservations;
        }*/
    }
}
