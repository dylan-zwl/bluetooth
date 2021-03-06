package com.example.dylan.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by dylan on 2018/3/5.
 */

public class AcceptThread extends Thread {
    private static String TAG = AcceptThread.class.getSimpleName();
    private BluetoothServerSocket mServerSocket;
    private BluetoothSocket mSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;

    public AcceptThread(BluetoothAdapter bluetoothAdapter) {
        //创建BluetoothServerSocket对象
        try {
            mServerSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("name", UUID.fromString
                    ("5dd231bf-d217-4e85-a26c-5e5cfda9aa0c"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            mSocket = mServerSocket.accept();
        } catch (IOException connectException) {
            try {
                mSocket.close();
            } catch (IOException closeException) {
            }
            return;
        }
        try {
            Log.d(TAG, "bluetooth mSocket connect");
            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();
            StringBuilder recvData = new StringBuilder();
            byte[] buffer = new byte[128];
            while (true) {
                int count = mInputStream.read(buffer);
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
        Log.d(TAG, "close");
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

    /**
     * Will close the listening mSocket, and cause the thread to finish
     */
    public void close() {
        try {
            if (mInputStream != null) {
                mInputStream.close();
            }
            if (mOutputStream != null) {
                mOutputStream.close();
            }
            if (mServerSocket != null) {
                mServerSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        interrupt();
        close();
    }

    private ReceiveListener mReceiveListener;

    public void setReciverListener(ReceiveListener receiveListener) {
        this.mReceiveListener = receiveListener;
    }

    public interface ReceiveListener {
        void data(String text);
    }
}
