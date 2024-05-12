package com.example.nescolglass.bluetooth;

import static com.example.nescolglass.Globals.*;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

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


    public SendReceive(BluetoothSocket socket, Handler handler) {
        this.handler = handler;
        this.bluetoothSocket = socket;
        InputStream tempIn = null;
        OutputStream tempOut = null;

        try {
            tempIn = bluetoothSocket.getInputStream();
        } catch (IOException e) {
//            e.printStackTrace();
//            Log.i("System.out.println()", "Error occurred when creating input stream", e);
            Message message = Message.obtain();
            message.what = INPUT_STREAM_FAIL;
            handler.sendMessage(message);
        }
        try {
            tempOut = socket.getOutputStream();
        } catch (IOException e) {
//            Log.i("System.out.println()", "Error occurred when creating output stream", e);
            Message message = Message.obtain();
            message.what = OUTPUT_STREAM_FAIL;
            handler.sendMessage(message);
        }

        inputStream = tempIn;
        outputStream = tempOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];
        int bytes; // bytes returned from read()

        startPingTimer();

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
//                Log.i("System.out.println()", "Input stream was disconnected", e);
                Message message = Message.obtain();
                message.what = INPUT_STREAM_DISCONNECT;
                handler.sendMessage(message);
                cancel();
                break;
            }
        }
    }

    private void startPingTimer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                final Handler pingHandler = new Handler();

                pingHandler.post(new Runnable() {
                    @Override
                    public void run() {
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


    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
            handler.obtainMessage(STATE_MESSAGE_SENT).sendToTarget();
        } catch (IOException e) {
//            e.printStackTrace();
//            Log.i("System.out.println()", "Error occurred when sending data", e);
            Message message = Message.obtain();
            message.what = SENDING_FAILURE;
            handler.sendMessage(message);
        }
    }

    public String getADDR() {
        if (bluetoothSocket != null) {
            return bluetoothSocket.getRemoteDevice().getAddress();
        } else {
            return "";
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
//            Log.i("System.out.println()", "Could not close the connect socket", e);
            Message message = Message.obtain();
            message.what = SOCKET_CLOSING_ERROR;
            handler.sendMessage(message);
        }
    }
}
