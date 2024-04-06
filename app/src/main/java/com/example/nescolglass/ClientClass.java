package com.example.nescolglass;

//import static com.example.nescolglass.Globals.MY_UUID;
import static com.example.nescolglass.Globals.MY_UUID;
import static com.example.nescolglass.Globals.STATE_CONNECTED;
import static com.example.nescolglass.Globals.STATE_CONNECTION_FAILED;
import static com.example.nescolglass.Globals.STATE_DISCONNECTED;
import static com.example.nescolglass.Globals.STATE_DISCONNECTED_ERROR;
import static com.example.nescolglass.Globals.STATE_DISCONNECTED_SUCCESS;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

public class ClientClass extends Thread {
    private static BluetoothDevice device;
    private static BluetoothSocket socket;
    public Handler handler;

    @SuppressLint("MissingPermission")
    public ClientClass(BluetoothDevice device, Handler handler) {
        this.device = device;
        this.handler = handler;

        try {
            socket = this.device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.i("System.out.println()", "Socket's create() method failed", e);
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void run() {
        try {
            socket.connect();

            Message message = Message.obtain();
            message.what = STATE_CONNECTED;
            handler.sendMessage(message);

            MainActivity.sendReceive = new SendReceive(socket, handler);
            MainActivity.sendReceive.start();
        } catch (IOException connectException) {
            Log.i("System.out.println()", String.valueOf(connectException));
            Message message = Message.obtain();
            message.what = STATE_CONNECTION_FAILED;
            handler.sendMessage(message);
            try {
                socket.close();
                message = Message.obtain();
                message.what = STATE_DISCONNECTED_SUCCESS;
                handler.sendMessage(message);
            } catch (IOException e) {
                message = Message.obtain();
                message.what = STATE_DISCONNECTED_ERROR;
                handler.sendMessage(message);
                e.printStackTrace();
            }
        }
    }


//    public boolean isConnected() {
//        if (socket != null) {
//            return socket.isConnected();
//        } else {
//            return false;
//        }
//    }

    public void cancel() {
        try {
            socket.close();

            Message message = Message.obtain();
            message.what = STATE_DISCONNECTED;
            handler.sendMessage(message);
        } catch (IOException e) {
            Log.i("System.out.println()", "Could not close the client socket", e);
        }
    }
}
