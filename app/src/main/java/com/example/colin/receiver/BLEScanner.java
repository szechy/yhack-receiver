package com.example.colin.receiver;

import android.app.Activity;
import android.bluetooth.*;
import android.content.Context;
import android.os.Handler;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Sasawat on 11/01/2014.
 */
public class BLEScanner {
    static long timeoutMillis;
    static BluetoothAdapter bta;
    static ArrayList<DeviceInfo> activeDevices;
    final static BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
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
    static Timer timothy;
    static TimerTask timmytime;
    static TimerTask timmytimeScan;
    static Handler scanEndHandler;
    static DeviceListActivity activity;

    public static void init(BluetoothAdapter btAdapter, DeviceListActivity dlActivity)
    {
        activity = dlActivity;
        bta = btAdapter;
        timeoutMillis = 300000;
        activeDevices = new ArrayList<DeviceInfo>();
        activeDevices.add(new DeviceInfo("Placeholder", 133742069, -69, 0));

        scanEndHandler = new Handler();

        timothy = new Timer("BLE Scanner Timer");
        timmytime = new TimerTask() {
            @Override
            public void run() {
                pruneActiveDevices();
            }
        };
        timmytimeScan = new TimerTask() {
            @Override
            public void run() {
                BLEScanner.scan();
            }
        };
        timothy.scheduleAtFixedRate(timmytime, 10000, 10000);
        timothy.scheduleAtFixedRate(timmytimeScan, 0, 10000);
    }

    public static void scan()
    {
        scanEndHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bta.stopLeScan(scanCallback);
            }
        }, 9000);
        bta.startLeScan(scanCallback);

    }

    public static void setTimeout(int minute)
    {
        timeoutMillis = minute * 60 * 1000;
    }

    public static void pruneActiveDevices()
    {
        long currentTime = System.currentTimeMillis();
        for(int iter = 0; iter < activeDevices.size(); iter++)
        {
            if(currentTime - activeDevices.get(iter).getLastSeen() > timeoutMillis)
            {
                activeDevices.remove(iter);
                iter--;
            }
        }
    }
}
