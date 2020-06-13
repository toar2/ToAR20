package com.aaksoft.toar.fragments;

/*
    Created By Aasharib
    on
    15 August, 2018
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.aaksoft.toar.R;
import com.aaksoft.toar.activities.MapsActivity;
import com.aaksoft.toar.activities.augmentModels;
import com.aaksoft.toar.fragments.ContactListFragment;

import java.util.Map;

/*
    Handles the Application menu
 */

public class MenuFragment extends Fragment {

    Button backButton;

    Button buildingModelOptionButton;
    Button userContactsButton;
    Button locationHistoryButton;
    Button navigationOptionButton;
    Button helpButton;
    Button uploadImagesToCloudbutton;
    //Button previewBuildingModelButton;

    public MenuFragment() {
        // Required empty public constructor
    }

    public static MenuFragment newInstance() {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        backButton = view.findViewById(R.id.backButtonMenuFragment);
        backButton.setOnClickListener(view1->{
            ((MapsActivity) getActivity()).isMenuBeingDisplayed = false;
            removeFragment(this);
        });


        buildingModelOptionButton = view.findViewById(R.id.buttonBuildingModelMenuFragment);
        buildingModelOptionButton.setOnClickListener(view1->{

            Intent intent = new Intent(getActivity(), augmentModels.class);
            intent.putExtra("uniqueUserID" , ((MapsActivity)getActivity()).getUniqueUserID());              // Passing unique userId
            intent.putExtra("latitude", ((MapsActivity)getActivity()).getLatitude());                            // Passing latitude and longitude to
                                                                                                                    // augment models activity
            intent.putExtra("longitude", ((MapsActivity)getActivity()).getLongitude());
            intent.putExtra("userName", ((MapsActivity)getActivity()).getUserName());
            startActivity(intent);
        });

        userContactsButton = view.findViewById(R.id.userContactsMenuButton);
        userContactsButton.setOnClickListener(view1 -> {

            if(((MapsActivity)getActivity()).currentUser == null){

                Toast.makeText(getActivity().getApplicationContext(), "Please login to continue", Toast.LENGTH_LONG).show();

            }

            ContactListFragment contactListFragment = new ContactListFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_container, contactListFragment);
            fragmentTransaction.commit();
            fragmentTransaction.addToBackStack(null);
            removeFragment(this);


        });


        locationHistoryButton = view.findViewById(R.id.buttonLocationHistoryFragment);
        locationHistoryButton.setOnClickListener(view1->{
            UserLocationHistoryFragment userLocationHistoryFragment = new UserLocationHistoryFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_container, userLocationHistoryFragment);
            fragmentTransaction.commit();
            fragmentTransaction.addToBackStack(null);
            removeFragment(this);

            //((MapsActivity)getActivity()).downloadCloudImages();

        });
        uploadImagesToCloudbutton = view.findViewById(R.id.syncLocalDbWithCloudButton);
        uploadImagesToCloudbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(((MapsActivity)getActivity()).currentUser != null){

                    ((MapsActivity)getActivity()).synchronizeLocalDbWithCloud();

                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Please login to continue", Toast.LENGTH_LONG).show();
//                    Toast.makeText(getApplicationContext(), "Can not get user location", Toast.LENGTH_SHORT).show();
                }

            }
        });


        navigationOptionButton = view.findViewById(R.id.buttonNavigationOptionFragment);
        navigationOptionButton.setOnClickListener(view1->{
            NavigationOptionFragment navigationOptionFragment = NavigationOptionFragment.newInstance(((MapsActivity)getActivity()).getCurAppMode());
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_container, navigationOptionFragment);
            fragmentTransaction.commit();
            fragmentTransaction.addToBackStack(null);
            removeFragment(this);
        });

        helpButton = view.findViewById(R.id.buttonHelpFragment);
        helpButton.setOnClickListener(view1->{
            HelpFragment helpFragment = new HelpFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_container, helpFragment);
            fragmentTransaction.commit();
            fragmentTransaction.addToBackStack(null);
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
