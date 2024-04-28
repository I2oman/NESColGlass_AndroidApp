package com.example.nescolglass;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.example.nescolglass.Globals.*;
import static com.example.nescolglass.LocalStorage.getPrefs;
import static com.example.nescolglass.LocalStorage.putPrefs;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Set;

public class SettingsPage extends Fragment implements RecyclerViewInterface {
    private Context appContext;
    private Handler handler;
    private Button connecting_btn;
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

    public SettingsPage(Context context, Handler handler) {
        // Required empty public constructor
        this.appContext = context;
        this.handler = handler;
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

        return view;
    }

    // TODO: create onSaveInstanceState method to restore button color+text after reopening tab etc.
    //  implement applying settings from prefs on opening tab (Connect on startup checkbox)

    public void bondedDevices(View view) {
//        progressBar.setVisibility(View.INVISIBLE);
        ((MainActivity) getActivity()).disconnectDevice();

        if (cardView.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.INVISIBLE);
            cardView.setVisibility(View.INVISIBLE);
        } else {
//            ((MainActivity) getActivity()).accessPermission();
            devices.clear();
            setAddapter(devices);
            cardView.setVisibility(View.VISIBLE);
            setAddapter(devices = ((MainActivity) getActivity()).getbondedDevices());
//            Log.i("System.out.println()", String.valueOf(devices));
//            progressBar.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.INVISIBLE);
        }
    }


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
        putPrefs(CONSTART, constart_chkb.isChecked());
    }

    public void applyPrefs() {
        constart_chkb.setChecked(getPrefs(CONSTART, Boolean.class));
        if (constart_chkb.isChecked()) {
            for (BluetoothDevice device : ((MainActivity) getActivity()).getbondedDevices()) {
                if (device.getAddress().equals(getPrefs(LASTDEVADDR, String.class))) {
                    ((MainActivity) getActivity()).connectDevice(device);
                    break;
                }
            }
        }
    }

    public void showNotConnected() {
        connecting_btn.setText("Not connected");
        connecting_btn.setBackgroundColor(getResources().getColor(R.color.red));
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void showConnecting() {
        connecting_btn.setText("Connecting...");
        connecting_btn.setBackgroundColor(getResources().getColor(R.color.orange));
        progressBar.setVisibility(View.VISIBLE);
    }

    public void showConnected() {
        connecting_btn.setText("Connected");
        connecting_btn.setBackgroundColor(getResources().getColor(R.color.green));
        progressBar.setVisibility(View.INVISIBLE);
        cardView.setVisibility(View.INVISIBLE);
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
        if (((MainActivity) getActivity()).isConnectedDevice()) {
            MainActivity.sendReceive.write(formattedText.getBytes());
        }
    }
}