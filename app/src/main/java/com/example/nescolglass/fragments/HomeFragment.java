package com.example.nescolglass.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nescolglass.MainActivity;
import com.example.nescolglass.R;

import java.util.Locale;

public class HomeFragment extends Fragment {
    // Variables for UI elements and state
    private boolean setTimer;
    private ProgressBar btConnectionProgressBar;
    private ImageView btConnectionImageView;
    private TextView btConnectionTextView;
    private Button timerButton;
    private Button stopwatchButton;
    private Button timePickerButton;
    private ImageButton startButton;
    private ImageButton pauseButton;
    private ImageButton resetButton;
    private ImageButton stopButton;
    private String btConnectionTextView_text;
    private int hour, minute, second;
    private boolean paused;

    // Constructor initializing default values
    public HomeFragment() {
        btConnectionTextView_text = "Not connected";
        setTimer = true;
        paused = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI elements
        btConnectionProgressBar = view.findViewById(R.id.btConnectionProgressBar);
        btConnectionImageView = view.findViewById(R.id.btConnectionImageView);
        btConnectionTextView = view.findViewById(R.id.btConnectionTextView);

        timerButton = view.findViewById(R.id.timerButton);
        timerButton.setOnClickListener(this::timerButtonVoid);

        stopwatchButton = view.findViewById(R.id.stopwatchButton);
        stopwatchButton.setOnClickListener(this::stopwatchButtonVoid);

        timePickerButton = view.findViewById(R.id.timePickerButton);
        timePickerButton.setOnClickListener(this::timePickerBtn);

        startButton = view.findViewById(R.id.startButton);
        startButton.setOnClickListener((view1) -> timerStopwatchPostRequest(view1, startButton.getId()));

        pauseButton = view.findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener((view1) -> timerStopwatchPostRequest(view1, pauseButton.getId()));

        resetButton = view.findViewById(R.id.resetButton);
        resetButton.setOnClickListener((view1) -> timerStopwatchPostRequest(view1, resetButton.getId()));

        stopButton = view.findViewById(R.id.stopButton);
        stopButton.setOnClickListener((view1) -> timerStopwatchPostRequest(view1, stopButton.getId()));

        // Apply stored preferences
        applyPrefs();

        return view;
    }

    // Method to handle timer button click
    private void timerButtonVoid(View view) {
        // Update UI for timer mode
        timerButton.setTextColor(getResources().getColor(R.color.white));
        timerButton.setBackgroundColor(getResources().getColor(R.color.black));
        stopwatchButton.setTextColor(getResources().getColor(R.color.gray));
        stopwatchButton.setBackgroundColor(getResources().getColor(R.color.light_gray));
        timePickerButton.setTextColor(getResources().getColor(R.color.white));
        timePickerButton.setBackgroundColor(getResources().getColor(R.color.black));
        timePickerButton.setClickable(true);
        setTimer = true;
        paused = false;
    }

    // Method to handle stopwatch button click
    private void stopwatchButtonVoid(View view) {
        // Update UI for stopwatch mode
        stopwatchButton.setTextColor(getResources().getColor(R.color.white));
        stopwatchButton.setBackgroundColor(getResources().getColor(R.color.black));
        timerButton.setTextColor(getResources().getColor(R.color.gray));
        timerButton.setBackgroundColor(getResources().getColor(R.color.light_gray));
        timePickerButton.setTextColor(getResources().getColor(R.color.gray));
        timePickerButton.setBackgroundColor(getResources().getColor(R.color.light_gray));
        timePickerButton.setClickable(false);
        setTimer = false;
        paused = false;
        hour = 0;
        minute = 0;
        second = 0;
        timePickerButton.setText(getResources().getText(R.string.select_time));
    }

