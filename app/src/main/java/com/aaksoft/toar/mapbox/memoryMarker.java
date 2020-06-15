package com.aaksoft.toar.mapbox;

import com.aaksoft.toar.firebase.Memory;
import com.mapbox.mapboxsdk.annotations.Marker;

public class memoryMarker extends MarkerType {

    public Memory getMarkersMemory() {
        return markersMemory;
    }

    public void setMarkersMemory(Memory markersMemory) {
        this.markersMemory = markersMemory;
    }

    Memory markersMemory;


    public memoryMarker(Marker marker, String tag, Memory markerMemory){
        super(marker, tag);
        this.markersMemory = markerMemory;

    }




}
