package com.example.colin.receiver;

/**
 * Created by Colin on 11/1/14.
 */
public class DeviceInfo {

    private String name;
    private long mac;
    private int strength;

    public DeviceInfo(String deviceName, long deviceMAC, int signalStrength) {
        name = deviceName;
        mac = deviceMAC;
        strength = signalStrength;
    }

    public String getName() {
        return name;
    }

    public long getMAC() {
        return mac;
    }

    public int getStrength() {
        return strength;
    }

    public void setName(String deviceName) {
        name = deviceName;
    }

    public void setMAC(int addr) {
        mac = addr;
    }

    public void setStrength(int signal) {
        strength = signal;
    }
}
