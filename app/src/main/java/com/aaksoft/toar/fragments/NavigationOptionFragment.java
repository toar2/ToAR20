package com.aaksoft.toar.fragments;

/*
    Created By Aasharib
    on
    25 February, 2019
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.aaksoft.toar.R;
import com.aaksoft.toar.activities.MapsActivity;
import com.aaksoft.toar.localdb.utils.AppSetting;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NavigationOptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 *
 * Gives user option to select type of route for navigation
 * 1) Walking
 * 2) Driving
 * 3) Cycling
 */

public class NavigationOptionFragment extends Fragment {
    private static final String ARG_PARAM1 = "SELECTED_MODE";

    private Spinner selectedNavModeSpinner;

    private int selectedMode;


    public NavigationOptionFragment() {
        // Required empty public constructor
    }

    public static NavigationOptionFragment newInstance(int selectedMode) {
        NavigationOptionFragment fragment = new NavigationOptionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, selectedMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedMode = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation_option, container, false);

        selectedNavModeSpinner = view.findViewById(R.id.spinnerSelectRouteType);
        ArrayAdapter<String> routeTypeAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.spinner_item,getResources().getStringArray(R.array.routes_types));

        routeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectedNavModeSpinner.setAdapter(routeTypeAdapter);
        selectedNavModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((MapsActivity)getActivity()).setCurNavMode(position);
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        AppSetting locationHistoryRenderableRangeSetting = new AppSetting("nav_mode",String.valueOf(position));
                        locationHistoryRenderableRangeSetting.insertOrUpdateSettingToLocalDb(((MapsActivity)getActivity()).localDatabaseHelper);
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(((MapsActivity)getActivity()).getCurNavMode() == 0){
            selectedNavModeSpinner.setSelection(routeTypeAdapter.getPosition("Walking"));
        }
        else if(((MapsActivity)getActivity()).getCurNavMode() == 1){
            selectedNavModeSpinner.setSelection(routeTypeAdapter.getPosition("Driving"));
        }
        else if(((MapsActivity)getActivity()).getCurNavMode() == 2){
            selectedNavModeSpinner.setSelection(routeTypeAdapter.getPosition("Cycling"));
        }

        Button backButton = view.findViewById(R.id.backButtonNavigationOptionFragment);
        backButton.setOnClickListener(view1->{
            ((MapsActivity) getActivity()).isMenuBeingDisplayed = false;
            removeFragment(this);
        });

        Button doneButton = view.findViewById(R.id.buttonDoneNavigationOption);
        doneButton.setOnClickListener(view1->{
            ((MapsActivity) getActivity()).isMenuBeingDisplayed = false;
            removeFragment(this);
        });

        return view;
    }


    protected void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

}
