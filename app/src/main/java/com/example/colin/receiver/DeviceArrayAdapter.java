package com.example.colin.receiver;

import android.widget.ArrayAdapter;
import android.content.Context;
import java.util.ArrayList;
import java.util.Arrays;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Colin on 11/1/14.
 */
public class DeviceArrayAdapter extends ArrayAdapter<DeviceInfo> {

    private Context context;
    private ArrayList<DeviceInfo> devices;

    public DeviceArrayAdapter(Context context, DeviceInfo[] myDevices) {
        super(context, R.layout.device_item, myDevices);
        devices = new ArrayList<DeviceInfo>(Arrays.asList(myDevices));
        this.context = context;
    }

    public void addDevice(DeviceInfo newDevice) {
        devices.add(newDevice);
    }

    public void updateAllDevices(DeviceInfo[] myDevices) {
        devices = new ArrayList<DeviceInfo>(Arrays.asList(myDevices));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.device_item, parent, false);
        //inflate with values from ArrayList

        TextView deviceName = (TextView)rowView.findViewById(R.id.device_name);
        deviceName.setText(devices.get(position).getName());
        TextView deviceAddress = (TextView)rowView.findViewById(R.id.device_mac);
        deviceAddress.setText(Long.toHexString(devices.get(position).getMAC()));
        TextView deviceStrength = (TextView)rowView.findViewById(R.id.device_strength);
        deviceStrength.setText(Integer.toString(devices.get(position).getStrength()));

        return rowView;
    }
}
