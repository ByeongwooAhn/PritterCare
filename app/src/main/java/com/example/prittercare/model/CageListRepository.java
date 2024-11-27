package com.example.prittercare.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CageListRepository {
    private static final String PREF_NAME = "CageData";
    private static final String CAGE_LIST_KEY = "cageList";
    private static final String SELECTED_CAGE_KEY = "selectedCage";

    private SharedPreferences sharedPreferences;

    public CageListRepository(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public List<CageData> loadCages() {
        String savedData = sharedPreferences.getString(CAGE_LIST_KEY, "");
        List<CageData> cageList = new ArrayList<>();

        if (!savedData.isEmpty()) {
            Log.d("CageListRepository", "Saved Data: " + savedData);
            String[] items = savedData.split(";");
            for (String item : items) {
                String[] parts = item.split(",");
                if (parts.length == 8) {
                    Log.d("CageListRepository", "Loading item: " + item);
                    CageData cageData = new CageData(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]);
                    cageList.add(cageData);
                } else {
                    Log.e("CageListRepository", "Invalid item format: " + item);
                }
            }
        }
        return cageList;
    }

    public void saveCages(List<CageData> cageList) {
        StringBuilder dataBuilder = new StringBuilder();
        for (CageData cage : cageList) {
            dataBuilder.append(cage.getCageSerialNumber()).append(",")
                    .append(cage.getUserName()).append(",")
                    .append(cage.getCageName()).append(",")
                    .append(cage.getAnimalType()).append(",")
                    .append(cage.getTemperature()).append(",")
                    .append(cage.getHumidity()).append(",")
                    .append(cage.getLighting()).append(",")
                    .append(cage.getWaterLevel()).append(";");
        }
        if (dataBuilder.length() > 0) {
            dataBuilder.setLength(dataBuilder.length() - 1); // 마지막 세미콜론 제거
        }
        sharedPreferences.edit().putString(CAGE_LIST_KEY, dataBuilder.toString()).apply();
        Log.d("CageListRepository", "Saved Data in SharedPreferences: " + dataBuilder.toString());
    }

    // 선택된 CageData 저장
    public void saveSelectedCage(CageData cage) {
        String data = cage.getCageSerialNumber() + "," +
                cage.getUserName() + "," +
                cage.getCageName() + "," +
                cage.getAnimalType() + "," +
                cage.getTemperature() + "," +
                cage.getHumidity() + "," +
                cage.getLighting() + "," +
                cage.getWaterLevel();
        sharedPreferences.edit().putString(SELECTED_CAGE_KEY, data).apply();
        Log.d("CageListRepository", "Saved Selected Cage: " + data);
    }

    // 선택된 CageData 로드
    public CageData loadSelectedCage() {
        String data = sharedPreferences.getString(SELECTED_CAGE_KEY, "");
        if (!data.isEmpty()) {
            String[] parts = data.split(",");
            if (parts.length == 8) {
                return new CageData(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]);
            }
        }
        Log.d("CageListRepository", "No Selected Cage Data Found.");
        return null;
    }

}
