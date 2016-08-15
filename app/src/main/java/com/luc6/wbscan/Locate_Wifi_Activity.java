package com.luc6.wbscan;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import com.luc6.wbscan.custom_adapter.CustomWifiAdapter;
import com.luc6.wbscan.helper.FrequencyConverter;
import com.luc6.wbscan.helper.SignalLevel;
import com.luc6.wbscan.model.WifiApModel;

import java.util.ArrayList;
import java.util.List;

public class Locate_Wifi_Activity extends AppCompatActivity {
    private WifiManager wifiMngr;
    private WifiScanReceiver wifiReceiver;
    private CustomWifiAdapter networkAdapter;
    private String ap_ssid;
    private String ap_bssid;
    private boolean isScanning = false;
    private boolean sound = false;
    private Button soundButton;
    private MediaPlayer mp;
    private int signalLevel;
    private int newSignalLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_locate_);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        wifiMngr = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        networkAdapter = new CustomWifiAdapter();
        wifiReceiver = new WifiScanReceiver();


        ListView listV = (ListView)findViewById(R.id.listView2);
        listV.setAdapter(networkAdapter);

        // extract data from bundle
        Intent intent = getIntent();
        Bundle bndl = intent.getExtras();
        ap_ssid = bndl.getString("data0");
        ap_bssid = bndl.getString("data1");

        // button to start/stop sound
        soundButton = (Button) this.findViewById(R.id.soundBtn);
        soundButton.setOnClickListener(buttonListener);

        startWifiScan();
    }

    // a listener for that button
    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!sound) {
                sound = true;
                soundButton.setText(R.string.sound_off);
                signalLevel =newSignalLevel;
                soundController(1);
            } else {
                sound = false;
                soundButton.setText(R.string.sound_on);
                soundController(2);
            }
        }
    };

    // sound controller is called when button is clicked, and is also called by Broadcast Receiver
    // every time it receives a new scan, different sources have a different parameter;
    // sound controller reacts in different ways, based on who calls it
    public void soundController(int param) {

        switch (param) {
            case 0:
                if (newSignalLevel != signalLevel && sound) {
                    signalLevel = newSignalLevel;
                    Pause();
                    setMediaPlayer();
                    Play();
                }
                break;
            // value -1 is set by Broadcast Receiver when AP is out of range
            case 1:
                if (mp == null && signalLevel != -1) {
                    setMediaPlayer();
                    Play();
                }
                break;
            case 2:
                Pause();
                break;
            case 3:
                signalLevel = newSignalLevel;
                Pause();
                break;
        }
    }

    public void setMediaPlayer () {

        switch (newSignalLevel) {
            case 0:
                mp = MediaPlayer.create(this, R.raw.b4);
                break;
            case 1:
                mp = MediaPlayer.create(this, R.raw.b3);
                break;
            case 2:
                mp = MediaPlayer.create(this, R.raw.b2);
                break;
            case 3:
                mp = MediaPlayer.create(this, R.raw.b1);
                break;
            case 4:
                mp = MediaPlayer.create(this, R.raw.b0);
                break;
        }
    }

    public void Play() {
        mp.setLooping(true);
        mp.start();
    }

    public void Pause()  {
        if (mp != null) {
            mp.release();
            mp = null;
        }
    }

    // menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // menu clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                //showSettings();
                Intent intent0 = new Intent(this, SettingsActivity.class);
                startActivity(intent0);
                return true;
            case R.id.action_info:
                Intent intent2 = new Intent(this, Info_Activity.class);
                startActivity(intent2);
                return true;
            case R.id.action_about:
                Intent intent = new Intent(this, About_Activity.class);
                startActivity(intent);
                return true;
            case R.id.action_exit:
                SharedPreferences sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
                boolean iEnabledWifi = sharedPref.getBoolean("iEnabledWifi", false);
                boolean iEnabledBt = sharedPref.getBoolean("iEnabledBt", false);

                if (iEnabledWifi) {
                    WifiManager wifiM = (WifiManager)getSystemService(Context.WIFI_SERVICE);
                    wifiM.setWifiEnabled(false);
                    sharedPref.edit().putBoolean("iEnabledWifi", false).apply();
                }

                if (iEnabledBt) {
                    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                    btAdapter.disable();
                    sharedPref.edit().putBoolean("iEnabledBt", false).apply();
                }

                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopWifiScan();
        unregisterReceiver(wifiReceiver);
        if (mp != null) {
            Pause();
        }
        sound = false;
        soundButton.setText(R.string.sound_on);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        startWifiScan();
    }

    // create a receiver
    private class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = wifiMngr.getScanResults();
            List<WifiApModel> apList = new ArrayList<>();
            boolean foundAp = false;
            for (ScanResult result : wifiScanList) {
                if (result.BSSID.equals(ap_bssid)) {
                    foundAp = true;
                    WifiApModel network = new WifiApModel(result.SSID, result.BSSID,
                            result.frequency, FrequencyConverter.convert(result.frequency), result.level,
                            result.capabilities);
                    apList.add(network);
                    newSignalLevel = SignalLevel.determineLevel(result.level);
                    soundController(0);
                }
            }
            if (!foundAp) {
                WifiApModel network = new WifiApModel(ap_ssid, ap_bssid, 0, 0, -100, "");
                apList.add(network);
                newSignalLevel = -1;
                soundController(3);
            }
            // set the list you want to be printed
            networkAdapter.setIsSelected();
            networkAdapter.setNetworkList(apList);
        }
    }

    public class WifiScanAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            while (isScanning) {
                int interval = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(Locate_Wifi_Activity.this)
                        .getString("wifi_interval", "1000"));
                wifiMngr.startScan();
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public void startWifiScan() {
        isScanning = true;
        // Start AsyncTask to scan for networks in the background
        new WifiScanAsyncTask().execute();
    }

    public void stopWifiScan() {
        isScanning = false;
    }
}
