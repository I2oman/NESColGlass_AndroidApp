package com.example.nescolglass.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.nescolglass.MainActivity;
import com.example.nescolglass.R;

import java.util.Locale;

public class HomeFragment extends Fragment {
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
    private int hour, mimute;
    private boolean paused;

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

        Button stopwatch_button = view.findViewById(R.id.stopwatchButton1);
        stopwatch_button.setOnClickListener(this::stopwatchButtonClicked);
        Button timer_button = view.findViewById(R.id.timerButton1);
        timer_button.setOnClickListener(this::timerButtonCliked);
        Log.d("programstart", "program stared");

        applyPrefs();

        return view;
    }

    private void timerButtonVoid(View view) {
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

    private void stopwatchButtonVoid(View view) {
        stopwatchButton.setTextColor(getResources().getColor(R.color.white));
        stopwatchButton.setBackgroundColor(getResources().getColor(R.color.black));
        timerButton.setTextColor(getResources().getColor(R.color.gray));
        timerButton.setBackgroundColor(getResources().getColor(R.color.light_gray));
        timePickerButton.setTextColor(getResources().getColor(R.color.gray));
        timePickerButton.setBackgroundColor(getResources().getColor(R.color.light_gray));
        timePickerButton.setClickable(false);
        setTimer = false;
        paused = false;
    }

    private void timePickerBtn(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                hour = h;
                mimute = m;
                timePickerButton.setText(String.format(Locale.getDefault(), "00:%02d:%02d", hour, mimute));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), AlertDialog.THEME_HOLO_DARK, onTimeSetListener, hour, mimute, true);
        timePickerDialog.setTitle("Select time");
        timePickerDialog.show();
    }

    private void timerStopwatchPostRequest(View view1, int id) {
        String formattedText = "";
        if (id == R.id.startButton) {
            if (!paused) {
                if (setTimer) {
                    formattedText += "7=1;";
                    formattedText += String.format(Locale.getDefault(), "8=00:%02d:%02d;", hour, mimute);
                } else {
                    formattedText += "7=2;";
                }
            }
            formattedText += "9=1;";
            paused = false;
        } else if (id == R.id.pauseButton) {
            formattedText += "9=0;";
            paused = true;
        } else if (id == R.id.resetButton) {
            if (setTimer) {
                formattedText += String.format(Locale.getDefault(), "8=00:%02d:%02d;", hour, mimute);
                paused = true;
            }
            formattedText += "9=2;";
        } else if (id == R.id.stopButton) {
            formattedText += "9=3;";
        }

        Log.d("System.out.println();", formattedText);
        if (MainActivity.sendReceive != null) {
            if (MainActivity.sendReceive.isConnected()) {
                MainActivity.sendReceive.write(formattedText.getBytes());
            }
        }
    }

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

    public void setConnectinState(String state) {
        btConnectionTextView_text = state;

        try {
            applyPrefs();
        } catch (Exception ignored) {
        }
    }

    public void stopwatchButtonClicked(View view) {
        Dialog stopwatchDialog = new Dialog(view.getContext());
        stopwatchDialog.setContentView(R.layout.fragment_stopwatch);

        Button stopwatch_start_button = stopwatchDialog.findViewById(R.id.stopwatchStartButton);
        stopwatch_start_button.setOnClickListener(this::stopwatchStartButtonClicked);

        Button stopwatch_stop_button = stopwatchDialog.findViewById(R.id.stopwatchStopButton);
        stopwatch_stop_button.setOnClickListener(this::stopwatchStopButtonClicked);

        Button stopwatch_reset_button = stopwatchDialog.findViewById(R.id.stopwatchResetButton);
        stopwatch_reset_button.setOnClickListener(this::stopwatchResetButtonClicked);

        Button stopwatch_stopdelete_button = stopwatchDialog.findViewById(R.id.stopwatchStopDeleteButton);
        stopwatch_stopdelete_button.setOnClickListener(this::stopwatchStopDeleteButtonClicked);

        stopwatchDialog.show();

        Log.d("buttonClicked", "stopwatchButtonClicked");
    }

    // sends Start signal to glasses (Stopwatch)
    public void stopwatchStartButtonClicked(View view) {
        Log.d("stopwatchStartButtonClicked sending...", "timer start");
        MainActivity.sendReceive.write("7=1".getBytes());
    }

    // sends Stop signal to glasses (Stopwatch)
    public void stopwatchStopButtonClicked(View view) {
        Log.d("stopwatchStopButtonClicked sending...", "timer stop");
        MainActivity.sendReceive.write("7=0".getBytes());
    }

    // sends Reset signal to glasses (Stopwatch)
    public void stopwatchResetButtonClicked(View view) {
        Log.d("stopwatchResetButtonClicked sending...", "timer reset");
        MainActivity.sendReceive.write("7=2".getBytes());
    }

    // sends Stop & Delete signal to glasses (Stopwatch)
    public void stopwatchStopDeleteButtonClicked(View view) {
        Log.d("stopwatchStopDeleteButtonClicked sending...", "timer stop delete");
        MainActivity.sendReceive.write("7=3".getBytes());
    }


    // sends Start signal to glasses (Timer)
    public void timerStartButtonClicked(View view) {
        ViewParent parent = view.getParent();
        View dialogView = (View) parent;

        Spinner hourSelector = dialogView.findViewById(R.id.timerHourSelector);
        Spinner minuteSelector = dialogView.findViewById(R.id.timerMinuteSelector);
        Spinner secondSelector = dialogView.findViewById(R.id.timerSecondSelector);

        Log.d("timerStartButtonClicked hours", (String) hourSelector.getSelectedItem());
        Log.d("timerStartButtonClicked minutes", (String) minuteSelector.getSelectedItem());
        Log.d("timerStartButtonClicked secons", (String) secondSelector.getSelectedItem());

        Log.d("timerStartButtonClicked sending...", String.format("9=%s:%s:%s", (String) hourSelector.getSelectedItem(), minuteSelector.getSelectedItem(), secondSelector.getSelectedItem()));
        MainActivity.sendReceive.write(String.format("8=%s:%s:%s", (String) hourSelector.getSelectedItem(), minuteSelector.getSelectedItem(), secondSelector.getSelectedItem()).getBytes());
    }

    // sends Stop signal to glasses (Timer)
    public void timerStopButtonClicked(View view) {
        Log.d("timerStopButtonClicked sending...", "timer stop");
        MainActivity.sendReceive.write("9=0".getBytes());
    }

    // sends Reset signal to glasses (Timer)
    public void timerResetButtonClicked(View view) {
        Log.d("timerResetButtonClicked sending...", "timer reset");
        MainActivity.sendReceive.write("9=2".getBytes());
    }

    // sends Stop & Delete signal to glasses (Timer)
    public void timerStopDeleteButtonClicked(View view) {
        Log.d("timerStopDeleteButtonClicked sending...", "timer stop delete");
        MainActivity.sendReceive.write("9=3".getBytes());
    }

    public void timerButtonCliked(View view) {
        Dialog timerDialog = new Dialog(view.getContext());
        timerDialog.setContentView(R.layout.fragment_timer);

        Button timer_start_button = timerDialog.findViewById(R.id.timerStartButton);
        timer_start_button.setOnClickListener(this::timerStartButtonClicked);

        Button timer_stop_button = timerDialog.findViewById(R.id.timerStopButton);
        timer_stop_button.setOnClickListener(this::timerStopButtonClicked);

        Button timer_reset_button = timerDialog.findViewById(R.id.timerResetButton);
        timer_reset_button.setOnClickListener(this::timerResetButtonClicked);

        Button timer_stopdelete_button = timerDialog.findViewById(R.id.timerStopDeleteButton);
        timer_stopdelete_button.setOnClickListener(this::timerStopDeleteButtonClicked);

        Spinner hourSelector = timerDialog.findViewById(R.id.timerHourSelector);
        Spinner minuteSelector = timerDialog.findViewById(R.id.timerMinuteSelector);
        Spinner secondSelector = timerDialog.findViewById(R.id.timerSecondSelector);

        ArrayAdapter<CharSequence> hourSelectorAdapter = ArrayAdapter.createFromResource(timerDialog.getContext(), R.array.hoursArray, android.R.layout.simple_spinner_item);
        hourSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourSelector.setAdapter(hourSelectorAdapter);

        ArrayAdapter<CharSequence> minuteSelectorAdapter = ArrayAdapter.createFromResource(timerDialog.getContext(), R.array.minutesArray, android.R.layout.simple_spinner_item);
        minuteSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minuteSelector.setAdapter(minuteSelectorAdapter);

        ArrayAdapter<CharSequence> secondSelectorAdapter = ArrayAdapter.createFromResource(timerDialog.getContext(), R.array.secondsArray, android.R.layout.simple_spinner_item);
        secondSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        secondSelector.setAdapter(secondSelectorAdapter);
        Log.d("buttonClicked", "timerButtonCliked");

        Log.d("buttonClickedADDITIONAL hours", (String) hourSelector.getSelectedItem());
        Log.d("buttonClickedADDITIONAL minutes", (String) minuteSelector.getSelectedItem());
        Log.d("buttonClickedADDITIONAL secons", (String) secondSelector.getSelectedItem());

        timerDialog.show();
    }
}