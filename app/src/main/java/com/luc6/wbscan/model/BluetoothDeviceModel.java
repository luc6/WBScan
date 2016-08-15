package com.luc6.wbscan.model;

// BluetoothDeviceModel class is the model used by bluetooth BroadcastReceiver to save received data

public class BluetoothDeviceModel {
    private String name;
    private String address;
    private int major;
    private int type;
    private int bondState;
    private int rssi;

    public BluetoothDeviceModel(String name, String address, int major, int type, int bondState, int rssi) {
        this.name = name;
        this.address = address;
        this.major = major;
        this.type = type;
        this.bondState = bondState;
        this.rssi = rssi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getTypel() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getBondState() {
        return bondState;
    }

    public void setBondState(int bondState) {
        this.bondState = bondState;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
