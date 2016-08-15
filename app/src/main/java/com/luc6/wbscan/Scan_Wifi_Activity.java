package com.luc6.wbscan;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.luc6.wbscan.custom_adapter.CustomWifiAdapter;
import com.luc6.wbscan.helper.FrequencyConverter;
import com.luc6.wbscan.model.WifiApModel;

import java.util.ArrayList;
import java.util.List;


public class Scan_Wifi_Activity extends AppCompatActivity {
    private WifiManager wifiMngr;
    private WifiScanReceiver wifiReceiver;
    private CustomWifiAdapter networkAdapter;
    private boolean isScanning = false;
    private boolean iEnabledWifi = false;
    private final int MY_PERMISSION_COARSE_LOCATION = 0;
    private boolean permissions_all_good = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi__scan_);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        wifiMngr = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        networkAdapter = new CustomWifiAdapter();
        wifiReceiver = new WifiScanReceiver();

        ListView listV = (ListView)findViewById(R.id.listView);
        listV.setAdapter(networkAdapter);

        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView myTextView0 = (TextView) view.findViewById(R.id.net_ssid);
                TextView myTextView1 = (TextView) view.findViewById(R.id.net_bssid);
                String text0 = myTextView0.getText().toString();
                String text1 = myTextView1.getText().toString();
                // send some data bundled with the intent
                Intent intent = new Intent(Scan_Wifi_Activity.this, Locate_Wifi_Activity.class);
                intent.putExtra("data0", text0);
                intent.putExtra("data1", text1);
                startActivity(intent);
            }
        });

        startWifiScan();
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

        if (iEnabledWifi) {
            SharedPreferences.Editor editor = getSharedPreferences("myPref", MODE_PRIVATE).edit();
            editor.putBoolean("iEnabledWifi", true).apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        startWifiScan();
    }

    // create a receiver
    private class WifiScanReceiver extends BroadcastReceiver{

        public void onReceive(Context c, Intent intent) {
            String band = PreferenceManager.getDefaultSharedPreferences(Scan_Wifi_Activity.this).getString("wifi_band", "2400");
            List<ScanResult> wifiScanList = wifiMngr.getScanResults();
            List<WifiApModel> apList = new ArrayList<>();
            for (ScanResult result : wifiScanList) {
                switch (band) {
                    case "2400":
                        if (result.frequency < 2500) {
                            WifiApModel network = new WifiApModel(result.SSID, result.BSSID,
                                    result.frequency, FrequencyConverter.convert(result.frequency), result.level,
                                    result.capabilities);
                            apList.add(network);
                        }
                        break;
                    case "5000":
                        if (result.frequency > 5000) {
                            WifiApModel network = new WifiApModel(result.SSID, result.BSSID,
                                    result.frequency, FrequencyConverter.convert(result.frequency), result.level,
                                    result.capabilities);
                            apList.add(network);
                        }
                        break;
                }
            }
            // set the list you want to be printed
            if (permissions_all_good) {
                networkAdapter.setNetworkList(apList);
            }
        }
    }

    public class WifiScanAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            int interval = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(Scan_Wifi_Activity.this)
                    .getString("wifi_interval", "1000"));
            while (isScanning) {
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
        // enable wifi if is off
        if (!wifiMngr.isWifiEnabled()) {
            wifiMngr.setWifiEnabled(true);
            Toast.makeText(getApplicationContext(), R.string.wifi_is_enabled, Toast.LENGTH_SHORT).show();
            iEnabledWifi = true;
        }

        // check API level and permissions, and request permissions if not granted
        if((Build.VERSION.SDK_INT >= 23) && (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {
            permissions_all_good = false;
            requestPermissions();
        } else {
            // Start AsyncTask to scan for networks in the background
            new WifiScanAsyncTask().execute();
        }
    }

    public void stopWifiScan() {
        isScanning = false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSION_COARSE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // task you need to do.
                    // Start AsyncTask to scan for networks in the background
                    permissions_all_good =true;
                    new WifiScanAsyncTask().execute();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "ACCESS_COARSE_LOCATION permission is not granted. Abort.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}