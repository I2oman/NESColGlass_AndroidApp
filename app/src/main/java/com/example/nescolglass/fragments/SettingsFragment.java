package com.example.nescolglass.fragments;

import static com.example.nescolglass.Globals.*;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nescolglass.MainActivity;
import com.example.nescolglass.R;
import com.example.nescolglass.adapters.RecyclerViewInterface;
import com.example.nescolglass.adapters.ListAdapter;

import java.util.ArrayList;

public class SettingsFragment extends Fragment implements RecyclerViewInterface {
    private Button connecting_btn;
    private CheckBox constart_chkb;
    private CheckBox shTimeOnStandByCheckBox;
    private SeekBar notificationTimeoutSeekBar;
    private TextView seekBarValueTextView;
    private CardView cardView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();
    private String connecting_btn_text;

    public SettingsFragment() {
        connecting_btn_text = "Not connected";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        connecting_btn = view.findViewById(R.id.connecting_btn);
        connecting_btn.setOnClickListener(this::bondedDevices);
        constart_chkb = view.findViewById(R.id.constart_chkb);
        constart_chkb.setOnClickListener(this::constart_void);
        shTimeOnStandByCheckBox = view.findViewById(R.id.shTimeOnStandByCheckBox);
        shTimeOnStandByCheckBox.setOnClickListener(this::shTimeOnStandBy);
        notificationTimeoutSeekBar = view.findViewById(R.id.notificationTimeoutSeekBar);
        notificationTimeoutSeekBar.setMin(5);
        notificationTimeoutSeekBar.setMax(60);
        seekBarValueTextView = view.findViewById(R.id.seekBarValueTextView);
        notificationTimeoutSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the TextView with the current progress
                seekBarValueTextView.setText("Seconds: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No action needed here
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MainActivity.localStorage.putPrefs(NOTIFICATIONTIMEOUT, seekBar.getProgress());
                if (MainActivity.sendReceive != null) {
                    if (MainActivity.sendReceive.isConnected()) {
                        MainActivity.sendReceive.write(("2=" + seekBar.getProgress() * 1000 + ";").getBytes());
                    }
                }
            }
        });
        cardView = view.findViewById(R.id.cardView);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);

        applyPrefs();

        return view;
    }

    private void shTimeOnStandBy(View view) {
        MainActivity.localStorage.putPrefs(SHTIMEONSTANDBY, shTimeOnStandByCheckBox.isChecked());
        if (MainActivity.sendReceive != null) {
            if (MainActivity.sendReceive.isConnected()) {
                if (shTimeOnStandByCheckBox.isChecked()) {
                    MainActivity.sendReceive.write("1=1;".getBytes());
                } else {
                    MainActivity.sendReceive.write("1=0;".getBytes());
                }
            }
        }
    }

    public void bondedDevices(View view) {
        if (MainActivity.sendReceive != null) {
            if (MainActivity.sendReceive.isConnected()) {
                MainActivity.sendReceive.cancel();
                return;
            }
        }
        if (cardView.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.INVISIBLE);
            cardView.setVisibility(View.INVISIBLE);
        } else {
            ((MainActivity) getActivity()).accessPermission();
            devices.clear();
            setAddapter(devices, view.getContext());
            cardView.setVisibility(View.VISIBLE);
            setAddapter(devices = ((MainActivity) getActivity()).getbondedDevices(), view.getContext());
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onItemClick(int position) {
        ((MainActivity) getActivity()).connectDevice(devices.get(position));
    }


    private void setAddapter(ArrayList<BluetoothDevice> list, Context context) {
        ListAdapter adapter = new ListAdapter(list, (RecyclerViewInterface) this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    public void constart_void(View view) {
        MainActivity.localStorage.putPrefs(CONSTART, constart_chkb.isChecked());
    }

    public void applyPrefs() {
        connecting_btn.setText(connecting_btn_text);
        switch (connecting_btn_text) {
            case "Not connected":
                connecting_btn.setBackgroundColor(getResources().getColor(R.color.red));
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case "Connecting...":
                connecting_btn.setBackgroundColor(getResources().getColor(R.color.orange));
                progressBar.setVisibility(View.VISIBLE);
                break;
            case "Connected":
                connecting_btn.setBackgroundColor(getResources().getColor(R.color.green));
                progressBar.setVisibility(View.INVISIBLE);
                cardView.setVisibility(View.INVISIBLE);
                break;
        }
        constart_chkb.setChecked(MainActivity.localStorage.getPrefs(CONSTART, Boolean.class));
        shTimeOnStandByCheckBox.setChecked(MainActivity.localStorage.getPrefs(SHTIMEONSTANDBY, Boolean.class));
        notificationTimeoutSeekBar.setProgress(MainActivity.localStorage.getPrefs(NOTIFICATIONTIMEOUT, Integer.class));
        seekBarValueTextView.setText("Seconds: " + MainActivity.localStorage.getPrefs(NOTIFICATIONTIMEOUT, Integer.class));
    }

    public void setConnectinState(String state) {
        connecting_btn_text = state;

        try {
            applyPrefs();
        } catch (Exception ignored) {
        }
    }
}