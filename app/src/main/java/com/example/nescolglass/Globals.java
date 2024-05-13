package com.example.nescolglass;

import java.util.UUID;

public class Globals {
    public static final int INPUT_STREAM_DISCONNECT = -3;
    public static final int INPUT_STREAM_FAIL = -2;
    public static final int OUTPUT_STREAM_FAIL = -1;
    public static final int SOCKET_FAIL = 0;
    public static final int STATE_LISTENING = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_CONNECTION_FAILED = 4;
    public static final int SENDING_FAILURE = -4;
    public static final int STATE_MESSAGE_RECEIVED = 5;
    public static final int STATE_MESSAGE_SENT = 6;
    public static final int STATE_DISCONNECTED = 7;
    public static final int STATE_DISCONNECTED_SUCCESS = 8;
    public static final int STATE_DISCONNECTED_ERROR = 9;
    public static final int SOCKET_CLOSING_ERROR = 10;
    public static final String APP_NAME = "NESCOl_Glass";
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String SHARED_PREFS = "NSG_SHARED_PREFERENCES";
    public static final String CONSTART = "CONNECT_ON_STARTUP";
    public static final String LASTDEVADDR = "LAST_CONNECTED_DEVICE_ADDRESS";
    public static final String SHTIMEONSTANDBY = "SHOW_TIME_ON_STANDBY";
    public static final String NOTIFICATIONTIMEOUT = "NOTIFICATION_TIMEOUT";
}
