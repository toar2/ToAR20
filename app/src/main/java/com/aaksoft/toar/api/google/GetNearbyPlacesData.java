package com.aaksoft.toar.api.google;

/*
    Created By Aasharib
    on
    15 August, 2018
 */

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.aaksoft.toar.activities.MapsActivity;
import com.aaksoft.toar.R;
import com.aaksoft.toar.api.google.Information.Places;
import com.aaksoft.toar.mapbox.MarkerType;
import uk.co.appoly.arcorelocation.LocationScene;

/*
    Calls Google Place API to get list of nearby places
 */

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    //GoogleMap mMap;
    MapboxMap mMap;
    String url;
    ArrayList<MarkerType> placeMarkerArrayList;

    private Context context;

    private LocationScene locationScene;

    private ViewRenderable exampleLayoutRenderable;
    private ArSceneView arSceneView;
    private ArFragment arFragment;
    private Snackbar loadingMessageSnackbar;
    private MapsActivity mapsActivity;

    private boolean hasFinishedLoading = false;

    private CompletableFuture[] completableFutureArray;
    private HashMap<String, CompletableFuture> completableFutureHashMap;

    public GetNearbyPlacesData.AsyncResponse delegate = null;

    /*public GetNearbyPlacesData(Context context, LocationScene locationScene, ArSceneView arSceneView, ArFragment arFragment, Snackbar loadingMessageSnackbar, MapsActivity mapsActivity, GetNearbyPlacesData.AsyncResponse delegate){
        this.context = context;
        this.locationScene = locationScene;
        this.arSceneView = arSceneView;
        this.arFragment = arFragment;
        this.loadingMessageSnackbar = loadingMessageSnackbar;
        this.mapsActivity = mapsActivity;
        this.delegate = delegate;
    }*/

    public GetNearbyPlacesData(Context context, GetNearbyPlacesData.AsyncResponse delegate){
        this.context = context;
        this.delegate = delegate;
    }

    public interface AsyncResponse {
        void processFinish(ArrayList<Marker> output);

        void internetUnavailable();

        void getCompletableFutureArrayHashMap(HashMap<String, CompletableFuture> hm, CompletableFuture[] completableFutureArray, List<Places> nearbyPlaceList, ArrayList<MarkerType> placeMarkerArrayList);
    }



    @Override
    protected String doInBackground(Object... objects) {
        if(isInternetAvailable()) {
            //mMap = (GoogleMap) objects[0];
            mMap = (MapboxMap) objects[0];
            url = (String) objects[1];

            DownloadUrl downloadUrl = new DownloadUrl();
            try {
                googlePlacesData = downloadUrl.readUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return googlePlacesData;
        }
        else{
            googlePlacesData = "INTERNET_UNAVAILABLE";
            return googlePlacesData;
        }
    }

    @Override
    protected void onPostExecute(String s){
        if(s.equals("INTERNET_UNAVAILABLE")){
            delegate.internetUnavailable();
        }
        else {
            //List<HashMap<String, String>> nearbyPlaceList = null;
            List<Places> nearbyPlaceList = null;
            DataParser parser = new DataParser();
            nearbyPlaceList = parser.parse(s);
            if(nearbyPlaceList.size() == 0){
                Toast.makeText(context, "NO Nearby place found. Please try other filters", Toast.LENGTH_SHORT).show();
            }
            else {
                showNearbyPlaces(nearbyPlaceList);
                Toast.makeText(context, "Showing Nearby place", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showNearbyPlaces(List<Places> nearbyPlaceList){
        placeMarkerArrayList = new ArrayList<>();
        completableFutureHashMap = new HashMap<>();
        completableFutureArray = new CompletableFuture[nearbyPlaceList.size()];

        for(int i =0; i <nearbyPlaceList.size(); i++) {
            Places curPlace = nearbyPlaceList.get(i);
            MarkerOptions nearbyMarkerOptions = new MarkerOptions();
            //HashMap<String, String> googlePlace = nearbyPlaceList.get(i);   //googleplace = locationSourceList
            String placeName = curPlace.getName();
            String vicinity = curPlace.getVicinity();
            double lat = curPlace.getLat();
            double lng = curPlace.getLng();
            //String rating = googlePlace.get("rating");
            //String reference = googlePlace.get("reference");
            //String placeId = googlePlace.get("place_id");

            LatLng latLng = new LatLng(lat, lng);
            //nearbyMarkerOptions.title(placeName + "  : " + vicinity);
            nearbyMarkerOptions.title(placeName);
            nearbyMarkerOptions.setSnippet(vicinity);
            nearbyMarkerOptions.position(latLng);
            
            Marker placeMarker = mMap.addMarker(nearbyMarkerOptions);
            placeMarkerArrayList.add(new MarkerType(placeMarker, "Place_Marker"));

            CompletableFuture<ViewRenderable> completableFuture = ViewRenderable.builder()
                    .setView(context, R.layout.nearbyplace_info_ar_layout)
                    .build();
            completableFutureHashMap.put(placeName, completableFuture);
            completableFutureArray[i] = completableFuture;
        }

        delegate.getCompletableFutureArrayHashMap(completableFutureHashMap, completableFutureArray,nearbyPlaceList, placeMarkerArrayList);

        /*CompletableFuture
                .allOf(completableFutureArray)
                .handle(
                        (notUsed, throwable) -> {
                            if (throwable != null) {
                                DemoUtils.displayError(context, "Unable to load renderables", throwable);
                                return null;
                            }
                            //try {
                                //exampleLayoutRenderable = exampleLayout.get();
                                //andyRenderable = andy.get();
                                hasFinishedLoading = true;

                            //} catch (InterruptedException | ExecutionException ex) {
                            //    DemoUtils.displayError(context, "Unable to load renderables", ex);
                            //}

                            return null;
                        });

        arSceneView
                .getScene()
                .setOnUpdateListener(
                    frameTime -> {
                        if (!hasFinishedLoading) {
                            return;
                        }
                        if (locationScene == null) {
                            locationScene = new LocationScene(context, mapsActivity, arSceneView);
                            //locationScene.setAnchorRefreshInterval(ANCHOR_REFRESH_INTERVAL);

                            for(int i1 =0; i1 < nearbyPlaceList.size(); i1++) {

                                HashMap<String, String> googlePlace1 = nearbyPlaceList.get(i1);
                                String placeName1 = googlePlace1.get("place_name");
                                String vicinity1 = googlePlace1.get("vicinitiy");
                                double lat1 = Double.parseDouble(googlePlace1.get("lat"));
                                double lng1 = Double.parseDouble(googlePlace1.get("lng"));

                                LocationMarker layoutLocationMarker = new LocationMarker(lng1, lat1, new Node());
                                CompletableFuture<ViewRenderable> completableFuture = completableFutureHashMap.get(placeName1);

                                    try {
                                        ViewRenderable viewRenderable = completableFuture.get();
                                        layoutLocationMarker.node.setRenderable(viewRenderable);
                                        //layoutLocationMarker.node.setOnTapListener((v, event) -> Toast.makeText(this, placeName1 +" - distance "+ layoutLocationMarker.anchorNode.getDistance() + " meters", Toast.LENGTH_LONG).show());
                                        //layoutLocationMarker.setOnlyRenderWhenWithin(ONLY_RENDER_WHEN_WITHIN);
                                        TextView nameTextView = viewRenderable.getView().findViewById(R.id.textView);
                                        nameTextView.setText(placeName1);
                                        layoutLocationMarker.setRenderEvent(node -> {
                                            TextView distanceTextView = viewRenderable.getView().findViewById(R.id.textView2);
                                            distanceTextView.setText(node.getDistance() + "m");
                                        });
                                        locationScene.mLocationMarkers.add(layoutLocationMarker);
                                        //locationScene.setOffsetOverlapping(true); // attempt to move one of the markers vertically so they don't overlap. (It needs some work still however, as its not entirely reliable)
                                    } catch (InterruptedException | ExecutionException e) {
                                        e.printStackTrace();
                                        DemoUtils.displayError(context, "Unable to load renderables", e);
                                    }


                                //layoutLocationMarker.setRenderEvent(new LocationNodeRender() {
                                //    @Override
                                //    public void render(LocationNode node) {
                                //        View eView = exampleLayoutRenderable.getView();
                                //        TextView locationName = eView.findViewById(R.id.textView);
                                //        locationName.setText(placeName1);
                                //        TextView distanceTextView = eView.findViewById(R.id.textView2);
                                //        distanceTextView.setText(node.getDistance() + "m");

                                 //   }
                                //});
                                //locationScene.mLocationMarkers.add(layoutLocationMarker);
                            }

                        }

                        Frame frame = arSceneView.getArFrame();
                        if (frame == null) {
                            return;
                        }

                        if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                            return;
                        }

                        if (locationScene != null) {
                            locationScene.processFrame(frame);
                        }

                        if (loadingMessageSnackbar != null) {
                            for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                                if (plane.getTrackingState() == TrackingState.TRACKING) {
                                    hideLoadingMessage();
                                    arFragment.getPlaneDiscoveryController().hide();
                                    arFragment.getPlaneDiscoveryController().setInstructionView(null);
                                }
                            }
                        }

                    });*/


            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }

    /*private Node getExampleView() {
        Node base = new Node();
        base.setRenderable(exampleLayoutRenderable);
        Context c = context;
        // Add  listeners etc here
        View eView = exampleLayoutRenderable.getView();
        eView.setOnTouchListener((v, event) -> {
            Toast.makeText(
                    c, "Location marker touched.", Toast.LENGTH_LONG)
                    .show();
            return false;
        });

        return base;
    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }*/

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

}
