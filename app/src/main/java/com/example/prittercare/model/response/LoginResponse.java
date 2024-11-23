package com.example.prittercare.model.response;

import com.example.prittercare.model.CageData;

import java.util.List;

public class LoginResponse {
    private boolean success;
    private String username;
    private List<CageData> cages; // 케이지 리스트

    public LoginResponse(boolean success, String username, List<CageData> cages) {
        this.success = success;
        this.username = username;
        this.cages = cages;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getUsername() {
        return username;
    }

    public List<CageData> getCages() {
        return cages;
    }
}
