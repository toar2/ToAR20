package com.aaksoft.toar.mapbox;

/*
    Created By Aasharib
    on
    15 February, 2019
 */

import com.mapbox.mapboxsdk.annotations.Marker;

/*
    This class is for differentiating places markers from 3D markers on map.

    Tagged Markers means 3D Model Markers on map and Location history Markers.

    Untagged Markers i.e with empty string are place api markers shown on map.
*/

public class MarkerType{

    private Marker marker;
    private String tag;

    public MarkerType(Marker marker, String tag) {
        this.marker = marker;
        this.tag = tag;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

}
