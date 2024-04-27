package com.example.nescolglass;

import static com.example.nescolglass.Globals.*;
import static com.example.nescolglass.LocalStorage.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface {
    private LocalStorage localStorage;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    public static SendReceive sendReceive;
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

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localStorage = new LocalStorage(this);

        connecting_btn = findViewById(R.id.connecting_btn);
        constart_chkb = findViewById(R.id.constart_chkb);
        cardView = findViewById(R.id.cardView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        textSize = findViewById(R.id.textSizeET);
        xValue = findViewById(R.id.xValue);
        yValue = findViewById(R.id.yValue);
        inverColorCheckBox = findViewById(R.id.inverColorCheckBox);
        textToDisplay = findViewById(R.id.textToDisplay);
        sentBtn = findViewById(R.id.sentBtn);

        bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();

//        Log.i("System.out.println()", bluetoothAdapter.getAddress());

        accessPermission();

        applyPrefs();
        setAddapter(devices);

//        Log.i("System.out.println()", String.valueOf(MY_UUID));
//        showPrefs();

        restartOrEnableNotificationListenerService();
    }

    private void restartOrEnableNotificationListenerService() {
        if (!isNotificationServiceEnabled()) {
            // If the notification listener service is not enabled, launch settings to enable it
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        } else {
            // If the notification listener service is enabled, restart it
            restartNotificationListenerService();
        }
    }

    private boolean isNotificationServiceEnabled() {
        ComponentName cn = new ComponentName(this, MyNotificationListenerService.class);
        String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        return flat != null && flat.contains(cn.flattenToString());
    }

    private void restartNotificationListenerService() {
        // Disable the notification listener service
        ComponentName componentName = new ComponentName(this, MyNotificationListenerService.class);
        getPackageManager().setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        // Re-enable the notification listener service
        getPackageManager().setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }


    private void applyPrefs() {
        constart_chkb.setChecked(getPrefs(CONSTART, Boolean.class));
        if (constart_chkb.isChecked()) {
            for (BluetoothDevice device : getbondedDevices()) {
                if (device.getAddress().equals(getPrefs(LASTDEVADDR, String.class))) {
                    connectDevice(device);
                    break;
                }
            }
        }
    }

    public Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case INPUT_STREAM_DISCONNECT:
                Log.w("System.out.println()", "Input stream was disconnected");
                connecting_btn.setBackgroundColor(getColor(R.color.red));
                connecting_btn.setText("Not connected");
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case INPUT_STREAM_FAIL:
                Log.e("System.out.println()", "Error occurred when creating input stream");
                break;
            case OUTPUT_STREAM_FAIL:
                Log.e("System.out.println()", "Error occurred when creating output stream");
                break;
            case SOCKET_FAIL:
                Log.e("System.out.println()", "Socket's create() method failed");
                break;
            case STATE_LISTENING:
                Log.i("System.out.println()", "STATE_LISTENING");
                break;
            case STATE_CONNECTING:
                Log.i("System.out.println()", "STATE_CONNECTING");
                connecting_btn.setText("Connecting...");
                connecting_btn.setBackgroundColor(getColor(R.color.orange));
                progressBar.setVisibility(View.VISIBLE);
                break;
            case STATE_CONNECTED:
                Log.i("System.out.println()", "STATE_CONNECTED");
                Toast.makeText(getApplicationContext(), "Successfully connected", Toast.LENGTH_SHORT).show();
                connecting_btn.setText("Connected");
                connecting_btn.setBackgroundColor(getColor(R.color.green));
                progressBar.setVisibility(View.INVISIBLE);
                cardView.setVisibility(View.INVISIBLE);
                putPrefs(LASTDEVADDR, sendReceive.getADDR());
//                putPrefs(LASTDEVADDR, sendReceive.getName());
                break;
            case STATE_CONNECTION_FAILED:
                Toast.makeText(getApplicationContext(), "Connection FAILED", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                connecting_btn.setText("Not connected");
                connecting_btn.setBackgroundColor(getColor(R.color.red));
                Log.e("System.out.println()", "STATE_CONNECTION_FAILED");
                break;
            case SENDING_FAILURE:
                Log.e("System.out.println()", "Error occurred when sending data");
                break;
            case STATE_MESSAGE_RECEIVED:
                byte[] readBuff = (byte[]) msg.obj;
                String tempMsg = new String(readBuff, 0, msg.arg1);
                Log.i("System.out.println()", "STATE_MESSAGE_RECEIVED: " + tempMsg);
                break;
            case STATE_MESSAGE_SENT:
                Log.i("System.out.println()", "STATE_MESSAGE_SENT");
                break;
            case STATE_DISCONNECTED:
                Log.i("System.out.println()", "STATE_DISCONNECTED");
                connecting_btn.setText("Not connected");
                connecting_btn.setBackgroundColor(getColor(R.color.red));
                break;
            case STATE_DISCONNECTED_SUCCESS:
                Log.i("System.out.println()", "STATE_DISCONNECTED_SUCCESS");
                break;
            case STATE_DISCONNECTED_ERROR:
                Log.e("System.out.println()", "STATE_DISCONNECTED_ERROR");
                break;
            case SOCKET_CLOSING_ERROR:
                Log.e("System.out.println()", "Could not close the client socket");
                break;
        }
        return true;
    });


    public void bondedDevices(View view) {
//        progressBar.setVisibility(View.INVISIBLE);
        if (sendReceive != null) {
            if (sendReceive.isConnected()) {
                sendReceive.cancel();
                return;
            }
        }

        if (cardView.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.INVISIBLE);
            cardView.setVisibility(View.INVISIBLE);
        } else {
            accessPermission();
            devices.clear();
            setAddapter(devices);
            cardView.setVisibility(View.VISIBLE);
            setAddapter(devices = getbondedDevices());
//            Log.i("System.out.println()", String.valueOf(devices));
//            progressBar.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("MissingPermission")
    private ArrayList<BluetoothDevice> getbondedDevices() {
        ArrayList<BluetoothDevice> bondedDevices = new ArrayList<BluetoothDevice>();

        bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            bondedDevices.addAll(pairedDevices);
        }

        return bondedDevices;
    }

    @Override
    public void onItemClick(int position) {
        connectDevice(devices.get(position));
    }

    @SuppressLint("MissingPermission")
    private void connectDevice(BluetoothDevice selected_dev) {
        Toast.makeText(getApplicationContext(),
                "Connecting to \"" + selected_dev.getName() + "\" - " + selected_dev.getAddress(),
                Toast.LENGTH_SHORT).show();
        ClientClass clientClass = new ClientClass(selected_dev, handler);
        clientClass.start();
    }

    private void setAddapter(ArrayList<BluetoothDevice> list) {
        ListAdapter adapter = new ListAdapter(list, (RecyclerViewInterface) this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("MissingPermission")
    private void accessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE

            }, 100);
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
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
        if (sendReceive.isConnected()) {
            sendReceive.write(formattedText.getBytes());
        }
    }

    public void constart_void(View view) {
        putPrefs(CONSTART, constart_chkb.isChecked());
    }
}