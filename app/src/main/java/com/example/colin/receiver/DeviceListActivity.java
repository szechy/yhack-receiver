package com.example.colin.receiver;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceListActivity
        extends Activity
        implements SharedPreferences.OnSharedPreferenceChangeListener{
    ListView listview;

    SharedPreferences sharepref;

    WebClient client;

    Handler updateListHandler;
    Runnable updateListTask = new Runnable() {
        @Override
        public void run() {
            DeviceArrayAdapter adapter = new DeviceArrayAdapter(getApplicationContext(),
                    activeDevices.toArray(new DeviceInfo[activeDevices.size()]));
            listview.setAdapter(adapter);
            updateListHandler.postDelayed(updateListTask, 1000);

        }
    };

    int clientTimeout;
    Handler pruneListHandler;
    Runnable pruneListTask = new Runnable() {
        @Override
        public void run() {
            long now = System.currentTimeMillis();
            for(int iter = 0; iter < activeDevices.size(); iter++)
            {
                DeviceInfo info = activeDevices.get(iter);
                if(now - info.getLastSeen() > clientTimeout)
                {
                    activeDevices.remove(iter);
                    iter--;
                    //TODO: Send the they aint active no more to server

                }
            }

            pruneListHandler.postDelayed(pruneListTask, 60000);
        }
    };

    ArrayList<DeviceInfo> activeDevices;
    BluetoothAdapter bta;
    Timer scantimer;
    TimerTask scantask;
    Handler scanEndHandler;

    Timer sendtimer;
    TimerTask sendtask;

    int rssiThreshold;
    final BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if(-rssi > rssiThreshold)
            {
                return;
            }
            String strAddr = device.getAddress();
            String strAddrSimple = "";
            for (char c : strAddr.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    strAddrSimple += c;
                }
            }
            long addr = Long.parseLong(strAddrSimple, 16);
            boolean isNew = true;
            for (DeviceInfo bledinfo : activeDevices) {

                if (addr == bledinfo.getMAC()) {
                    activeDevices.remove(bledinfo);
                    isNew = false;
                    break;
                }
            }
            if(isNew)
            {
                //TODO: Notify server
            }

            String strName = device.getName();
            if(strName == null)
            {
                strName = device.getAddress();
            }
            activeDevices.add(
                    new DeviceInfo(strName, addr, rssi, System.currentTimeMillis()));
        }
    };

    BroadcastReceiver bluetoothScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int rssi = 0;
                if(intent.hasExtra(BluetoothDevice.EXTRA_RSSI))
                {
                    rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,(short)0);
                }
                String strAddr = device.getAddress();
                String strAddrSimple = "";
                for (char c : strAddr.toCharArray()) {
                    if (c >= '0' && c <= '9') {
                        strAddrSimple += c;
                    }
                }
                long addr = Long.parseLong(strAddrSimple, 16);
                boolean isNew = true;
                for (DeviceInfo bledinfo : activeDevices) {

                    if (addr == bledinfo.getMAC()) {
                        activeDevices.remove(bledinfo);
                        isNew = false;
                        break;
                    }
                }
                if(isNew)
                {
                    //TODO: Notify server
                }

                String strName = device.getName();
                if(strName == null)
                {
                    strName = device.getAddress();
                }
                activeDevices.add(
                        new DeviceInfo(strName, addr, rssi, System.currentTimeMillis()));
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Import default values if no preferences have been set
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharepref = PreferenceManager.getDefaultSharedPreferences(this);


        clientTimeout = new Integer(sharepref.getString("timeout","5"));
        clientTimeout *= 60000;
        rssiThreshold = new Integer(sharepref.getString("rssi_thresh","50"));
        sharepref.registerOnSharedPreferenceChangeListener(this);

        super.onCreate(savedInstanceState);

        client = new WebClient(sharepref.getString("location", "location"));

        setContentView(R.layout.activity_device_list);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        listview = (ListView)findViewById(R.id.device_list);
        String[] names = {"Ariel", "Jasmine", "Belle", "Eve", "Edward", "Jasper"};
        long[] addresses = {890456783, 2342342, 23904895, 234234, 98043843, 1};
        int[] strengths = {-57, -89, -42, -35, -120, -105};

        DeviceInfo[] examples = new DeviceInfo[6];


        bta = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

        activeDevices = new ArrayList<DeviceInfo>();
        activeDevices.add(new DeviceInfo("Placeholder", 133742069, -69, 0));
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothScanReceiver, filter);
        scanEndHandler = new Handler();
        scantimer = new Timer("BLE Scanner Timer");
        scantask = new TimerTask() {
            @Override
            public void run() {
//                scanEndHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //bta.stopLeScan(scanCallback);
//                    }
//                }, 9000);
                //bta.startLeScan(scanCallback)
                if(!bta.isDiscovering()) {
                    if(!bta.startDiscovery())
                    {
                        Log.e("Bluetooth", "Discovery Failed To Start");
                    }
                }
            }
        };
        scantimer.scheduleAtFixedRate(scantask, 0, 3000);

        sendtimer = new Timer("Web Server Update Timer");
        sendtask = new TimerTask() {
            @Override
            public void run() {
                client.sendActive(getApplicationContext(), activeDevices);
            }
        };
        sendtimer.scheduleAtFixedRate(sendtask, 15000, 60000);

        for(int i = 0; i < 6; ++i) {
            examples[i] = new DeviceInfo(names[i], addresses[i], strengths[i], 0);
        }

        updateListHandler = new Handler();
        updateListTask.run();

        pruneListHandler = new Handler();
        pruneListTask.run();
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.device_list, menu);
        setTitle("Active Devices: " + sharepref.getString("location", "Location"));
        getActionBar().setDisplayShowHomeEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SetupActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        clientTimeout = new Integer(
               sharepref.getString("timeout","5"));
        clientTimeout *= 60000;
        setTitle("Active Devices: " + sharepref.getString("location", "location"));
        client.setLocation(sharepref.getString("location", "location"));
    }

    @Override
    public void onDestroy()
    {
        unregisterReceiver(bluetoothScanReceiver);
        sharepref.unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_device_list, container, false);
            return rootView;
        }
    }
}
