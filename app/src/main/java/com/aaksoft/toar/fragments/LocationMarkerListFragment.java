package com.aaksoft.toar.fragments;

/*
    Created By Aasharib
    on
    1 October, 2018
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.io.Serializable;
import java.util.List;

import com.aaksoft.toar.R;
import com.aaksoft.toar.adapters.LocationMarkerListAdapter;
import com.aaksoft.toar.api.google.Information.Places;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationMarkerListFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Displays all the api response as list
 */
public class LocationMarkerListFragment extends Fragment {

    private List<Places> placesList;
    private ListView listView;
    Context context;

    public LocationMarkerListFragment() {
        // Required empty public constructor
    }

    public static LocationMarkerListFragment newInstance(List<Places> places) {
        LocationMarkerListFragment fragment = new LocationMarkerListFragment();
        Bundle args = new Bundle();
        args.putSerializable("place", (Serializable) places);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            placesList = (List<Places>)getArguments().getSerializable("place");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_location_marker_list, container, false);
        listView = view.findViewById(R.id.listViewFragment);
        LocationMarkerListAdapter locationMarkerListAdapter = new LocationMarkerListAdapter(placesList, getActivity(),LocationMarkerListFragment.this);
        listView.setAdapter(locationMarkerListAdapter);
        Button backButtonLocationList = view.findViewById(R.id.backButtonLocationList);
        backButtonLocationList.setOnClickListener(view1->{
            removeFragement(this);
        });
        return view;
    }

    public void removeFragement(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }
}
