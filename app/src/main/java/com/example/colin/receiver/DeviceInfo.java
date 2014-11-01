package com.example.colin.receiver;

/**
 * Created by Colin on 11/1/14.
 */
public class DeviceInfo {

    private String name;
    private long mac;
    private int strength;
    private long lastseen;

    public DeviceInfo(String deviceName, long deviceMAC, int signalStrength, long lastSeen) {
        name = deviceName;
        mac = deviceMAC;
        strength = signalStrength;
        lastseen = lastSeen;
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

    public long getLastSeen() { return lastseen; }

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
