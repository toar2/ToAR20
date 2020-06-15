/*
    Created By Aasharib
    on
    25 June, 2018
 */

package com.aaksoft.toar.activities;

/*
    Created By Aasharib
    on
    16 June, 2018
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;

import com.aaksoft.toar.firebase.Memory;
import com.aaksoft.toar.firebase.Users;
import com.aaksoft.toar.firebase.contact;
import com.aaksoft.toar.firebase.getJointNode;
import com.google.android.gms.location.LocationListener;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;

import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;

// classes needed to add location layer
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

// classes to calculate a route
// classes needed to launch navigation UI
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import com.aaksoft.toar.arcore.CustomArFragment;
import com.aaksoft.toar.firebase.ImageManager;
import com.aaksoft.toar.firebase.ImagesData;
import com.aaksoft.toar.fragments.MenuFragment;
import com.aaksoft.toar.fragments.ModelSceneViewRenderFragment;
import com.aaksoft.toar.fragments.NavigationFragment;
import com.aaksoft.toar.R;

import com.aaksoft.toar.arcore.DemoUtils;
import com.aaksoft.toar.fragments.UserLocationHistoryFragment;
import com.aaksoft.toar.fragments.UserPictureCapturePreviewDetailFragment;
import com.aaksoft.toar.localdb.ImageInformation;
import com.aaksoft.toar.localdb.LocalDatabaseHelper;
import com.aaksoft.toar.localdb.utils.AppSetting;
import com.aaksoft.toar.mapbox.MarkerType;

import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;

import org.apache.commons.io.FileUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

/*
    This is the main activity of application. It contains all the logic for mapbox maps
    and ARCore camera. It is acting as facade for integrating all the application
*/

