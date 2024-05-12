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
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nescolglass.LocalStorage;
import com.example.nescolglass.MainActivity;
import com.example.nescolglass.R;
import com.example.nescolglass.adapters.RecyclerViewInterface;
import com.example.nescolglass.adapters.ListAdapter;

import java.util.ArrayList;

public class SettingsFragment extends Fragment implements RecyclerViewInterface {
    private Context appContext;
    private LocalStorage localStorage;
    private Button connecting_btn;
    private String connecting_btn_text;
    private CheckBox constart_chkb;
    private CardView cardView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();
    private EditText textSize;
    private EditText xValue;
    private EditText yValue;
    private CheckBox inverColorCheckBox;
    private EditText textToDisplay;
    private Button sentBtn;

    public SettingsFragment(Context context, LocalStorage localStorage) {
        // Required empty public constructor
        this.appContext = context;
        this.localStorage = localStorage;

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
        cardView = view.findViewById(R.id.cardView);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        textSize = view.findViewById(R.id.textSizeET);
        xValue = view.findViewById(R.id.xValue);
        yValue = view.findViewById(R.id.yValue);
        inverColorCheckBox = view.findViewById(R.id.inverColorCheckBox);
        textToDisplay = view.findViewById(R.id.textToDisplay);
        sentBtn = view.findViewById(R.id.sentBtn);
        sentBtn.setOnClickListener(this::sent);

        applyPrefs();

        return view;
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
            setAddapter(devices);
            cardView.setVisibility(View.VISIBLE);
            setAddapter(devices = ((MainActivity) getActivity()).getbondedDevices());
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onItemClick(int position) {
        ((MainActivity) getActivity()).connectDevice(devices.get(position));
    }


    private void setAddapter(ArrayList<BluetoothDevice> list) {
        ListAdapter adapter = new ListAdapter(list, (RecyclerViewInterface) this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(appContext.getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    public void constart_void(View view) {
        localStorage.putPrefs(CONSTART, constart_chkb.isChecked());
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
        constart_chkb.setChecked(localStorage.getPrefs(CONSTART, Boolean.class));
    }

    public void setConnectinState(String state) {
        connecting_btn_text = state;

        try {
            applyPrefs();
        } catch (Exception ignored) {
        }
    }

    public void sent(View view) {
        String formattedText = "s:";
        formattedText += textSize.getText().toString();
        formattedText += ",c:";
        if (inverColorCheckBox.isChecked()) {
            formattedText += "1";
        } else {
            formattedText += "0";
        }
        formattedText += ",X:";
        formattedText += xValue.getText().toString();
        formattedText += ",Y:";
        formattedText += yValue.getText().toString();
        formattedText += ",t:";
        formattedText += textToDisplay.getText().toString();
        formattedText += ";";
        if (MainActivity.sendReceive != null) {
            if (MainActivity.sendReceive.isConnected()) {
                MainActivity.sendReceive.write(formattedText.getBytes());
            }
        }
    }
}