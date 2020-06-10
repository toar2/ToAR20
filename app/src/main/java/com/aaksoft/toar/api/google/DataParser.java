package com.aaksoft.toar.api.google;

/*
    Created By Aasharib
    on
    15 August, 2018
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.aaksoft.toar.api.google.Information.Places;

/*
    Contains logic for parsing response of google api called
 */

public class DataParser {

    ////////        PLACES API DATA PARSING  RETURNING HASHMAP  /////////
    ////////////////////////////////////////////////////////////////////////////////////////
    /*
    private HashMap<String,String> getPlace(JSONObject googlePlaceJson){
        HashMap<String, String> googlePlacesHashMap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";
        String rating = "-NA-";
        String placeId = "";

        try {
            if(!googlePlaceJson.isNull("name")){
                placeName = googlePlaceJson.getString("name");
            }
            if(!googlePlaceJson.isNull("vicinity")){
                vicinity = googlePlaceJson.getString("vicinity");
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJson.getString("reference");
            rating = googlePlaceJson.getString("rating");
            placeId = googlePlaceJson.getString("place_id");

            googlePlacesHashMap.put("place_name", placeName);
            googlePlacesHashMap.put("vicinity", vicinity);
            googlePlacesHashMap.put("lat", latitude);
            googlePlacesHashMap.put("lng", longitude);
            googlePlacesHashMap.put("reference", reference);
            googlePlacesHashMap.put("rating", rating);
            googlePlacesHashMap.put("place_id", placeId);

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlacesHashMap;
    }

    private List<HashMap<String,String>> getPlaces(JSONArray jsonArray){
        int count = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
        HashMap<String, String> placesMap = null;

        for(int i = 0 ; i < count ; i++){
            try {
                placesMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placesMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return placesList;
    }

    public List<HashMap<String,String>> parse(String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }
    */
    ////////////////////    PLACES API DATA PARSING END   ////////////////////////


    /**************************************************************************************/
    /*#############///////        DIRECTION API DATA PARSING    /////////################*/
    ////////////////////////////////////////////////////////////////////////////////////////

    /*

    private HashMap<String , String> getDuration (JSONArray googleDirectionsJson){
        HashMap<String, String> googleDirectionsMap = new HashMap<>();
        String duration = "";
        String distance = "";

        try {
            //Log.d("json responce: ", googleDirectionsJson.toString());
            duration = googleDirectionsJson.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = googleDirectionsJson.getJSONObject(0).getJSONObject("distance").getString("text");

            googleDirectionsMap.put("duration", duration);
            googleDirectionsMap.put("distance", distance);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googleDirectionsMap;
    }

    public HashMap<String, String> parseDirectionsOnly(String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");

        }catch(JSONException e){
            e.printStackTrace();
        }
        return getDuration(jsonArray);
    }

    public DirectionsData parseDirectionsRoute(String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        DirectionsData directionsData = null;
        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
            String[] paths = getPaths(jsonArray);
            String[] maneuver = getManeuvers(jsonArray);
            String[] htmlInstruction = getHtmlInstruction(jsonArray);
            List<HashMap<String, String>> distanceDuration = getDurationDistance(jsonArray);
            directionsData = new DirectionsData(paths, maneuver, htmlInstruction, distanceDuration);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return directionsData;
    }

    public String[] getPaths(JSONArray googleStepJson){
        int count = googleStepJson.length();
        String[] polylines = new String[count];

        for(int i=0; i<count; i++){
            try {
                polylines[i] = getPath(googleStepJson.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return polylines;
    }

    public String getPath(JSONObject googlePathJson){
        String polyline="";
        try {
            polyline = googlePathJson.getJSONObject("polyline").getString("points");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyline;
    }

    public String[] getManeuvers(JSONArray googleManeuversJson){
        int count = googleManeuversJson.length();
        String[] maneuver = new String[count];
        for(int i = 0; i < count; i++){
            try {
                if(i == 0){
                    maneuver[i] = "Start Position";
                }
                else {
                    maneuver[i] = googleManeuversJson.getJSONObject(i).getString("maneuver");
                }
            } catch (JSONException e) {
                //maneuver[i] = "";
                e.printStackTrace();
            }
        }
        return  maneuver;
    }

    public String[] getHtmlInstruction(JSONArray googleHtmlJson){
        int count = googleHtmlJson.length();
        String[] htmlInstruction = new String[count];
        for(int i = 0; i < count; i++){
            try {
                htmlInstruction[i] = googleHtmlJson.getJSONObject(i).getString("html_instructions");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return htmlInstruction;
    }

    private List<HashMap<String , String>> getDurationDistance (JSONArray googleDirectionsJson){
        int count = googleDirectionsJson.length();
        List<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

        for(int i=0; i<count; i++){
            try {
            String distance = googleDirectionsJson.getJSONObject(i).getJSONObject("distance").getString("text");
            String duration = googleDirectionsJson.getJSONObject(i).getJSONObject("duration").getString("text");
            String startLatitude = googleDirectionsJson.getJSONObject(i).getJSONObject("start_location").getString("lat");
            String startLongitude = googleDirectionsJson.getJSONObject(i).getJSONObject("start_location").getString("lng");
            String endLatitude = googleDirectionsJson.getJSONObject(i).getJSONObject("end_location").getString("lat");
            String endLongitude = googleDirectionsJson.getJSONObject(i).getJSONObject("end_location").getString("lng");

            HashMap<String, String> distanceDurationMap = new HashMap<String, String>();
            distanceDurationMap.put("distance", distance);
            distanceDurationMap.put("duration", duration);
            distanceDurationMap.put("start_latitude", startLatitude);
            distanceDurationMap.put("start_longitude", startLongitude);
            distanceDurationMap.put("end_latitude", endLatitude);
            distanceDurationMap.put("end_longitude", endLongitude);
            arrayList.add(i,distanceDurationMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return arrayList;
    }

    */
    ////////////////////DIRECTION API DATA PARSING END   ////////////////////////


