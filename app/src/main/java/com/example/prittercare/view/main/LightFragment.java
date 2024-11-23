package com.example.prittercare.view.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
    private Button btnOn;
    private Button btnOff;

    // Light level variables
    private int currentLightLevel = 1; // Start from Level 1 (1-5)
    private int maxLightLevel = 5; // 5 steps

    // Container for light level
    private View containerLightLevel;

    // Boolean to track if the light is on or off
    private boolean isLightOn = true;

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
        lightLevelText = rootView.findViewById(R.id.tv_light_level);
        btnLeft = rootView.findViewById(R.id.btn_left);
        btnRight = rootView.findViewById(R.id.btn_right);
        btnOn = rootView.findViewById(R.id.btn_light_on);  // ON button
        btnOff = rootView.findViewById(R.id.btn_light_off);  // OFF button
        containerLightLevel = rootView.findViewById(R.id.container_light_level); // Initialize the container

        // Update initial level and visibility of points and line
        updateLightLevel();

        // Set click listeners for light control buttons (ON/OFF)
        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButtonState(btnOn,btnOff);
                turnLightOn();  // Turn light on
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButtonState(btnOff,btnOn);
                turnLightOff();  // Turn light off
            }
        });

        // Set click listeners for navigation buttons (Left/Right)
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decrease light level when left button is clicked
                if (isLightOn && currentLightLevel > 1) {
                    currentLightLevel--;
                    updateLightLevel();
                }
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Increase light level when right button is clicked
                if (isLightOn && currentLightLevel < maxLightLevel) {
                    currentLightLevel++;
                    updateLightLevel();
                }
            }
        });

        return rootView;
    }

    // Update the light level on the UI
    private void updateLightLevel() {
        if (isLightOn) {
            // Update light level text
            lightLevelText.setText(currentLightLevel + " 단계");

            // Dynamically change the text color based on the current light level
            int colorResId = getColorForLightLevel(currentLightLevel);
            lightLevelText.setTextColor(getResources().getColor(colorResId));

            // Dynamically change the background color of the container
            int containerColorResId = getContainerColorForLightLevel(currentLightLevel);
            containerLightLevel.setBackgroundResource(containerColorResId);
        } else {
            // If light is off, disable controls
            lightLevelText.setText("조명 꺼짐");
            lightLevelText.setTextColor(getResources().getColor(R.color.lightOFFText));
            containerLightLevel.setBackgroundResource(R.drawable.shape_card_main_light_off);
        }
    }

    // Get color resource ID based on light level
    private int getColorForLightLevel(int level) {
        switch (level) {
            case 1:
                return R.color.lightLevelText1; // Color for level 1
            case 2:
                return R.color.lightLevelText2; // Color for level 2
            case 3:
                return R.color.lightLevelText3; // Color for level 3
            case 4:
                return R.color.lightLevelText4; // Color for level 4
            case 5:
                return R.color.lightLevelText5; // Color for level 5
            default:
                return R.color.lightLevelText1; // Default color if level is invalid
        }
    }

    // Get background color resource ID for the container based on light level
    private int getContainerColorForLightLevel(int level) {
        switch (level) {
            case 1:
                return R.drawable.shape_card_main_light_level1; // Background for level 1
            case 2:
                return R.drawable.shape_card_main_light_level2; // Background for level 2
            case 3:
                return R.drawable.shape_card_main_light_level3; // Background for level 3
            case 4:
                return R.drawable.shape_card_main_light_level4; // Background for level 4
            case 5:
                return R.drawable.shape_card_main_light_level5; // Background for level 5
            default:
                return R.drawable.shape_card_main_light_level1; // Default background if level is invalid
        }
    }

    // Method to turn the light ON
    private void turnLightOn() {
        isLightOn = true;
        updateLightLevel();
    }

    // Method to turn the light OFF
    private void turnLightOff() {
        isLightOn = false;
        updateLightLevel();
    }

    private void toggleButtonState(Button activeButton, Button inactiveButton) {
        // 활성 버튼을 primaryStyle 로 수정
        activeButton.setBackgroundResource(R.drawable.shape_button_main_light_active);
        activeButton.setTextColor(getResources().getColor(R.color.mainLightActiveBtnTextColor));

        // 비활성 버튼을 inactiveStyle 로 수정
        inactiveButton.setBackgroundResource(R.drawable.shape_button_main_primary);
        inactiveButton.setTextColor(getResources().getColor(R.color.mainPrimaryBtnTextColor));
    }
}
