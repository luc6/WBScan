package com.luc6.wbscan.helper;

import android.bluetooth.BluetoothClass;

// List of bluetooth devices defined in BluetoothClass.Device.Major

public class DevicesList {
    public static String Convert (int blueDev) {
        switch (blueDev) {
                case BluetoothClass.Device.Major.AUDIO_VIDEO:       return "AUDIO_VIDEO";
                case BluetoothClass.Device.Major.COMPUTER:          return "COMPUTER";
                case BluetoothClass.Device.Major.HEALTH:            return "HEALTH";
                case BluetoothClass.Device.Major.IMAGING:           return "IMAGING";
                case BluetoothClass.Device.Major.MISC:              return "MISC";
                case BluetoothClass.Device.Major.NETWORKING:        return "NETWORKING";
                case BluetoothClass.Device.Major.PERIPHERAL:        return "PERIPHERAL";
                case BluetoothClass.Device.Major.PHONE:             return "PHONE";
                case BluetoothClass.Device.Major.TOY:               return "TOY";
                case BluetoothClass.Device.Major.UNCATEGORIZED:     return "UNCATEGORIZED";
                case BluetoothClass.Device.Major.WEARABLE:          return "AUDIO_VIDEO";
                default:                                            return "unknown!";
        }
    }
}
