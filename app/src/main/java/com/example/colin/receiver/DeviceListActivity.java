package com.example.colin.receiver;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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

public class DeviceListActivity extends Activity {
    ListView listview;

    Runnable updateListTask = new Runnable() {
        @Override
        public void run() {
            DeviceArrayAdapter adapter = new DeviceArrayAdapter(getApplicationContext(),
                    activeDevices.toArray(new DeviceInfo[activeDevices.size()]));
            listview.setAdapter(adapter);
            updateListHandler.postDelayed(updateListTask, 3000);

        }
    };
    ArrayList<DeviceInfo> activeDevices;
    BluetoothAdapter bta;
    Timer scantimer;
    TimerTask scantask;
    Handler scanEndHandler;
    final BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            String strAddr = device.getAddress();
            String strAddrSimple = "";
            for (char c : strAddr.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    strAddrSimple += c;
                }
            }
            long addr = Long.parseLong(strAddrSimple, 16);
            for (DeviceInfo bledinfo : activeDevices) {

                if (addr == bledinfo.getMAC()) {
                    activeDevices.remove(bledinfo);
                    break;
                }
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

    Handler updateListHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Import default values if no preferences have been set
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        super.onCreate(savedInstanceState);
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
        updateListHandler = new Handler();

        bta = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        activeDevices = new ArrayList<DeviceInfo>();
        activeDevices.add(new DeviceInfo("Placeholder", 133742069, -69, 0));
        scanEndHandler = new Handler();
        scantimer = new Timer("BLE Scanner Timer");
        scantask = new TimerTask() {
            @Override
            public void run() {
                scanEndHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bta.stopLeScan(scanCallback);
                    }
                }, 9000);
                bta.startLeScan(scanCallback);
            }
        };
        scantimer.scheduleAtFixedRate(scantask, 0, 10000);

        for(int i = 0; i < 6; ++i) {
            examples[i] = new DeviceInfo(names[i], addresses[i], strengths[i], 0);
        }


        DeviceArrayAdapter adapter = new DeviceArrayAdapter(this, activeDevices.toArray(new DeviceInfo[activeDevices.size()]));
        listview.setAdapter(adapter);
        updateListTask.run();
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.device_list, menu);
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
