package com.example.nescolglass.bluetooth;

import static com.example.nescolglass.Globals.*;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.example.nescolglass.MainActivity;

import java.io.IOException;

public class ClientClass extends Thread {
    // Bluetooth device and socket for client connection
    private static BluetoothDevice device;
    private static BluetoothSocket socket;
    public Handler handler;

    // Constructor to initialize the client class with the Bluetooth device and handler
    @SuppressLint("MissingPermission")
    public ClientClass(BluetoothDevice device, Handler handler) {
        this.device = device;
        this.handler = handler;

        try {
            // Create Bluetooth socket for the specified device
            socket = this.device.createRfcommSocketToServiceRecord(MY_UUID);

            // Send connecting state message to the handler
            Message message = Message.obtain();
            message.what = STATE_CONNECTING;
            handler.sendMessage(message);
        } catch (IOException e) {
            // Error handling if socket creation fails
            Message message = Message.obtain();
            message.what = SOCKET_FAIL;
            handler.sendMessage(message);
        }
    }

    // Method to handle the client thread's execution
    @SuppressLint("MissingPermission")
    public void run() {
        try {
            // Connect to the Bluetooth socket
            socket.connect();

            // Send connected state message to the handler
            Message message = Message.obtain();
            message.what = STATE_CONNECTED;
            handler.sendMessage(message);

            // Start SendReceive thread for data communication
            MainActivity.sendReceive = new SendReceive(socket, handler);
            MainActivity.sendReceive.start();
        } catch (IOException connectException) {
            // Error handling if socket connection fails
            Message message = Message.obtain();
            message.what = STATE_CONNECTION_FAILED;
            handler.sendMessage(message);
            try {
                // Close the socket on connection failure
                socket.close();
                message = Message.obtain();
                message.what = STATE_DISCONNECTED_SUCCESS;
                handler.sendMessage(message);
            } catch (IOException e) {
                // Error handling if socket closing fails
                message = Message.obtain();
                message.what = STATE_DISCONNECTED_ERROR;
                handler.sendMessage(message);
                e.printStackTrace();
            }
        }
    }

    // Method to cancel the client connection
    public void cancel() {
        try {
            // Close the Bluetooth socket
            socket.close();

            // Send disconnected state message to the handler
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
