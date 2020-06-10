package com.aaksoft.toar.fragments;

/*
    Created By Aasharib
    on
    8 September, 2018
 */

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mapbox.api.directions.v5.models.DirectionsRoute;
//import com.mapbox.services.android.navigation.testapp.R;
//import com.mapbox.services.android.navigation.testapp.activity.navigationui.SimplifiedCallback;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;

import com.aaksoft.toar.R;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

/*
    Handles the mapbox navigation
    get route and start following and rendering navigation
 */

@SuppressLint("ValidFragment")
public class NavigationFragment extends Fragment implements OnNavigationReadyCallback {

    private NavigationView navigationView;
    private DirectionsRoute directionsRoute;


    @SuppressLint("ValidFragment")
    public NavigationFragment(DirectionsRoute route){
        directionsRoute = route;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.navigation_view_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigationView = view.findViewById(R.id.navigation_view_fragment);
        navigationView.onCreate(savedInstanceState);
        navigationView.initialize(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        navigationView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        navigationView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            navigationView.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        navigationView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        navigationView.onStop();
        //navigationView.retrieveNavigationMapboxMap();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        navigationView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        navigationView.onDestroy();
    }

    @Override
    public void onNavigationReady(boolean isRunning) {
        NavigationViewOptions options = NavigationViewOptions.builder()
                .directionsRoute(directionsRoute)
                .shouldSimulateRoute(false)
                .navigationListener(new NavigationListener() {
                    @Override
                    public void onCancelNavigation() {
                        Toast.makeText(getApplicationContext(), "Navigation Canceled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNavigationFinished() {
                        Toast.makeText(getApplicationContext(), "Navigation Finished", Toast.LENGTH_SHORT).show();
/*                        FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.add(R.id.map, NavigationFragment.this);
                        fragmentTransaction.commit();*/
                    }

                    @Override
                    public void onNavigationRunning() {

                    }
                })
                .build();

        navigationView.startNavigation(options);


    }

}