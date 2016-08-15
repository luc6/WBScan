package com.luc6.wbscan;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Check if WIFI is supported
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI)) {
            // Disable WIFI radio button
            RadioButton myRadioButton = (RadioButton) findViewById(R.id.radioButton1);
            myRadioButton.setEnabled(false);
        }

        // Check if Bluetooth is supported
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            // Disable BLUETOOTH radio button
            RadioButton myRadioButton = (RadioButton) findViewById(R.id.radioButton2);
            myRadioButton.setEnabled(false);
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
                Intent intent0 = new Intent(this, SettingsActivity.class);
                startActivity(intent0);
                return true;
            case R.id.action_info:
                Intent intent2 = new Intent(this, Info_Activity.class);
                startActivity(intent2);
                return true;
            case R.id.action_about:
                Intent intent1 = new Intent(this, About_Activity.class);
                startActivity(intent1);
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

    // here is the response to click on wifi button
    public void selectWifi(View v) {
        Intent intent = new Intent(MainActivity.this, Scan_Wifi_Activity.class);
        startActivity(intent);
    }

    // here is the response to click on bluetooth button
    public void startClassicBluetoothScan(View v) {
        Intent intent = new Intent(MainActivity.this, Scan_Bluetooth_Activity.class);
        startActivity(intent);
    }
}
