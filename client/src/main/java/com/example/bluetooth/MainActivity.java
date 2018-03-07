package com.example.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int count = 0;
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//得到默认的蓝牙适配器
        Set<BluetoothDevice> paireDevices = bluetoothAdapter.getBondedDevices();//得到已经绑定的蓝牙设备
        if (paireDevices.size() > 0) {//若存在
            String[] data = new String[paireDevices.size()];
            for (BluetoothDevice bluetoothDevice : paireDevices) {
                data[count++] = bluetoothDevice.getName() + ":" + bluetoothDevice.getAddress();//得到绑定蓝牙设备的名称和地址
            }
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout
                    .simple_expandable_list_item_1, data);
            mListView = (ListView) findViewById(R.id.list_view);
            mListView.setAdapter(adapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub
                    String s = adapter.getItem(position);
                    //获得要连接的蓝牙设备的地址
                    String address = s.substring(s.indexOf(":") + 1).trim();

                    BluetoothSocket clientSocket = null;
                    BluetoothDevice device;
                    //获得蓝牙设备，相当于网路客户端制定的socketip地址
                    device = bluetoothAdapter.getRemoteDevice(address);
                    OutputStream os = null;
                    try {
                        clientSocket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString
                                ("5dd231bf-d217-4e85-a26c-5e5cfda9aa0c"));
                        //开始连接蓝牙设备
                        clientSocket.connect();
                        os = clientSocket.getOutputStream();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (null != os) {
                        //向服务器端发送一个字符串
                        try {
                            String data = "这是另一台手机发送过来的数据\0";
                            os.write(data.getBytes());
                            Toast.makeText(MainActivity.this, "发送成功", 1000);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(MainActivity.this, "发送失败", 1000);
                            e.printStackTrace();
                        }
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
}
