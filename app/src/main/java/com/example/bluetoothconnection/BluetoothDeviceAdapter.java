package com.example.bluetoothconnection;

import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder>  {

    ArrayList<BluetoothDevice> btDeviceList;
    OnListItemClick onListItemClicks;

    public BluetoothDeviceAdapter(ArrayList<BluetoothDevice> btDevices, OnListItemClick onListItemClick) {

        this.btDeviceList = btDevices;
        this.onListItemClicks = onListItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.devices_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final BluetoothDevice devieData = btDeviceList.get(position);
        holder.btName.setText(devieData.getName());
        holder.btDeviceId.setText(devieData.getAddress());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListItemClicks.onClick(view,position);
            }
        });


    }


    @Override
    public int getItemCount() {
        Log.e("SIZE OF DEVICES ", ""+ btDeviceList.size());
        return btDeviceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView btName;
        private final TextView btDeviceId;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btName = (TextView) itemView.findViewById(R.id.btname);
            btDeviceId = (TextView) itemView.findViewById(R.id.device_id);
        }
    }
}
