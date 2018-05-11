/*
 * Created by Ryan Aquilina on 10/25/17 11:39 AM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 10/25/17 11:39 AM
 */

package frost.com.homelighting.ui.designmode;

import java.util.ArrayList;

public class DesignConfiguration {
    private int startColor;
    private int endColor;
    private int centreCircleColor;
    private int repetitions;
    private int duration;
    private int spinnerPosition;
    private ArrayList<String> selectedDevicesIP;
    private String effect;
    private String command;
    private boolean startCircleState;


    public DesignConfiguration(int startColor, int endColor, int centreCircleColor, int repetitions, int duration, String effect, String command, int spinnerPosition, ArrayList<String> selectedDevicesIP, boolean startCircleState) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.centreCircleColor = centreCircleColor;
        this.repetitions = repetitions;
        this.duration = duration;
        this.effect = effect;
        this.command = command;
        this.spinnerPosition = spinnerPosition;
        this.selectedDevicesIP = selectedDevicesIP;
        this.startCircleState = startCircleState;
    }

    public int getStartColor() {
        return startColor;
    }

    public int getEndColor() {
        return endColor;
    }

    public int getCentreCircleColor() {
        return centreCircleColor;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public int getDuration() {
        return duration;
    }

    public String getEffect() {
        return effect;
    }

    public String getCommand() {
        return command;
    }

    public int getSpinnerPosition() {
        return spinnerPosition;
    }

    public ArrayList<String> getSelectedDevicesIP() {
        return selectedDevicesIP;
    }

    public void setSelectedDevicesIP(ArrayList<String> selectedDevicesIP) {
        this.selectedDevicesIP = selectedDevicesIP;
    }

    public boolean isStartCircleState() {
        return startCircleState;
    }
}
