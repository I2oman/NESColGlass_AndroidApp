package com.example.nescolglass.bluetooth;

import static com.example.nescolglass.Globals.*;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.nescolglass.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SendReceive extends Thread {
    private BluetoothSocket bluetoothSocket;
    private static InputStream inputStream;
    private static OutputStream outputStream;
    public Handler handler;

    // Constructor to initialize SendReceive with BluetoothSocket and handler
    public SendReceive(BluetoothSocket socket, Handler handler) {
        this.handler = handler;
        this.bluetoothSocket = socket;
        InputStream tempIn = null;
        OutputStream tempOut = null;

        try {
            tempIn = bluetoothSocket.getInputStream();
        } catch (IOException e) {
            // Error handling if input stream creation fails
            Message message = Message.obtain();
            message.what = INPUT_STREAM_FAIL;
            handler.sendMessage(message);
        }
        try {
            tempOut = socket.getOutputStream();
        } catch (IOException e) {
            // Error handling if output stream creation fails
            Message message = Message.obtain();
            message.what = OUTPUT_STREAM_FAIL;
            handler.sendMessage(message);
        }

        inputStream = tempIn;
        outputStream = tempOut;
    }

    // Method to handle SendReceive thread's execution
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes; // bytes returned from read()

        // Start ping timer and send initial settings
        startPingTimer();
        if (MainActivity.localStorage.getPrefs(SHTIMEONSTANDBY, Boolean.class)) {
            write("1=1;".getBytes());
        } else {
            write("1=0;".getBytes());
        }
        write(("2=" + MainActivity.localStorage.getPrefs(NOTIFICATIONTIMEOUT, Integer.class) * 1000 + ";").getBytes());

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                // Read from the InputStream.
                bytes = inputStream.read(buffer);
                // Send the obtained bytes to the handler.
                handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
            } catch (IOException e) {
                // Error handling if input stream is disconnected
                Message message = Message.obtain();
                message.what = INPUT_STREAM_DISCONNECT;
                handler.sendMessage(message);
                cancel();
                break;
            }
        }
    }

    // Method to start the ping timer
    private void startPingTimer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                final Handler pingHandler = new Handler();

                pingHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Send ping message every 15 seconds
                        if (isConnected()) {
                            String formattedText = "0=";
                            formattedText += new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(System.currentTimeMillis()));
                            formattedText += ";";
                            write(formattedText.getBytes());
                            pingHandler.postDelayed(this, 15 * 1000);
                        } else {
                            Looper.myLooper().quit();
                        }
                    }
                });
                Looper.loop();
            }
        }).start();
    }

    // Method to write data to the OutputStream
    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
            handler.obtainMessage(STATE_MESSAGE_SENT).sendToTarget();
        } catch (IOException e) {
            // Error handling if data sending fails
            Message message = Message.obtain();
            message.what = SENDING_FAILURE;
            handler.sendMessage(message);
        }
    }

    // Method to get the address of the connected Bluetooth device
    public String getADDR() {
        if (bluetoothSocket != null) {
            return bluetoothSocket.getRemoteDevice().getAddress();
        } else {
            return "";
        }
    }

    // Method to check if the Bluetooth socket is connected
    public boolean isConnected() {
        if (bluetoothSocket != null) {
            return bluetoothSocket.isConnected();
        } else {
            return false;
        }
    }

    // Method to cancel the Bluetooth connection
    public void cancel() {
        try {
            bluetoothSocket.close();

            Message message = Message.obtain();
            message.what = STATE_DISCONNECTED;
            handler.sendMessage(message);
        } catch (IOException e) {
            // Error handling if socket closing fails
            Message message = Message.obtain();
            message.what = SOCKET_CLOSING_ERROR;
            handler.sendMessage(message);
        }
    }
}
