package com.example.nescolglass;

import static com.example.nescolglass.Globals.INPUT_STREAM_DISCONNECT;
import static com.example.nescolglass.Globals.INPUT_STREAM_FAIL;
import static com.example.nescolglass.Globals.LASTDEVADDR;
import static com.example.nescolglass.Globals.OUTPUT_STREAM_FAIL;
import static com.example.nescolglass.Globals.SENDING_FAILURE;
import static com.example.nescolglass.Globals.SOCKET_CLOSING_ERROR;
import static com.example.nescolglass.Globals.SOCKET_FAIL;
import static com.example.nescolglass.Globals.STATE_CONNECTED;
import static com.example.nescolglass.Globals.STATE_CONNECTING;
import static com.example.nescolglass.Globals.STATE_CONNECTION_FAILED;
import static com.example.nescolglass.Globals.STATE_DISCONNECTED;
import static com.example.nescolglass.Globals.STATE_DISCONNECTED_ERROR;
import static com.example.nescolglass.Globals.STATE_DISCONNECTED_SUCCESS;
import static com.example.nescolglass.Globals.STATE_LISTENING;
import static com.example.nescolglass.Globals.STATE_MESSAGE_RECEIVED;
import static com.example.nescolglass.Globals.STATE_MESSAGE_SENT;
import static com.example.nescolglass.LocalStorage.putPrefs;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.nescolglass.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private LocalStorage localStorage;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    public static SendReceive sendReceive;
    //    private Button connecting_btn;
//    private CheckBox constart_chkb;
//    private CardView cardView;
//    private ProgressBar progressBar;
//    private RecyclerView recyclerView;
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();
//    private EditText textSize;
//    private EditText xValue;
//    private EditText yValue;
//    private CheckBox inverColorCheckBox;
//    private EditText textToDisplay;
//    private Button sentBtn;

    private SettingsPage settingsPage;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingsPage = new SettingsPage(this, handler);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomePage());

        localStorage = new LocalStorage(this);
//        showPrefs();

//        connecting_btn = findViewById(R.id.connecting_btn);
//        constart_chkb = findViewById(R.id.constart_chkb);
//        cardView = findViewById(R.id.cardView);
//        progressBar = findViewById(R.id.progressBar);
//        recyclerView = findViewById(R.id.recyclerView);
//        textSize = findViewById(R.id.textSizeET);
//        xValue = findViewById(R.id.xValue);
//        yValue = findViewById(R.id.yValue);
//        inverColorCheckBox = findViewById(R.id.inverColorCheckBox);
//        textToDisplay = findViewById(R.id.textToDisplay);
//        sentBtn = findViewById(R.id.sentBtn);
//
        bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();

        accessPermission();

        applyPrefs();
//        setAddapter(devices);

        restartOrEnableNotificationListenerService();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_item_home) {
                replaceFragment(new HomePage());
                return true;
            } else if ((item.getItemId() == R.id.menu_item_profile)) {
                replaceFragment(new ProfilePage());
                return true;
            } else if ((item.getItemId() == R.id.menu_item_settings)) {
                replaceFragment(settingsPage);
                return true;
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
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
//        constart_chkb.setChecked(getPrefs(CONSTART, Boolean.class));
//        if (constart_chkb.isChecked()) {
//            for (BluetoothDevice device : getbondedDevices()) {
//                if (device.getAddress().equals(getPrefs(LASTDEVADDR, String.class))) {
//                    connectDevice(device);
//                    break;
//                }
//            }
//        }
//        settingsPage.applyPrefs();
    }

    public Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case INPUT_STREAM_DISCONNECT:
                Log.w("System.out.println()", "Input stream was disconnected");
                settingsPage.showNotConnected();
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
                settingsPage.showConnecting();
                break;
            case STATE_CONNECTED:
                Log.i("System.out.println()", "STATE_CONNECTED");
                Toast.makeText(getApplicationContext(), "Successfully connected", Toast.LENGTH_SHORT).show();
                settingsPage.showConnected();
                putPrefs(LASTDEVADDR, sendReceive.getADDR());
//                putPrefs(LASTDEVADDR, sendReceive.getName());
                break;
            case STATE_CONNECTION_FAILED:
                Toast.makeText(getApplicationContext(), "Connection FAILED", Toast.LENGTH_SHORT).show();
                settingsPage.showNotConnected();
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
                settingsPage.showNotConnected();
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

    @SuppressLint("MissingPermission")
    public void accessPermission() {
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

    @SuppressLint("MissingPermission")
    public ArrayList<BluetoothDevice> getbondedDevices() {
        ArrayList<BluetoothDevice> bondedDevices = new ArrayList<BluetoothDevice>();

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            bondedDevices.addAll(pairedDevices);
        }

        return bondedDevices;
    }

    @SuppressLint("MissingPermission")
    public void connectDevice(BluetoothDevice selected_dev) {
        Toast.makeText(getApplicationContext(),
                "Connecting to \"" + selected_dev.getName() + "\" - " + selected_dev.getAddress(),
                Toast.LENGTH_SHORT).show();
        ClientClass clientClass = new ClientClass(selected_dev, handler);
        clientClass.start();
    }

    public boolean isConnectedDevice() {
        if (sendReceive != null) {
            if (sendReceive.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public void disconnectDevice() {
        if (sendReceive != null) {
            if (sendReceive.isConnected()) {
                sendReceive.cancel();
            }
        }
    }
}