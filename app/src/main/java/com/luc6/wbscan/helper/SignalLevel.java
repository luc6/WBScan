package com.luc6.wbscan.helper;

import android.net.wifi.WifiManager;

// this class determine the level of the AP signal

public class SignalLevel {
    public static int determineLevel(int signal) {
        return WifiManager.calculateSignalLevel(signal, 5);
    }
}
