package com.aaksoft.toar.api.google;

/*
    Created By Aasharib
    on
    15 August, 2018
 */

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetDirectionsData extends AsyncTask<Object, String, String> {

    GoogleMap mMap;
    String url;
    String googleDirectionsData;
    String duration;
    String distance;
    LatLng latLng;
    ArrayList<Marker> markerStepsArray = new ArrayList<Marker>();

    public interface AsyncResponse {
        void processFinish(ArrayList<Marker> output);

        void internetUnavailable();
    }

    public AsyncResponse delegate = null;

    public GetDirectionsData(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(Object... objects) {
        if(isInternetAvailable()) {
            mMap = (GoogleMap) objects[0];
            url = (String) objects[1];
            latLng = (LatLng) objects[2];

            DownloadUrl downloadUrl = new DownloadUrl();
            try {
                googleDirectionsData = downloadUrl.readUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            googleDirectionsData = "INTERNET_UNAVAILABLE";
        }

        return googleDirectionsData;
    }

    /*@Override
    protected void onPostExecute(String s) {
        HashMap<String, String> directionList = null;
        DataParser dataParser = new DataParser();
        directionList = dataParser.parseDirectionsOnly(s);

        duration = directionList.get("duration");
        distance = directionList.get("distance");

        mMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(true);
        markerOptions.title("Duration ="+duration);
        markerOptions.snippet("Distance ="+ distance);

        mMap.addMarker(markerOptions);
    }*/

    @Override
    protected void onPostExecute(String s) {

        if(s.equals("INTERNET_UNAVAILABLE")){
            delegate.internetUnavailable();
        }
        else {

            String[] directionsList;
            List<HashMap<String, String>> stepsList;
            String[] maneuvers;
            String[] htmlInstruction;
            DataParser parser = new DataParser();
            //DirectionsData directionsData = parser.parseDirectionsRoute(s);
            ////directionsList = parser.parseDirectionsRoute(s);
            //directionsList = directionsData.getPolyline();
            //stepsList = directionsData.getDistanceDuration();
            //maneuvers = directionsData.getManeuver();
            //htmlInstruction = directionsData.getHtmlInstruction();
            //displayDirection(directionsList);
            //displayStepsInfoMarkers(stepsList, maneuvers, htmlInstruction);
            delegate.processFinish(markerStepsArray);
            /*JSONObject jSONObject;
            try {
                jSONObject = new JSONObject(s);

            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }
    }

    public void displayDirection(String[] directionsList) {
        int count = directionsList.length;
        for(int i = 0; i<count; i++){
            PolylineOptions options = new PolylineOptions();
            options.color(Color.RED);
            options.width(10);
            options.addAll(PolyUtil.decode(directionsList[i]));

            mMap.addPolyline(options);
        }
    }

    public void displayStepsInfoMarkers(List<HashMap<String, String>> stepsList, String[] maneuvers, String[] htmlInstruction){
        ArrayList<Marker> markerArray = new ArrayList<Marker>();
        for (int i = 0; i < stepsList.size(); i++){
            HashMap<String, String> directionData = stepsList.get(i);
            String start_lat = directionData.get("start_latitude");
            String start_lng = directionData.get("start_longitude");
            String end_lat = directionData.get("end_latitude");
            String end_lng = directionData.get("end_longitude");
            String distance = directionData.get("distance");
            String duration = directionData.get("duration");
            String instruction = Jsoup.parse(htmlInstruction[i]).text();
            LatLng stepLatLng = new LatLng(Double.parseDouble(start_lat),Double.parseDouble(start_lng));
            // placing markers at start positions
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(stepLatLng);
            markerOptions.title(maneuvers[i]);
            markerOptions.snippet(instruction + "\n" +"Distance: " + distance + "\n" + "Duration: " + duration + "\n");

            Marker marker = mMap.addMarker(markerOptions);
            markerArray.add(marker);

            // handling end position markers
            if(stepsList.size() - i == 1){
                MarkerOptions markerOptionsEnd = new MarkerOptions();
                LatLng destinationLatLng = new LatLng(Double.parseDouble(end_lat),Double.parseDouble(end_lng));
                markerOptionsEnd.position(destinationLatLng);
                markerOptionsEnd.title("Destination");
                Marker endMarker = mMap.addMarker(markerOptionsEnd);
                markerArray.add(endMarker);
            }
        }
        markerStepsArray = markerArray;
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

    /*public List<List<HashMap<String,String>>> parse(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONArray)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONArray)jLegs.get(j)).getJSONArray("steps");

                    for(int k=0;k<jSteps.length();k++){

                        String html_instructions = jSteps.get(k).getString("html_instructions");
                        String travel_mode = jSteps.get(k).getString("travel_mode");
                        String maneuver = jSteps.get(k).getString("maneuver");

                        String distance_text = jSteps.get(k).getJSONObject("distance").getString("text");
                        String distance_value = jSteps.get(k).getJSONObject("distance").getString("value");

                        String duration_text = jSteps.get(k).getJSONObject("duration").getString("text");
                        String duration_value = jSteps.get(k).getJSONObject("duration").getString("value");

                        String start_lat = jSteps.get(k).getJSONObject("start_location").getString("lat");
                        String start_lon = jSteps.get(k).getJSONObject("start_location").getString("lng");

                        String end_lat = jSteps.get(k).getJSONObject("end_location").getString("lat");
                        String end_lon = jSteps.get(k).getJSONObject("end_location").getString("lng");

                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);


                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }


        return routes;
    }*/

}
