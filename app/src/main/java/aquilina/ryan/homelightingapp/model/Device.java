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
    private int id;
    private String name;
    private String ipAddress;
    private int port;
    private int leds;

    public Device(int id, String name, String ipAddress) {
        this.id = id;
        this.name = name;
        this.ipAddress = ipAddress;
    }

    public Device(String name, String ipAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getLeds() {
        return leds;
    }

    public void setLeds(int leds) {
        this.leds = leds;
    }
}
