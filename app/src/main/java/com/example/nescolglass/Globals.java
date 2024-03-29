package com.example.nescolglass;

import java.util.UUID;

public class Globals {
    public static final int STATE_LISTENING = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_CONNECTION_FAILED = 4;
    public static final int STATE_MESSAGE_RECEIVED = 5;
    public static final int STATE_MESSAGE_SENT = 6;
    public static final int STATE_DISCONNECTED = 7;
    public static final int STATE_DISCONNECTED_SUCCESS = 8;
    public static final int STATE_DISCONNECTED_ERROR = 9;
    public static final String APP_NAME = "Remote Touchpad";
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
}
