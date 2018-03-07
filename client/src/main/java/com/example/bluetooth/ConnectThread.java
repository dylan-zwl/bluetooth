package com.example.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by dylan on 2018/3/7.
 */
public class ConnectThread extends Thread {
    private static final String MY_UUID = "5dd231bf-d217-4e85-a26c-5e5cfda9aa0c";
    private BluetoothAdapter mBluetoothAdapter;
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private ConnectManage mConnectManage;

    public ConnectThread(BluetoothDevice device) {
        mDevice = device;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        BluetoothSocket tmp = null;
        try {
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
        } catch (IOException e) {
        }
        mSocket = tmp;

        mConnectManage = new ConnectManage();
    }

    public void run() {
        mBluetoothAdapter.cancelDiscovery();
        try {
            mSocket.connect();
        } catch (IOException connectException) {
            try {
                mSocket.close();
            } catch (IOException closeException) {
            }
            return;
        }
        mConnectManage.init(mSocket);
        mConnectManage.read();
        mConnectManage.close();
    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */
    public void close() {
        try {
            mConnectManage.close();
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        interrupt();
        close();
    }

    public void setReciverListener(ConnectManage.ReceiveListener receiveListener) {
        mConnectManage.setReciverListener(receiveListener);
    }
}