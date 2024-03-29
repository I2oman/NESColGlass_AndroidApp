package com.example.nescolglass;

import static com.example.nescolglass.Globals.STATE_CONNECTED;
import static com.example.nescolglass.Globals.STATE_CONNECTING;
import static com.example.nescolglass.Globals.STATE_CONNECTION_FAILED;
import static com.example.nescolglass.Globals.STATE_DISCONNECTED;
import static com.example.nescolglass.Globals.STATE_DISCONNECTED_ERROR;
import static com.example.nescolglass.Globals.STATE_DISCONNECTED_SUCCESS;
import static com.example.nescolglass.Globals.STATE_LISTENING;
import static com.example.nescolglass.Globals.STATE_MESSAGE_RECEIVED;
import static com.example.nescolglass.Globals.STATE_MESSAGE_SENT;
import static com.example.nescolglass.Globals.MY_UUID;

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
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    public static SendReceive sendReceive;
    private Button connecting_btn;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("System.out.println()", "String.valueOf(MY_UUID)");

        connecting_btn = findViewById(R.id.connecting_btn);
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

        accessPermission();

        setAddapter(devices);

        Log.i("System.out.println()", String.valueOf(MY_UUID));
    }

    Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case STATE_LISTENING:
                Log.i("System.out.println()", "STATE_LISTENING");
                break;
            case STATE_CONNECTING:
                Log.i("System.out.println()", "STATE_CONNECTING");
                break;
            case STATE_CONNECTED:
                Log.i("System.out.println()", "STATE_CONNECTED");
                connecting_btn.setBackgroundColor(getColor(R.color.green));
                progressBar.setVisibility(View.INVISIBLE);
                cardView.setVisibility(View.INVISIBLE);
                break;
            case STATE_CONNECTION_FAILED:
                Toast.makeText(getApplicationContext(), "Connection FAILED", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                Log.i("System.out.println()", "STATE_CONNECTION_FAILED");
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
                connecting_btn.setBackgroundColor(getColor(R.color.red));
                break;
            case STATE_DISCONNECTED_SUCCESS:
                Log.i("System.out.println()", "STATE_DISCONNECTED_SUCCESS");
                break;
            case STATE_DISCONNECTED_ERROR:
                Log.i("System.out.println()", "STATE_DISCONNECTED_ERROR");
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
            for (BluetoothDevice device : pairedDevices) {
                bondedDevices.add(device);
            }
        }

        return bondedDevices;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onItemClick(int position) {
        BluetoothDevice sel_dev = devices.get(position);
        Toast.makeText(getApplicationContext(),
                "Connecting to \"" + sel_dev.getName() + "\" - " + sel_dev.getAddress(),
                Toast.LENGTH_SHORT).show();
        ClientClass clientClass = new ClientClass(sel_dev, handler);
        clientClass.start();
        progressBar.setVisibility(View.VISIBLE);
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
                    Manifest.permission.BLUETOOTH_SCAN

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
        }else{
            formattedText += "0";
        }
        formattedText += ",X:";
        formattedText += xValue.getText().toString();
        formattedText += ",Y:";
        formattedText += yValue.getText().toString();
        formattedText += ",t:";
        formattedText += textToDisplay.getText().toString();
        formattedText += ";";
        sendReceive.write(formattedText.getBytes());
    }
}