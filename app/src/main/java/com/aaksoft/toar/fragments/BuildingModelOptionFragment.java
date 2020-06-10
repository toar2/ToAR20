package com.aaksoft.toar.fragments;

/*
    Created By Aasharib
    on
    2 January, 2019
 */

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.aaksoft.toar.R;
import com.aaksoft.toar.activities.MapsActivity;
import com.aaksoft.toar.adapters.BuildingModelListAdapter;
import com.aaksoft.toar.localdb.utils.AppSetting;

/*
    Handles 3D Model Rendering settings
 */

public class BuildingModelOptionFragment extends Fragment {

    Activity activity;
    Button backButton;
    Button doneButton;
    CheckBox enableModelRenderCheckbox;
    ListView modelOptionListView;
    LinearLayout listContainer;
    List<String> modelList = new ArrayList<String>() {{
        add("SEECS");
        add("NUST Masjid");
        add("NUST gate 10");
        add("NUST gate 3");
        add("NICE");
    }};


    public BuildingModelOptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_building_model_option, container, false);
        modelOptionListView = view.findViewById(R.id.modelOptionListView);
        BuildingModelListAdapter buildingModelListAdapter = new BuildingModelListAdapter(modelList, getActivity(),BuildingModelOptionFragment.this);
        modelOptionListView.setAdapter(buildingModelListAdapter);

        listContainer = view.findViewById(R.id.list_container_building_model_option);

        enableModelRenderCheckbox = view.findViewById(R.id.checkboxEnableModelRender);
        if(((MapsActivity)getActivity()).isModelingEnabled) {
            enableModelRenderCheckbox.setChecked(((MapsActivity) getActivity()).isModelingEnabled);
            onCheckAction();
        }
        enableModelRenderCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    onCheckAction();

                    enableModelRenderCheckbox.setEnabled(false);
                    Timer checkboxTimer = new Timer();
                    checkboxTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            ((MapsActivity)activity).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    enableModelRenderCheckbox.setEnabled(true);
                                }
                            });
                        }
                    }, 5000);
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            AppSetting locationHistoryRenderableRangeSetting = new AppSetting("modeling_enb","true");
                            locationHistoryRenderableRangeSetting.insertOrUpdateSettingToLocalDb(((MapsActivity)getActivity()).localDatabaseHelper);
                        }
                    });
                }
                else {
                    //ArrayList<LocationMarker> allMarkers = ((MapsActivity)getActivity()).getLocationScene().mLocationMarkers;
                    //for(LocationMarker locationMarker : allMarkers) {
                    //((MapsActivity) getContext()).getLocationScene().clearAllExceptTheNonTaggedMarkers();
                    onUnCheckAction();

                    for(int i=0; i<5; i++) {
                        buildingModelListAdapter.onUnCheckAdapterAction(i);
                    }

                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            AppSetting locationHistoryRenderableRangeSetting = new AppSetting("modeling_enb","false");
                            locationHistoryRenderableRangeSetting.insertOrUpdateSettingToLocalDb(((MapsActivity)getActivity()).localDatabaseHelper);
                        }
                    });

                    //}
                    /*int flagCheck = 0;
                    for (int i=0;i<listContainer.getCount();i++){
                        CheckBox checkBox = (CheckBox) buildingModelListAdapter.getItem(i);
                        Boolean isCurChecked = checkBox.isChecked();
                        if(isCurChecked == true){
                            flagCheck = 1;
                        }
                    }
                    if(flagCheck == 0){
                        onUnCheckAction();
                    }
                    else{
                        Toast.makeText(getContext(), "Please Uncheck all the models to close modeling", Toast.LENGTH_SHORT).show();
                    }*/
                }
            }
        });

        backButton = view.findViewById(R.id.backButtonBuildingModelOption);
        backButton.setOnClickListener(view1->{
            ((MapsActivity) getActivity()).isMenuBeingDisplayed = false;
            removeFragment(this);
        });

        doneButton = view.findViewById(R.id.doneButtonModelOption);
        doneButton.setOnClickListener(view1->{
            ((MapsActivity) getActivity()).isMenuBeingDisplayed = false;
            removeFragment(this);
        });
        return view;
    }

    private void onCheckAction() {
        ((MapsActivity)getContext()).isModelingEnabled = true;
        listContainer.setVisibility(View.VISIBLE);
    }

    private void onUnCheckAction() {
        ((MapsActivity)getContext()).isModelingEnabled = false;
        listContainer.setVisibility(View.GONE);
    }

    protected void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }
}
