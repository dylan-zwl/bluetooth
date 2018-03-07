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

    public ConnectThread(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mSocket,
        // because mSocket is final
        BluetoothSocket tmp = null;
        mDevice = device;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
        } catch (IOException e) {
        }
        mSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mSocket.close();
            } catch (IOException closeException) {
            }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mSocket);
    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */
    public void close() {
        try {
            mSocket.close();
        } catch (IOException e) {
        }
    }
}