public class MapsActivity extends FragmentActivity implements
        LocationListener,
        SensorEventListener,
        LocationEngineListener,
        PermissionsListener {

    //Mapbox related variable
    // variable for adding mapbox map
    private MapboxMap mapboxMap;
    private MapView mapView;
    private BuildingPlugin buildingPlugin;
    // variables for adding location layer
    private LocationLayerPlugin locationLayerPlugin;
    private LocationEngine locationEngine;
    private Location lastLocation;
    private PermissionsManager permissionsManager;

    public List<contact> userContacts;
    private Button trackModeButton;
    private Button startNavigationButton;
    private Button dontNavigateButton;
    private Button mapModeButton;
    private Button hybridModeButton;
    private Button cameraModeButton;
    private Button directionApiButton;
    private Button testButton;
    private Button cancelNearbyRenderButton;

    private Button menuButton;
    public Button takePicButton;

    public TextView userSigninStatusTV;
    public Button userSignStatusButton;

    private LinearLayout navigationOptionPanel;
    private RelativeLayout map_container;
    private RelativeLayout ar_container;
    private LinearLayout sceneformFragmentContainer;
    // defining map mode variable
    private int curAppMode = 0;
    private boolean modeTrack = false;
    private boolean navigationMode = false;
    private boolean islocationLayerPluginEnabled;
    private LatLng lastLocationPos = null;

    private double latitude, longitude;
    private double endLatitude, endLongitude;

    boolean isDestinationSelected = false;
    // defining sensor manager for getting the orientation of device
    protected SensorManager mSensorManager;
    protected Sensor mOrientation;
    // variables for calculating and drawing a route on mapbox using MAPBOX direction api


    private Point originPosition;
    private Point destinationPosition;
    private DirectionsRoute currentRoute;

    boolean pendingNavigation = false;
    int modelToNavigateTo;



    private static final String TAG = "DirectionsActivity";     // used to debug errors in navigation activity
    private NavigationMapRoute navigationMapRoute;
    //  Defining Sceneform variables
    private boolean installRequested;

    private Snackbar loadingMessageSnackbar = null;
    private ArSceneView arSceneView;
    public ArFragment fragment;

    public LocationScene getLocationScene() {
        return locationScene;
    }

    // Defining ARCore-Location scene variable
    private LocationScene locationScene;
    ArrayList<com.google.android.gms.maps.model.Marker> markerStepsArray = null;

    // Defining Mapbox Navigation fragment variables
    private FrameLayout navigationView;
    NavigationFragment navigationFragment;
    MenuFragment menuFragment;



    //Location Based History Variables
    private boolean isLocationHistoryEnabled;
    public LocalDatabaseHelper localDatabaseHelper;
    private ViewRenderable viewRenderable;
    private Anchor anchorLocationHistoryView;

    int imageRenderableDistance = 100;
    ArrayList<Bitmap> imagesToBeShown;
    ArrayList<String> commentsToBeShown;
    ArrayList<String> datesToBeShown;
    ArrayList<String> ownerNameToBeShown;
    ArrayList<LatLng> latLngsToBeShown;
    IconFactory iconFactory;

    ArrayList<MarkerType> markerTypeArrayList;
    //HashMap<String,Marker> markerTypeHashmap;

    public int STORAGE_PERMISSION = 1;
    private AlertDialog alertDialog;

    private String uniqueUserID = "1";

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private String userName = "local";

    public boolean isMenuBeingDisplayed;

    private int curNavMode = 0;



    private FirebaseAuth mAuth;                             // this is used to sign up the user
    public FirebaseUser currentUser;
    StorageReference userImagesStorageReference;
    DatabaseReference usersDatabaseReference;
    DatabaseReference usersImagesDatabaseReference;

    public Users currentUserPojo;
    public boolean beenGreetedOnce = false;
    public Boolean isUserSignedIn = false;





    String[] modelsNames = new String[] {"serena.sfb", "centaurus.sfb", "faisalmosque.sfb", "monument.sfb", "library.sfb", "convention.sfb" ,"parliment.sfb", "nice.sfb", "nustgatethree.sfb", "nustgateten.sfb", "nustmosque.sfb", "seecs.sfb"};
    String[] modelsTitles = new String[] {"Serena Hotel", "Centaurus Mall", "Faisal Mosque", "Monument", "Nust Library", "Convention Center", "Paliment Building", "Nust NICE", "Nust Gate 3", "Nust Gate 10", "Nust Mosque", "Nust SEECS"};
    LatLng[] modelCoordinates = new LatLng[] {new LatLng(33.7153,73.1020), new LatLng(33.7077,73.0501), new LatLng(33.7299,73.0383), new LatLng(33.6931,73.0689), new LatLng(33.6421,72.9923), new LatLng(33.7182,73.1055), new LatLng(33.7302,73.0971), new LatLng(33.640538  ,72.984592), new LatLng(33.64609 ,72.980647), new LatLng(33.649184,72.999722), new LatLng(33.643992,72.985466), new LatLng(33.64284793989045,72.99053166073543)};





    @Override
    protected void onCreate(Bundle savedInstanceState) {




        // Test button in enabled

        int testMode = 0;

        mAuth = FirebaseAuth.getInstance();         // Fetching an instance of Firebase
        currentUser = mAuth.getCurrentUser();       // Checking status of current user

        userContacts = new ArrayList<contact>();


        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_maps);

        permissionsManager = new PermissionsManager(this);
        //checkPermissions();

        fragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
        arSceneView = fragment.getArSceneView();

        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        navigationView = findViewById(R.id.navigation_view_fragment);

        mapView.setStyleUrl("mapbox://styles/muhammadaasharib/cjnrnmpvi18cu2sqm8vqcw44w");


        markerTypeArrayList = new ArrayList<>();


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        map_container = findViewById(R.id.map_container);
        ar_container = findViewById(R.id.ar_container);
        sceneformFragmentContainer = findViewById(R.id.ar_fragment_container);


        userSignStatusButton = findViewById(R.id.userSignStatusButton);

        userSignStatusButton.setOnClickListener(view1->{
            UserLocationHistoryFragment userLocationHistoryFragment = new UserLocationHistoryFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.screen_container, userLocationHistoryFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });



        menuButton =  findViewById(R.id.menuButton);
        menuButton.setOnClickListener(view1 -> {
            if(!isMenuBeingDisplayed) {
                isMenuBeingDisplayed = true;
                menuFragment = MenuFragment.newInstance();
                //showDialog(menuFragment.getView());
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.screen_container, menuFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        mapModeButton =  findViewById(R.id.mapModeButton);
        mapModeButton.setOnClickListener(view1 -> {
            switchToMapMode();
        });

        hybridModeButton =  findViewById(R.id.hybridModeButton);
        hybridModeButton.setOnClickListener(view1 -> {
            switchToHybridMapMode();
        });

        cameraModeButton =  findViewById(R.id.cameraModeButton);
        cameraModeButton.setOnClickListener(view1 -> {
            switchToCameraMode();
        });

        takePicButton = findViewById(R.id.takepic_button1);
        takePicButton.setOnClickListener(view1->{
            takePhoto();
        });


        testButton =  findViewById(R.id.buttonTest1);
        testButton.setVisibility(View.GONE);
        cameraModeButton.setVisibility(View.VISIBLE);

        if (testMode == 1) {
            testButton.setVisibility(View.VISIBLE);
            cameraModeButton.setVisibility(View.GONE);

        }

        testButton.setOnClickListener(view1 -> {

            if(currentUser!= null) {
                synchronizeLocalDbWithCloud();
            }
            else{
                // TODO: Alert user to login before trying to sync local db with cloud
                alertDisplayer("Login To Continue", "Please login before attempting to sync local db with cloud");
            }
                });








        directionApiButton =  findViewById(R.id.buttonDirectionApi);
        directionApiButton.setOnClickListener(view1 -> {
            routeNavigate(endLongitude, endLatitude);
        });

        localDatabaseHelper = new LocalDatabaseHelper(this);

        setSettingVariables(localDatabaseHelper);

        if(isLocationHistoryEnabled) {
            userSignStatusButton.setVisibility(View.VISIBLE);
            if (!userName.equals("") && !userName.equals("local") && userName != null) {
                userSignStatusButton.setText("Signed in as: " + userName);
                userSignStatusButton.setBackgroundResource(R.drawable.user_login_status_green_tv);
                userSignStatusButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                //userSigninStatusTV.setBackgroundColor("#0f0");
            } else {
                userSignStatusButton.setText("Not Logged In");
                userSignStatusButton.setBackgroundResource(R.drawable.user_login_status_red_tv);
                userSignStatusButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            }
        }

        iconFactory = IconFactory.getInstance(this);

        trackModeButton =  findViewById(R.id.trackButton);
        if(islocationLayerPluginEnabled == false){
            trackModeButton.setBackgroundResource(R.drawable.icons_track_48);
        }
        trackModeButton.setOnClickListener(view1 -> {
            if(islocationLayerPluginEnabled) {
                modeTrack = !modeTrack;
                if (modeTrack) {
                    trackModeButton.setBackgroundResource(R.drawable.icons_track_48);
                    if (lastLocationPos != null) {
                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())) // Sets the new camera position
                                .zoom(17) // Sets the zoom
                                .bearing(180) // Rotate the camera
                                .tilt(30) // Set the camera tilt
                                .build(); // Creates a CameraPosition from the builder

                        mapboxMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(position), 7000);
                        //MapsActivity.this.mapboxMap.animateCamera(CameraUpdateFactory.newLatLng(lastLocationPos));
                    } else {
                        Toast.makeText(getApplicationContext(), "Can not get user location", Toast.LENGTH_SHORT).show();
                        trackModeButton.setBackgroundResource(R.drawable.icons_track_48);
                    }
                } else {
                    trackModeButton.setBackgroundResource(R.drawable.icons_untrack_48);
                }
            }
            else{
                enableLocationPlugin();
                trackModeButton.setBackgroundResource(R.drawable.icons_untrack_48);
            }
        });

        startNavigationButton =  findViewById(R.id.navigateStart);
        startNavigationButton.setOnClickListener(view1 -> {
            if(!modeTrack){
                trackModeButton.performClick();
            }
            navigationMode(true);

            navigatingButtonsVisibility(true);
            boolean simulateRoute = false;

            if (curAppMode == 1) {//map mode
                LinearLayout.LayoutParams showParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.90f);
                LinearLayout.LayoutParams hideParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.0f);
                map_container.setLayoutParams(showParam);
                ar_container.setLayoutParams(hideParam);
                navigationView.setVisibility(View.VISIBLE);
                mapView.setVisibility(View.GONE);
                navigationFragment = new NavigationFragment(currentRoute);
                setFragment(navigationFragment);  //make a fragment for turn by turn navigation
            }
            if (curAppMode == 0) {
                LinearLayout.LayoutParams showParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.44f);
                LinearLayout.LayoutParams hideParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.47f);
                map_container.setLayoutParams(hideParam);
                ar_container.setLayoutParams(showParam);
                navigationView.setVisibility(View.VISIBLE);
                mapView.setVisibility(View.GONE);
                navigationFragment = new NavigationFragment(currentRoute);
                setFragment(navigationFragment);  //make a fragment for turn by turn navigation
            }
        });

        dontNavigateButton =  findViewById(R.id.dontNavigateButton);
        dontNavigateButton.setOnClickListener(view1 -> {
            deselectDestination();

            navigationMode = false;
            navigationMode(false);
            navigatingButtonsVisibility(false);


            if (curAppMode == 1) {//map mode
                LinearLayout.LayoutParams showParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.90f);
                LinearLayout.LayoutParams hideParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.0f);
                map_container.setLayoutParams(showParam);
                ar_container.setLayoutParams(hideParam);
                navigationView.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
                if (navigationFragment != null) {
                    removeFragment(navigationFragment);
                }
            }
            if (curAppMode == 0) {//hybrid mode
                LinearLayout.LayoutParams showParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.51f);
                LinearLayout.LayoutParams hideParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.39f);
                map_container.setLayoutParams(hideParam);
                ar_container.setLayoutParams(showParam);
                navigationView.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
                if (navigationFragment != null) {
                    removeFragment(navigationFragment);
                }
            }
        });


        navigationOptionPanel =  findViewById(R.id.navigationPanel);
        navigationOptionPanel.setVisibility(View.GONE);

        mapView.getMapAsync(new com.mapbox.mapboxsdk.maps.OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                MapsActivity.this.mapboxMap = mapboxMap;
                buildingPlugin = new BuildingPlugin(mapView, MapsActivity.this.mapboxMap);
                buildingPlugin.setVisibility(true);
                if (PermissionsManager.areLocationPermissionsGranted(MapsActivity.this)) {
                    trackModeButton.setBackgroundResource(R.drawable.icons_untrack_48);
                    enableLocationPlugin();
                }
                MapsActivity.this.mapboxMap.getUiSettings().setCompassEnabled(false);
                MapsActivity.this.mapboxMap.setOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NonNull com.mapbox.mapboxsdk.geometry.LatLng latLng) {
                        if(!isDestinationSelected){
                            MapsActivity.this.selectDestination(latLng);
                        }
                        else{
                            MapsActivity.this.deselectDestination();
                        }
                    }
                });


                MapsActivity.this.mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        //Toast.makeText(MapsActivity.this, marker.getTitle(), Toast.LENGTH_LONG).show();
                        URI model=null;
                        for(int i = 0; i < 12; i++){
                            if(marker.getTitle().equals(modelsTitles[i])){
                                try{
                                    model = new URI(modelsNames[i].replace(" ", "%20"));
                                    break;
                                }
                                catch(URISyntaxException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                        if(model != null) {
                            ModelSceneViewRenderFragment modelSceneViewRenderFragment = ModelSceneViewRenderFragment.newInstance(model);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.container, modelSceneViewRenderFragment);
                            fragmentTransaction.commit();
                            fragmentTransaction.addToBackStack(null);
                        }
                        return true;
                    }
                });
                setBuildingModelMarker();

                createTimerTask(0,10000);
