package com.luc6.wbscan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import com.luc6.wbscan.custom_adapter.CustomBluetoothAdapter;
import com.luc6.wbscan.model.BluetoothDeviceModel;

import java.util.ArrayList;
import java.util.List;


public class Scan_Bluetooth_Activity extends AppCompatActivity {
    private final static int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private CustomBluetoothAdapter mArrayAdapter;
    private Button buttonBT;
    private boolean iEnabledBt = false;

    // this list store Broadcast Receiver's items; normally it should be placed inside BroadcastReceiver,
    // but i need to reset it when the Start button is clicked, so that's why is here
    List<BluetoothDeviceModel> devices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth__scan_);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mArrayAdapter = new CustomBluetoothAdapter();

        // if Bt is off, ask permission to enable it
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            if (mBluetoothAdapter != null) {
                iEnabledBt = true;
            }
        }

        // register a filter to listen for new connections
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        this.registerReceiver(mReceiver, filter1);



        // connect list (activity's list) to adapter
        ListView lv = (ListView) findViewById(R.id.listView_BT);
        lv.setAdapter(mArrayAdapter);


        buttonBT = (Button)findViewById(R.id.buttonBT);
        buttonBT.setOnClickListener(myListener);
    }

    private View.OnClickListener myListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.startDiscovery();
                buttonBT.setText(R.string.cancelBluetoothScan);
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

                devices.clear();
                mArrayAdapter.notifyDataSetChanged();
            }
            else {
                mBluetoothAdapter.cancelDiscovery();
                buttonBT.setText(R.string.startBluetoothScan);
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
        }
    };

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
        mBluetoothAdapter.cancelDiscovery();
        unregisterReceiver(mReceiver);

        if (iEnabledBt) {
            SharedPreferences.Editor editor = getSharedPreferences("myPref", MODE_PRIVATE).edit();
            editor.putBoolean("iEnabledBt", true).apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        this.registerReceiver(mReceiver, filter1);
        buttonBT.setText(R.string.startBluetoothScan);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

    // Create a BroadcastReceiver
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        private BluetoothDeviceModel dev;

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) 0);

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                dev = new BluetoothDeviceModel(device.getName(), device.getAddress(),
                        device.getBluetoothClass().getMajorDeviceClass(), device.getType(), device.getBondState(), rssi);
                devices.add(dev);
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                dev = new BluetoothDeviceModel(device.getName(), device.getAddress(),
                        device.getBluetoothClass().getMajorDeviceClass(), device.getType(), device.getBondState(), rssi);
                devices.add(dev);
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
            mArrayAdapter.setDeviceList(devices);
        }
    };
}
