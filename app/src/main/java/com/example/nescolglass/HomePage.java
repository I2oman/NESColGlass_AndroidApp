package com.example.nescolglass;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class HomePage extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"MissingInflatedId", "ResourceType"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button stopwatch_button = view.findViewById(R.id.stopwatchButton);
        stopwatch_button.setOnClickListener(this::stopwatchButtonClicked);

        Button timer_button = view.findViewById(R.id.timerButton);
        timer_button.setOnClickListener(this::timerButtonCliked);
        Log.d("programstart", "program stared");

        return view;
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
