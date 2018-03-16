/*
 * Created by Ryan Aquilina on 10/18/17 4:38 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 10/9/17 10:03 AM
 */

package aquilina.ryan.homelightingapp.model;

import java.io.Serializable;

public class Device implements Serializable{
    private String name;
    private String ipAddress;
    private Short pixelsPerStrip;
    private Short chunkSize;
    private Short maPerPixel;
    private Integer udpStreamingPort;
    private Float ampsLimit;
    private String warmUpColor;

    public Device(String name, String ipAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.pixelsPerStrip = -1;
        this.chunkSize = -1;
        this.maPerPixel = -1;
        this.udpStreamingPort = -1;
        this.ampsLimit = -1f;
        this.warmUpColor = "-1";
    }

    public Device(String name, String ipAddress, Short pixelsPerStrip, Short chunkSize, Short maPerPixel, Integer udpStreamingPort, Float ampsLimit, String warmUpColor) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.pixelsPerStrip = pixelsPerStrip;
        this.chunkSize = chunkSize;
        this.maPerPixel = maPerPixel;
        this.udpStreamingPort = udpStreamingPort;
        this.ampsLimit = ampsLimit;
        this.warmUpColor = warmUpColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Short getPixelsPerStrip() {
        return pixelsPerStrip;
    }

    public void setPixelsPerStrip(Short pixelsPerStrip) {
        this.pixelsPerStrip = pixelsPerStrip;
    }

    public Short getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Short chunkSize) {
        this.chunkSize = chunkSize;
    }

    public Short getMaPerPixel() {
        return maPerPixel;
    }

    public void setMaPerPixel(Short maPerPixel) {
        this.maPerPixel = maPerPixel;
    }

    public Integer getUdpStreamingPort() {
        return udpStreamingPort;
    }

    public void setUdpStreamingPort(Integer udpStreamingPort) {
        this.udpStreamingPort = udpStreamingPort;
    }

    public Float getAmpsLimit() {
        return ampsLimit;
    }

    public void setAmpsLimit(Float ampsLimit) {
        this.ampsLimit = ampsLimit;
    }

    public String getWarmUpColor() {
        return warmUpColor;
    }

    public void setWarmUpColor(String warmUpColor) {
        this.warmUpColor = warmUpColor;
    }
}
