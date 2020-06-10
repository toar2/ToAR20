package com.aaksoft.toar.fragments;

/*
    Created By Aasharib
    on
    20 September, 2018
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aaksoft.toar.R;
import com.aaksoft.toar.activities.MapsActivity;
import com.aaksoft.toar.api.google.Information.Places;

/*
    Fragment for detail description of nearby places
 */

public class LocationMarkerDetailsDescriptionFragment extends Fragment {

    private Places places;

    public LocationMarkerDetailsDescriptionFragment() {
        // Required empty public constructor
    }

    public static LocationMarkerDetailsDescriptionFragment newInstance(Places places) {
        LocationMarkerDetailsDescriptionFragment fragment = new LocationMarkerDetailsDescriptionFragment();
        Bundle args = new Bundle();
        args.putSerializable("Place", places);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            places = (Places) getArguments().getSerializable("Place");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location_marker_details_description, container, false);
        updateTextFieldInformation(view,places);
        Button backButtonDetailFragment = view.findViewById(R.id.backButtonDetailFragment);
        backButtonDetailFragment.setOnClickListener(view1->{
            removeFragment(this);
            //getActivity().onBackPressed();
        });
        Button navigateButtonDetailFragment = view.findViewById(R.id.navigateButtonDetailFragment);
        navigateButtonDetailFragment.setOnClickListener(view1->{
            if(((MapsActivity)getActivity()).getCurAppMode() == 2){
                ((MapsActivity)getActivity()).switchToHybridMapMode();
                Toast.makeText(getContext(),"For Navigation We have to switch to Hybrid mode or Map mode",Toast.LENGTH_SHORT).show();
            }
            ((MapsActivity)getActivity()).setDestinationSelected(true);
            ((MapsActivity)getActivity()).routeNavigate(places.getLng(),places.getLat());
            ((MapsActivity) getActivity()).hideListViewButton();
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

    private void updateTextFieldInformation(View v, Places places){
        TextView placeNameDetail = v.findViewById(R.id.placeNameDetailFragment);
        TextView placeVicinityDetail = v.findViewById(R.id.placeVicinityDetailFragment);
        TextView placeRatingDetail = v.findViewById(R.id.placeRatingDetailFragment);
        TextView placeTypeDetail = v.findViewById(R.id.placeTypeDetailFragment);

        placeNameDetail.setText(places.getName());
        placeVicinityDetail.setText(places.getVicinity());
        placeRatingDetail.setText("Rating: " + String.valueOf(places.getRating()));
        placeTypeDetail.setText(places.getPlaceTypeString());
    }

}
