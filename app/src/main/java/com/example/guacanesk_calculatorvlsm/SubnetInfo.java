package com.example.guacanesk_calculatorvlsm;

public class SubnetInfo {

    private String name;
    private String ip;
    private String firstIp;
    private String lastIp;
    private String broadcast;
    private int mask;

    // Constructor
    public SubnetInfo(String name, String ip, String firstIp, String lastIp, String broadcast, int mask) {
        this.name = name;
        this.ip = ip;
        this.firstIp = firstIp;
        this.lastIp = lastIp;
        this.broadcast = broadcast;
        this.mask = mask;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getFirstIp() {
        return firstIp;
    }

    public void setFirstIp(String firstIp) {
        this.firstIp = firstIp;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public String getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(String broadcast) {
        this.broadcast = broadcast;
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }
}
