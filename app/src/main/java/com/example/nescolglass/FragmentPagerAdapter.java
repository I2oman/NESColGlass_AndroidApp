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


import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentPagerAdapter extends FragmentStateAdapter {
    private SettingsPage settingsPage;

    public FragmentPagerAdapter(@NonNull FragmentActivity fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new HomePage();
            case 1:
                return new MapsPage();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
