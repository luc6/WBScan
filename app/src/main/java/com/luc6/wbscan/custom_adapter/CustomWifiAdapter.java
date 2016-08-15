package com.luc6.wbscan.custom_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.luc6.wbscan.R;
import com.luc6.wbscan.helper.Distance_calculator;
import com.luc6.wbscan.helper.SignalLevel;
import com.luc6.wbscan.model.WifiApModel;

import java.util.ArrayList;
import java.util.List;

public class CustomWifiAdapter extends BaseAdapter {
    private List<WifiApModel> networkList = new ArrayList<>();
    boolean isSelected = false;

    public void setNetworkList(List<WifiApModel> networkList) {
        this.networkList = networkList;
        notifyDataSetChanged();
    }

    public void setIsSelected() {
        isSelected = true;
    }

    @Override
    public int getCount() {
        return networkList.size();
    }

    @Override
    public Object getItem(int position) {
        return networkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (!isSelected) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.wifi_row, null);
            }

            TextView ssid_row = (TextView) convertView.findViewById(R.id.net_ssid);
            TextView bssid_row = (TextView) convertView.findViewById(R.id.net_bssid);
            TextView channel_row = (TextView) convertView.findViewById(R.id.net_channel);
            ImageView signal_image_row = (ImageView) convertView.findViewById(R.id.net_signal_img);
            TextView security_row = (TextView) convertView.findViewById(R.id.net_security);
            TextView signal_row = (TextView) convertView.findViewById(R.id.net_signal);

            final WifiApModel network = networkList.get(position);

            ssid_row.setText(network.getSsid());
            bssid_row.setText(network.getBssid());
            channel_row.setText("channel: " +String.valueOf(network.getChannel()));
            security_row.setText("security: ");
            signal_row.setText(String.valueOf(network.getSignal()) + " dBm");

            if ((network.getSecurity().contains("WPA")) && (network.getSecurity().contains("WPA2"))) {
                security_row.append("WPA2 ");
            } else if (network.getSecurity().contains("WPA")) {
                security_row.append("WPA ");
            } else if (network.getSecurity().contains("WEP")) {
                security_row.append("WEP ");
            }

            if (network.getSecurity().contains("CCMP")) {
                security_row.append("CCMP ");
            }

            if (network.getSecurity().contains("TKIP")) {
                security_row.append("TKIP ");
            }

            if (network.getSecurity().contains("PSK")) {
                security_row.append("PSK ");
            }

            if (network.getSecurity().contains("WPS")) {
            security_row.append("WPS ");
            }


            if (network.getSecurity().contains("WEP")) {
                // select appropriate image based on signal level
                int sig = (SignalLevel.determineLevel(network.getSignal()));
                switch (sig) {
                    case 0: signal_image_row.setImageResource(R.drawable.open20);
                        break;
                    case 1: signal_image_row.setImageResource(R.drawable.open40);
                        break;
                    case 2: signal_image_row.setImageResource(R.drawable.open60);
                        break;
                    case 3: signal_image_row.setImageResource(R.drawable.open80);
                        break;
                    case 4: signal_image_row.setImageResource(R.drawable.open100);
                        break;
                }
            } else {
                // select appropriate image based on signal level
                int sig = (SignalLevel.determineLevel(network.getSignal()));
                switch (sig) {
                    case 0: signal_image_row.setImageResource(R.drawable.enc20);
                        break;
                    case 1: signal_image_row.setImageResource(R.drawable.enc40);
                        break;
                    case 2: signal_image_row.setImageResource(R.drawable.enc60);
                        break;
                    case 3: signal_image_row.setImageResource(R.drawable.enc80);
                        break;
                    case 4: signal_image_row.setImageResource(R.drawable.enc100);
                        break;
                }
            }

            return convertView;

        } else {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.locate_wifi, null);
            }

            TextView ssid = (TextView) convertView.findViewById(R.id.network_ssid);
            TextView bssid = (TextView) convertView.findViewById(R.id.network_bssid);
            TextView frequency = (TextView) convertView.findViewById(R.id.network_frequency);
            TextView channel = (TextView) convertView.findViewById(R.id.network_channel);
            TextView signal = (TextView) convertView.findViewById(R.id.network_signal);
            TextView distance = (TextView) convertView.findViewById(R.id.network_distance);
            TextView security = (TextView) convertView.findViewById(R.id.network_security);


            final WifiApModel network = networkList.get(position);
            ssid.setText("SSID: " + network.getSsid());
            bssid.setText("BSSID: " + network.getBssid());
            frequency.setText("frequency: " + String.valueOf(network.getFrequency()) + " MHz");
            channel.setText("channel: " + String.valueOf(network.getChannel()));
            signal.setText(String.valueOf(network.getSignal()) + " dBm");
            security.setText(String.valueOf("security: "));

            double dist = Distance_calculator.calculateDistance(network.getSignal(), network.getFrequency());

             if (dist != -1) {
                distance.setText(String.valueOf("distance_estimation:  " + dist + "m"));
            } else {
                distance.setText(String.valueOf("----OUT OF RANGE----"));
            }

            ProgressBar progBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
            progBar.setProgress(network.getSignal() + 100);


            if ((network.getSecurity().contains("[WPA-PSK-CCMP]")) && (network.getSecurity().contains("[WPA2-PSK-CCMP]"))) {
                security.append("[WPA-WPA2-PSK-CCMP]");
            } else if ((network.getSecurity().contains("[WPA-PSK-CCMP+TKIP]")) && (network.getSecurity().contains("[WPA2-PSK-CCMP+TKIP]"))) {
                security.append("[WPA2-PSK-CCMP+TKIP]");
            }

            if (network.getSecurity().contains("WPS")) {
                security.append("[WPS]");
            }

            if (network.getSecurity().contains("WEP")) {
                security.append("[WEP]");
            }

            if (network.getSecurity().contains("ESS")) {
                security.append("[ESS]");
            }

            return convertView;
        }
    }
}