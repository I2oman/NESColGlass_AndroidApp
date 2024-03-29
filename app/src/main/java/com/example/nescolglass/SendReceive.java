package com.example.nescolglass;

import static com.example.nescolglass.Globals.STATE_DISCONNECTED;
import static com.example.nescolglass.Globals.STATE_MESSAGE_RECEIVED;
import static com.example.nescolglass.Globals.STATE_MESSAGE_SENT;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SendReceive extends Thread {
    private final BluetoothSocket bluetoothSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    public static Handler handler;


    public SendReceive(BluetoothSocket socket, Handler handler) {
        this.handler = handler;
        bluetoothSocket = socket;
        InputStream tempIn = null;
        OutputStream tempOut = null;

        try {
            tempIn = bluetoothSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("System.out.println()", "Error occurred when creating input stream", e);
        }
        try {
            tempOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.i("System.out.println()", "Error occurred when creating output stream", e);
        }

        inputStream = tempIn;
        outputStream = tempOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                // Read from the InputStream.
                bytes = inputStream.read(buffer);
                // Send the obtained bytes to the UI activity.
//                Log.i("System.out.println()", "bytes "+String.valueOf(bytes));
//                Log.i("System.out.println()", "buffer "+Arrays.toString(buffer));
                handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.i("System.out.println()", "Input stream was disconnected", e);
                break;
            }
        }
    }

    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
            handler.obtainMessage(STATE_MESSAGE_SENT).sendToTarget();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("System.out.println()", "Error occurred when sending data", e);
        }
    }

    public boolean isConnected() {
        if (bluetoothSocket != null) {
            return bluetoothSocket.isConnected();
        } else {
            return false;
        }
    }

    public void cancel() {
        try {
            bluetoothSocket.close();

            Message message = Message.obtain();
            message.what = STATE_DISCONNECTED;
            handler.sendMessage(message);
        } catch (IOException e) {
            Log.i("System.out.println()", "Could not close the connect socket", e);
        }
    }
}
