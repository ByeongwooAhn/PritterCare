package com.example.prittercare.view.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.prittercare.R;

public class LightFragment extends Fragment {

    // Parameters for the fragment
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    // Views
    private TextView lightLevelText;
    private ImageButton btnLeft;
    private ImageButton btnRight;
    private ImageView point1, point2, point3, point4, point5;

    // Light level variables
    private int currentLightLevel = 1; // Start from Level 1 (1-5)
    private int maxLightLevel = 5; // 5 steps

    public LightFragment() {
        // Required empty public constructor
    }

    public static LightFragment newInstance(String param1, String param2) {
        LightFragment fragment = new LightFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_light, container, false);

        // Initialize the views
        lightLevelText = rootView.findViewById(R.id.lightLevel);
        btnLeft = rootView.findViewById(R.id.btnLeft);
        btnRight = rootView.findViewById(R.id.btnRight);
        point1 = rootView.findViewById(R.id.point1);
        point2 = rootView.findViewById(R.id.point2);
        point3 = rootView.findViewById(R.id.point3);
        point4 = rootView.findViewById(R.id.point4);
        point5 = rootView.findViewById(R.id.point5);

        // Update initial level and visibility of points and line
        updateLightLevel();

        // Set click listeners for buttons
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decrease light level when left button is clicked
                if (currentLightLevel > 1) {
                    currentLightLevel--;
                    updateLightLevel();
                }
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Increase light level when right button is clicked
                if (currentLightLevel < maxLightLevel) {
                    currentLightLevel++;
                    updateLightLevel();
                }
            }
        });

        return rootView;
    }

    // Update the light level on the UI
    private void updateLightLevel() {
        // Update light level text
        lightLevelText.setText("Level: " + currentLightLevel);

        // Reset visibility of all points and the line
        point1.setVisibility(View.INVISIBLE);
        point2.setVisibility(View.INVISIBLE);
        point3.setVisibility(View.INVISIBLE);
        point4.setVisibility(View.INVISIBLE);
        point5.setVisibility(View.INVISIBLE);

        // Show points and line based on the current level
        switch (currentLightLevel) {
            case 1:
                point1.setVisibility(View.VISIBLE);
                break;
            case 2:
                point2.setVisibility(View.VISIBLE);
                break;
            case 3:
                point3.setVisibility(View.VISIBLE);
                break;
            case 4:
                point4.setVisibility(View.VISIBLE);
                break;
            case 5:
                point5.setVisibility(View.VISIBLE);
                break;
        }
    }
}