    // Method to handle time picker button click
    private void timePickerBtn(View view) {
        // Show time picker dialog
        Dialog timePickerDialog = new Dialog(view.getContext());
        timePickerDialog.setContentView(R.layout.time_picker);

        // Initialize number pickers for hours, minutes, and seconds
        NumberPicker numpicker_hours = timePickerDialog.findViewById(R.id.numpicker_hours);
        setNumpickerStyle(numpicker_hours, hour);
        NumberPicker numpicker_minutes = timePickerDialog.findViewById(R.id.numpicker_minutes);
        setNumpickerStyle(numpicker_minutes, minute);
        NumberPicker numpicker_seconds = timePickerDialog.findViewById(R.id.numpicker_seconds);
        setNumpickerStyle(numpicker_seconds, second);

        // Set click listeners for cancel and ok buttons
        Button dialogCancelBtn = timePickerDialog.findViewById(R.id.dialogCancelBtn);
        dialogCancelBtn.setOnClickListener(view1 -> dialogCancelBtnVoid(view1, timePickerDialog));
        Button dialogOkBtn = timePickerDialog.findViewById(R.id.dialogOkBtn);
        dialogOkBtn.setOnClickListener((view1) -> dialogOkBtnVoid(view1, numpicker_hours, numpicker_minutes, numpicker_seconds, timePickerDialog));

        timePickerDialog.show();
    }

    // Method to set number picker style
    private void setNumpickerStyle(NumberPicker numpickerHours, int value) {
        numpickerHours.setMinValue(0);
        numpickerHours.setMaxValue(99);
        numpickerHours.setValue(value);
    }

    // Method to handle OK button click in time picker dialog
    private void dialogOkBtnVoid(View view, NumberPicker numpicker_hour, NumberPicker numpicker_minutes, NumberPicker numpicker_seconds, Dialog timePickerDialog) {
        // Update selected time values
        hour = numpicker_hour.getValue();
        minute = numpicker_minutes.getValue();
        second = numpicker_seconds.getValue();
        if (hour > 0 || minute > 0 || second > 0) {
            timePickerButton.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, second));
        } else {
            timePickerButton.setText(getResources().getText(R.string.select_time));
        }
        timePickerDialog.dismiss();
    }

    // Method to handle cancel button click in time picker dialog
    private void dialogCancelBtnVoid(View view, Dialog timePickerDialog) {
        timePickerDialog.dismiss();
    }

    // Method to handle start, pause, reset, and stop button clicks
    private void timerStopwatchPostRequest(View view1, int id) {
        String formattedText = "";
        if (id == R.id.startButton) {
            if (!paused) {
                if (setTimer) {
                    // Create timer
                    formattedText += "7=1;";
                    formattedText += String.format(Locale.getDefault(), "8=%02d:%02d:%02d;", hour, minute, second);
                } else {
                    // Create stopwatch
                    formattedText += "7=2;";
                }
            }
            // Start timer or stopwatch
            formattedText += "9=1;";
            paused = false;
        } else if (id == R.id.pauseButton) {
            // Pause timer or stopwatch
            formattedText += "9=0;";
            paused = true;
        } else if (id == R.id.resetButton) {
            if (setTimer) {
                // Reset timer
                formattedText += String.format(Locale.getDefault(), "8=%02d:%02d:%02d;", hour, minute, second);
                paused = true;
            }
            // Reset timer on Arduino
            formattedText += "9=2;";
        } else if (id == R.id.stopButton) {
            formattedText += "9=3;";
        }

        // Send formatted text to connected device
        if (MainActivity.sendReceive != null) {
            if (MainActivity.sendReceive.isConnected()) {
                MainActivity.sendReceive.write(formattedText.getBytes());
            }
        }
    }

    // Method to apply stored preferences
    public void applyPrefs() {
        btConnectionTextView.setText(btConnectionTextView_text);
        switch (btConnectionTextView_text) {
            case "Not connected":
                btConnectionProgressBar.setVisibility(View.INVISIBLE);
                btConnectionImageView.setVisibility(View.VISIBLE);
                btConnectionImageView.setImageResource(R.drawable.baseline_bluetooth_disabled);
                break;
            case "Connecting...":
                btConnectionProgressBar.setVisibility(View.VISIBLE);
                btConnectionImageView.setVisibility(View.INVISIBLE);
                break;
            case "Connected":
                btConnectionProgressBar.setVisibility(View.INVISIBLE);
                btConnectionImageView.setVisibility(View.VISIBLE);
                btConnectionImageView.setImageResource(R.drawable.baseline_bluetooth_connected);
                break;
        }
    }

    // Method to set connection state
    public void setConnectinState(String state) {
        btConnectionTextView_text = state;

        try {
            applyPrefs();
        } catch (Exception ignored) {
        }
    }
}