package com.example.bluetoothconnection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    BluetoothAdapter bluetoothAdapter;
    Button button,discoverable,discover;
    RecyclerView listview;
    ArrayList<BluetoothDevice> btDevices;
    BluetoothDeviceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        discoverable = findViewById(R.id.discoverable);
        discover = findViewById(R.id.discover);
        listview = findViewById(R.id.item);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadCasteRecevier4,filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btDevices = new ArrayList<BluetoothDevice>();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothAdapter ==null){
                    Log.e("TAG", "bluetooth is not supported");
                }
                if (!bluetoothAdapter.isEnabled()){
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enableIntent);
                    IntentFilter enableFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    registerReceiver(mBroadCasteRecevier1,enableFilter);
                }
                if (bluetoothAdapter.isEnabled()){
                     bluetoothAdapter.disable();
                    IntentFilter enableFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    registerReceiver(mBroadCasteRecevier1,enableFilter);
                }
            }
        });

        discoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("TAG", "btnEnabledDisabled Discoverable : making device discoverable in 300 seconds ");
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
                startActivity(discoverableIntent);
                IntentFilter discoverableFilter = new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
                registerReceiver(mBroadCasteRecevier2,discoverableFilter);
            }
        });

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAG", "Looking for unpaired Devices");
              //  btDevices.clear();

                if (bluetoothAdapter.isDiscovering()){
                    bluetoothAdapter.cancelDiscovery();
                    checkpermission();
                    bluetoothAdapter.startDiscovery();
                    IntentFilter descoverFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mBroadCasteRecevier3,descoverFilter);
                }
                if (!bluetoothAdapter.isDiscovering()){
                    checkpermission();
                    bluetoothAdapter.startDiscovery();
                    IntentFilter descoverFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mBroadCasteRecevier3,descoverFilter);
                }
            }
        });


    }

    private void checkpermission() {
        if (Build.VERSION.SDK_INT >Build.VERSION_CODES.LOLLIPOP){
            int permissioncheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissioncheck +=  this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if(permissioncheck !=0){
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1001);
            }
        }else{
            Log.e("TAG", "Missing Permissions");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("TAG", "bluetooth receiver is Destroy");
        unregisterReceiver(mBroadCasteRecevier1);
        unregisterReceiver(mBroadCasteRecevier2);
        unregisterReceiver(mBroadCasteRecevier3);
        unregisterReceiver(mBroadCasteRecevier4);
    }

    BroadcastReceiver mBroadCasteRecevier1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action  = intent.getAction();
            if(action.equals(bluetoothAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,bluetoothAdapter.ERROR);
                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE_OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: STATE_ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "onReceive: STATE_TURNING_OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "onReceive: STATE_TURNING_ON");
                        break;
                }
            }
        }
    };


    BroadcastReceiver mBroadCasteRecevier2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action  = intent.getAction();
            if(action.equals(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,bluetoothAdapter.ERROR);
                switch(state){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadCasteRecevier2 : DISCOVERABLE Enabled");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadCasteRecevier2 : DISCOVERABLE Disable,  Able to receive connections");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadCasteRecevier2 : DISCOVERABLE Disable, Not Able to receive connections");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "onReceive: STATE Connection...");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "onReceive: STATE Connected.");
                        break;
                }
            }
        }
    };

    BroadcastReceiver mBroadCasteRecevier3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "mBroadCasteRecevier3 : called");
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btDevices.add(device);

                listview.setLayoutManager(new LinearLayoutManager(context));
                adapter = new BluetoothDeviceAdapter(btDevices,new OnListItemClick(){
                    @Override
                    public void onClick(View view, int position) {
                        Log.e(TAG, "onItemClick: Device pairing");
                        bluetoothAdapter.cancelDiscovery();
                        String deviceName = btDevices.get(position).getName();
                        String deviceAddress = btDevices.get(position).getAddress();
                        Log.d(TAG, "On Item Click  :"+deviceName+ ", "+deviceAddress);
                        btDevices.get(position).createBond();
                    }
                });

                listview.setAdapter(adapter);

            }


        }
    };

    BroadcastReceiver mBroadCasteRecevier4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG,"onReceive : mBroadCasteRecevier4 ");
             if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                 BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                 if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                     Log.d(TAG,"BOND_BONDED");
                 }

                 if (device.getBondState() == BluetoothDevice.BOND_BONDING){
                     Log.d(TAG,"BOND_BONDING");
                 }

                 if (device.getBondState() != BluetoothDevice.BOND_NONE){
                     Log.d(TAG,"BOND_NONE");
                 }
             }

        }
    };
}