    ////////        PLACES API DATA PARSING RETURNING PLACES OBJECT  /////////
    ////////////////////////////////////////////////////////////////////////////////////////

    public List<Places> parse(String jsonData){  //parsePlacesAPIJson
        JSONArray jsonArrayResult = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonData);
            jsonArrayResult = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArrayResult);
    }

    private List<Places> getPlaces(JSONArray jsonArray){
        int count = jsonArray.length();
        Places place;
        List<Places> placesList = new ArrayList<>();
        for(int i = 0 ; i < count ; i++){
            try {
                place = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(place);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placesList;
    }

    private Places getPlace(JSONObject googlePlaceJson){
        double lat=0.0;
        double lng=0.0;
        String placeId="";
        String iconUrl="";
        String addr="";
        String name="";
        double rating=0.0;
        String vicinity="";
        String reference="";
        boolean isOpen = false;
        boolean isHoursDefined;
        boolean isPriceDefined;
        double price = 0.0;
        String hour="";
        String phoneNumber="";
        List<String> type= new ArrayList<>();

        Places place = new Places();

        try {
            if (!googlePlaceJson.isNull("name")) {
                name = googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("vicinity");
            }
            lat = Double.parseDouble(googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat"));
            lng = Double.parseDouble(googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng"));
            placeId = googlePlaceJson.getString("place_id");
            iconUrl = googlePlaceJson.getString("icon");
            reference = googlePlaceJson.getString("reference");
            rating = Double.parseDouble(googlePlaceJson.getString("rating"));
            JSONObject hours = googlePlaceJson.getJSONObject("opening_hours");
            isHoursDefined = hours != null && googlePlaceJson.getJSONObject("opening_hours").getBoolean("open_now");
            if(isHoursDefined){
                isOpen = googlePlaceJson.getJSONObject("opening_hours").getBoolean("open_now");
            }
            isPriceDefined = googlePlaceJson.has("price_level");
            if(isPriceDefined){
                price = googlePlaceJson.getDouble("price_level");
            }
            JSONArray jsonTypes = googlePlaceJson.getJSONArray("types");
            if (jsonTypes != null) {
                for (int a = 0; a < jsonTypes.length(); a++) {
                    type.add(jsonTypes.getString(a));
                }
            }
            if(googlePlaceJson.has("formatted_address")){
                addr = googlePlaceJson.getString("formatted_address");
            }
            if(googlePlaceJson.has("formatted_phone_number")) {
                phoneNumber = googlePlaceJson.getString("formatted_phone_number");
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        place.setName(name);
        place.setVicinity(vicinity);
        place.setLat(lat);
        place.setLng(lng);
        place.setPlaceId(placeId);
        place.setIconUrl(iconUrl);
        place.setAddr(addr);
        place.setPhoneNumber(phoneNumber);
        place.setReference(reference);
        place.setRating(rating);
        place.setOpen(isOpen);
        place.setPrice(price);
        place.setType(type);
        return place;
    }
    ////////////////////    PLACES API DATA PARSING INTO PLACES CLASS END   ////////////////////////
}
