package com.example.dylan.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView mTextView;

    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private AcceptThread mAcceptThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.recv_text);
        mHandler=new Handler();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
        startAcceptThread();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        startAcceptThread();
                        break;
                }
            }
        }, intentFilter);
    }

    private void startAcceptThread() {
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        mAcceptThread = new AcceptThread(mBluetoothAdapter);

        mAcceptThread.setReciverListener(new AcceptThread.ReceiveListener() {
            @Override
            public void data(final String text) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText(text);
                    }
                });
            }
        });

        mAcceptThread.start();
    }
}
