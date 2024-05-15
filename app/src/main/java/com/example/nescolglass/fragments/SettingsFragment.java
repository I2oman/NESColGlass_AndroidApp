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

    public CheckBox telegramCheckBox;
    public CheckBox whatsappCheckBox;
    public CheckBox teamsCheckBox;
    public CheckBox gmailCheckBox;
    public CheckBox outlookCheckBox;
    public CheckBox instagramCheckBox;
    public CheckBox messengerCheckBox;
    public CheckBox discordCheckBox;
    public CheckBox viberCheckBox;
    public CheckBox messagesCheckBox;
    public CheckBox phoneCheckBox;

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

        telegramCheckBox = view.findViewById(R.id.telegramCheckBox);
        telegramCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        whatsappCheckBox = view.findViewById(R.id.whatsappCheckBox);
        whatsappCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        teamsCheckBox = view.findViewById(R.id.teamsCheckBox);
        teamsCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        gmailCheckBox = view.findViewById(R.id.gmailCheckBox);
        gmailCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        outlookCheckBox = view.findViewById(R.id.outlookCheckBox);
        outlookCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        instagramCheckBox = view.findViewById(R.id.instagramCheckBox);
        instagramCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        messengerCheckBox = view.findViewById(R.id.messengerCheckBox);
        messengerCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        discordCheckBox = view.findViewById(R.id.discordCheckBox);
        discordCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        viberCheckBox = view.findViewById(R.id.viberCheckBox);
        viberCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        messagesCheckBox = view.findViewById(R.id.messagesCheckBox);
        messagesCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);
        phoneCheckBox = view.findViewById(R.id.phoneCheckBox);
        phoneCheckBox.setOnClickListener(this::appAlertCheckBoxVoid);

        applyPrefs();

        return view;
    }

    private void appAlertCheckBoxVoid(View view) {
        MainActivity.localStorage.putPrefs(SHTELEGRAM, !telegramCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHWHATSAPP, !whatsappCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHTEAMS, !teamsCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHGMAIL, !gmailCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHOUTLOOK, !outlookCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHINSTAGRAM, !instagramCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHMESSENGER, !messengerCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHDISCORD, !discordCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHVIBER, !viberCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHMESSAGES, !messagesCheckBox.isChecked());
        MainActivity.localStorage.putPrefs(SHPHONE, !phoneCheckBox.isChecked());
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
        if (MainActivity.localStorage.getPrefs(NOTIFICATIONTIMEOUT, Integer.class) >= 5) {
            seekBarValueTextView.setText("Seconds: " + MainActivity.localStorage.getPrefs(NOTIFICATIONTIMEOUT, Integer.class));
        } else {
            seekBarValueTextView.setText("Seconds: 5");
        }

        telegramCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHTELEGRAM, Boolean.class));
        whatsappCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHWHATSAPP, Boolean.class));
        teamsCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHTEAMS, Boolean.class));
        gmailCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHGMAIL, Boolean.class));
        outlookCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHOUTLOOK, Boolean.class));
        instagramCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHINSTAGRAM, Boolean.class));
        messengerCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHMESSENGER, Boolean.class));
        discordCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHDISCORD, Boolean.class));
        viberCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHVIBER, Boolean.class));
        messagesCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHMESSAGES, Boolean.class));
        phoneCheckBox.setChecked(!MainActivity.localStorage.getPrefs(SHPHONE, Boolean.class));
    }

    public void setConnectinState(String state) {
        connecting_btn_text = state;

        try {
            applyPrefs();
        } catch (Exception ignored) {
        }
    }
}