package com.example.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by dylan on 2018/3/7.
 */

public class ConnectedThread {
    private static String TAG = ConnectedThread.class.getSimpleName();
    private final BluetoothSocket mSocket;
    private final InputStream mInputStream;
    private final OutputStream mOutputStream;

    public ConnectedThread(BluetoothSocket socket) {
        mSocket = socket;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "temp sockets not created", e);
        }

        mInputStream = inputStream;
        mOutputStream = outputStream;
    }

    public void run() {
        Log.i(TAG, "BEGIN mConnectedThread");
        StringBuilder recvData = new StringBuilder();
        byte[] buffer = new byte[128];
        try {
            while (true) {
                int count = 0;
                count = mInputStream.read(buffer);
                if (count > 0) {
                    byte[] temp = new byte[count];
                    System.arraycopy(buffer, 0, temp, 0, count);
                    recvData.append(new String(temp));
                }
                if (recvData.toString().endsWith("\0")) {
                    if (mReceiveListener != null) {
                        mReceiveListener.data(recvData.toString());
                    }
                    Log.d(TAG, "reciver data : " + recvData.toString());
                    recvData.setLength(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        close();
    }

    /**
     * Write to the connected OutStream.
     *
     * @param buffer The bytes to write
     */
    public void write(byte[] buffer) {
        try {
            mOutputStream.write(buffer);
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }

    public void close() {
        try {
            if (mInputStream != null) {
                mInputStream.close();
            }
            if (mOutputStream != null) {
                mOutputStream.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "close() of connect socket failed", e);
        }
    }

    private ReceiveListener mReceiveListener;

    public void setReciverListener(ReceiveListener receiveListener) {
        this.mReceiveListener = receiveListener;
    }

    public interface ReceiveListener {
        void data(String text);
    }
}
