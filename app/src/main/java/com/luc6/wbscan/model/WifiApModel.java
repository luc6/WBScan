package com.luc6.wbscan.model;

// this WifiApModel class is the model used by wifi BroadcastReceiver to save received data

public class WifiApModel {
    private String ssid;
    private String bssid;
    private int frequency;
    private int channel;
    private int signal;
    private String security;

    public WifiApModel(String ssid, String bssid, int frequency, int channel, int signal, String security) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.frequency = frequency;
        this.channel = channel;
        this.signal = signal;
        this.security = security;
    }


    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }
}
