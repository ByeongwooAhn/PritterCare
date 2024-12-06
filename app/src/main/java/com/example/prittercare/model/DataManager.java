package com.example.prittercare.model;

import com.example.prittercare.model.data.CageData;

import java.util.List;

public class DataManager {
    private static DataManager instance;
    private String userToken;
    private String userName;
    private List<CageData> cageList;
    private String currentCageSerialNumber; // 현재 타겟 케이지의 Serial Number

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

    public String getCurrentCageSerialNumber() {
        return currentCageSerialNumber;
    }

    public void setCurrentCageSerialNumber(String currentCageSerialNumber) {
        this.currentCageSerialNumber = currentCageSerialNumber;
    }

    // 현재 타겟 CageData 가져오기
    private CageData getCurrentCageData() {
        if (cageList == null || currentCageSerialNumber == null) {
            return null;
        }
        for (CageData cage : cageList) {
            if (currentCageSerialNumber.equals(cage.getCageSerialNumber())) {
                return cage;
            }
        }
        return null;
    }

    // 현재 타겟 CageName 가져오기
    public String getCurrentCageName() {
        CageData currentCage = getCurrentCageData();
        return currentCage != null ? currentCage.getCageName() : null;
    }

    // 현재 타겟 AnimalType 가져오기
    public String getCurrentAnimalType() {
        CageData currentCage = getCurrentCageData();
        return currentCage != null ? currentCage.getAnimalType() : null;
    }

    public void updateCageData(CageData updatedCage) {
        for (CageData cage : cageList) {
            if (cage.getCageSerialNumber().equals(updatedCage.getCageSerialNumber())) {
                cage.setCageName(updatedCage.getCageName());
                break;
            }
        }
    }

    public void removeCageData(CageData cage) {
        cageList.remove(cage);
    }
}
