package com.aaksoft.toar.api.google;

/*
    Created By Aasharib
    on
    15 August, 2018
 */

import java.util.HashMap;
import java.util.List;

public class DirectionsData {
    private String[] polyline;
    private String[] maneuver;
    private String[] htmlInstruction;
    private List<HashMap<String, String>> distanceDuration;

    public DirectionsData(String[] polyline, String[] maneuver, String[] htmlInstruction, List<HashMap<String, String>> distanceDuration) {
        this.polyline = polyline;
        this.maneuver = maneuver;
        this.htmlInstruction = htmlInstruction;
        this.distanceDuration = distanceDuration;
    }

    public String[] getPolyline() {
        return polyline;
    }

    public void setPolyline(String[] polyline) {
        this.polyline = polyline;
    }

    public String[] getManeuver() {
        return maneuver;
    }

    public void setManeuver(String[] maneuver) {
        this.maneuver = maneuver;
    }

    public String[] getHtmlInstruction() {
        return htmlInstruction;
    }

    public void setHtmlInstruction(String[] htmlInstruction) {
        this.htmlInstruction = htmlInstruction;
    }

    public List<HashMap<String, String>> getDistanceDuration() {
        return distanceDuration;
    }

    public void setDistanceDuration(List<HashMap<String, String>> distanceDuration) {
        this.distanceDuration = distanceDuration;
    }


}
