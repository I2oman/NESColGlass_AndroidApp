package com.example.nescolglass;

import static com.example.nescolglass.Globals.*;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.nescolglass.adapters.ViewPager2Adapter;
import com.example.nescolglass.bluetooth.ClientClass;
import com.example.nescolglass.bluetooth.SendReceive;
import com.example.nescolglass.fragments.AboutUsFragment;
import com.example.nescolglass.fragments.HomeFragment;
import com.example.nescolglass.fragments.MapsFragment;
import com.example.nescolglass.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public ViewPager2 viewPager2;
    private ArrayList<Fragment> fragments;
    private BottomNavigationView bottomNavigationView;
    public static LocalStorage localStorage;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    public static SendReceive sendReceive;
    public boolean requestedGeolocation;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize LocalStorage
        localStorage = new LocalStorage(this);

        viewPager2 = findViewById(R.id.viewPager2);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Initialize fragments
        fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new MapsFragment());
        fragments.add(new SettingsFragment());
        fragments.add(new AboutUsFragment());

        // Set up ViewPager2 with fragments
        ViewPager2Adapter viewPager2Adapter = new ViewPager2Adapter(this, fragments);
        viewPager2.setAdapter(viewPager2Adapter);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Update bottom navigation selection based on ViewPager2 page change
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.menu_item_home);
                        viewPager2.setUserInputEnabled(true);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.menu_item_maps);
                        viewPager2.setUserInputEnabled(false);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.menu_item_settings);
                        viewPager2.setUserInputEnabled(true);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.menu_item_about_us);
                        viewPager2.setUserInputEnabled(true);
                        break;
                }
                super.onPageSelected(position);
            }
        });
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);

        // Initialize Bluetooth adapter and manager
        bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Request necessary permissions and check for Bluetooth status
        accessPermission();

        // Apply preferences and restart or enable notification listener service
        applyPrefs();
        restartOrEnableNotificationListenerService();
    }

    // Handle bottom navigation item selection
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_item_home) {
            viewPager2.setCurrentItem(0);
        }
        if (menuItem.getItemId() == R.id.menu_item_maps) {
            viewPager2.setCurrentItem(1);
        }
        if (menuItem.getItemId() == R.id.menu_item_settings) {
            viewPager2.setCurrentItem(2);
        }
        if (menuItem.getItemId() == R.id.menu_item_about_us) {
            viewPager2.setCurrentItem(3);
        }
        return true;
    }

    // Restart or enable notification listener service if necessary
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

    // Check if the notification listener service is enabled
    private boolean isNotificationServiceEnabled() {
        ComponentName cn = new ComponentName(this, MyNotificationListenerService.class);
        String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        return flat != null && flat.contains(cn.flattenToString());
    }

    // Restart the notification listener service
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

    // Apply stored preferences
    private void applyPrefs() {
        if (localStorage.getPrefs(CONSTART, Boolean.class)) {
            for (BluetoothDevice device : getbondedDevices()) {
                if (device.getAddress().equals(localStorage.getPrefs(LASTDEVADDR, String.class))) {
                    connectDevice(device);
                    break;
                }
            }
        }
    }

    // Handler for various Bluetooth connection states
    public Handler handler = new Handler(msg -> {
        // Handle different message types received from the Bluetooth connection handler
        switch (msg.what) {
            case INPUT_STREAM_DISCONNECT:
                Log.w("System.out.println()", "Input stream was disconnected");
                ((HomeFragment) fragments.get(0)).setConnectinState("Not connected");
                ((SettingsFragment) fragments.get(2)).setConnectinState("Not connected");
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
                ((HomeFragment) fragments.get(0)).setConnectinState("Connecting...");
                ((SettingsFragment) fragments.get(2)).setConnectinState("Connecting...");
                break;
            case STATE_CONNECTED:
                Log.i("System.out.println()", "STATE_CONNECTED");
                Toast.makeText(getApplicationContext(), "Successfully connected", Toast.LENGTH_SHORT).show();
                ((HomeFragment) fragments.get(0)).setConnectinState("Connected");
                ((SettingsFragment) fragments.get(2)).setConnectinState("Connected");
                localStorage.putPrefs(LASTDEVADDR, sendReceive.getADDR());
                break;
            case STATE_CONNECTION_FAILED:
                Toast.makeText(getApplicationContext(), "Connection FAILED", Toast.LENGTH_SHORT).show();
                ((HomeFragment) fragments.get(0)).setConnectinState("Not connected");
                ((SettingsFragment) fragments.get(2)).setConnectinState("Not connected");
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
                ((HomeFragment) fragments.get(0)).setConnectinState("Not connected");
                ((SettingsFragment) fragments.get(2)).setConnectinState("Not connected");
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

    // Request necessary permissions
    @SuppressLint("MissingPermission")
    public void accessPermission() {
        // Request permissions based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        // Check if Bluetooth is enabled, if not prompt user to enable it
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }

        // Check if location services are enabled, if not prompt user to enable them (only once)
        if (!requestedGeolocation) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Intent enableLocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(enableLocationIntent);
            }
            requestedGeolocation = true;
        }
    }

    // Retrieve bonded Bluetooth devices
    @SuppressLint("MissingPermission")
    public ArrayList<BluetoothDevice> getbondedDevices() {
        ArrayList<BluetoothDevice> bondedDevices = new ArrayList<BluetoothDevice>();

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            bondedDevices.addAll(pairedDevices);
        }

        return bondedDevices;
    }

    // Connect to a Bluetooth device
    @SuppressLint("MissingPermission")
    public void connectDevice(BluetoothDevice selected_dev) {
        Toast.makeText(getApplicationContext(),
                "Connecting to \"" + selected_dev.getName() + "\" - " + selected_dev.getAddress(),
                Toast.LENGTH_SHORT).show();
        ClientClass clientClass = new ClientClass(selected_dev, handler);
        clientClass.start();
    }
}