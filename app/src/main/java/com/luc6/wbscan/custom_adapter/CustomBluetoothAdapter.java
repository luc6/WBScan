package com.luc6.wbscan.custom_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.luc6.wbscan.R;
import com.luc6.wbscan.helper.BluetoothDeviceDetails;
import com.luc6.wbscan.helper.DevicesList;
import com.luc6.wbscan.model.BluetoothDeviceModel;

import java.util.ArrayList;
import java.util.List;

public class CustomBluetoothAdapter extends BaseAdapter {
    private List<BluetoothDeviceModel> deviceList = new ArrayList<>();

    public void setDeviceList(List<BluetoothDeviceModel> deviceList) {
        this.deviceList = deviceList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.bluetooth_row, null);
            }

            TextView name = (TextView) convertView.findViewById(R.id.device_name);
            TextView address = (TextView) convertView.findViewById(R.id.device_mac_address);
            TextView bluetoothClass = (TextView) convertView.findViewById(R.id.device_class);
            TextView rssi = (TextView) convertView.findViewById(R.id.device_rssi);
            TextView type = (TextView) convertView.findViewById(R.id.device_type);
            TextView bondState = (TextView) convertView.findViewById(R.id.device_bond_state);

            final BluetoothDeviceModel device = deviceList.get(position);

            name.setText("name: " + device.getName());
            address.setText("MAC_address: " + device.getAddress());
            bluetoothClass.setText("bluetooth_class: " + DevicesList.Convert(device.getMajor()));
            type.setText("blutooth_type: " + BluetoothDeviceDetails.Type(String.valueOf(device.getTypel())));
            bondState.setText("bond_state: " + BluetoothDeviceDetails.BondState(String.valueOf(device.getBondState())));
            rssi.setText("rssi: " + String.valueOf(device.getRssi()) + " dBm");

            return convertView;
    }
}