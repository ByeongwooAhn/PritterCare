package com.example.prittercare.model;

import static android.content.ContentValues.TAG;

import android.util.Log;

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
    public CageData getCurrentCageData() {
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

    // 로그아웃 시 데이터 초기화
    public void clearData() {
        userToken = null;
        userName = null;
        if (cageList != null) {
            cageList.clear();
        }
        currentCageSerialNumber = null;
    }

    // 모든 데이터를 Log로 출력하는 메서드
    public void logAllData() {
        Log.d(TAG, "User Token: " + userToken);
        Log.d(TAG, "User Name: " + userName);
        Log.d(TAG, "Current Cage Serial Number: " + currentCageSerialNumber);

        if (cageList != null && !cageList.isEmpty()) {
            Log.d(TAG, "Cage List:");
            for (CageData cage : cageList) {
                Log.d(TAG, " - Serial Number: " + cage.getCageSerialNumber());
                Log.d(TAG, "   Name: " + cage.getCageName());
                Log.d(TAG, "   Animal Type: " + cage.getAnimalType());
                Log.d(TAG, "   Temperature: " + cage.getEnvTemperature());
                Log.d(TAG, "   Humidity: " + cage.getEnvHumidity());
                Log.d(TAG, "   Lighting: " + cage.getEnvLighting());
                Log.d(TAG, "   Water Level: " + cage.getEnvWaterLevel());
            }
        } else {
            Log.d(TAG, "Cage List is empty or null.");
        }
    }
}