//                createTimerTaskForSync(0,10000);
            }
        });



        // TODO: Fix the following
        fragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    //makeRenderablePicture();
                    if (viewRenderable == null) {
                        return;
                    }
                    anchorLocationHistoryView = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchorLocationHistoryView);
                    anchorNode.setParent(fragment.getArSceneView().getScene());

                    // Create the transformable andy and add it to the anchor.
                    TransformableNode andy = new TransformableNode(fragment.getTransformationSystem());
                    andy.setParent(anchorNode);
                    andy.setRenderable(viewRenderable);
                    andy.select();

                });


        refreshCurrentUser();


    }


    public void routeNavigate(double endLongitude, double endLatitude) {
        if (isNetworkConnected()) {
            if (isDestinationSelected) {

                navigationMode = true;
                navigationMode(navigationMode);
                destinationPosition = Point.fromLngLat(endLongitude, endLatitude);
                originPosition = Point.fromLngLat(lastLocation.getLongitude(), lastLocation.getLatitude());

                getRoute(originPosition, destinationPosition);


                alertDisplayer("Safety Warning", "Please be aware of your surrounding while navigating.");
                if(curAppMode == 0){
                    alertBatterySaverDisplayer("Battery Saving Advice", "You can save battery by navigating in map mode.");
                }

            } else {
                Toast.makeText(getApplicationContext(), "Please Select Destination by long pressing map to drop marker", Toast.LENGTH_LONG).show();

            }
        } else {
            Toast.makeText(MapsActivity.this, "Internet Unavailable", Toast.LENGTH_SHORT).show();

        }
    }

    private void showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
            return;
        }
        loadingMessageSnackbar =
                Snackbar.make(
                        MapsActivity.this.findViewById(android.R.id.content),
                        R.string.plane_finding,
                        Snackbar.LENGTH_INDEFINITE);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();
    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }
        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }

    @Override
    protected void onStart() {


        super.onStart();
        if (locationEngine != null)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationEngine.requestLocationUpdates();

                return;
            }
        mapView.onStart();
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }

//        refreshCurrentUser();


    }

    @Override protected void onResume() {
//
        super.onResume();


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mapView.onResume();
        fragment.getPlaneDiscoveryController().hide();
        fragment.getPlaneDiscoveryController().setInstructionView(null);
        mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_FASTEST);
        if (locationScene != null) {
            locationScene.resume();
        }
        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = DemoUtils.createArSession(this, installRequested);
                if (session == null) {
                    installRequested = ARLocationPermissionHelper.hasPermission(this);
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                DemoUtils.handleSessionException(this, e);
            }
        }
        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            DemoUtils.displayError(this, "Unable to get camera", ex);
            finish();
            return;
        }
        if (arSceneView.getSession() != null) {
            //showLoadingMessage();
        }

        if(getIntent().getStringExtra("navigate")!=null){


            switchToMapMode();                          // if user wants to navigate to somewhere
            pendingNavigation = true;                   // pending navigation is set to true
            modelToNavigateTo = getIntent().getIntExtra("navigateToModelNumber", 0) - 1;



        }

