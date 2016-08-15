package com.luc6.wbscan.helper;


public class BluetoothDeviceDetails {

    public static String Type(String type) {

        String tp;

        switch (type) {
            case "0": tp = "Unknown";
                break;
            case "1": tp =  "Classic";
                break;
            case "2": tp =  "LE (Low Energy)";
                break;
            case "3": tp =  "Dual";
                break;
            default: tp = "Unknown";
        }
        return tp;
    }

    public static String BondState (String bondState) {

        String bS;

        switch (bondState) {
            case "10": bS = "Not Bonded";
                break;
            case "11": bS = "Bonding..";
                break;
            case "12": bS = "Bonded";
                break;
            default: bS = "Unknown";
        }
        return bS;
    }
}
