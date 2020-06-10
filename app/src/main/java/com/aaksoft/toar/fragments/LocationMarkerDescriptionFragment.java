package com.aaksoft.toar.fragments;

/*
    Created By Aasharib
    on
    18 September, 2018
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aaksoft.toar.R;
import com.aaksoft.toar.activities.MapsActivity;
import com.aaksoft.toar.api.google.Information.Places;

/*
    Fragment for displaying marker description
 */

public class LocationMarkerDescriptionFragment extends Fragment {
    private Places places;

    public LocationMarkerDescriptionFragment() {
        // Required empty public constructor
    }

    public static LocationMarkerDescriptionFragment newInstance(Places places) {
        LocationMarkerDescriptionFragment fragment = new LocationMarkerDescriptionFragment();
        Bundle args = new Bundle();
        args.putSerializable("Place",places);
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
        View view = inflater.inflate(R.layout.fragment_location_marker_description, container, false);
        updateTextViews(view, places);
        updateLocationImage(view,places);
        Button cancelButton = view.findViewById(R.id.cancelButtonFragment);
        cancelButton.setOnClickListener(view1->{
            //Toast.makeText(getContext(),"Cancel",Toast.LENGTH_SHORT).show();
            removeFragement(this);
            //FrameLayout frameLayout = view.findViewById(R.id.fragment_location_marker);
            //frameLayout.setVisibility(View.GONE);
        });
        Button navigateButtonFragment = view.findViewById(R.id.navigateButtonFragment);
        navigateButtonFragment.setOnClickListener(view1->{
            if(((MapsActivity)getActivity()).getCurAppMode() == 2){
                ((MapsActivity)getActivity()).switchToHybridMapMode();
                Toast.makeText(getContext(),"For Navigation We have to switch to Hybrid mode or Map mode",Toast.LENGTH_SHORT).show();
            }
            ((MapsActivity)getActivity()).setDestinationSelected(true);
            ((MapsActivity)getActivity()).routeNavigate(places.getLng(),places.getLat());
            ((MapsActivity) getActivity()).hideListViewButton();
            removeFragement(this);
        });
        Button moreButtonFragment = view.findViewById(R.id.moreButtonFragment);
        moreButtonFragment.setOnClickListener(view1->{
            LocationMarkerDetailsDescriptionFragment locationMarkerDetailsDescriptionFragment = LocationMarkerDetailsDescriptionFragment.newInstance(places);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.add(R.id.locationDetailContainerFragment, locationMarkerDetailsDescriptionFragment);
            fragmentTransaction.add(R.id.screen_container, locationMarkerDetailsDescriptionFragment);
            fragmentTransaction.commit();
            removeFragement(this);
        });
        return view;
    }

    private void updateLocationImage(View view, Places places) {
        ImageView imageView = view.findViewById(R.id.imageViewLocationDescriptionFragment);
        if(places.getType().contains("food")||places.getType().contains("restaurant")){
            imageView.setImageResource(R.drawable.icons_restaurant_48);
        }
        else if(places.getType().contains("hospital")||places.getType().contains("health")){
            imageView.setImageResource(R.drawable.icon_doctor_24);
        }
        else if(places.getType().contains("park")){
            imageView.setImageResource(R.drawable.icon_generic_recreational_24);
        }
        else if(places.getType().contains("school")){
            imageView.setImageResource(R.drawable.icon_school_24);
        }
        else{
            imageView.setImageResource(R.drawable.icons_point_of_interest_48);
        }
    }

    public void updateTextViews(View view, Places places){
        TextView placeNameTextView = (TextView)view.findViewById(R.id.placeNameTextView);
        TextView placeVicinityTextView = (TextView)view.findViewById(R.id.placeVicinityTextBox);
        TextView placeRatingTextView = (TextView)view.findViewById(R.id.placeRatingTextView);
        placeVicinityTextView.setText(places.getVicinity());
        placeNameTextView.setText(places.getName());
        placeRatingTextView.setText("Rating: "+places.getRating());
    }

    protected void removeFragement(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }
}
