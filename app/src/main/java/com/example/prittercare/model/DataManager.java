package com.example.prittercare.model;

import com.example.prittercare.model.data.CageData;

import java.util.List;

public class DataManager {
    private static DataManager instance;
    private String userToken;
    private String userName;
    private List<CageData> cageList;

    private DataManager() {
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    // UserToken
    public String getUserToken() {
        return userToken;
    }

    public void setToken(String userToken) {
        this.userToken = userToken;
    }

    // UserName
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // CachedCageList
    public void setCageList(List<CageData> cageList) {
        this.cageList = cageList;
    }

    public List<CageData> getCageList() {
        return cageList;
    }
}