//        refreshCurrentUser();
    }

    @Override protected void onPause() {
        super.onPause();
        mapView.onPause();
        mSensorManager.unregisterListener(this);
        //releaseCamera();
        if (locationScene != null) {
            locationScene.pause();
        }
        arSceneView.pause();


    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        try {
            arSceneView.destroy();
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("Destroy: ", e.toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isMenuBeingDisplayed = false;
    }

    //@SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            islocationLayerPluginEnabled = true;
            initializeLocationEngine();

            if(mapboxMap != null) {
                LocationLayerPlugin locationLayerPlugin = new LocationLayerPlugin(mapView, mapboxMap);
                // Set the plugin's camera mode
                locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
                getLifecycle().addObserver(locationLayerPlugin);
            }

        } else {
            //permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    //@SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {
        LocationEngineProvider locationEngineProvider = new LocationEngineProvider(this);
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.setInterval(0);
        locationEngine.activate();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location lastLocation = locationEngine.getLastLocation();

        if (lastLocation != null) {
            //originLocation = lastLocation;
            latitude = lastLocation.getLatitude();
            longitude = lastLocation.getLongitude();
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(permissionsManager!=null) {
            permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        if (requestCode == STORAGE_PERMISSION && (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)){
            alertDialogRuntimePermission(MapsActivity.this, getApplicationContext(),"Storage Permission Required", "Application require storage permission for storing images", "ok", "back", Manifest.permission.WRITE_EXTERNAL_STORAGE, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
        }
    }

    private void alertDialogRuntimePermission(Activity activity, Context context, String title, String message, String acceptButtonText, String negativeButtonText, String checkPermission ,String[] permissionsRequested, int requestCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(message);
        builder.setPositiveButton(acceptButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!(ActivityCompat.checkSelfPermission(context, checkPermission) == PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(activity, permissionsRequested, requestCode);
                }
            }
        });
        builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
            trackModeButton.performClick();
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
                locationEngine.requestLocationUpdates();
            }
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

            }
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onConnected() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null){
            Toast.makeText(this, "Sorry We Can't able to Get Your Current Location", Toast.LENGTH_SHORT).show();
        }
        else {
            lastLocation = location;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            lastLocationPos = new LatLng(location.getLatitude(), location.getLongitude());
            if (modeTrack || pendingNavigation) {
                /*change camera position on location changed if tracking mode*/
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())) // Sets the new camera position
                        .zoom(17) // Sets the zoom
                        .tilt(30) // Set the camera tilt
                        .build(); // Creates a CameraPosition from the builder
                if(mapboxMap != null) {
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1);
                }
            }
            if(pendingNavigation){

                pendingNavigation = false;
                selectDestination(modelCoordinates[modelToNavigateTo]);
                routeNavigate(modelCoordinates[modelToNavigateTo].getLongitude(), modelCoordinates[modelToNavigateTo].getLatitude());

            }
        }
    }









    private void updateCameraBearing(MapboxMap mapboxMap, float bearing) {
        if ( mapboxMap == null || lastLocation == null) return;
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude())) // Sets the new camera position
                .zoom(17) // Sets the zoom
                .tilt(60) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1);
        mapboxMap.animateCamera(CameraUpdateFactory.bearingTo(bearing));
    }


    static float mAzimuthOrientation = 0.0f;
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_ORIENTATION:
                float azimuth = event.values[0];
                if (Math.abs(azimuth-mAzimuthOrientation)>1.0f){
                    mAzimuthOrientation = azimuth;
                    if (modeTrack) {
                        updateCameraBearing(mapboxMap, mAzimuthOrientation);
                    }

                }
            default:
                break;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }





    private void getRoute(Point origin, Point destination) {
//        Log.d("info" , "onRouteRan");
        String choice = DirectionsCriteria.PROFILE_DRIVING;
        if(getCurNavMode() == 0){
             choice = DirectionsCriteria.PROFILE_WALKING;
        }
        else if(getCurNavMode() == 1){
            choice = DirectionsCriteria.PROFILE_DRIVING;
        }
        else if(getCurNavMode() == 2){
            choice = DirectionsCriteria.PROFILE_CYCLING;
        }

        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .profile(choice)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
//                        Toast.makeText((MapsActivity)getApplicationContext(), "get route" , Toast.LENGTH_LONG).show();
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }
                        currentRoute = response.body().routes().get(0);
                        Log.d("info" , "currentRouteAssigned");
                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();

                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationViewLight);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());

                    }
                });
    }

    // TODO: automate marker addition

    private void setBuildingModelMarker(){

        Icon icon = iconFactory.fromResource( R.drawable.icons_3d_model_marker_48);
        for (int i = 0; i < 12; i++){
            markerTypeArrayList.add(new MarkerType(mapboxMap.addMarker(new MarkerOptions().position(modelCoordinates[i]).title(modelsTitles[i]).icon(icon)) , "3D_Model_Marker"));
        }

    }

    protected void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.navigation_view_fragment, fragment);
        fragmentTransaction.commit();
    }

    protected void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }


    public void navigationMode(boolean enable){
        if(enable){
            trackModeButton.setVisibility(View.GONE);
            directionApiButton.setVisibility(View.INVISIBLE);
            navigationOptionPanel.setVisibility(View.VISIBLE);
        }
        else{
            trackModeButton.setVisibility(View.VISIBLE);
            directionApiButton.setVisibility(View.VISIBLE);
            navigationOptionPanel.setVisibility(View.GONE);
        }
    }

    public void navigatingButtonsVisibility(boolean navigating){
        if(navigating){
            startNavigationButton.setVisibility(View.GONE);
        }
        else{
            startNavigationButton.setVisibility(View.VISIBLE);
        }
    }

    public void switchToCameraMode() {
        if(curAppMode!=2) {
            if(navigationMode){
                Toast.makeText(getApplicationContext(), "Cannot Switch to full Camera Mode during navigation", Toast.LENGTH_SHORT).show();
                return;
            }
            curAppMode = 2;         // currAppMode 2 means camera mode
            LinearLayout.LayoutParams showParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.90f);
            LinearLayout.LayoutParams hideParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0f);
            map_container.setLayoutParams(hideParam);
            ar_container.setLayoutParams(showParam);
            sceneformFragmentContainer.setVisibility(View.VISIBLE);
            //sceneformFragmentContainer.setLayoutParams(showParam);
            fragment.getPlaneDiscoveryController().hide();
            fragment.getPlaneDiscoveryController().setInstructionView(null);
            if (locationScene != null) {
                locationScene.resume();
            }
            if (arSceneView.getSession() == null) {
                // If the session wasn't created yet, don't resume rendering.
                // This can happen if ARCore needs to be updated or permissions are not granted yet.
                try {
                    Session session = DemoUtils.createArSession(this, installRequested);
                    if (session == null) {
                        installRequested = ARLocationPermissionHelper.hasPermission(this);
                        return;
                    } else {
                        arSceneView.setupSession(session);
                    }
                } catch (UnavailableException e) {
                    DemoUtils.handleSessionException(this, e);
                }
            }
            try {
                arSceneView.resume();
            } catch (CameraNotAvailableException ex) {
                DemoUtils.displayError(this, "Unable to get camera", ex);
                finish();
                return;
            }
            if (arSceneView.getSession() != null) {
                //showLoadingMessage();
            }
        }
    }

    public void switchToHybridMapMode() {
        if(curAppMode!=0) {         // hybrid map mode is 0
            curAppMode = 0;
            LinearLayout.LayoutParams camParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.51f);
            LinearLayout.LayoutParams mapParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.39f);
            map_container.setLayoutParams(mapParam);
            //sceneformFragmentContainer.setLayoutParams(camParam);
            ar_container.setLayoutParams(camParam);
            sceneformFragmentContainer.setVisibility(View.VISIBLE);
            fragment.getPlaneDiscoveryController().hide();
            fragment.getPlaneDiscoveryController().setInstructionView(null);
            if (locationScene != null) {
                locationScene.resume();
            }
            if (arSceneView.getSession() == null) {
                // If the session wasn't created yet, don't resume rendering.
                // This can happen if ARCore needs to be updated or permissions are not granted yet.
                try {
                    Session session = DemoUtils.createArSession(this, installRequested);
                    if (session == null) {
                        installRequested = ARLocationPermissionHelper.hasPermission(this);
                        return;
                    } else {
                        arSceneView.setupSession(session);
                    }
                } catch (UnavailableException e) {
                    DemoUtils.handleSessionException(this, e);
                }
            }
            try {
                arSceneView.resume();
            } catch (CameraNotAvailableException ex) {
                DemoUtils.displayError(this, "Unable to get camera", ex);
                finish();
                return;
            }
            if (arSceneView.getSession() != null) {
                //showLoadingMessage();
            }
        }
    }

    public void switchToMapMode() {
        if(curAppMode!=1) {
            curAppMode = 1;
            //ar_container.setVisibility(View.GONE);
            if (navigationMode == false) {
                LinearLayout.LayoutParams showParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.90f);
                LinearLayout.LayoutParams hideParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0f);
                map_container.setLayoutParams(showParam);
                ar_container.setLayoutParams(hideParam);
            }//LinearLayout.LayoutParams tParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,0f);
            else {
                LinearLayout.LayoutParams showParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.90f);
                LinearLayout.LayoutParams hideParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0f);
                map_container.setLayoutParams(showParam);
                ar_container.setLayoutParams(hideParam);
            }
            sceneformFragmentContainer.setVisibility(View.GONE);
            if (locationScene != null) {
                locationScene.pause();
            }
            arSceneView.pause();
        }
    }

    public int getCurAppMode(){
        return curAppMode;
    }


    public void selectDestination(LatLng latLng){
        MarkerOptions markerOptions = new com.mapbox.mapboxsdk.annotations.MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("destination");
        MapsActivity.this.mapboxMap.clear();
        MapsActivity.this.mapboxMap.addMarker(markerOptions);
        endLatitude = latLng.getLatitude();
        endLongitude = latLng.getLongitude();
        setDestinationSelected(true);
    }

    public void deselectDestination(){

        mapboxMap.clear();                      // clearing the destination marker
        setBuildingModelMarker();
        navigationMode(false);
        setDestinationSelected(false);
        if(navigationMapRoute!=null) {
            navigationMapRoute.removeRoute();
        }

    }





    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null)
            return info.isConnected(); // WIFI connected
        else {
            return false;
        }
    }

    //run it on another thread
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }

    private List<Boolean> initializeBooleanArrayOfSizeN(int size) {
        List<Boolean> list = new ArrayList<Boolean>(size);
        for(int i=0; i<size; i++){
            list.add(false);
        }
        return list;
    }

    public boolean isDestinationSelected() {
        return isDestinationSelected;
    }

    public void setDestinationSelected(boolean destinationSelected) {
        isDestinationSelected = destinationSelected;
    }




    public boolean getIsLocationHistoryEnabled() {
        return isLocationHistoryEnabled;
    }

    public void setIsLocationHistoryEnabled(boolean isLocationHistoryEnabled) {
        this.isLocationHistoryEnabled = isLocationHistoryEnabled;
    }


    public String generateUniqueImageName(){
        String date = new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        String uniqueFileName = uniqueUserID + "_" + date + "_image.jpg";
        return uniqueFileName;
    }

    private String generateFilePathLocal(String uniqueFileName) {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "ToAR/" + uniqueUserID + "/" + uniqueFileName;
    }

    public boolean saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {
        boolean status;
        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            //bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
            status = true;
        } catch (IOException ex) {
            status = false;
            throw new IOException("Failed to save bitmap to disk", ex);
        }
        return status;
    }


    private void takePhoto() {
        final String uniqueFileName = generateUniqueImageName();
        final String localFilePath = generateFilePathLocal(uniqueFileName);
        ArSceneView view = fragment.getArSceneView();

        // Create a bitmap the size of the scene view.
        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        // Create a handler thread to offload the processing of the image.
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();

        // Make the request to copy.
        PixelCopy.request(view, bitmap, (copyResult) -> {
            if (copyResult == PixelCopy.SUCCESS) {
                MapsActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        UserPictureCapturePreviewDetailFragment userPictureCapturePreviewDetailFragment = UserPictureCapturePreviewDetailFragment.newInstance(bitmap, uniqueFileName, localFilePath, latitude, longitude, uniqueUserID, userName);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.screen_container, userPictureCapturePreviewDetailFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            } else {
                Toast toast = Toast.makeText(MapsActivity.this,
                        "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG);
                toast.show();
            }
            handlerThread.quitSafely();
        }, new Handler(handlerThread.getLooper()));
    }


    public ArrayList<ImageInformation> getImagesDataFromLocalDB(){
        ArrayList<ImageInformation> allResult = localDatabaseHelper.getImageInformation();
        return allResult;
    }

    public ArrayList<ImageInformation> getUnsyncImagesDataFromLocalDB(){
        ArrayList<ImageInformation> allResult = localDatabaseHelper.getUnSyncImageInformation(uniqueUserID);
        return allResult;
    }

    // Takes a list of images, and returns the images that can be rendered within the allowed distances
    public ArrayList<ImageInformation> getOnlyRenderableImages(ArrayList<ImageInformation> imageInformationList){

        ArrayList<ImageInformation> selectedRenderableImagesInformation = new ArrayList<>();

        for(ImageInformation imageInformation : imageInformationList){
            double lat = imageInformation.getLat();
            double lng = imageInformation.getLng();
            float distanceInMetre = calculateDistance(latitude,longitude,lat,lng);
            if(distanceInMetre < imageRenderableDistance){
                selectedRenderableImagesInformation.add(imageInformation);
            }
        }
        return selectedRenderableImagesInformation;
    }

    // Returns the distance between two locations
    public float calculateDistance(double userLat, double userLng, double venueLat, double venueLng) {
        Location location1 = new Location("");
        location1.setLatitude(userLat);
        location1.setLongitude(userLng);
        Location location2 = new Location("");
        location2.setLatitude(venueLat);
        location2.setLongitude(venueLng);
        return location1.distanceTo(location2);
    }

    public HashMap<Integer, Pair> retrieveImageFromStorage(ArrayList<ImageInformation> renderableImageList){
        HashMap<Integer,Pair> imageAndDescriptionHashmap = new HashMap<>();
        for(int i = 0; i<renderableImageList.size(); i++){
            ImageInformation imageInformation = renderableImageList.get(i);
            String imagePath = imageInformation.getName();
            File photoFile = new File(imagePath);
            if(photoFile.exists()) {    // to check whether image is present along with the comment
                Uri photoURI = FileProvider.getUriForFile(this, this.getPackageName() + ".ar.waisoft.name.provider", photoFile);

                Bitmap bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Pair<ImageInformation, Bitmap> imageAndDescriptionPair = new Pair<>(imageInformation, bitmap);
                imageAndDescriptionHashmap.put(i, imageAndDescriptionPair);
            }
        }
        return imageAndDescriptionHashmap;
    }

    public void showImages(HashMap<Integer, Pair> imageAndDescriptionHashmap){
        int totalImages = imageAndDescriptionHashmap.size();
        imagesToBeShown = new ArrayList<>(totalImages);
        commentsToBeShown = new ArrayList<>(totalImages);
        datesToBeShown = new ArrayList<>(totalImages);
        ownerNameToBeShown = new ArrayList<>(totalImages);
        latLngsToBeShown = new ArrayList<>(totalImages);
        for (int i = 0; i< totalImages; i++){
            Pair<ImageInformation,Bitmap> curImageInfo = imageAndDescriptionHashmap.get(i);
            if (curImageInfo != null) {
                commentsToBeShown.add(curImageInfo.first.getDescription());
                datesToBeShown.add(curImageInfo.first.getCreatedOn());
                ownerNameToBeShown.add(curImageInfo.first.getOwnerName());
                imagesToBeShown.add(curImageInfo.second);
                latLngsToBeShown.add(new LatLng(curImageInfo.first.getLat(),curImageInfo.first.getLng()));
            }
        }
    }

    public void makeRenderablePicture(){
        ViewRenderable.builder()
                .setView(getApplicationContext(), R.layout.image_ar_display_layout)
                .build()
                .thenAccept(renderable -> {
                    viewRenderable = renderable;
                    ImageView imgView = renderable.getView().findViewById(R.id.imageCard);
                    if(commentsToBeShown.size()>0) {
                        AtomicInteger i = new AtomicInteger(0);
                        TextView commentTextView = renderable.getView().findViewById(R.id.pictureCommentTextView);

                        Icon iconNew = iconFactory.fromResource(R.drawable.icons_locationhistory_selected_image_marker_48);
                        Icon iconOld = iconFactory.fromResource(R.drawable.icons_locationhistory_image_marker_48);

                        try {
                            int j = i.get();

                            commentTextView.setText(commentsToBeShown.get(j));
                            TextView dateTextView = renderable.getView().findViewById(R.id.pictureDateTextView);
                            TextView ownerTextView = renderable.getView().findViewById(R.id.pictureOwnerTextView);

                            dateTextView.setText(datesToBeShown.get(j));
                            ownerTextView.setText(ownerNameToBeShown.get(j));

                            imgView.setImageBitmap(imagesToBeShown.get(j));
                            imgView.setRotation(0);

                            /////////////////////////////////

                            LatLng curImageLatLng = latLngsToBeShown.get(j);
                            showCurImageMarker(curImageLatLng, iconNew);


                            Button previousImageButton = renderable.getView().findViewById(R.id.showPreviousPictureButton);
                            previousImageButton.setOnClickListener(view1 -> {
                                Animation anim = AnimationUtils.loadAnimation(this, R.anim.fadein);
                                anim.reset();
                                imgView.startAnimation(anim);
                                int k = 0;
                                if (imagesToBeShown.size() - 1 < i.get()) {
                                    if (imagesToBeShown.size() > 0) {
                                        i.set(0);
                                        k = i.get();
                                    } else {
                                        return;
                                    }
                                } else {
                                    k = i.decrementAndGet();
                                }
                                if (k < 0) {
                                    i.set(imagesToBeShown.size());
                                    int initialVal = i.decrementAndGet();
                                    imgView.setImageBitmap(imagesToBeShown.get(initialVal));
                                    commentTextView.setText(commentsToBeShown.get(initialVal));
                                    dateTextView.setText(datesToBeShown.get(initialVal));
                                    ownerTextView.setText(ownerNameToBeShown.get(initialVal));

                                    final LatLng curImageLatLng1 = latLngsToBeShown.get(initialVal);
                                    showCurImageMarker(curImageLatLng1, iconNew);

                                    final LatLng curImageLatLngprevious = latLngsToBeShown.get(0);
                                    showCurImageMarker(curImageLatLngprevious, iconOld);

                                } else {
                                    imgView.setImageBitmap(imagesToBeShown.get(k));
                                    commentTextView.setText(commentsToBeShown.get(k));
                                    dateTextView.setText(datesToBeShown.get(k));
                                    ownerTextView.setText(ownerNameToBeShown.get(k));

                                    final LatLng curImageLatLng1 = latLngsToBeShown.get(k);
                                    showCurImageMarker(curImageLatLng1, iconNew);

                                    if (k == imagesToBeShown.size() - 1) {
                                        final LatLng curImageLatLngprevious = latLngsToBeShown.get(0);
                                        showCurImageMarker(curImageLatLngprevious, iconOld);
                                    } else {
                                        final LatLng curImageLatLngprevious = latLngsToBeShown.get(k + 1);
                                        showCurImageMarker(curImageLatLngprevious, iconOld);
                                    }
                                }
                            });

                            Button nextImageButton = renderable.getView().findViewById(R.id.showNextPictureButton);
                            nextImageButton.setOnClickListener(view1 -> {
                                Animation anim = AnimationUtils.loadAnimation(this, R.anim.fadein);
                                anim.reset();
                                imgView.startAnimation(anim);
                                int l = i.incrementAndGet();
                                if (l > imagesToBeShown.size()) {
                                    if (imagesToBeShown.size() > 0) {
                                        i.set(0);
                                        l = 0;
                                    } else {
                                        i.set(0);
                                        return;
                                    }
                                }
                                if (l == imagesToBeShown.size()) {
                                    i.set(-1);
                                    int endVal = i.incrementAndGet();
                                    imgView.setImageBitmap(imagesToBeShown.get(endVal));
                                    commentTextView.setText(commentsToBeShown.get(endVal));
                                    dateTextView.setText(datesToBeShown.get(endVal));
                                    ownerTextView.setText(ownerNameToBeShown.get(endVal));

                                    final LatLng curImageLatLng1 = latLngsToBeShown.get(endVal);
                                    showCurImageMarker(curImageLatLng1, iconNew);

                                    final LatLng curImageLatLngprevious = latLngsToBeShown.get(latLngsToBeShown.size() - 1);
                                    showCurImageMarker(curImageLatLngprevious, iconOld);
                                } else {
                                    imgView.setImageBitmap(imagesToBeShown.get(l));
                                    commentTextView.setText(commentsToBeShown.get(l));
                                    dateTextView.setText(datesToBeShown.get(l));
                                    ownerTextView.setText(ownerNameToBeShown.get(l));

                                    final LatLng curImageLatLng1 = latLngsToBeShown.get(l);
                                    showCurImageMarker(curImageLatLng1, iconNew);
                                    final LatLng curImageLatLngprevious;
                                    if (l == 0) {
                                        curImageLatLngprevious = latLngsToBeShown.get(latLngsToBeShown.size() - 1);
                                    }
                                    else {
                                        curImageLatLngprevious = latLngsToBeShown.get(l - 1);
                                    }
                                    showCurImageMarker(curImageLatLngprevious, iconOld);
                                }
                            });

                            AtomicInteger curImgRotationAngle = new AtomicInteger(0);
                            Button rotateImageRightButton = renderable.getView().findViewById(R.id.buttonRotateImageRight);
                            rotateImageRightButton.setOnClickListener(view1 -> {
                                int newangle = 90 * curImgRotationAngle.incrementAndGet();
                                imgView.setRotation(newangle);
                            });
                        }
                        catch (ArrayIndexOutOfBoundsException e){
                            Log.e("Error: Array out of bound: ", "Array out of bound error quiting...");
                            i.set(0);
                            imgView.setImageBitmap(imagesToBeShown.get(0));
                            commentTextView.setText(commentsToBeShown.get(0));
                            final LatLng curImageLatLng1 = latLngsToBeShown.get(0);
                            showCurImageMarker(curImageLatLng1, iconNew);
                        }
                    }

                    Button closeImageArRenderButton = renderable.getView().findViewById(R.id.closeImageArRenderButton);
                    closeImageArRenderButton.setOnClickListener(view1->{
                        if(anchorLocationHistoryView!=null){
                            anchorLocationHistoryView.detach();
                        }
                    });

                });
    }


    private void showCurImageMarker(LatLng curImageLatLng, Icon icon) {
        List<Marker> markerList = mapboxMap.getMarkers();
        ArrayList<Marker> curMarker = findMarkerAtLocation(markerList, curImageLatLng);
        for(Marker marker: curMarker){
            changeImageMarker(marker, icon);
        }
    }

    private void changeImageMarker(Marker marker, Icon icon) {
        marker.setIcon(icon);

    }

    private ArrayList<Marker> findMarkerAtLocation(List<Marker> markerList, LatLng curImageLatLng) {
        ArrayList<Marker> markersFoundAtLocation = new ArrayList<>();
        for(int i=0; i< markerList.size(); i++){
            Marker marker = markerList.get(i);
            if(marker.getPosition().equals(curImageLatLng)){
                markersFoundAtLocation.add(marker);
            }
        }
        return markersFoundAtLocation;
    }

    public void loadImageInformation(){
        ArrayList<ImageInformation> imageData = getImagesDataFromLocalDB();
        ArrayList<ImageInformation> onlyRenderedImageData = getOnlyRenderableImages(imageData);
        HashMap<Integer, Pair> imagesWithData = retrieveImageFromStorage(onlyRenderedImageData);
        showImages(imagesWithData);
    }

    private void createThreadPictureHistoryLoadDB() {
        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(getApplicationContext(),"Handler Called",Toast.LENGTH_SHORT).show();
                                ArrayList<ImageInformation> showableImageList = getListOfShowableImagesOnMap(imageRenderableDistance);
                                for(int i=0; i < showableImageList.size(); i++){
                                    ImageInformation curImageInfo = showableImageList.get(i);

                                    Icon icon = iconFactory.fromResource( R.drawable.icons_locationhistory_image_marker_48);
                                    Marker locHistoryMarker = mapboxMap.addMarker(
                                            new MarkerOptions()
                                                    .position(new LatLng(curImageInfo.getLat(),curImageInfo.getLng()))
                                                    .title("History Found for the location")
                                                    .snippet("Please tap surface on camera to view memories")
                                                    .icon(icon));
                                    markerTypeArrayList.add(new MarkerType(locHistoryMarker,"Loc_History_Marker"));

                                }
                            }
                        });
                        handler.removeCallbacks(this);
                        Looper.myLooper().quit();
                    }
                }, 0);
                Looper.loop();
            }
        };
        thread.start();
    }


    private void createTimerTask(int delay, int repeatInterval){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(isLocationHistoryEnabled) {
                    createThreadPictureHistoryLoadDB();
                }
            }
        };
        timer.schedule(timerTask, delay, repeatInterval);
    }


    // The following function returns the information of all images
    // that can be showed on the map. I.e, that fall within the valid
    // distance
    private ArrayList<ImageInformation> getListOfShowableImagesOnMap(float renderableDistance){
        // The following array will store all the images that can be displayed on the map
        ArrayList<ImageInformation> showableImagesList = new ArrayList<>();

        // Getting information of all images that are stored in the location database
        ArrayList<ImageInformation> imageInformationList = localDatabaseHelper.getImageInformation();

        // for each image, and see where it was taken.
        // Find the distance that from current place to where that image was taken
        // if distance is less than renderable distance
        // append the image information to the list of showable images
        for (int i=0; i< imageInformationList.size(); i++){
            ImageInformation imageInformation = imageInformationList.get(i);
            double placeLat = imageInformation.getLat();
            double placeLng = imageInformation.getLng();
            float placeCurDistance = calculateDistance(placeLat,placeLng,latitude,longitude);
            if(placeCurDistance < renderableDistance){
                showableImagesList.add(imageInformation);
            }
        }
        return showableImagesList;
    }

    public void setImageRenderableDistance(int imageRenderableDistance) {

        this.imageRenderableDistance = imageRenderableDistance;

    }

    public int getImageRenderableDistance() {
        return imageRenderableDistance;
    }

    public void removeMapMarkerOfParticularClass(String markerType){
        for(Iterator<MarkerType> mtIter = markerTypeArrayList.iterator(); mtIter.hasNext();){
            MarkerType curMarkerType = mtIter.next();
            String curMarkerTag = curMarkerType.getTag();
            if(curMarkerTag.equals(markerType)){
                Marker curMarker = curMarkerType.getMarker();
                mapboxMap.removeMarker(curMarker);
                mtIter.remove();
            }
        }
    }

    public void showDialog(View vIew){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = this.getLayoutInflater().inflate(R.layout.fragment_menu, null);
        builder.setView(view)
                .setPositiveButton("OK", null)
        //.setNegativeButton("Cancel", null)
        ;

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public int getCurNavMode() {
        return curNavMode;
    }

    public void setCurNavMode(int curNavMode) {
        this.curNavMode = curNavMode;
    }



    // TODO: Enable the following two when optimized

//    //Timer function for synchronization
//    private void createTimerTaskForSync(int delay, int repeatInterval){
//        Timer timer = new Timer();
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                if(isUserSignedIn) {
//                    syncDatabase();
//                }
//
//            }
//        };
//        timer.schedule(timerTask, delay, repeatInterval);
//    }
//
//
//    private void syncDatabase() {
//
////        Toast.makeText(getApplicationContext(), "Sync Started", Toast.LENGTH_SHORT).show();
//        Thread thread = new Thread() {
//            public void run() {
//                Looper.prepare();
//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //Function for synchronization of local db with azure db
////                        synchronizeLocalDbWithCloud();
//                        handler.removeCallbacks(this);
//                        Looper.myLooper().quit();
//                    }
//                }, 0);
//                Looper.loop();
//            }
//        };
//
//        thread.start();
//
//    }
//
//
//



    public void synchronizeLocalDbWithCloud(){

        ArrayList<ImageInformation> UnsyncImages = getUnsyncImagesDataFromLocalDB();

        for(ImageInformation unsyncImage : UnsyncImages){

            ImagesData imagesData = new ImagesData();
            imagesData.name = unsyncImage.getName();
            imagesData.owner_name = unsyncImage.getOwnerName();
            imagesData.description = unsyncImage.getDescription();
            imagesData.created_on = unsyncImage.getCreatedOn();
            imagesData.image_status=Integer.toString(2);
            imagesData.lat = unsyncImage.getLat();
            imagesData.lng = unsyncImage.getLng();
            imagesData.unique_filename = unsyncImage.getUniqueFileName();
            imagesData.owner_id=unsyncImage.getOwner_id();


            usersImagesDatabaseReference.push().setValue(imagesData);
            localDatabaseHelper.updateStatusData(unsyncImage.getId(),2);
            uploadCurrentImage(unsyncImage.getName(), unsyncImage.getUniqueFileName(), imagesData);

           }
    }

    public void uploadCurrentImage(String imagePath, String uniqueFileName, ImagesData imageData) {

        try {
            File photoFile = new File(imagePath);
            if (photoFile.exists()) {    // to check whether image is present along with the comment
                Uri photoURI = FileProvider.getUriForFile(this, this.getPackageName() + ".ar.waisoft.name.provider", photoFile);

                final InputStream imageStream = getContentResolver().openInputStream(photoURI);
                final int imageLength = imageStream.available();
                final Handler handler = new Handler();

                // this is done as a thread so it happen concurrently
                // and the app does not halt until image is uploaded
                Thread th = new Thread(new Runnable() {
                    public void run() {

                        try {

                            final String imageName = ImageManager.UploadImage(imageStream, imageLength, uniqueFileName, imageData);

                            handler.post(new Runnable() {

                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Image Uploaded Successfully. Name = " + imageName, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception ex) {
                            final String exceptionMessage = ex.getMessage();
                            handler.post(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), exceptionMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                th.start();
            }
            } catch(FileNotFoundException e){
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            }
    }




    public void downloadCloudImages(){

        // Below is an attempt to implement download cloud images functionality


        ArrayList<String> localUniqueFileNames = localDatabaseHelper.getAllUniqueFileNames();


        // Thus far what this function does is that it
        // retrieves the names of all images on the cloud
        // filters them out to get the images that are needed to be downloaded

        usersImagesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Currently dataSnapshot contains the list of all images

                for(DataSnapshot singleImage : dataSnapshot.getChildren()){


                    String ImageName = singleImage.child("unique_filename").getValue().toString();

                    if(!localUniqueFileNames.contains(ImageName)){

                        Toast.makeText(getApplicationContext(), "Downloading" + ImageName,Toast.LENGTH_SHORT).show();

                        ImageManager.downloadImage(ImageName, getUniqueUserID());
                        // Persist this data to the local database
                        ImagesData imagesData = singleImage.getValue(ImagesData.class);
                        MapsActivity.this.localDatabaseHelper.insertImagesData(imagesData.name,imagesData.lat,imagesData.lng,imagesData.description, imagesData.created_on, imagesData.owner_name,Integer.parseInt(imagesData.image_status), imagesData.unique_filename,imagesData.owner_id);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void getUserContacts(){

        FirebaseDatabase.getInstance().getReference().child("users").child(getUniqueUserID()).child("contacts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Toast.makeText(getApplicationContext(), "No of contacts: " + Long.toString(dataSnapshot.getChildrenCount()), Toast.LENGTH_LONG).show();
                for (DataSnapshot singleContact: dataSnapshot.getChildren()){

                    contact latestContact = singleContact.getValue(contact.class);
//                    alertDisplayer("hh", singleContact.getValue().toString());
                    userContacts.add(latestContact);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });




    }

    // deletes the directory of user images
    public boolean clearImageDirectory(){
        File picDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "ToAR/" + uniqueUserID + "/" );
        Boolean goneRight = false;
        try {
            FileUtils.deleteDirectory(picDir);
            Toast.makeText(getApplicationContext(), "Deleted" + uniqueUserID + "Images directory", Toast.LENGTH_SHORT);
            goneRight = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return goneRight;
    }


    public String getUniqueUserID() {
        return uniqueUserID;
    }

    public void setUniqueUserID(String uniqueUserID) {
        this.uniqueUserID = uniqueUserID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private void setSettingVariables(LocalDatabaseHelper localDatabaseHelper) {
        ArrayList<AppSetting> settingArrayList = localDatabaseHelper.getAllSettings();
        for (AppSetting appSetting: settingArrayList){
            if(appSetting.getName().equals("user_profile")){
                uniqueUserID = appSetting.getValue();
                isUserSignedIn = true;
            }
            else if(appSetting.getName().equals("user_name")){
                userName = appSetting.getValue();
            }
            else if(appSetting.getName().equals("loc_hist_range")){
                setImageRenderableDistance(Integer.parseInt(appSetting.getValue()));
            }
            else if(appSetting.getName().equals("loc_hist_enabled")){
                Boolean loc_his_enab = Boolean.parseBoolean(appSetting.getValue());
                isLocationHistoryEnabled = loc_his_enab;
            }
            else if(appSetting.getName().equals("nav_mode")){
                setCurNavMode(Integer.parseInt(appSetting.getValue()));
            }

        }
    }

    /*
    * The following function is used to create a
    * an alert message
    * */
    public void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog ok = builder.create();
        ok.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                ok.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.red));
            }
        });
        ok.show();
    }


    public void setCurrentUserPojo(Users pojo){
        this.currentUserPojo = pojo;
    }





    // runs whenever a user signs in, signs up, or signs out.

    public void refreshCurrentUser(){
        this.mAuth = FirebaseAuth.getInstance();            // get an instance of firebase authentication
        this.currentUser = mAuth.getCurrentUser();          // null if noone is signed in, otherwise contains user id


        if(this.currentUser != null){
            // Changes to make sure if the current user is not null
            setUniqueUserID(currentUser.getUid());
            isUserSignedIn = true;


            // Creating the references for our user to access his/her profile information, ImagesDatabase, and
            // Images from the cloud
            this.usersDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(getUniqueUserID());


            this.usersImagesDatabaseReference = usersDatabaseReference.child("userImages");


            // storage reference will remain the same
            this.userImagesStorageReference = FirebaseStorage.getInstance().getReference("usersImages/" + getUniqueUserID()+"/");




            usersDatabaseReference.child("profileInformation").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // This pojo recieves and stores all of the user data from the cloud

                    Users userPojo = dataSnapshot.getValue(Users.class);

                    // The following operations will Make relavent changes to the
                    // maps activity when the user signs in as well update the
                    // location database

                    setCurrentUserPojo(userPojo);
                    setUserName(userPojo.getName());
//                    alertDisplayer("pok", currentUserPojo.getId());


                    AppSetting userProfileIdSetting = new AppSetting("user_profile", userPojo.getId());
                    AppSetting userNameSetting = new AppSetting("user_name", userPojo.getName());


                    userProfileIdSetting.insertOrUpdateSettingToLocalDb(localDatabaseHelper);
                    userNameSetting.insertOrUpdateSettingToLocalDb(localDatabaseHelper);

                    userSignStatusButton.setText("Signed in as: " + getUserName());
                    if(!beenGreetedOnce){
//                        alertDisplayer("Welcome back!", "Hi " + currentUserPojo.getName() +"!");
                        beenGreetedOnce = true;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                  alertDisplayer("Failed!", "Could Not fetch user data!");
                }
                }
                );


            userSignStatusButton.setBackgroundResource(R.drawable.user_login_status_green_tv);
            userSignStatusButton.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.black));
            isUserSignedIn = true;

            getUserContacts();
            downloadCloudImages();
        }

        else{
            // Changes to make if currently there is not user signed in
            currentUserPojo = null;
            setUniqueUserID("1");
            setUserName("local");
            isUserSignedIn = false;
            localDatabaseHelper.clearImagesDatabase();           // Clears the whole images database
            localDatabaseHelper.clearSettingsDatabase();                // Clears the whole settings database
            userSignStatusButton.setText("Not Logged In");
            userSignStatusButton.setBackgroundResource(R.drawable.user_login_status_red_tv);
            userSignStatusButton.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
            loadImageInformation();
            makeRenderablePicture();

        }


    }


    /*
    * The following function is used to help save battery when user
    * switches to navigation mode
    * Feature: navigation.
    * */
    private void alertBatterySaverDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switchToMapMode();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No, thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


        AlertDialog ok = builder.create();
        ok.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                ok.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getColor(R.color.red));
                ok.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.green));

            }
        });
        ok.show();
    }
    public void goToMessanger(View view){
        String contactId = (String)view.getTag();
        String uName = "";
        String jointChatNode;
        try {
            jointChatNode = getJointNode.getUniqueNode(uniqueUserID, contactId);
        }
        catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "No such algorithm exception occured!", Toast.LENGTH_LONG).show();
            return;
        }
        Intent i = new Intent(getApplicationContext(), chatActivity.class).putExtra("chatNode", (String)jointChatNode);

        for(int j = 0 ; j <userContacts.size() ; j ++ ){
            if(userContacts.get(j).id.equals(contactId)){
                uName = userContacts.get(j).name;
            }
        }

        i.putExtra("uName", uName);
        startActivity(i);


    }

    public void sendMemoriesToContacts(Memory newMemory, ArrayList<String> contactsToSendMemoriesTo){

        for(int i =0 ; i < contactsToSendMemoriesTo.size(); i++){
            FirebaseDatabase.getInstance().getReference().child("users").child(contactsToSendMemoriesTo.get(i)).child("memories").push().setValue(newMemory);
        }
    }





}


