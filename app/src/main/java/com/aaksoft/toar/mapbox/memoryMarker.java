package com.aaksoft.toar.mapbox;

import com.aaksoft.toar.firebase.Memory;
import com.mapbox.mapboxsdk.annotations.Marker;

public class memoryMarker extends MarkerType {
    Memory markersMemory;

    public memoryMarker(Marker marker, Memory markerMemory){
        super(marker, "MemoryMarker");
        this.markersMemory = markerMemory;

    }
    public Memory getMarkersMemory() {
        return markersMemory;
    }
    public void setMarkersMemory(Memory markersMemory) {
        this.markersMemory = markersMemory;
    }






}
