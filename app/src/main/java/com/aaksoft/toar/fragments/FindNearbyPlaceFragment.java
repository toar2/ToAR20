package com.aaksoft.toar.fragments;


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
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aaksoft.toar.R;
import com.aaksoft.toar.activities.MapsActivity;


public class FindNearbyPlaceFragment extends Fragment {
    int radius = 1000;

    public FindNearbyPlaceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_nearby_place, container, false);

        EditText specificPlaceCharacteristicSearchEditTextView = view.findViewById(R.id.foodTypeSearchTextView);

        Spinner spinner = view.findViewById(R.id.spinnerFindNearbyPlace);
        ArrayAdapter<String> placeTypeListAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.spinner_item,getResources().getStringArray(R.array.places_types));
        placeTypeListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(placeTypeListAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinner.getSelectedItem() == "restaurant"){
                    specificPlaceCharacteristicSearchEditTextView.setHint("Enter Specific food type (if any)");
                }
                else{
                    specificPlaceCharacteristicSearchEditTextView.setHint("Enter Specific Place feature (if any)");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                specificPlaceCharacteristicSearchEditTextView.setHint("");
            }
        });

        SeekBar seekBar = view.findViewById(R.id.seekBarFindRadius);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius = Integer.valueOf(progress);
                TextView radiusTextView = view.findViewById(R.id.radiusFindTextViewFNPFragment);
                radiusTextView.setText(radius+"m");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Button buttonFindNearbyPlace = view.findViewById(R.id.buttonFindPlaceFragment);
        buttonFindNearbyPlace.setOnClickListener(view1->{
            if(spinner.getSelectedItem() == null || spinner.getSelectedItem() == ""){
                return;
            }
            String keywordPlaceSearch = specificPlaceCharacteristicSearchEditTextView.getText().toString();
            keywordPlaceSearch.trim();
            ((MapsActivity)getActivity()).findPlaceNearby(spinner.getSelectedItem().toString(),radius, keywordPlaceSearch);
            Toast.makeText(getContext(),"Finding Place in radius: "+radius + "m", Toast.LENGTH_SHORT).show();
            ((MapsActivity) getActivity()).isMenuBeingDisplayed = false;
            removeFragment(this);
        });

        Button backButton = view.findViewById(R.id.backButtonFindNearbyPlaceFragment);
        backButton.setOnClickListener(view1->{
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
